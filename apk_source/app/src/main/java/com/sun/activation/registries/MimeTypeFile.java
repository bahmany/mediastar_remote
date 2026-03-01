package com.sun.activation.registries;

import com.hisilicon.dlna.dmc.data.PlaylistSQLiteHelper;
import com.hisilicon.multiscreen.protocol.ClientInfo;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.Hashtable;
import java.util.StringTokenizer;

/* loaded from: classes.dex */
public class MimeTypeFile {
    private String fname;
    private Hashtable type_hash;

    public MimeTypeFile(String new_fname) throws IOException {
        this.fname = null;
        this.type_hash = new Hashtable();
        this.fname = new_fname;
        File mime_file = new File(this.fname);
        FileReader fr = new FileReader(mime_file);
        try {
            parse(new BufferedReader(fr));
        } finally {
            try {
                fr.close();
            } catch (IOException e) {
            }
        }
    }

    public MimeTypeFile(InputStream is) throws IOException {
        this.fname = null;
        this.type_hash = new Hashtable();
        parse(new BufferedReader(new InputStreamReader(is, "iso-8859-1")));
    }

    public MimeTypeFile() {
        this.fname = null;
        this.type_hash = new Hashtable();
    }

    public MimeTypeEntry getMimeTypeEntry(String file_ext) {
        return (MimeTypeEntry) this.type_hash.get(file_ext);
    }

    public String getMIMETypeString(String file_ext) {
        MimeTypeEntry entry = getMimeTypeEntry(file_ext);
        if (entry != null) {
            return entry.getMIMEType();
        }
        return null;
    }

    public void appendToRegistry(String mime_types) {
        try {
            parse(new BufferedReader(new StringReader(mime_types)));
        } catch (IOException e) {
        }
    }

    private void parse(BufferedReader buf_reader) throws IOException {
        String prev;
        String prev2 = null;
        while (true) {
            String line = buf_reader.readLine();
            if (line == null) {
                break;
            }
            if (prev2 == null) {
                prev = line;
            } else {
                prev = String.valueOf(prev2) + line;
            }
            int end = prev.length();
            if (prev.length() > 0 && prev.charAt(end - 1) == '\\') {
                prev2 = prev.substring(0, end - 1);
            } else {
                parseEntry(prev);
                prev2 = null;
            }
        }
        if (prev2 != null) {
            parseEntry(prev2);
        }
    }

    private void parseEntry(String line) {
        String mime_type = null;
        String line2 = line.trim();
        if (line2.length() != 0 && line2.charAt(0) != '#') {
            if (line2.indexOf(61) > 0) {
                LineTokenizer lt = new LineTokenizer(line2);
                while (lt.hasMoreTokens()) {
                    String name = lt.nextToken();
                    String value = null;
                    if (lt.hasMoreTokens() && lt.nextToken().equals("=") && lt.hasMoreTokens()) {
                        value = lt.nextToken();
                    }
                    if (value == null) {
                        if (LogSupport.isLoggable()) {
                            LogSupport.log("Bad .mime.types entry: " + line2);
                            return;
                        }
                        return;
                    } else if (name.equals(PlaylistSQLiteHelper.COL_TYPE)) {
                        mime_type = value;
                    } else if (name.equals("exts")) {
                        StringTokenizer st = new StringTokenizer(value, ClientInfo.SEPARATOR_BETWEEN_VARS);
                        while (st.hasMoreTokens()) {
                            String file_ext = st.nextToken();
                            MimeTypeEntry entry = new MimeTypeEntry(mime_type, file_ext);
                            this.type_hash.put(file_ext, entry);
                            if (LogSupport.isLoggable()) {
                                LogSupport.log("Added: " + entry.toString());
                            }
                        }
                    }
                }
                return;
            }
            StringTokenizer strtok = new StringTokenizer(line2);
            int num_tok = strtok.countTokens();
            if (num_tok != 0) {
                String mime_type2 = strtok.nextToken();
                while (strtok.hasMoreTokens()) {
                    String file_ext2 = strtok.nextToken();
                    MimeTypeEntry entry2 = new MimeTypeEntry(mime_type2, file_ext2);
                    this.type_hash.put(file_ext2, entry2);
                    if (LogSupport.isLoggable()) {
                        LogSupport.log("Added: " + entry2.toString());
                    }
                }
            }
        }
    }
}
