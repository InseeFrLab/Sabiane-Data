package fr.insee.sabianedata.ws.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import fr.insee.sabianedata.ws.Constants;
import org.apache.commons.io.IOUtils;

public class FileArchiver {

	/**
	 * Add a file into Zip file.
	 *
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public static File createZipForExample()
			throws FileNotFoundException, IOException {

		File outputZip = Files.createTempFile("data-", ".zip").toFile();
		FileOutputStream fileOutputStream = new FileOutputStream(outputZip);
		ZipOutputStream zipStream = new ZipOutputStream(fileOutputStream);

		FileInputStream fileIS = new FileInputStream(Constants.EXAMPLE_QUEEN_CAMPAIGN);
		ZipEntry zipEntry = new ZipEntry(Constants.EXAMPLE_QUEEN_CAMPAIGN.getName());
		zipStream.putNextEntry(zipEntry);
		zipStream.write(IOUtils.toByteArray(fileIS));
		zipStream.closeEntry();
		fileIS.close();
		// zipFile(Constants.EXAMPLE_NOMENCLATURES_FOLDER, "nomenclatures",zipStream);
		zipFile(Constants.EXAMPLE_QUESTIONNAIRES_FOLDER, "questionnaireModels", zipStream);
		zipFile(Constants.EXAMPLE_SURVEYUNITS_FOLDER, "surveyUnits", zipStream);

		zipStream.close();
		fileOutputStream.close();

		return outputZip;
	}

	public static File zipFilesForCreateQueenCampaign(String folderTemp, List<File> rootFiles)
			throws FileNotFoundException, IOException {
		File outputZip = Files.createTempFile("data-", ".zip").toFile();
		FileOutputStream fileOutputStream = new FileOutputStream(outputZip);
		ZipOutputStream zipStream = new ZipOutputStream(fileOutputStream);

		for (File file : rootFiles) {
			FileInputStream fileIS = new FileInputStream(file);
			ZipEntry zipEntry = new ZipEntry(file.getName());
			zipStream.putNextEntry(zipEntry);
			zipStream.write(IOUtils.toByteArray(fileIS));
			zipStream.closeEntry();
			fileIS.close();
		}
		File nomenclatureFolder = new File(folderTemp + "/nomenclatures");
		File questionnaireModelsFolder = new File(folderTemp + "/questionnaireModels");
		zipFile(nomenclatureFolder, "nomenclatures", zipStream);
		zipFile(questionnaireModelsFolder, "questionnaireModels", zipStream);

		zipStream.close();
		fileOutputStream.close();

		return outputZip;
	}

	private static void zipFile(File fileToZip, String fileName, ZipOutputStream zipOut) throws IOException {
		if (fileToZip.isHidden()) {
			return;
		}
		if (fileToZip.isDirectory()) {
			if (fileName.endsWith("/")) {
				zipOut.putNextEntry(new ZipEntry(fileName));
				zipOut.closeEntry();
			} else {
				zipOut.putNextEntry(new ZipEntry(fileName + "/"));
				zipOut.closeEntry();
			}
			File[] children = fileToZip.listFiles();
			for (File childFile : children) {
				zipFile(childFile, fileName + "/" + childFile.getName(), zipOut);
			}
			return;
		}
		FileInputStream fileIS = new FileInputStream(fileToZip);
		ZipEntry zipEntry = new ZipEntry(fileName);
		zipOut.putNextEntry(zipEntry);
		zipOut.write(IOUtils.toByteArray(fileIS));
		zipOut.closeEntry();
		fileIS.close();
	}

}
