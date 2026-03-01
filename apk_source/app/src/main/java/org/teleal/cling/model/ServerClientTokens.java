package org.teleal.cling.model;

/* loaded from: classes.dex */
public class ServerClientTokens {
    public static final String UNKNOWN_PLACEHOLDER = "UNKNOWN";
    private int majorVersion;
    private int minorVersion;
    private String osName;
    private String osVersion;
    private String productName;
    private String productVersion;

    public ServerClientTokens() {
        this.majorVersion = 1;
        this.minorVersion = 0;
        this.osName = System.getProperty("os.name").replaceAll("[^a-zA-Z0-9\\.\\-_]", "");
        this.osVersion = System.getProperty("os.version").replaceAll("[^a-zA-Z0-9\\.\\-_]", "");
        this.productName = Constants.PRODUCT_TOKEN_NAME;
        this.productVersion = "1.0";
    }

    public ServerClientTokens(int majorVersion, int minorVersion) {
        this.majorVersion = 1;
        this.minorVersion = 0;
        this.osName = System.getProperty("os.name").replaceAll("[^a-zA-Z0-9\\.\\-_]", "");
        this.osVersion = System.getProperty("os.version").replaceAll("[^a-zA-Z0-9\\.\\-_]", "");
        this.productName = Constants.PRODUCT_TOKEN_NAME;
        this.productVersion = "1.0";
        this.majorVersion = majorVersion;
        this.minorVersion = minorVersion;
    }

    public ServerClientTokens(int majorVersion, int minorVersion, String osName, String osVersion, String productName, String productVersion) {
        this.majorVersion = 1;
        this.minorVersion = 0;
        this.osName = System.getProperty("os.name").replaceAll("[^a-zA-Z0-9\\.\\-_]", "");
        this.osVersion = System.getProperty("os.version").replaceAll("[^a-zA-Z0-9\\.\\-_]", "");
        this.productName = Constants.PRODUCT_TOKEN_NAME;
        this.productVersion = "1.0";
        this.majorVersion = majorVersion;
        this.minorVersion = minorVersion;
        this.osName = osName;
        this.osVersion = osVersion;
        this.productName = productName;
        this.productVersion = productVersion;
    }

    public int getMajorVersion() {
        return this.majorVersion;
    }

    public void setMajorVersion(int majorVersion) {
        this.majorVersion = majorVersion;
    }

    public int getMinorVersion() {
        return this.minorVersion;
    }

    public void setMinorVersion(int minorVersion) {
        this.minorVersion = minorVersion;
    }

    public String getOsName() {
        return this.osName;
    }

    public void setOsName(String osName) {
        this.osName = osName;
    }

    public String getOsVersion() {
        return this.osVersion;
    }

    public void setOsVersion(String osVersion) {
        this.osVersion = osVersion;
    }

    public String getProductName() {
        return this.productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductVersion() {
        return this.productVersion;
    }

    public void setProductVersion(String productVersion) {
        this.productVersion = productVersion;
    }

    public String toString() {
        return String.valueOf(getOsName()) + ServiceReference.DELIMITER + getOsVersion() + " UPnP/" + getMajorVersion() + "." + getMinorVersion() + " " + getProductName() + ServiceReference.DELIMITER + getProductVersion();
    }

    public String getHttpToken() {
        return String.valueOf(getOsName().replaceAll(" ", "_")) + ServiceReference.DELIMITER + getOsVersion().replaceAll(" ", "_") + " UPnP/" + getMajorVersion() + "." + getMinorVersion() + " " + getProductName().replaceAll(" ", "_") + ServiceReference.DELIMITER + getProductVersion().replaceAll(" ", "_");
    }

    public String getOsToken() {
        return String.valueOf(getOsName().replaceAll(" ", "_")) + ServiceReference.DELIMITER + getOsVersion().replaceAll(" ", "_");
    }

    public String getProductToken() {
        return String.valueOf(getProductName().replaceAll(" ", "_")) + ServiceReference.DELIMITER + getProductVersion().replaceAll(" ", "_");
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ServerClientTokens that = (ServerClientTokens) o;
        return this.majorVersion == that.majorVersion && this.minorVersion == that.minorVersion && this.osName.equals(that.osName) && this.osVersion.equals(that.osVersion) && this.productName.equals(that.productName) && this.productVersion.equals(that.productVersion);
    }

    public int hashCode() {
        int result = this.majorVersion;
        return (((((((((result * 31) + this.minorVersion) * 31) + this.osName.hashCode()) * 31) + this.osVersion.hashCode()) * 31) + this.productName.hashCode()) * 31) + this.productVersion.hashCode();
    }
}
