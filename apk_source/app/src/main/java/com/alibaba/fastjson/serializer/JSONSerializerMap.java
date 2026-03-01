package com.alibaba.fastjson.serializer;

@Deprecated
/* loaded from: classes.dex */
public class JSONSerializerMap extends SerializeConfig {
    public final boolean put(Class<?> clazz, ObjectSerializer serializer) {
        return super.put((JSONSerializerMap) clazz, (Class<?>) serializer);
    }
}
