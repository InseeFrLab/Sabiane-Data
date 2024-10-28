package fr.insee.sabianedata.ws.utils;

import java.io.*;
import java.nio.file.*;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class FilesCleanerUtils {

    public static void unzip(File zipFile) throws Exception {
        File destDir = zipFile.getParentFile();
        ZipFile file = new ZipFile(zipFile);

        FileSystem fileSystem = FileSystems.getDefault();
        //Get file entries
        Enumeration<? extends ZipEntry> entries = file.entries();

        //We will unzip files in this folder
        String uncompressedDirectory = destDir.getAbsolutePath();

        //Iterate over entries
        while (entries.hasMoreElements()) {
            ZipEntry entry = entries.nextElement();
            //If directory then create a new directory in uncompressed folder
            if (entry.isDirectory()) {
                Files.createDirectories(fileSystem.getPath(uncompressedDirectory + "/" + entry.getName()));
            }
            //Else create the file
            else {
                InputStream is = file.getInputStream(entry);
                BufferedInputStream bis = new BufferedInputStream(is);
                String uncompressedFileName = uncompressedDirectory + "/" + entry.getName();
                Path uncompressedFilePath = fileSystem.getPath(uncompressedFileName);
                Files.createFile(uncompressedFilePath);
                FileOutputStream fileOutput = new FileOutputStream(uncompressedFileName);
                while (bis.available() > 0) {
                    fileOutput.write(bis.read());
                }
                fileOutput.close();
            }
        }
        file.close();

    }
}
