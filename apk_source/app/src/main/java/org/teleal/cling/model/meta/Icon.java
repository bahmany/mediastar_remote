package org.teleal.cling.model.meta;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import org.teleal.cling.model.Validatable;
import org.teleal.cling.model.ValidationError;
import org.teleal.cling.model.types.BinHexDatatype;
import org.teleal.common.io.IO;
import org.teleal.common.util.ByteArray;
import org.teleal.common.util.MimeType;
import org.teleal.common.util.URIUtil;

/* loaded from: classes.dex */
public class Icon implements Validatable {
    private static final Logger log = Logger.getLogger(StateVariable.class.getName());
    private final byte[] data;
    private final int depth;
    private Device device;
    private final int height;
    private final MimeType mimeType;
    private final URI uri;
    private final int width;

    public Icon(String mimeType, int width, int height, int depth, String uri) throws IllegalArgumentException {
        this(mimeType, width, height, depth, URI.create(uri), "");
    }

    public Icon(String mimeType, int width, int height, int depth, URI uri) {
        this(mimeType, width, height, depth, uri, "");
    }

    public Icon(String mimeType, int width, int height, int depth, URI uri, String data) {
        this(mimeType, width, height, depth, uri, (data == null || data.equals("")) ? null : ByteArray.toPrimitive(new BinHexDatatype().valueOf(data)));
    }

    public Icon(String mimeType, int width, int height, int depth, URL url) throws IOException {
        this(mimeType, width, height, depth, new File(URIUtil.toURI(url)));
    }

    public Icon(String mimeType, int width, int height, int depth, URI uri, InputStream is) throws IOException {
        this(mimeType, width, height, depth, uri, IO.readBytes(is));
    }

    public Icon(String mimeType, int width, int height, int depth, File file) throws IOException {
        this(mimeType, width, height, depth, URI.create(file.getName()), IO.readBytes(file));
    }

    public Icon(String mimeType, int width, int height, int depth, URI uri, byte[] data) {
        this((mimeType == null || mimeType.length() <= 0) ? null : MimeType.valueOf(mimeType), width, height, depth, uri, data);
    }

    public Icon(MimeType mimeType, int width, int height, int depth, URI uri, byte[] data) {
        this.mimeType = mimeType;
        this.width = width;
        this.height = height;
        this.depth = depth;
        this.uri = uri;
        this.data = data;
    }

    public MimeType getMimeType() {
        return this.mimeType;
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }

    public int getDepth() {
        return this.depth;
    }

    public URI getUri() {
        return this.uri;
    }

    public byte[] getData() {
        return this.data;
    }

    public Device getDevice() {
        return this.device;
    }

    void setDevice(Device device) {
        if (this.device != null) {
            throw new IllegalStateException("Final value has been set already, model is immutable");
        }
        this.device = device;
    }

    @Override // org.teleal.cling.model.Validatable
    public List<ValidationError> validate() throws MalformedURLException {
        List<ValidationError> errors = new ArrayList<>();
        if (getMimeType() == null) {
            log.warning("UPnP specification violation of: " + getDevice());
            log.warning("Invalid icon, missing mime type: " + this);
        }
        if (getWidth() == 0) {
            log.warning("UPnP specification violation of: " + getDevice());
            log.warning("Invalid icon, missing width: " + this);
        }
        if (getHeight() == 0) {
            log.warning("UPnP specification violation of: " + getDevice());
            log.warning("Invalid icon, missing height: " + this);
        }
        if (getDepth() == 0) {
            log.warning("UPnP specification violation of: " + getDevice());
            log.warning("Invalid icon, missing bitmap depth: " + this);
        }
        if (getUri() == null) {
            errors.add(new ValidationError(getClass(), "uri", "URL is required"));
        }
        try {
            URL testURI = getUri().toURL();
            if (testURI == null) {
                throw new MalformedURLException();
            }
        } catch (IllegalArgumentException e) {
        } catch (MalformedURLException ex) {
            errors.add(new ValidationError(getClass(), "uri", "URL must be valid: " + ex.getMessage()));
        }
        return errors;
    }

    public Icon deepCopy() {
        return new Icon(getMimeType(), getWidth(), getHeight(), getDepth(), getUri(), getData());
    }

    public String toString() {
        return "Icon(" + getWidth() + "x" + getHeight() + ", " + getMimeType() + ") " + getUri();
    }
}
