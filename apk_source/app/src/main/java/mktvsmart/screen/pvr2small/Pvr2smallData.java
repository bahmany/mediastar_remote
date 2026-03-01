package mktvsmart.screen.pvr2small;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import mktvsmart.screen.channel.Sat2ipUtil;
import mktvsmart.screen.dataconvert.model.DataConvertPvrInfoModel;
import mktvsmart.screen.dataconvert.parser.DataParser;
import mktvsmart.screen.dataconvert.parser.ParserFactory;

/* loaded from: classes.dex */
public class Pvr2smallData {
    private List<DataConvertPvrInfoModel> mPvr2smallList;

    private static class SingletonHolder {
        static final Pvr2smallData INSTANCE = new Pvr2smallData(null);

        private SingletonHolder() {
        }
    }

    public static Pvr2smallData getInstance() {
        return SingletonHolder.INSTANCE;
    }

    private Pvr2smallData() {
        this.mPvr2smallList = new ArrayList();
    }

    /* synthetic */ Pvr2smallData(Pvr2smallData pvr2smallData) {
        this();
    }

    private Object readResolve() {
        return getInstance();
    }

    public List<DataConvertPvrInfoModel> getPvr2smallList() {
        return this.mPvr2smallList;
    }

    public void setPvr2smallList(List<DataConvertPvrInfoModel> pvr2smallList) {
        this.mPvr2smallList = pvr2smallList;
    }

    public void initPvr2SmallList(byte[] recvData) {
        InputStream istream = new ByteArrayInputStream(recvData, 0, recvData.length);
        DataParser parser = ParserFactory.getParser();
        try {
            this.mPvr2smallList = parser.parse(istream, 20);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getPlayUrlBase(int position, String ipAddr) {
        String urlBase = String.valueOf(Sat2ipUtil.getRtspUriBase(ipAddr)) + "record=" + this.mPvr2smallList.get(position).getmPvrId();
        return urlBase;
    }

    public String getPlayUrlQuery() {
        return "";
    }

    public void clearPvr2smallList() {
        if (this.mPvr2smallList != null) {
            this.mPvr2smallList.clear();
        }
    }
}
