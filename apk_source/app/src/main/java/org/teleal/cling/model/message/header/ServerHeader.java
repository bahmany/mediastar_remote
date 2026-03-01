package org.teleal.cling.model.message.header;

import com.hisilicon.multiscreen.protocol.ClientInfo;
import org.teleal.cling.model.ServerClientTokens;
import org.teleal.cling.model.ServiceReference;

/* loaded from: classes.dex */
public class ServerHeader extends UpnpHeader<ServerClientTokens> {
    public ServerHeader() {
        setValue(new ServerClientTokens());
    }

    public ServerHeader(ServerClientTokens tokens) {
        setValue(tokens);
    }

    @Override // org.teleal.cling.model.message.header.UpnpHeader
    public void setString(String s) throws InvalidHeaderException {
        String[] osNameVersion;
        String[] productNameVersion;
        ServerClientTokens serverClientTokens = new ServerClientTokens();
        serverClientTokens.setOsName(ServerClientTokens.UNKNOWN_PLACEHOLDER);
        serverClientTokens.setOsVersion(ServerClientTokens.UNKNOWN_PLACEHOLDER);
        serverClientTokens.setProductName(ServerClientTokens.UNKNOWN_PLACEHOLDER);
        serverClientTokens.setProductVersion(ServerClientTokens.UNKNOWN_PLACEHOLDER);
        if (s.contains("UPnP/1.1")) {
            serverClientTokens.setMinorVersion(1);
        } else if (!s.contains("UPnP/1.")) {
            throw new InvalidHeaderException("Missing 'UPnP/1.' in server information: " + s);
        }
        int numberOfSpaces = 0;
        for (int i = 0; i < s.length(); i++) {
            try {
                if (s.charAt(i) == ' ') {
                    numberOfSpaces++;
                }
            } catch (Exception e) {
                serverClientTokens.setOsName(ServerClientTokens.UNKNOWN_PLACEHOLDER);
                serverClientTokens.setOsVersion(ServerClientTokens.UNKNOWN_PLACEHOLDER);
                serverClientTokens.setProductName(ServerClientTokens.UNKNOWN_PLACEHOLDER);
                serverClientTokens.setProductVersion(ServerClientTokens.UNKNOWN_PLACEHOLDER);
            }
        }
        if (s.contains(ClientInfo.SEPARATOR_BETWEEN_VARS)) {
            String[] productTokens = s.split(ClientInfo.SEPARATOR_BETWEEN_VARS);
            osNameVersion = productTokens[0].split(ServiceReference.DELIMITER);
            productNameVersion = productTokens[2].split(ServiceReference.DELIMITER);
        } else if (numberOfSpaces > 2) {
            String beforeUpnpToken = s.substring(0, s.indexOf("UPnP/1.")).trim();
            String afterUpnpToken = s.substring(s.indexOf("UPnP/1.") + 8).trim();
            osNameVersion = beforeUpnpToken.split(ServiceReference.DELIMITER);
            productNameVersion = afterUpnpToken.split(ServiceReference.DELIMITER);
        } else {
            String[] productTokens2 = s.split(" ");
            osNameVersion = productTokens2[0].split(ServiceReference.DELIMITER);
            productNameVersion = productTokens2[2].split(ServiceReference.DELIMITER);
        }
        serverClientTokens.setOsName(osNameVersion[0].trim());
        if (osNameVersion.length > 1) {
            serverClientTokens.setOsVersion(osNameVersion[1].trim());
        }
        serverClientTokens.setProductName(productNameVersion[0].trim());
        if (productNameVersion.length > 1) {
            serverClientTokens.setProductVersion(productNameVersion[1].trim());
        }
        setValue(serverClientTokens);
    }

    @Override // org.teleal.cling.model.message.header.UpnpHeader
    public String getString() {
        return getValue().getHttpToken();
    }
}
