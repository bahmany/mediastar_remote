package com.hisilicon.multiscreen.protocol.remote;

import com.hisilicon.multiscreen.protocol.message.Action;

/* loaded from: classes.dex */
public interface IVImeActivityHandler {
    void callInput(Action action);

    void closeVimeSwitch();

    void endInputBySelf();

    void endInputByServer();

    boolean isInputActivityOnTop();

    void openVimeSwitch();
}
