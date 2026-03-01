package com.sun.mail.handlers;

import com.hisilicon.dlna.dmc.processor.upnp.mediaserver.HttpServer;
import javax.activation.ActivationDataFlavor;

/* loaded from: classes.dex */
public class text_xml extends text_plain {
    private static ActivationDataFlavor myDF = new ActivationDataFlavor(String.class, HttpServer.MIME_XML, "XML String");

    @Override // com.sun.mail.handlers.text_plain
    protected ActivationDataFlavor getDF() {
        return myDF;
    }
}
