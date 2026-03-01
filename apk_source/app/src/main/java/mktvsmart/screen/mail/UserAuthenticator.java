package mktvsmart.screen.mail;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;

/* loaded from: classes.dex */
public class UserAuthenticator extends Authenticator {
    String password;
    String username;

    public UserAuthenticator(String username, String password) {
        this.username = null;
        this.password = null;
        this.username = username;
        this.password = password;
    }

    @Override // javax.mail.Authenticator
    protected PasswordAuthentication getPasswordAuthentication() {
        return new PasswordAuthentication(this.username, this.password);
    }
}
