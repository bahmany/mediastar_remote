package org.teleal.cling.binding.staging;

import java.util.List;
import org.teleal.cling.model.meta.StateVariable;
import org.teleal.cling.model.meta.StateVariableAllowedValueRange;
import org.teleal.cling.model.meta.StateVariableEventDetails;
import org.teleal.cling.model.meta.StateVariableTypeDetails;
import org.teleal.cling.model.types.Datatype;

/* loaded from: classes.dex */
public class MutableStateVariable {
    public MutableAllowedValueRange allowedValueRange;
    public List<String> allowedValues;
    public Datatype dataType;
    public String defaultValue;
    public StateVariableEventDetails eventDetails;
    public String name;

    public StateVariable build() {
        return new StateVariable(this.name, new StateVariableTypeDetails(this.dataType, this.defaultValue, (this.allowedValues == null || this.allowedValues.size() == 0) ? null : (String[]) this.allowedValues.toArray(new String[this.allowedValues.size()]), this.allowedValueRange != null ? new StateVariableAllowedValueRange(this.allowedValueRange.minimum.longValue(), this.allowedValueRange.maximum.longValue(), this.allowedValueRange.step.longValue()) : null), this.eventDetails);
    }
}
