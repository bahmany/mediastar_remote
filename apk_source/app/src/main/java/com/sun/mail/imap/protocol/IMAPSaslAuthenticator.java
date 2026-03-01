package com.sun.mail.imap.protocol;

import com.sun.mail.iap.ProtocolException;
import com.sun.mail.iap.Response;
import com.sun.mail.util.ASCIIUtility;
import com.sun.mail.util.BASE64DecoderStream;
import com.sun.mail.util.BASE64EncoderStream;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Properties;
import java.util.Vector;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.sasl.RealmCallback;
import javax.security.sasl.RealmChoiceCallback;
import javax.security.sasl.Sasl;
import javax.security.sasl.SaslClient;
import javax.security.sasl.SaslException;

/* loaded from: classes.dex */
public class IMAPSaslAuthenticator implements SaslAuthenticator {
    private boolean debug;
    private String host;
    private String name;
    private PrintStream out;
    private IMAPProtocol pr;
    private Properties props;

    public IMAPSaslAuthenticator(IMAPProtocol pr, String name, Properties props, boolean debug, PrintStream out, String host) {
        this.pr = pr;
        this.name = name;
        this.props = props;
        this.debug = debug;
        this.out = out;
        this.host = host;
    }

    @Override // com.sun.mail.imap.protocol.SaslAuthenticator
    public boolean authenticate(String[] mechs, final String realm, String authzid, final String u, final String p) throws ProtocolException {
        String qop;
        synchronized (this.pr) {
            Vector v = new Vector();
            Response r = null;
            boolean done = false;
            if (this.debug) {
                this.out.print("IMAP SASL DEBUG: Mechanisms:");
                for (String str : mechs) {
                    this.out.print(" " + str);
                }
                this.out.println();
            }
            CallbackHandler cbh = new CallbackHandler() { // from class: com.sun.mail.imap.protocol.IMAPSaslAuthenticator.1
                @Override // javax.security.auth.callback.CallbackHandler
                public void handle(Callback[] callbacks) {
                    if (IMAPSaslAuthenticator.this.debug) {
                        IMAPSaslAuthenticator.this.out.println("IMAP SASL DEBUG: callback length: " + callbacks.length);
                    }
                    for (int i = 0; i < callbacks.length; i++) {
                        if (IMAPSaslAuthenticator.this.debug) {
                            IMAPSaslAuthenticator.this.out.println("IMAP SASL DEBUG: callback " + i + ": " + callbacks[i]);
                        }
                        if (callbacks[i] instanceof NameCallback) {
                            NameCallback ncb = (NameCallback) callbacks[i];
                            ncb.setName(u);
                        } else if (callbacks[i] instanceof PasswordCallback) {
                            PasswordCallback pcb = (PasswordCallback) callbacks[i];
                            pcb.setPassword(p.toCharArray());
                        } else if (callbacks[i] instanceof RealmCallback) {
                            RealmCallback rcb = (RealmCallback) callbacks[i];
                            rcb.setText(realm != null ? realm : rcb.getDefaultText());
                        } else if (callbacks[i] instanceof RealmChoiceCallback) {
                            RealmChoiceCallback rcb2 = (RealmChoiceCallback) callbacks[i];
                            if (realm == null) {
                                rcb2.setSelectedIndex(rcb2.getDefaultChoice());
                            } else {
                                String[] choices = rcb2.getChoices();
                                int k = 0;
                                while (true) {
                                    if (k >= choices.length) {
                                        break;
                                    }
                                    if (!choices[k].equals(realm)) {
                                        k++;
                                    } else {
                                        rcb2.setSelectedIndex(k);
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }
            };
            try {
                SaslClient sc = Sasl.createSaslClient(mechs, authzid, this.name, this.host, this.props, cbh);
                if (sc == null) {
                    if (this.debug) {
                        this.out.println("IMAP SASL DEBUG: No SASL support");
                    }
                    return false;
                }
                if (this.debug) {
                    this.out.println("IMAP SASL DEBUG: SASL client " + sc.getMechanismName());
                }
                try {
                    String tag = this.pr.writeCommand("AUTHENTICATE " + sc.getMechanismName(), null);
                    OutputStream os = this.pr.getIMAPOutputStream();
                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    byte[] CRLF = {13, 10};
                    boolean isXGWTRUSTEDAPP = sc.getMechanismName().equals("XGWTRUSTEDAPP");
                    while (!done) {
                        try {
                            r = this.pr.readResponse();
                            if (r.isContinuation()) {
                                byte[] ba = (byte[]) null;
                                if (!sc.isComplete()) {
                                    byte[] ba2 = r.readByteArray().getNewBytes();
                                    if (ba2.length > 0) {
                                        ba2 = BASE64DecoderStream.decode(ba2);
                                    }
                                    if (this.debug) {
                                        this.out.println("IMAP SASL DEBUG: challenge: " + ASCIIUtility.toString(ba2, 0, ba2.length) + " :");
                                    }
                                    ba = sc.evaluateChallenge(ba2);
                                }
                                if (ba == null) {
                                    if (this.debug) {
                                        this.out.println("IMAP SASL DEBUG: no response");
                                    }
                                    os.write(CRLF);
                                    os.flush();
                                    bos.reset();
                                } else {
                                    if (this.debug) {
                                        this.out.println("IMAP SASL DEBUG: response: " + ASCIIUtility.toString(ba, 0, ba.length) + " :");
                                    }
                                    byte[] ba3 = BASE64EncoderStream.encode(ba);
                                    if (isXGWTRUSTEDAPP) {
                                        bos.write("XGWTRUSTEDAPP ".getBytes());
                                    }
                                    bos.write(ba3);
                                    bos.write(CRLF);
                                    os.write(bos.toByteArray());
                                    os.flush();
                                    bos.reset();
                                }
                            } else if (r.isTagged() && r.getTag().equals(tag)) {
                                done = true;
                            } else if (r.isBYE()) {
                                done = true;
                            } else {
                                v.addElement(r);
                            }
                        } catch (Exception ioex) {
                            if (this.debug) {
                                ioex.printStackTrace();
                            }
                            r = Response.byeResponse(ioex);
                            done = true;
                        }
                    }
                    if (sc.isComplete() && (qop = (String) sc.getNegotiatedProperty("javax.security.sasl.qop")) != null && (qop.equalsIgnoreCase("auth-int") || qop.equalsIgnoreCase("auth-conf"))) {
                        if (this.debug) {
                            this.out.println("IMAP SASL DEBUG: Mechanism requires integrity or confidentiality");
                        }
                        return false;
                    }
                    Response[] responses = new Response[v.size()];
                    v.copyInto(responses);
                    this.pr.notifyResponseHandlers(responses);
                    this.pr.handleResult(r);
                    this.pr.setCapabilities(r);
                    return true;
                } catch (Exception ex) {
                    if (this.debug) {
                        this.out.println("IMAP SASL DEBUG: AUTHENTICATE Exception: " + ex);
                    }
                    return false;
                }
            } catch (SaslException sex) {
                if (this.debug) {
                    this.out.println("IMAP SASL DEBUG: Failed to create SASL client: " + sex);
                }
                return false;
            }
        }
    }
}
