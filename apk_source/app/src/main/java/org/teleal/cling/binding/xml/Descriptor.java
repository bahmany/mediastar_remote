package org.teleal.cling.binding.xml;

import org.w3c.dom.Node;

/* loaded from: classes.dex */
public abstract class Descriptor {

    public interface Device {
        public static final String DLNA_NAMESPACE_URI = "urn:schemas-dlna-org:device-1-0";
        public static final String DLNA_PREFIX = "dlna";
        public static final String NAMESPACE_URI = "urn:schemas-upnp-org:device-1-0";

        public enum ELEMENT {
            root,
            specVersion,
            major,
            minor,
            URLBase,
            device,
            UDN,
            X_DLNADOC,
            X_DLNACAP,
            deviceType,
            friendlyName,
            manufacturer,
            manufacturerURL,
            modelDescription,
            modelName,
            modelNumber,
            modelURL,
            presentationURL,
            UPC,
            serialNumber,
            iconList,
            icon,
            width,
            height,
            depth,
            url,
            mimetype,
            serviceList,
            service,
            serviceType,
            serviceId,
            SCPDURL,
            controlURL,
            eventSubURL,
            deviceList;

            /* renamed from: values, reason: to resolve conflict with enum method */
            public static ELEMENT[] valuesCustom() {
                ELEMENT[] elementArrValuesCustom = values();
                int length = elementArrValuesCustom.length;
                ELEMENT[] elementArr = new ELEMENT[length];
                System.arraycopy(elementArrValuesCustom, 0, elementArr, 0, length);
                return elementArr;
            }

            public static ELEMENT valueOrNullOf(String s) {
                try {
                    return valueOf(s);
                } catch (IllegalArgumentException e) {
                    return null;
                }
            }

            public boolean equals(Node node) {
                return toString().equals(node.getLocalName());
            }
        }
    }

    public interface Service {
        public static final String NAMESPACE_URI = "urn:schemas-upnp-org:service-1-0";

        public enum ATTRIBUTE {
            sendEvents;

            /* renamed from: values, reason: to resolve conflict with enum method */
            public static ATTRIBUTE[] valuesCustom() {
                ATTRIBUTE[] attributeArrValuesCustom = values();
                int length = attributeArrValuesCustom.length;
                ATTRIBUTE[] attributeArr = new ATTRIBUTE[length];
                System.arraycopy(attributeArrValuesCustom, 0, attributeArr, 0, length);
                return attributeArr;
            }
        }

        public enum ELEMENT {
            scpd,
            specVersion,
            major,
            minor,
            actionList,
            action,
            name,
            argumentList,
            argument,
            direction,
            relatedStateVariable,
            retval,
            serviceStateTable,
            stateVariable,
            dataType,
            defaultValue,
            allowedValueList,
            allowedValue,
            allowedValueRange,
            minimum,
            maximum,
            step;

            /* renamed from: values, reason: to resolve conflict with enum method */
            public static ELEMENT[] valuesCustom() {
                ELEMENT[] elementArrValuesCustom = values();
                int length = elementArrValuesCustom.length;
                ELEMENT[] elementArr = new ELEMENT[length];
                System.arraycopy(elementArrValuesCustom, 0, elementArr, 0, length);
                return elementArr;
            }

            public static ELEMENT valueOrNullOf(String s) {
                try {
                    return valueOf(s);
                } catch (IllegalArgumentException e) {
                    return null;
                }
            }

            public boolean equals(Node node) {
                return toString().equals(node.getLocalName());
            }
        }
    }
}
