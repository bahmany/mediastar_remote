package org.teleal.cling.support.renderingcontrol.lastchange;

import java.util.Map;
import org.teleal.cling.model.types.BooleanDatatype;
import org.teleal.cling.model.types.Datatype;
import org.teleal.cling.model.types.InvalidValueException;
import org.teleal.cling.support.lastchange.EventedValue;
import org.teleal.cling.support.model.Channel;
import org.teleal.cling.support.shared.AbstractMap;

/* loaded from: classes.dex */
public class EventedValueChannelMute extends EventedValue<ChannelMute> {
    @Override // org.teleal.cling.support.lastchange.EventedValue
    protected /* bridge */ /* synthetic */ ChannelMute valueOf(Map.Entry[] entryArr) throws InvalidValueException {
        return valueOf2((Map.Entry<String, String>[]) entryArr);
    }

    public EventedValueChannelMute(ChannelMute value) {
        super(value);
    }

    public EventedValueChannelMute(Map.Entry<String, String>[] entryArr) {
        super(entryArr);
    }

    @Override // org.teleal.cling.support.lastchange.EventedValue
    /* renamed from: valueOf */
    protected ChannelMute valueOf2(Map.Entry<String, String>[] entryArr) throws InvalidValueException {
        Channel channel = null;
        Boolean mute = null;
        for (Map.Entry<String, String> attribute : entryArr) {
            if (attribute.getKey().equals("channel")) {
                channel = Channel.valueOf(attribute.getValue());
            }
            if (attribute.getKey().equals("val")) {
                mute = new BooleanDatatype().valueOf(attribute.getValue());
            }
        }
        if (channel == null || mute == null) {
            return null;
        }
        return new ChannelMute(channel, mute);
    }

    @Override // org.teleal.cling.support.lastchange.EventedValue
    public Map.Entry<String, String>[] getAttributes() {
        return new Map.Entry[]{new AbstractMap.SimpleEntry("val", new BooleanDatatype().getString(getValue().getMute())), new AbstractMap.SimpleEntry("channel", getValue().getChannel().name())};
    }

    @Override // org.teleal.cling.support.lastchange.EventedValue
    public String toString() {
        return getValue().toString();
    }

    @Override // org.teleal.cling.support.lastchange.EventedValue
    protected Datatype getDatatype() {
        return null;
    }
}
