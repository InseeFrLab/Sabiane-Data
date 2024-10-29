package fr.insee.sabianedata.ws.service.xsl;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.insee.sabianedata.ws.Constants;

public class QueenCampaignTransformer {

    private static final XslTransformation saxonService = new XslTransformation();

    private static final Logger logger = LoggerFactory.getLogger(QueenCampaignTransformer.class);

    public File extractCampaign(File input) throws Exception {
        return extract(input, Constants.CAMPAIGN);
    }

    public File extractQuestionnaireModels(File input) throws Exception {
        return extract(input, Constants.QUESTIONNAIRE_MODELS);
    }

    public File extractSurveyUnits(File input) throws Exception {
        return extract(input, Constants.SURVEY_UNITS);
    }

    public File extractNomenclatures(File input) throws Exception {
        return extract(input, Constants.NOMENCLATURES);
    }

    public File extract(File input, String type) throws Exception {
        File outputFile = new File(input.getParent(),
                type + ".xml");
        logger.debug("Output folder : {}", outputFile.getAbsolutePath());

        InputStream inputStream = FileUtils.openInputStream(input);
        OutputStream outputStream = FileUtils.openOutputStream(outputFile);

        InputStream XSL = switch (type) {
            case Constants.CAMPAIGN -> Constants.getInputStreamFromPath(Constants.QUEEN_EXTRACT_CAMPAIGN);
            case Constants.QUESTIONNAIRE_MODELS ->
                    Constants.getInputStreamFromPath(Constants.QUEEN_EXTRACT_QUESTIONNAIRE);
            case Constants.SURVEY_UNITS -> Constants.getInputStreamFromPath(Constants.QUEEN_EXTRACT_SURVEYUNITS);
            case Constants.NOMENCLATURES -> Constants.getInputStreamFromPath(Constants.QUEEN_EXTRACT_NOMENCLATURES);
            default -> null;
        };

        try {
            saxonService.transformFods2XML(inputStream, outputStream, XSL);
        } catch (Exception e) {
            String errorMessage = "An error was occured during the operations fods2xml transformation.";
            logger.error(errorMessage, e);
            throw new Exception(errorMessage);
        }
        inputStream.close();
        outputStream.close();
        XSL.close();
        logger.info("End of extract queen {}", type);

        return outputFile;
    }
}
