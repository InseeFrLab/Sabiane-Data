package fr.insee.sabianedata.ws.model.pearl;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CampaignId {
    @JsonProperty("campaign")
    private String id;
    @JsonProperty("campaignLabel")
    private String label;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }


}