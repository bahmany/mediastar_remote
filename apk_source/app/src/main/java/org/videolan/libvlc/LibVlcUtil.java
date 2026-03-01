package org.videolan.libvlc;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.net.Uri;
import android.os.Build;
import android.support.v4.media.TransportMediator;
import android.util.Log;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Locale;
import tv.cjump.jni.DeviceUtils;

/* loaded from: classes.dex */
public class LibVlcUtil {
    private static final int ELF_HEADER_SIZE = 52;
    private static final int EM_386 = 3;
    private static final int EM_AARCH64 = 183;
    private static final int EM_ARM = 40;
    private static final int EM_MIPS = 8;
    private static final int EM_X86_64 = 62;
    private static final int SECTION_HEADER_SIZE = 40;
    private static final int SHT_ARM_ATTRIBUTES = 1879048195;
    public static final String TAG = "VLC/LibVLC/Util";
    private static String errorMsg = null;
    private static boolean isCompatible = false;
    private static MachineSpecs machineSpecs = null;
    private static String[] CPU_archs = {"*Pre-v4", "*v4", "*v4T", "v5T", "v5TE", "v5TEJ", "v6", "v6KZ", "v6T2", "v6K", "v7", "*v6-M", "*v6S-M", "*v7E-M", "*v8"};

    public static class MachineSpecs {
        public float bogoMIPS;
        public float frequency;
        public boolean hasArmV6;
        public boolean hasArmV7;
        public boolean hasFpu;
        public boolean hasMips;
        public boolean hasNeon;
        public boolean hasX86;
        public boolean is64bits;
        public int processors;
    }

    public static boolean isFroyoOrLater() {
        return Build.VERSION.SDK_INT >= 8;
    }

    public static boolean isGingerbreadOrLater() {
        return Build.VERSION.SDK_INT >= 9;
    }

    public static boolean isHoneycombOrLater() {
        return Build.VERSION.SDK_INT >= 11;
    }

    public static boolean isICSOrLater() {
        return Build.VERSION.SDK_INT >= 14;
    }

    public static boolean isJellyBeanOrLater() {
        return Build.VERSION.SDK_INT >= 16;
    }

    public static boolean isJellyBeanMR1OrLater() {
        return Build.VERSION.SDK_INT >= 17;
    }

    public static boolean isJellyBeanMR2OrLater() {
        return Build.VERSION.SDK_INT >= 18;
    }

    public static boolean isKitKatOrLater() {
        return Build.VERSION.SDK_INT >= 19;
    }

    public static boolean isLolliPopOrLater() {
        return Build.VERSION.SDK_INT >= 21;
    }

    public static String getErrorMsg() {
        return errorMsg;
    }

    public static File URItoFile(String URI) {
        if (URI == null) {
            return null;
        }
        return new File(Uri.decode(URI).replace("file://", ""));
    }

    public static String URItoFileName(String URI) {
        if (URI == null) {
            return null;
        }
        return URItoFile(URI).getName();
    }

    public static boolean hasCompatibleCPU(Context context) throws Throwable {
        if (errorMsg != null || isCompatible) {
            return isCompatible;
        }
        File lib = searchLibrary(context);
        if (lib == null) {
            return true;
        }
        ElfData elf = readLib(lib);
        if (elf == null) {
            Log.e(TAG, "WARNING: Unable to read libvlcjni.so; cannot check device ABI!");
            Log.e(TAG, "WARNING: Cannot guarantee correct ABI for this build (may crash)!");
            return true;
        }
        String CPU_ABI = Build.CPU_ABI;
        String CPU_ABI2 = "none";
        if (Build.VERSION.SDK_INT >= 8) {
            try {
                CPU_ABI2 = (String) Build.class.getDeclaredField("CPU_ABI2").get(null);
            } catch (Exception e) {
            }
        }
        boolean elfHasX86 = elf.e_machine == 3 || elf.e_machine == 62;
        boolean elfHasArm = elf.e_machine == 40 || elf.e_machine == 183;
        boolean elfHasMips = elf.e_machine == 8;
        boolean elfIs64bits = elf.is64bits;
        Log.i(TAG, "ELF ABI = " + (elfHasArm ? "arm" : elfHasX86 ? DeviceUtils.ABI_X86 : DeviceUtils.ABI_MIPS) + ", " + (elfIs64bits ? "64bits" : "32bits"));
        Log.i(TAG, "ELF arch = " + elf.att_arch);
        Log.i(TAG, "ELF fpu = " + elf.att_fpu);
        boolean hasNeon = false;
        boolean hasFpu = false;
        boolean hasArmV6 = false;
        boolean hasArmV7 = false;
        boolean hasMips = false;
        boolean hasX86 = false;
        boolean is64bits = false;
        float bogoMIPS = -1.0f;
        int processors = 0;
        if (CPU_ABI.equals(DeviceUtils.ABI_X86) || CPU_ABI2.equals(DeviceUtils.ABI_X86)) {
            hasX86 = true;
        } else if (CPU_ABI.equals("x86_64") || CPU_ABI2.equals("x86_64")) {
            hasX86 = true;
            is64bits = true;
        } else if (CPU_ABI.equals("armeabi-v7a") || CPU_ABI2.equals("armeabi-v7a")) {
            hasArmV7 = true;
            hasArmV6 = true;
        } else if (CPU_ABI.equals("armeabi") || CPU_ABI2.equals("armeabi")) {
            hasArmV6 = true;
        } else if (CPU_ABI.equals("arm64-v8a") || CPU_ABI2.equals("arm64-v8a")) {
            hasNeon = true;
            hasArmV6 = true;
            hasArmV7 = true;
            is64bits = true;
        }
        FileReader fileReader = null;
        BufferedReader br = null;
        try {
            try {
                FileReader fileReader2 = new FileReader("/proc/cpuinfo");
                try {
                    BufferedReader br2 = new BufferedReader(fileReader2);
                    while (true) {
                        try {
                            String line = br2.readLine();
                            if (line == null) {
                                break;
                            }
                            if (!hasArmV7 && line.contains("AArch64")) {
                                hasArmV7 = true;
                                hasArmV6 = true;
                            }
                            if (!hasArmV7 && line.contains("ARMv7")) {
                                hasArmV7 = true;
                                hasArmV6 = true;
                            }
                            if (!hasArmV7 && !hasArmV6 && line.contains("ARMv6")) {
                                hasArmV6 = true;
                            }
                            if (line.contains("clflush size")) {
                                hasX86 = true;
                            }
                            if (line.contains("GenuineIntel")) {
                                hasX86 = true;
                            }
                            if (line.contains("microsecond timers")) {
                                hasMips = true;
                            }
                            if (!hasNeon && (line.contains("neon") || line.contains("asimd"))) {
                                hasNeon = true;
                            }
                            if (!hasFpu && (line.contains("vfp") || (line.contains("Features") && line.contains("fp")))) {
                                hasFpu = true;
                            }
                            if (line.startsWith("processor")) {
                                processors++;
                            }
                            if (bogoMIPS < 0.0f && line.toLowerCase(Locale.ENGLISH).contains("bogomips")) {
                                String[] bogo_parts = line.split(":");
                                try {
                                    bogoMIPS = Float.parseFloat(bogo_parts[1].trim());
                                } catch (NumberFormatException e2) {
                                    bogoMIPS = -1.0f;
                                }
                            }
                        } catch (IOException e3) {
                            ex = e3;
                            br = br2;
                            fileReader = fileReader2;
                            ex.printStackTrace();
                            errorMsg = "IOException whilst reading cpuinfo flags";
                            isCompatible = false;
                            if (br != null) {
                                try {
                                    br.close();
                                } catch (IOException e4) {
                                }
                            }
                            if (fileReader != null) {
                                try {
                                    fileReader.close();
                                } catch (IOException e5) {
                                }
                            }
                            return false;
                        } catch (Throwable th) {
                            th = th;
                            br = br2;
                            fileReader = fileReader2;
                            if (br != null) {
                                try {
                                    br.close();
                                } catch (IOException e6) {
                                }
                            }
                            if (fileReader == null) {
                                throw th;
                            }
                            try {
                                fileReader.close();
                                throw th;
                            } catch (IOException e7) {
                                throw th;
                            }
                        }
                    }
                    if (br2 != null) {
                        try {
                            br2.close();
                        } catch (IOException e8) {
                        }
                    }
                    if (fileReader2 != null) {
                        try {
                            fileReader2.close();
                        } catch (IOException e9) {
                        }
                    }
                    if (processors == 0) {
                        processors = 1;
                    }
                    if (elfHasX86 && !hasX86) {
                        errorMsg = "x86 build on non-x86 device";
                        isCompatible = false;
                        return false;
                    }
                    if (elfHasArm && hasX86) {
                        errorMsg = "ARM build on x86 device";
                        isCompatible = false;
                        return false;
                    }
                    if (elfHasMips && !hasMips) {
                        errorMsg = "MIPS build on non-MIPS device";
                        isCompatible = false;
                        return false;
                    }
                    if (elfHasArm && hasMips) {
                        errorMsg = "ARM build on MIPS device";
                        isCompatible = false;
                        return false;
                    }
                    if (elf.e_machine == 40 && elf.att_arch.startsWith("v7") && !hasArmV7) {
                        errorMsg = "ARMv7 build on non-ARMv7 device";
                        isCompatible = false;
                        return false;
                    }
                    if (elf.e_machine == 40) {
                        if (elf.att_arch.startsWith("v6") && !hasArmV6) {
                            errorMsg = "ARMv6 build on non-ARMv6 device";
                            isCompatible = false;
                            return false;
                        }
                        if (elf.att_fpu && !hasFpu) {
                            errorMsg = "FPU-enabled build on non-FPU device";
                            isCompatible = false;
                            return false;
                        }
                    }
                    if (elfIs64bits && !is64bits) {
                        errorMsg = "64bits build on 32bits device";
                        isCompatible = false;
                    }
                    FileReader fileReader3 = null;
                    BufferedReader br3 = null;
                    String line2 = "";
                    try {
                        try {
                            FileReader fileReader4 = new FileReader("/sys/devices/system/cpu/cpu0/cpufreq/cpuinfo_max_freq");
                            try {
                                BufferedReader br4 = new BufferedReader(fileReader4);
                                try {
                                    line2 = br4.readLine();
                                    frequency = line2 != null ? Float.parseFloat(line2) / 1000.0f : -1.0f;
                                    if (br4 != null) {
                                        try {
                                            br4.close();
                                        } catch (IOException e10) {
                                        }
                                    }
                                    if (fileReader4 != null) {
                                        try {
                                            fileReader4.close();
                                            br3 = br4;
                                            fileReader3 = fileReader4;
                                        } catch (IOException e11) {
                                            br3 = br4;
                                            fileReader3 = fileReader4;
                                        }
                                    } else {
                                        br3 = br4;
                                        fileReader3 = fileReader4;
                                    }
                                } catch (IOException e12) {
                                    br3 = br4;
                                    fileReader3 = fileReader4;
                                    Log.w(TAG, "Could not find maximum CPU frequency!");
                                    if (br3 != null) {
                                        try {
                                            br3.close();
                                        } catch (IOException e13) {
                                        }
                                    }
                                    if (fileReader3 != null) {
                                        try {
                                            fileReader3.close();
                                        } catch (IOException e14) {
                                        }
                                    }
                                    errorMsg = null;
                                    isCompatible = true;
                                    machineSpecs = new MachineSpecs();
                                    machineSpecs.hasArmV6 = hasArmV6;
                                    machineSpecs.hasArmV7 = hasArmV7;
                                    machineSpecs.hasFpu = hasFpu;
                                    machineSpecs.hasMips = hasMips;
                                    machineSpecs.hasNeon = hasNeon;
                                    machineSpecs.hasX86 = hasX86;
                                    machineSpecs.is64bits = is64bits;
                                    machineSpecs.bogoMIPS = bogoMIPS;
                                    machineSpecs.processors = processors;
                                    machineSpecs.frequency = frequency;
                                    return true;
                                } catch (NumberFormatException e15) {
                                    br3 = br4;
                                    fileReader3 = fileReader4;
                                    Log.w(TAG, "Could not parse maximum CPU frequency!");
                                    Log.w(TAG, "Failed to parse: " + line2);
                                    if (br3 != null) {
                                        try {
                                            br3.close();
                                        } catch (IOException e16) {
                                        }
                                    }
                                    if (fileReader3 != null) {
                                        try {
                                            fileReader3.close();
                                        } catch (IOException e17) {
                                        }
                                    }
                                    errorMsg = null;
                                    isCompatible = true;
                                    machineSpecs = new MachineSpecs();
                                    machineSpecs.hasArmV6 = hasArmV6;
                                    machineSpecs.hasArmV7 = hasArmV7;
                                    machineSpecs.hasFpu = hasFpu;
                                    machineSpecs.hasMips = hasMips;
                                    machineSpecs.hasNeon = hasNeon;
                                    machineSpecs.hasX86 = hasX86;
                                    machineSpecs.is64bits = is64bits;
                                    machineSpecs.bogoMIPS = bogoMIPS;
                                    machineSpecs.processors = processors;
                                    machineSpecs.frequency = frequency;
                                    return true;
                                } catch (Throwable th2) {
                                    th = th2;
                                    br3 = br4;
                                    fileReader3 = fileReader4;
                                    if (br3 != null) {
                                        try {
                                            br3.close();
                                        } catch (IOException e18) {
                                        }
                                    }
                                    if (fileReader3 == null) {
                                        throw th;
                                    }
                                    try {
                                        fileReader3.close();
                                        throw th;
                                    } catch (IOException e19) {
                                        throw th;
                                    }
                                }
                            } catch (IOException e20) {
                                fileReader3 = fileReader4;
                            } catch (NumberFormatException e21) {
                                fileReader3 = fileReader4;
                            } catch (Throwable th3) {
                                th = th3;
                                fileReader3 = fileReader4;
                            }
                        } catch (IOException e22) {
                        } catch (NumberFormatException e23) {
                        }
                        errorMsg = null;
                        isCompatible = true;
                        machineSpecs = new MachineSpecs();
                        machineSpecs.hasArmV6 = hasArmV6;
                        machineSpecs.hasArmV7 = hasArmV7;
                        machineSpecs.hasFpu = hasFpu;
                        machineSpecs.hasMips = hasMips;
                        machineSpecs.hasNeon = hasNeon;
                        machineSpecs.hasX86 = hasX86;
                        machineSpecs.is64bits = is64bits;
                        machineSpecs.bogoMIPS = bogoMIPS;
                        machineSpecs.processors = processors;
                        machineSpecs.frequency = frequency;
                        return true;
                    } catch (Throwable th4) {
                        th = th4;
                    }
                } catch (IOException e24) {
                    ex = e24;
                    fileReader = fileReader2;
                } catch (Throwable th5) {
                    th = th5;
                    fileReader = fileReader2;
                }
            } catch (Throwable th6) {
                th = th6;
            }
        } catch (IOException e25) {
            ex = e25;
        }
    }

    public static MachineSpecs getMachineSpecs() {
        return machineSpecs;
    }

    private static class ElfData {
        String att_arch;
        boolean att_fpu;
        int e_machine;
        int e_shnum;
        int e_shoff;
        boolean is64bits;
        ByteOrder order;
        int sh_offset;
        int sh_size;

        private ElfData() {
        }

        /* synthetic */ ElfData(ElfData elfData) {
            this();
        }
    }

    private static File searchLibrary(Context context) {
        String[] libraryPaths;
        ApplicationInfo applicationInfo = context.getApplicationInfo();
        if ((applicationInfo.flags & 1) != 0) {
            String property = System.getProperty("java.library.path");
            libraryPaths = property.split(":");
        } else {
            libraryPaths = new String[2];
            if (isGingerbreadOrLater()) {
                libraryPaths[0] = applicationInfo.nativeLibraryDir;
                libraryPaths[1] = String.valueOf(applicationInfo.dataDir) + "/libs";
            } else {
                libraryPaths[0] = String.valueOf(applicationInfo.dataDir) + "/lib";
                libraryPaths[1] = String.valueOf(applicationInfo.dataDir) + "/libs";
            }
        }
        if (libraryPaths == null) {
            Log.e(TAG, "can't find library path");
            return null;
        }
        for (String libraryPath : libraryPaths) {
            File lib = new File(libraryPath, "libvlcjni.so");
            if (lib.exists() && lib.canRead()) {
                return lib;
            }
        }
        Log.e(TAG, "WARNING: Can't find shared library");
        return null;
    }

    private static ElfData readLib(File file) throws Throwable {
        RandomAccessFile in = null;
        try {
            try {
                RandomAccessFile in2 = new RandomAccessFile(file, "r");
                try {
                    ElfData elf = new ElfData(null);
                    if (!readHeader(in2, elf)) {
                        if (in2 != null) {
                            try {
                                in2.close();
                            } catch (IOException e) {
                            }
                        }
                        return null;
                    }
                    switch (elf.e_machine) {
                        case 3:
                        case 8:
                        case 62:
                        case 183:
                            if (in2 != null) {
                                try {
                                    in2.close();
                                } catch (IOException e2) {
                                }
                            }
                            return elf;
                        case 40:
                            in2.close();
                            in = new RandomAccessFile(file, "r");
                            if (!readSection(in, elf)) {
                                if (in != null) {
                                    try {
                                        in.close();
                                    } catch (IOException e3) {
                                    }
                                }
                                return null;
                            }
                            in.close();
                            in2 = new RandomAccessFile(file, "r");
                            if (readArmAttributes(in2, elf)) {
                                if (in2 != null) {
                                    try {
                                        in2.close();
                                    } catch (IOException e4) {
                                    }
                                }
                                return elf;
                            }
                            if (in2 != null) {
                                try {
                                    in2.close();
                                } catch (IOException e5) {
                                }
                            }
                            return null;
                        default:
                            if (in2 != null) {
                                try {
                                    in2.close();
                                } catch (IOException e6) {
                                }
                            }
                            return null;
                    }
                } catch (FileNotFoundException e7) {
                    e = e7;
                    in = in2;
                    e.printStackTrace();
                    if (in != null) {
                        try {
                            in.close();
                        } catch (IOException e8) {
                        }
                    }
                    return null;
                } catch (IOException e9) {
                    e = e9;
                    in = in2;
                    e.printStackTrace();
                    if (in != null) {
                        try {
                            in.close();
                        } catch (IOException e10) {
                        }
                    }
                    return null;
                } catch (Throwable th) {
                    th = th;
                    in = in2;
                    if (in != null) {
                        try {
                            in.close();
                        } catch (IOException e11) {
                        }
                    }
                    throw th;
                }
            } catch (FileNotFoundException e12) {
                e = e12;
            } catch (IOException e13) {
                e = e13;
            }
        } catch (Throwable th2) {
            th = th2;
        }
    }

    private static boolean readHeader(RandomAccessFile in, ElfData elf) throws IOException {
        byte[] bytes = new byte[52];
        in.readFully(bytes);
        if (bytes[0] != Byte.MAX_VALUE || bytes[1] != 69 || bytes[2] != 76 || bytes[3] != 70 || (bytes[4] != 1 && bytes[4] != 2)) {
            Log.e(TAG, "ELF header invalid");
            return false;
        }
        elf.is64bits = bytes[4] == 2;
        elf.order = bytes[5] == 1 ? ByteOrder.LITTLE_ENDIAN : ByteOrder.BIG_ENDIAN;
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        buffer.order(elf.order);
        elf.e_machine = buffer.getShort(18);
        elf.e_shoff = buffer.getInt(32);
        elf.e_shnum = buffer.getShort(48);
        return true;
    }

    private static boolean readSection(RandomAccessFile in, ElfData elf) throws IOException {
        byte[] bytes = new byte[40];
        in.seek(elf.e_shoff);
        for (int i = 0; i < elf.e_shnum; i++) {
            in.readFully(bytes);
            ByteBuffer buffer = ByteBuffer.wrap(bytes);
            buffer.order(elf.order);
            int sh_type = buffer.getInt(4);
            if (sh_type == SHT_ARM_ATTRIBUTES) {
                elf.sh_offset = buffer.getInt(16);
                elf.sh_size = buffer.getInt(20);
                return true;
            }
        }
        return false;
    }

    private static boolean readArmAttributes(RandomAccessFile in, ElfData elf) throws IOException {
        byte[] bytes = new byte[elf.sh_size];
        in.seek(elf.sh_offset);
        in.readFully(bytes);
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        buffer.order(elf.order);
        if (buffer.get() != 65) {
            return false;
        }
        while (buffer.remaining() > 0) {
            int start_section = buffer.position();
            int length = buffer.getInt();
            String vendor = getString(buffer);
            if (vendor.equals("aeabi")) {
                while (buffer.position() < start_section + length) {
                    int start = buffer.position();
                    int tag = buffer.get();
                    int size = buffer.getInt();
                    if (tag != 1) {
                        buffer.position(start + size);
                    } else {
                        while (buffer.position() < start + size) {
                            int tag2 = getUleb128(buffer);
                            if (tag2 == 6) {
                                int arch = getUleb128(buffer);
                                elf.att_arch = CPU_archs[arch];
                            } else if (tag2 == 27) {
                                getUleb128(buffer);
                                elf.att_fpu = true;
                            } else {
                                int tag3 = tag2 % 128;
                                if (tag3 == 4 || tag3 == 5 || tag3 == 32 || (tag3 > 32 && (tag3 & 1) != 0)) {
                                    getString(buffer);
                                } else {
                                    getUleb128(buffer);
                                }
                            }
                        }
                    }
                }
                return true;
            }
        }
        return true;
    }

    private static String getString(ByteBuffer buffer) {
        char c;
        StringBuilder sb = new StringBuilder(buffer.limit());
        while (buffer.remaining() > 0 && (c = (char) buffer.get()) != 0) {
            sb.append(c);
        }
        return sb.toString();
    }

    private static int getUleb128(ByteBuffer buffer) {
        int c;
        int ret = 0;
        do {
            c = buffer.get();
            ret = (ret << 7) | (c & TransportMediator.KEYCODE_MEDIA_PAUSE);
        } while ((c & 128) > 0);
        return ret;
    }
}
