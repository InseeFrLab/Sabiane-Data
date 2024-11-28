package fr.insee.sabianedata.ws.model.massiveAttack;

import java.util.List;

import fr.insee.sabianedata.ws.model.pearl.Assignement;
import fr.insee.sabianedata.ws.model.queen.NomenclatureDto;
import fr.insee.sabianedata.ws.model.queen.QuestionnaireModelDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class TrainingCourse {

    private List<MassiveSurveyUnit> surveyUnits;
    private MassiveCampaign campaign;
    private List<QuestionnaireModelDto> questionnaireModels;
    private List<NomenclatureDto> nomenclatures;
    private List<Assignement> assignements;





    public String getCampaignId() {
        return campaign.getId();
    }
}
