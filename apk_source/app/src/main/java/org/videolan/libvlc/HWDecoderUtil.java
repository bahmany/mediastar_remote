package org.videolan.libvlc;

import java.lang.reflect.Method;
import java.util.HashMap;

/* loaded from: classes.dex */
public class HWDecoderUtil {
    private static final DecoderBySOC[] sDecoderBySOCList = {new DecoderBySOC("ro.product.brand", "SEMC", Decoder.NONE), new DecoderBySOC("ro.board.platform", "msm7627", Decoder.NONE), new DecoderBySOC("ro.board.platform", "omap3", Decoder.OMX), new DecoderBySOC("ro.board.platform", "rockchip", Decoder.OMX), new DecoderBySOC("ro.board.platform", "rk29", Decoder.OMX), new DecoderBySOC("ro.board.platform", "msm7630", Decoder.OMX), new DecoderBySOC("ro.board.platform", "s5pc", Decoder.OMX), new DecoderBySOC("ro.board.platform", "montblanc", Decoder.OMX), new DecoderBySOC("ro.board.platform", "exdroid", Decoder.OMX), new DecoderBySOC("ro.board.platform", "sun6i", Decoder.OMX), new DecoderBySOC("ro.board.platform", "exynos4", Decoder.MEDIACODEC), new DecoderBySOC("ro.board.platform", "omap4", Decoder.ALL), new DecoderBySOC("ro.board.platform", "tegra", Decoder.ALL), new DecoderBySOC("ro.board.platform", "tegra3", Decoder.ALL), new DecoderBySOC("ro.board.platform", "msm8660", Decoder.ALL), new DecoderBySOC("ro.board.platform", "exynos5", Decoder.ALL), new DecoderBySOC("ro.board.platform", "rk30", Decoder.ALL), new DecoderBySOC("ro.board.platform", "rk31", Decoder.ALL), new DecoderBySOC("ro.board.platform", "mv88de3100", Decoder.ALL), new DecoderBySOC("ro.hardware", "mt65", Decoder.ALL), new DecoderBySOC("ro.hardware", "mt83", Decoder.ALL)};
    private static final AudioOutputBySOC[] sAudioOutputBySOCList = {new AudioOutputBySOC("ro.product.brand", "Amazon", AudioOutput.OPENSLES)};
    private static final HashMap<String, String> sSystemPropertyMap = new HashMap<>();

    public enum AudioOutput {
        OPENSLES,
        AUDIOTRACK,
        ALL;

        /* renamed from: values, reason: to resolve conflict with enum method */
        public static AudioOutput[] valuesCustom() {
            AudioOutput[] audioOutputArrValuesCustom = values();
            int length = audioOutputArrValuesCustom.length;
            AudioOutput[] audioOutputArr = new AudioOutput[length];
            System.arraycopy(audioOutputArrValuesCustom, 0, audioOutputArr, 0, length);
            return audioOutputArr;
        }
    }

    public enum Decoder {
        UNKNOWN,
        NONE,
        OMX,
        MEDIACODEC,
        ALL;

        /* renamed from: values, reason: to resolve conflict with enum method */
        public static Decoder[] valuesCustom() {
            Decoder[] decoderArrValuesCustom = values();
            int length = decoderArrValuesCustom.length;
            Decoder[] decoderArr = new Decoder[length];
            System.arraycopy(decoderArrValuesCustom, 0, decoderArr, 0, length);
            return decoderArr;
        }
    }

    private static class DecoderBySOC {
        public final Decoder dec;
        public final String key;
        public final String value;

        public DecoderBySOC(String key, String value, Decoder dec) {
            this.key = key;
            this.value = value;
            this.dec = dec;
        }
    }

    private static class AudioOutputBySOC {
        public final AudioOutput aout;
        public final String key;
        public final String value;

        public AudioOutputBySOC(String key, String value, AudioOutput aout) {
            this.key = key;
            this.value = value;
            this.aout = aout;
        }
    }

    public static Decoder getDecoderFromDevice() throws NoSuchMethodException, ClassNotFoundException, SecurityException {
        if (LibVlcUtil.isJellyBeanMR2OrLater()) {
            return Decoder.ALL;
        }
        if (LibVlcUtil.isHoneycombOrLater()) {
            for (DecoderBySOC decBySOC : sDecoderBySOCList) {
                String prop = getSystemPropertyCached(decBySOC.key);
                if (prop != null && prop.contains(decBySOC.value)) {
                    return decBySOC.dec;
                }
            }
        }
        return Decoder.UNKNOWN;
    }

    public static AudioOutput getAudioOutputFromDevice() throws NoSuchMethodException, ClassNotFoundException, SecurityException {
        if (!LibVlcUtil.isGingerbreadOrLater()) {
            return AudioOutput.AUDIOTRACK;
        }
        for (AudioOutputBySOC aoutBySOC : sAudioOutputBySOCList) {
            String prop = getSystemPropertyCached(aoutBySOC.key);
            if (prop != null && prop.contains(aoutBySOC.value)) {
                return aoutBySOC.aout;
            }
        }
        return AudioOutput.ALL;
    }

    private static String getSystemPropertyCached(String key) throws NoSuchMethodException, ClassNotFoundException, SecurityException {
        String prop = sSystemPropertyMap.get(key);
        if (prop == null) {
            String prop2 = getSystemProperty(key, "none");
            sSystemPropertyMap.put(key, prop2);
            return prop2;
        }
        return prop;
    }

    private static String getSystemProperty(String key, String def) throws NoSuchMethodException, ClassNotFoundException, SecurityException {
        try {
            ClassLoader cl = ClassLoader.getSystemClassLoader();
            Class<?> SystemProperties = cl.loadClass("android.os.SystemProperties");
            Class[] paramTypes = {String.class, String.class};
            Method get = SystemProperties.getMethod("get", paramTypes);
            Object[] params = {key, def};
            return (String) get.invoke(SystemProperties, params);
        } catch (Exception e) {
            return def;
        }
    }
}
