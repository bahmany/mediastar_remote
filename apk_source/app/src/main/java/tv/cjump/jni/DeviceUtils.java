package tv.cjump.jni;

import android.os.Build;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.lang.reflect.Field;

/* loaded from: classes.dex */
public class DeviceUtils {
    public static final String ABI_MIPS = "mips";
    public static final String ABI_X86 = "x86";
    private static final int EM_386 = 3;
    private static final int EM_AARCH64 = 183;
    private static final int EM_ARM = 40;
    private static final int EM_MIPS = 8;
    private static ARCH sArch = ARCH.Unknown;

    public enum ARCH {
        Unknown,
        ARM,
        X86,
        MIPS,
        ARM64;

        /* renamed from: values, reason: to resolve conflict with enum method */
        public static ARCH[] valuesCustom() {
            ARCH[] archArrValuesCustom = values();
            int length = archArrValuesCustom.length;
            ARCH[] archArr = new ARCH[length];
            System.arraycopy(archArrValuesCustom, 0, archArr, 0, length);
            return archArr;
        }
    }

    public static synchronized ARCH getMyCpuArch() {
        byte[] data = new byte[20];
        File libc = new File(Environment.getRootDirectory(), "lib/libc.so");
        if (libc.canRead()) {
            RandomAccessFile fp = null;
            try {
                try {
                    RandomAccessFile fp2 = new RandomAccessFile(libc, "r");
                    try {
                        fp2.readFully(data);
                        int machine = (data[19] << 8) | data[18];
                        switch (machine) {
                            case 3:
                                sArch = ARCH.X86;
                                break;
                            case 8:
                                sArch = ARCH.MIPS;
                                break;
                            case 40:
                                sArch = ARCH.ARM;
                                break;
                            case 183:
                                sArch = ARCH.ARM64;
                                break;
                            default:
                                Log.e("NativeBitmapFactory", "libc.so is unknown arch: " + Integer.toHexString(machine));
                                break;
                        }
                        if (fp2 != null) {
                            try {
                                fp2.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    } catch (FileNotFoundException e2) {
                        e = e2;
                        fp = fp2;
                        e.printStackTrace();
                        if (fp != null) {
                            try {
                                fp.close();
                            } catch (IOException e3) {
                                e3.printStackTrace();
                            }
                        }
                        return sArch;
                    } catch (IOException e4) {
                        e = e4;
                        fp = fp2;
                        e.printStackTrace();
                        if (fp != null) {
                            try {
                                fp.close();
                            } catch (IOException e5) {
                                e5.printStackTrace();
                            }
                        }
                        return sArch;
                    } catch (Throwable th) {
                        th = th;
                        fp = fp2;
                        if (fp != null) {
                            try {
                                fp.close();
                            } catch (IOException e6) {
                                e6.printStackTrace();
                            }
                        }
                        throw th;
                    }
                } catch (FileNotFoundException e7) {
                    e = e7;
                } catch (IOException e8) {
                    e = e8;
                }
            } catch (Throwable th2) {
                th = th2;
            }
        }
        return sArch;
    }

    public static String get_CPU_ABI() {
        return Build.CPU_ABI;
    }

    public static String get_CPU_ABI2() throws IllegalAccessException, NoSuchFieldException, IllegalArgumentException {
        try {
            Field field = Build.class.getDeclaredField("CPU_ABI2");
            if (field == null) {
                return null;
            }
            Object fieldValue = field.get(null);
            if (fieldValue instanceof String) {
                return (String) fieldValue;
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    public static boolean supportABI(String requestAbi) throws IllegalAccessException, NoSuchFieldException, IllegalArgumentException {
        String abi = get_CPU_ABI();
        if (!TextUtils.isEmpty(abi) && abi.equalsIgnoreCase(requestAbi)) {
            return true;
        }
        String abi2 = get_CPU_ABI2();
        return !TextUtils.isEmpty(abi2) && abi.equalsIgnoreCase(requestAbi);
    }

    public static boolean supportX86() {
        return supportABI(ABI_X86);
    }

    public static boolean supportMips() {
        return supportABI(ABI_MIPS);
    }

    public static boolean isARMSimulatedByX86() {
        ARCH arch = getMyCpuArch();
        return !supportX86() && ARCH.X86.equals(arch);
    }

    public static boolean isMiBox2Device() {
        String manufacturer = Build.MANUFACTURER;
        String productName = Build.PRODUCT;
        return manufacturer.equalsIgnoreCase("Xiaomi") && productName.equalsIgnoreCase("dredd");
    }

    public static boolean isMagicBoxDevice() {
        String manufacturer = Build.MANUFACTURER;
        String productName = Build.PRODUCT;
        return manufacturer.equalsIgnoreCase("MagicBox") && productName.equalsIgnoreCase("MagicBox");
    }

    public static boolean isProblemBoxDevice() {
        return isMiBox2Device() || isMagicBoxDevice();
    }

    public static boolean isRealARMArch() {
        ARCH arch = getMyCpuArch();
        return (supportABI("armeabi-v7a") || supportABI("armeabi")) && ARCH.ARM.equals(arch);
    }

    public static boolean isRealX86Arch() {
        ARCH arch = getMyCpuArch();
        return supportABI(ABI_X86) || ARCH.X86.equals(arch);
    }
}
