package com.alibaba.fastjson.util;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.annotation.JSONCreator;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/* loaded from: classes.dex */
public class DeserializeBeanInfo {
    private final Class<?> clazz;
    private Constructor<?> creatorConstructor;
    private Constructor<?> defaultConstructor;
    private Method factoryMethod;
    private int parserFeatures;
    private final List<FieldInfo> fieldList = new ArrayList();
    private final List<FieldInfo> sortedFieldList = new ArrayList();

    public DeserializeBeanInfo(Class<?> clazz) {
        this.parserFeatures = 0;
        this.clazz = clazz;
        this.parserFeatures = TypeUtils.getParserFeatures(clazz);
    }

    public Constructor<?> getDefaultConstructor() {
        return this.defaultConstructor;
    }

    public void setDefaultConstructor(Constructor<?> defaultConstructor) {
        this.defaultConstructor = defaultConstructor;
    }

    public Constructor<?> getCreatorConstructor() {
        return this.creatorConstructor;
    }

    public void setCreatorConstructor(Constructor<?> createConstructor) {
        this.creatorConstructor = createConstructor;
    }

    public Method getFactoryMethod() {
        return this.factoryMethod;
    }

    public void setFactoryMethod(Method factoryMethod) {
        this.factoryMethod = factoryMethod;
    }

    public Class<?> getClazz() {
        return this.clazz;
    }

    public List<FieldInfo> getFieldList() {
        return this.fieldList;
    }

    public List<FieldInfo> getSortedFieldList() {
        return this.sortedFieldList;
    }

    public FieldInfo getField(String propertyName) {
        for (FieldInfo item : this.fieldList) {
            if (item.getName().equals(propertyName)) {
                return item;
            }
        }
        return null;
    }

    public boolean add(FieldInfo field) {
        for (FieldInfo item : this.fieldList) {
            if (item.getName().equals(field.getName()) && (!item.isGetOnly() || field.isGetOnly())) {
                return false;
            }
        }
        this.fieldList.add(field);
        this.sortedFieldList.add(field);
        Collections.sort(this.sortedFieldList);
        return true;
    }

    /* JADX WARN: Removed duplicated region for block: B:65:0x01c6 A[PHI: r8 r9
  0x01c6: PHI (r8v6 'ordinal' int) = (r8v5 'ordinal' int), (r8v9 'ordinal' int) binds: [B:59:0x018c, B:63:0x01a8] A[DONT_GENERATE, DONT_INLINE]
  0x01c6: PHI (r9v6 'serialzeFeatures' int) = (r9v5 'serialzeFeatures' int), (r9v9 'serialzeFeatures' int) binds: [B:59:0x018c, B:63:0x01a8] A[DONT_GENERATE, DONT_INLINE]] */
    /* JADX WARN: Removed duplicated region for block: B:95:0x02d0 A[PHI: r8 r9
  0x02d0: PHI (r8v7 'ordinal' int) = (r8v6 'ordinal' int), (r8v6 'ordinal' int), (r8v8 'ordinal' int) binds: [B:77:0x022d, B:79:0x0237, B:81:0x024d] A[DONT_GENERATE, DONT_INLINE]
  0x02d0: PHI (r9v7 'serialzeFeatures' int) = (r9v6 'serialzeFeatures' int), (r9v6 'serialzeFeatures' int), (r9v8 'serialzeFeatures' int) binds: [B:77:0x022d, B:79:0x0237, B:81:0x024d] A[DONT_GENERATE, DONT_INLINE]] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public static com.alibaba.fastjson.util.DeserializeBeanInfo computeSetters(java.lang.Class<?> r36, java.lang.reflect.Type r37) throws java.lang.SecurityException {
        /*
            Method dump skipped, instructions count: 1080
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: com.alibaba.fastjson.util.DeserializeBeanInfo.computeSetters(java.lang.Class, java.lang.reflect.Type):com.alibaba.fastjson.util.DeserializeBeanInfo");
    }

    public static Constructor<?> getDefaultConstructor(Class<?> clazz) throws SecurityException {
        if (Modifier.isAbstract(clazz.getModifiers())) {
            return null;
        }
        Constructor<?> defaultConstructor = null;
        Constructor<?>[] declaredConstructors = clazz.getDeclaredConstructors();
        int length = declaredConstructors.length;
        int i = 0;
        while (true) {
            if (i >= length) {
                break;
            }
            Constructor<?> constructor = declaredConstructors[i];
            if (constructor.getParameterTypes().length != 0) {
                i++;
            } else {
                defaultConstructor = constructor;
                break;
            }
        }
        if (defaultConstructor == null && clazz.isMemberClass() && !Modifier.isStatic(clazz.getModifiers())) {
            for (Constructor<?> constructor2 : clazz.getDeclaredConstructors()) {
                if (constructor2.getParameterTypes().length == 1 && constructor2.getParameterTypes()[0].equals(clazz.getDeclaringClass())) {
                    return constructor2;
                }
            }
            return defaultConstructor;
        }
        return defaultConstructor;
    }

    public static Constructor<?> getCreatorConstructor(Class<?> clazz) throws SecurityException {
        for (Constructor<?> constructor : clazz.getDeclaredConstructors()) {
            JSONCreator annotation = (JSONCreator) constructor.getAnnotation(JSONCreator.class);
            if (annotation != null) {
                if (0 != 0) {
                    throw new JSONException("multi-json creator");
                }
                return constructor;
            }
        }
        return null;
    }

    public static Method getFactoryMethod(Class<?> clazz) throws SecurityException {
        for (Method method : clazz.getDeclaredMethods()) {
            if (Modifier.isStatic(method.getModifiers()) && clazz.isAssignableFrom(method.getReturnType())) {
                JSONCreator annotation = (JSONCreator) method.getAnnotation(JSONCreator.class);
                if (annotation != null) {
                    if (0 != 0) {
                        throw new JSONException("multi-json creator");
                    }
                    return method;
                }
            }
        }
        return null;
    }

    public int getParserFeatures() {
        return this.parserFeatures;
    }
}
