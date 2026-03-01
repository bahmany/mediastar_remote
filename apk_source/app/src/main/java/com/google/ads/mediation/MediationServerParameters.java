package com.google.ads.mediation;

import com.google.android.gms.internal.gs;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

@Deprecated
/* loaded from: classes.dex */
public abstract class MediationServerParameters {

    public static final class MappingException extends Exception {
        public MappingException(String message) {
            super(message);
        }
    }

    @Target({ElementType.FIELD})
    @Retention(RetentionPolicy.RUNTIME)
    protected @interface Parameter {
        String name();

        boolean required() default true;
    }

    protected void a() {
    }

    public void load(Map<String, String> parameters) throws IllegalAccessException, SecurityException, MappingException, IllegalArgumentException {
        HashMap map = new HashMap();
        for (Field field : getClass().getFields()) {
            Parameter parameter = (Parameter) field.getAnnotation(Parameter.class);
            if (parameter != null) {
                map.put(parameter.name(), field);
            }
        }
        if (map.isEmpty()) {
            gs.W("No server options fields detected. To suppress this message either add a field with the @Parameter annotation, or override the load() method.");
        }
        for (Map.Entry<String, String> entry : parameters.entrySet()) {
            Field field2 = (Field) map.remove(entry.getKey());
            if (field2 != null) {
                try {
                    field2.set(this, entry.getValue());
                } catch (IllegalAccessException e) {
                    gs.W("Server option \"" + entry.getKey() + "\" could not be set: Illegal Access");
                } catch (IllegalArgumentException e2) {
                    gs.W("Server option \"" + entry.getKey() + "\" could not be set: Bad Type");
                }
            } else {
                gs.S("Unexpected server option: " + entry.getKey() + " = \"" + entry.getValue() + "\"");
            }
        }
        StringBuilder sb = new StringBuilder();
        for (Field field3 : map.values()) {
            if (((Parameter) field3.getAnnotation(Parameter.class)).required()) {
                gs.W("Required server option missing: " + ((Parameter) field3.getAnnotation(Parameter.class)).name());
                if (sb.length() > 0) {
                    sb.append(", ");
                }
                sb.append(((Parameter) field3.getAnnotation(Parameter.class)).name());
            }
        }
        if (sb.length() > 0) {
            throw new MappingException("Required server option(s) missing: " + sb.toString());
        }
        a();
    }
}
