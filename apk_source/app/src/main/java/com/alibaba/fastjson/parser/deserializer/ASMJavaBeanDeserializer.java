package com.alibaba.fastjson.parser.deserializer;

import com.alibaba.fastjson.parser.DefaultJSONParser;
import com.alibaba.fastjson.parser.JSONLexer;
import com.alibaba.fastjson.parser.ParserConfig;
import com.alibaba.fastjson.util.FieldInfo;
import java.lang.reflect.Type;
import java.util.Iterator;
import java.util.Map;

/* loaded from: classes.dex */
public abstract class ASMJavaBeanDeserializer implements ObjectDeserializer {
    protected InnerJavaBeanDeserializer serializer;

    public abstract Object createInstance(DefaultJSONParser defaultJSONParser, Type type);

    public ASMJavaBeanDeserializer(ParserConfig mapping, Class<?> clazz) {
        this.serializer = new InnerJavaBeanDeserializer(mapping, clazz);
        this.serializer.getFieldDeserializerMap();
    }

    public InnerJavaBeanDeserializer getInnterSerializer() {
        return this.serializer;
    }

    @Override // com.alibaba.fastjson.parser.deserializer.ObjectDeserializer
    public <T> T deserialze(DefaultJSONParser defaultJSONParser, Type type, Object obj) {
        return (T) this.serializer.deserialze(defaultJSONParser, type, obj);
    }

    @Override // com.alibaba.fastjson.parser.deserializer.ObjectDeserializer
    public int getFastMatchToken() {
        return this.serializer.getFastMatchToken();
    }

    public Object createInstance(DefaultJSONParser parser) {
        return this.serializer.createInstance(parser, this.serializer.getClazz());
    }

    public FieldDeserializer createFieldDeserializer(ParserConfig mapping, Class<?> clazz, FieldInfo fieldInfo) {
        return mapping.createFieldDeserializer(mapping, clazz, fieldInfo);
    }

    public FieldDeserializer getFieldDeserializer(String name) {
        return this.serializer.getFieldDeserializerMap().get(name);
    }

    public Type getFieldType(String name) {
        return this.serializer.getFieldDeserializerMap().get(name).getFieldType();
    }

    public boolean parseField(DefaultJSONParser parser, String key, Object object, Type objectType, Map<String, Object> fieldValues) {
        JSONLexer lexer = parser.getLexer();
        Map<String, FieldDeserializer> feildDeserializerMap = this.serializer.getFieldDeserializerMap();
        FieldDeserializer fieldDeserializer = feildDeserializerMap.get(key);
        if (fieldDeserializer == null) {
            Iterator<Map.Entry<String, FieldDeserializer>> it = feildDeserializerMap.entrySet().iterator();
            while (true) {
                if (!it.hasNext()) {
                    break;
                }
                Map.Entry<String, FieldDeserializer> entry = it.next();
                if (entry.getKey().equalsIgnoreCase(key)) {
                    fieldDeserializer = entry.getValue();
                    break;
                }
            }
        }
        if (fieldDeserializer == null) {
            this.serializer.parseExtra(parser, object, key);
            return false;
        }
        lexer.nextTokenWithColon(fieldDeserializer.getFastMatchToken());
        fieldDeserializer.parseField(parser, object, objectType, fieldValues);
        return true;
    }

    public final class InnerJavaBeanDeserializer extends JavaBeanDeserializer {
        /* synthetic */ InnerJavaBeanDeserializer(ASMJavaBeanDeserializer x0, ParserConfig x1, Class x2, AnonymousClass1 x3) {
            this(x1, x2);
        }

        private InnerJavaBeanDeserializer(ParserConfig mapping, Class<?> clazz) {
            super(mapping, clazz);
        }

        @Override // com.alibaba.fastjson.parser.deserializer.JavaBeanDeserializer
        public boolean parseField(DefaultJSONParser parser, String key, Object object, Type objectType, Map<String, Object> fieldValues) {
            return ASMJavaBeanDeserializer.this.parseField(parser, key, object, objectType, fieldValues);
        }

        @Override // com.alibaba.fastjson.parser.deserializer.JavaBeanDeserializer
        public FieldDeserializer createFieldDeserializer(ParserConfig mapping, Class<?> clazz, FieldInfo fieldInfo) {
            return ASMJavaBeanDeserializer.this.createFieldDeserializer(mapping, clazz, fieldInfo);
        }
    }

    public boolean isSupportArrayToBean(JSONLexer lexer) {
        return this.serializer.isSupportArrayToBean(lexer);
    }

    public Object parseRest(DefaultJSONParser parser, Type type, Object fieldName, Object instance) {
        Object value = this.serializer.deserialze(parser, type, fieldName, instance);
        return value;
    }
}
