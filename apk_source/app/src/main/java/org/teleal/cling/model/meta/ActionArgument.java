package org.teleal.cling.model.meta;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import org.teleal.cling.model.ModelUtil;
import org.teleal.cling.model.Validatable;
import org.teleal.cling.model.ValidationError;
import org.teleal.cling.model.meta.Service;
import org.teleal.cling.model.types.Datatype;

/* loaded from: classes.dex */
public class ActionArgument<S extends Service> implements Validatable {
    private static final Logger log = Logger.getLogger(ActionArgument.class.getName());
    private Action<S> action;
    private final String[] aliases;
    private final Direction direction;
    private final String name;
    private final String relatedStateVariableName;
    private final boolean returnValue;

    public enum Direction {
        IN,
        OUT;

        /* renamed from: values, reason: to resolve conflict with enum method */
        public static Direction[] valuesCustom() {
            Direction[] directionArrValuesCustom = values();
            int length = directionArrValuesCustom.length;
            Direction[] directionArr = new Direction[length];
            System.arraycopy(directionArrValuesCustom, 0, directionArr, 0, length);
            return directionArr;
        }
    }

    public ActionArgument(String name, String relatedStateVariableName, Direction direction) {
        this(name, new String[0], relatedStateVariableName, direction, false);
    }

    public ActionArgument(String name, String[] aliases, String relatedStateVariableName, Direction direction) {
        this(name, aliases, relatedStateVariableName, direction, false);
    }

    public ActionArgument(String name, String relatedStateVariableName, Direction direction, boolean returnValue) {
        this(name, new String[0], relatedStateVariableName, direction, returnValue);
    }

    public ActionArgument(String name, String[] aliases, String relatedStateVariableName, Direction direction, boolean returnValue) {
        this.name = name;
        this.aliases = aliases;
        this.relatedStateVariableName = relatedStateVariableName;
        this.direction = direction;
        this.returnValue = returnValue;
    }

    public String getName() {
        return this.name;
    }

    public String[] getAliases() {
        return this.aliases;
    }

    public boolean isNameOrAlias(String name) {
        if (getName().equals(name)) {
            return true;
        }
        for (String alias : this.aliases) {
            if (alias.equals(name)) {
                return true;
            }
        }
        return false;
    }

    public String getRelatedStateVariableName() {
        return this.relatedStateVariableName;
    }

    public Direction getDirection() {
        return this.direction;
    }

    public boolean isReturnValue() {
        return this.returnValue;
    }

    public Action<S> getAction() {
        return this.action;
    }

    void setAction(Action<S> action) {
        if (this.action != null) {
            throw new IllegalStateException("Final value has been set already, model is immutable");
        }
        this.action = action;
    }

    public Datatype getDatatype() {
        return getAction().getService().getDatatype(this);
    }

    @Override // org.teleal.cling.model.Validatable
    public List<ValidationError> validate() {
        List<ValidationError> errors = new ArrayList<>();
        if (getName() == null || getName().length() == 0) {
            errors.add(new ValidationError(getClass(), "name", "Argument without name of: " + getAction()));
        } else if (!ModelUtil.isValidUDAName(getName())) {
            log.warning("UPnP specification violation of: " + getAction().getService().getDevice());
            log.warning("Invalid argument name: " + this);
        } else if (getName().length() > 32) {
            log.warning("UPnP specification violation of: " + getAction().getService().getDevice());
            log.warning("Argument name should be less than 32 characters: " + this);
        }
        if (getDirection() == null) {
            errors.add(new ValidationError(getClass(), "direction", "Argument '" + getName() + "' requires a direction, either IN or OUT"));
        }
        if (isReturnValue() && getDirection() != Direction.OUT) {
            errors.add(new ValidationError(getClass(), "direction", "Return value argument '" + getName() + "' must be direction OUT"));
        }
        return errors;
    }

    public ActionArgument<S> deepCopy() {
        return new ActionArgument<>(getName(), getAliases(), getRelatedStateVariableName(), getDirection(), isReturnValue());
    }

    public String toString() {
        return "(" + getClass().getSimpleName() + ", " + getDirection() + ") " + getName();
    }
}
