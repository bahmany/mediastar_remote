package org.teleal.cling.support.lastchange;

import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.List;
import org.teleal.cling.model.types.UnsignedIntegerFourBytes;

/* loaded from: classes.dex */
public class LastChange {
    private final Event event;
    private final LastChangeParser parser;
    private String previousValue;

    public LastChange(String s) {
        throw new UnsupportedOperationException("This constructor is only for service binding detection");
    }

    public LastChange(LastChangeParser parser, Event event) {
        this.parser = parser;
        this.event = event;
    }

    public LastChange(LastChangeParser parser) {
        this(parser, new Event());
    }

    public LastChange(LastChangeParser parser, String xml) throws Exception {
        if (xml != null && xml.length() > 0) {
            this.event = parser.parse(xml);
        } else {
            this.event = new Event();
        }
        this.parser = parser;
    }

    public synchronized void reset() {
        this.previousValue = toString();
        this.event.clear();
    }

    public synchronized void setEventedValue(int instanceID, EventedValue... ev) {
        setEventedValue(new UnsignedIntegerFourBytes(instanceID), ev);
    }

    public synchronized void setEventedValue(UnsignedIntegerFourBytes instanceID, EventedValue... ev) {
        for (EventedValue eventedValue : ev) {
            if (eventedValue != null) {
                this.event.setEventedValue(instanceID, eventedValue);
            }
        }
    }

    public synchronized UnsignedIntegerFourBytes[] getInstanceIDs() {
        List<UnsignedIntegerFourBytes> list;
        list = new ArrayList<>();
        for (InstanceID instanceID : this.event.getInstanceIDs()) {
            list.add(instanceID.getId());
        }
        return (UnsignedIntegerFourBytes[]) list.toArray(new UnsignedIntegerFourBytes[list.size()]);
    }

    synchronized EventedValue[] getEventedValues(UnsignedIntegerFourBytes instanceID) {
        InstanceID inst;
        inst = this.event.getInstanceID(instanceID);
        return inst != null ? (EventedValue[]) inst.getValues().toArray(new EventedValue[inst.getValues().size()]) : null;
    }

    public synchronized <EV extends EventedValue> EV getEventedValue(int i, Class<EV> cls) {
        return (EV) getEventedValue(new UnsignedIntegerFourBytes(i), cls);
    }

    public synchronized <EV extends EventedValue> EV getEventedValue(UnsignedIntegerFourBytes unsignedIntegerFourBytes, Class<EV> cls) {
        return (EV) this.event.getEventedValue(unsignedIntegerFourBytes, cls);
    }

    public synchronized void fire(PropertyChangeSupport propertyChangeSupport) {
        String lastChanges = toString();
        if (lastChanges != null && lastChanges.length() > 0) {
            propertyChangeSupport.firePropertyChange("LastChange", this.previousValue, lastChanges);
            reset();
        }
    }

    public synchronized String toString() {
        String strGenerate;
        if (this.event.hasChanges()) {
            try {
                strGenerate = this.parser.generate(this.event);
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        } else {
            strGenerate = "";
        }
        return strGenerate;
    }
}
