package fr.insee.sabianedata.ws.model.queen;

import java.io.File;

import com.fasterxml.jackson.databind.JsonNode;

import fr.insee.sabianedata.ws.utils.JsonFileToJsonNode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class QueenSurveyUnit extends SurveyUnit {

    public static final String FOLDER = "surveyUnits";

    private JsonNode data;
    private JsonNode comment;
    private JsonNode personalization;
    private JsonNode stateData;

    public QueenSurveyUnit(SurveyUnit surveyUnit, String folder) {
        super(surveyUnit.getId(), surveyUnit.getQuestionnaireId(), surveyUnit.getStateDataFile());
        String finalFolder = folder + File.separator + FOLDER;
        File dtodataFile = new File(finalFolder + File.separator + surveyUnit.getDataFile());
        File commentFile = new File(finalFolder + File.separator + surveyUnit.getCommentFile());
        File personalizationFile = new File(finalFolder + File.separator + surveyUnit.getPersonalizationFile());
        this.data = JsonFileToJsonNode.getJsonNodeFromFile(dtodataFile);
        this.comment = JsonFileToJsonNode.getJsonNodeFromFile(commentFile);
        this.personalization = JsonFileToJsonNode.getJsonNodeFromFile(personalizationFile);
    }

    public QueenSurveyUnit(SurveyUnit surveyUnit) {
        super(surveyUnit.getId(), surveyUnit.getQuestionnaireId(), surveyUnit.getStateDataFile(),
                surveyUnit.getDataFile(),
                surveyUnit.getCommentFile(), surveyUnit.getPersonalizationFile());
    }

    public QueenSurveyUnit(QueenSurveyUnit suDto, SurveyUnit su) {
        super(su);
        this.data = suDto.getData();
        this.comment = suDto.getComment();
        this.personalization = suDto.getPersonalization();
        this.stateData = suDto.getStateData();
    }

    public void extractJsonFromFiles(String folder) {
        String finalFolder = folder + File.separator + FOLDER;
        File dtodataFile = new File(finalFolder + File.separator + getDataFile());
        File commentFile = new File(finalFolder + File.separator + getCommentFile());
        File personalizationFile = new File(finalFolder + File.separator + getPersonalizationFile());
        // handle previous data structure
        populateStateData(finalFolder);

        setData(JsonFileToJsonNode.getJsonNodeFromFile(dtodataFile));
        setComment(JsonFileToJsonNode.getJsonNodeFromFile(commentFile));
        setPersonalization(JsonFileToJsonNode.getJsonNodeFromFile(personalizationFile));

        // to prevent jsonification to API calls
        setDataFile(null);
        setCommentFile(null);
        setPersonalizationFile(null);
        setStateDataFile(null);
    }

    private void populateStateData(String finalFolder) {
        String sdf = getStateDataFile();
        if (sdf == null || sdf.isEmpty()) {
            setStateData(null);
        } else {
            File stateDataFile = new File(finalFolder + File.separator + getStateDataFile());
            setStateData(JsonFileToJsonNode.getJsonNodeFromFile(stateDataFile));
        }
    }

}
