package mktvsmart.screen.gchat.ui;

import android.os.Bundle;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;
import mktvsmart.screen.CreateSocket;
import mktvsmart.screen.GlobalConstantValue;
import mktvsmart.screen.GsSendSocket;
import mktvsmart.screen.R;
import mktvsmart.screen.dataconvert.model.DataConvertChatMsgModel;
import mktvsmart.screen.dataconvert.parser.DataParser;
import mktvsmart.screen.dataconvert.parser.ParserFactory;
import mktvsmart.screen.gchat.bean.GsChatRoomInfo;
import mktvsmart.screen.gchat.bean.GsChatSetting;
import mktvsmart.screen.gchat.bean.GsChatUser;
import mktvsmart.screen.message.process.MessageProcessor;

/* loaded from: classes.dex */
public class GChatByStbFragment extends GChatBaseFragment {
    private DataParser mParser = ParserFactory.getParser();
    private Socket mTcpSocket;

    @Override // mktvsmart.screen.gchat.ui.GChatBaseFragment, android.support.v4.app.Fragment
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override // mktvsmart.screen.gchat.ui.GChatBaseFragment, android.support.v4.app.Fragment
    public void onActivityCreated(Bundle savedInstanceState) throws UnsupportedEncodingException {
        super.onActivityCreated(savedInstanceState);
        try {
            CreateSocket cSocket = new CreateSocket(null, 0);
            this.mTcpSocket = cSocket.GetSocket();
        } catch (Exception e) {
            e.printStackTrace();
        }
        setMessageProcess();
        GsSendSocket.sendOnlyCommandSocketToStb(this.mTcpSocket, 100);
        GsSendSocket.sendOnlyCommandSocketToStb(this.mTcpSocket, 102);
        this.mIsChatStart = true;
        setChatByStb(true);
    }

    @Override // mktvsmart.screen.gchat.ui.GChatBaseFragment
    public void finish() throws UnsupportedEncodingException {
        GsSendSocket.sendOnlyCommandSocketToStb(this.mTcpSocket, GlobalConstantValue.GMS_MSG_GCHAT_DO_EXIT);
        getActivity().finish();
    }

    @Override // mktvsmart.screen.gchat.ui.GChatBaseFragment
    public void initViews() {
        super.initViews();
        this.mSetUsernameBtn.setVisibility(8);
    }

    @Override // mktvsmart.screen.gchat.ui.GChatBaseFragment
    public void sendBlockListCommand(List<GsChatUser> blockList) throws SocketException, UnsupportedEncodingException {
        try {
            byte[] data_buff = this.mParser.serialize(blockList, GlobalConstantValue.GMS_MSG_GCHAT_DO_BLOCK_UNBLOCK_USER).getBytes("UTF-8");
            this.mTcpSocket.setSoTimeout(3000);
            GsSendSocket.sendSocketToStb(data_buff, this.mTcpSocket, 0, data_buff.length, GlobalConstantValue.GMS_MSG_GCHAT_DO_BLOCK_UNBLOCK_USER);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override // mktvsmart.screen.gchat.ui.GChatBaseFragment
    public void doMessageSend(DataConvertChatMsgModel newMsg) throws SocketException, UnsupportedEncodingException {
        List<DataConvertChatMsgModel> models = new ArrayList<>();
        models.add(newMsg);
        try {
            byte[] data_buff = this.mParser.serialize(models, GlobalConstantValue.GMS_MSG_GCHAT_DO_SEND_MSG).getBytes("UTF-8");
            this.mTcpSocket.setSoTimeout(3000);
            GsSendSocket.sendSocketToStb(data_buff, this.mTcpSocket, 0, data_buff.length, GlobalConstantValue.GMS_MSG_GCHAT_DO_SEND_MSG);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override // mktvsmart.screen.gchat.ui.GChatBaseFragment
    protected GsChatUser getOwnerUserInfo() {
        GsChatUser user = new GsChatUser();
        user.setUserID(GsChatSetting.getInstance().getUserId());
        user.setUsername(GsChatSetting.getInstance().getUsername());
        return user;
    }

    private void setMessageProcess() {
        MessageProcessor msgProcessor = MessageProcessor.obtain();
        msgProcessor.recycle();
        msgProcessor.setOnMessageProcess(2101, getActivity(), new MessageProcessor.PerformOnBackground() { // from class: mktvsmart.screen.gchat.ui.GChatByStbFragment.1
            @Override // mktvsmart.screen.message.process.MessageProcessor.PerformOnBackground
            public void doInBackground(Message msg) throws UnsupportedEncodingException {
                GsSendSocket.sendOnlyCommandSocketToStb(GChatByStbFragment.this.mTcpSocket, 100);
            }
        });
        msgProcessor.setOnMessageProcess(100, getActivity(), new MessageProcessor.PerformOnForeground() { // from class: mktvsmart.screen.gchat.ui.GChatByStbFragment.2
            @Override // mktvsmart.screen.message.process.MessageProcessor.PerformOnForeground
            public void doInForeground(Message msg) {
                if (msg.arg1 > 0) {
                    try {
                        Bundle data = msg.getData();
                        byte[] recvData = data.getByteArray("ReceivedData");
                        if (recvData != null) {
                            InputStream istream = new ByteArrayInputStream(recvData, 0, recvData.length);
                            GChatByStbFragment.this.mChatRoomInfo = (GsChatRoomInfo) GChatByStbFragment.this.mParser.parse(istream, 22).get(0);
                            GChatByStbFragment.this.mEpgTitle.setText(GChatByStbFragment.this.mChatRoomInfo.getEventTitle());
                            GChatByStbFragment.this.mUserNum.setText(GChatByStbFragment.this.getResources().getString(R.string.gchat_num_format, Integer.valueOf(GChatByStbFragment.this.mChatRoomInfo.getOnlineNum())));
                            if (GChatByStbFragment.this.mCurrentRoomID != GChatByStbFragment.this.mChatRoomInfo.getRoomID()) {
                                GChatByStbFragment.this.mCurrentRoomID = GChatByStbFragment.this.mChatRoomInfo.getRoomID();
                                GChatByStbFragment.this.clearMessages();
                            }
                            GChatByStbFragment.this.mLoginBar.setVisibility(8);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        msgProcessor.setOnMessageProcess(2102, getActivity(), new MessageProcessor.PerformOnBackground() { // from class: mktvsmart.screen.gchat.ui.GChatByStbFragment.3
            @Override // mktvsmart.screen.message.process.MessageProcessor.PerformOnBackground
            public void doInBackground(Message msg) throws UnsupportedEncodingException {
                GsSendSocket.sendOnlyCommandSocketToStb(GChatByStbFragment.this.mTcpSocket, 101);
            }
        });
        msgProcessor.setOnMessageProcess(101, getActivity(), new MessageProcessor.PerformOnForeground() { // from class: mktvsmart.screen.gchat.ui.GChatByStbFragment.4
            /* JADX WARN: Multi-variable type inference failed */
            @Override // mktvsmart.screen.message.process.MessageProcessor.PerformOnForeground
            public void doInForeground(Message msg) {
                if (msg.arg1 > 0) {
                    Bundle data = msg.getData();
                    byte[] recvData = data.getByteArray("ReceivedData");
                    if (recvData != null) {
                        InputStream inStream = new ByteArrayInputStream(recvData, 0, recvData.length);
                        try {
                            List<?> list = GChatByStbFragment.this.mParser.parse(inStream, 23);
                            if (list != null && !list.isEmpty()) {
                                GChatByStbFragment.this.messageInsert((List<DataConvertChatMsgModel>) list);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });
        msgProcessor.setOnMessageProcess(2100, getActivity(), new MessageProcessor.PerformOnForeground() { // from class: mktvsmart.screen.gchat.ui.GChatByStbFragment.5
            @Override // mktvsmart.screen.message.process.MessageProcessor.PerformOnForeground
            public void doInForeground(Message msg) throws UnsupportedEncodingException {
                Toast.makeText(GChatByStbFragment.this.getActivity(), R.string.chat_quit, 0).show();
                GChatByStbFragment.this.finish();
            }
        });
        msgProcessor.setOnMessageProcess(102, getActivity(), new MessageProcessor.PerformOnForeground() { // from class: mktvsmart.screen.gchat.ui.GChatByStbFragment.6
            @Override // mktvsmart.screen.message.process.MessageProcessor.PerformOnForeground
            public void doInForeground(Message msg) {
                if (msg.arg1 > 0) {
                    try {
                        Bundle data = msg.getData();
                        byte[] recvData = data.getByteArray("ReceivedData");
                        if (recvData != null) {
                            InputStream istream = new ByteArrayInputStream(recvData, 0, recvData.length);
                            GChatByStbFragment.this.mBlockList = GChatByStbFragment.this.mParser.parse(istream, 24);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        msgProcessor.setOnMessageProcess(GlobalConstantValue.GMS_MSG_GCHAT_DO_BLOCK_UNBLOCK_USER, getActivity(), new MessageProcessor.PerformOnForeground() { // from class: mktvsmart.screen.gchat.ui.GChatByStbFragment.7
            @Override // mktvsmart.screen.message.process.MessageProcessor.PerformOnForeground
            public void doInForeground(Message msg) throws UnsupportedEncodingException {
                GsSendSocket.sendOnlyCommandSocketToStb(GChatByStbFragment.this.mTcpSocket, 102);
            }
        });
        msgProcessor.setOnMessageProcess(2103, getActivity(), new MessageProcessor.PerformOnForeground() { // from class: mktvsmart.screen.gchat.ui.GChatByStbFragment.8
            @Override // mktvsmart.screen.message.process.MessageProcessor.PerformOnForeground
            public void doInForeground(Message msg) throws UnsupportedEncodingException {
                GsSendSocket.sendOnlyCommandSocketToStb(GChatByStbFragment.this.mTcpSocket, 105);
            }
        });
        msgProcessor.setOnMessageProcess(105, getActivity(), new MessageProcessor.PerformOnForeground() { // from class: mktvsmart.screen.gchat.ui.GChatByStbFragment.9
            @Override // mktvsmart.screen.message.process.MessageProcessor.PerformOnForeground
            public void doInForeground(Message msg) {
            }
        });
        msgProcessor.setOnMessageProcess(GlobalConstantValue.GSCMD_NOTIFY_SOCKET_CLOSED, getActivity(), new MessageProcessor.PerformOnForeground() { // from class: mktvsmart.screen.gchat.ui.GChatByStbFragment.10
            @Override // mktvsmart.screen.message.process.MessageProcessor.PerformOnForeground
            public void doInForeground(Message msg) throws UnsupportedEncodingException {
                GChatByStbFragment.this.finish();
            }
        });
    }
}
