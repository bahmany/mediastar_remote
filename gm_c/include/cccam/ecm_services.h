#pragma once
// Three background services for continuous CW harvesting, AI training, offline CW provision.
// Service 1: EcmHarvester - probes upstream CCcam servers with known ECMs
// Service 2: OfflineCwDb  - persistent CW database for offline descrambling
// Service 3: AiTrainer    - periodic deep analysis and model saving

#include <thread>
#include <atomic>
#include <mutex>
#include <vector>
#include <map>
#include <set>
#include <array>
#include <chrono>
#include <functional>
#include <algorithm>
#include <numeric>
#include <cstring>
#include <cstdio>
#include <fstream>
#include <sstream>
#include "cccam/cccam_config.h"

#ifdef _WIN32
#  ifndef WIN32_LEAN_AND_MEAN
#    define WIN32_LEAN_AND_MEAN
#  endif
#  ifndef NOMINMAX
#    define NOMINMAX
#  endif
#  include <winsock2.h>
#  include <ws2tcpip.h>
#  include <windows.h>
   using HcSockT = SOCKET;
#  define HC_INVALID INVALID_SOCKET
#  define HC_CLOSE(s) closesocket(s)
#else
#  include <sys/socket.h>
#  include <netinet/in.h>
#  include <arpa/inet.h>
#  include <unistd.h>
   using HcSockT = int;
#  define HC_INVALID (-1)
#  define HC_CLOSE(s) close(s)
#endif

namespace cccam {

// Forward: CcCryptBlock, sha1, cc_crypt_xor, CcMsg enums from cccam_server.h
// This header must be included AFTER cccam_server.h in the translation unit.

// ═══════════════════════════════════════════════════════════════════════════
//  HARVEST TARGET
// ═══════════════════════════════════════════════════════════════════════════
struct HarvestTarget {
    uint16_t caid = 0;
    uint32_t provid = 0;
    uint16_t sid = 0;
    std::vector<uint8_t> ecm_msg;
    time_t captured_at = 0;
    time_t last_harvest = 0;
    time_t last_success = 0;
    int harvest_count = 0;
    int success_count = 0;
    int fail_count = 0;
    std::array<uint8_t, 16> last_cw{};
    std::string best_server;
};

// ═══════════════════════════════════════════════════════════════════════════
//  OFFLINE CW ENTRY
// ═══════════════════════════════════════════════════════════════════════════
struct OfflineCwEntry {
    uint16_t caid = 0;
    uint16_t sid = 0;
    uint32_t provid = 0;
    std::array<uint8_t, 16> cw{};
    time_t timestamp = 0;
    int confidence = 0;
    std::string server;
};

// ═══════════════════════════════════════════════════════════════════════════
//  HARVEST CONNECTION — standalone CCcam client
// ═══════════════════════════════════════════════════════════════════════════
struct HarvestConn {
    HcSockT sock = HC_INVALID;
    CcCryptBlock sendBlock{};
    CcCryptBlock recvBlock{};
    bool connected = false;
    int cardCount = 0;
    std::string label;

    void close() {
        if (sock != HC_INVALID) { HC_CLOSE(sock); sock = HC_INVALID; }
        connected = false;
    }
    ~HarvestConn() { close(); }

    // Prevent copy (socket ownership)
    HarvestConn() = default;
    HarvestConn(HarvestConn&& o) noexcept
        : sock(o.sock), sendBlock(o.sendBlock), recvBlock(o.recvBlock),
          connected(o.connected), cardCount(o.cardCount), label(std::move(o.label))
    { o.sock = HC_INVALID; o.connected = false; }
    HarvestConn& operator=(HarvestConn&& o) noexcept {
        if (this != &o) { close(); sock=o.sock; sendBlock=o.sendBlock; recvBlock=o.recvBlock;
            connected=o.connected; cardCount=o.cardCount; label=std::move(o.label);
            o.sock=HC_INVALID; o.connected=false; }
        return *this;
    }
    HarvestConn(const HarvestConn&) = delete;
    HarvestConn& operator=(const HarvestConn&) = delete;

    bool connect(const UpstreamServer& srv, int timeoutMs = 5000) {
        close(); cardCount = 0;
        label = srv.name + "(" + srv.host + ":" + std::to_string(srv.port) + ")";
        sock = socket(AF_INET, SOCK_STREAM, 0);
        if (sock == HC_INVALID) return false;
        auto setTo = [&](int ms) {
#ifdef _WIN32
            DWORD t = (DWORD)ms;
            setsockopt(sock, SOL_SOCKET, SO_RCVTIMEO, (char*)&t, sizeof(t));
            setsockopt(sock, SOL_SOCKET, SO_SNDTIMEO, (char*)&t, sizeof(t));
#else
            timeval t; t.tv_sec=ms/1000; t.tv_usec=(ms%1000)*1000;
            setsockopt(sock, SOL_SOCKET, SO_RCVTIMEO, &t, sizeof(t));
            setsockopt(sock, SOL_SOCKET, SO_SNDTIMEO, &t, sizeof(t));
#endif
        };
        setTo(timeoutMs);
        auto sa = [&](const uint8_t* d, int n) -> bool {
            int s=0; while(s<n){int r=send(sock,(const char*)d+s,n-s,0);if(r<=0)return false;s+=r;} return true;
        };
        auto ra = [&](uint8_t* d, int n) -> bool {
            int g=0; while(g<n){int r=recv(sock,(char*)d+g,n-g,0);if(r<=0)return false;g+=r;} return true;
        };
        // TCP connect (direct or SOCKS5)
        if (srv.hasProxy()) {
            sockaddr_in pa{}; pa.sin_family=AF_INET; pa.sin_port=htons((uint16_t)srv.proxy_port);
            if(inet_pton(AF_INET,srv.proxy_host.c_str(),&pa.sin_addr)!=1){
                addrinfo hints{},*res=nullptr;hints.ai_family=AF_INET;hints.ai_socktype=SOCK_STREAM;
                if(getaddrinfo(srv.proxy_host.c_str(),nullptr,&hints,&res)!=0||!res){close();return false;}
                pa.sin_addr=((sockaddr_in*)res->ai_addr)->sin_addr;freeaddrinfo(res);}
            if(::connect(sock,(sockaddr*)&pa,sizeof(pa))<0){close();return false;}
            bool hasA=!srv.proxy_user.empty();
            uint8_t gr[4]={0x05,(uint8_t)(hasA?2:1),0x00,0x02};
            if(!sa(gr,hasA?4:3)){close();return false;}
            uint8_t gp[2];if(!ra(gp,2)||gp[0]!=0x05){close();return false;}
            if(gp[1]==0x02&&hasA){
                std::vector<uint8_t> au;au.push_back(0x01);
                au.push_back((uint8_t)srv.proxy_user.size());
                au.insert(au.end(),srv.proxy_user.begin(),srv.proxy_user.end());
                au.push_back((uint8_t)srv.proxy_pass.size());
                au.insert(au.end(),srv.proxy_pass.begin(),srv.proxy_pass.end());
                if(!sa(au.data(),(int)au.size())){close();return false;}
                uint8_t ar[2];if(!ra(ar,2)||ar[1]!=0x00){close();return false;}
            } else if(gp[1]!=0x00){close();return false;}
            std::vector<uint8_t> rq;rq.push_back(0x05);rq.push_back(0x01);rq.push_back(0x00);
            rq.push_back(0x03);rq.push_back((uint8_t)srv.host.size());
            rq.insert(rq.end(),srv.host.begin(),srv.host.end());
            rq.push_back((uint8_t)(srv.port>>8));rq.push_back((uint8_t)(srv.port&0xFF));
            if(!sa(rq.data(),(int)rq.size())){close();return false;}
            uint8_t rr[10];if(!ra(rr,4)||rr[1]!=0x00){close();return false;}
            if(rr[3]==0x01){uint8_t d[6];ra(d,6);}
            else if(rr[3]==0x03){uint8_t dl;ra(&dl,1);std::vector<uint8_t>d(dl+2);ra(d.data(),(int)d.size());}
            else if(rr[3]==0x04){uint8_t d[18];ra(d,18);}
        } else {
            sockaddr_in addr{};addr.sin_family=AF_INET;addr.sin_port=htons((uint16_t)srv.port);
            if(inet_pton(AF_INET,srv.host.c_str(),&addr.sin_addr)!=1){
                addrinfo hints{},*res=nullptr;hints.ai_family=AF_INET;hints.ai_socktype=SOCK_STREAM;
                if(getaddrinfo(srv.host.c_str(),nullptr,&hints,&res)!=0||!res){close();return false;}
                addr.sin_addr=((sockaddr_in*)res->ai_addr)->sin_addr;freeaddrinfo(res);}
            if(::connect(sock,(sockaddr*)&addr,sizeof(addr))<0){close();return false;}
        }
        // CCcam handshake
        uint8_t seed[16]; if(!ra(seed,16)){close();return false;}
        uint8_t cd[16]; memcpy(cd,seed,16); cc_crypt_xor(cd);
        uint8_t ch[20]; sha1(cd,16,ch);
        recvBlock.init(ch,20); recvBlock.decrypt(cd,16);
        sendBlock.init(cd,16); sendBlock.decrypt(ch,20);
        uint8_t hc[20]; memcpy(hc,ch,20); sendBlock.encrypt(hc,20);
        if(!sa(hc,20)){close();return false;}
        uint8_t ub[20]={};memcpy(ub,srv.user.data(),std::min(srv.user.size(),(size_t)19));
        sendBlock.encrypt(ub,20);if(!sa(ub,20)){close();return false;}
        if(!srv.pass.empty()){std::vector<uint8_t>pd(srv.pass.begin(),srv.pass.end());sendBlock.encrypt(pd.data(),(int)pd.size());}
        uint8_t cc6[6]={'C','C','c','a','m',0};sendBlock.encrypt(cc6,6);if(!sa(cc6,6)){close();return false;}
        uint8_t ack[20];if(!ra(ack,20)){close();return false;}
        recvBlock.decrypt(ack,20);if(memcmp(ack,"CCcam",5)!=0){close();return false;}
        // Drain cards (2s)
        uint8_t cH[4]={0,0x00,0,0};sendBlock.encrypt(cH,4);sa(cH,4);
        setTo(2000);
        for(int a=0;a<300;a++){
            uint8_t uh[4];if(!ra(uh,4))break;recvBlock.decrypt(uh,4);
            uint16_t ml=(uint16_t)((uh[2]<<8)|uh[3]);if(ml>2048)break;
            std::vector<uint8_t>um(ml);if(ml>0&&!ra(um.data(),ml))break;
            if(ml>0)recvBlock.decrypt(um.data(),ml);
            if(uh[1]==0x07)cardCount++;
            else if(uh[1]==0x06){uint8_t ka[4]={0,0x06,0,0};sendBlock.encrypt(ka,4);sa(ka,4);}
            else if(uh[1]==0x08||uh[1]==0x00||uh[1]==0x04||uh[1]==0x05){}
            else break;
        }
        setTo(10000); connected=true; return true;
    }

    bool sendEcm(const uint8_t* data, uint16_t len, uint8_t cwOut[16]) {
        if(!connected||sock==HC_INVALID) return false;
        std::vector<uint8_t> buf(4+len);
        buf[0]=0;buf[1]=0x01;buf[2]=(uint8_t)(len>>8);buf[3]=(uint8_t)(len&0xFF);
        memcpy(buf.data()+4,data,len);
        sendBlock.encrypt(buf.data(),(int)buf.size());
        int s=0;while(s<(int)buf.size()){int r=send(sock,(const char*)buf.data()+s,(int)buf.size()-s,0);if(r<=0){connected=false;return false;}s+=r;}
        for(int t=0;t<10;t++){
            uint8_t hdr[4];int g=0;
            while(g<4){fd_set fs;FD_ZERO(&fs);FD_SET(sock,&fs);timeval tv;tv.tv_sec=10;tv.tv_usec=0;
                int sl=select((int)sock+1,&fs,nullptr,nullptr,&tv);if(sl<=0){connected=(sl!=0);return false;}
                int r=recv(sock,(char*)hdr+g,4-g,0);if(r<=0){connected=false;return false;}g+=r;}
            recvBlock.decrypt(hdr,4);uint16_t ml=(uint16_t)((hdr[2]<<8)|hdr[3]);if(ml>2048){connected=false;return false;}
            uint8_t mb[2048];if(ml>0){g=0;while(g<ml){int r=recv(sock,(char*)mb+g,ml-g,0);if(r<=0){connected=false;return false;}g+=r;}recvBlock.decrypt(mb,ml);}
            if(hdr[1]==0x01&&ml>=16){memcpy(cwOut,mb,16);return true;}
            else if(hdr[1]==0xFE||hdr[1]==0xFF)return false;
            else if(hdr[1]==0x06){uint8_t ka[4]={0,0x06,0,0};sendBlock.encrypt(ka,4);send(sock,(const char*)ka,4,0);}
            else if(hdr[1]==0x07||hdr[1]==0x04||hdr[1]==0x05||hdr[1]==0x08)continue;
            else return false;
        }
        return false;
    }

    void sendKeepalive() {
        if(!connected||sock==HC_INVALID)return;
        uint8_t ka[4]={0,0x06,0,0};sendBlock.encrypt(ka,4);
        if(send(sock,(const char*)ka,4,0)<=0)connected=false;
    }
};

// ═══════════════════════════════════════════════════════════════════════════
//  SERVICE 1: ECM HARVESTER
// ═══════════════════════════════════════════════════════════════════════════
class EcmHarvester {
public:
    struct Stats {
        int total_targets=0, total_harvests=0, total_success=0, total_fail=0;
        int cycle_count=0, servers_connected=0, servers_total=0;
        float harvest_rate=0; bool running=false;
        std::string current_target;
    };
    int probe_interval_ms = 3000;
    int reconnect_interval_s = 60;
    std::atomic<bool> running{false};
    std::function<void(const std::string&)> onLog;
    std::function<void(uint16_t caid,uint32_t provid,uint16_t sid,
        const uint8_t* ecm,int ecmLen,bool ok,const uint8_t* cw,
        const std::string& server,float latMs)> onResult;

    void addTarget(uint16_t caid,uint32_t provid,uint16_t sid,
                   const uint8_t* msg,int msgLen){
        std::lock_guard<std::mutex> g(mu_);
        uint32_t k=((uint32_t)caid<<16)|sid;
        auto& t=targets_[k]; t.caid=caid; t.provid=provid; t.sid=sid;
        t.ecm_msg.assign(msg,msg+msgLen); t.captured_at=time(nullptr);
    }
    void start(std::vector<UpstreamServer>* srvs){
        if(running)return; servers_=srvs; running=true;
        startTime_=std::chrono::steady_clock::now();
        thread_=std::thread(&EcmHarvester::loop,this);
    }
    void stop(){
        running=false; if(thread_.joinable())thread_.join();
        for(auto& c:conns_)c.close(); conns_.clear();
    }
    Stats getStats() const {
        std::lock_guard<std::mutex> g(mu_);
        Stats s; s.total_targets=(int)targets_.size();
        s.total_harvests=totalH_; s.total_success=totalOk_; s.total_fail=totalFail_;
        s.cycle_count=cycles_; s.running=running.load(); s.current_target=curTarget_;
        int cn=0; for(auto& c:conns_)if(c.connected)cn++;
        s.servers_connected=cn; s.servers_total=(int)conns_.size();
        auto el=std::chrono::duration_cast<std::chrono::seconds>(
            std::chrono::steady_clock::now()-startTime_).count();
        s.harvest_rate=el>0?(float)totalH_/el*60:0;
        return s;
    }
    std::vector<HarvestTarget> getTargets() const {
        std::lock_guard<std::mutex> g(mu_);
        std::vector<HarvestTarget> r;
        for(auto&[k,t]:targets_)r.push_back(t);
        std::sort(r.begin(),r.end(),[](auto&a,auto&b){return a.success_count>b.success_count;});
        return r;
    }
private:
    mutable std::mutex mu_;
    std::thread thread_;
    std::vector<UpstreamServer>* servers_=nullptr;
    std::map<uint32_t,HarvestTarget> targets_;
    std::vector<HarvestConn> conns_;
    int totalH_=0,totalOk_=0,totalFail_=0,cycles_=0;
    std::string curTarget_;
    std::chrono::steady_clock::time_point startTime_;

    void lg(const std::string& s){if(onLog)onLog("[Harvester] "+s);}

    void reconnectAll(){
        if(!servers_)return;
        std::vector<UpstreamServer*> en;
        for(auto& s:*servers_)if(s.enabled&&s.valid())en.push_back(&s);
        while(conns_.size()<en.size())conns_.emplace_back();
        while(conns_.size()>en.size()){conns_.back().close();conns_.pop_back();}
        int cn=0;
        for(size_t i=0;i<en.size()&&i<conns_.size();i++){
            if(!conns_[i].connected){
                if(conns_[i].connect(*en[i],5000)){
                    lg("Connected "+conns_[i].label+" ("+std::to_string(conns_[i].cardCount)+" cards)");cn++;}
            } else cn++;
        }
        lg("Upstream: "+std::to_string(cn)+"/"+std::to_string(en.size()));
    }

    void loop(){
        lg("Started");
        auto lastRec=std::chrono::steady_clock::now();
        auto lastKA=lastRec;
        while(running){
            auto now=std::chrono::steady_clock::now();
            if(std::chrono::duration_cast<std::chrono::seconds>(now-lastRec).count()>=reconnect_interval_s||conns_.empty()){
                reconnectAll(); lastRec=now;}
            if(std::chrono::duration_cast<std::chrono::seconds>(now-lastKA).count()>=25){
                for(auto& c:conns_)if(c.connected)c.sendKeepalive(); lastKA=now;}
            // Pick oldest target
            HarvestTarget tgt; bool have=false;
            {   std::lock_guard<std::mutex> g(mu_);
                if(!targets_.empty()){
                    time_t oldest=time(nullptr); uint32_t bk=0;
                    for(auto&[k,t]:targets_)if(t.last_harvest<oldest&&!t.ecm_msg.empty()){oldest=t.last_harvest;bk=k;}
                    if(targets_.count(bk)){tgt=targets_[bk];have=true;
                        char b[32];snprintf(b,sizeof(b),"%04X:%04X",tgt.caid,tgt.sid);curTarget_=b;}
                }
            }
            if(have){
                for(auto& conn:conns_){
                    if(!conn.connected||!running)continue;
                    auto t0=std::chrono::steady_clock::now();
                    uint8_t cw[16]={};
                    bool got=conn.sendEcm(tgt.ecm_msg.data(),(uint16_t)tgt.ecm_msg.size(),cw);
                    float lat=std::chrono::duration<float,std::milli>(std::chrono::steady_clock::now()-t0).count();
                    {   std::lock_guard<std::mutex> g(mu_); totalH_++;
                        uint32_t k=((uint32_t)tgt.caid<<16)|tgt.sid;
                        auto it=targets_.find(k);
                        if(it!=targets_.end()){it->second.harvest_count++;it->second.last_harvest=time(nullptr);
                            if(got){it->second.success_count++;it->second.last_success=time(nullptr);
                                memcpy(it->second.last_cw.data(),cw,16);it->second.best_server=conn.label;totalOk_++;}
                            else{it->second.fail_count++;totalFail_++;}
                        }
                    }
                    if(onResult){
                        int bl=(int)tgt.ecm_msg.size()>13?(int)tgt.ecm_msg.size()-13:0;
                        const uint8_t* bp=tgt.ecm_msg.size()>13?tgt.ecm_msg.data()+13:nullptr;
                        onResult(tgt.caid,tgt.provid,tgt.sid,bp,bl,got,cw,conn.label,lat);
                    }
                    if(got)break;
                }
            }
            std::this_thread::sleep_for(std::chrono::milliseconds(probe_interval_ms));
            {   std::lock_guard<std::mutex> g(mu_);
                if(targets_.size()>0&&totalH_>0&&totalH_%(int)targets_.size()==0)cycles_++;
            }
        }
        for(auto& c:conns_)c.close(); conns_.clear(); lg("Stopped");
    }
};

// ═══════════════════════════════════════════════════════════════════════════
//  SERVICE 2: OFFLINE CW DATABASE
// ═══════════════════════════════════════════════════════════════════════════
class OfflineCwDb {
public:
    struct Stats {
        int total_entries=0, unique_channels=0;
        int lookups=0, hits=0, misses=0;
        float hit_rate=0;
    };
    void store(uint16_t caid,uint16_t sid,uint32_t provid,
               const uint8_t* ecm,int ecmLen,const uint8_t* cw,const std::string& srv){
        std::lock_guard<std::mutex> g(mu_);
        std::string k=makeKey(caid,sid,ecm,std::min(ecmLen,8));
        auto& e=entries_[k]; e.caid=caid;e.sid=sid;e.provid=provid;
        memcpy(e.cw.data(),cw,16); e.timestamp=time(nullptr); e.confidence++; e.server=srv;
        channels_.insert(((uint32_t)caid<<16)|sid);
        chanLatest_[chanKey(caid,sid)]=k; dirty_=true;
    }
    bool lookup(uint16_t caid,uint16_t sid,const uint8_t* ecm,int ecmLen,
                uint8_t cwOut[16],int* conf=nullptr){
        std::lock_guard<std::mutex> g(mu_); lookups_++;
        std::string k=makeKey(caid,sid,ecm,std::min(ecmLen,8));
        auto it=entries_.find(k);
        if(it!=entries_.end()){memcpy(cwOut,it->second.cw.data(),16);if(conf)*conf=it->second.confidence;hits_++;return true;}
        k=makeKey(caid,sid,ecm,std::min(ecmLen,4));it=entries_.find(k);
        if(it!=entries_.end()&&it->second.confidence>=3){memcpy(cwOut,it->second.cw.data(),16);if(conf)*conf=it->second.confidence;hits_++;return true;}
        auto ci=chanLatest_.find(chanKey(caid,sid));
        if(ci!=chanLatest_.end()){auto ei=entries_.find(ci->second);
            if(ei!=entries_.end()&&ei->second.confidence>=5){memcpy(cwOut,ei->second.cw.data(),16);if(conf)*conf=ei->second.confidence;hits_++;return true;}}
        misses_++;return false;
    }
    Stats getStats() const {
        std::lock_guard<std::mutex> g(mu_); Stats s;
        s.total_entries=(int)entries_.size();s.unique_channels=(int)channels_.size();
        s.lookups=lookups_;s.hits=hits_;s.misses=misses_;
        s.hit_rate=lookups_>0?(float)hits_/lookups_*100:0; return s;
    }
    bool save(const std::string& p=""){
        std::lock_guard<std::mutex> g(mu_);
        std::string fp=p.empty()?dfPath():p;
        try{std::ofstream f(fp,std::ios::binary);if(!f.is_open())return false;
            f<<"CWDB1\n"<<entries_.size()<<"\n";
            for(auto&[k,e]:entries_){
                f<<k<<" "<<e.caid<<" "<<e.sid<<" "<<e.provid<<" ";
                for(int i=0;i<16;i++){char h[4];snprintf(h,sizeof(h),"%02X",e.cw[i]);f<<h;}
                f<<" "<<e.timestamp<<" "<<e.confidence<<" "<<e.server<<"\n";}
            dirty_=false;return true;
        }catch(...){return false;}
    }
    bool load(const std::string& p=""){
        std::lock_guard<std::mutex> g(mu_);
        std::string fp=p.empty()?dfPath():p;
        try{std::ifstream f(fp);if(!f.is_open())return false;
            std::string hdr;std::getline(f,hdr);if(hdr!="CWDB1")return false;
            size_t sz;f>>sz;entries_.clear();channels_.clear();
            for(size_t i=0;i<sz;i++){
                std::string k;OfflineCwEntry e;std::string cwH;
                f>>k>>e.caid>>e.sid>>e.provid>>cwH>>e.timestamp>>e.confidence>>e.server;
                for(int j=0;j<16&&j*2+1<(int)cwH.size();j++){unsigned b;sscanf(cwH.c_str()+j*2,"%02x",&b);e.cw[j]=(uint8_t)b;}
                entries_[k]=e;channels_.insert(((uint32_t)e.caid<<16)|e.sid);
                chanLatest_[chanKey(e.caid,e.sid)]=k;}
            dirty_=false;return true;
        }catch(...){return false;}
    }
    bool isDirty()const{return dirty_;}
private:
    mutable std::mutex mu_;
    std::map<std::string,OfflineCwEntry> entries_;
    std::set<uint32_t> channels_;
    std::map<std::string,std::string> chanLatest_;
    int lookups_=0,hits_=0,misses_=0; bool dirty_=false;
    static std::string toHex(const uint8_t* d,int n){std::string r;for(int i=0;i<n;i++){char h[4];snprintf(h,sizeof(h),"%02X",d[i]);r+=h;}return r;}
    std::string makeKey(uint16_t caid,uint16_t sid,const uint8_t* e,int pl)const{
        char h[16];snprintf(h,sizeof(h),"%04X%04X_",caid,sid);return std::string(h)+toHex(e,pl);}
    std::string chanKey(uint16_t caid,uint16_t sid)const{char b[12];snprintf(b,sizeof(b),"CH%04X%04X",caid,sid);return b;}
    static std::string dfPath(){return CccamConfig::exeDir()+"gmscreen_cwdb.dat";}
};

// ═══════════════════════════════════════════════════════════════════════════
//  SERVICE 3: AI TRAINER
// ═══════════════════════════════════════════════════════════════════════════
class AiTrainer {
public:
    struct Stats {
        int train_cycles=0, patterns_discovered=0, models_updated=0;
        bool running=false; time_t last_train=0; std::string status;
    };
    int train_interval_s = 30;
    int save_interval_s = 120;
    std::atomic<bool> running{false};
    std::function<void(const std::string&)> onLog;
    // Set before start:
    ai::CwPredictor* predictor = nullptr;
    CwLearner* learner = nullptr;
    OfflineCwDb* offlineDb = nullptr;

    void start(){
        if(running||!predictor||!learner)return;
        running=true; thread_=std::thread(&AiTrainer::loop,this);
    }
    void stop(){running=false;if(thread_.joinable())thread_.join();}
    Stats getStats()const{
        std::lock_guard<std::mutex> g(mu_);Stats s;
        s.train_cycles=cycles_;s.patterns_discovered=patterns_;s.models_updated=saves_;
        s.running=running.load();s.last_train=lastTrain_;s.status=status_;return s;
    }
private:
    mutable std::mutex mu_;
    std::thread thread_;
    int cycles_=0,patterns_=0,saves_=0;
    time_t lastTrain_=0,lastSave_=0;
    std::string status_;
    void lg(const std::string& s){if(onLog)onLog("[AiTrainer] "+s);std::lock_guard<std::mutex>g(mu_);status_=s;}
    void loop(){
        lg("Started"); lastSave_=time(nullptr);
        while(running){
            std::this_thread::sleep_for(std::chrono::seconds(train_interval_s));
            if(!running)break;
            time_t now=time(nullptr);
            {std::lock_guard<std::mutex>g(mu_);cycles_++;lastTrain_=now;}
            // Analyze channel stats
            if(predictor&&offlineDb){
                auto chs=predictor->getChannelStatsList();
                int np=0;for(auto&c:chs)if(c.ecm_total>0&&c.success_rate>0.5f)np++;
                {std::lock_guard<std::mutex>g(mu_);patterns_+=np;
                 status_="Analyzed "+std::to_string(chs.size())+" channels";}
            }
            // Periodic save
            if(now-lastSave_>=save_interval_s){
                lastSave_=now;
                if(predictor)try{predictor->save();}catch(...){}
                if(learner)try{learner->save();}catch(...){}
                if(offlineDb&&offlineDb->isDirty())try{offlineDb->save();}catch(...){}
                {std::lock_guard<std::mutex>g(mu_);saves_++;}
                lg("Models saved");
            }
        }
        if(predictor)try{predictor->save();}catch(...){}
        if(learner)try{learner->save();}catch(...){}
        if(offlineDb&&offlineDb->isDirty())try{offlineDb->save();}catch(...){}
        lg("Stopped");
    }
};

} // namespace cccam
