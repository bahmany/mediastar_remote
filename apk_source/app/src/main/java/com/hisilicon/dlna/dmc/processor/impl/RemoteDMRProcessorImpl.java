package com.hisilicon.dlna.dmc.processor.impl;

import com.hisilicon.dlna.dmc.gui.activity.AppPreference;
import com.hisilicon.dlna.dmc.processor.interfaces.DMRProcessor;
import com.hisilicon.dlna.dmc.processor.interfaces.PlaylistProcessor;
import com.hisilicon.dlna.dmc.processor.model.PlaylistItem;
import com.hisilicon.dlna.dmc.processor.upnp.mediaserver.HttpServerUtil;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.teleal.cling.controlpoint.ControlPoint;
import org.teleal.cling.model.ModelUtil;
import org.teleal.cling.model.action.ActionInvocation;
import org.teleal.cling.model.message.UpnpResponse;
import org.teleal.cling.model.meta.Action;
import org.teleal.cling.model.meta.Device;
import org.teleal.cling.model.meta.Service;
import org.teleal.cling.model.types.ServiceType;
import org.teleal.cling.support.avtransport.callback.GetPositionInfo;
import org.teleal.cling.support.avtransport.callback.GetTransportInfo;
import org.teleal.cling.support.avtransport.callback.Pause;
import org.teleal.cling.support.avtransport.callback.Play;
import org.teleal.cling.support.avtransport.callback.Seek;
import org.teleal.cling.support.avtransport.callback.SetAVTransportURI;
import org.teleal.cling.support.avtransport.callback.Stop;
import org.teleal.cling.support.model.DIDLObject;
import org.teleal.cling.support.model.PositionInfo;
import org.teleal.cling.support.model.SeekMode;
import org.teleal.cling.support.model.TransportInfo;
import org.teleal.cling.support.model.TransportState;
import org.teleal.cling.support.renderingcontrol.callback.GetVolume;
import org.teleal.cling.support.renderingcontrol.callback.SetVolume;

/* loaded from: classes.dex */
public class RemoteDMRProcessorImpl implements DMRProcessor {
    private static final int AUTO_NEXT_DELAY = 4;
    private static final int MAX_VOLUME = 100;
    private static final long SEEK_DELAY_INTERVAL = 200;
    private static final String TAG = RemoteDMRProcessorImpl.class.getName();
    private static final int UPDATE_INTERVAL = 1000;
    private Service m_avtransportService;
    private ControlPoint m_controlPoint;
    private PlaylistItem m_currentItem;
    private int m_currentPosition;
    private TransportState m_currentState;
    private int m_currentVolume;
    private Device m_device;
    private List<DMRProcessor.DMRProcessorListener> m_listeners;
    private Service m_renderingControl;
    private UpdateThread m_updateThread;
    private boolean m_isBusy = false;
    private boolean isSeeked = false;
    private volatile boolean imagePlay = false;
    private volatile boolean mediaPlay = false;
    private volatile boolean mediaNext = false;
    private volatile boolean mediaMiddleEnd = false;
    private boolean m_checkGetPositionInfo = false;
    private boolean m_checkGetTransportInfo = false;
    private boolean m_checkGetVolumeInfo = false;
    private int m_autoNextPending = 4;

    private class UpdateThread extends Thread {
        private boolean running;

        public UpdateThread() {
            this.running = false;
            this.running = true;
        }

        public void stopThread() {
            this.running = false;
            interrupt();
        }

        @Override // java.lang.Thread, java.lang.Runnable
        public void run() throws InterruptedException {
            while (this.running && RemoteDMRProcessorImpl.this.m_avtransportService != null) {
                if (!RemoteDMRProcessorImpl.this.m_checkGetPositionInfo) {
                    RemoteDMRProcessorImpl.this.m_checkGetPositionInfo = true;
                    RemoteDMRProcessorImpl.this.m_controlPoint.execute(new GetPositionInfo(RemoteDMRProcessorImpl.this.m_avtransportService) { // from class: com.hisilicon.dlna.dmc.processor.impl.RemoteDMRProcessorImpl.UpdateThread.1
                        AnonymousClass1(Service $anonymous0) {
                            super($anonymous0);
                        }

                        @Override // org.teleal.cling.controlpoint.ActionCallback
                        public void failure(ActionInvocation invocation, UpnpResponse response, String defaultMsg) {
                            RemoteDMRProcessorImpl.this.fireOnFailEvent(invocation.getAction(), response, defaultMsg);
                            RemoteDMRProcessorImpl.this.m_checkGetPositionInfo = false;
                        }

                        @Override // org.teleal.cling.support.avtransport.callback.GetPositionInfo
                        public void received(ActionInvocation invocation, PositionInfo positionInfo) {
                            RemoteDMRProcessorImpl.this.m_currentPosition = (int) positionInfo.getTrackElapsedSeconds();
                            RemoteDMRProcessorImpl.this.fireUpdatePositionEvent(positionInfo.getTrackElapsedSeconds(), positionInfo.getTrackDurationSeconds());
                            RemoteDMRProcessorImpl.this.m_checkGetPositionInfo = false;
                        }
                    });
                }
                if (!RemoteDMRProcessorImpl.this.m_checkGetTransportInfo) {
                    RemoteDMRProcessorImpl.this.m_checkGetTransportInfo = true;
                    RemoteDMRProcessorImpl.this.m_controlPoint.execute(new GetTransportInfo(RemoteDMRProcessorImpl.this.m_avtransportService) { // from class: com.hisilicon.dlna.dmc.processor.impl.RemoteDMRProcessorImpl.UpdateThread.2
                        private static /* synthetic */ int[] $SWITCH_TABLE$org$teleal$cling$support$model$TransportState;

                        AnonymousClass2(Service $anonymous0) {
                            super($anonymous0);
                        }

                        static /* synthetic */ int[] $SWITCH_TABLE$org$teleal$cling$support$model$TransportState() {
                            int[] iArr = $SWITCH_TABLE$org$teleal$cling$support$model$TransportState;
                            if (iArr == null) {
                                iArr = new int[TransportState.valuesCustom().length];
                                try {
                                    iArr[TransportState.CUSTOM.ordinal()] = 8;
                                } catch (NoSuchFieldError e) {
                                }
                                try {
                                    iArr[TransportState.NO_MEDIA_PRESENT.ordinal()] = 7;
                                } catch (NoSuchFieldError e2) {
                                }
                                try {
                                    iArr[TransportState.PAUSED_PLAYBACK.ordinal()] = 4;
                                } catch (NoSuchFieldError e3) {
                                }
                                try {
                                    iArr[TransportState.PAUSED_RECORDING.ordinal()] = 5;
                                } catch (NoSuchFieldError e4) {
                                }
                                try {
                                    iArr[TransportState.PLAYING.ordinal()] = 2;
                                } catch (NoSuchFieldError e5) {
                                }
                                try {
                                    iArr[TransportState.RECORDING.ordinal()] = 6;
                                } catch (NoSuchFieldError e6) {
                                }
                                try {
                                    iArr[TransportState.STOPPED.ordinal()] = 1;
                                } catch (NoSuchFieldError e7) {
                                }
                                try {
                                    iArr[TransportState.TRANSITIONING.ordinal()] = 3;
                                } catch (NoSuchFieldError e8) {
                                }
                                $SWITCH_TABLE$org$teleal$cling$support$model$TransportState = iArr;
                            }
                            return iArr;
                        }

                        @Override // org.teleal.cling.controlpoint.ActionCallback
                        public void failure(ActionInvocation invocation, UpnpResponse operation, String defaultMsg) {
                            RemoteDMRProcessorImpl.this.fireOnFailEvent(invocation.getAction(), operation, defaultMsg);
                            RemoteDMRProcessorImpl.this.m_checkGetTransportInfo = false;
                        }

                        @Override // org.teleal.cling.support.avtransport.callback.GetTransportInfo
                        public void received(ActionInvocation invocation, TransportInfo transportInfo) {
                            RemoteDMRProcessorImpl.this.m_currentState = transportInfo.getCurrentTransportState();
                            switch ($SWITCH_TABLE$org$teleal$cling$support$model$TransportState()[transportInfo.getCurrentTransportState().ordinal()]) {
                                case 1:
                                    RemoteDMRProcessorImpl.this.m_currentItem.setPlayStatue(0);
                                    RemoteDMRProcessorImpl.this.fireOnStopedEvent();
                                    if (RemoteDMRProcessorImpl.this.mediaNext) {
                                        System.out.println("----STOPPED---- fireOnEndTrackEvent");
                                        RemoteDMRProcessorImpl.this.fireOnEndTrackEvent();
                                        break;
                                    } else {
                                        RemoteDMRProcessorImpl.this.fireOnMiddleEndEvent();
                                        break;
                                    }
                                case 2:
                                    RemoteDMRProcessorImpl.this.m_currentItem.setPlayStatue(1);
                                    RemoteDMRProcessorImpl.this.fireOnPlayingEvent();
                                    break;
                                case 4:
                                    RemoteDMRProcessorImpl.this.m_currentItem.setPlayStatue(2);
                                    RemoteDMRProcessorImpl.this.fireOnPausedEvent();
                                    break;
                            }
                            RemoteDMRProcessorImpl.this.m_checkGetTransportInfo = false;
                        }
                    });
                }
                if (RemoteDMRProcessorImpl.this.m_renderingControl != null && !RemoteDMRProcessorImpl.this.m_checkGetVolumeInfo) {
                    RemoteDMRProcessorImpl.this.m_checkGetVolumeInfo = true;
                    RemoteDMRProcessorImpl.this.m_controlPoint.execute(new GetVolume(RemoteDMRProcessorImpl.this.m_renderingControl) { // from class: com.hisilicon.dlna.dmc.processor.impl.RemoteDMRProcessorImpl.UpdateThread.3
                        AnonymousClass3(Service $anonymous0) {
                            super($anonymous0);
                        }

                        @Override // org.teleal.cling.controlpoint.ActionCallback
                        public void failure(ActionInvocation invocation, UpnpResponse operation, String defaultMsg) {
                            RemoteDMRProcessorImpl.this.fireOnFailEvent(invocation.getAction(), operation, defaultMsg);
                            RemoteDMRProcessorImpl.this.m_checkGetVolumeInfo = false;
                        }

                        @Override // org.teleal.cling.support.renderingcontrol.callback.GetVolume
                        public void received(ActionInvocation actionInvocation, int currentVolume) {
                            RemoteDMRProcessorImpl.this.m_currentVolume = currentVolume;
                            RemoteDMRProcessorImpl.this.m_checkGetVolumeInfo = false;
                        }
                    });
                }
                try {
                    Thread.sleep(1000L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        /* renamed from: com.hisilicon.dlna.dmc.processor.impl.RemoteDMRProcessorImpl$UpdateThread$1 */
        class AnonymousClass1 extends GetPositionInfo {
            AnonymousClass1(Service $anonymous0) {
                super($anonymous0);
            }

            @Override // org.teleal.cling.controlpoint.ActionCallback
            public void failure(ActionInvocation invocation, UpnpResponse response, String defaultMsg) {
                RemoteDMRProcessorImpl.this.fireOnFailEvent(invocation.getAction(), response, defaultMsg);
                RemoteDMRProcessorImpl.this.m_checkGetPositionInfo = false;
            }

            @Override // org.teleal.cling.support.avtransport.callback.GetPositionInfo
            public void received(ActionInvocation invocation, PositionInfo positionInfo) {
                RemoteDMRProcessorImpl.this.m_currentPosition = (int) positionInfo.getTrackElapsedSeconds();
                RemoteDMRProcessorImpl.this.fireUpdatePositionEvent(positionInfo.getTrackElapsedSeconds(), positionInfo.getTrackDurationSeconds());
                RemoteDMRProcessorImpl.this.m_checkGetPositionInfo = false;
            }
        }

        /* renamed from: com.hisilicon.dlna.dmc.processor.impl.RemoteDMRProcessorImpl$UpdateThread$2 */
        class AnonymousClass2 extends GetTransportInfo {
            private static /* synthetic */ int[] $SWITCH_TABLE$org$teleal$cling$support$model$TransportState;

            AnonymousClass2(Service $anonymous0) {
                super($anonymous0);
            }

            static /* synthetic */ int[] $SWITCH_TABLE$org$teleal$cling$support$model$TransportState() {
                int[] iArr = $SWITCH_TABLE$org$teleal$cling$support$model$TransportState;
                if (iArr == null) {
                    iArr = new int[TransportState.valuesCustom().length];
                    try {
                        iArr[TransportState.CUSTOM.ordinal()] = 8;
                    } catch (NoSuchFieldError e) {
                    }
                    try {
                        iArr[TransportState.NO_MEDIA_PRESENT.ordinal()] = 7;
                    } catch (NoSuchFieldError e2) {
                    }
                    try {
                        iArr[TransportState.PAUSED_PLAYBACK.ordinal()] = 4;
                    } catch (NoSuchFieldError e3) {
                    }
                    try {
                        iArr[TransportState.PAUSED_RECORDING.ordinal()] = 5;
                    } catch (NoSuchFieldError e4) {
                    }
                    try {
                        iArr[TransportState.PLAYING.ordinal()] = 2;
                    } catch (NoSuchFieldError e5) {
                    }
                    try {
                        iArr[TransportState.RECORDING.ordinal()] = 6;
                    } catch (NoSuchFieldError e6) {
                    }
                    try {
                        iArr[TransportState.STOPPED.ordinal()] = 1;
                    } catch (NoSuchFieldError e7) {
                    }
                    try {
                        iArr[TransportState.TRANSITIONING.ordinal()] = 3;
                    } catch (NoSuchFieldError e8) {
                    }
                    $SWITCH_TABLE$org$teleal$cling$support$model$TransportState = iArr;
                }
                return iArr;
            }

            @Override // org.teleal.cling.controlpoint.ActionCallback
            public void failure(ActionInvocation invocation, UpnpResponse operation, String defaultMsg) {
                RemoteDMRProcessorImpl.this.fireOnFailEvent(invocation.getAction(), operation, defaultMsg);
                RemoteDMRProcessorImpl.this.m_checkGetTransportInfo = false;
            }

            @Override // org.teleal.cling.support.avtransport.callback.GetTransportInfo
            public void received(ActionInvocation invocation, TransportInfo transportInfo) {
                RemoteDMRProcessorImpl.this.m_currentState = transportInfo.getCurrentTransportState();
                switch ($SWITCH_TABLE$org$teleal$cling$support$model$TransportState()[transportInfo.getCurrentTransportState().ordinal()]) {
                    case 1:
                        RemoteDMRProcessorImpl.this.m_currentItem.setPlayStatue(0);
                        RemoteDMRProcessorImpl.this.fireOnStopedEvent();
                        if (RemoteDMRProcessorImpl.this.mediaNext) {
                            System.out.println("----STOPPED---- fireOnEndTrackEvent");
                            RemoteDMRProcessorImpl.this.fireOnEndTrackEvent();
                            break;
                        } else {
                            RemoteDMRProcessorImpl.this.fireOnMiddleEndEvent();
                            break;
                        }
                    case 2:
                        RemoteDMRProcessorImpl.this.m_currentItem.setPlayStatue(1);
                        RemoteDMRProcessorImpl.this.fireOnPlayingEvent();
                        break;
                    case 4:
                        RemoteDMRProcessorImpl.this.m_currentItem.setPlayStatue(2);
                        RemoteDMRProcessorImpl.this.fireOnPausedEvent();
                        break;
                }
                RemoteDMRProcessorImpl.this.m_checkGetTransportInfo = false;
            }
        }

        /* renamed from: com.hisilicon.dlna.dmc.processor.impl.RemoteDMRProcessorImpl$UpdateThread$3 */
        class AnonymousClass3 extends GetVolume {
            AnonymousClass3(Service $anonymous0) {
                super($anonymous0);
            }

            @Override // org.teleal.cling.controlpoint.ActionCallback
            public void failure(ActionInvocation invocation, UpnpResponse operation, String defaultMsg) {
                RemoteDMRProcessorImpl.this.fireOnFailEvent(invocation.getAction(), operation, defaultMsg);
                RemoteDMRProcessorImpl.this.m_checkGetVolumeInfo = false;
            }

            @Override // org.teleal.cling.support.renderingcontrol.callback.GetVolume
            public void received(ActionInvocation actionInvocation, int currentVolume) {
                RemoteDMRProcessorImpl.this.m_currentVolume = currentVolume;
                RemoteDMRProcessorImpl.this.m_checkGetVolumeInfo = false;
            }
        }
    }

    public RemoteDMRProcessorImpl(Device dmr, ControlPoint controlPoint) {
        this.m_avtransportService = null;
        this.m_renderingControl = null;
        this.m_updateThread = null;
        this.m_device = dmr;
        this.m_controlPoint = controlPoint;
        this.m_avtransportService = this.m_device.findService(new ServiceType("schemas-upnp-org", "AVTransport"));
        this.m_renderingControl = this.m_device.findService(new ServiceType("schemas-upnp-org", "RenderingControl"));
        if (this.m_renderingControl != null && (this.m_renderingControl.getAction("SetVolume") == null || this.m_renderingControl.getAction("GetVolume") == null)) {
            this.m_renderingControl = null;
        }
        this.m_listeners = new ArrayList();
        this.m_currentItem = new PlaylistItem();
        this.m_updateThread = new UpdateThread();
    }

    @Override // com.hisilicon.dlna.dmc.processor.interfaces.DMRProcessor
    public void play() {
        if (this.m_controlPoint != null && this.m_avtransportService != null) {
            this.m_isBusy = true;
            Play play = new Play(this.m_avtransportService) { // from class: com.hisilicon.dlna.dmc.processor.impl.RemoteDMRProcessorImpl.1
                AnonymousClass1(Service $anonymous0) {
                    super($anonymous0);
                }

                @Override // org.teleal.cling.support.avtransport.callback.Play, org.teleal.cling.controlpoint.ActionCallback
                public void success(ActionInvocation invocation) {
                    super.success(invocation);
                    int position = AppPreference.getPlayPosition();
                    if (position != 0) {
                        RemoteDMRProcessorImpl.this.playingSeek(ModelUtil.toTimeString(position));
                        AppPreference.setPlayPosition(0);
                    }
                    RemoteDMRProcessorImpl.this.m_isBusy = false;
                    RemoteDMRProcessorImpl.this.imagePlay = true;
                }

                @Override // org.teleal.cling.controlpoint.ActionCallback
                public void failure(ActionInvocation invocation, UpnpResponse response, String defaultMsg) {
                    RemoteDMRProcessorImpl.this.fireOnFailEvent(invocation.getAction(), response, defaultMsg);
                    int position = AppPreference.getPlayPosition();
                    if (position != 0) {
                        RemoteDMRProcessorImpl.this.playingSeek(ModelUtil.toTimeString(position));
                        AppPreference.setPlayPosition(0);
                    }
                    RemoteDMRProcessorImpl.this.m_isBusy = false;
                    RemoteDMRProcessorImpl.this.imagePlay = true;
                }
            };
            this.m_controlPoint.execute(play);
        }
    }

    /* renamed from: com.hisilicon.dlna.dmc.processor.impl.RemoteDMRProcessorImpl$1 */
    class AnonymousClass1 extends Play {
        AnonymousClass1(Service $anonymous0) {
            super($anonymous0);
        }

        @Override // org.teleal.cling.support.avtransport.callback.Play, org.teleal.cling.controlpoint.ActionCallback
        public void success(ActionInvocation invocation) {
            super.success(invocation);
            int position = AppPreference.getPlayPosition();
            if (position != 0) {
                RemoteDMRProcessorImpl.this.playingSeek(ModelUtil.toTimeString(position));
                AppPreference.setPlayPosition(0);
            }
            RemoteDMRProcessorImpl.this.m_isBusy = false;
            RemoteDMRProcessorImpl.this.imagePlay = true;
        }

        @Override // org.teleal.cling.controlpoint.ActionCallback
        public void failure(ActionInvocation invocation, UpnpResponse response, String defaultMsg) {
            RemoteDMRProcessorImpl.this.fireOnFailEvent(invocation.getAction(), response, defaultMsg);
            int position = AppPreference.getPlayPosition();
            if (position != 0) {
                RemoteDMRProcessorImpl.this.playingSeek(ModelUtil.toTimeString(position));
                AppPreference.setPlayPosition(0);
            }
            RemoteDMRProcessorImpl.this.m_isBusy = false;
            RemoteDMRProcessorImpl.this.imagePlay = true;
        }
    }

    /* renamed from: com.hisilicon.dlna.dmc.processor.impl.RemoteDMRProcessorImpl$2 */
    class AnonymousClass2 implements Runnable {
        private final /* synthetic */ String val$time;

        AnonymousClass2(String str) {
            str = str;
        }

        @Override // java.lang.Runnable
        public void run() throws InterruptedException {
            while (!RemoteDMRProcessorImpl.this.isSeeked) {
                try {
                    TimeUnit.SECONDS.sleep(1L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (!RemoteDMRProcessorImpl.this.m_isBusy && RemoteDMRProcessorImpl.this.m_controlPoint != null && RemoteDMRProcessorImpl.this.m_avtransportService != null) {
                    Action action = RemoteDMRProcessorImpl.this.m_avtransportService.getAction("GetTransportInfo");
                    if (action != null) {
                        RemoteDMRProcessorImpl.this.m_isBusy = true;
                        GetTransportInfo getTransportInfo = new GetTransportInfo(RemoteDMRProcessorImpl.this.m_avtransportService) { // from class: com.hisilicon.dlna.dmc.processor.impl.RemoteDMRProcessorImpl.2.1
                            private static /* synthetic */ int[] $SWITCH_TABLE$org$teleal$cling$support$model$TransportState;
                            private final /* synthetic */ String val$time;

                            /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
                            AnonymousClass1(Service $anonymous0, String str) {
                                super($anonymous0);
                                str = str;
                            }

                            static /* synthetic */ int[] $SWITCH_TABLE$org$teleal$cling$support$model$TransportState() {
                                int[] iArr = $SWITCH_TABLE$org$teleal$cling$support$model$TransportState;
                                if (iArr == null) {
                                    iArr = new int[TransportState.valuesCustom().length];
                                    try {
                                        iArr[TransportState.CUSTOM.ordinal()] = 8;
                                    } catch (NoSuchFieldError e2) {
                                    }
                                    try {
                                        iArr[TransportState.NO_MEDIA_PRESENT.ordinal()] = 7;
                                    } catch (NoSuchFieldError e3) {
                                    }
                                    try {
                                        iArr[TransportState.PAUSED_PLAYBACK.ordinal()] = 4;
                                    } catch (NoSuchFieldError e4) {
                                    }
                                    try {
                                        iArr[TransportState.PAUSED_RECORDING.ordinal()] = 5;
                                    } catch (NoSuchFieldError e5) {
                                    }
                                    try {
                                        iArr[TransportState.PLAYING.ordinal()] = 2;
                                    } catch (NoSuchFieldError e6) {
                                    }
                                    try {
                                        iArr[TransportState.RECORDING.ordinal()] = 6;
                                    } catch (NoSuchFieldError e7) {
                                    }
                                    try {
                                        iArr[TransportState.STOPPED.ordinal()] = 1;
                                    } catch (NoSuchFieldError e8) {
                                    }
                                    try {
                                        iArr[TransportState.TRANSITIONING.ordinal()] = 3;
                                    } catch (NoSuchFieldError e9) {
                                    }
                                    $SWITCH_TABLE$org$teleal$cling$support$model$TransportState = iArr;
                                }
                                return iArr;
                            }

                            @Override // org.teleal.cling.controlpoint.ActionCallback
                            public void failure(ActionInvocation invocation, UpnpResponse operation, String defaultMsg) {
                                RemoteDMRProcessorImpl.this.m_isBusy = false;
                            }

                            @Override // org.teleal.cling.support.avtransport.callback.GetTransportInfo
                            public void received(ActionInvocation invocation, TransportInfo transportInfo) {
                                switch ($SWITCH_TABLE$org$teleal$cling$support$model$TransportState()[transportInfo.getCurrentTransportState().ordinal()]) {
                                    case 2:
                                        RemoteDMRProcessorImpl.this.seek(str);
                                        RemoteDMRProcessorImpl.this.isSeeked = true;
                                        break;
                                }
                                RemoteDMRProcessorImpl.this.m_isBusy = false;
                            }
                        };
                        RemoteDMRProcessorImpl.this.m_controlPoint.execute(getTransportInfo);
                    }
                }
            }
        }

        /* renamed from: com.hisilicon.dlna.dmc.processor.impl.RemoteDMRProcessorImpl$2$1 */
        class AnonymousClass1 extends GetTransportInfo {
            private static /* synthetic */ int[] $SWITCH_TABLE$org$teleal$cling$support$model$TransportState;
            private final /* synthetic */ String val$time;

            /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
            AnonymousClass1(Service $anonymous0, String str) {
                super($anonymous0);
                str = str;
            }

            static /* synthetic */ int[] $SWITCH_TABLE$org$teleal$cling$support$model$TransportState() {
                int[] iArr = $SWITCH_TABLE$org$teleal$cling$support$model$TransportState;
                if (iArr == null) {
                    iArr = new int[TransportState.valuesCustom().length];
                    try {
                        iArr[TransportState.CUSTOM.ordinal()] = 8;
                    } catch (NoSuchFieldError e2) {
                    }
                    try {
                        iArr[TransportState.NO_MEDIA_PRESENT.ordinal()] = 7;
                    } catch (NoSuchFieldError e3) {
                    }
                    try {
                        iArr[TransportState.PAUSED_PLAYBACK.ordinal()] = 4;
                    } catch (NoSuchFieldError e4) {
                    }
                    try {
                        iArr[TransportState.PAUSED_RECORDING.ordinal()] = 5;
                    } catch (NoSuchFieldError e5) {
                    }
                    try {
                        iArr[TransportState.PLAYING.ordinal()] = 2;
                    } catch (NoSuchFieldError e6) {
                    }
                    try {
                        iArr[TransportState.RECORDING.ordinal()] = 6;
                    } catch (NoSuchFieldError e7) {
                    }
                    try {
                        iArr[TransportState.STOPPED.ordinal()] = 1;
                    } catch (NoSuchFieldError e8) {
                    }
                    try {
                        iArr[TransportState.TRANSITIONING.ordinal()] = 3;
                    } catch (NoSuchFieldError e9) {
                    }
                    $SWITCH_TABLE$org$teleal$cling$support$model$TransportState = iArr;
                }
                return iArr;
            }

            @Override // org.teleal.cling.controlpoint.ActionCallback
            public void failure(ActionInvocation invocation, UpnpResponse operation, String defaultMsg) {
                RemoteDMRProcessorImpl.this.m_isBusy = false;
            }

            @Override // org.teleal.cling.support.avtransport.callback.GetTransportInfo
            public void received(ActionInvocation invocation, TransportInfo transportInfo) {
                switch ($SWITCH_TABLE$org$teleal$cling$support$model$TransportState()[transportInfo.getCurrentTransportState().ordinal()]) {
                    case 2:
                        RemoteDMRProcessorImpl.this.seek(str);
                        RemoteDMRProcessorImpl.this.isSeeked = true;
                        break;
                }
                RemoteDMRProcessorImpl.this.m_isBusy = false;
            }
        }
    }

    public void playingSeek(String time) {
        Thread thread = new Thread(new Runnable() { // from class: com.hisilicon.dlna.dmc.processor.impl.RemoteDMRProcessorImpl.2
            private final /* synthetic */ String val$time;

            AnonymousClass2(String time2) {
                str = time2;
            }

            @Override // java.lang.Runnable
            public void run() throws InterruptedException {
                while (!RemoteDMRProcessorImpl.this.isSeeked) {
                    try {
                        TimeUnit.SECONDS.sleep(1L);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if (!RemoteDMRProcessorImpl.this.m_isBusy && RemoteDMRProcessorImpl.this.m_controlPoint != null && RemoteDMRProcessorImpl.this.m_avtransportService != null) {
                        Action action = RemoteDMRProcessorImpl.this.m_avtransportService.getAction("GetTransportInfo");
                        if (action != null) {
                            RemoteDMRProcessorImpl.this.m_isBusy = true;
                            GetTransportInfo getTransportInfo = new GetTransportInfo(RemoteDMRProcessorImpl.this.m_avtransportService) { // from class: com.hisilicon.dlna.dmc.processor.impl.RemoteDMRProcessorImpl.2.1
                                private static /* synthetic */ int[] $SWITCH_TABLE$org$teleal$cling$support$model$TransportState;
                                private final /* synthetic */ String val$time;

                                /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
                                AnonymousClass1(Service $anonymous0, String str) {
                                    super($anonymous0);
                                    str = str;
                                }

                                static /* synthetic */ int[] $SWITCH_TABLE$org$teleal$cling$support$model$TransportState() {
                                    int[] iArr = $SWITCH_TABLE$org$teleal$cling$support$model$TransportState;
                                    if (iArr == null) {
                                        iArr = new int[TransportState.valuesCustom().length];
                                        try {
                                            iArr[TransportState.CUSTOM.ordinal()] = 8;
                                        } catch (NoSuchFieldError e2) {
                                        }
                                        try {
                                            iArr[TransportState.NO_MEDIA_PRESENT.ordinal()] = 7;
                                        } catch (NoSuchFieldError e3) {
                                        }
                                        try {
                                            iArr[TransportState.PAUSED_PLAYBACK.ordinal()] = 4;
                                        } catch (NoSuchFieldError e4) {
                                        }
                                        try {
                                            iArr[TransportState.PAUSED_RECORDING.ordinal()] = 5;
                                        } catch (NoSuchFieldError e5) {
                                        }
                                        try {
                                            iArr[TransportState.PLAYING.ordinal()] = 2;
                                        } catch (NoSuchFieldError e6) {
                                        }
                                        try {
                                            iArr[TransportState.RECORDING.ordinal()] = 6;
                                        } catch (NoSuchFieldError e7) {
                                        }
                                        try {
                                            iArr[TransportState.STOPPED.ordinal()] = 1;
                                        } catch (NoSuchFieldError e8) {
                                        }
                                        try {
                                            iArr[TransportState.TRANSITIONING.ordinal()] = 3;
                                        } catch (NoSuchFieldError e9) {
                                        }
                                        $SWITCH_TABLE$org$teleal$cling$support$model$TransportState = iArr;
                                    }
                                    return iArr;
                                }

                                @Override // org.teleal.cling.controlpoint.ActionCallback
                                public void failure(ActionInvocation invocation, UpnpResponse operation, String defaultMsg) {
                                    RemoteDMRProcessorImpl.this.m_isBusy = false;
                                }

                                @Override // org.teleal.cling.support.avtransport.callback.GetTransportInfo
                                public void received(ActionInvocation invocation, TransportInfo transportInfo) {
                                    switch ($SWITCH_TABLE$org$teleal$cling$support$model$TransportState()[transportInfo.getCurrentTransportState().ordinal()]) {
                                        case 2:
                                            RemoteDMRProcessorImpl.this.seek(str);
                                            RemoteDMRProcessorImpl.this.isSeeked = true;
                                            break;
                                    }
                                    RemoteDMRProcessorImpl.this.m_isBusy = false;
                                }
                            };
                            RemoteDMRProcessorImpl.this.m_controlPoint.execute(getTransportInfo);
                        }
                    }
                }
            }

            /* renamed from: com.hisilicon.dlna.dmc.processor.impl.RemoteDMRProcessorImpl$2$1 */
            class AnonymousClass1 extends GetTransportInfo {
                private static /* synthetic */ int[] $SWITCH_TABLE$org$teleal$cling$support$model$TransportState;
                private final /* synthetic */ String val$time;

                /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
                AnonymousClass1(Service $anonymous0, String str) {
                    super($anonymous0);
                    str = str;
                }

                static /* synthetic */ int[] $SWITCH_TABLE$org$teleal$cling$support$model$TransportState() {
                    int[] iArr = $SWITCH_TABLE$org$teleal$cling$support$model$TransportState;
                    if (iArr == null) {
                        iArr = new int[TransportState.valuesCustom().length];
                        try {
                            iArr[TransportState.CUSTOM.ordinal()] = 8;
                        } catch (NoSuchFieldError e2) {
                        }
                        try {
                            iArr[TransportState.NO_MEDIA_PRESENT.ordinal()] = 7;
                        } catch (NoSuchFieldError e3) {
                        }
                        try {
                            iArr[TransportState.PAUSED_PLAYBACK.ordinal()] = 4;
                        } catch (NoSuchFieldError e4) {
                        }
                        try {
                            iArr[TransportState.PAUSED_RECORDING.ordinal()] = 5;
                        } catch (NoSuchFieldError e5) {
                        }
                        try {
                            iArr[TransportState.PLAYING.ordinal()] = 2;
                        } catch (NoSuchFieldError e6) {
                        }
                        try {
                            iArr[TransportState.RECORDING.ordinal()] = 6;
                        } catch (NoSuchFieldError e7) {
                        }
                        try {
                            iArr[TransportState.STOPPED.ordinal()] = 1;
                        } catch (NoSuchFieldError e8) {
                        }
                        try {
                            iArr[TransportState.TRANSITIONING.ordinal()] = 3;
                        } catch (NoSuchFieldError e9) {
                        }
                        $SWITCH_TABLE$org$teleal$cling$support$model$TransportState = iArr;
                    }
                    return iArr;
                }

                @Override // org.teleal.cling.controlpoint.ActionCallback
                public void failure(ActionInvocation invocation, UpnpResponse operation, String defaultMsg) {
                    RemoteDMRProcessorImpl.this.m_isBusy = false;
                }

                @Override // org.teleal.cling.support.avtransport.callback.GetTransportInfo
                public void received(ActionInvocation invocation, TransportInfo transportInfo) {
                    switch ($SWITCH_TABLE$org$teleal$cling$support$model$TransportState()[transportInfo.getCurrentTransportState().ordinal()]) {
                        case 2:
                            RemoteDMRProcessorImpl.this.seek(str);
                            RemoteDMRProcessorImpl.this.isSeeked = true;
                            break;
                    }
                    RemoteDMRProcessorImpl.this.m_isBusy = false;
                }
            }
        });
        thread.start();
    }

    @Override // com.hisilicon.dlna.dmc.processor.interfaces.DMRProcessor
    public void pause() {
        if (this.m_controlPoint != null && this.m_avtransportService != null) {
            this.m_isBusy = true;
            Pause pause = new Pause(this.m_avtransportService) { // from class: com.hisilicon.dlna.dmc.processor.impl.RemoteDMRProcessorImpl.3
                AnonymousClass3(Service $anonymous0) {
                    super($anonymous0);
                }

                @Override // org.teleal.cling.support.avtransport.callback.Pause, org.teleal.cling.controlpoint.ActionCallback
                public void success(ActionInvocation invocation) {
                    super.success(invocation);
                    RemoteDMRProcessorImpl.this.m_isBusy = false;
                }

                @Override // org.teleal.cling.controlpoint.ActionCallback
                public void failure(ActionInvocation invocation, UpnpResponse response, String defaultMsg) {
                    RemoteDMRProcessorImpl.this.fireOnFailEvent(invocation.getAction(), response, defaultMsg);
                    RemoteDMRProcessorImpl.this.m_isBusy = false;
                }
            };
            this.m_controlPoint.execute(pause);
        }
    }

    /* renamed from: com.hisilicon.dlna.dmc.processor.impl.RemoteDMRProcessorImpl$3 */
    class AnonymousClass3 extends Pause {
        AnonymousClass3(Service $anonymous0) {
            super($anonymous0);
        }

        @Override // org.teleal.cling.support.avtransport.callback.Pause, org.teleal.cling.controlpoint.ActionCallback
        public void success(ActionInvocation invocation) {
            super.success(invocation);
            RemoteDMRProcessorImpl.this.m_isBusy = false;
        }

        @Override // org.teleal.cling.controlpoint.ActionCallback
        public void failure(ActionInvocation invocation, UpnpResponse response, String defaultMsg) {
            RemoteDMRProcessorImpl.this.fireOnFailEvent(invocation.getAction(), response, defaultMsg);
            RemoteDMRProcessorImpl.this.m_isBusy = false;
        }
    }

    @Override // com.hisilicon.dlna.dmc.processor.interfaces.DMRProcessor
    public void stop() {
        if (this.m_controlPoint != null && this.m_avtransportService != null) {
            this.m_isBusy = true;
            if (this.m_currentPosition != 0) {
                AppPreference.setPlayPosition(this.m_currentPosition);
            }
            Stop stop = new Stop(this.m_avtransportService) { // from class: com.hisilicon.dlna.dmc.processor.impl.RemoteDMRProcessorImpl.4
                AnonymousClass4(Service $anonymous0) {
                    super($anonymous0);
                }

                @Override // org.teleal.cling.support.avtransport.callback.Stop, org.teleal.cling.controlpoint.ActionCallback
                public void success(ActionInvocation invocation) {
                    super.success(invocation);
                    RemoteDMRProcessorImpl.this.fireUpdatePositionEvent(0L, 0L);
                    RemoteDMRProcessorImpl.this.m_isBusy = false;
                }

                @Override // org.teleal.cling.controlpoint.ActionCallback
                public void failure(ActionInvocation invocation, UpnpResponse response, String defaultMsg) {
                    RemoteDMRProcessorImpl.this.fireOnFailEvent(invocation.getAction(), response, defaultMsg);
                    RemoteDMRProcessorImpl.this.m_isBusy = false;
                }
            };
            this.m_controlPoint.execute(stop);
        }
    }

    /* renamed from: com.hisilicon.dlna.dmc.processor.impl.RemoteDMRProcessorImpl$4 */
    class AnonymousClass4 extends Stop {
        AnonymousClass4(Service $anonymous0) {
            super($anonymous0);
        }

        @Override // org.teleal.cling.support.avtransport.callback.Stop, org.teleal.cling.controlpoint.ActionCallback
        public void success(ActionInvocation invocation) {
            super.success(invocation);
            RemoteDMRProcessorImpl.this.fireUpdatePositionEvent(0L, 0L);
            RemoteDMRProcessorImpl.this.m_isBusy = false;
        }

        @Override // org.teleal.cling.controlpoint.ActionCallback
        public void failure(ActionInvocation invocation, UpnpResponse response, String defaultMsg) {
            RemoteDMRProcessorImpl.this.fireOnFailEvent(invocation.getAction(), response, defaultMsg);
            RemoteDMRProcessorImpl.this.m_isBusy = false;
        }
    }

    @Override // com.hisilicon.dlna.dmc.processor.interfaces.DMRProcessor
    public void addListener(DMRProcessor.DMRProcessorListener listener) {
        synchronized (this.m_listeners) {
            if (!this.m_listeners.contains(listener)) {
                this.m_listeners.add(listener);
            }
            if (this.m_avtransportService == null) {
                fireOnErrorEvent("Cannot get service on this device");
            }
        }
    }

    @Override // com.hisilicon.dlna.dmc.processor.interfaces.DMRProcessor
    public void removeListener(DMRProcessor.DMRProcessorListener listener) {
        synchronized (this.m_listeners) {
            this.m_listeners.remove(listener);
        }
    }

    public void fireOnFailEvent(Action action, UpnpResponse response, String message) {
        synchronized (this.m_listeners) {
            for (DMRProcessor.DMRProcessorListener listener : this.m_listeners) {
                listener.onActionFail(action, response, message);
            }
        }
    }

    public void fireUpdatePositionEvent(long current, long max) {
        if (!this.m_isBusy) {
            if ((max > 0 && max <= 5) || (max > 5 && max - current <= 5)) {
                this.mediaNext = true;
            }
            if (max > 0 && current > 0) {
                this.mediaPlay = true;
            }
            synchronized (this.m_listeners) {
                for (DMRProcessor.DMRProcessorListener listener : this.m_listeners) {
                    listener.onUpdatePosition(current, max);
                }
            }
        }
    }

    public void fireOnStopedEvent() {
        if (!this.m_isBusy) {
            synchronized (this.m_listeners) {
                for (DMRProcessor.DMRProcessorListener listener : this.m_listeners) {
                    listener.onStoped();
                }
            }
        }
    }

    public void fireOnPausedEvent() {
        if (!this.m_isBusy) {
            synchronized (this.m_listeners) {
                for (DMRProcessor.DMRProcessorListener listener : this.m_listeners) {
                    listener.onPaused();
                }
            }
        }
    }

    public void fireOnPlayingEvent() {
        if (!this.m_isBusy) {
            synchronized (this.m_listeners) {
                for (DMRProcessor.DMRProcessorListener listener : this.m_listeners) {
                    listener.onPlaying();
                }
            }
        }
    }

    public void fireOnEndTrackEvent() {
        synchronized (this.m_listeners) {
            for (DMRProcessor.DMRProcessorListener listener : this.m_listeners) {
                listener.onCompleted();
            }
        }
    }

    public void fireOnMiddleEndEvent() {
        PlaylistProcessor m_playlistProcessor;
        PlaylistItem currentItem;
        if (!this.m_isBusy && !this.mediaMiddleEnd && (m_playlistProcessor = UpnpProcessorImpl.getSington().getPlaylistProcessor()) != null && (currentItem = m_playlistProcessor.getCurrentItem()) != null) {
            boolean isImage = currentItem.getType() == PlaylistItem.Type.IMAGE_LOCAL || currentItem.getType() == PlaylistItem.Type.IMAGE_REMOTE;
            if (this.mediaPlay || isImage) {
                if (this.imagePlay || !isImage) {
                    this.mediaMiddleEnd = true;
                    synchronized (this.m_listeners) {
                        for (DMRProcessor.DMRProcessorListener listener : this.m_listeners) {
                            listener.onMiddleEndEvent();
                        }
                    }
                }
            }
        }
    }

    private void fireOnErrorEvent(String error) {
        synchronized (this.m_listeners) {
            for (DMRProcessor.DMRProcessorListener listener : this.m_listeners) {
                listener.onErrorEvent(error);
            }
        }
    }

    @Override // com.hisilicon.dlna.dmc.processor.interfaces.DMRProcessor
    public void dispose() {
        stop();
        if (this.m_updateThread != null) {
            this.m_updateThread.stopThread();
        }
        this.isSeeked = true;
        synchronized (this.m_listeners) {
            this.m_listeners.clear();
        }
    }

    @Override // com.hisilicon.dlna.dmc.processor.interfaces.DMRProcessor
    public void seek(String position) {
        this.m_isBusy = true;
        Seek seek = new Seek(this.m_avtransportService, SeekMode.REL_TIME, position) { // from class: com.hisilicon.dlna.dmc.processor.impl.RemoteDMRProcessorImpl.5
            AnonymousClass5(Service $anonymous0, SeekMode $anonymous1, String position2) {
                super($anonymous0, $anonymous1, position2);
            }

            @Override // org.teleal.cling.support.avtransport.callback.Seek, org.teleal.cling.controlpoint.ActionCallback
            public void success(ActionInvocation invocation) throws InterruptedException {
                super.success(invocation);
                RemoteDMRProcessorImpl.this.m_isBusy = false;
                try {
                    Thread.sleep(RemoteDMRProcessorImpl.SEEK_DELAY_INTERVAL);
                    RemoteDMRProcessorImpl.this.m_isBusy = false;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            @Override // org.teleal.cling.controlpoint.ActionCallback
            public void failure(ActionInvocation invocation, UpnpResponse reponse, String defaultMsg) {
                RemoteDMRProcessorImpl.this.m_isBusy = false;
            }
        };
        this.m_controlPoint.execute(seek);
    }

    /* renamed from: com.hisilicon.dlna.dmc.processor.impl.RemoteDMRProcessorImpl$5 */
    class AnonymousClass5 extends Seek {
        AnonymousClass5(Service $anonymous0, SeekMode $anonymous1, String position2) {
            super($anonymous0, $anonymous1, position2);
        }

        @Override // org.teleal.cling.support.avtransport.callback.Seek, org.teleal.cling.controlpoint.ActionCallback
        public void success(ActionInvocation invocation) throws InterruptedException {
            super.success(invocation);
            RemoteDMRProcessorImpl.this.m_isBusy = false;
            try {
                Thread.sleep(RemoteDMRProcessorImpl.SEEK_DELAY_INTERVAL);
                RemoteDMRProcessorImpl.this.m_isBusy = false;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        @Override // org.teleal.cling.controlpoint.ActionCallback
        public void failure(ActionInvocation invocation, UpnpResponse reponse, String defaultMsg) {
            RemoteDMRProcessorImpl.this.m_isBusy = false;
        }
    }

    @Override // com.hisilicon.dlna.dmc.processor.interfaces.DMRProcessor
    public void setVolume(int newVolume) {
        if (this.m_renderingControl != null) {
            this.m_isBusy = true;
            this.m_controlPoint.execute(new SetVolume(this.m_renderingControl, newVolume) { // from class: com.hisilicon.dlna.dmc.processor.impl.RemoteDMRProcessorImpl.6
                AnonymousClass6(Service $anonymous0, long newVolume2) {
                    super($anonymous0, newVolume2);
                }

                @Override // org.teleal.cling.support.renderingcontrol.callback.SetVolume, org.teleal.cling.controlpoint.ActionCallback
                public void success(ActionInvocation invocation) {
                    super.success(invocation);
                    RemoteDMRProcessorImpl.this.m_isBusy = false;
                }

                @Override // org.teleal.cling.controlpoint.ActionCallback
                public void failure(ActionInvocation invocation, UpnpResponse operation, String defaultMsg) {
                    RemoteDMRProcessorImpl.this.fireOnFailEvent(invocation.getAction(), operation, defaultMsg);
                    RemoteDMRProcessorImpl.this.m_isBusy = false;
                }
            });
        }
    }

    /* renamed from: com.hisilicon.dlna.dmc.processor.impl.RemoteDMRProcessorImpl$6 */
    class AnonymousClass6 extends SetVolume {
        AnonymousClass6(Service $anonymous0, long newVolume2) {
            super($anonymous0, newVolume2);
        }

        @Override // org.teleal.cling.support.renderingcontrol.callback.SetVolume, org.teleal.cling.controlpoint.ActionCallback
        public void success(ActionInvocation invocation) {
            super.success(invocation);
            RemoteDMRProcessorImpl.this.m_isBusy = false;
        }

        @Override // org.teleal.cling.controlpoint.ActionCallback
        public void failure(ActionInvocation invocation, UpnpResponse operation, String defaultMsg) {
            RemoteDMRProcessorImpl.this.fireOnFailEvent(invocation.getAction(), operation, defaultMsg);
            RemoteDMRProcessorImpl.this.m_isBusy = false;
        }
    }

    @Override // com.hisilicon.dlna.dmc.processor.interfaces.DMRProcessor
    public int getVolume() {
        return this.m_currentVolume;
    }

    @Override // com.hisilicon.dlna.dmc.processor.interfaces.DMRProcessor
    public int getMaxVolume() {
        return 100;
    }

    @Override // com.hisilicon.dlna.dmc.processor.interfaces.DMRProcessor
    public String getName() {
        return this.m_device != null ? this.m_device.getDetails().getFriendlyName() : "NULL";
    }

    @Override // com.hisilicon.dlna.dmc.processor.interfaces.DMRProcessor
    public String getCurrentTrackURI() {
        return this.m_currentItem == null ? "" : this.m_currentItem.getData() instanceof DIDLObject ? HttpServerUtil.getUrlFrom((DIDLObject) this.m_currentItem.getData()) : this.m_currentItem.getUrl();
    }

    @Override // com.hisilicon.dlna.dmc.processor.interfaces.DMRProcessor
    public void setRunning(boolean running) {
        if (this.m_updateThread != null) {
            this.m_updateThread.stopThread();
            this.m_updateThread = null;
        }
        if (running) {
            this.m_updateThread = new UpdateThread();
            this.m_updateThread.start();
        }
    }

    @Override // com.hisilicon.dlna.dmc.processor.interfaces.DMRProcessor
    public void setURIandPlay(PlaylistItem item) {
        System.out.println("---come into setURIandPlay---");
        this.imagePlay = false;
        this.mediaPlay = false;
        this.mediaNext = false;
        this.mediaMiddleEnd = false;
        this.m_autoNextPending = 4;
        if (item == null) {
            this.m_currentItem = null;
            stop();
            return;
        }
        item.setRemotePlay(true);
        String url = item.getUrl();
        this.m_autoNextPending = 4;
        if (this.m_controlPoint != null && this.m_avtransportService != null) {
            if (this.m_currentItem == null || !this.m_currentItem.equals(item) || this.m_currentState == TransportState.STOPPED) {
                System.out.println("----remot start play----");
                this.m_isBusy = true;
                this.m_currentItem = item;
                System.out.println("The metaData:" + item.getMetaData());
                System.out.println("The url12 is:" + url);
                new Thread(new Runnable() { // from class: com.hisilicon.dlna.dmc.processor.impl.RemoteDMRProcessorImpl.7
                    private final /* synthetic */ PlaylistItem val$item;
                    private final /* synthetic */ String val$url;

                    AnonymousClass7(String url2, PlaylistItem item2) {
                        str = url2;
                        playlistItem = item2;
                    }

                    @Override // java.lang.Runnable
                    public void run() {
                        RemoteDMRProcessorImpl.this.setUriAndPlay(str, playlistItem.getMetaData());
                    }
                }).start();
            }
        }
    }

    /* renamed from: com.hisilicon.dlna.dmc.processor.impl.RemoteDMRProcessorImpl$7 */
    class AnonymousClass7 implements Runnable {
        private final /* synthetic */ PlaylistItem val$item;
        private final /* synthetic */ String val$url;

        AnonymousClass7(String url2, PlaylistItem item2) {
            str = url2;
            playlistItem = item2;
        }

        @Override // java.lang.Runnable
        public void run() {
            RemoteDMRProcessorImpl.this.setUriAndPlay(str, playlistItem.getMetaData());
        }
    }

    public void setUriAndPlay(String url, String metaData) {
        synchronized (this.m_currentItem) {
            Stop stop = new Stop(this.m_avtransportService) { // from class: com.hisilicon.dlna.dmc.processor.impl.RemoteDMRProcessorImpl.8
                private final /* synthetic */ String val$metaData;
                private final /* synthetic */ String val$url;

                /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
                AnonymousClass8(Service $anonymous0, String url2, String metaData2) {
                    super($anonymous0);
                    str = url2;
                    str = metaData2;
                }

                @Override // org.teleal.cling.support.avtransport.callback.Stop, org.teleal.cling.controlpoint.ActionCallback
                public void success(ActionInvocation invocation) {
                    super.success(invocation);
                    RemoteDMRProcessorImpl.this.fireUpdatePositionEvent(0L, 0L);
                    RemoteDMRProcessorImpl.this.setAVTransportURIAndPlay(str, str);
                    RemoteDMRProcessorImpl.this.m_isBusy = false;
                }

                @Override // org.teleal.cling.controlpoint.ActionCallback
                public void failure(ActionInvocation invocation, UpnpResponse response, String defaultMsg) {
                    RemoteDMRProcessorImpl.this.fireUpdatePositionEvent(0L, 0L);
                    RemoteDMRProcessorImpl.this.setAVTransportURIAndPlay(str, str);
                    RemoteDMRProcessorImpl.this.m_isBusy = false;
                }
            };
            this.m_controlPoint.execute(stop);
        }
    }

    /* renamed from: com.hisilicon.dlna.dmc.processor.impl.RemoteDMRProcessorImpl$8 */
    class AnonymousClass8 extends Stop {
        private final /* synthetic */ String val$metaData;
        private final /* synthetic */ String val$url;

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        AnonymousClass8(Service $anonymous0, String url2, String metaData2) {
            super($anonymous0);
            str = url2;
            str = metaData2;
        }

        @Override // org.teleal.cling.support.avtransport.callback.Stop, org.teleal.cling.controlpoint.ActionCallback
        public void success(ActionInvocation invocation) {
            super.success(invocation);
            RemoteDMRProcessorImpl.this.fireUpdatePositionEvent(0L, 0L);
            RemoteDMRProcessorImpl.this.setAVTransportURIAndPlay(str, str);
            RemoteDMRProcessorImpl.this.m_isBusy = false;
        }

        @Override // org.teleal.cling.controlpoint.ActionCallback
        public void failure(ActionInvocation invocation, UpnpResponse response, String defaultMsg) {
            RemoteDMRProcessorImpl.this.fireUpdatePositionEvent(0L, 0L);
            RemoteDMRProcessorImpl.this.setAVTransportURIAndPlay(str, str);
            RemoteDMRProcessorImpl.this.m_isBusy = false;
        }
    }

    public void setAVTransportURIAndPlay(String url, String metaData) {
        this.m_controlPoint.execute(new SetAVTransportURI(this.m_avtransportService, url, metaData) { // from class: com.hisilicon.dlna.dmc.processor.impl.RemoteDMRProcessorImpl.9
            AnonymousClass9(Service $anonymous0, String url2, String metaData2) {
                super($anonymous0, url2, metaData2);
            }

            @Override // org.teleal.cling.support.avtransport.callback.SetAVTransportURI, org.teleal.cling.controlpoint.ActionCallback
            public void success(ActionInvocation invocation) {
                super.success(invocation);
                RemoteDMRProcessorImpl.this.play();
            }

            @Override // org.teleal.cling.controlpoint.ActionCallback
            public void failure(ActionInvocation invocation, UpnpResponse response, String defaultMsg) {
                RemoteDMRProcessorImpl.this.fireUpdatePositionEvent(0L, 0L);
                System.out.println("The failure defaultMsg:" + defaultMsg);
                RemoteDMRProcessorImpl.this.fireOnFailEvent(invocation.getAction(), response, defaultMsg);
                RemoteDMRProcessorImpl.this.m_isBusy = false;
            }
        });
    }

    /* renamed from: com.hisilicon.dlna.dmc.processor.impl.RemoteDMRProcessorImpl$9 */
    class AnonymousClass9 extends SetAVTransportURI {
        AnonymousClass9(Service $anonymous0, String url2, String metaData2) {
            super($anonymous0, url2, metaData2);
        }

        @Override // org.teleal.cling.support.avtransport.callback.SetAVTransportURI, org.teleal.cling.controlpoint.ActionCallback
        public void success(ActionInvocation invocation) {
            super.success(invocation);
            RemoteDMRProcessorImpl.this.play();
        }

        @Override // org.teleal.cling.controlpoint.ActionCallback
        public void failure(ActionInvocation invocation, UpnpResponse response, String defaultMsg) {
            RemoteDMRProcessorImpl.this.fireUpdatePositionEvent(0L, 0L);
            System.out.println("The failure defaultMsg:" + defaultMsg);
            RemoteDMRProcessorImpl.this.fireOnFailEvent(invocation.getAction(), response, defaultMsg);
            RemoteDMRProcessorImpl.this.m_isBusy = false;
        }
    }

    @Override // com.hisilicon.dlna.dmc.processor.interfaces.DMRProcessor
    public PlaylistItem getCurrentItem() {
        return this.m_currentItem;
    }
}
