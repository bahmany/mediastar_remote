package org.teleal.cling.model;

/* loaded from: classes.dex */
public interface Command<T> {
    void execute(ServiceManager<T> serviceManager) throws Exception;
}
