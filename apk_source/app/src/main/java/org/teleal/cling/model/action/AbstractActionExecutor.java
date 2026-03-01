package org.teleal.cling.model.action;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.teleal.cling.model.Command;
import org.teleal.cling.model.ServiceManager;
import org.teleal.cling.model.meta.Action;
import org.teleal.cling.model.meta.ActionArgument;
import org.teleal.cling.model.meta.LocalService;
import org.teleal.cling.model.state.StateVariableAccessor;
import org.teleal.cling.model.types.ErrorCode;
import org.teleal.cling.model.types.InvalidValueException;
import org.teleal.common.util.Exceptions;

/* loaded from: classes.dex */
public abstract class AbstractActionExecutor implements ActionExecutor {
    private static Logger log = Logger.getLogger(AbstractActionExecutor.class.getName());
    protected Map<ActionArgument<LocalService>, StateVariableAccessor> outputArgumentAccessors;

    protected abstract void execute(ActionInvocation<LocalService> actionInvocation, Object obj) throws Exception;

    protected AbstractActionExecutor() {
        this.outputArgumentAccessors = new HashMap();
    }

    protected AbstractActionExecutor(Map<ActionArgument<LocalService>, StateVariableAccessor> outputArgumentAccessors) {
        this.outputArgumentAccessors = new HashMap();
        this.outputArgumentAccessors = outputArgumentAccessors;
    }

    public Map<ActionArgument<LocalService>, StateVariableAccessor> getOutputArgumentAccessors() {
        return this.outputArgumentAccessors;
    }

    @Override // org.teleal.cling.model.action.ActionExecutor
    public void execute(final ActionInvocation<LocalService> actionInvocation) {
        log.fine("Invoking on local service: " + actionInvocation);
        LocalService service = (LocalService) actionInvocation.getAction().getService();
        try {
            if (service.getManager() == null) {
                throw new IllegalStateException("Service has no implementation factory, can't get service instance");
            }
            service.getManager().execute(new Command() { // from class: org.teleal.cling.model.action.AbstractActionExecutor.1
                @Override // org.teleal.cling.model.Command
                public void execute(ServiceManager serviceManager) throws Exception {
                    AbstractActionExecutor.this.execute(actionInvocation, serviceManager.getImplementation());
                }

                public String toString() {
                    return "Action invocation: " + actionInvocation.getAction();
                }
            });
        } catch (ActionException ex) {
            log.fine("ActionException thrown by service method, wrapping in invocation and returning: " + ex);
            log.log(Level.FINE, "Exception root cause: ", Exceptions.unwrap(ex));
            actionInvocation.setFailure(ex);
        } catch (Exception ex2) {
            log.fine("Exception thrown by execution, wrapping in ActionException and returning: " + ex2);
            log.log(Level.FINE, "Exception root cause: ", Exceptions.unwrap(ex2));
            actionInvocation.setFailure(new ActionException(ErrorCode.ACTION_FAILED, "Action method invocation failed: " + (ex2.getMessage() != null ? ex2.getMessage() : ex2.toString()), ex2));
        }
    }

    protected Object readOutputArgumentValues(Action<LocalService> action, Object instance) throws Exception {
        Object[] results = new Object[action.getOutputArguments().length];
        log.fine("Attempting to retrieve output argument values using accessor: " + results.length);
        ActionArgument[] outputArguments = action.getOutputArguments();
        int length = outputArguments.length;
        int i = 0;
        int i2 = 0;
        while (i < length) {
            ActionArgument outputArgument = outputArguments[i];
            log.finer("Calling acccessor method for: " + outputArgument);
            StateVariableAccessor accessor = getOutputArgumentAccessors().get(outputArgument);
            if (accessor != null) {
                log.fine("Calling accessor to read output argument value: " + accessor);
                results[i2] = accessor.read(instance);
                i++;
                i2++;
            } else {
                throw new IllegalStateException("No accessor bound for: " + outputArgument);
            }
        }
        if (results.length == 1) {
            return results[0];
        }
        if (results.length <= 0) {
            return null;
        }
        return results;
    }

    protected void setOutputArgumentValue(ActionInvocation<LocalService> actionInvocation, ActionArgument<LocalService> argument, Object result) throws ActionException {
        LocalService service = (LocalService) actionInvocation.getAction().getService();
        if (result != null) {
            try {
                if (service.isStringConvertibleType(result)) {
                    log.fine("Result of invocation matches convertible type, setting toString() single output argument value");
                    actionInvocation.setOutput(new ActionArgumentValue<>(argument, result.toString()));
                } else {
                    log.fine("Result of invocation is Object, setting single output argument value");
                    actionInvocation.setOutput(new ActionArgumentValue<>(argument, result));
                }
                return;
            } catch (InvalidValueException ex) {
                throw new ActionException(ErrorCode.ARGUMENT_VALUE_INVALID, "Wrong type or invalid value for '" + argument.getName() + "': " + ex.getMessage(), ex);
            }
        }
        log.fine("Result of invocation is null, not setting any output argument value(s)");
    }
}
