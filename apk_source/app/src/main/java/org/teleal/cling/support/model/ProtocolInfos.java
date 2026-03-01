package org.teleal.cling.support.model;

import java.util.ArrayList;
import org.teleal.cling.model.ModelUtil;
import org.teleal.cling.model.types.InvalidValueException;

/* loaded from: classes.dex */
public class ProtocolInfos extends ArrayList<ProtocolInfo> {
    public ProtocolInfos(ProtocolInfo... info) {
        for (ProtocolInfo protocolInfo : info) {
            add(protocolInfo);
        }
    }

    public ProtocolInfos(String s) throws InvalidValueException {
        String[] infos = ModelUtil.fromCommaSeparatedList(s);
        if (infos != null) {
            for (String info : infos) {
                add(new ProtocolInfo(info));
            }
        }
    }

    @Override // java.util.AbstractCollection
    public String toString() {
        return ModelUtil.toCommaSeparatedList(toArray(new ProtocolInfo[size()]));
    }
}
