package org.teleal.cling.model.action;

import org.teleal.cling.model.meta.LocalService;

/* loaded from: classes.dex */
public interface ActionExecutor {
    void execute(ActionInvocation<LocalService> actionInvocation);
}
