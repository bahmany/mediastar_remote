package org.teleal.cling.support.shared;

import java.awt.Dimension;
import java.awt.Frame;
import java.util.List;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JWindow;
import javax.swing.UIManager;
import org.teleal.cling.UpnpService;
import org.teleal.common.logging.LoggingUtil;
import org.teleal.common.swingfwk.AbstractController;
import org.teleal.common.swingfwk.Application;
import org.teleal.common.swingfwk.logging.LogCategory;
import org.teleal.common.swingfwk.logging.LogController;
import org.teleal.common.swingfwk.logging.LogMessage;
import org.teleal.common.swingfwk.logging.LoggingHandler;

/* loaded from: classes.dex */
public abstract class MainController extends AbstractController<JFrame> {
    private final LogController logController;
    private final JPanel logPanel;

    public abstract UpnpService getUpnpService();

    public MainController(JFrame view, List<LogCategory> logCategories) throws SecurityException {
        super(view);
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ex) {
            System.out.println("Unable to load native look and feel: " + ex.toString());
        }
        System.setProperty("sun.awt.exception.handler", AWTExceptionHandler.class.getName());
        Runtime.getRuntime().addShutdownHook(new Thread() { // from class: org.teleal.cling.support.shared.MainController.1
            @Override // java.lang.Thread, java.lang.Runnable
            public void run() {
                if (MainController.this.getUpnpService() != null) {
                    MainController.this.getUpnpService().shutdown();
                }
            }
        });
        this.logController = new LogController(this, logCategories) { // from class: org.teleal.cling.support.shared.MainController.2
            protected void expand(LogMessage logMessage) {
                fireEventGlobal(new TextExpandEvent(logMessage.getMessage()));
            }

            protected Frame getParentWindow() {
                return MainController.this.getView();
            }
        };
        this.logPanel = this.logController.getView();
        this.logPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));
        Handler handler = new LoggingHandler() { // from class: org.teleal.cling.support.shared.MainController.3
            protected void log(LogMessage msg) {
                MainController.this.logController.pushMessage(msg);
            }
        };
        if (System.getProperty("java.util.logging.config.file") != null) {
            LogManager.getLogManager().getLogger("").addHandler(handler);
        } else {
            LoggingUtil.resetRootHandler(new Handler[]{handler});
        }
    }

    public LogController getLogController() {
        return this.logController;
    }

    public JPanel getLogPanel() {
        return this.logPanel;
    }

    public void log(Level level, String msg) {
        log(new LogMessage(level, msg));
    }

    public void log(LogMessage message) {
        getLogController().pushMessage(message);
    }

    /* JADX WARN: Type inference failed for: r0v1, types: [org.teleal.cling.support.shared.MainController$4] */
    public void dispose() {
        super.dispose();
        ShutdownWindow.INSTANCE.setVisible(true);
        new Thread() { // from class: org.teleal.cling.support.shared.MainController.4
            @Override // java.lang.Thread, java.lang.Runnable
            public void run() {
                System.exit(0);
            }
        }.start();
    }

    public static class ShutdownWindow extends JWindow {
        public static final JWindow INSTANCE = new ShutdownWindow();

        protected ShutdownWindow() {
            JLabel shutdownLabel = new JLabel("Shutting down, please wait...");
            shutdownLabel.setHorizontalAlignment(0);
            getContentPane().add(shutdownLabel);
            setPreferredSize(new Dimension(300, 30));
            pack();
            Application.center(this);
        }
    }
}
