package org.teleal.cling.support.shared;

import java.awt.Dimension;
import java.awt.Frame;
import java.util.logging.Logger;
import javax.swing.JDialog;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import org.teleal.cling.model.ModelUtil;
import org.teleal.common.swingfwk.Application;
import org.teleal.common.xml.DOM;
import org.teleal.common.xml.DOMParser;
import org.w3c.dom.Document;

/* loaded from: classes.dex */
public class TextExpandDialog extends JDialog {
    private static Logger log = Logger.getLogger(TextExpandDialog.class.getName());

    /* JADX WARN: Type inference failed for: r4v9, types: [org.teleal.cling.support.shared.TextExpandDialog$1] */
    public TextExpandDialog(Frame frame, String text) {
        String pretty;
        super(frame);
        setResizable(true);
        JTextArea textArea = new JTextArea();
        JScrollPane textPane = new JScrollPane(textArea);
        textPane.setPreferredSize(new Dimension(500, 400));
        add(textPane);
        if (text.startsWith("<") && text.endsWith(">")) {
            try {
                pretty = new DOMParser() { // from class: org.teleal.cling.support.shared.TextExpandDialog.1
                    protected DOM createDOM(Document document) {
                        return null;
                    }
                }.print(text, 2, false);
            } catch (Exception ex) {
                log.severe("Error pretty printing XML: " + ex.toString());
                pretty = text;
            }
        } else if (text.startsWith("http-get")) {
            pretty = ModelUtil.commaToNewline(text);
        } else {
            pretty = text;
        }
        textArea.setEditable(false);
        textArea.setText(pretty);
        pack();
        Application.center(this, getOwner());
        setVisible(true);
    }
}
