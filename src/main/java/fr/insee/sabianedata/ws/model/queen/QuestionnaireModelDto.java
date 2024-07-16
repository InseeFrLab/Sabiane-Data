package fr.insee.sabianedata.ws.model.queen;

import com.fasterxml.jackson.databind.JsonNode;
import fr.insee.sabianedata.ws.utils.JsonFileToJsonNode;

import java.io.File;

public class QuestionnaireModelDto extends QuestionnaireModel {

    public static final String FOLDER = "questionnaireModels";

    private JsonNode value;

    public QuestionnaireModelDto(QuestionnaireModel questionnaireModel, String folder) {
        super(questionnaireModel.getIdQuestionnaireModel(), questionnaireModel.getLabel(),
                questionnaireModel.getRequiredNomenclatureIds());
        File questionnaireFile = new File(
                folder + File.separator + FOLDER + File.separator + questionnaireModel.getFileName());
        this.value = JsonFileToJsonNode.getJsonNodeFromFile(questionnaireFile);
    }

    public JsonNode getValue() {
        return value;
    }

    public void setValue(JsonNode value) {
        this.value = value;
    }
}
