package com.hisilicon.multiscreen.protocol.remote;

import com.hisilicon.multiscreen.protocol.utils.VImeStateMachineDriverMessage;
import com.hisilicon.multiscreen.protocol.utils.VImeStatusDefine;

/* loaded from: classes.dex */
public interface IVImeClientStateMachineHandler {
    VImeStatusDefine.VimeStatus getVimeStatus();

    boolean initVimeMachine(VImeStateMachineDriverMessage vImeStateMachineDriverMessage);

    VImeStatusDefine.VimeStatus updateVimeStatus(VImeStateMachineDriverMessage vImeStateMachineDriverMessage);
}
