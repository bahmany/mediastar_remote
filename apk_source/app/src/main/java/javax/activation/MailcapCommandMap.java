package javax.activation;

import com.sun.activation.registries.LogSupport;
import com.sun.activation.registries.MailcapFile;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.teleal.cling.model.ServiceReference;

/* loaded from: classes.dex */
public class MailcapCommandMap extends CommandMap {
    private static final int PROG = 0;
    private static MailcapFile defDB = null;
    private MailcapFile[] DB;

    public MailcapCommandMap() throws IOException {
        List dbv = new ArrayList(5);
        dbv.add(null);
        LogSupport.log("MailcapCommandMap: load HOME");
        try {
            String user_home = System.getProperty("user.home");
            if (user_home != null) {
                String path = String.valueOf(user_home) + File.separator + ".mailcap";
                MailcapFile mf = loadFile(path);
                if (mf != null) {
                    dbv.add(mf);
                }
            }
        } catch (SecurityException e) {
        }
        LogSupport.log("MailcapCommandMap: load SYS");
        try {
            String system_mailcap = String.valueOf(System.getProperty("java.home")) + File.separator + "lib" + File.separator + "mailcap";
            MailcapFile mf2 = loadFile(system_mailcap);
            if (mf2 != null) {
                dbv.add(mf2);
            }
        } catch (SecurityException e2) {
        }
        LogSupport.log("MailcapCommandMap: load JAR");
        loadAllResources(dbv, "mailcap");
        LogSupport.log("MailcapCommandMap: load DEF");
        synchronized (MailcapCommandMap.class) {
            if (defDB == null) {
                defDB = loadResource("mailcap.default");
            }
        }
        if (defDB != null) {
            dbv.add(defDB);
        }
        this.DB = new MailcapFile[dbv.size()];
        this.DB = (MailcapFile[]) dbv.toArray(this.DB);
    }

    private MailcapFile loadResource(String name) throws IOException {
        InputStream clis;
        InputStream clis2 = null;
        try {
            try {
                clis = SecuritySupport.getResourceAsStream(getClass(), name);
            } catch (IOException e) {
                if (LogSupport.isLoggable()) {
                    LogSupport.log("MailcapCommandMap: can't load " + name, e);
                }
                if (0 != 0) {
                    try {
                        clis2.close();
                    } catch (IOException e2) {
                    }
                }
            } catch (SecurityException sex) {
                if (LogSupport.isLoggable()) {
                    LogSupport.log("MailcapCommandMap: can't load " + name, sex);
                }
                if (0 != 0) {
                    try {
                        clis2.close();
                    } catch (IOException e3) {
                    }
                }
            }
            if (clis == null) {
                if (LogSupport.isLoggable()) {
                    LogSupport.log("MailcapCommandMap: not loading mailcap file: " + name);
                }
                if (clis != null) {
                    try {
                        clis.close();
                    } catch (IOException e4) {
                    }
                }
                return null;
            }
            MailcapFile mf = new MailcapFile(clis);
            if (LogSupport.isLoggable()) {
                LogSupport.log("MailcapCommandMap: successfully loaded mailcap file: " + name);
            }
            if (clis == null) {
                return mf;
            }
            try {
                clis.close();
                return mf;
            } catch (IOException e5) {
                return mf;
            }
        } catch (Throwable th) {
            if (0 != 0) {
                try {
                    clis2.close();
                } catch (IOException e6) {
                }
            }
            throw th;
        }
    }

    private void loadAllResources(List v, String name) throws IOException {
        boolean anyLoaded = false;
        try {
            ClassLoader cld = SecuritySupport.getContextClassLoader();
            if (cld == null) {
                cld = getClass().getClassLoader();
            }
            URL[] urls = cld != null ? SecuritySupport.getResources(cld, name) : SecuritySupport.getSystemResources(name);
            if (urls != null) {
                if (LogSupport.isLoggable()) {
                    LogSupport.log("MailcapCommandMap: getResources");
                }
                for (URL url : urls) {
                    InputStream clis = null;
                    if (LogSupport.isLoggable()) {
                        LogSupport.log("MailcapCommandMap: URL " + url);
                    }
                    try {
                        try {
                            clis = SecuritySupport.openStream(url);
                            if (clis != null) {
                                v.add(new MailcapFile(clis));
                                anyLoaded = true;
                                if (LogSupport.isLoggable()) {
                                    LogSupport.log("MailcapCommandMap: successfully loaded mailcap file from URL: " + url);
                                }
                            } else if (LogSupport.isLoggable()) {
                                LogSupport.log("MailcapCommandMap: not loading mailcap file from URL: " + url);
                            }
                            if (clis != null) {
                                try {
                                    clis.close();
                                } catch (IOException e) {
                                }
                            }
                        } catch (IOException ioex) {
                            if (LogSupport.isLoggable()) {
                                LogSupport.log("MailcapCommandMap: can't load " + url, ioex);
                            }
                            if (clis != null) {
                                try {
                                    clis.close();
                                } catch (IOException e2) {
                                }
                            }
                        } catch (SecurityException sex) {
                            if (LogSupport.isLoggable()) {
                                LogSupport.log("MailcapCommandMap: can't load " + url, sex);
                            }
                            if (clis != null) {
                                try {
                                    clis.close();
                                } catch (IOException e3) {
                                }
                            }
                        }
                    } catch (Throwable th) {
                        if (clis != null) {
                            try {
                                clis.close();
                            } catch (IOException e4) {
                            }
                        }
                        throw th;
                    }
                }
            }
        } catch (Exception ex) {
            if (LogSupport.isLoggable()) {
                LogSupport.log("MailcapCommandMap: can't load " + name, ex);
            }
        }
        if (anyLoaded) {
            return;
        }
        if (LogSupport.isLoggable()) {
            LogSupport.log("MailcapCommandMap: !anyLoaded");
        }
        MailcapFile mf = loadResource(ServiceReference.DELIMITER + name);
        if (mf != null) {
            v.add(mf);
        }
    }

    private MailcapFile loadFile(String name) {
        try {
            MailcapFile mtf = new MailcapFile(name);
            return mtf;
        } catch (IOException e) {
            return null;
        }
    }

    public MailcapCommandMap(String fileName) throws IOException {
        this();
        if (LogSupport.isLoggable()) {
            LogSupport.log("MailcapCommandMap: load PROG from " + fileName);
        }
        if (this.DB[0] == null) {
            this.DB[0] = new MailcapFile(fileName);
        }
    }

    public MailcapCommandMap(InputStream is) {
        this();
        LogSupport.log("MailcapCommandMap: load PROG");
        if (this.DB[0] == null) {
            try {
                this.DB[0] = new MailcapFile(is);
            } catch (IOException e) {
            }
        }
    }

    @Override // javax.activation.CommandMap
    public synchronized CommandInfo[] getPreferredCommands(String mimeType) {
        List cmdList;
        CommandInfo[] cmdInfos;
        Map cmdMap;
        Map cmdMap2;
        cmdList = new ArrayList();
        if (mimeType != null) {
            mimeType = mimeType.toLowerCase(Locale.ENGLISH);
        }
        for (int i = 0; i < this.DB.length; i++) {
            if (this.DB[i] != null && (cmdMap2 = this.DB[i].getMailcapList(mimeType)) != null) {
                appendPrefCmdsToList(cmdMap2, cmdList);
            }
        }
        for (int i2 = 0; i2 < this.DB.length; i2++) {
            if (this.DB[i2] != null && (cmdMap = this.DB[i2].getMailcapFallbackList(mimeType)) != null) {
                appendPrefCmdsToList(cmdMap, cmdList);
            }
        }
        cmdInfos = new CommandInfo[cmdList.size()];
        return (CommandInfo[]) cmdList.toArray(cmdInfos);
    }

    private void appendPrefCmdsToList(Map cmdHash, List cmdList) {
        for (String verb : cmdHash.keySet()) {
            if (!checkForVerb(cmdList, verb)) {
                List cmdList2 = (List) cmdHash.get(verb);
                String className = (String) cmdList2.get(0);
                cmdList.add(new CommandInfo(verb, className));
            }
        }
    }

    private boolean checkForVerb(List cmdList, String verb) {
        Iterator ee = cmdList.iterator();
        while (ee.hasNext()) {
            String enum_verb = ((CommandInfo) ee.next()).getCommandName();
            if (enum_verb.equals(verb)) {
                return true;
            }
        }
        return false;
    }

    @Override // javax.activation.CommandMap
    public synchronized CommandInfo[] getAllCommands(String mimeType) {
        List cmdList;
        CommandInfo[] cmdInfos;
        Map cmdMap;
        Map cmdMap2;
        cmdList = new ArrayList();
        if (mimeType != null) {
            mimeType = mimeType.toLowerCase(Locale.ENGLISH);
        }
        for (int i = 0; i < this.DB.length; i++) {
            if (this.DB[i] != null && (cmdMap2 = this.DB[i].getMailcapList(mimeType)) != null) {
                appendCmdsToList(cmdMap2, cmdList);
            }
        }
        for (int i2 = 0; i2 < this.DB.length; i2++) {
            if (this.DB[i2] != null && (cmdMap = this.DB[i2].getMailcapFallbackList(mimeType)) != null) {
                appendCmdsToList(cmdMap, cmdList);
            }
        }
        cmdInfos = new CommandInfo[cmdList.size()];
        return (CommandInfo[]) cmdList.toArray(cmdInfos);
    }

    private void appendCmdsToList(Map typeHash, List cmdList) {
        for (String verb : typeHash.keySet()) {
            List<String> cmdList2 = (List) typeHash.get(verb);
            for (String cmd : cmdList2) {
                cmdList.add(new CommandInfo(verb, cmd));
            }
        }
    }

    @Override // javax.activation.CommandMap
    public synchronized CommandInfo getCommand(String mimeType, String cmdName) {
        CommandInfo commandInfo;
        Map cmdMap;
        List v;
        String cmdClassName;
        Map cmdMap2;
        List v2;
        String cmdClassName2;
        if (mimeType != null) {
            mimeType = mimeType.toLowerCase(Locale.ENGLISH);
        }
        int i = 0;
        while (true) {
            if (i < this.DB.length) {
                if (this.DB[i] == null || (cmdMap2 = this.DB[i].getMailcapList(mimeType)) == null || (v2 = (List) cmdMap2.get(cmdName)) == null || (cmdClassName2 = (String) v2.get(0)) == null) {
                    i++;
                } else {
                    commandInfo = new CommandInfo(cmdName, cmdClassName2);
                    break;
                }
            } else {
                int i2 = 0;
                while (true) {
                    if (i2 < this.DB.length) {
                        if (this.DB[i2] == null || (cmdMap = this.DB[i2].getMailcapFallbackList(mimeType)) == null || (v = (List) cmdMap.get(cmdName)) == null || (cmdClassName = (String) v.get(0)) == null) {
                            i2++;
                        } else {
                            commandInfo = new CommandInfo(cmdName, cmdClassName);
                            break;
                        }
                    } else {
                        commandInfo = null;
                        break;
                    }
                }
            }
        }
        return commandInfo;
    }

    public synchronized void addMailcap(String mail_cap) {
        LogSupport.log("MailcapCommandMap: add to PROG");
        if (this.DB[0] == null) {
            this.DB[0] = new MailcapFile();
        }
        this.DB[0].appendToMailcap(mail_cap);
    }

    /* JADX WARN: Code restructure failed: missing block: B:11:0x0027, code lost:
    
        r2 = 0;
     */
    /* JADX WARN: Code restructure failed: missing block: B:13:0x002b, code lost:
    
        if (r2 < r7.DB.length) goto L30;
     */
    /* JADX WARN: Code restructure failed: missing block: B:14:0x002d, code lost:
    
        r1 = null;
     */
    /* JADX WARN: Code restructure failed: missing block: B:31:0x0077, code lost:
    
        if (r7.DB[r2] != null) goto L33;
     */
    /* JADX WARN: Code restructure failed: missing block: B:32:0x0079, code lost:
    
        r2 = r2 + 1;
     */
    /* JADX WARN: Code restructure failed: missing block: B:34:0x0080, code lost:
    
        if (com.sun.activation.registries.LogSupport.isLoggable() == false) goto L36;
     */
    /* JADX WARN: Code restructure failed: missing block: B:35:0x0082, code lost:
    
        com.sun.activation.registries.LogSupport.log("  search fallback DB #" + r2);
     */
    /* JADX WARN: Code restructure failed: missing block: B:36:0x0094, code lost:
    
        r0 = r7.DB[r2].getMailcapFallbackList(r8);
     */
    /* JADX WARN: Code restructure failed: missing block: B:37:0x009c, code lost:
    
        if (r0 == null) goto L57;
     */
    /* JADX WARN: Code restructure failed: missing block: B:38:0x009e, code lost:
    
        r4 = (java.util.List) r0.get("content-handler");
     */
    /* JADX WARN: Code restructure failed: missing block: B:39:0x00a6, code lost:
    
        if (r4 == null) goto L58;
     */
    /* JADX WARN: Code restructure failed: missing block: B:40:0x00a8, code lost:
    
        r3 = (java.lang.String) r4.get(0);
        r1 = getDataContentHandler(r3);
     */
    /* JADX WARN: Code restructure failed: missing block: B:41:0x00b3, code lost:
    
        if (r1 == null) goto L59;
     */
    @Override // javax.activation.CommandMap
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public synchronized javax.activation.DataContentHandler createDataContentHandler(java.lang.String r8) {
        /*
            r7 = this;
            monitor-enter(r7)
            boolean r5 = com.sun.activation.registries.LogSupport.isLoggable()     // Catch: java.lang.Throwable -> Lb7
            if (r5 == 0) goto L19
            java.lang.StringBuilder r5 = new java.lang.StringBuilder     // Catch: java.lang.Throwable -> Lb7
            java.lang.String r6 = "MailcapCommandMap: createDataContentHandler for "
            r5.<init>(r6)     // Catch: java.lang.Throwable -> Lb7
            java.lang.StringBuilder r5 = r5.append(r8)     // Catch: java.lang.Throwable -> Lb7
            java.lang.String r5 = r5.toString()     // Catch: java.lang.Throwable -> Lb7
            com.sun.activation.registries.LogSupport.log(r5)     // Catch: java.lang.Throwable -> Lb7
        L19:
            if (r8 == 0) goto L21
            java.util.Locale r5 = java.util.Locale.ENGLISH     // Catch: java.lang.Throwable -> Lb7
            java.lang.String r8 = r8.toLowerCase(r5)     // Catch: java.lang.Throwable -> Lb7
        L21:
            r2 = 0
        L22:
            com.sun.activation.registries.MailcapFile[] r5 = r7.DB     // Catch: java.lang.Throwable -> Lb7
            int r5 = r5.length     // Catch: java.lang.Throwable -> Lb7
            if (r2 < r5) goto L30
            r2 = 0
        L28:
            com.sun.activation.registries.MailcapFile[] r5 = r7.DB     // Catch: java.lang.Throwable -> Lb7
            int r5 = r5.length     // Catch: java.lang.Throwable -> Lb7
            if (r2 < r5) goto L73
            r1 = 0
        L2e:
            monitor-exit(r7)
            return r1
        L30:
            com.sun.activation.registries.MailcapFile[] r5 = r7.DB     // Catch: java.lang.Throwable -> Lb7
            r5 = r5[r2]     // Catch: java.lang.Throwable -> Lb7
            if (r5 != 0) goto L39
        L36:
            int r2 = r2 + 1
            goto L22
        L39:
            boolean r5 = com.sun.activation.registries.LogSupport.isLoggable()     // Catch: java.lang.Throwable -> Lb7
            if (r5 == 0) goto L51
            java.lang.StringBuilder r5 = new java.lang.StringBuilder     // Catch: java.lang.Throwable -> Lb7
            java.lang.String r6 = "  search DB #"
            r5.<init>(r6)     // Catch: java.lang.Throwable -> Lb7
            java.lang.StringBuilder r5 = r5.append(r2)     // Catch: java.lang.Throwable -> Lb7
            java.lang.String r5 = r5.toString()     // Catch: java.lang.Throwable -> Lb7
            com.sun.activation.registries.LogSupport.log(r5)     // Catch: java.lang.Throwable -> Lb7
        L51:
            com.sun.activation.registries.MailcapFile[] r5 = r7.DB     // Catch: java.lang.Throwable -> Lb7
            r5 = r5[r2]     // Catch: java.lang.Throwable -> Lb7
            java.util.Map r0 = r5.getMailcapList(r8)     // Catch: java.lang.Throwable -> Lb7
            if (r0 == 0) goto L36
            java.lang.String r5 = "content-handler"
            java.lang.Object r4 = r0.get(r5)     // Catch: java.lang.Throwable -> Lb7
            java.util.List r4 = (java.util.List) r4     // Catch: java.lang.Throwable -> Lb7
            if (r4 == 0) goto L36
            r5 = 0
            java.lang.Object r3 = r4.get(r5)     // Catch: java.lang.Throwable -> Lb7
            java.lang.String r3 = (java.lang.String) r3     // Catch: java.lang.Throwable -> Lb7
            javax.activation.DataContentHandler r1 = r7.getDataContentHandler(r3)     // Catch: java.lang.Throwable -> Lb7
            if (r1 == 0) goto L36
            goto L2e
        L73:
            com.sun.activation.registries.MailcapFile[] r5 = r7.DB     // Catch: java.lang.Throwable -> Lb7
            r5 = r5[r2]     // Catch: java.lang.Throwable -> Lb7
            if (r5 != 0) goto L7c
        L79:
            int r2 = r2 + 1
            goto L28
        L7c:
            boolean r5 = com.sun.activation.registries.LogSupport.isLoggable()     // Catch: java.lang.Throwable -> Lb7
            if (r5 == 0) goto L94
            java.lang.StringBuilder r5 = new java.lang.StringBuilder     // Catch: java.lang.Throwable -> Lb7
            java.lang.String r6 = "  search fallback DB #"
            r5.<init>(r6)     // Catch: java.lang.Throwable -> Lb7
            java.lang.StringBuilder r5 = r5.append(r2)     // Catch: java.lang.Throwable -> Lb7
            java.lang.String r5 = r5.toString()     // Catch: java.lang.Throwable -> Lb7
            com.sun.activation.registries.LogSupport.log(r5)     // Catch: java.lang.Throwable -> Lb7
        L94:
            com.sun.activation.registries.MailcapFile[] r5 = r7.DB     // Catch: java.lang.Throwable -> Lb7
            r5 = r5[r2]     // Catch: java.lang.Throwable -> Lb7
            java.util.Map r0 = r5.getMailcapFallbackList(r8)     // Catch: java.lang.Throwable -> Lb7
            if (r0 == 0) goto L79
            java.lang.String r5 = "content-handler"
            java.lang.Object r4 = r0.get(r5)     // Catch: java.lang.Throwable -> Lb7
            java.util.List r4 = (java.util.List) r4     // Catch: java.lang.Throwable -> Lb7
            if (r4 == 0) goto L79
            r5 = 0
            java.lang.Object r3 = r4.get(r5)     // Catch: java.lang.Throwable -> Lb7
            java.lang.String r3 = (java.lang.String) r3     // Catch: java.lang.Throwable -> Lb7
            javax.activation.DataContentHandler r1 = r7.getDataContentHandler(r3)     // Catch: java.lang.Throwable -> Lb7
            if (r1 == 0) goto L79
            goto L2e
        Lb7:
            r5 = move-exception
            monitor-exit(r7)
            throw r5
        */
        throw new UnsupportedOperationException("Method not decompiled: javax.activation.MailcapCommandMap.createDataContentHandler(java.lang.String):javax.activation.DataContentHandler");
    }

    private DataContentHandler getDataContentHandler(String name) throws ClassNotFoundException {
        Class cl;
        if (LogSupport.isLoggable()) {
            LogSupport.log("    got content-handler");
        }
        if (LogSupport.isLoggable()) {
            LogSupport.log("      class " + name);
        }
        try {
            ClassLoader cld = SecuritySupport.getContextClassLoader();
            if (cld == null) {
                cld = getClass().getClassLoader();
            }
            try {
                cl = cld.loadClass(name);
            } catch (Exception e) {
                cl = Class.forName(name);
            }
            if (cl != null) {
                return (DataContentHandler) cl.newInstance();
            }
        } catch (ClassNotFoundException e2) {
            if (LogSupport.isLoggable()) {
                LogSupport.log("Can't load DCH " + name, e2);
            }
        } catch (IllegalAccessException e3) {
            if (LogSupport.isLoggable()) {
                LogSupport.log("Can't load DCH " + name, e3);
            }
        } catch (InstantiationException e4) {
            if (LogSupport.isLoggable()) {
                LogSupport.log("Can't load DCH " + name, e4);
            }
        }
        return null;
    }

    @Override // javax.activation.CommandMap
    public synchronized String[] getMimeTypes() {
        List mtList;
        String[] mts;
        String[] ts;
        mtList = new ArrayList();
        for (int i = 0; i < this.DB.length; i++) {
            if (this.DB[i] != null && (ts = this.DB[i].getMimeTypes()) != null) {
                for (int j = 0; j < ts.length; j++) {
                    if (!mtList.contains(ts[j])) {
                        mtList.add(ts[j]);
                    }
                }
            }
        }
        mts = new String[mtList.size()];
        return (String[]) mtList.toArray(mts);
    }

    public synchronized String[] getNativeCommands(String mimeType) {
        List cmdList;
        String[] cmds;
        String[] cmds2;
        cmdList = new ArrayList();
        if (mimeType != null) {
            mimeType = mimeType.toLowerCase(Locale.ENGLISH);
        }
        for (int i = 0; i < this.DB.length; i++) {
            if (this.DB[i] != null && (cmds2 = this.DB[i].getNativeCommands(mimeType)) != null) {
                for (int j = 0; j < cmds2.length; j++) {
                    if (!cmdList.contains(cmds2[j])) {
                        cmdList.add(cmds2[j]);
                    }
                }
            }
        }
        cmds = new String[cmdList.size()];
        return (String[]) cmdList.toArray(cmds);
    }
}
