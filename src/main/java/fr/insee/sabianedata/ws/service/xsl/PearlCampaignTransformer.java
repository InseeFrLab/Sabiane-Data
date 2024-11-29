package fr.insee.sabianedata.ws.service.xsl;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;

import fr.insee.sabianedata.ws.utils.ExtractionType;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.insee.sabianedata.ws.Constants;
import org.springframework.stereotype.Service;

@Service
public class PearlCampaignTransformer {

    private static final XslTransformation saxonService = new XslTransformation();

    private static final Logger logger = LoggerFactory.getLogger(PearlCampaignTransformer.class);

    public File extractCampaign(File input) throws Exception {
        return extract(input, ExtractionType.CAMPAIGN);
    }

    public File extractSurveyUnits(File input) throws Exception {
        return extract(input, ExtractionType.SURVEY_UNITS);
    }

    public File extractAssignement(File input) throws Exception {
        return extract(input, ExtractionType.ASSIGNEMENT);
    }

    public File extract(File input, ExtractionType type) throws Exception {
        File outputFile = new File(input.getParent(), type + ".xml");
        logger.debug("Output folder : {}", outputFile.getAbsolutePath());

        try (InputStream inputStream = FileUtils.openInputStream(input);
             OutputStream outputStream = FileUtils.openOutputStream(outputFile);

             InputStream xsl = switch (type) {
                 case CAMPAIGN -> Constants.getInputStreamFromPath(Constants.PEARL_EXTRACT_CAMPAIGN);
                 case ASSIGNEMENT -> Constants.getInputStreamFromPath(Constants.PEARL_EXTRACT_ASSIGNEMENT);
                 case SURVEY_UNITS -> Constants.getInputStreamFromPath(Constants.PEARL_EXTRACT_SURVEYUNITS);
             }) {
            saxonService.transformFods2XML(inputStream, outputStream, xsl);
        } catch (Exception e) {
            String errorMessage = "An error was occured during the operations fods2xml transformation. "
                    + e.getMessage();
            logger.error(errorMessage, e);
            throw new Exception(errorMessage);
        }
        logger.info("End of extract pearl {}", type);

        return outputFile;
    }
}
