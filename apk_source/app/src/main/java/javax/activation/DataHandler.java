package javax.activation;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.net.URL;
import myjava.awt.datatransfer.DataFlavor;
import myjava.awt.datatransfer.Transferable;
import myjava.awt.datatransfer.UnsupportedFlavorException;

/* loaded from: classes.dex */
public class DataHandler implements Transferable {
    private static final DataFlavor[] emptyFlavors = new DataFlavor[0];
    private static DataContentHandlerFactory factory = null;
    private CommandMap currentCommandMap;
    private DataContentHandler dataContentHandler;
    private DataSource dataSource;
    private DataContentHandler factoryDCH;
    private DataSource objDataSource;
    private Object object;
    private String objectMimeType;
    private DataContentHandlerFactory oldFactory;
    private String shortType;
    private DataFlavor[] transferFlavors;

    public DataHandler(DataSource ds) {
        this.dataSource = null;
        this.objDataSource = null;
        this.object = null;
        this.objectMimeType = null;
        this.currentCommandMap = null;
        this.transferFlavors = emptyFlavors;
        this.dataContentHandler = null;
        this.factoryDCH = null;
        this.oldFactory = null;
        this.shortType = null;
        this.dataSource = ds;
        this.oldFactory = factory;
    }

    public DataHandler(Object obj, String mimeType) {
        this.dataSource = null;
        this.objDataSource = null;
        this.object = null;
        this.objectMimeType = null;
        this.currentCommandMap = null;
        this.transferFlavors = emptyFlavors;
        this.dataContentHandler = null;
        this.factoryDCH = null;
        this.oldFactory = null;
        this.shortType = null;
        this.object = obj;
        this.objectMimeType = mimeType;
        this.oldFactory = factory;
    }

    public DataHandler(URL url) {
        this.dataSource = null;
        this.objDataSource = null;
        this.object = null;
        this.objectMimeType = null;
        this.currentCommandMap = null;
        this.transferFlavors = emptyFlavors;
        this.dataContentHandler = null;
        this.factoryDCH = null;
        this.oldFactory = null;
        this.shortType = null;
        this.dataSource = new URLDataSource(url);
        this.oldFactory = factory;
    }

    private synchronized CommandMap getCommandMap() {
        return this.currentCommandMap != null ? this.currentCommandMap : CommandMap.getDefaultCommandMap();
    }

    public DataSource getDataSource() {
        if (this.dataSource != null) {
            return this.dataSource;
        }
        if (this.objDataSource == null) {
            this.objDataSource = new DataHandlerDataSource(this);
        }
        return this.objDataSource;
    }

    public String getName() {
        if (this.dataSource != null) {
            return this.dataSource.getName();
        }
        return null;
    }

    public String getContentType() {
        return this.dataSource != null ? this.dataSource.getContentType() : this.objectMimeType;
    }

    public InputStream getInputStream() throws IOException {
        if (this.dataSource != null) {
            InputStream ins = this.dataSource.getInputStream();
            return ins;
        }
        DataContentHandler dch = getDataContentHandler();
        if (dch == null) {
            throw new UnsupportedDataTypeException("no DCH for MIME type " + getBaseType());
        }
        if ((dch instanceof ObjectDataContentHandler) && ((ObjectDataContentHandler) dch).getDCH() == null) {
            throw new UnsupportedDataTypeException("no object DCH for MIME type " + getBaseType());
        }
        PipedOutputStream pos = new PipedOutputStream();
        PipedInputStream pin = new PipedInputStream(pos);
        new Thread(new Runnable() { // from class: javax.activation.DataHandler.1
            private final /* synthetic */ DataContentHandler val$fdch;
            private final /* synthetic */ PipedOutputStream val$pos;

            AnonymousClass1(PipedOutputStream pos2, DataContentHandler dch2) {
                pipedOutputStream = pos2;
                dataContentHandler = dch2;
            }

            @Override // java.lang.Runnable
            public void run() throws IOException {
                try {
                    dataContentHandler.writeTo(DataHandler.this.object, DataHandler.this.objectMimeType, pipedOutputStream);
                    try {
                        pipedOutputStream.close();
                    } catch (IOException e) {
                    }
                } catch (IOException e2) {
                    try {
                        pipedOutputStream.close();
                    } catch (IOException e3) {
                    }
                } catch (Throwable th) {
                    try {
                        pipedOutputStream.close();
                    } catch (IOException e4) {
                    }
                    throw th;
                }
            }
        }, "DataHandler.getInputStream").start();
        return pin;
    }

    /* renamed from: javax.activation.DataHandler$1 */
    class AnonymousClass1 implements Runnable {
        private final /* synthetic */ DataContentHandler val$fdch;
        private final /* synthetic */ PipedOutputStream val$pos;

        AnonymousClass1(PipedOutputStream pos2, DataContentHandler dch2) {
            pipedOutputStream = pos2;
            dataContentHandler = dch2;
        }

        @Override // java.lang.Runnable
        public void run() throws IOException {
            try {
                dataContentHandler.writeTo(DataHandler.this.object, DataHandler.this.objectMimeType, pipedOutputStream);
                try {
                    pipedOutputStream.close();
                } catch (IOException e) {
                }
            } catch (IOException e2) {
                try {
                    pipedOutputStream.close();
                } catch (IOException e3) {
                }
            } catch (Throwable th) {
                try {
                    pipedOutputStream.close();
                } catch (IOException e4) {
                }
                throw th;
            }
        }
    }

    public void writeTo(OutputStream os) throws IOException {
        if (this.dataSource != null) {
            byte[] data = new byte[8192];
            InputStream is = this.dataSource.getInputStream();
            while (true) {
                try {
                    int bytes_read = is.read(data);
                    if (bytes_read > 0) {
                        os.write(data, 0, bytes_read);
                    } else {
                        return;
                    }
                } finally {
                    is.close();
                }
            }
        } else {
            DataContentHandler dch = getDataContentHandler();
            dch.writeTo(this.object, this.objectMimeType, os);
        }
    }

    public OutputStream getOutputStream() throws IOException {
        if (this.dataSource != null) {
            return this.dataSource.getOutputStream();
        }
        return null;
    }

    public synchronized DataFlavor[] getTransferDataFlavors() {
        if (factory != this.oldFactory) {
            this.transferFlavors = emptyFlavors;
        }
        if (this.transferFlavors == emptyFlavors) {
            this.transferFlavors = getDataContentHandler().getTransferDataFlavors();
        }
        return this.transferFlavors;
    }

    public boolean isDataFlavorSupported(DataFlavor flavor) {
        DataFlavor[] lFlavors = getTransferDataFlavors();
        for (DataFlavor dataFlavor : lFlavors) {
            if (dataFlavor.equals(flavor)) {
                return true;
            }
        }
        return false;
    }

    public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
        return getDataContentHandler().getTransferData(flavor, this.dataSource);
    }

    public synchronized void setCommandMap(CommandMap commandMap) {
        if (commandMap != this.currentCommandMap || commandMap == null) {
            this.transferFlavors = emptyFlavors;
            this.dataContentHandler = null;
            this.currentCommandMap = commandMap;
        }
    }

    public CommandInfo[] getPreferredCommands() {
        return this.dataSource != null ? getCommandMap().getPreferredCommands(getBaseType(), this.dataSource) : getCommandMap().getPreferredCommands(getBaseType());
    }

    public CommandInfo[] getAllCommands() {
        return this.dataSource != null ? getCommandMap().getAllCommands(getBaseType(), this.dataSource) : getCommandMap().getAllCommands(getBaseType());
    }

    public CommandInfo getCommand(String cmdName) {
        return this.dataSource != null ? getCommandMap().getCommand(getBaseType(), cmdName, this.dataSource) : getCommandMap().getCommand(getBaseType(), cmdName);
    }

    public Object getContent() throws IOException {
        return this.object != null ? this.object : getDataContentHandler().getContent(getDataSource());
    }

    public Object getBean(CommandInfo cmdinfo) {
        try {
            ClassLoader cld = SecuritySupport.getContextClassLoader();
            if (cld == null) {
                cld = getClass().getClassLoader();
            }
            Object bean = cmdinfo.getCommandObject(this, cld);
            return bean;
        } catch (IOException e) {
            return null;
        } catch (ClassNotFoundException e2) {
            return null;
        }
    }

    private synchronized DataContentHandler getDataContentHandler() {
        DataContentHandler dataContentHandler;
        if (factory != this.oldFactory) {
            this.oldFactory = factory;
            this.factoryDCH = null;
            this.dataContentHandler = null;
            this.transferFlavors = emptyFlavors;
        }
        if (this.dataContentHandler != null) {
            dataContentHandler = this.dataContentHandler;
        } else {
            String simpleMT = getBaseType();
            if (this.factoryDCH == null && factory != null) {
                this.factoryDCH = factory.createDataContentHandler(simpleMT);
            }
            if (this.factoryDCH != null) {
                this.dataContentHandler = this.factoryDCH;
            }
            if (this.dataContentHandler == null) {
                if (this.dataSource != null) {
                    this.dataContentHandler = getCommandMap().createDataContentHandler(simpleMT, this.dataSource);
                } else {
                    this.dataContentHandler = getCommandMap().createDataContentHandler(simpleMT);
                }
            }
            if (this.dataSource != null) {
                this.dataContentHandler = new DataSourceDataContentHandler(this.dataContentHandler, this.dataSource);
            } else {
                this.dataContentHandler = new ObjectDataContentHandler(this.dataContentHandler, this.object, this.objectMimeType);
            }
            dataContentHandler = this.dataContentHandler;
        }
        return dataContentHandler;
    }

    private synchronized String getBaseType() {
        if (this.shortType == null) {
            String ct = getContentType();
            try {
                MimeType mt = new MimeType(ct);
                this.shortType = mt.getBaseType();
            } catch (MimeTypeParseException e) {
                this.shortType = ct;
            }
        }
        return this.shortType;
    }

    public static synchronized void setDataContentHandlerFactory(DataContentHandlerFactory newFactory) {
        if (factory != null) {
            throw new Error("DataContentHandlerFactory already defined");
        }
        SecurityManager security = System.getSecurityManager();
        if (security != null) {
            try {
                security.checkSetFactory();
            } catch (SecurityException ex) {
                if (DataHandler.class.getClassLoader() != newFactory.getClass().getClassLoader()) {
                    throw ex;
                }
            }
        }
        factory = newFactory;
    }
}
