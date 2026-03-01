package org.teleal.cling.protocol.sync;

import java.net.URI;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.teleal.cling.UpnpService;
import org.teleal.cling.binding.xml.DescriptorBindingException;
import org.teleal.cling.binding.xml.DeviceDescriptorBinder;
import org.teleal.cling.binding.xml.ServiceDescriptorBinder;
import org.teleal.cling.model.message.StreamRequestMessage;
import org.teleal.cling.model.message.StreamResponseMessage;
import org.teleal.cling.model.message.UpnpResponse;
import org.teleal.cling.model.message.header.ContentTypeHeader;
import org.teleal.cling.model.message.header.ServerHeader;
import org.teleal.cling.model.message.header.UpnpHeader;
import org.teleal.cling.model.meta.Icon;
import org.teleal.cling.model.meta.LocalDevice;
import org.teleal.cling.model.meta.LocalService;
import org.teleal.cling.model.profile.ControlPointInfo;
import org.teleal.cling.model.resource.DeviceDescriptorResource;
import org.teleal.cling.model.resource.IconResource;
import org.teleal.cling.model.resource.Resource;
import org.teleal.cling.model.resource.ServiceDescriptorResource;
import org.teleal.cling.protocol.ReceivingSync;
import org.teleal.common.util.Exceptions;

/* loaded from: classes.dex */
public class ReceivingRetrieval extends ReceivingSync<StreamRequestMessage, StreamResponseMessage> {
    private static final Logger log = Logger.getLogger(ReceivingRetrieval.class.getName());

    public ReceivingRetrieval(UpnpService upnpService, StreamRequestMessage inputMessage) {
        super(upnpService, inputMessage);
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.teleal.cling.protocol.ReceivingSync
    protected StreamResponseMessage executeSync() throws IllegalArgumentException {
        if (!((StreamRequestMessage) getInputMessage()).hasHostHeader()) {
            log.fine("Ignoring message, missing HOST header: " + getInputMessage());
            return new StreamResponseMessage(new UpnpResponse(UpnpResponse.Status.PRECONDITION_FAILED));
        }
        URI requestedURI = ((StreamRequestMessage) getInputMessage()).getOperation().getURI();
        Resource foundResource = getUpnpService().getRegistry().getResource(requestedURI);
        if (foundResource == null) {
            log.fine("No local resource found: " + getInputMessage());
            return null;
        }
        return createResponse(requestedURI, foundResource);
    }

    protected StreamResponseMessage createResponse(URI requestedURI, Resource resource) {
        StreamResponseMessage response;
        try {
            if (DeviceDescriptorResource.class.isAssignableFrom(resource.getClass())) {
                log.fine("Found local device matching relative request URI: " + requestedURI);
                LocalDevice device = (LocalDevice) resource.getModel();
                DeviceDescriptorBinder deviceDescriptorBinder = getUpnpService().getConfiguration().getDeviceDescriptorBinderUDA10();
                String deviceDescriptor = deviceDescriptorBinder.generate(device, createControlPointInfo(), getUpnpService().getConfiguration().getNamespace());
                response = new StreamResponseMessage(deviceDescriptor, new ContentTypeHeader(ContentTypeHeader.DEFAULT_CONTENT_TYPE));
            } else if (ServiceDescriptorResource.class.isAssignableFrom(resource.getClass())) {
                log.fine("Found local service matching relative request URI: " + requestedURI);
                LocalService service = (LocalService) resource.getModel();
                ServiceDescriptorBinder serviceDescriptorBinder = getUpnpService().getConfiguration().getServiceDescriptorBinderUDA10();
                String serviceDescriptor = serviceDescriptorBinder.generate(service);
                response = new StreamResponseMessage(serviceDescriptor, new ContentTypeHeader(ContentTypeHeader.DEFAULT_CONTENT_TYPE));
            } else if (IconResource.class.isAssignableFrom(resource.getClass())) {
                log.fine("Found local icon matching relative request URI: " + requestedURI);
                Icon icon = (Icon) resource.getModel();
                response = new StreamResponseMessage(icon.getData(), icon.getMimeType());
            } else {
                log.fine("Ignoring GET for found local resource: " + resource);
                return null;
            }
        } catch (DescriptorBindingException ex) {
            log.warning("Error generating requested device/service descriptor: " + ex.toString());
            log.log(Level.WARNING, "Exception root cause: ", Exceptions.unwrap(ex));
            response = new StreamResponseMessage(UpnpResponse.Status.INTERNAL_SERVER_ERROR);
        }
        response.getHeaders().add(UpnpHeader.Type.SERVER, new ServerHeader());
        return response;
    }

    /* JADX WARN: Multi-variable type inference failed */
    protected ControlPointInfo createControlPointInfo() {
        return new ControlPointInfo(((StreamRequestMessage) getInputMessage()).getHeaders());
    }
}
