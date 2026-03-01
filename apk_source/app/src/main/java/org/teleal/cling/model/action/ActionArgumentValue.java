package org.teleal.cling.model.action;

import org.teleal.cling.model.VariableValue;
import org.teleal.cling.model.meta.ActionArgument;
import org.teleal.cling.model.meta.Service;
import org.teleal.cling.model.types.Datatype;
import org.teleal.cling.model.types.InvalidValueException;

/* loaded from: classes.dex */
public class ActionArgumentValue<S extends Service> extends VariableValue {
    private final ActionArgument<S> argument;

    /* JADX WARN: Illegal instructions before constructor call */
    public ActionArgumentValue(ActionArgument<S> argument, Object value) throws InvalidValueException {
        Datatype datatype = argument.getDatatype();
        if (value != null && value.getClass().isEnum()) {
            value = value.toString();
        }
        super(datatype, value);
        this.argument = argument;
    }

    public ActionArgument<S> getArgument() {
        return this.argument;
    }
}
