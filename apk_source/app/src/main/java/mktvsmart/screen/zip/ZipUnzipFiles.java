package mktvsmart.screen.zip;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import org.teleal.cling.model.ServiceReference;

/* loaded from: classes.dex */
public class ZipUnzipFiles {
    private File compressFile;
    private File uncompressFile;
    private ZipOutputStream zipOutStream;

    public ZipUnzipFiles(File toZipFile, File zipFile) {
        this.uncompressFile = toZipFile;
        this.compressFile = zipFile;
        try {
            this.zipOutStream = new ZipOutputStream(new FileOutputStream(zipFile));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public ZipUnzipFiles(String toZipFile) {
        this.uncompressFile = new File(toZipFile);
        this.compressFile = new File(String.valueOf(toZipFile) + ".zip");
        try {
            this.zipOutStream = new ZipOutputStream(new FileOutputStream(this.compressFile));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public ZipUnzipFiles(String toZipFile, String zipFile) {
        this.uncompressFile = new File(toZipFile);
        this.compressFile = new File(zipFile);
        try {
            this.zipOutStream = new ZipOutputStream(new FileOutputStream(this.compressFile));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public File zipFiles() throws IOException {
        return zipFiles(false);
    }

    public File zipFiles(boolean deletable) throws IOException {
        return zipFiles("", deletable);
    }

    public File zipFiles(String baseFolder) throws IOException {
        return zipFiles(baseFolder);
    }

    public File zipFiles(String baseFolder, boolean deletable) throws IOException {
        if (this.zipOutStream == null && this.compressFile != null) {
            this.zipOutStream = new ZipOutputStream(new FileOutputStream(this.compressFile));
        }
        boolean ret = zipFiles(this.zipOutStream, baseFolder, this.uncompressFile);
        if (this.uncompressFile != null && deletable) {
            this.uncompressFile.delete();
            this.uncompressFile = null;
        }
        this.zipOutStream.close();
        this.zipOutStream = null;
        if (ret) {
            return this.compressFile;
        }
        return null;
    }

    private boolean zipFiles(ZipOutputStream zipOutStream, String baseFolder, File toZipFile) throws IOException {
        String baseFolder2;
        if (zipOutStream == null || toZipFile == null) {
            return false;
        }
        if (toZipFile.isDirectory()) {
            if (baseFolder.length() == 0 || baseFolder.endsWith(ServiceReference.DELIMITER)) {
                baseFolder2 = String.valueOf(baseFolder) + toZipFile.getName() + ServiceReference.DELIMITER;
            } else {
                baseFolder2 = String.valueOf(baseFolder) + ServiceReference.DELIMITER + toZipFile.getName() + ServiceReference.DELIMITER;
            }
            zipOutStream.putNextEntry(new ZipEntry(baseFolder2));
            for (File file : toZipFile.listFiles()) {
                zipFiles(zipOutStream, baseFolder2, file);
            }
        } else {
            String baseFolder3 = String.valueOf(baseFolder) + toZipFile.getName();
            FileInputStream fileInStream = new FileInputStream(toZipFile);
            zipOutStream.putNextEntry(new ZipEntry(baseFolder3));
            byte[] buffer = new byte[1024];
            while (true) {
                int readCount = fileInStream.read(buffer);
                if (readCount <= 0) {
                    break;
                }
                zipOutStream.write(buffer, 0, readCount);
            }
            zipOutStream.flush();
            zipOutStream.closeEntry();
            fileInStream.close();
        }
        return true;
    }

    public boolean unzipFiles() {
        return true;
    }

    public File getToZipFile() {
        return this.uncompressFile;
    }

    public void setToZipFile(File toZipFile) {
        this.uncompressFile = toZipFile;
    }

    public File getZipFile() {
        return this.compressFile;
    }

    public void setZipFile(File zipFile) {
        this.compressFile = zipFile;
    }
}
