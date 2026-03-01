package org.teleal.cling.support.avtransport.impl;

import java.net.URI;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;
import org.teleal.cling.model.ModelUtil;
import org.teleal.cling.model.types.ErrorCode;
import org.teleal.cling.model.types.UnsignedIntegerFourBytes;
import org.teleal.cling.support.avtransport.AVTransportErrorCode;
import org.teleal.cling.support.avtransport.AVTransportException;
import org.teleal.cling.support.avtransport.AbstractAVTransportService;
import org.teleal.cling.support.avtransport.impl.state.AbstractState;
import org.teleal.cling.support.lastchange.LastChange;
import org.teleal.cling.support.model.AVTransport;
import org.teleal.cling.support.model.DeviceCapabilities;
import org.teleal.cling.support.model.MediaInfo;
import org.teleal.cling.support.model.PlayMode;
import org.teleal.cling.support.model.PositionInfo;
import org.teleal.cling.support.model.RecordQualityMode;
import org.teleal.cling.support.model.SeekMode;
import org.teleal.cling.support.model.StorageMedium;
import org.teleal.cling.support.model.TransportAction;
import org.teleal.cling.support.model.TransportInfo;
import org.teleal.cling.support.model.TransportSettings;
import org.teleal.common.statemachine.StateMachineBuilder;
import org.teleal.common.statemachine.TransitionException;

/* loaded from: classes.dex */
public class AVTransportService<T extends AVTransport> extends AbstractAVTransportService {
    private static final Logger log = Logger.getLogger(AVTransportService.class.getName());
    final Class<? extends AbstractState> initialState;
    final Class<? extends AVTransportStateMachine> stateMachineDefinition;
    private final Map<Long, AVTransportStateMachine> stateMachines;
    final Class<? extends AVTransport> transportClass;

    public AVTransportService(Class<? extends AVTransportStateMachine> stateMachineDefinition, Class<? extends AbstractState> initialState) {
        this(stateMachineDefinition, initialState, AVTransport.class);
    }

    /* JADX WARN: Multi-variable type inference failed */
    public AVTransportService(Class<? extends AVTransportStateMachine> stateMachineDefinition, Class<? extends AbstractState> initialState, Class<T> cls) {
        this.stateMachines = new ConcurrentHashMap();
        this.stateMachineDefinition = stateMachineDefinition;
        this.initialState = initialState;
        this.transportClass = cls;
    }

    @Override // org.teleal.cling.support.avtransport.AbstractAVTransportService
    public void setAVTransportURI(UnsignedIntegerFourBytes instanceId, String currentURI, String currentURIMetaData) throws AVTransportException {
        try {
            URI uri = new URI(currentURI);
            try {
                AVTransportStateMachine transportStateMachine = findStateMachine(instanceId, true);
                transportStateMachine.setTransportURI(uri, currentURIMetaData);
            } catch (TransitionException ex) {
                throw new AVTransportException(AVTransportErrorCode.TRANSITION_NOT_AVAILABLE, ex.getMessage());
            }
        } catch (Exception e) {
            throw new AVTransportException(ErrorCode.INVALID_ARGS, "CurrentURI can not be null or malformed");
        }
    }

    @Override // org.teleal.cling.support.avtransport.AbstractAVTransportService
    public void setNextAVTransportURI(UnsignedIntegerFourBytes instanceId, String nextURI, String nextURIMetaData) throws AVTransportException {
        try {
            URI uri = new URI(nextURI);
            try {
                AVTransportStateMachine transportStateMachine = findStateMachine(instanceId, true);
                transportStateMachine.setNextTransportURI(uri, nextURIMetaData);
            } catch (TransitionException ex) {
                throw new AVTransportException(AVTransportErrorCode.TRANSITION_NOT_AVAILABLE, ex.getMessage());
            }
        } catch (Exception e) {
            throw new AVTransportException(ErrorCode.INVALID_ARGS, "NextURI can not be null or malformed");
        }
    }

    @Override // org.teleal.cling.support.avtransport.AbstractAVTransportService
    public void setPlayMode(UnsignedIntegerFourBytes instanceId, String newPlayMode) throws AVTransportException {
        AVTransport transport = ((AbstractState) findStateMachine(instanceId).getCurrentState()).getTransport();
        try {
            transport.setTransportSettings(new TransportSettings(PlayMode.valueOf(newPlayMode), transport.getTransportSettings().getRecQualityMode()));
        } catch (IllegalArgumentException e) {
            throw new AVTransportException(AVTransportErrorCode.PLAYMODE_NOT_SUPPORTED, "Unsupported play mode: " + newPlayMode);
        }
    }

    @Override // org.teleal.cling.support.avtransport.AbstractAVTransportService
    public void setRecordQualityMode(UnsignedIntegerFourBytes instanceId, String newRecordQualityMode) throws AVTransportException {
        AVTransport transport = ((AbstractState) findStateMachine(instanceId).getCurrentState()).getTransport();
        try {
            transport.setTransportSettings(new TransportSettings(transport.getTransportSettings().getPlayMode(), RecordQualityMode.valueOrExceptionOf(newRecordQualityMode)));
        } catch (IllegalArgumentException e) {
            throw new AVTransportException(AVTransportErrorCode.RECORDQUALITYMODE_NOT_SUPPORTED, "Unsupported record quality mode: " + newRecordQualityMode);
        }
    }

    @Override // org.teleal.cling.support.avtransport.AbstractAVTransportService
    public MediaInfo getMediaInfo(UnsignedIntegerFourBytes instanceId) throws AVTransportException {
        return ((AbstractState) findStateMachine(instanceId).getCurrentState()).getTransport().getMediaInfo();
    }

    @Override // org.teleal.cling.support.avtransport.AbstractAVTransportService
    public TransportInfo getTransportInfo(UnsignedIntegerFourBytes instanceId) throws AVTransportException {
        return ((AbstractState) findStateMachine(instanceId).getCurrentState()).getTransport().getTransportInfo();
    }

    @Override // org.teleal.cling.support.avtransport.AbstractAVTransportService
    public PositionInfo getPositionInfo(UnsignedIntegerFourBytes instanceId) throws AVTransportException {
        return ((AbstractState) findStateMachine(instanceId).getCurrentState()).getTransport().getPositionInfo();
    }

    @Override // org.teleal.cling.support.avtransport.AbstractAVTransportService
    public DeviceCapabilities getDeviceCapabilities(UnsignedIntegerFourBytes instanceId) throws AVTransportException {
        return ((AbstractState) findStateMachine(instanceId).getCurrentState()).getTransport().getDeviceCapabilities();
    }

    @Override // org.teleal.cling.support.avtransport.AbstractAVTransportService
    public TransportSettings getTransportSettings(UnsignedIntegerFourBytes instanceId) throws AVTransportException {
        return ((AbstractState) findStateMachine(instanceId).getCurrentState()).getTransport().getTransportSettings();
    }

    @Override // org.teleal.cling.support.avtransport.AbstractAVTransportService
    public String getCurrentTransportActions(UnsignedIntegerFourBytes instanceId) throws AVTransportException {
        AVTransportStateMachine stateMachine = findStateMachine(instanceId);
        try {
            TransportAction[] actions = ((AbstractState) stateMachine.getCurrentState()).getCurrentTransportActions();
            return ModelUtil.toCommaSeparatedList(actions);
        } catch (TransitionException e) {
            return "";
        }
    }

    @Override // org.teleal.cling.support.avtransport.AbstractAVTransportService
    public void stop(UnsignedIntegerFourBytes instanceId) throws AVTransportException {
        try {
            findStateMachine(instanceId).stop();
        } catch (TransitionException ex) {
            throw new AVTransportException(AVTransportErrorCode.TRANSITION_NOT_AVAILABLE, ex.getMessage());
        }
    }

    @Override // org.teleal.cling.support.avtransport.AbstractAVTransportService
    public void play(UnsignedIntegerFourBytes instanceId, String speed) throws AVTransportException {
        try {
            findStateMachine(instanceId).play(speed);
        } catch (TransitionException ex) {
            throw new AVTransportException(AVTransportErrorCode.TRANSITION_NOT_AVAILABLE, ex.getMessage());
        }
    }

    @Override // org.teleal.cling.support.avtransport.AbstractAVTransportService
    public void pause(UnsignedIntegerFourBytes instanceId) throws AVTransportException {
        try {
            findStateMachine(instanceId).pause();
        } catch (TransitionException ex) {
            throw new AVTransportException(AVTransportErrorCode.TRANSITION_NOT_AVAILABLE, ex.getMessage());
        }
    }

    @Override // org.teleal.cling.support.avtransport.AbstractAVTransportService
    public void record(UnsignedIntegerFourBytes instanceId) throws AVTransportException {
        try {
            findStateMachine(instanceId).record();
        } catch (TransitionException ex) {
            throw new AVTransportException(AVTransportErrorCode.TRANSITION_NOT_AVAILABLE, ex.getMessage());
        }
    }

    @Override // org.teleal.cling.support.avtransport.AbstractAVTransportService
    public void seek(UnsignedIntegerFourBytes instanceId, String unit, String target) throws AVTransportException {
        try {
            SeekMode seekMode = SeekMode.valueOrExceptionOf(unit);
            try {
                findStateMachine(instanceId).seek(seekMode, target);
            } catch (TransitionException ex) {
                throw new AVTransportException(AVTransportErrorCode.TRANSITION_NOT_AVAILABLE, ex.getMessage());
            }
        } catch (IllegalArgumentException e) {
            throw new AVTransportException(AVTransportErrorCode.SEEKMODE_NOT_SUPPORTED, "Unsupported seek mode: " + unit);
        }
    }

    @Override // org.teleal.cling.support.avtransport.AbstractAVTransportService
    public void next(UnsignedIntegerFourBytes instanceId) throws AVTransportException {
        try {
            findStateMachine(instanceId).next();
        } catch (TransitionException ex) {
            throw new AVTransportException(AVTransportErrorCode.TRANSITION_NOT_AVAILABLE, ex.getMessage());
        }
    }

    @Override // org.teleal.cling.support.avtransport.AbstractAVTransportService
    public void previous(UnsignedIntegerFourBytes instanceId) throws AVTransportException {
        try {
            findStateMachine(instanceId).previous();
        } catch (TransitionException ex) {
            throw new AVTransportException(AVTransportErrorCode.TRANSITION_NOT_AVAILABLE, ex.getMessage());
        }
    }

    protected AVTransportStateMachine findStateMachine(UnsignedIntegerFourBytes instanceId) throws AVTransportException {
        return findStateMachine(instanceId, true);
    }

    protected AVTransportStateMachine findStateMachine(UnsignedIntegerFourBytes instanceId, boolean createDefaultTransport) throws AVTransportException {
        AVTransportStateMachine stateMachine;
        synchronized (this.stateMachines) {
            long id = instanceId.getValue().longValue();
            stateMachine = this.stateMachines.get(Long.valueOf(id));
            if (stateMachine == null && id == 0 && createDefaultTransport) {
                log.fine("Creating default transport instance with ID '0'");
                stateMachine = createStateMachine(instanceId);
                this.stateMachines.put(Long.valueOf(id), stateMachine);
            } else if (stateMachine == null) {
                throw new AVTransportException(AVTransportErrorCode.INVALID_INSTANCE_ID);
            }
            log.fine("Found transport control with ID '" + id + "'");
        }
        return stateMachine;
    }

    protected AVTransportStateMachine createStateMachine(UnsignedIntegerFourBytes instanceId) {
        return (AVTransportStateMachine) StateMachineBuilder.build(this.stateMachineDefinition, this.initialState, new Class[]{this.transportClass}, new Object[]{createTransport(instanceId, getLastChange())});
    }

    protected AVTransport createTransport(UnsignedIntegerFourBytes instanceId, LastChange lastChange) {
        return new AVTransport(instanceId, lastChange, StorageMedium.NETWORK);
    }
}
