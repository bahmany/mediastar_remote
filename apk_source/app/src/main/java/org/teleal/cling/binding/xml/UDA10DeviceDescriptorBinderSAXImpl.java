package org.teleal.cling.binding.xml;

import java.io.StringReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import org.teleal.cling.binding.staging.MutableDevice;
import org.teleal.cling.binding.staging.MutableIcon;
import org.teleal.cling.binding.staging.MutableService;
import org.teleal.cling.binding.staging.MutableUDAVersion;
import org.teleal.cling.binding.xml.Descriptor;
import org.teleal.cling.model.ValidationException;
import org.teleal.cling.model.meta.Device;
import org.teleal.cling.model.types.DLNACaps;
import org.teleal.cling.model.types.DLNADoc;
import org.teleal.cling.model.types.InvalidValueException;
import org.teleal.cling.model.types.ServiceId;
import org.teleal.cling.model.types.ServiceType;
import org.teleal.cling.model.types.UDN;
import org.teleal.common.xml.SAXParser;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/* loaded from: classes.dex */
public class UDA10DeviceDescriptorBinderSAXImpl extends UDA10DeviceDescriptorBinderImpl {
    private static Logger log = Logger.getLogger(DeviceDescriptorBinder.class.getName());

    @Override // org.teleal.cling.binding.xml.UDA10DeviceDescriptorBinderImpl, org.teleal.cling.binding.xml.DeviceDescriptorBinder
    public <D extends Device> D describe(D d, String str) throws ValidationException, DescriptorBindingException {
        if (str == null || str.length() == 0) {
            throw new DescriptorBindingException("Null or empty descriptor");
        }
        try {
            log.fine("Populating device from XML descriptor: " + d);
            SAXParser sAXParser = new SAXParser();
            MutableDevice mutableDevice = new MutableDevice();
            new RootHandler(mutableDevice, sAXParser);
            sAXParser.parse(new InputSource(new StringReader(str.trim())));
            return (D) mutableDevice.build(d);
        } catch (ValidationException e) {
            throw e;
        } catch (Exception e2) {
            throw new DescriptorBindingException("Could not parse device descriptor: " + e2.toString(), e2);
        }
    }

    protected static class RootHandler extends DeviceDescriptorHandler<MutableDevice> {
        private static /* synthetic */ int[] $SWITCH_TABLE$org$teleal$cling$binding$xml$Descriptor$Device$ELEMENT;

        static /* synthetic */ int[] $SWITCH_TABLE$org$teleal$cling$binding$xml$Descriptor$Device$ELEMENT() {
            int[] iArr = $SWITCH_TABLE$org$teleal$cling$binding$xml$Descriptor$Device$ELEMENT;
            if (iArr == null) {
                iArr = new int[Descriptor.Device.ELEMENT.valuesCustom().length];
                try {
                    iArr[Descriptor.Device.ELEMENT.SCPDURL.ordinal()] = 32;
                } catch (NoSuchFieldError e) {
                }
                try {
                    iArr[Descriptor.Device.ELEMENT.UDN.ordinal()] = 7;
                } catch (NoSuchFieldError e2) {
                }
                try {
                    iArr[Descriptor.Device.ELEMENT.UPC.ordinal()] = 19;
                } catch (NoSuchFieldError e3) {
                }
                try {
                    iArr[Descriptor.Device.ELEMENT.URLBase.ordinal()] = 5;
                } catch (NoSuchFieldError e4) {
                }
                try {
                    iArr[Descriptor.Device.ELEMENT.X_DLNACAP.ordinal()] = 9;
                } catch (NoSuchFieldError e5) {
                }
                try {
                    iArr[Descriptor.Device.ELEMENT.X_DLNADOC.ordinal()] = 8;
                } catch (NoSuchFieldError e6) {
                }
                try {
                    iArr[Descriptor.Device.ELEMENT.controlURL.ordinal()] = 33;
                } catch (NoSuchFieldError e7) {
                }
                try {
                    iArr[Descriptor.Device.ELEMENT.depth.ordinal()] = 25;
                } catch (NoSuchFieldError e8) {
                }
                try {
                    iArr[Descriptor.Device.ELEMENT.device.ordinal()] = 6;
                } catch (NoSuchFieldError e9) {
                }
                try {
                    iArr[Descriptor.Device.ELEMENT.deviceList.ordinal()] = 35;
                } catch (NoSuchFieldError e10) {
                }
                try {
                    iArr[Descriptor.Device.ELEMENT.deviceType.ordinal()] = 10;
                } catch (NoSuchFieldError e11) {
                }
                try {
                    iArr[Descriptor.Device.ELEMENT.eventSubURL.ordinal()] = 34;
                } catch (NoSuchFieldError e12) {
                }
                try {
                    iArr[Descriptor.Device.ELEMENT.friendlyName.ordinal()] = 11;
                } catch (NoSuchFieldError e13) {
                }
                try {
                    iArr[Descriptor.Device.ELEMENT.height.ordinal()] = 24;
                } catch (NoSuchFieldError e14) {
                }
                try {
                    iArr[Descriptor.Device.ELEMENT.icon.ordinal()] = 22;
                } catch (NoSuchFieldError e15) {
                }
                try {
                    iArr[Descriptor.Device.ELEMENT.iconList.ordinal()] = 21;
                } catch (NoSuchFieldError e16) {
                }
                try {
                    iArr[Descriptor.Device.ELEMENT.major.ordinal()] = 3;
                } catch (NoSuchFieldError e17) {
                }
                try {
                    iArr[Descriptor.Device.ELEMENT.manufacturer.ordinal()] = 12;
                } catch (NoSuchFieldError e18) {
                }
                try {
                    iArr[Descriptor.Device.ELEMENT.manufacturerURL.ordinal()] = 13;
                } catch (NoSuchFieldError e19) {
                }
                try {
                    iArr[Descriptor.Device.ELEMENT.mimetype.ordinal()] = 27;
                } catch (NoSuchFieldError e20) {
                }
                try {
                    iArr[Descriptor.Device.ELEMENT.minor.ordinal()] = 4;
                } catch (NoSuchFieldError e21) {
                }
                try {
                    iArr[Descriptor.Device.ELEMENT.modelDescription.ordinal()] = 14;
                } catch (NoSuchFieldError e22) {
                }
                try {
                    iArr[Descriptor.Device.ELEMENT.modelName.ordinal()] = 15;
                } catch (NoSuchFieldError e23) {
                }
                try {
                    iArr[Descriptor.Device.ELEMENT.modelNumber.ordinal()] = 16;
                } catch (NoSuchFieldError e24) {
                }
                try {
                    iArr[Descriptor.Device.ELEMENT.modelURL.ordinal()] = 17;
                } catch (NoSuchFieldError e25) {
                }
                try {
                    iArr[Descriptor.Device.ELEMENT.presentationURL.ordinal()] = 18;
                } catch (NoSuchFieldError e26) {
                }
                try {
                    iArr[Descriptor.Device.ELEMENT.root.ordinal()] = 1;
                } catch (NoSuchFieldError e27) {
                }
                try {
                    iArr[Descriptor.Device.ELEMENT.serialNumber.ordinal()] = 20;
                } catch (NoSuchFieldError e28) {
                }
                try {
                    iArr[Descriptor.Device.ELEMENT.service.ordinal()] = 29;
                } catch (NoSuchFieldError e29) {
                }
                try {
                    iArr[Descriptor.Device.ELEMENT.serviceId.ordinal()] = 31;
                } catch (NoSuchFieldError e30) {
                }
                try {
                    iArr[Descriptor.Device.ELEMENT.serviceList.ordinal()] = 28;
                } catch (NoSuchFieldError e31) {
                }
                try {
                    iArr[Descriptor.Device.ELEMENT.serviceType.ordinal()] = 30;
                } catch (NoSuchFieldError e32) {
                }
                try {
                    iArr[Descriptor.Device.ELEMENT.specVersion.ordinal()] = 2;
                } catch (NoSuchFieldError e33) {
                }
                try {
                    iArr[Descriptor.Device.ELEMENT.url.ordinal()] = 26;
                } catch (NoSuchFieldError e34) {
                }
                try {
                    iArr[Descriptor.Device.ELEMENT.width.ordinal()] = 23;
                } catch (NoSuchFieldError e35) {
                }
                $SWITCH_TABLE$org$teleal$cling$binding$xml$Descriptor$Device$ELEMENT = iArr;
            }
            return iArr;
        }

        public RootHandler(MutableDevice instance, SAXParser parser) {
            super(instance, parser);
        }

        @Override // org.teleal.cling.binding.xml.UDA10DeviceDescriptorBinderSAXImpl.DeviceDescriptorHandler
        public void startElement(Descriptor.Device.ELEMENT element, Attributes attributes) throws SAXException {
            if (element.equals(SpecVersionHandler.EL)) {
                MutableUDAVersion udaVersion = new MutableUDAVersion();
                ((MutableDevice) getInstance()).udaVersion = udaVersion;
                new SpecVersionHandler(udaVersion, this);
            }
            if (element.equals(DeviceHandler.EL)) {
                new DeviceHandler((MutableDevice) getInstance(), this);
            }
        }

        @Override // org.teleal.cling.binding.xml.UDA10DeviceDescriptorBinderSAXImpl.DeviceDescriptorHandler
        public void endElement(Descriptor.Device.ELEMENT element) throws SAXException {
            switch ($SWITCH_TABLE$org$teleal$cling$binding$xml$Descriptor$Device$ELEMENT()[element.ordinal()]) {
                case 5:
                    try {
                        ((MutableDevice) getInstance()).baseURL = new URL(getCharacters());
                        return;
                    } catch (Exception ex) {
                        throw new SAXException("Invalid URLBase: " + ex.toString());
                    }
                default:
                    return;
            }
        }
    }

    protected static class SpecVersionHandler extends DeviceDescriptorHandler<MutableUDAVersion> {
        private static /* synthetic */ int[] $SWITCH_TABLE$org$teleal$cling$binding$xml$Descriptor$Device$ELEMENT;
        public static final Descriptor.Device.ELEMENT EL = Descriptor.Device.ELEMENT.specVersion;

        static /* synthetic */ int[] $SWITCH_TABLE$org$teleal$cling$binding$xml$Descriptor$Device$ELEMENT() {
            int[] iArr = $SWITCH_TABLE$org$teleal$cling$binding$xml$Descriptor$Device$ELEMENT;
            if (iArr == null) {
                iArr = new int[Descriptor.Device.ELEMENT.valuesCustom().length];
                try {
                    iArr[Descriptor.Device.ELEMENT.SCPDURL.ordinal()] = 32;
                } catch (NoSuchFieldError e) {
                }
                try {
                    iArr[Descriptor.Device.ELEMENT.UDN.ordinal()] = 7;
                } catch (NoSuchFieldError e2) {
                }
                try {
                    iArr[Descriptor.Device.ELEMENT.UPC.ordinal()] = 19;
                } catch (NoSuchFieldError e3) {
                }
                try {
                    iArr[Descriptor.Device.ELEMENT.URLBase.ordinal()] = 5;
                } catch (NoSuchFieldError e4) {
                }
                try {
                    iArr[Descriptor.Device.ELEMENT.X_DLNACAP.ordinal()] = 9;
                } catch (NoSuchFieldError e5) {
                }
                try {
                    iArr[Descriptor.Device.ELEMENT.X_DLNADOC.ordinal()] = 8;
                } catch (NoSuchFieldError e6) {
                }
                try {
                    iArr[Descriptor.Device.ELEMENT.controlURL.ordinal()] = 33;
                } catch (NoSuchFieldError e7) {
                }
                try {
                    iArr[Descriptor.Device.ELEMENT.depth.ordinal()] = 25;
                } catch (NoSuchFieldError e8) {
                }
                try {
                    iArr[Descriptor.Device.ELEMENT.device.ordinal()] = 6;
                } catch (NoSuchFieldError e9) {
                }
                try {
                    iArr[Descriptor.Device.ELEMENT.deviceList.ordinal()] = 35;
                } catch (NoSuchFieldError e10) {
                }
                try {
                    iArr[Descriptor.Device.ELEMENT.deviceType.ordinal()] = 10;
                } catch (NoSuchFieldError e11) {
                }
                try {
                    iArr[Descriptor.Device.ELEMENT.eventSubURL.ordinal()] = 34;
                } catch (NoSuchFieldError e12) {
                }
                try {
                    iArr[Descriptor.Device.ELEMENT.friendlyName.ordinal()] = 11;
                } catch (NoSuchFieldError e13) {
                }
                try {
                    iArr[Descriptor.Device.ELEMENT.height.ordinal()] = 24;
                } catch (NoSuchFieldError e14) {
                }
                try {
                    iArr[Descriptor.Device.ELEMENT.icon.ordinal()] = 22;
                } catch (NoSuchFieldError e15) {
                }
                try {
                    iArr[Descriptor.Device.ELEMENT.iconList.ordinal()] = 21;
                } catch (NoSuchFieldError e16) {
                }
                try {
                    iArr[Descriptor.Device.ELEMENT.major.ordinal()] = 3;
                } catch (NoSuchFieldError e17) {
                }
                try {
                    iArr[Descriptor.Device.ELEMENT.manufacturer.ordinal()] = 12;
                } catch (NoSuchFieldError e18) {
                }
                try {
                    iArr[Descriptor.Device.ELEMENT.manufacturerURL.ordinal()] = 13;
                } catch (NoSuchFieldError e19) {
                }
                try {
                    iArr[Descriptor.Device.ELEMENT.mimetype.ordinal()] = 27;
                } catch (NoSuchFieldError e20) {
                }
                try {
                    iArr[Descriptor.Device.ELEMENT.minor.ordinal()] = 4;
                } catch (NoSuchFieldError e21) {
                }
                try {
                    iArr[Descriptor.Device.ELEMENT.modelDescription.ordinal()] = 14;
                } catch (NoSuchFieldError e22) {
                }
                try {
                    iArr[Descriptor.Device.ELEMENT.modelName.ordinal()] = 15;
                } catch (NoSuchFieldError e23) {
                }
                try {
                    iArr[Descriptor.Device.ELEMENT.modelNumber.ordinal()] = 16;
                } catch (NoSuchFieldError e24) {
                }
                try {
                    iArr[Descriptor.Device.ELEMENT.modelURL.ordinal()] = 17;
                } catch (NoSuchFieldError e25) {
                }
                try {
                    iArr[Descriptor.Device.ELEMENT.presentationURL.ordinal()] = 18;
                } catch (NoSuchFieldError e26) {
                }
                try {
                    iArr[Descriptor.Device.ELEMENT.root.ordinal()] = 1;
                } catch (NoSuchFieldError e27) {
                }
                try {
                    iArr[Descriptor.Device.ELEMENT.serialNumber.ordinal()] = 20;
                } catch (NoSuchFieldError e28) {
                }
                try {
                    iArr[Descriptor.Device.ELEMENT.service.ordinal()] = 29;
                } catch (NoSuchFieldError e29) {
                }
                try {
                    iArr[Descriptor.Device.ELEMENT.serviceId.ordinal()] = 31;
                } catch (NoSuchFieldError e30) {
                }
                try {
                    iArr[Descriptor.Device.ELEMENT.serviceList.ordinal()] = 28;
                } catch (NoSuchFieldError e31) {
                }
                try {
                    iArr[Descriptor.Device.ELEMENT.serviceType.ordinal()] = 30;
                } catch (NoSuchFieldError e32) {
                }
                try {
                    iArr[Descriptor.Device.ELEMENT.specVersion.ordinal()] = 2;
                } catch (NoSuchFieldError e33) {
                }
                try {
                    iArr[Descriptor.Device.ELEMENT.url.ordinal()] = 26;
                } catch (NoSuchFieldError e34) {
                }
                try {
                    iArr[Descriptor.Device.ELEMENT.width.ordinal()] = 23;
                } catch (NoSuchFieldError e35) {
                }
                $SWITCH_TABLE$org$teleal$cling$binding$xml$Descriptor$Device$ELEMENT = iArr;
            }
            return iArr;
        }

        public SpecVersionHandler(MutableUDAVersion instance, DeviceDescriptorHandler parent) {
            super(instance, parent);
        }

        @Override // org.teleal.cling.binding.xml.UDA10DeviceDescriptorBinderSAXImpl.DeviceDescriptorHandler
        public void endElement(Descriptor.Device.ELEMENT element) throws SAXException {
            switch ($SWITCH_TABLE$org$teleal$cling$binding$xml$Descriptor$Device$ELEMENT()[element.ordinal()]) {
                case 3:
                    ((MutableUDAVersion) getInstance()).major = Integer.valueOf(getCharacters()).intValue();
                    break;
                case 4:
                    ((MutableUDAVersion) getInstance()).minor = Integer.valueOf(getCharacters()).intValue();
                    break;
            }
        }

        @Override // org.teleal.cling.binding.xml.UDA10DeviceDescriptorBinderSAXImpl.DeviceDescriptorHandler
        public boolean isLastElement(Descriptor.Device.ELEMENT element) {
            return element.equals(EL);
        }
    }

    protected static class DeviceHandler extends DeviceDescriptorHandler<MutableDevice> {
        private static /* synthetic */ int[] $SWITCH_TABLE$org$teleal$cling$binding$xml$Descriptor$Device$ELEMENT;
        public static final Descriptor.Device.ELEMENT EL = Descriptor.Device.ELEMENT.device;

        static /* synthetic */ int[] $SWITCH_TABLE$org$teleal$cling$binding$xml$Descriptor$Device$ELEMENT() {
            int[] iArr = $SWITCH_TABLE$org$teleal$cling$binding$xml$Descriptor$Device$ELEMENT;
            if (iArr == null) {
                iArr = new int[Descriptor.Device.ELEMENT.valuesCustom().length];
                try {
                    iArr[Descriptor.Device.ELEMENT.SCPDURL.ordinal()] = 32;
                } catch (NoSuchFieldError e) {
                }
                try {
                    iArr[Descriptor.Device.ELEMENT.UDN.ordinal()] = 7;
                } catch (NoSuchFieldError e2) {
                }
                try {
                    iArr[Descriptor.Device.ELEMENT.UPC.ordinal()] = 19;
                } catch (NoSuchFieldError e3) {
                }
                try {
                    iArr[Descriptor.Device.ELEMENT.URLBase.ordinal()] = 5;
                } catch (NoSuchFieldError e4) {
                }
                try {
                    iArr[Descriptor.Device.ELEMENT.X_DLNACAP.ordinal()] = 9;
                } catch (NoSuchFieldError e5) {
                }
                try {
                    iArr[Descriptor.Device.ELEMENT.X_DLNADOC.ordinal()] = 8;
                } catch (NoSuchFieldError e6) {
                }
                try {
                    iArr[Descriptor.Device.ELEMENT.controlURL.ordinal()] = 33;
                } catch (NoSuchFieldError e7) {
                }
                try {
                    iArr[Descriptor.Device.ELEMENT.depth.ordinal()] = 25;
                } catch (NoSuchFieldError e8) {
                }
                try {
                    iArr[Descriptor.Device.ELEMENT.device.ordinal()] = 6;
                } catch (NoSuchFieldError e9) {
                }
                try {
                    iArr[Descriptor.Device.ELEMENT.deviceList.ordinal()] = 35;
                } catch (NoSuchFieldError e10) {
                }
                try {
                    iArr[Descriptor.Device.ELEMENT.deviceType.ordinal()] = 10;
                } catch (NoSuchFieldError e11) {
                }
                try {
                    iArr[Descriptor.Device.ELEMENT.eventSubURL.ordinal()] = 34;
                } catch (NoSuchFieldError e12) {
                }
                try {
                    iArr[Descriptor.Device.ELEMENT.friendlyName.ordinal()] = 11;
                } catch (NoSuchFieldError e13) {
                }
                try {
                    iArr[Descriptor.Device.ELEMENT.height.ordinal()] = 24;
                } catch (NoSuchFieldError e14) {
                }
                try {
                    iArr[Descriptor.Device.ELEMENT.icon.ordinal()] = 22;
                } catch (NoSuchFieldError e15) {
                }
                try {
                    iArr[Descriptor.Device.ELEMENT.iconList.ordinal()] = 21;
                } catch (NoSuchFieldError e16) {
                }
                try {
                    iArr[Descriptor.Device.ELEMENT.major.ordinal()] = 3;
                } catch (NoSuchFieldError e17) {
                }
                try {
                    iArr[Descriptor.Device.ELEMENT.manufacturer.ordinal()] = 12;
                } catch (NoSuchFieldError e18) {
                }
                try {
                    iArr[Descriptor.Device.ELEMENT.manufacturerURL.ordinal()] = 13;
                } catch (NoSuchFieldError e19) {
                }
                try {
                    iArr[Descriptor.Device.ELEMENT.mimetype.ordinal()] = 27;
                } catch (NoSuchFieldError e20) {
                }
                try {
                    iArr[Descriptor.Device.ELEMENT.minor.ordinal()] = 4;
                } catch (NoSuchFieldError e21) {
                }
                try {
                    iArr[Descriptor.Device.ELEMENT.modelDescription.ordinal()] = 14;
                } catch (NoSuchFieldError e22) {
                }
                try {
                    iArr[Descriptor.Device.ELEMENT.modelName.ordinal()] = 15;
                } catch (NoSuchFieldError e23) {
                }
                try {
                    iArr[Descriptor.Device.ELEMENT.modelNumber.ordinal()] = 16;
                } catch (NoSuchFieldError e24) {
                }
                try {
                    iArr[Descriptor.Device.ELEMENT.modelURL.ordinal()] = 17;
                } catch (NoSuchFieldError e25) {
                }
                try {
                    iArr[Descriptor.Device.ELEMENT.presentationURL.ordinal()] = 18;
                } catch (NoSuchFieldError e26) {
                }
                try {
                    iArr[Descriptor.Device.ELEMENT.root.ordinal()] = 1;
                } catch (NoSuchFieldError e27) {
                }
                try {
                    iArr[Descriptor.Device.ELEMENT.serialNumber.ordinal()] = 20;
                } catch (NoSuchFieldError e28) {
                }
                try {
                    iArr[Descriptor.Device.ELEMENT.service.ordinal()] = 29;
                } catch (NoSuchFieldError e29) {
                }
                try {
                    iArr[Descriptor.Device.ELEMENT.serviceId.ordinal()] = 31;
                } catch (NoSuchFieldError e30) {
                }
                try {
                    iArr[Descriptor.Device.ELEMENT.serviceList.ordinal()] = 28;
                } catch (NoSuchFieldError e31) {
                }
                try {
                    iArr[Descriptor.Device.ELEMENT.serviceType.ordinal()] = 30;
                } catch (NoSuchFieldError e32) {
                }
                try {
                    iArr[Descriptor.Device.ELEMENT.specVersion.ordinal()] = 2;
                } catch (NoSuchFieldError e33) {
                }
                try {
                    iArr[Descriptor.Device.ELEMENT.url.ordinal()] = 26;
                } catch (NoSuchFieldError e34) {
                }
                try {
                    iArr[Descriptor.Device.ELEMENT.width.ordinal()] = 23;
                } catch (NoSuchFieldError e35) {
                }
                $SWITCH_TABLE$org$teleal$cling$binding$xml$Descriptor$Device$ELEMENT = iArr;
            }
            return iArr;
        }

        public DeviceHandler(MutableDevice instance, DeviceDescriptorHandler parent) {
            super(instance, parent);
        }

        @Override // org.teleal.cling.binding.xml.UDA10DeviceDescriptorBinderSAXImpl.DeviceDescriptorHandler
        public void startElement(Descriptor.Device.ELEMENT element, Attributes attributes) throws SAXException {
            if (element.equals(IconListHandler.EL)) {
                List<MutableIcon> icons = new ArrayList<>();
                ((MutableDevice) getInstance()).icons = icons;
                new IconListHandler(icons, this);
            }
            if (element.equals(ServiceListHandler.EL)) {
                List<MutableService> services = new ArrayList<>();
                ((MutableDevice) getInstance()).services = services;
                new ServiceListHandler(services, this);
            }
            if (element.equals(DeviceListHandler.EL)) {
                List<MutableDevice> devices = new ArrayList<>();
                ((MutableDevice) getInstance()).embeddedDevices = devices;
                new DeviceListHandler(devices, this);
            }
        }

        @Override // org.teleal.cling.binding.xml.UDA10DeviceDescriptorBinderSAXImpl.DeviceDescriptorHandler
        public void endElement(Descriptor.Device.ELEMENT element) throws SAXException {
            switch ($SWITCH_TABLE$org$teleal$cling$binding$xml$Descriptor$Device$ELEMENT()[element.ordinal()]) {
                case 7:
                    ((MutableDevice) getInstance()).udn = UDN.valueOf(getCharacters());
                    break;
                case 8:
                    String txt = getCharacters();
                    try {
                        ((MutableDevice) getInstance()).dlnaDocs.add(DLNADoc.valueOf(txt));
                        break;
                    } catch (InvalidValueException e) {
                        UDA10DeviceDescriptorBinderSAXImpl.log.info("Invalid X_DLNADOC value, ignoring value: " + txt);
                        return;
                    }
                case 9:
                    ((MutableDevice) getInstance()).dlnaCaps = DLNACaps.valueOf(getCharacters());
                    break;
                case 10:
                    ((MutableDevice) getInstance()).deviceType = getCharacters();
                    break;
                case 11:
                    ((MutableDevice) getInstance()).friendlyName = getCharacters();
                    break;
                case 12:
                    ((MutableDevice) getInstance()).manufacturer = getCharacters();
                    break;
                case 13:
                    ((MutableDevice) getInstance()).manufacturerURI = UDA10DeviceDescriptorBinderSAXImpl.parseURI(getCharacters());
                    break;
                case 14:
                    ((MutableDevice) getInstance()).modelDescription = getCharacters();
                    break;
                case 15:
                    ((MutableDevice) getInstance()).modelName = getCharacters();
                    break;
                case 16:
                    ((MutableDevice) getInstance()).modelNumber = getCharacters();
                    break;
                case 17:
                    ((MutableDevice) getInstance()).modelURI = UDA10DeviceDescriptorBinderSAXImpl.parseURI(getCharacters());
                    break;
                case 18:
                    ((MutableDevice) getInstance()).presentationURI = UDA10DeviceDescriptorBinderSAXImpl.parseURI(getCharacters());
                    break;
                case 19:
                    ((MutableDevice) getInstance()).upc = getCharacters();
                    break;
                case 20:
                    ((MutableDevice) getInstance()).serialNumber = getCharacters();
                    break;
            }
        }

        @Override // org.teleal.cling.binding.xml.UDA10DeviceDescriptorBinderSAXImpl.DeviceDescriptorHandler
        public boolean isLastElement(Descriptor.Device.ELEMENT element) {
            return element.equals(EL);
        }
    }

    protected static class IconListHandler extends DeviceDescriptorHandler<List<MutableIcon>> {
        public static final Descriptor.Device.ELEMENT EL = Descriptor.Device.ELEMENT.iconList;

        public IconListHandler(List<MutableIcon> instance, DeviceDescriptorHandler parent) {
            super(instance, parent);
        }

        @Override // org.teleal.cling.binding.xml.UDA10DeviceDescriptorBinderSAXImpl.DeviceDescriptorHandler
        public void startElement(Descriptor.Device.ELEMENT element, Attributes attributes) throws SAXException {
            if (element.equals(IconHandler.EL)) {
                MutableIcon icon = new MutableIcon();
                ((List) getInstance()).add(icon);
                new IconHandler(icon, this);
            }
        }

        @Override // org.teleal.cling.binding.xml.UDA10DeviceDescriptorBinderSAXImpl.DeviceDescriptorHandler
        public boolean isLastElement(Descriptor.Device.ELEMENT element) {
            return element.equals(EL);
        }
    }

    protected static class IconHandler extends DeviceDescriptorHandler<MutableIcon> {
        private static /* synthetic */ int[] $SWITCH_TABLE$org$teleal$cling$binding$xml$Descriptor$Device$ELEMENT;
        public static final Descriptor.Device.ELEMENT EL = Descriptor.Device.ELEMENT.icon;

        static /* synthetic */ int[] $SWITCH_TABLE$org$teleal$cling$binding$xml$Descriptor$Device$ELEMENT() {
            int[] iArr = $SWITCH_TABLE$org$teleal$cling$binding$xml$Descriptor$Device$ELEMENT;
            if (iArr == null) {
                iArr = new int[Descriptor.Device.ELEMENT.valuesCustom().length];
                try {
                    iArr[Descriptor.Device.ELEMENT.SCPDURL.ordinal()] = 32;
                } catch (NoSuchFieldError e) {
                }
                try {
                    iArr[Descriptor.Device.ELEMENT.UDN.ordinal()] = 7;
                } catch (NoSuchFieldError e2) {
                }
                try {
                    iArr[Descriptor.Device.ELEMENT.UPC.ordinal()] = 19;
                } catch (NoSuchFieldError e3) {
                }
                try {
                    iArr[Descriptor.Device.ELEMENT.URLBase.ordinal()] = 5;
                } catch (NoSuchFieldError e4) {
                }
                try {
                    iArr[Descriptor.Device.ELEMENT.X_DLNACAP.ordinal()] = 9;
                } catch (NoSuchFieldError e5) {
                }
                try {
                    iArr[Descriptor.Device.ELEMENT.X_DLNADOC.ordinal()] = 8;
                } catch (NoSuchFieldError e6) {
                }
                try {
                    iArr[Descriptor.Device.ELEMENT.controlURL.ordinal()] = 33;
                } catch (NoSuchFieldError e7) {
                }
                try {
                    iArr[Descriptor.Device.ELEMENT.depth.ordinal()] = 25;
                } catch (NoSuchFieldError e8) {
                }
                try {
                    iArr[Descriptor.Device.ELEMENT.device.ordinal()] = 6;
                } catch (NoSuchFieldError e9) {
                }
                try {
                    iArr[Descriptor.Device.ELEMENT.deviceList.ordinal()] = 35;
                } catch (NoSuchFieldError e10) {
                }
                try {
                    iArr[Descriptor.Device.ELEMENT.deviceType.ordinal()] = 10;
                } catch (NoSuchFieldError e11) {
                }
                try {
                    iArr[Descriptor.Device.ELEMENT.eventSubURL.ordinal()] = 34;
                } catch (NoSuchFieldError e12) {
                }
                try {
                    iArr[Descriptor.Device.ELEMENT.friendlyName.ordinal()] = 11;
                } catch (NoSuchFieldError e13) {
                }
                try {
                    iArr[Descriptor.Device.ELEMENT.height.ordinal()] = 24;
                } catch (NoSuchFieldError e14) {
                }
                try {
                    iArr[Descriptor.Device.ELEMENT.icon.ordinal()] = 22;
                } catch (NoSuchFieldError e15) {
                }
                try {
                    iArr[Descriptor.Device.ELEMENT.iconList.ordinal()] = 21;
                } catch (NoSuchFieldError e16) {
                }
                try {
                    iArr[Descriptor.Device.ELEMENT.major.ordinal()] = 3;
                } catch (NoSuchFieldError e17) {
                }
                try {
                    iArr[Descriptor.Device.ELEMENT.manufacturer.ordinal()] = 12;
                } catch (NoSuchFieldError e18) {
                }
                try {
                    iArr[Descriptor.Device.ELEMENT.manufacturerURL.ordinal()] = 13;
                } catch (NoSuchFieldError e19) {
                }
                try {
                    iArr[Descriptor.Device.ELEMENT.mimetype.ordinal()] = 27;
                } catch (NoSuchFieldError e20) {
                }
                try {
                    iArr[Descriptor.Device.ELEMENT.minor.ordinal()] = 4;
                } catch (NoSuchFieldError e21) {
                }
                try {
                    iArr[Descriptor.Device.ELEMENT.modelDescription.ordinal()] = 14;
                } catch (NoSuchFieldError e22) {
                }
                try {
                    iArr[Descriptor.Device.ELEMENT.modelName.ordinal()] = 15;
                } catch (NoSuchFieldError e23) {
                }
                try {
                    iArr[Descriptor.Device.ELEMENT.modelNumber.ordinal()] = 16;
                } catch (NoSuchFieldError e24) {
                }
                try {
                    iArr[Descriptor.Device.ELEMENT.modelURL.ordinal()] = 17;
                } catch (NoSuchFieldError e25) {
                }
                try {
                    iArr[Descriptor.Device.ELEMENT.presentationURL.ordinal()] = 18;
                } catch (NoSuchFieldError e26) {
                }
                try {
                    iArr[Descriptor.Device.ELEMENT.root.ordinal()] = 1;
                } catch (NoSuchFieldError e27) {
                }
                try {
                    iArr[Descriptor.Device.ELEMENT.serialNumber.ordinal()] = 20;
                } catch (NoSuchFieldError e28) {
                }
                try {
                    iArr[Descriptor.Device.ELEMENT.service.ordinal()] = 29;
                } catch (NoSuchFieldError e29) {
                }
                try {
                    iArr[Descriptor.Device.ELEMENT.serviceId.ordinal()] = 31;
                } catch (NoSuchFieldError e30) {
                }
                try {
                    iArr[Descriptor.Device.ELEMENT.serviceList.ordinal()] = 28;
                } catch (NoSuchFieldError e31) {
                }
                try {
                    iArr[Descriptor.Device.ELEMENT.serviceType.ordinal()] = 30;
                } catch (NoSuchFieldError e32) {
                }
                try {
                    iArr[Descriptor.Device.ELEMENT.specVersion.ordinal()] = 2;
                } catch (NoSuchFieldError e33) {
                }
                try {
                    iArr[Descriptor.Device.ELEMENT.url.ordinal()] = 26;
                } catch (NoSuchFieldError e34) {
                }
                try {
                    iArr[Descriptor.Device.ELEMENT.width.ordinal()] = 23;
                } catch (NoSuchFieldError e35) {
                }
                $SWITCH_TABLE$org$teleal$cling$binding$xml$Descriptor$Device$ELEMENT = iArr;
            }
            return iArr;
        }

        public IconHandler(MutableIcon instance, DeviceDescriptorHandler parent) {
            super(instance, parent);
        }

        @Override // org.teleal.cling.binding.xml.UDA10DeviceDescriptorBinderSAXImpl.DeviceDescriptorHandler
        public void endElement(Descriptor.Device.ELEMENT element) throws SAXException {
            switch ($SWITCH_TABLE$org$teleal$cling$binding$xml$Descriptor$Device$ELEMENT()[element.ordinal()]) {
                case 23:
                    ((MutableIcon) getInstance()).width = Integer.valueOf(getCharacters()).intValue();
                    break;
                case 24:
                    ((MutableIcon) getInstance()).height = Integer.valueOf(getCharacters()).intValue();
                    break;
                case 25:
                    ((MutableIcon) getInstance()).depth = Integer.valueOf(getCharacters()).intValue();
                    break;
                case 26:
                    ((MutableIcon) getInstance()).uri = UDA10DeviceDescriptorBinderSAXImpl.parseURI(getCharacters());
                    break;
                case 27:
                    ((MutableIcon) getInstance()).mimeType = getCharacters();
                    break;
            }
        }

        @Override // org.teleal.cling.binding.xml.UDA10DeviceDescriptorBinderSAXImpl.DeviceDescriptorHandler
        public boolean isLastElement(Descriptor.Device.ELEMENT element) {
            return element.equals(EL);
        }
    }

    protected static class ServiceListHandler extends DeviceDescriptorHandler<List<MutableService>> {
        public static final Descriptor.Device.ELEMENT EL = Descriptor.Device.ELEMENT.serviceList;

        public ServiceListHandler(List<MutableService> instance, DeviceDescriptorHandler parent) {
            super(instance, parent);
        }

        @Override // org.teleal.cling.binding.xml.UDA10DeviceDescriptorBinderSAXImpl.DeviceDescriptorHandler
        public void startElement(Descriptor.Device.ELEMENT element, Attributes attributes) throws SAXException {
            if (element.equals(ServiceHandler.EL)) {
                MutableService service = new MutableService();
                ((List) getInstance()).add(service);
                new ServiceHandler(service, this);
            }
        }

        @Override // org.teleal.cling.binding.xml.UDA10DeviceDescriptorBinderSAXImpl.DeviceDescriptorHandler
        public boolean isLastElement(Descriptor.Device.ELEMENT element) {
            return element.equals(EL);
        }
    }

    protected static class ServiceHandler extends DeviceDescriptorHandler<MutableService> {
        private static /* synthetic */ int[] $SWITCH_TABLE$org$teleal$cling$binding$xml$Descriptor$Device$ELEMENT;
        public static final Descriptor.Device.ELEMENT EL = Descriptor.Device.ELEMENT.service;

        static /* synthetic */ int[] $SWITCH_TABLE$org$teleal$cling$binding$xml$Descriptor$Device$ELEMENT() {
            int[] iArr = $SWITCH_TABLE$org$teleal$cling$binding$xml$Descriptor$Device$ELEMENT;
            if (iArr == null) {
                iArr = new int[Descriptor.Device.ELEMENT.valuesCustom().length];
                try {
                    iArr[Descriptor.Device.ELEMENT.SCPDURL.ordinal()] = 32;
                } catch (NoSuchFieldError e) {
                }
                try {
                    iArr[Descriptor.Device.ELEMENT.UDN.ordinal()] = 7;
                } catch (NoSuchFieldError e2) {
                }
                try {
                    iArr[Descriptor.Device.ELEMENT.UPC.ordinal()] = 19;
                } catch (NoSuchFieldError e3) {
                }
                try {
                    iArr[Descriptor.Device.ELEMENT.URLBase.ordinal()] = 5;
                } catch (NoSuchFieldError e4) {
                }
                try {
                    iArr[Descriptor.Device.ELEMENT.X_DLNACAP.ordinal()] = 9;
                } catch (NoSuchFieldError e5) {
                }
                try {
                    iArr[Descriptor.Device.ELEMENT.X_DLNADOC.ordinal()] = 8;
                } catch (NoSuchFieldError e6) {
                }
                try {
                    iArr[Descriptor.Device.ELEMENT.controlURL.ordinal()] = 33;
                } catch (NoSuchFieldError e7) {
                }
                try {
                    iArr[Descriptor.Device.ELEMENT.depth.ordinal()] = 25;
                } catch (NoSuchFieldError e8) {
                }
                try {
                    iArr[Descriptor.Device.ELEMENT.device.ordinal()] = 6;
                } catch (NoSuchFieldError e9) {
                }
                try {
                    iArr[Descriptor.Device.ELEMENT.deviceList.ordinal()] = 35;
                } catch (NoSuchFieldError e10) {
                }
                try {
                    iArr[Descriptor.Device.ELEMENT.deviceType.ordinal()] = 10;
                } catch (NoSuchFieldError e11) {
                }
                try {
                    iArr[Descriptor.Device.ELEMENT.eventSubURL.ordinal()] = 34;
                } catch (NoSuchFieldError e12) {
                }
                try {
                    iArr[Descriptor.Device.ELEMENT.friendlyName.ordinal()] = 11;
                } catch (NoSuchFieldError e13) {
                }
                try {
                    iArr[Descriptor.Device.ELEMENT.height.ordinal()] = 24;
                } catch (NoSuchFieldError e14) {
                }
                try {
                    iArr[Descriptor.Device.ELEMENT.icon.ordinal()] = 22;
                } catch (NoSuchFieldError e15) {
                }
                try {
                    iArr[Descriptor.Device.ELEMENT.iconList.ordinal()] = 21;
                } catch (NoSuchFieldError e16) {
                }
                try {
                    iArr[Descriptor.Device.ELEMENT.major.ordinal()] = 3;
                } catch (NoSuchFieldError e17) {
                }
                try {
                    iArr[Descriptor.Device.ELEMENT.manufacturer.ordinal()] = 12;
                } catch (NoSuchFieldError e18) {
                }
                try {
                    iArr[Descriptor.Device.ELEMENT.manufacturerURL.ordinal()] = 13;
                } catch (NoSuchFieldError e19) {
                }
                try {
                    iArr[Descriptor.Device.ELEMENT.mimetype.ordinal()] = 27;
                } catch (NoSuchFieldError e20) {
                }
                try {
                    iArr[Descriptor.Device.ELEMENT.minor.ordinal()] = 4;
                } catch (NoSuchFieldError e21) {
                }
                try {
                    iArr[Descriptor.Device.ELEMENT.modelDescription.ordinal()] = 14;
                } catch (NoSuchFieldError e22) {
                }
                try {
                    iArr[Descriptor.Device.ELEMENT.modelName.ordinal()] = 15;
                } catch (NoSuchFieldError e23) {
                }
                try {
                    iArr[Descriptor.Device.ELEMENT.modelNumber.ordinal()] = 16;
                } catch (NoSuchFieldError e24) {
                }
                try {
                    iArr[Descriptor.Device.ELEMENT.modelURL.ordinal()] = 17;
                } catch (NoSuchFieldError e25) {
                }
                try {
                    iArr[Descriptor.Device.ELEMENT.presentationURL.ordinal()] = 18;
                } catch (NoSuchFieldError e26) {
                }
                try {
                    iArr[Descriptor.Device.ELEMENT.root.ordinal()] = 1;
                } catch (NoSuchFieldError e27) {
                }
                try {
                    iArr[Descriptor.Device.ELEMENT.serialNumber.ordinal()] = 20;
                } catch (NoSuchFieldError e28) {
                }
                try {
                    iArr[Descriptor.Device.ELEMENT.service.ordinal()] = 29;
                } catch (NoSuchFieldError e29) {
                }
                try {
                    iArr[Descriptor.Device.ELEMENT.serviceId.ordinal()] = 31;
                } catch (NoSuchFieldError e30) {
                }
                try {
                    iArr[Descriptor.Device.ELEMENT.serviceList.ordinal()] = 28;
                } catch (NoSuchFieldError e31) {
                }
                try {
                    iArr[Descriptor.Device.ELEMENT.serviceType.ordinal()] = 30;
                } catch (NoSuchFieldError e32) {
                }
                try {
                    iArr[Descriptor.Device.ELEMENT.specVersion.ordinal()] = 2;
                } catch (NoSuchFieldError e33) {
                }
                try {
                    iArr[Descriptor.Device.ELEMENT.url.ordinal()] = 26;
                } catch (NoSuchFieldError e34) {
                }
                try {
                    iArr[Descriptor.Device.ELEMENT.width.ordinal()] = 23;
                } catch (NoSuchFieldError e35) {
                }
                $SWITCH_TABLE$org$teleal$cling$binding$xml$Descriptor$Device$ELEMENT = iArr;
            }
            return iArr;
        }

        public ServiceHandler(MutableService instance, DeviceDescriptorHandler parent) {
            super(instance, parent);
        }

        @Override // org.teleal.cling.binding.xml.UDA10DeviceDescriptorBinderSAXImpl.DeviceDescriptorHandler
        public void endElement(Descriptor.Device.ELEMENT element) throws SAXException {
            switch ($SWITCH_TABLE$org$teleal$cling$binding$xml$Descriptor$Device$ELEMENT()[element.ordinal()]) {
                case 30:
                    ((MutableService) getInstance()).serviceType = ServiceType.valueOf(getCharacters());
                    break;
                case 31:
                    ((MutableService) getInstance()).serviceId = ServiceId.valueOf(getCharacters());
                    break;
                case 32:
                    ((MutableService) getInstance()).descriptorURI = UDA10DeviceDescriptorBinderSAXImpl.parseURI(getCharacters());
                    break;
                case 33:
                    ((MutableService) getInstance()).controlURI = UDA10DeviceDescriptorBinderSAXImpl.parseURI(getCharacters());
                    break;
                case 34:
                    ((MutableService) getInstance()).eventSubscriptionURI = UDA10DeviceDescriptorBinderSAXImpl.parseURI(getCharacters());
                    break;
            }
        }

        @Override // org.teleal.cling.binding.xml.UDA10DeviceDescriptorBinderSAXImpl.DeviceDescriptorHandler
        public boolean isLastElement(Descriptor.Device.ELEMENT element) {
            return element.equals(EL);
        }
    }

    protected static class DeviceListHandler extends DeviceDescriptorHandler<List<MutableDevice>> {
        public static final Descriptor.Device.ELEMENT EL = Descriptor.Device.ELEMENT.deviceList;

        public DeviceListHandler(List<MutableDevice> instance, DeviceDescriptorHandler parent) {
            super(instance, parent);
        }

        @Override // org.teleal.cling.binding.xml.UDA10DeviceDescriptorBinderSAXImpl.DeviceDescriptorHandler
        public void startElement(Descriptor.Device.ELEMENT element, Attributes attributes) throws SAXException {
            if (element.equals(DeviceHandler.EL)) {
                MutableDevice device = new MutableDevice();
                ((List) getInstance()).add(device);
                new DeviceHandler(device, this);
            }
        }

        @Override // org.teleal.cling.binding.xml.UDA10DeviceDescriptorBinderSAXImpl.DeviceDescriptorHandler
        public boolean isLastElement(Descriptor.Device.ELEMENT element) {
            return element.equals(EL);
        }
    }

    protected static class DeviceDescriptorHandler<I> extends SAXParser.Handler<I> {
        public DeviceDescriptorHandler(I instance) {
            super(instance);
        }

        public DeviceDescriptorHandler(I instance, SAXParser parser) {
            super(instance, parser);
        }

        public DeviceDescriptorHandler(I instance, DeviceDescriptorHandler parent) {
            super(instance, parent);
        }

        public DeviceDescriptorHandler(I instance, SAXParser parser, DeviceDescriptorHandler parent) {
            super(instance, parser, parent);
        }

        public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
            super.startElement(uri, localName, qName, attributes);
            Descriptor.Device.ELEMENT el = Descriptor.Device.ELEMENT.valueOrNullOf(localName);
            if (el != null) {
                startElement(el, attributes);
            }
        }

        public void endElement(String uri, String localName, String qName) throws SAXException {
            super.endElement(uri, localName, qName);
            Descriptor.Device.ELEMENT el = Descriptor.Device.ELEMENT.valueOrNullOf(localName);
            if (el != null) {
                endElement(el);
            }
        }

        protected boolean isLastElement(String uri, String localName, String qName) {
            Descriptor.Device.ELEMENT el = Descriptor.Device.ELEMENT.valueOrNullOf(localName);
            return el != null && isLastElement(el);
        }

        public void startElement(Descriptor.Device.ELEMENT element, Attributes attributes) throws SAXException {
        }

        public void endElement(Descriptor.Device.ELEMENT element) throws SAXException {
        }

        public boolean isLastElement(Descriptor.Device.ELEMENT element) {
            return false;
        }
    }
}
