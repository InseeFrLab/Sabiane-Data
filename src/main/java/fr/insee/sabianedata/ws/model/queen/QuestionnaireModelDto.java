package fr.insee.sabianedata.ws.model.queen;

import com.fasterxml.jackson.databind.JsonNode;
import fr.insee.sabianedata.ws.utils.JsonFileToJsonNode;
import lombok.Getter;
import lombok.Setter;

import java.io.File;

@Getter
@Setter
public class QuestionnaireModelDto extends QuestionnaireModel {

    private JsonNode value;
    private static final String QUESTIONNAIRE_MODELS = "questionnaireModels";

    public QuestionnaireModelDto(QuestionnaireModel questionnaireModel, String folder) {
        super(questionnaireModel.getIdQuestionnaireModel(), questionnaireModel.getLabel(),
                questionnaireModel.getRequiredNomenclatureIds());
        File questionnaireFile = new File(
                folder + File.separator + QUESTIONNAIRE_MODELS + File.separator + questionnaireModel.getFileName());
        this.value = JsonFileToJsonNode.getJsonNodeFromFile(questionnaireFile);
    }

}
