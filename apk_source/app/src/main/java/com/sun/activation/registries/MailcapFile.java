package com.sun.activation.registries;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import org.teleal.cling.model.ServiceReference;

/* loaded from: classes.dex */
public class MailcapFile {
    private static boolean addReverse;
    private Map type_hash = new HashMap();
    private Map fallback_hash = new HashMap();
    private Map native_commands = new HashMap();

    static {
        addReverse = false;
        try {
            addReverse = Boolean.getBoolean("javax.activation.addreverse");
        } catch (Throwable th) {
        }
    }

    public MailcapFile(String new_fname) throws Throwable {
        if (LogSupport.isLoggable()) {
            LogSupport.log("new MailcapFile: file " + new_fname);
        }
        FileReader reader = null;
        try {
            FileReader reader2 = new FileReader(new_fname);
            try {
                parse(new BufferedReader(reader2));
                if (reader2 != null) {
                    try {
                        reader2.close();
                    } catch (IOException e) {
                    }
                }
            } catch (Throwable th) {
                th = th;
                reader = reader2;
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e2) {
                    }
                }
                throw th;
            }
        } catch (Throwable th2) {
            th = th2;
        }
    }

    public MailcapFile(InputStream is) throws IOException {
        if (LogSupport.isLoggable()) {
            LogSupport.log("new MailcapFile: InputStream");
        }
        parse(new BufferedReader(new InputStreamReader(is, "iso-8859-1")));
    }

    public MailcapFile() {
        if (LogSupport.isLoggable()) {
            LogSupport.log("new MailcapFile: default");
        }
    }

    public Map getMailcapList(String mime_type) {
        Map search_result = (Map) this.type_hash.get(mime_type);
        int separator = mime_type.indexOf(47);
        String subtype = mime_type.substring(separator + 1);
        if (!subtype.equals("*")) {
            String type = String.valueOf(mime_type.substring(0, separator + 1)) + "*";
            Map wildcard_result = (Map) this.type_hash.get(type);
            if (wildcard_result != null) {
                if (search_result != null) {
                    return mergeResults(search_result, wildcard_result);
                }
                return wildcard_result;
            }
            return search_result;
        }
        return search_result;
    }

    public Map getMailcapFallbackList(String mime_type) {
        Map search_result = (Map) this.fallback_hash.get(mime_type);
        int separator = mime_type.indexOf(47);
        String subtype = mime_type.substring(separator + 1);
        if (!subtype.equals("*")) {
            String type = String.valueOf(mime_type.substring(0, separator + 1)) + "*";
            Map wildcard_result = (Map) this.fallback_hash.get(type);
            if (wildcard_result != null) {
                if (search_result != null) {
                    return mergeResults(search_result, wildcard_result);
                }
                return wildcard_result;
            }
            return search_result;
        }
        return search_result;
    }

    public String[] getMimeTypes() {
        Set types = new HashSet(this.type_hash.keySet());
        types.addAll(this.fallback_hash.keySet());
        types.addAll(this.native_commands.keySet());
        String[] mts = new String[types.size()];
        return (String[]) types.toArray(mts);
    }

    public String[] getNativeCommands(String mime_type) {
        String[] cmds = (String[]) null;
        List v = (List) this.native_commands.get(mime_type.toLowerCase(Locale.ENGLISH));
        if (v != null) {
            String[] cmds2 = new String[v.size()];
            return (String[]) v.toArray(cmds2);
        }
        return cmds;
    }

    private Map mergeResults(Map first, Map second) {
        Map clonedHash = new HashMap(first);
        for (String verb : second.keySet()) {
            List cmdVector = (List) clonedHash.get(verb);
            if (cmdVector == null) {
                clonedHash.put(verb, second.get(verb));
            } else {
                List oldV = (List) second.get(verb);
                ArrayList arrayList = new ArrayList(cmdVector);
                arrayList.addAll(oldV);
                clonedHash.put(verb, arrayList);
            }
        }
        return clonedHash;
    }

    public void appendToMailcap(String mail_cap) {
        if (LogSupport.isLoggable()) {
            LogSupport.log("appendToMailcap: " + mail_cap);
        }
        try {
            parse(new StringReader(mail_cap));
        } catch (IOException e) {
        }
    }

    private void parse(Reader reader) throws IOException {
        BufferedReader buf_reader = new BufferedReader(reader);
        String continued = null;
        while (true) {
            String line = buf_reader.readLine();
            if (line != null) {
                String line2 = line.trim();
                try {
                    if (line2.charAt(0) != '#') {
                        if (line2.charAt(line2.length() - 1) == '\\') {
                            if (continued != null) {
                                continued = String.valueOf(continued) + line2.substring(0, line2.length() - 1);
                            } else {
                                continued = line2.substring(0, line2.length() - 1);
                            }
                        } else if (continued != null) {
                            try {
                                parseLine(String.valueOf(continued) + line2);
                            } catch (MailcapParseException e) {
                            }
                            continued = null;
                        } else {
                            try {
                                parseLine(line2);
                            } catch (MailcapParseException e2) {
                            }
                        }
                    }
                } catch (StringIndexOutOfBoundsException e3) {
                }
            } else {
                return;
            }
        }
    }

    protected void parseLine(String mailcapEntry) throws MailcapParseException, IOException {
        int currentToken;
        MailcapTokenizer tokenizer = new MailcapTokenizer(mailcapEntry);
        tokenizer.setIsAutoquoting(false);
        if (LogSupport.isLoggable()) {
            LogSupport.log("parse: " + mailcapEntry);
        }
        int currentToken2 = tokenizer.nextToken();
        if (currentToken2 != 2) {
            reportParseError(2, currentToken2, tokenizer.getCurrentTokenValue());
        }
        String primaryType = tokenizer.getCurrentTokenValue().toLowerCase(Locale.ENGLISH);
        String subType = "*";
        int currentToken3 = tokenizer.nextToken();
        if (currentToken3 != 47 && currentToken3 != 59) {
            reportParseError(47, 59, currentToken3, tokenizer.getCurrentTokenValue());
        }
        if (currentToken3 == 47) {
            int currentToken4 = tokenizer.nextToken();
            if (currentToken4 != 2) {
                reportParseError(2, currentToken4, tokenizer.getCurrentTokenValue());
            }
            subType = tokenizer.getCurrentTokenValue().toLowerCase(Locale.ENGLISH);
            currentToken3 = tokenizer.nextToken();
        }
        String mimeType = String.valueOf(primaryType) + ServiceReference.DELIMITER + subType;
        if (LogSupport.isLoggable()) {
            LogSupport.log("  Type: " + mimeType);
        }
        LinkedHashMap linkedHashMap = new LinkedHashMap();
        if (currentToken3 != 59) {
            reportParseError(59, currentToken3, tokenizer.getCurrentTokenValue());
        }
        tokenizer.setIsAutoquoting(true);
        int currentToken5 = tokenizer.nextToken();
        tokenizer.setIsAutoquoting(false);
        if (currentToken5 != 2 && currentToken5 != 59) {
            reportParseError(2, 59, currentToken5, tokenizer.getCurrentTokenValue());
        }
        if (currentToken5 == 2) {
            List v = (List) this.native_commands.get(mimeType);
            if (v == null) {
                ArrayList arrayList = new ArrayList();
                arrayList.add(mailcapEntry);
                this.native_commands.put(mimeType, arrayList);
            } else {
                v.add(mailcapEntry);
            }
        }
        if (currentToken5 != 59) {
            currentToken5 = tokenizer.nextToken();
        }
        if (currentToken5 == 59) {
            boolean isFallback = false;
            do {
                int currentToken6 = tokenizer.nextToken();
                if (currentToken6 != 2) {
                    reportParseError(2, currentToken6, tokenizer.getCurrentTokenValue());
                }
                String paramName = tokenizer.getCurrentTokenValue().toLowerCase(Locale.ENGLISH);
                currentToken = tokenizer.nextToken();
                if (currentToken != 61 && currentToken != 59 && currentToken != 5) {
                    reportParseError(61, 59, 5, currentToken, tokenizer.getCurrentTokenValue());
                }
                if (currentToken == 61) {
                    tokenizer.setIsAutoquoting(true);
                    int currentToken7 = tokenizer.nextToken();
                    tokenizer.setIsAutoquoting(false);
                    if (currentToken7 != 2) {
                        reportParseError(2, currentToken7, tokenizer.getCurrentTokenValue());
                    }
                    String paramValue = tokenizer.getCurrentTokenValue();
                    if (paramName.startsWith("x-java-")) {
                        String commandName = paramName.substring(7);
                        if (commandName.equals("fallback-entry") && paramValue.equalsIgnoreCase("true")) {
                            isFallback = true;
                        } else {
                            if (LogSupport.isLoggable()) {
                                LogSupport.log("    Command: " + commandName + ", Class: " + paramValue);
                            }
                            List classes = (List) linkedHashMap.get(commandName);
                            if (classes == null) {
                                classes = new ArrayList();
                                linkedHashMap.put(commandName, classes);
                            }
                            if (addReverse) {
                                classes.add(0, paramValue);
                            } else {
                                classes.add(paramValue);
                            }
                        }
                    }
                    currentToken = tokenizer.nextToken();
                }
            } while (currentToken == 59);
            Map masterHash = isFallback ? this.fallback_hash : this.type_hash;
            Map curcommands = (Map) masterHash.get(mimeType);
            if (curcommands == null) {
                masterHash.put(mimeType, linkedHashMap);
                return;
            }
            if (LogSupport.isLoggable()) {
                LogSupport.log("Merging commands for type " + mimeType);
            }
            for (String cmdName : curcommands.keySet()) {
                List ccv = (List) curcommands.get(cmdName);
                List<String> cv = (List) linkedHashMap.get(cmdName);
                if (cv != null) {
                    for (String clazz : cv) {
                        if (!ccv.contains(clazz)) {
                            if (addReverse) {
                                ccv.add(0, clazz);
                            } else {
                                ccv.add(clazz);
                            }
                        }
                    }
                }
            }
            for (String cmdName2 : linkedHashMap.keySet()) {
                if (!curcommands.containsKey(cmdName2)) {
                    curcommands.put(cmdName2, (List) linkedHashMap.get(cmdName2));
                }
            }
            return;
        }
        if (currentToken5 != 5) {
            reportParseError(5, 59, currentToken5, tokenizer.getCurrentTokenValue());
        }
    }

    protected static void reportParseError(int expectedToken, int actualToken, String actualTokenValue) throws MailcapParseException {
        throw new MailcapParseException("Encountered a " + MailcapTokenizer.nameForToken(actualToken) + " token (" + actualTokenValue + ") while expecting a " + MailcapTokenizer.nameForToken(expectedToken) + " token.");
    }

    protected static void reportParseError(int expectedToken, int otherExpectedToken, int actualToken, String actualTokenValue) throws MailcapParseException {
        throw new MailcapParseException("Encountered a " + MailcapTokenizer.nameForToken(actualToken) + " token (" + actualTokenValue + ") while expecting a " + MailcapTokenizer.nameForToken(expectedToken) + " or a " + MailcapTokenizer.nameForToken(otherExpectedToken) + " token.");
    }

    protected static void reportParseError(int expectedToken, int otherExpectedToken, int anotherExpectedToken, int actualToken, String actualTokenValue) throws MailcapParseException {
        if (LogSupport.isLoggable()) {
            LogSupport.log("PARSE ERROR: Encountered a " + MailcapTokenizer.nameForToken(actualToken) + " token (" + actualTokenValue + ") while expecting a " + MailcapTokenizer.nameForToken(expectedToken) + ", a " + MailcapTokenizer.nameForToken(otherExpectedToken) + ", or a " + MailcapTokenizer.nameForToken(anotherExpectedToken) + " token.");
        }
        throw new MailcapParseException("Encountered a " + MailcapTokenizer.nameForToken(actualToken) + " token (" + actualTokenValue + ") while expecting a " + MailcapTokenizer.nameForToken(expectedToken) + ", a " + MailcapTokenizer.nameForToken(otherExpectedToken) + ", or a " + MailcapTokenizer.nameForToken(anotherExpectedToken) + " token.");
    }
}
