package com.hisilicon.multiscreen.protocol.remote;

import com.hisilicon.multiscreen.protocol.utils.LogTool;
import com.hisilicon.multiscreen.protocol.utils.VImeStateMachineDriverMessage;
import com.hisilicon.multiscreen.protocol.utils.VImeStatusDefine;

/* loaded from: classes.dex */
public class VImeClientStateMachine {
    private static VImeClientStateMachine mVimeClientStateMachine = null;
    private VImeStatusDefine.VimeStatus mVimeStatusValue = VImeStatusDefine.VimeStatus.DEINIT_STATUS_VIME_CLIENT;
    private IVImeClientStateMachineHandler mVimeStatusMachineHandler = new IVImeClientStateMachineHandler() { // from class: com.hisilicon.multiscreen.protocol.remote.VImeClientStateMachine.1
        private static /* synthetic */ int[] $SWITCH_TABLE$com$hisilicon$multiscreen$protocol$utils$VImeStatusDefine$VimeStatus;

        static /* synthetic */ int[] $SWITCH_TABLE$com$hisilicon$multiscreen$protocol$utils$VImeStatusDefine$VimeStatus() {
            int[] iArr = $SWITCH_TABLE$com$hisilicon$multiscreen$protocol$utils$VImeStatusDefine$VimeStatus;
            if (iArr == null) {
                iArr = new int[VImeStatusDefine.VimeStatus.valuesCustom().length];
                try {
                    iArr[VImeStatusDefine.VimeStatus.DEINIT_STATUS_VIME_CLIENT.ordinal()] = 5;
                } catch (NoSuchFieldError e) {
                }
                try {
                    iArr[VImeStatusDefine.VimeStatus.INIT_STATUS_VIME_CLIENT.ordinal()] = 1;
                } catch (NoSuchFieldError e2) {
                }
                try {
                    iArr[VImeStatusDefine.VimeStatus.INPUT_STATUS_VIME_CLIENT.ordinal()] = 4;
                } catch (NoSuchFieldError e3) {
                }
                try {
                    iArr[VImeStatusDefine.VimeStatus.READY_STATUS_VIME_CLIENT.ordinal()] = 3;
                } catch (NoSuchFieldError e4) {
                }
                try {
                    iArr[VImeStatusDefine.VimeStatus.SETUP_STATUS_VIME_CLIENT.ordinal()] = 2;
                } catch (NoSuchFieldError e5) {
                }
                try {
                    iArr[VImeStatusDefine.VimeStatus.VIME_SERVER_STATUS_DEINIT.ordinal()] = 9;
                } catch (NoSuchFieldError e6) {
                }
                try {
                    iArr[VImeStatusDefine.VimeStatus.VIME_SERVER_STATUS_INIT.ordinal()] = 6;
                } catch (NoSuchFieldError e7) {
                }
                try {
                    iArr[VImeStatusDefine.VimeStatus.VIME_SERVER_STATUS_INPUT.ordinal()] = 8;
                } catch (NoSuchFieldError e8) {
                }
                try {
                    iArr[VImeStatusDefine.VimeStatus.VIME_SERVER_STATUS_READY.ordinal()] = 7;
                } catch (NoSuchFieldError e9) {
                }
                $SWITCH_TABLE$com$hisilicon$multiscreen$protocol$utils$VImeStatusDefine$VimeStatus = iArr;
            }
            return iArr;
        }

        @Override // com.hisilicon.multiscreen.protocol.remote.IVImeClientStateMachineHandler
        public boolean initVimeMachine(VImeStateMachineDriverMessage driver) {
            if (VImeClientStateMachine.this.mVimeStatusValue != VImeStatusDefine.VimeStatus.DEINIT_STATUS_VIME_CLIENT) {
                return false;
            }
            VImeClientStateMachine.this.mVimeStatusValue = VImeStatusDefine.VimeStatus.INIT_STATUS_VIME_CLIENT;
            return true;
        }

        @Override // com.hisilicon.multiscreen.protocol.remote.IVImeClientStateMachineHandler
        public synchronized VImeStatusDefine.VimeStatus updateVimeStatus(VImeStateMachineDriverMessage driver) {
            VImeStatusDefine.VimeStatus vimeStatus;
            if (driver == null) {
                vimeStatus = VImeClientStateMachine.this.mVimeStatusValue;
            } else {
                switch ($SWITCH_TABLE$com$hisilicon$multiscreen$protocol$utils$VImeStatusDefine$VimeStatus()[VImeClientStateMachine.this.mVimeStatusValue.ordinal()]) {
                    case 1:
                        if (driver == VImeStateMachineDriverMessage.CHECK_OK) {
                            VImeClientStateMachine.this.mVimeStatusValue = VImeStatusDefine.VimeStatus.READY_STATUS_VIME_CLIENT;
                            break;
                        } else if (driver == VImeStateMachineDriverMessage.CHECK_FAIL) {
                            VImeClientStateMachine.this.mVimeStatusValue = VImeStatusDefine.VimeStatus.SETUP_STATUS_VIME_CLIENT;
                            break;
                        } else if (driver == VImeStateMachineDriverMessage.CLOSE_VIME) {
                            VImeClientStateMachine.this.mVimeStatusValue = VImeStatusDefine.VimeStatus.DEINIT_STATUS_VIME_CLIENT;
                            LogTool.e("To close vime when it is in init status.");
                            break;
                        } else {
                            LogTool.e("state machine: INIT_STATUS_VIME_CLIENT, being driven by " + driver.getName());
                            break;
                        }
                    case 2:
                        if (driver == VImeStateMachineDriverMessage.CHECK_OK) {
                            VImeClientStateMachine.this.mVimeStatusValue = VImeStatusDefine.VimeStatus.READY_STATUS_VIME_CLIENT;
                            break;
                        } else if (driver == VImeStateMachineDriverMessage.CHECK_FAIL) {
                            VImeClientStateMachine.this.mVimeStatusValue = VImeStatusDefine.VimeStatus.DEINIT_STATUS_VIME_CLIENT;
                            break;
                        } else if (driver == VImeStateMachineDriverMessage.CLOSE_VIME) {
                            VImeClientStateMachine.this.mVimeStatusValue = VImeStatusDefine.VimeStatus.DEINIT_STATUS_VIME_CLIENT;
                            break;
                        } else {
                            LogTool.e("state machine: SETUP_STATUS_VIME_CLIENT, being driven by " + driver.getName());
                            break;
                        }
                    case 3:
                        if (driver == VImeStateMachineDriverMessage.CALL_INPUT) {
                            VImeClientStateMachine.this.mVimeStatusValue = VImeStatusDefine.VimeStatus.INPUT_STATUS_VIME_CLIENT;
                            break;
                        } else if (driver == VImeStateMachineDriverMessage.CHECK_FAIL) {
                            VImeClientStateMachine.this.mVimeStatusValue = VImeStatusDefine.VimeStatus.SETUP_STATUS_VIME_CLIENT;
                            break;
                        } else if (driver == VImeStateMachineDriverMessage.CLOSE_VIME) {
                            VImeClientStateMachine.this.mVimeStatusValue = VImeStatusDefine.VimeStatus.DEINIT_STATUS_VIME_CLIENT;
                            break;
                        } else if (driver != VImeStateMachineDriverMessage.CHECK_OK) {
                            LogTool.w("state machine: READY_STATUS_VIME_CLIENT, being driven by " + driver.getName());
                            break;
                        }
                        break;
                    case 4:
                        if (driver == VImeStateMachineDriverMessage.END_INPUT) {
                            VImeClientStateMachine.this.mVimeStatusValue = VImeStatusDefine.VimeStatus.READY_STATUS_VIME_CLIENT;
                            break;
                        } else if (driver == VImeStateMachineDriverMessage.CHECK_FAIL) {
                            VImeClientStateMachine.this.mVimeStatusValue = VImeStatusDefine.VimeStatus.SETUP_STATUS_VIME_CLIENT;
                            break;
                        } else if (driver == VImeStateMachineDriverMessage.CLOSE_VIME) {
                            VImeClientStateMachine.this.mVimeStatusValue = VImeStatusDefine.VimeStatus.DEINIT_STATUS_VIME_CLIENT;
                            LogTool.e("To close VIME when it is in input status.");
                            break;
                        } else if (driver != VImeStateMachineDriverMessage.CHECK_OK) {
                            LogTool.e("state machine: INPUT_STATUS_VIME_CLIENT, being driven by " + driver.getName());
                            break;
                        }
                        break;
                    case 5:
                        if (driver == VImeStateMachineDriverMessage.OPEN_VIME) {
                            VImeClientStateMachine.this.mVimeStatusValue = VImeStatusDefine.VimeStatus.INIT_STATUS_VIME_CLIENT;
                            break;
                        } else {
                            LogTool.e("state machine: DEINIT_STATUS_VIME_CLIENT, being driven by " + driver.getName());
                            break;
                        }
                }
                vimeStatus = VImeClientStateMachine.this.mVimeStatusValue;
            }
            return vimeStatus;
        }

        @Override // com.hisilicon.multiscreen.protocol.remote.IVImeClientStateMachineHandler
        public synchronized VImeStatusDefine.VimeStatus getVimeStatus() {
            return VImeClientStateMachine.this.mVimeStatusValue;
        }
    };

    public static VImeClientStateMachine getInstance() {
        if (mVimeClientStateMachine == null) {
            mVimeClientStateMachine = new VImeClientStateMachine();
        }
        return mVimeClientStateMachine;
    }

    public boolean initVimeClient(VImeStateMachineDriverMessage driver) {
        return this.mVimeStatusMachineHandler.initVimeMachine(driver);
    }

    public void deInitVimeClient() {
        this.mVimeStatusValue = VImeStatusDefine.VimeStatus.DEINIT_STATUS_VIME_CLIENT;
    }

    public VImeStatusDefine.VimeStatus updateStatus(VImeStateMachineDriverMessage driver) {
        return this.mVimeStatusMachineHandler.updateVimeStatus(driver);
    }

    public int getStatusIndex() {
        return this.mVimeStatusValue.getIndex();
    }

    public VImeStatusDefine.VimeStatus getStatus() {
        return this.mVimeStatusValue;
    }

    public void setVimeStatusMachineHandler(IVImeClientStateMachineHandler vimeStatusMachineHandler) {
        this.mVimeStatusMachineHandler = vimeStatusMachineHandler;
    }
}
