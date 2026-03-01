package org.teleal.cling.model.types;

import java.math.BigInteger;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.MessageDigest;
import java.util.UUID;
import org.teleal.cling.model.ModelUtil;

/* loaded from: classes.dex */
public class UDN {
    public static final String PREFIX = "uuid:";
    private String identifierString;

    public UDN(String identifierString) {
        this.identifierString = identifierString;
    }

    public UDN(UUID uuid) {
        this.identifierString = uuid.toString();
    }

    public boolean isUDA11Compliant() {
        try {
            UUID.fromString(this.identifierString);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    public String getIdentifierString() {
        return this.identifierString;
    }

    public static UDN valueOf(String udnString) {
        if (udnString.startsWith("uuid:")) {
            udnString = udnString.substring("uuid:".length());
        }
        return new UDN(udnString);
    }

    public static UDN uniqueSystemIdentifier(String salt) throws UnknownHostException {
        StringBuilder systemSalt = new StringBuilder();
        try {
            InetAddress i = InetAddress.getLocalHost();
            systemSalt.append(i.getHostName()).append(i.getHostAddress());
        } catch (Exception e) {
            try {
                systemSalt.append(new String(ModelUtil.getFirstNetworkInterfaceHardwareAddress()));
            } catch (Throwable th) {
            }
        }
        systemSalt.append(System.getProperty("os.name"));
        systemSalt.append(System.getProperty("os.version"));
        try {
            byte[] hash = MessageDigest.getInstance("MD5").digest(systemSalt.toString().getBytes());
            return new UDN(new UUID(new BigInteger(-1, hash).longValue(), salt.hashCode()));
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public String toString() {
        return "uuid:" + getIdentifierString();
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || !(o instanceof UDN)) {
            return false;
        }
        UDN udn = (UDN) o;
        return this.identifierString.equals(udn.identifierString);
    }

    public int hashCode() {
        return this.identifierString.hashCode();
    }
}
