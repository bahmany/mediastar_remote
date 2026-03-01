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
public class EventedValueChannelVolumeDB extends EventedValue<ChannelVolumeDB> {
    @Override // org.teleal.cling.support.lastchange.EventedValue
    protected /* bridge */ /* synthetic */ ChannelVolumeDB valueOf(Map.Entry[] entryArr) throws InvalidValueException {
        return valueOf2((Map.Entry<String, String>[]) entryArr);
    }

    public EventedValueChannelVolumeDB(ChannelVolumeDB value) {
        super(value);
    }

    public EventedValueChannelVolumeDB(Map.Entry<String, String>[] entryArr) {
        super(entryArr);
    }

    @Override // org.teleal.cling.support.lastchange.EventedValue
    /* renamed from: valueOf */
    protected ChannelVolumeDB valueOf2(Map.Entry<String, String>[] entryArr) throws InvalidValueException {
        Channel channel = null;
        Integer volumeDB = null;
        for (Map.Entry<String, String> attribute : entryArr) {
            if (attribute.getKey().equals("channel")) {
                channel = Channel.valueOf(attribute.getValue());
            }
            if (attribute.getKey().equals("val")) {
                volumeDB = Integer.valueOf(new UnsignedIntegerTwoBytesDatatype().valueOf(attribute.getValue()).getValue().intValue());
            }
        }
        if (channel == null || volumeDB == null) {
            return null;
        }
        return new ChannelVolumeDB(channel, volumeDB);
    }

    @Override // org.teleal.cling.support.lastchange.EventedValue
    public Map.Entry<String, String>[] getAttributes() {
        return new Map.Entry[]{new AbstractMap.SimpleEntry("val", new UnsignedIntegerTwoBytesDatatype().getString(new UnsignedIntegerTwoBytes(getValue().getVolumeDB().intValue()))), new AbstractMap.SimpleEntry("channel", getValue().getChannel().name())};
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
