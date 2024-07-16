package fr.insee.sabianedata.ws.model.queen;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

@JacksonXmlRootElement(localName = "SurveyUnit")
public class SurveyUnit {

    @JacksonXmlProperty(localName = "Id")
    @JsonProperty("id")
    private String id;

    @JacksonXmlProperty(localName = "QuestionnaireId")
    private String QuestionnaireId;

    @JacksonXmlProperty(localName = "StateDataFile")
    private String stateDataFile;

    @JacksonXmlProperty(localName = "PersonalizationFile")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String personalizationFile;

    @JacksonXmlProperty(localName = "DataFile")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String dataFile;

    @JacksonXmlProperty(localName = "CommentFile")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String commentFile;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getQuestionnaireId() {
        return QuestionnaireId;
    }

    public void setQuestionnaireId(String questionnaireId) {
        QuestionnaireId = questionnaireId;
    }

    public String getStateDataFile() {
        return stateDataFile;
    }

    public void setStateDataFile(String stateDataFile) {
        this.stateDataFile = stateDataFile;
    }

    public String getPersonalizationFile() {
        return personalizationFile;
    }

    public void setPersonalizationFile(String personalizationFile) {
        this.personalizationFile = personalizationFile;
    }

    public String getDataFile() {
        return dataFile;
    }

    public void setDataFile(String dataFile) {
        this.dataFile = dataFile;
    }

    public String getCommentFile() {
        return commentFile;
    }

    public void setCommentFile(String commentFile) {
        this.commentFile = commentFile;
    }

    public SurveyUnit(String id, String questionnaireId, String stateDataFile) {
        this.id = id;
        this.QuestionnaireId = questionnaireId;
        this.stateDataFile = stateDataFile;
    }

    public SurveyUnit(String id, String questionnaireId, String stateDataFile, String dataFile, String commentFile,
            String personalizationFile) {
        this.id = id;
        this.QuestionnaireId = questionnaireId;
        this.stateDataFile = stateDataFile;
        this.dataFile = dataFile;
        this.commentFile = commentFile;
        this.personalizationFile = personalizationFile;
    }

    public SurveyUnit(SurveyUnit su) {
        this(su.getId(), su.getQuestionnaireId(), su.getStateDataFile(), su.getDataFile(), su.getCommentFile(),
                su.getPersonalizationFile());
    }

    public SurveyUnit() {
    }
}
