package org.teleal.cling.model.state;

import org.teleal.cling.model.Command;
import org.teleal.cling.model.ServiceManager;
import org.teleal.cling.model.meta.LocalService;
import org.teleal.cling.model.meta.StateVariable;

/* loaded from: classes.dex */
public abstract class StateVariableAccessor {
    public abstract Class<?> getReturnType();

    public abstract Object read(Object obj) throws Exception;

    /* renamed from: org.teleal.cling.model.state.StateVariableAccessor$1AccessCommand, reason: invalid class name */
    class C1AccessCommand implements Command {
        Object result;
        private final /* synthetic */ Object val$serviceImpl;
        private final /* synthetic */ StateVariable val$stateVariable;

        C1AccessCommand(Object obj, StateVariable stateVariable) {
            this.val$serviceImpl = obj;
            this.val$stateVariable = stateVariable;
        }

        @Override // org.teleal.cling.model.Command
        public void execute(ServiceManager serviceManager) throws Exception {
            this.result = StateVariableAccessor.this.read(this.val$serviceImpl);
            if (((LocalService) this.val$stateVariable.getService()).isStringConvertibleType(this.result)) {
                this.result = this.result.toString();
            }
        }
    }

    public StateVariableValue read(StateVariable<LocalService> stateVariable, Object serviceImpl) throws Exception {
        C1AccessCommand cmd = new C1AccessCommand(serviceImpl, stateVariable);
        ((LocalService) stateVariable.getService()).getManager().execute(cmd);
        return new StateVariableValue(stateVariable, cmd.result);
    }

    public String toString() {
        return "(" + getClass().getSimpleName() + ")";
    }
}
