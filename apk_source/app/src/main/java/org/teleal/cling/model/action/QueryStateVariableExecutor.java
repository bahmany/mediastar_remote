package org.teleal.cling.model.action;

import org.cybergarage.upnp.control.Control;
import org.teleal.cling.model.meta.LocalService;
import org.teleal.cling.model.meta.QueryStateVariableAction;
import org.teleal.cling.model.meta.StateVariable;
import org.teleal.cling.model.state.StateVariableAccessor;
import org.teleal.cling.model.types.ErrorCode;

/* loaded from: classes.dex */
public class QueryStateVariableExecutor extends AbstractActionExecutor {
    @Override // org.teleal.cling.model.action.AbstractActionExecutor
    protected void execute(ActionInvocation<LocalService> actionInvocation, Object serviceImpl) throws Exception {
        if (actionInvocation.getAction() instanceof QueryStateVariableAction) {
            if (!((LocalService) actionInvocation.getAction().getService()).isSupportsQueryStateVariables()) {
                actionInvocation.setFailure(new ActionException(ErrorCode.INVALID_ACTION, "This service does not support querying state variables"));
                return;
            } else {
                executeQueryStateVariable(actionInvocation, serviceImpl);
                return;
            }
        }
        throw new IllegalStateException("This class can only execute QueryStateVariableAction's, not: " + actionInvocation.getAction());
    }

    protected void executeQueryStateVariable(ActionInvocation<LocalService> actionInvocation, Object serviceImpl) throws Exception {
        LocalService service = (LocalService) actionInvocation.getAction().getService();
        String stateVariableName = actionInvocation.getInput(Control.VAR_NAME).toString();
        StateVariable stateVariable = service.getStateVariable(stateVariableName);
        if (stateVariable == null) {
            throw new ActionException(ErrorCode.ARGUMENT_VALUE_INVALID, "No state variable found: " + stateVariableName);
        }
        StateVariableAccessor accessor = service.getAccessor(stateVariable.getName());
        if (accessor == null) {
            throw new ActionException(ErrorCode.ARGUMENT_VALUE_INVALID, "No accessor for state variable, can't read state: " + stateVariableName);
        }
        try {
            setOutputArgumentValue(actionInvocation, actionInvocation.getAction().getOutputArgument(Control.RETURN), accessor.read(stateVariable, serviceImpl).toString());
        } catch (Exception ex) {
            throw new ActionException(ErrorCode.ACTION_FAILED, ex.getMessage());
        }
    }
}
