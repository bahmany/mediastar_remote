package com.sun.mail.handlers;

import com.hisilicon.dlna.dmc.processor.upnp.mediaserver.HttpServer;
import javax.activation.ActivationDataFlavor;

/* loaded from: classes.dex */
public class text_html extends text_plain {
    private static ActivationDataFlavor myDF = new ActivationDataFlavor(String.class, HttpServer.MIME_HTML, "HTML String");

    @Override // com.sun.mail.handlers.text_plain
    protected ActivationDataFlavor getDF() {
        return myDF;
    }
}
