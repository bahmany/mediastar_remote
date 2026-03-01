package com.alibaba.fastjson.serializer;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.concurrent.atomic.AtomicLong;

/* loaded from: classes.dex */
public class AtomicLongSerializer implements ObjectSerializer {
    public static final AtomicLongSerializer instance = new AtomicLongSerializer();

    @Override // com.alibaba.fastjson.serializer.ObjectSerializer
    public void write(JSONSerializer serializer, Object object, Object fieldName, Type fieldType, int features) throws IOException {
        SerializeWriter out = serializer.getWriter();
        AtomicLong val = (AtomicLong) object;
        out.writeLong(val.get());
    }
}
