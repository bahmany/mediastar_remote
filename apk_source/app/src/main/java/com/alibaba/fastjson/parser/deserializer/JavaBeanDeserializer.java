package com.alibaba.fastjson.parser.deserializer;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.DefaultJSONParser;
import com.alibaba.fastjson.parser.Feature;
import com.alibaba.fastjson.parser.JSONLexer;
import com.alibaba.fastjson.parser.ParserConfig;
import com.alibaba.fastjson.serializer.FilterUtils;
import com.alibaba.fastjson.util.DeserializeBeanInfo;
import com.alibaba.fastjson.util.FieldInfo;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/* loaded from: classes.dex */
public class JavaBeanDeserializer implements ObjectDeserializer {
    private DeserializeBeanInfo beanInfo;
    private final Class<?> clazz;
    private final Map<String, FieldDeserializer> feildDeserializerMap;
    private final List<FieldDeserializer> fieldDeserializers;
    private final List<FieldDeserializer> sortedFieldDeserializers;

    public JavaBeanDeserializer(ParserConfig config, Class<?> clazz) {
        this(config, clazz, clazz);
    }

    public JavaBeanDeserializer(ParserConfig config, Class<?> clazz, Type type) {
        this.feildDeserializerMap = new IdentityHashMap();
        this.fieldDeserializers = new ArrayList();
        this.sortedFieldDeserializers = new ArrayList();
        this.clazz = clazz;
        this.beanInfo = DeserializeBeanInfo.computeSetters(clazz, type);
        for (FieldInfo fieldInfo : this.beanInfo.getFieldList()) {
            addFieldDeserializer(config, clazz, fieldInfo);
        }
        for (FieldInfo fieldInfo2 : this.beanInfo.getSortedFieldList()) {
            FieldDeserializer fieldDeserializer = this.feildDeserializerMap.get(fieldInfo2.getName().intern());
            this.sortedFieldDeserializers.add(fieldDeserializer);
        }
    }

    public Map<String, FieldDeserializer> getFieldDeserializerMap() {
        return this.feildDeserializerMap;
    }

    public FieldDeserializer getFieldDeserializer(String name) {
        FieldDeserializer feildDeser = this.feildDeserializerMap.get(name);
        if (feildDeser == null) {
            for (Map.Entry<String, FieldDeserializer> entry : this.feildDeserializerMap.entrySet()) {
                if (name.equals(entry.getKey())) {
                    return entry.getValue();
                }
            }
            return null;
        }
        return feildDeser;
    }

    public Class<?> getClazz() {
        return this.clazz;
    }

    private void addFieldDeserializer(ParserConfig mapping, Class<?> clazz, FieldInfo fieldInfo) {
        String interName = fieldInfo.getName().intern();
        FieldDeserializer fieldDeserializer = createFieldDeserializer(mapping, clazz, fieldInfo);
        this.feildDeserializerMap.put(interName, fieldDeserializer);
        this.fieldDeserializers.add(fieldDeserializer);
    }

    public FieldDeserializer createFieldDeserializer(ParserConfig mapping, Class<?> clazz, FieldInfo fieldInfo) {
        return mapping.createFieldDeserializer(mapping, clazz, fieldInfo);
    }

    public Object createInstance(DefaultJSONParser parser, Type type) throws IllegalAccessException, InstantiationException, IllegalArgumentException, InvocationTargetException {
        Object object;
        if ((type instanceof Class) && this.clazz.isInterface()) {
            Class<?> clazz = (Class) type;
            ClassLoader loader = Thread.currentThread().getContextClassLoader();
            JSONObject obj = new JSONObject();
            return Proxy.newProxyInstance(loader, new Class[]{clazz}, obj);
        }
        if (this.beanInfo.getDefaultConstructor() == null) {
            return null;
        }
        try {
            Constructor<?> constructor = this.beanInfo.getDefaultConstructor();
            if (constructor.getParameterTypes().length == 0) {
                object = constructor.newInstance(new Object[0]);
            } else {
                object = constructor.newInstance(parser.getContext().getObject());
            }
            if (parser.isEnabled(Feature.InitStringFieldAsEmpty)) {
                for (FieldInfo fieldInfo : this.beanInfo.getFieldList()) {
                    if (fieldInfo.getFieldClass() == String.class) {
                        try {
                            fieldInfo.set(object, "");
                        } catch (Exception e) {
                            throw new JSONException("create instance error, class " + this.clazz.getName(), e);
                        }
                    }
                }
            }
            return object;
        } catch (Exception e2) {
            throw new JSONException("create instance error, class " + this.clazz.getName(), e2);
        }
    }

    @Override // com.alibaba.fastjson.parser.deserializer.ObjectDeserializer
    public <T> T deserialze(DefaultJSONParser defaultJSONParser, Type type, Object obj) {
        return (T) deserialze(defaultJSONParser, type, obj, null);
    }

    public <T> T deserialzeArrayMapping(DefaultJSONParser defaultJSONParser, Type type, Object obj, Object obj2) {
        JSONLexer lexer = defaultJSONParser.getLexer();
        if (lexer.token() != 14) {
            throw new JSONException("error");
        }
        T t = (T) createInstance(defaultJSONParser, type);
        int size = this.sortedFieldDeserializers.size();
        int i = 0;
        while (i < size) {
            char c = i == size + (-1) ? ']' : ',';
            FieldDeserializer fieldDeserializer = this.sortedFieldDeserializers.get(i);
            Class<?> fieldClass = fieldDeserializer.getFieldClass();
            if (fieldClass == Integer.TYPE) {
                fieldDeserializer.setValue((Object) t, lexer.scanInt(c));
            } else if (fieldClass == String.class) {
                fieldDeserializer.setValue((Object) t, lexer.scanString(c));
            } else if (fieldClass == Long.TYPE) {
                fieldDeserializer.setValue(t, lexer.scanLong(c));
            } else if (fieldClass.isEnum()) {
                fieldDeserializer.setValue(t, lexer.scanEnum(fieldClass, defaultJSONParser.getSymbolTable(), c));
            } else {
                lexer.nextToken(14);
                fieldDeserializer.setValue(t, defaultJSONParser.parseObject(fieldDeserializer.getFieldType()));
                if (c == ']') {
                    if (lexer.token() != 15) {
                        throw new JSONException("syntax error");
                    }
                    lexer.nextToken(16);
                } else if (c == ',' && lexer.token() != 16) {
                    throw new JSONException("syntax error");
                }
            }
            i++;
        }
        lexer.nextToken(16);
        return t;
    }

    /* JADX WARN: Code restructure failed: missing block: B:263:0x0101, code lost:
    
        if (r31 != null) goto L359;
     */
    /* JADX WARN: Code restructure failed: missing block: B:264:0x0103, code lost:
    
        if (r8 != null) goto L352;
     */
    /* JADX WARN: Code restructure failed: missing block: B:265:0x0105, code lost:
    
        r31 = (T) createInstance(r28, r29);
     */
    /* JADX WARN: Code restructure failed: missing block: B:266:0x0109, code lost:
    
        if (r10 != null) goto L268;
     */
    /* JADX WARN: Code restructure failed: missing block: B:267:0x010b, code lost:
    
        r10 = r28.setContext(r11, r31, r30);
     */
    /* JADX WARN: Code restructure failed: missing block: B:268:0x0115, code lost:
    
        if (r10 == null) goto L270;
     */
    /* JADX WARN: Code restructure failed: missing block: B:269:0x0117, code lost:
    
        r10.setObject(r31);
     */
    /* JADX WARN: Code restructure failed: missing block: B:270:0x011c, code lost:
    
        r28.setContext(r11);
     */
    /* JADX WARN: Code restructure failed: missing block: B:322:0x0272, code lost:
    
        r26 = com.alibaba.fastjson.util.TypeUtils.loadClass(r25);
        r3 = (T) r28.getConfig().getDeserializer(r26).deserialze(r28, r26, r30);
     */
    /* JADX WARN: Code restructure failed: missing block: B:323:0x028a, code lost:
    
        if (r10 == null) goto L325;
     */
    /* JADX WARN: Code restructure failed: missing block: B:324:0x028c, code lost:
    
        r10.setObject(r31);
     */
    /* JADX WARN: Code restructure failed: missing block: B:325:0x0291, code lost:
    
        r28.setContext(r11);
     */
    /* JADX WARN: Code restructure failed: missing block: B:337:0x02d5, code lost:
    
        if (r18.token() != 13) goto L391;
     */
    /* JADX WARN: Code restructure failed: missing block: B:338:0x02d7, code lost:
    
        r18.nextToken();
     */
    /* JADX WARN: Code restructure failed: missing block: B:352:0x032d, code lost:
    
        r15 = r27.beanInfo.getFieldList();
        r24 = r15.size();
        r0 = new java.lang.Object[r24];
        r17 = 0;
     */
    /* JADX WARN: Code restructure failed: missing block: B:354:0x0345, code lost:
    
        if (r17 >= r24) goto L405;
     */
    /* JADX WARN: Code restructure failed: missing block: B:355:0x0347, code lost:
    
        r0[r17] = r8.get(r15.get(r17).getName());
        r17 = r17 + 1;
     */
    /* JADX WARN: Code restructure failed: missing block: B:357:0x0364, code lost:
    
        if (r27.beanInfo.getCreatorConstructor() == null) goto L365;
     */
    /* JADX WARN: Code restructure failed: missing block: B:358:0x0366, code lost:
    
        r31 = r27.beanInfo.getCreatorConstructor().newInstance(r0);
     */
    /* JADX WARN: Code restructure failed: missing block: B:359:0x0374, code lost:
    
        if (r10 == null) goto L361;
     */
    /* JADX WARN: Code restructure failed: missing block: B:360:0x0376, code lost:
    
        r10.setObject(r31);
     */
    /* JADX WARN: Code restructure failed: missing block: B:361:0x037b, code lost:
    
        r28.setContext(r11);
     */
    /* JADX WARN: Code restructure failed: missing block: B:362:0x0384, code lost:
    
        r13 = move-exception;
     */
    /* JADX WARN: Code restructure failed: missing block: B:364:0x03a9, code lost:
    
        throw new com.alibaba.fastjson.JSONException("create instance error, " + r27.beanInfo.getCreatorConstructor().toGenericString(), r13);
     */
    /* JADX WARN: Code restructure failed: missing block: B:366:0x03b2, code lost:
    
        if (r27.beanInfo.getFactoryMethod() == null) goto L359;
     */
    /* JADX WARN: Code restructure failed: missing block: B:367:0x03b4, code lost:
    
        r31 = r27.beanInfo.getFactoryMethod().invoke(null, r0);
     */
    /* JADX WARN: Code restructure failed: missing block: B:369:0x03c4, code lost:
    
        r13 = move-exception;
     */
    /* JADX WARN: Code restructure failed: missing block: B:371:0x03e9, code lost:
    
        throw new com.alibaba.fastjson.JSONException("create factory method error, " + r27.beanInfo.getFactoryMethod().toString(), r13);
     */
    /* JADX WARN: Code restructure failed: missing block: B:410:?, code lost:
    
        return r31;
     */
    /* JADX WARN: Code restructure failed: missing block: B:412:?, code lost:
    
        return r3;
     */
    /* JADX WARN: Code restructure failed: missing block: B:413:?, code lost:
    
        return (T) r31;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public <T> T deserialze(com.alibaba.fastjson.parser.DefaultJSONParser r28, java.lang.reflect.Type r29, java.lang.Object r30, java.lang.Object r31) throws java.lang.Throwable {
        /*
            Method dump skipped, instructions count: 1010
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: com.alibaba.fastjson.parser.deserializer.JavaBeanDeserializer.deserialze(com.alibaba.fastjson.parser.DefaultJSONParser, java.lang.reflect.Type, java.lang.Object, java.lang.Object):java.lang.Object");
    }

    public boolean parseField(DefaultJSONParser parser, String key, Object object, Type objectType, Map<String, Object> fieldValues) {
        JSONLexer lexer = parser.getLexer();
        FieldDeserializer fieldDeserializer = this.feildDeserializerMap.get(key);
        if (fieldDeserializer == null) {
            Iterator<Map.Entry<String, FieldDeserializer>> it = this.feildDeserializerMap.entrySet().iterator();
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
            parseExtra(parser, object, key);
            return false;
        }
        lexer.nextTokenWithColon(fieldDeserializer.getFastMatchToken());
        fieldDeserializer.parseField(parser, object, objectType, fieldValues);
        return true;
    }

    void parseExtra(DefaultJSONParser parser, Object object, String key) {
        Object value;
        JSONLexer lexer = parser.getLexer();
        if (!lexer.isEnabled(Feature.IgnoreNotMatch)) {
            throw new JSONException("setter not found, class " + this.clazz.getName() + ", property " + key);
        }
        lexer.nextTokenWithColon();
        Type type = FilterUtils.getExtratype(parser, object, key);
        if (type == null) {
            value = parser.parse();
        } else {
            value = parser.parseObject(type);
        }
        FilterUtils.processExtra(parser, object, key, value);
    }

    @Override // com.alibaba.fastjson.parser.deserializer.ObjectDeserializer
    public int getFastMatchToken() {
        return 12;
    }

    public List<FieldDeserializer> getSortedFieldDeserializers() {
        return this.sortedFieldDeserializers;
    }

    public final boolean isSupportArrayToBean(JSONLexer lexer) {
        return Feature.isEnabled(this.beanInfo.getParserFeatures(), Feature.SupportArrayToBean) || lexer.isEnabled(Feature.SupportArrayToBean);
    }
}
