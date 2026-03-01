package org.teleal.cling.support.shared;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import javax.swing.JFrame;
import org.teleal.common.swingfwk.Controller;

/* loaded from: classes.dex */
public class PlatformApple {
    public static void setup(Controller<JFrame> appController, String appName) throws Exception {
        System.setProperty("apple.laf.useScreenMenuBar", "true");
        System.setProperty("com.apple.mrj.application.apple.menu.about.name", appName);
        System.setProperty("apple.awt.showGrowBox", "true");
        Class appClass = Class.forName("com.apple.eawt.Application");
        Object application = appClass.newInstance();
        Class listenerClass = Class.forName("com.apple.eawt.ApplicationListener");
        Method addAppListmethod = appClass.getDeclaredMethod("addApplicationListener", listenerClass);
        Class adapterClass = Class.forName("com.apple.eawt.ApplicationAdapter");
        Object listener = AppListenerProxy.newInstance(adapterClass.newInstance(), appController);
        addAppListmethod.invoke(application, listener);
    }

    static class AppListenerProxy implements InvocationHandler {
        private Controller<JFrame> appController;
        private Object object;

        public static Object newInstance(Object obj, Controller<JFrame> appController) {
            return Proxy.newProxyInstance(obj.getClass().getClassLoader(), obj.getClass().getInterfaces(), new AppListenerProxy(obj, appController));
        }

        private AppListenerProxy(Object obj, Controller<JFrame> appController) {
            this.object = obj;
            this.appController = appController;
        }

        @Override // java.lang.reflect.InvocationHandler
        public Object invoke(Object proxy, Method m, Object[] args) throws Throwable {
            Object result = null;
            try {
                if ("handleQuit".equals(m.getName())) {
                    if (this.appController != null) {
                        this.appController.dispose();
                        this.appController.getView().dispose();
                    }
                } else {
                    result = m.invoke(this.object, args);
                }
            } catch (Exception e) {
            }
            return result;
        }
    }
}
