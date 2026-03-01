package org.teleal.cling.model.meta;

import java.util.Collections;
import java.util.List;
import org.cybergarage.upnp.control.Control;
import org.teleal.cling.model.ValidationError;
import org.teleal.cling.model.meta.ActionArgument;
import org.teleal.cling.model.meta.Service;

/* loaded from: classes.dex */
public class QueryStateVariableAction<S extends Service> extends Action<S> {
    public static final String ACTION_NAME = "QueryStateVariable";
    public static final String VIRTUAL_STATEVARIABLE_INPUT = "VirtualQueryActionInput";
    public static final String VIRTUAL_STATEVARIABLE_OUTPUT = "VirtualQueryActionOutput";

    public QueryStateVariableAction() {
        this(null);
    }

    public QueryStateVariableAction(S service) {
        super("QueryStateVariable", new ActionArgument[]{new ActionArgument(Control.VAR_NAME, VIRTUAL_STATEVARIABLE_INPUT, ActionArgument.Direction.IN), new ActionArgument(Control.RETURN, VIRTUAL_STATEVARIABLE_OUTPUT, ActionArgument.Direction.OUT)});
        setService(service);
    }

    @Override // org.teleal.cling.model.meta.Action
    public String getName() {
        return "QueryStateVariable";
    }

    @Override // org.teleal.cling.model.meta.Action, org.teleal.cling.model.Validatable
    public List<ValidationError> validate() {
        return Collections.EMPTY_LIST;
    }
}
