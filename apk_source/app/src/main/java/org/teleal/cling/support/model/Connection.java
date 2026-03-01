package org.teleal.cling.support.model;

import org.teleal.cling.model.types.UnsignedIntegerFourBytes;

/* loaded from: classes.dex */
public class Connection {

    public enum Error {
        ERROR_NONE,
        ERROR_COMMAND_ABORTED,
        ERROR_NOT_ENABLED_FOR_INTERNET,
        ERROR_USER_DISCONNECT,
        ERROR_ISP_DISCONNECT,
        ERROR_IDLE_DISCONNECT,
        ERROR_FORCED_DISCONNECT,
        ERROR_NO_CARRIER,
        ERROR_IP_CONFIGURATION,
        ERROR_UNKNOWN;

        /* renamed from: values, reason: to resolve conflict with enum method */
        public static Error[] valuesCustom() {
            Error[] errorArrValuesCustom = values();
            int length = errorArrValuesCustom.length;
            Error[] errorArr = new Error[length];
            System.arraycopy(errorArrValuesCustom, 0, errorArr, 0, length);
            return errorArr;
        }
    }

    public enum Status {
        Unconfigured,
        Connecting,
        Connected,
        PendingDisconnect,
        Disconnecting,
        Disconnected;

        /* renamed from: values, reason: to resolve conflict with enum method */
        public static Status[] valuesCustom() {
            Status[] statusArrValuesCustom = values();
            int length = statusArrValuesCustom.length;
            Status[] statusArr = new Status[length];
            System.arraycopy(statusArrValuesCustom, 0, statusArr, 0, length);
            return statusArr;
        }
    }

    public enum Type {
        Unconfigured,
        IP_Routed,
        IP_Bridged;

        /* renamed from: values, reason: to resolve conflict with enum method */
        public static Type[] valuesCustom() {
            Type[] typeArrValuesCustom = values();
            int length = typeArrValuesCustom.length;
            Type[] typeArr = new Type[length];
            System.arraycopy(typeArrValuesCustom, 0, typeArr, 0, length);
            return typeArr;
        }
    }

    public static class StatusInfo {
        private Error lastError;
        private Status status;
        private long uptimeSeconds;

        public StatusInfo(Status status, UnsignedIntegerFourBytes uptime, Error lastError) {
            this(status, uptime.getValue().longValue(), lastError);
        }

        public StatusInfo(Status status, long uptimeSeconds, Error lastError) {
            this.status = status;
            this.uptimeSeconds = uptimeSeconds;
            this.lastError = lastError;
        }

        public Status getStatus() {
            return this.status;
        }

        public long getUptimeSeconds() {
            return this.uptimeSeconds;
        }

        public UnsignedIntegerFourBytes getUptime() {
            return new UnsignedIntegerFourBytes(getUptimeSeconds());
        }

        public Error getLastError() {
            return this.lastError;
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            StatusInfo that = (StatusInfo) o;
            return this.uptimeSeconds == that.uptimeSeconds && this.lastError == that.lastError && this.status == that.status;
        }

        public int hashCode() {
            int result = this.status.hashCode();
            return (((result * 31) + ((int) (this.uptimeSeconds ^ (this.uptimeSeconds >>> 32)))) * 31) + this.lastError.hashCode();
        }

        public String toString() {
            return "(" + getClass().getSimpleName() + ") " + getStatus();
        }
    }
}
