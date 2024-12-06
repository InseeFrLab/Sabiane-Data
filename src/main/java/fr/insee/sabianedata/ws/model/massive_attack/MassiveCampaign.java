package fr.insee.sabianedata.ws.model.massive_attack;

import fr.insee.sabianedata.ws.model.pearl.PearlCampaign;
import fr.insee.sabianedata.ws.model.queen.QueenCampaign;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor()
public class MassiveCampaign {

    private PearlCampaign pearlCampaign;
    private QueenCampaign queenCampaign;

    public String getId(){
        return pearlCampaign.getCampaign();
    }

    public void updateCamapignsId(String newId){
        pearlCampaign.setCampaign(newId);
        queenCampaign.setId(newId);
    }

    public void updateLabel(String newLabel){
        pearlCampaign.setCampaignLabel(newLabel);
        queenCampaign.setLabel(newLabel);
    }
}
