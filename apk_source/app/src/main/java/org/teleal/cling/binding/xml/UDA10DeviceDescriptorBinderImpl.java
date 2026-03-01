package org.teleal.cling.binding.xml;

import java.io.StringReader;
import java.net.URI;
import java.net.URL;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilderFactory;
import org.teleal.cling.binding.staging.MutableDevice;
import org.teleal.cling.binding.staging.MutableIcon;
import org.teleal.cling.binding.staging.MutableService;
import org.teleal.cling.binding.xml.Descriptor;
import org.teleal.cling.model.Namespace;
import org.teleal.cling.model.ValidationException;
import org.teleal.cling.model.XMLUtil;
import org.teleal.cling.model.meta.Device;
import org.teleal.cling.model.meta.DeviceDetails;
import org.teleal.cling.model.meta.Icon;
import org.teleal.cling.model.meta.LocalService;
import org.teleal.cling.model.meta.RemoteService;
import org.teleal.cling.model.meta.Service;
import org.teleal.cling.model.profile.ControlPointInfo;
import org.teleal.cling.model.types.DLNACaps;
import org.teleal.cling.model.types.DLNADoc;
import org.teleal.cling.model.types.InvalidValueException;
import org.teleal.cling.model.types.ServiceId;
import org.teleal.cling.model.types.ServiceType;
import org.teleal.cling.model.types.UDN;
import org.teleal.common.util.Exceptions;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

/* loaded from: classes.dex */
public class UDA10DeviceDescriptorBinderImpl implements DeviceDescriptorBinder {
    private static Logger log = Logger.getLogger(DeviceDescriptorBinder.class.getName());

    @Override // org.teleal.cling.binding.xml.DeviceDescriptorBinder
    public <D extends Device> D describe(D d, String str) throws ValidationException, DescriptorBindingException {
        if (str == null || str.length() == 0) {
            throw new DescriptorBindingException("Null or empty descriptor");
        }
        try {
            log.fine("Populating device from XML descriptor: " + d);
            DocumentBuilderFactory documentBuilderFactoryNewInstance = DocumentBuilderFactory.newInstance();
            documentBuilderFactoryNewInstance.setNamespaceAware(true);
            return (D) describe((UDA10DeviceDescriptorBinderImpl) d, documentBuilderFactoryNewInstance.newDocumentBuilder().parse(new InputSource(new StringReader(str.trim()))));
        } catch (ValidationException e) {
            throw e;
        } catch (Exception e2) {
            throw new DescriptorBindingException("Could not parse device descriptor: " + e2.toString(), e2);
        }
    }

    @Override // org.teleal.cling.binding.xml.DeviceDescriptorBinder
    public <D extends Device> D describe(D d, Document document) throws ValidationException, DescriptorBindingException {
        try {
            log.fine("Populating device from DOM: " + d);
            MutableDevice mutableDevice = new MutableDevice();
            hydrateRoot(mutableDevice, document.getDocumentElement());
            return (D) buildInstance(d, mutableDevice);
        } catch (ValidationException e) {
            throw e;
        } catch (Exception e2) {
            throw new DescriptorBindingException("Could not parse device DOM: " + e2.toString(), e2);
        }
    }

    public <D extends Device> D buildInstance(D d, MutableDevice mutableDevice) throws ValidationException {
        return (D) mutableDevice.build(d);
    }

    protected void hydrateRoot(MutableDevice descriptor, Element rootElement) throws DescriptorBindingException {
        if (rootElement.getNamespaceURI() == null || !rootElement.getNamespaceURI().equals("urn:schemas-upnp-org:device-1-0")) {
            log.warning("Wrong XML namespace declared on root element: " + rootElement.getNamespaceURI());
        }
        if (!rootElement.getNodeName().equals(Descriptor.Device.ELEMENT.root.name())) {
            throw new DescriptorBindingException("Root element name is not <root>: " + rootElement.getNodeName());
        }
        NodeList rootChildren = rootElement.getChildNodes();
        Node deviceNode = null;
        for (int i = 0; i < rootChildren.getLength(); i++) {
            Node rootChild = rootChildren.item(i);
            if (rootChild.getNodeType() == 1) {
                if (Descriptor.Device.ELEMENT.specVersion.equals(rootChild)) {
                    hydrateSpecVersion(descriptor, rootChild);
                } else if (Descriptor.Device.ELEMENT.URLBase.equals(rootChild)) {
                    try {
                        descriptor.baseURL = new URL(XMLUtil.getTextContent(rootChild));
                    } catch (Exception ex) {
                        throw new DescriptorBindingException("Invalid URLBase: " + ex.getMessage());
                    }
                } else if (Descriptor.Device.ELEMENT.device.equals(rootChild)) {
                    if (deviceNode != null) {
                        throw new DescriptorBindingException("Found multiple <device> elements in <root>");
                    }
                    deviceNode = rootChild;
                } else {
                    log.finer("Ignoring unknown element: " + rootChild.getNodeName());
                }
            }
        }
        if (deviceNode == null) {
            throw new DescriptorBindingException("No <device> element in <root>");
        }
        hydrateDevice(descriptor, deviceNode);
    }

    public void hydrateSpecVersion(MutableDevice descriptor, Node specVersionNode) throws DescriptorBindingException {
        NodeList specVersionChildren = specVersionNode.getChildNodes();
        for (int i = 0; i < specVersionChildren.getLength(); i++) {
            Node specVersionChild = specVersionChildren.item(i);
            if (specVersionChild.getNodeType() == 1) {
                if (Descriptor.Device.ELEMENT.major.equals(specVersionChild)) {
                    descriptor.udaVersion.major = Integer.valueOf(XMLUtil.getTextContent(specVersionChild)).intValue();
                } else if (Descriptor.Device.ELEMENT.minor.equals(specVersionChild)) {
                    descriptor.udaVersion.minor = Integer.valueOf(XMLUtil.getTextContent(specVersionChild)).intValue();
                }
            }
        }
    }

    public void hydrateDevice(MutableDevice descriptor, Node deviceNode) throws DescriptorBindingException {
        NodeList deviceNodeChildren = deviceNode.getChildNodes();
        for (int i = 0; i < deviceNodeChildren.getLength(); i++) {
            Node deviceNodeChild = deviceNodeChildren.item(i);
            if (deviceNodeChild.getNodeType() == 1) {
                if (Descriptor.Device.ELEMENT.deviceType.equals(deviceNodeChild)) {
                    descriptor.deviceType = XMLUtil.getTextContent(deviceNodeChild);
                } else if (Descriptor.Device.ELEMENT.friendlyName.equals(deviceNodeChild)) {
                    descriptor.friendlyName = XMLUtil.getTextContent(deviceNodeChild);
                } else if (Descriptor.Device.ELEMENT.manufacturer.equals(deviceNodeChild)) {
                    descriptor.manufacturer = XMLUtil.getTextContent(deviceNodeChild);
                } else if (Descriptor.Device.ELEMENT.manufacturerURL.equals(deviceNodeChild)) {
                    descriptor.manufacturerURI = parseURI(XMLUtil.getTextContent(deviceNodeChild));
                } else if (Descriptor.Device.ELEMENT.modelDescription.equals(deviceNodeChild)) {
                    descriptor.modelDescription = XMLUtil.getTextContent(deviceNodeChild);
                } else if (Descriptor.Device.ELEMENT.modelName.equals(deviceNodeChild)) {
                    descriptor.modelName = XMLUtil.getTextContent(deviceNodeChild);
                } else if (Descriptor.Device.ELEMENT.modelNumber.equals(deviceNodeChild)) {
                    descriptor.modelNumber = XMLUtil.getTextContent(deviceNodeChild);
                } else if (Descriptor.Device.ELEMENT.modelURL.equals(deviceNodeChild)) {
                    descriptor.modelURI = parseURI(XMLUtil.getTextContent(deviceNodeChild));
                } else if (Descriptor.Device.ELEMENT.presentationURL.equals(deviceNodeChild)) {
                    descriptor.presentationURI = parseURI(XMLUtil.getTextContent(deviceNodeChild));
                } else if (Descriptor.Device.ELEMENT.UPC.equals(deviceNodeChild)) {
                    descriptor.upc = XMLUtil.getTextContent(deviceNodeChild);
                } else if (Descriptor.Device.ELEMENT.serialNumber.equals(deviceNodeChild)) {
                    descriptor.serialNumber = XMLUtil.getTextContent(deviceNodeChild);
                } else if (Descriptor.Device.ELEMENT.UDN.equals(deviceNodeChild)) {
                    descriptor.udn = UDN.valueOf(XMLUtil.getTextContent(deviceNodeChild));
                } else if (Descriptor.Device.ELEMENT.iconList.equals(deviceNodeChild)) {
                    hydrateIconList(descriptor, deviceNodeChild);
                } else if (Descriptor.Device.ELEMENT.serviceList.equals(deviceNodeChild)) {
                    hydrateServiceList(descriptor, deviceNodeChild);
                } else if (Descriptor.Device.ELEMENT.deviceList.equals(deviceNodeChild)) {
                    hydrateDeviceList(descriptor, deviceNodeChild);
                } else if (Descriptor.Device.ELEMENT.X_DLNADOC.equals(deviceNodeChild) && Descriptor.Device.DLNA_PREFIX.equals(deviceNodeChild.getPrefix())) {
                    String txt = XMLUtil.getTextContent(deviceNodeChild);
                    try {
                        descriptor.dlnaDocs.add(DLNADoc.valueOf(txt));
                    } catch (InvalidValueException e) {
                        log.info("Invalid X_DLNADOC value, ignoring value: " + txt);
                    }
                } else if (Descriptor.Device.ELEMENT.X_DLNACAP.equals(deviceNodeChild) && Descriptor.Device.DLNA_PREFIX.equals(deviceNodeChild.getPrefix())) {
                    descriptor.dlnaCaps = DLNACaps.valueOf(XMLUtil.getTextContent(deviceNodeChild));
                }
            }
        }
    }

    public void hydrateIconList(MutableDevice descriptor, Node iconListNode) throws DescriptorBindingException {
        NodeList iconListNodeChildren = iconListNode.getChildNodes();
        for (int i = 0; i < iconListNodeChildren.getLength(); i++) {
            Node iconListNodeChild = iconListNodeChildren.item(i);
            if (iconListNodeChild.getNodeType() == 1 && Descriptor.Device.ELEMENT.icon.equals(iconListNodeChild)) {
                MutableIcon icon = new MutableIcon();
                NodeList iconChildren = iconListNodeChild.getChildNodes();
                for (int x = 0; x < iconChildren.getLength(); x++) {
                    Node iconChild = iconChildren.item(x);
                    if (iconChild.getNodeType() == 1) {
                        if (Descriptor.Device.ELEMENT.width.equals(iconChild)) {
                            icon.width = Integer.valueOf(XMLUtil.getTextContent(iconChild)).intValue();
                        } else if (Descriptor.Device.ELEMENT.height.equals(iconChild)) {
                            icon.height = Integer.valueOf(XMLUtil.getTextContent(iconChild)).intValue();
                        } else if (Descriptor.Device.ELEMENT.depth.equals(iconChild)) {
                            icon.depth = Integer.valueOf(XMLUtil.getTextContent(iconChild)).intValue();
                        } else if (Descriptor.Device.ELEMENT.url.equals(iconChild)) {
                            icon.uri = parseURI(XMLUtil.getTextContent(iconChild));
                        } else if (Descriptor.Device.ELEMENT.mimetype.equals(iconChild)) {
                            icon.mimeType = XMLUtil.getTextContent(iconChild);
                        }
                    }
                }
                descriptor.icons.add(icon);
            }
        }
    }

    public void hydrateServiceList(MutableDevice descriptor, Node serviceListNode) throws DescriptorBindingException {
        NodeList serviceListNodeChildren = serviceListNode.getChildNodes();
        for (int i = 0; i < serviceListNodeChildren.getLength(); i++) {
            Node serviceListNodeChild = serviceListNodeChildren.item(i);
            if (serviceListNodeChild.getNodeType() == 1 && Descriptor.Device.ELEMENT.service.equals(serviceListNodeChild)) {
                MutableService service = new MutableService();
                NodeList serviceChildren = serviceListNodeChild.getChildNodes();
                for (int x = 0; x < serviceChildren.getLength(); x++) {
                    Node serviceChild = serviceChildren.item(x);
                    if (serviceChild.getNodeType() == 1) {
                        if (Descriptor.Device.ELEMENT.serviceType.equals(serviceChild)) {
                            service.serviceType = ServiceType.valueOf(XMLUtil.getTextContent(serviceChild));
                        } else if (Descriptor.Device.ELEMENT.serviceId.equals(serviceChild)) {
                            service.serviceId = ServiceId.valueOf(XMLUtil.getTextContent(serviceChild));
                        } else if (Descriptor.Device.ELEMENT.SCPDURL.equals(serviceChild)) {
                            service.descriptorURI = parseURI(XMLUtil.getTextContent(serviceChild));
                        } else if (Descriptor.Device.ELEMENT.controlURL.equals(serviceChild)) {
                            service.controlURI = parseURI(XMLUtil.getTextContent(serviceChild));
                        } else if (Descriptor.Device.ELEMENT.eventSubURL.equals(serviceChild)) {
                            service.eventSubscriptionURI = parseURI(XMLUtil.getTextContent(serviceChild));
                        }
                    }
                }
                descriptor.services.add(service);
            }
        }
    }

    public void hydrateDeviceList(MutableDevice descriptor, Node deviceListNode) throws DescriptorBindingException {
        NodeList deviceListNodeChildren = deviceListNode.getChildNodes();
        for (int i = 0; i < deviceListNodeChildren.getLength(); i++) {
            Node deviceListNodeChild = deviceListNodeChildren.item(i);
            if (deviceListNodeChild.getNodeType() == 1 && Descriptor.Device.ELEMENT.device.equals(deviceListNodeChild)) {
                MutableDevice embeddedDevice = new MutableDevice();
                embeddedDevice.parentDevice = descriptor;
                descriptor.embeddedDevices.add(embeddedDevice);
                hydrateDevice(embeddedDevice, deviceListNodeChild);
            }
        }
    }

    @Override // org.teleal.cling.binding.xml.DeviceDescriptorBinder
    public String generate(Device deviceModel, ControlPointInfo info, Namespace namespace) throws DescriptorBindingException {
        try {
            log.fine("Generating XML descriptor from device model: " + deviceModel);
            return XMLUtil.documentToString(buildDOM(deviceModel, info, namespace));
        } catch (Exception ex) {
            throw new DescriptorBindingException("Could not build DOM: " + ex.getMessage(), ex);
        }
    }

    @Override // org.teleal.cling.binding.xml.DeviceDescriptorBinder
    public Document buildDOM(Device deviceModel, ControlPointInfo info, Namespace namespace) throws DescriptorBindingException {
        try {
            log.fine("Generating DOM from device model: " + deviceModel);
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            Document d = factory.newDocumentBuilder().newDocument();
            generateRoot(namespace, deviceModel, d, info);
            return d;
        } catch (Exception ex) {
            throw new DescriptorBindingException("Could not generate device descriptor: " + ex.getMessage(), ex);
        }
    }

    protected void generateRoot(Namespace namespace, Device deviceModel, Document descriptor, ControlPointInfo info) throws DOMException {
        Element rootElement = descriptor.createElementNS("urn:schemas-upnp-org:device-1-0", Descriptor.Device.ELEMENT.root.toString());
        descriptor.appendChild(rootElement);
        generateSpecVersion(namespace, deviceModel, descriptor, rootElement);
        generateDevice(namespace, deviceModel, descriptor, rootElement, info);
    }

    protected void generateSpecVersion(Namespace namespace, Device deviceModel, Document descriptor, Element rootElement) {
        Element specVersionElement = XMLUtil.appendNewElement(descriptor, rootElement, Descriptor.Device.ELEMENT.specVersion);
        XMLUtil.appendNewElementIfNotNull(descriptor, specVersionElement, Descriptor.Device.ELEMENT.major, Integer.valueOf(deviceModel.getVersion().getMajor()));
        XMLUtil.appendNewElementIfNotNull(descriptor, specVersionElement, Descriptor.Device.ELEMENT.minor, Integer.valueOf(deviceModel.getVersion().getMinor()));
    }

    protected void generateDevice(Namespace namespace, Device deviceModel, Document descriptor, Element rootElement, ControlPointInfo info) {
        Element deviceElement = XMLUtil.appendNewElement(descriptor, rootElement, Descriptor.Device.ELEMENT.device);
        XMLUtil.appendNewElementIfNotNull(descriptor, deviceElement, Descriptor.Device.ELEMENT.deviceType, deviceModel.getType());
        XMLUtil.appendNewElementIfNotNull(descriptor, deviceElement, Descriptor.Device.ELEMENT.UDN, deviceModel.getIdentity().getUdn());
        DeviceDetails deviceModelDetails = deviceModel.getDetails(info);
        XMLUtil.appendNewElementIfNotNull(descriptor, deviceElement, Descriptor.Device.ELEMENT.friendlyName, deviceModelDetails.getFriendlyName());
        if (deviceModelDetails.getManufacturerDetails() != null) {
            XMLUtil.appendNewElementIfNotNull(descriptor, deviceElement, Descriptor.Device.ELEMENT.manufacturer, deviceModelDetails.getManufacturerDetails().getManufacturer());
            XMLUtil.appendNewElementIfNotNull(descriptor, deviceElement, Descriptor.Device.ELEMENT.manufacturerURL, deviceModelDetails.getManufacturerDetails().getManufacturerURI());
        }
        if (deviceModelDetails.getModelDetails() != null) {
            XMLUtil.appendNewElementIfNotNull(descriptor, deviceElement, Descriptor.Device.ELEMENT.modelDescription, deviceModelDetails.getModelDetails().getModelDescription());
            XMLUtil.appendNewElementIfNotNull(descriptor, deviceElement, Descriptor.Device.ELEMENT.modelName, deviceModelDetails.getModelDetails().getModelName());
            XMLUtil.appendNewElementIfNotNull(descriptor, deviceElement, Descriptor.Device.ELEMENT.modelNumber, deviceModelDetails.getModelDetails().getModelNumber());
            XMLUtil.appendNewElementIfNotNull(descriptor, deviceElement, Descriptor.Device.ELEMENT.modelURL, deviceModelDetails.getModelDetails().getModelURI());
        }
        XMLUtil.appendNewElementIfNotNull(descriptor, deviceElement, Descriptor.Device.ELEMENT.serialNumber, deviceModelDetails.getSerialNumber());
        XMLUtil.appendNewElementIfNotNull(descriptor, deviceElement, Descriptor.Device.ELEMENT.presentationURL, deviceModelDetails.getPresentationURI());
        XMLUtil.appendNewElementIfNotNull(descriptor, deviceElement, Descriptor.Device.ELEMENT.UPC, deviceModelDetails.getUpc());
        if (deviceModelDetails.getDlnaDocs() != null) {
            for (DLNADoc dlnaDoc : deviceModelDetails.getDlnaDocs()) {
                XMLUtil.appendNewElementIfNotNull(descriptor, deviceElement, "dlna:" + Descriptor.Device.ELEMENT.X_DLNADOC, dlnaDoc, Descriptor.Device.DLNA_NAMESPACE_URI);
            }
        }
        XMLUtil.appendNewElementIfNotNull(descriptor, deviceElement, "dlna:" + Descriptor.Device.ELEMENT.X_DLNACAP, deviceModelDetails.getDlnaCaps(), Descriptor.Device.DLNA_NAMESPACE_URI);
        generateIconList(namespace, deviceModel, descriptor, deviceElement);
        generateServiceList(namespace, deviceModel, descriptor, deviceElement);
        generateDeviceList(namespace, deviceModel, descriptor, deviceElement, info);
    }

    protected void generateIconList(Namespace namespace, Device deviceModel, Document descriptor, Element deviceElement) {
        if (deviceModel.hasIcons()) {
            Element iconListElement = XMLUtil.appendNewElement(descriptor, deviceElement, Descriptor.Device.ELEMENT.iconList);
            for (Icon icon : deviceModel.getIcons()) {
                Element iconElement = XMLUtil.appendNewElement(descriptor, iconListElement, Descriptor.Device.ELEMENT.icon);
                XMLUtil.appendNewElementIfNotNull(descriptor, iconElement, Descriptor.Device.ELEMENT.mimetype, icon.getMimeType());
                XMLUtil.appendNewElementIfNotNull(descriptor, iconElement, Descriptor.Device.ELEMENT.width, Integer.valueOf(icon.getWidth()));
                XMLUtil.appendNewElementIfNotNull(descriptor, iconElement, Descriptor.Device.ELEMENT.height, Integer.valueOf(icon.getHeight()));
                XMLUtil.appendNewElementIfNotNull(descriptor, iconElement, Descriptor.Device.ELEMENT.depth, Integer.valueOf(icon.getDepth()));
                XMLUtil.appendNewElementIfNotNull(descriptor, iconElement, Descriptor.Device.ELEMENT.url, icon.getUri());
            }
        }
    }

    protected void generateServiceList(Namespace namespace, Device deviceModel, Document descriptor, Element deviceElement) {
        if (deviceModel.hasServices()) {
            Element serviceListElement = XMLUtil.appendNewElement(descriptor, deviceElement, Descriptor.Device.ELEMENT.serviceList);
            for (Service service : deviceModel.getServices()) {
                Element serviceElement = XMLUtil.appendNewElement(descriptor, serviceListElement, Descriptor.Device.ELEMENT.service);
                XMLUtil.appendNewElementIfNotNull(descriptor, serviceElement, Descriptor.Device.ELEMENT.serviceType, service.getServiceType());
                XMLUtil.appendNewElementIfNotNull(descriptor, serviceElement, Descriptor.Device.ELEMENT.serviceId, service.getServiceId());
                if (service instanceof RemoteService) {
                    RemoteService rs = (RemoteService) service;
                    XMLUtil.appendNewElementIfNotNull(descriptor, serviceElement, Descriptor.Device.ELEMENT.controlURL, rs.getControlURI());
                    XMLUtil.appendNewElementIfNotNull(descriptor, serviceElement, Descriptor.Device.ELEMENT.eventSubURL, rs.getEventSubscriptionURI());
                    XMLUtil.appendNewElementIfNotNull(descriptor, serviceElement, Descriptor.Device.ELEMENT.SCPDURL, rs.getDescriptorURI());
                } else if (service instanceof LocalService) {
                    LocalService ls = (LocalService) service;
                    XMLUtil.appendNewElementIfNotNull(descriptor, serviceElement, Descriptor.Device.ELEMENT.controlURL, namespace.getControlPath(ls));
                    XMLUtil.appendNewElementIfNotNull(descriptor, serviceElement, Descriptor.Device.ELEMENT.eventSubURL, namespace.getEventSubscriptionPath(ls));
                    XMLUtil.appendNewElementIfNotNull(descriptor, serviceElement, Descriptor.Device.ELEMENT.SCPDURL, namespace.getDescriptorPath(ls));
                }
            }
        }
    }

    protected void generateDeviceList(Namespace namespace, Device deviceModel, Document descriptor, Element deviceElement, ControlPointInfo info) {
        if (deviceModel.hasEmbeddedDevices()) {
            Element deviceListElement = XMLUtil.appendNewElement(descriptor, deviceElement, Descriptor.Device.ELEMENT.deviceList);
            for (Device device : deviceModel.getEmbeddedDevices()) {
                generateDevice(namespace, device, descriptor, deviceListElement, info);
            }
        }
    }

    protected static URI parseURI(String uri) {
        if (uri.startsWith("www.")) {
            uri = "http://" + uri;
        }
        try {
            return URI.create(uri);
        } catch (IllegalArgumentException ex) {
            log.fine("Illegal URI, trying with ./ prefix: " + Exceptions.unwrap(ex));
            try {
                return URI.create("./" + uri);
            } catch (IllegalArgumentException ex2) {
                log.warning("Illegal URI '" + uri + "', ignoring value: " + Exceptions.unwrap(ex2));
                return null;
            }
        }
    }
}
