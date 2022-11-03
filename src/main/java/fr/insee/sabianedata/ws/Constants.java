package fr.insee.sabianedata.ws;

import java.io.File;
import java.io.InputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class Constants {


	private static final Logger logger = LoggerFactory.getLogger(Constants.class);

	private Constants() {
	}

	public static final String XSLT_FOLDER_PATH = "/xslt";

	public static final String PEARL_CAMPAIGN_FODS_TO_XML = XSLT_FOLDER_PATH + "/pearl-campaign-fods-to-xml.xsl";
	public static final String GEOGRAHICAL_LOCATIONS_FODS_TO_XML = XSLT_FOLDER_PATH + "/geographicalLocations-fods-to-xml.xsl";
	public static final String QUEEN_CAMPAIGN_FODS_TO_XML = XSLT_FOLDER_PATH + "/queen-campaign-fods-to-xml.xsl";
	public static final String SAMPLE_PROCESSING_GENERATOR = XSLT_FOLDER_PATH + "/sampleProcessing-generator.xsl";
	public static final String TEMP_FOLDER_PATH = System.getProperty("java.io.tmpdir") + "/build-test-data";

	public static final String QUEEN_EXTRACT_CAMPAIGN = XSLT_FOLDER_PATH + "/queen-extract-campaign.xsl";
	public static final String QUEEN_EXTRACT_QUESTIONNAIRE = XSLT_FOLDER_PATH + "/queen-extract-questionnaire-models.xsl";
	public static final String QUEEN_EXTRACT_SURVEYUNITS = XSLT_FOLDER_PATH + "/queen-extract-survey-units.xsl";
	public static final String QUEEN_EXTRACT_NOMENCLATURES = XSLT_FOLDER_PATH + "/queen-extract-nomenclatures.xsl";

	public static final String PEARL_EXTRACT_CAMPAIGN = XSLT_FOLDER_PATH + "/pearl-extract-campaign.xsl";
	public static final String PEARL_EXTRACT_ASSIGNEMENT = XSLT_FOLDER_PATH + "/pearl-extract-assignement.xsl";
	public static final String PEARL_EXTRACT_SURVEYUNITS = XSLT_FOLDER_PATH + "/pearl-extract-survey-units.xsl";
	public static final String PEARL_EXTRACT_INTERVIEWERS = XSLT_FOLDER_PATH + "/pearl-extract-interviewers.xsl";
	public static final String PEARL_EXTRACT_CONTEXT = XSLT_FOLDER_PATH + "/pearl-extract-context.xsl";

	public static final String NOMENCLATURES = "nomenclatures";
	public static final String QUESTIONNAIRE_MODELS = "questionnaireModels";
	public static final String CAMPAIGN = "campaign";
	public static final String SURVEY_UNITS = "surveyUnits";
	public static final String ASSIGNEMENT = "assignement";
	public static final String INTERVIEWERS = "interviewers";
	public static final String CONTEXT = "context";
	public static final String GEO_LOCATIONS = "geoLocations";

	public static final File EXAMPLE_NOMENCLATURES_FOLDER = getFile("/example/nomenclatures");
	public static final File EXAMPLE_QUESTIONNAIRES_FOLDER = getFile("/example/questionnaireModels");
	public static final File EXAMPLE_SURVEYUNITS_FOLDER = getFile("/example/surveyUnits");


	public static final File EXAMPLE_PEARL_CAMPAIGN = getFile("/example/pearl-campaign.fods");
	public static final File EXAMPLE_QUEEN_CAMPAIGN = getFile("/example/queen-campaign.fods");
	public static final File EXAMPLE_QUESTIONNAIRE = getFile("/example/questionnaire.xml");


	public static final File EXAMPLE_README = getFile("/example/LISEZ-MOI.txt");

	
	public static final File TEMP_FOLDER = getFile(TEMP_FOLDER_PATH);
	
	// ---------- Utilies
	/** Generic file getter from classpath 
	 * @return the file or null when not found.
	 * */
	public static InputStream getInputStreamFromPath(String path) {
		logger.debug("Loading " + path);
		try {
			return Constants.class.getResourceAsStream(path);
		} catch (Exception e) {
			logger.error("Error when loading file");
			return null;
		}
	}
	
	private static File getFile(String path)  {
		try {
			return new File(Constants.class.getResource(path).toURI());
		} catch (Exception e) {
			return null;
		}
	}

	
}
