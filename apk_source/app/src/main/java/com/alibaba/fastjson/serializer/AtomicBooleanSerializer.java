package com.alibaba.fastjson.serializer;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.concurrent.atomic.AtomicBoolean;

/* loaded from: classes.dex */
public class AtomicBooleanSerializer implements ObjectSerializer {
    public static final AtomicBooleanSerializer instance = new AtomicBooleanSerializer();

    @Override // com.alibaba.fastjson.serializer.ObjectSerializer
    public void write(JSONSerializer serializer, Object object, Object fieldName, Type fieldType, int features) throws IOException {
        SerializeWriter out = serializer.getWriter();
        AtomicBoolean val = (AtomicBoolean) object;
        if (val.get()) {
            out.append("true");
        } else {
            out.append("false");
        }
    }
}
