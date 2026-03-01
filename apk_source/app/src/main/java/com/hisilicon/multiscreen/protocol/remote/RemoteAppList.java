package com.hisilicon.multiscreen.protocol.remote;

import com.hisilicon.multiscreen.http.HiHttpClient;
import com.hisilicon.multiscreen.http.HiHttpResponse;
import com.hisilicon.multiscreen.protocol.HiDeviceInfo;
import com.hisilicon.multiscreen.protocol.message.Action;
import com.hisilicon.multiscreen.protocol.message.AppInfo;
import com.hisilicon.multiscreen.protocol.message.Argument;
import com.hisilicon.multiscreen.protocol.message.ArgumentValue;
import com.hisilicon.multiscreen.protocol.utils.LogTool;
import com.hisilicon.multiscreen.protocol.utils.SaxXmlUtil;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import org.xml.sax.SAXException;

/* loaded from: classes.dex */
public class RemoteAppList {
    private static final String ENCODE_CHARSET = "UTF-8";
    private static final int HTTP_REQUEST_TIMEOUT = 3000;
    private static final int STB_PUSH_SERVER_PORT = 8867;
    public ArrayList<AppInfo> mAppList = new ArrayList<>();
    private HiHttpClient mHiHttpClient;
    private String mHostIp;
    private SaxXmlUtil mSaxXmlUtil;

    public RemoteAppList(HiDeviceInfo deviceInfo) {
        this.mHiHttpClient = null;
        this.mHostIp = "";
        this.mSaxXmlUtil = null;
        if (deviceInfo == null) {
            LogTool.e("device info is null in remote app list.");
            return;
        }
        this.mHostIp = deviceInfo.getDeviceIP();
        this.mHiHttpClient = new HiHttpClient(this.mHostIp, STB_PUSH_SERVER_PORT, 3000);
        this.mSaxXmlUtil = new SaxXmlUtil();
    }

    public void destroy() {
        this.mAppList.clear();
    }

    protected void resetDevice(HiDeviceInfo deviceInfo) {
        this.mHostIp = deviceInfo.getDeviceIP();
        this.mHiHttpClient.setHostIp(this.mHostIp);
    }

    public void launchApp(String packagName) throws IOException, IllegalArgumentException {
        Action updateAction = getLaunchAppAction(new Action(), packagName);
        sendRequestAction(updateAction);
    }

    public void updateAppList() throws IOException, IllegalArgumentException {
        Action updateAction = getUpdateAppListAction(new Action());
        this.mAppList.clear();
        Action responseAction = sendRequestAction(updateAction);
        if (responseAction == null) {
            LogTool.e("response action is null.");
            return;
        }
        if (responseAction.getId() == 12290) {
            Iterator<Argument> it = responseAction.getArgumentList().iterator();
            while (it.hasNext()) {
                Argument arg = it.next();
                AppInfo appInfo = new AppInfo();
                appInfo.setAppName((String) arg.getArgumentValue(0).getVaule());
                appInfo.setPackageName((String) arg.getArgumentValue(1).getVaule());
                appInfo.setPackageIndex(((Integer) arg.getArgumentValue(2).getVaule()).intValue());
                byte[] icon = (byte[]) arg.getArgumentValue(3).getVaule();
                appInfo.setPackageIcon(icon);
                this.mAppList.add(appInfo);
            }
        }
    }

    private Action getUpdateAppListAction(Action action) {
        Argument argument = new Argument();
        ArgumentValue argumentValue = new ArgumentValue("reserve", 0);
        argument.addArgumentValue(argumentValue);
        action.setId(12289);
        action.setName("getAppList");
        action.setResponseFlag("yes");
        action.setResponseId(12290);
        action.addArgument(argument);
        return action;
    }

    private Action getLaunchAppAction(Action action, String packageName) {
        Argument argument = new Argument();
        ArgumentValue argumentValue = new ArgumentValue("PackageName", packageName);
        argument.addArgumentValue(argumentValue);
        action.setId(Action.ACTION_ID_REMOTE_APP_LAUNCH);
        action.setName("launchAPP");
        action.setResponseFlag("yes");
        action.setResponseId(Action.ACTION_ID_REMOTE_APP_RET_LAUNCH);
        action.addArgument(argument);
        return action;
    }

    private Action sendRequestAction(Action actionMsg) throws IOException, IllegalArgumentException {
        Action responseAction;
        String str = null;
        String responseContent = null;
        try {
            str = this.mSaxXmlUtil.serialize(actionMsg);
        } catch (TransformerConfigurationException e) {
            LogTool.e(e.getMessage());
            return null;
        } catch (SAXException e2) {
            LogTool.e(e2.getMessage());
        }
        HiHttpResponse httpResponse = this.mHiHttpClient.sendRequest(str);
        try {
            String responseContent2 = new String(httpResponse.getMessage(), "UTF-8");
            responseContent = responseContent2;
        } catch (UnsupportedEncodingException e3) {
            LogTool.e("Unsupported Encoding Exception.");
        }
        if (responseContent == null || responseContent.length() == 0) {
            LogTool.e("action response is null.");
            return null;
        }
        try {
            responseAction = this.mSaxXmlUtil.parse(responseContent.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e4) {
            responseAction = null;
            LogTool.e("unsupported encoding exception.");
        } catch (IOException e5) {
            responseAction = null;
            LogTool.e("IO exception.");
        } catch (ParserConfigurationException e6) {
            responseAction = null;
            LogTool.e("parse configuration exception.");
        } catch (SAXException e7) {
            responseAction = null;
            LogTool.e("SAX exception.");
        }
        return responseAction;
    }
}
