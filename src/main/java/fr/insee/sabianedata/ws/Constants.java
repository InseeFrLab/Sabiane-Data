package fr.insee.sabianedata.ws;

import java.io.InputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class Constants {


	private static final Logger logger = LoggerFactory.getLogger(Constants.class);

	private Constants() {
	}

	public static final String XSLT_FOLDER_PATH = "/xslt";

	public static final String QUEEN_EXTRACT_CAMPAIGN = XSLT_FOLDER_PATH + "/queen-extract-campaign.xsl";
	public static final String QUEEN_EXTRACT_QUESTIONNAIRE = XSLT_FOLDER_PATH + "/queen-extract-questionnaire-models.xsl";
	public static final String QUEEN_EXTRACT_SURVEYUNITS = XSLT_FOLDER_PATH + "/queen-extract-survey-units.xsl";
	public static final String QUEEN_EXTRACT_NOMENCLATURES = XSLT_FOLDER_PATH + "/queen-extract-nomenclatures.xsl";

	public static final String PEARL_EXTRACT_CAMPAIGN = XSLT_FOLDER_PATH + "/pearl-extract-campaign.xsl";
	public static final String PEARL_EXTRACT_ASSIGNEMENT = XSLT_FOLDER_PATH + "/pearl-extract-assignement.xsl";
	public static final String PEARL_EXTRACT_SURVEYUNITS = XSLT_FOLDER_PATH + "/pearl-extract-survey-units.xsl";


	public static final String NOMENCLATURES = "nomenclatures";
	public static final String QUESTIONNAIRE_MODELS = "questionnaireModels";
	public static final String CAMPAIGN = "campaign";
	public static final String SURVEY_UNITS = "surveyUnits";

	// ---------- Utilies
	/** Generic file getter from classpath 
	 * @return the file or null when not found.
	 * */
	public static InputStream getInputStreamFromPath(String path) {
        logger.debug("Loading {}", path);
		try {
			return Constants.class.getResourceAsStream(path);
		} catch (Exception e) {
			logger.error("Error when loading file",e);
			return null;
		}
	}

	
}
