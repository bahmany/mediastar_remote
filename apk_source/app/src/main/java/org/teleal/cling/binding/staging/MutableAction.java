package org.teleal.cling.binding.staging;

import java.util.ArrayList;
import java.util.List;
import org.teleal.cling.model.meta.Action;
import org.teleal.cling.model.meta.ActionArgument;

/* loaded from: classes.dex */
public class MutableAction {
    public List<MutableActionArgument> arguments = new ArrayList();
    public String name;

    public Action build() {
        return new Action(this.name, createActionArgumennts());
    }

    public ActionArgument[] createActionArgumennts() {
        ActionArgument[] array = new ActionArgument[this.arguments.size()];
        int i = 0;
        for (MutableActionArgument argument : this.arguments) {
            array[i] = argument.build();
            i++;
        }
        return array;
    }
}
