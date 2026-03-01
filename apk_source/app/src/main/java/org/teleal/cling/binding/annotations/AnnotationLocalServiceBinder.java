package org.teleal.cling.binding.annotations;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URI;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;
import org.teleal.cling.binding.LocalServiceBinder;
import org.teleal.cling.binding.LocalServiceBindingException;
import org.teleal.cling.model.ValidationError;
import org.teleal.cling.model.ValidationException;
import org.teleal.cling.model.action.ActionExecutor;
import org.teleal.cling.model.action.QueryStateVariableExecutor;
import org.teleal.cling.model.meta.Action;
import org.teleal.cling.model.meta.LocalService;
import org.teleal.cling.model.meta.QueryStateVariableAction;
import org.teleal.cling.model.meta.StateVariable;
import org.teleal.cling.model.state.FieldStateVariableAccessor;
import org.teleal.cling.model.state.GetterStateVariableAccessor;
import org.teleal.cling.model.state.StateVariableAccessor;
import org.teleal.cling.model.types.ServiceId;
import org.teleal.cling.model.types.ServiceType;
import org.teleal.cling.model.types.UDAServiceId;
import org.teleal.cling.model.types.UDAServiceType;
import org.teleal.cling.model.types.csv.CSV;
import org.teleal.common.util.Reflections;

/* loaded from: classes.dex */
public class AnnotationLocalServiceBinder implements LocalServiceBinder {
    private static Logger log = Logger.getLogger(AnnotationLocalServiceBinder.class.getName());

    @Override // org.teleal.cling.binding.LocalServiceBinder
    public LocalService read(Class<?> clazz) throws LocalServiceBindingException {
        ServiceId serviceId;
        ServiceType serviceType;
        log.fine("Reading and binding annotations of service implementation class: " + clazz);
        if (clazz.isAnnotationPresent(UpnpService.class)) {
            UpnpService annotation = (UpnpService) clazz.getAnnotation(UpnpService.class);
            UpnpServiceId idAnnotation = annotation.serviceId();
            UpnpServiceType typeAnnotation = annotation.serviceType();
            if (idAnnotation.namespace().equals(UDAServiceId.DEFAULT_NAMESPACE)) {
                serviceId = new UDAServiceId(idAnnotation.value());
            } else {
                serviceId = new ServiceId(idAnnotation.namespace(), idAnnotation.value());
            }
            if (typeAnnotation.namespace().equals("schemas-upnp-org")) {
                serviceType = new UDAServiceType(typeAnnotation.value(), typeAnnotation.version());
            } else {
                serviceType = new ServiceType(typeAnnotation.namespace(), typeAnnotation.value(), typeAnnotation.version());
            }
            boolean supportsQueryStateVariables = annotation.supportsQueryStateVariables();
            Set<Class> stringConvertibleTypes = readStringConvertibleTypes(annotation.stringConvertibleTypes());
            return read(clazz, serviceId, serviceType, supportsQueryStateVariables, stringConvertibleTypes);
        }
        throw new LocalServiceBindingException("Given class is not an @UpnpService");
    }

    @Override // org.teleal.cling.binding.LocalServiceBinder
    public LocalService read(Class<?> clazz, ServiceId id, ServiceType type, boolean supportsQueryStateVariables, Class[] stringConvertibleTypes) throws LocalServiceBindingException {
        return read(clazz, id, type, supportsQueryStateVariables, new HashSet(Arrays.asList(stringConvertibleTypes)));
    }

    public LocalService read(Class<?> clazz, ServiceId id, ServiceType type, boolean supportsQueryStateVariables, Set<Class> stringConvertibleTypes) throws LocalServiceBindingException {
        Map<StateVariable, StateVariableAccessor> stateVariables = readStateVariables(clazz, stringConvertibleTypes);
        Map<Action, ActionExecutor> actions = readActions(clazz, stateVariables, stringConvertibleTypes);
        if (supportsQueryStateVariables) {
            actions.put(new QueryStateVariableAction(), new QueryStateVariableExecutor());
        }
        try {
            return new LocalService(type, id, actions, stateVariables, stringConvertibleTypes, supportsQueryStateVariables);
        } catch (ValidationException ex) {
            log.severe("Could not validate device model: " + ex.toString());
            for (ValidationError validationError : ex.getErrors()) {
                log.severe(validationError.toString());
            }
            throw new LocalServiceBindingException("Validation of model failed, check the log");
        }
    }

    protected Set<Class> readStringConvertibleTypes(Class[] declaredTypes) throws LocalServiceBindingException, NoSuchMethodException, SecurityException {
        for (Class stringConvertibleType : declaredTypes) {
            if (!Modifier.isPublic(stringConvertibleType.getModifiers())) {
                throw new LocalServiceBindingException("Declared string-convertible type must be public: " + stringConvertibleType);
            }
            try {
                stringConvertibleType.getConstructor(String.class);
            } catch (NoSuchMethodException e) {
                throw new LocalServiceBindingException("Declared string-convertible type needs a public single-argument String constructor: " + stringConvertibleType);
            }
        }
        Set<Class> stringConvertibleTypes = new HashSet<>(Arrays.asList(declaredTypes));
        stringConvertibleTypes.add(URI.class);
        stringConvertibleTypes.add(URL.class);
        stringConvertibleTypes.add(CSV.class);
        return stringConvertibleTypes;
    }

    protected Map<StateVariable, StateVariableAccessor> readStateVariables(Class<?> clazz, Set<Class> stringConvertibleTypes) throws LocalServiceBindingException {
        String strName;
        String strName2;
        Map<StateVariable, StateVariableAccessor> map = new HashMap<>();
        if (clazz.isAnnotationPresent(UpnpStateVariables.class)) {
            UpnpStateVariables variables = (UpnpStateVariables) clazz.getAnnotation(UpnpStateVariables.class);
            for (UpnpStateVariable v : variables.value()) {
                if (v.name().length() == 0) {
                    throw new LocalServiceBindingException("Class-level @UpnpStateVariable name attribute value required");
                }
                String javaPropertyName = toJavaStateVariableName(v.name());
                Method getter = Reflections.getGetterMethod(clazz, javaPropertyName);
                Field field = Reflections.getField(clazz, javaPropertyName);
                StateVariableAccessor accessor = null;
                if (getter != null && field != null) {
                    if (variables.preferFields()) {
                        accessor = new FieldStateVariableAccessor(field);
                    } else {
                        accessor = new GetterStateVariableAccessor(getter);
                    }
                } else if (field != null) {
                    accessor = new FieldStateVariableAccessor(field);
                } else if (getter != null) {
                    accessor = new GetterStateVariableAccessor(getter);
                } else {
                    log.finer("No field or getter found for state variable, skipping accessor: " + v.name());
                }
                StateVariable stateVar = new AnnotationStateVariableBinder(v, v.name(), accessor, stringConvertibleTypes).createStateVariable();
                map.put(stateVar, accessor);
            }
        }
        for (Field field2 : Reflections.getFields(clazz, UpnpStateVariable.class)) {
            UpnpStateVariable svAnnotation = (UpnpStateVariable) field2.getAnnotation(UpnpStateVariable.class);
            StateVariableAccessor accessor2 = new FieldStateVariableAccessor(field2);
            if (svAnnotation.name().length() == 0) {
                strName2 = toUpnpStateVariableName(field2.getName());
            } else {
                strName2 = svAnnotation.name();
            }
            StateVariable stateVar2 = new AnnotationStateVariableBinder(svAnnotation, strName2, accessor2, stringConvertibleTypes).createStateVariable();
            map.put(stateVar2, accessor2);
        }
        for (Method getter2 : Reflections.getMethods(clazz, UpnpStateVariable.class)) {
            String propertyName = Reflections.getMethodPropertyName(getter2.getName());
            if (propertyName == null) {
                throw new LocalServiceBindingException("Annotated method is not a getter method (: " + getter2);
            }
            if (getter2.getParameterTypes().length > 0) {
                throw new LocalServiceBindingException("Getter method defined as @UpnpStateVariable can not have parameters: " + getter2);
            }
            UpnpStateVariable svAnnotation2 = (UpnpStateVariable) getter2.getAnnotation(UpnpStateVariable.class);
            StateVariableAccessor accessor3 = new GetterStateVariableAccessor(getter2);
            if (svAnnotation2.name().length() == 0) {
                strName = toUpnpStateVariableName(propertyName);
            } else {
                strName = svAnnotation2.name();
            }
            StateVariable stateVar3 = new AnnotationStateVariableBinder(svAnnotation2, strName, accessor3, stringConvertibleTypes).createStateVariable();
            map.put(stateVar3, accessor3);
        }
        return map;
    }

    protected Map<Action, ActionExecutor> readActions(Class<?> clazz, Map<StateVariable, StateVariableAccessor> stateVariables, Set<Class> stringConvertibleTypes) throws LocalServiceBindingException {
        Map<Action, ActionExecutor> map = new HashMap<>();
        for (Method method : Reflections.getMethods(clazz, UpnpAction.class)) {
            AnnotationActionBinder actionBinder = new AnnotationActionBinder(method, stateVariables, stringConvertibleTypes);
            actionBinder.appendAction(map);
        }
        return map;
    }

    static String toUpnpStateVariableName(String javaName) {
        if (javaName.length() < 1) {
            throw new IllegalArgumentException("Variable name must be at least 1 character long");
        }
        return String.valueOf(javaName.substring(0, 1).toUpperCase()) + javaName.substring(1);
    }

    static String toJavaStateVariableName(String upnpName) {
        if (upnpName.length() < 1) {
            throw new IllegalArgumentException("Variable name must be at least 1 character long");
        }
        return String.valueOf(upnpName.substring(0, 1).toLowerCase()) + upnpName.substring(1);
    }

    static String toUpnpActionName(String javaName) {
        if (javaName.length() < 1) {
            throw new IllegalArgumentException("Action name must be at least 1 character long");
        }
        return String.valueOf(javaName.substring(0, 1).toUpperCase()) + javaName.substring(1);
    }

    static String toJavaActionName(String upnpName) {
        if (upnpName.length() < 1) {
            throw new IllegalArgumentException("Variable name must be at least 1 character long");
        }
        return String.valueOf(upnpName.substring(0, 1).toLowerCase()) + upnpName.substring(1);
    }
}
