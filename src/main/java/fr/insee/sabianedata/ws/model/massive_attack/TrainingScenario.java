package fr.insee.sabianedata.ws.model.massive_attack;

import java.util.List;

import fr.insee.sabianedata.ws.model.pearl.PearlCampaign;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class TrainingScenario {

    // TODO migrate to massiveCampaign
    private List<PearlCampaign> campaigns;
    private ScenarioType type;
    private String label;

}
