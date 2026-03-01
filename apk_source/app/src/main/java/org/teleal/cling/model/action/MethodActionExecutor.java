package org.teleal.cling.model.action;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.logging.Logger;
import org.teleal.cling.model.VariableValue;
import org.teleal.cling.model.meta.ActionArgument;
import org.teleal.cling.model.meta.LocalService;
import org.teleal.cling.model.state.StateVariableAccessor;
import org.teleal.cling.model.types.ErrorCode;
import org.teleal.common.util.Reflections;

/* loaded from: classes.dex */
public class MethodActionExecutor extends AbstractActionExecutor {
    private static Logger log = Logger.getLogger(MethodActionExecutor.class.getName());
    protected Method method;

    public MethodActionExecutor(Method method) {
        this.method = method;
    }

    public MethodActionExecutor(Map<ActionArgument<LocalService>, StateVariableAccessor> outputArgumentAccessors, Method method) {
        super(outputArgumentAccessors);
        this.method = method;
    }

    public Method getMethod() {
        return this.method;
    }

    @Override // org.teleal.cling.model.action.AbstractActionExecutor
    protected void execute(ActionInvocation<LocalService> actionInvocation, Object serviceImpl) throws Exception {
        Object result;
        Object[] inputArgumentValues = createInputArgumentValues(actionInvocation, this.method);
        if (!actionInvocation.getAction().hasOutputArguments()) {
            log.fine("Calling local service method with no output arguments: " + this.method);
            Reflections.invoke(this.method, serviceImpl, inputArgumentValues);
            return;
        }
        boolean isVoid = this.method.getReturnType().equals(Void.TYPE);
        log.fine("Calling local service method with output arguments: " + this.method);
        boolean isArrayResultProcessed = true;
        if (isVoid) {
            log.fine("Action method is void, calling declared accessors(s) on service instance to retrieve ouput argument(s)");
            Reflections.invoke(this.method, serviceImpl, inputArgumentValues);
            result = readOutputArgumentValues(actionInvocation.getAction(), serviceImpl);
        } else if (isUseOutputArgumentAccessors(actionInvocation)) {
            log.fine("Action method is not void, calling declared accessor(s) on returned instance to retrieve ouput argument(s)");
            Object returnedInstance = Reflections.invoke(this.method, serviceImpl, inputArgumentValues);
            result = readOutputArgumentValues(actionInvocation.getAction(), returnedInstance);
        } else {
            log.fine("Action method is not void, using returned value as (single) output argument");
            result = Reflections.invoke(this.method, serviceImpl, inputArgumentValues);
            isArrayResultProcessed = false;
        }
        ActionArgument[] outputArgs = actionInvocation.getAction().getOutputArguments();
        if (isArrayResultProcessed && (result instanceof Object[])) {
            Object[] results = (Object[]) result;
            log.fine("Accessors returned Object[], setting output argument values: " + results.length);
            for (int i = 0; i < outputArgs.length; i++) {
                setOutputArgumentValue(actionInvocation, outputArgs[i], results[i]);
            }
            return;
        }
        if (outputArgs.length == 1) {
            setOutputArgumentValue(actionInvocation, outputArgs[0], result);
            return;
        }
        throw new ActionException(ErrorCode.ACTION_FAILED, "Method return does not match required number of output arguments: " + outputArgs.length);
    }

    protected boolean isUseOutputArgumentAccessors(ActionInvocation<LocalService> actionInvocation) {
        for (ActionArgument argument : actionInvocation.getAction().getOutputArguments()) {
            if (getOutputArgumentAccessors().get(argument) != null) {
                return true;
            }
        }
        return false;
    }

    protected Object[] createInputArgumentValues(ActionInvocation<LocalService> actionInvocation, Method method) throws IllegalAccessException, NoSuchMethodException, InstantiationException, SecurityException, ActionException, IllegalArgumentException, InvocationTargetException {
        int i;
        LocalService service = (LocalService) actionInvocation.getAction().getService();
        Object[] values = new Object[actionInvocation.getAction().getInputArguments().length];
        ActionArgument<LocalService>[] inputArguments = actionInvocation.getAction().getInputArguments();
        int length = inputArguments.length;
        int i2 = 0;
        int i3 = 0;
        while (i2 < length) {
            ActionArgument<LocalService> argument = inputArguments[i2];
            Class methodParameterType = method.getParameterTypes()[i3];
            VariableValue input = actionInvocation.getInput(argument);
            if (methodParameterType.isPrimitive() && (input == null || input.toString().length() == 0)) {
                throw new ActionException(ErrorCode.ARGUMENT_VALUE_INVALID, "Primitive action method argument '" + argument.getName() + "' requires input value, can't be null or empty string");
            }
            if (input == null) {
                i = i3 + 1;
                values[i3] = null;
            } else {
                String inputCallValueString = input.toString();
                if (inputCallValueString.length() > 0 && service.isStringConvertibleType(methodParameterType) && !methodParameterType.isEnum()) {
                    try {
                        Constructor<String> ctor = methodParameterType.getConstructor(String.class);
                        log.finer("Creating new input argument value instance with String.class constructor of type: " + methodParameterType);
                        Object o = ctor.newInstance(inputCallValueString);
                        i = i3 + 1;
                        try {
                            values[i3] = o;
                        } catch (Exception e) {
                            ex = e;
                            ex.printStackTrace(System.err);
                            throw new ActionException(ErrorCode.ARGUMENT_VALUE_INVALID, "Can't convert input argment string to desired type of '" + argument.getName() + "': " + ex);
                        }
                    } catch (Exception e2) {
                        ex = e2;
                    }
                } else {
                    i = i3 + 1;
                    values[i3] = input.getValue();
                }
            }
            i2++;
            i3 = i;
        }
        return values;
    }
}
