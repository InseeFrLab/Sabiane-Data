package fr.insee.sabianedata.ws.model.massiveAttack;

import fr.insee.sabianedata.ws.model.pearl.PearlSurveyUnit;
import fr.insee.sabianedata.ws.model.queen.QueenSurveyUnit;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class MassiveSurveyUnit {

    private String id;
    private PearlSurveyUnit pearlSurveyUnit;
    private QueenSurveyUnit queenSurveyUnit;
}
