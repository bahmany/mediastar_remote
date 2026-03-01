package org.teleal.cling.model.state;

import java.lang.reflect.Method;
import org.teleal.common.util.Reflections;

/* loaded from: classes.dex */
public class GetterStateVariableAccessor extends StateVariableAccessor {
    private Method getter;

    public GetterStateVariableAccessor(Method getter) {
        this.getter = getter;
    }

    public Method getGetter() {
        return this.getter;
    }

    @Override // org.teleal.cling.model.state.StateVariableAccessor
    public Class<?> getReturnType() {
        return getGetter().getReturnType();
    }

    @Override // org.teleal.cling.model.state.StateVariableAccessor
    public Object read(Object serviceImpl) throws Exception {
        return Reflections.invoke(getGetter(), serviceImpl, new Object[0]);
    }

    @Override // org.teleal.cling.model.state.StateVariableAccessor
    public String toString() {
        return String.valueOf(super.toString()) + " Method: " + getGetter();
    }
}
