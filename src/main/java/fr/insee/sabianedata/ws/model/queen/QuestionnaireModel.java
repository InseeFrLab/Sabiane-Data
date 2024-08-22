package fr.insee.sabianedata.ws.model.queen;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@JacksonXmlRootElement(localName = "QuestionnaireModel")
@Getter
@Setter
@NoArgsConstructor
public class QuestionnaireModel {

    @JacksonXmlProperty(localName = "Id")
    private String idQuestionnaireModel;

    @JacksonXmlProperty(localName = "Label")
    private String label;

    @JacksonXmlProperty(localName = "CampaignId")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String campaignId;

    @JacksonXmlElementWrapper(localName = "RequiredNomenclatures")
    @JacksonXmlProperty(localName = "Nomenclature")
    private List<String> requiredNomenclatureIds;

    @JacksonXmlProperty(localName = "FileName")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String fileName;

    public QuestionnaireModel(String idQuestionnaireModel, String label, List<String> requiredNomenclatureIds) {
        this.idQuestionnaireModel = idQuestionnaireModel;
        this.label = label;
        this.requiredNomenclatureIds = requiredNomenclatureIds;
    }
}
