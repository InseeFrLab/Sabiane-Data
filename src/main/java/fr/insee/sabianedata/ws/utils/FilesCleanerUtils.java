package fr.insee.sabianedata.ws.utils;

import java.io.*;
import java.nio.file.*;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class FilesCleanerUtils {

	private final static Logger logger = LoggerFactory.getLogger(FilesCleanerUtils.class);

	
	/**
	 * Method to clean all files in folder except one
	 * 
	 * @param folder : the folder to be cleaned
	 * @param generatedFile : the file to not delete
	 * @throws IOException : FileNotfound / NoAccess mainly
	 */
	public static void cleanOneFolderExceptGeneratedFile(File folder, File generatedFile) throws IOException  {
		logger.debug("Special cleaning into : " + folder+" (deleting all files except : "+generatedFile.getName() +")");
		if (folder.exists() && Files.isDirectory(folder.toPath())) {
			File[] matchCleaningInput = folder.listFiles(new FilenameFilter() {
				@Override
				public boolean accept(File dir, String name) {
					if(Files.isDirectory(Paths.get(dir+"/"+name))){
						try {
							cleanOneFolderExceptGeneratedFile(Paths.get(dir+"/"+name).toFile(),generatedFile);
						} catch (IOException e) {
							logger.error(e.getMessage());
						}
					}
					return !name.equals(generatedFile.getName());
				}
			});
			for(File file : matchCleaningInput) {
				if(Files.isDirectory(file.toPath())) {
					if(file.list().length==0) {
						FileUtils.forceDelete(file);
					}
				}
				else {
					FileUtils.forceDelete(file);
				}
			}		
		}
	}
		
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
				Files.createDirectories(fileSystem.getPath(uncompressedDirectory +"/"+ entry.getName()));
			}
			//Else create the file
			else {
				InputStream is = file.getInputStream(entry);
				BufferedInputStream bis = new BufferedInputStream(is);
				String uncompressedFileName = uncompressedDirectory +"/"+ entry.getName();
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
