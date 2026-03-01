package org.teleal.cling.support.renderingcontrol.lastchange;

import java.util.Map;
import org.teleal.cling.model.types.Datatype;
import org.teleal.cling.model.types.InvalidValueException;
import org.teleal.cling.model.types.UnsignedIntegerTwoBytes;
import org.teleal.cling.model.types.UnsignedIntegerTwoBytesDatatype;
import org.teleal.cling.support.lastchange.EventedValue;
import org.teleal.cling.support.model.Channel;
import org.teleal.cling.support.shared.AbstractMap;

/* loaded from: classes.dex */
public class EventedValueChannelVolume extends EventedValue<ChannelVolume> {
    @Override // org.teleal.cling.support.lastchange.EventedValue
    protected /* bridge */ /* synthetic */ ChannelVolume valueOf(Map.Entry[] entryArr) throws InvalidValueException {
        return valueOf2((Map.Entry<String, String>[]) entryArr);
    }

    public EventedValueChannelVolume(ChannelVolume value) {
        super(value);
    }

    public EventedValueChannelVolume(Map.Entry<String, String>[] entryArr) {
        super(entryArr);
    }

    @Override // org.teleal.cling.support.lastchange.EventedValue
    /* renamed from: valueOf */
    protected ChannelVolume valueOf2(Map.Entry<String, String>[] entryArr) throws InvalidValueException {
        Channel channel = null;
        Integer volume = null;
        for (Map.Entry<String, String> attribute : entryArr) {
            if (attribute.getKey().equals("channel")) {
                channel = Channel.valueOf(attribute.getValue());
            }
            if (attribute.getKey().equals("val")) {
                volume = Integer.valueOf(new UnsignedIntegerTwoBytesDatatype().valueOf(attribute.getValue()).getValue().intValue());
            }
        }
        if (channel == null || volume == null) {
            return null;
        }
        return new ChannelVolume(channel, volume);
    }

    @Override // org.teleal.cling.support.lastchange.EventedValue
    public Map.Entry<String, String>[] getAttributes() {
        return new Map.Entry[]{new AbstractMap.SimpleEntry("val", new UnsignedIntegerTwoBytesDatatype().getString(new UnsignedIntegerTwoBytes(getValue().getVolume().intValue()))), new AbstractMap.SimpleEntry("channel", getValue().getChannel().name())};
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
