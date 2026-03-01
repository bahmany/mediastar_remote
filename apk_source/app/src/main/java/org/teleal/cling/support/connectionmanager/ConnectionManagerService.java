package org.teleal.cling.support.connectionmanager;

import com.baoyz.swipemenulistview.BuildConfig;
import java.beans.PropertyChangeSupport;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;
import org.teleal.cling.binding.annotations.UpnpAction;
import org.teleal.cling.binding.annotations.UpnpInputArgument;
import org.teleal.cling.binding.annotations.UpnpOutputArgument;
import org.teleal.cling.binding.annotations.UpnpService;
import org.teleal.cling.binding.annotations.UpnpServiceId;
import org.teleal.cling.binding.annotations.UpnpServiceType;
import org.teleal.cling.binding.annotations.UpnpStateVariable;
import org.teleal.cling.binding.annotations.UpnpStateVariables;
import org.teleal.cling.model.ServiceReference;
import org.teleal.cling.model.action.ActionException;
import org.teleal.cling.model.types.UnsignedIntegerFourBytes;
import org.teleal.cling.model.types.csv.CSV;
import org.teleal.cling.model.types.csv.CSVUnsignedIntegerFourBytes;
import org.teleal.cling.support.model.ConnectionInfo;
import org.teleal.cling.support.model.ProtocolInfo;
import org.teleal.cling.support.model.ProtocolInfos;

@UpnpService(serviceId = @UpnpServiceId("ConnectionManager"), serviceType = @UpnpServiceType(value = "ConnectionManager", version = 1), stringConvertibleTypes = {ProtocolInfo.class, ProtocolInfos.class, ServiceReference.class})
@UpnpStateVariables({@UpnpStateVariable(datatype = "string", name = "SourceProtocolInfo"), @UpnpStateVariable(datatype = "string", name = "SinkProtocolInfo"), @UpnpStateVariable(datatype = "string", name = "CurrentConnectionIDs"), @UpnpStateVariable(allowedValuesEnum = ConnectionInfo.Status.class, name = "A_ARG_TYPE_ConnectionStatus", sendEvents = BuildConfig.DEBUG), @UpnpStateVariable(datatype = "string", name = "A_ARG_TYPE_ConnectionManager", sendEvents = BuildConfig.DEBUG), @UpnpStateVariable(allowedValuesEnum = ConnectionInfo.Direction.class, name = "A_ARG_TYPE_Direction", sendEvents = BuildConfig.DEBUG), @UpnpStateVariable(datatype = "string", name = "A_ARG_TYPE_ProtocolInfo", sendEvents = BuildConfig.DEBUG), @UpnpStateVariable(datatype = "i4", name = "A_ARG_TYPE_ConnectionID", sendEvents = BuildConfig.DEBUG), @UpnpStateVariable(datatype = "i4", name = "A_ARG_TYPE_AVTransportID", sendEvents = BuildConfig.DEBUG), @UpnpStateVariable(datatype = "i4", name = "A_ARG_TYPE_RcsID", sendEvents = BuildConfig.DEBUG)})
/* loaded from: classes.dex */
public class ConnectionManagerService {
    private static final Logger log = Logger.getLogger(ConnectionManagerService.class.getName());
    protected final Map<Integer, ConnectionInfo> activeConnections;
    protected final PropertyChangeSupport propertyChangeSupport;
    protected final ProtocolInfos sinkProtocolInfo;
    protected final ProtocolInfos sourceProtocolInfo;

    public ConnectionManagerService() {
        this(new ConnectionInfo());
    }

    public ConnectionManagerService(ProtocolInfos sourceProtocolInfo, ProtocolInfos sinkProtocolInfo) {
        this(sourceProtocolInfo, sinkProtocolInfo, new ConnectionInfo());
    }

    public ConnectionManagerService(ConnectionInfo... activeConnections) {
        this(null, new ProtocolInfos(new ProtocolInfo[0]), new ProtocolInfos(new ProtocolInfo[0]), activeConnections);
    }

    public ConnectionManagerService(ProtocolInfos sourceProtocolInfo, ProtocolInfos sinkProtocolInfo, ConnectionInfo... activeConnections) {
        this(null, sourceProtocolInfo, sinkProtocolInfo, activeConnections);
    }

    public ConnectionManagerService(PropertyChangeSupport propertyChangeSupport, ProtocolInfos sourceProtocolInfo, ProtocolInfos sinkProtocolInfo, ConnectionInfo... activeConnections) {
        this.activeConnections = new ConcurrentHashMap();
        this.propertyChangeSupport = propertyChangeSupport == null ? new PropertyChangeSupport(this) : propertyChangeSupport;
        this.sourceProtocolInfo = sourceProtocolInfo;
        this.sinkProtocolInfo = sinkProtocolInfo;
        for (ConnectionInfo activeConnection : activeConnections) {
            this.activeConnections.put(Integer.valueOf(activeConnection.getConnectionID()), activeConnection);
        }
    }

    public PropertyChangeSupport getPropertyChangeSupport() {
        return this.propertyChangeSupport;
    }

    @UpnpAction(out = {@UpnpOutputArgument(getterName = "getRcsID", name = "RcsID"), @UpnpOutputArgument(getterName = "getAvTransportID", name = "AVTransportID"), @UpnpOutputArgument(getterName = "getProtocolInfo", name = "ProtocolInfo"), @UpnpOutputArgument(getterName = "getPeerConnectionManager", name = "PeerConnectionManager", stateVariable = "A_ARG_TYPE_ConnectionManager"), @UpnpOutputArgument(getterName = "getPeerConnectionID", name = "PeerConnectionID", stateVariable = "A_ARG_TYPE_ConnectionID"), @UpnpOutputArgument(getterName = "getDirection", name = "Direction"), @UpnpOutputArgument(getterName = "getConnectionStatus", name = "Status", stateVariable = "A_ARG_TYPE_ConnectionStatus")})
    public synchronized ConnectionInfo getCurrentConnectionInfo(@UpnpInputArgument(name = "ConnectionID") int connectionId) throws ActionException {
        ConnectionInfo info;
        log.fine("Getting connection information of connection ID: " + connectionId);
        info = this.activeConnections.get(Integer.valueOf(connectionId));
        if (info == null) {
            throw new ConnectionManagerException(ConnectionManagerErrorCode.INVALID_CONNECTION_REFERENCE, "Non-active connection ID: " + connectionId);
        }
        return info;
    }

    @UpnpAction(out = {@UpnpOutputArgument(name = "ConnectionIDs")})
    public synchronized CSV<UnsignedIntegerFourBytes> getCurrentConnectionIDs() {
        CSV<UnsignedIntegerFourBytes> csv;
        csv = new CSVUnsignedIntegerFourBytes();
        for (Integer connectionID : this.activeConnections.keySet()) {
            csv.add(new UnsignedIntegerFourBytes(connectionID.intValue()));
        }
        log.fine("Returning current connection IDs: " + csv.size());
        return csv;
    }

    @UpnpAction(out = {@UpnpOutputArgument(getterName = "getSourceProtocolInfo", name = "Source", stateVariable = "SourceProtocolInfo"), @UpnpOutputArgument(getterName = "getSinkProtocolInfo", name = "Sink", stateVariable = "SinkProtocolInfo")})
    public synchronized void getProtocolInfo() throws ActionException {
    }

    public synchronized ProtocolInfos getSourceProtocolInfo() {
        return this.sourceProtocolInfo;
    }

    public synchronized ProtocolInfos getSinkProtocolInfo() {
        return this.sinkProtocolInfo;
    }
}
