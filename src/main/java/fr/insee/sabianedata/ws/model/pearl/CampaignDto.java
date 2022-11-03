package fr.insee.sabianedata.ws.model.pearl;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import java.util.List;

@JacksonXmlRootElement(localName = "Campaign")
public class CampaignDto {

    @JacksonXmlProperty(localName = "Campaign")
    private String campaign;

    @JacksonXmlProperty(localName = "CampaignLabel")
    private String campaignLabel;

    @JacksonXmlProperty(localName = "Email")
    private String email;

    @JacksonXmlProperty(localName = "IdentificationConfiguration")
    private String identificationConfiguration;

    @JacksonXmlProperty(localName = "ContactAttemptConfiguration")
    private String contactAttemptConfiguration;

    @JacksonXmlProperty(localName = "ContactOutcomeConfiguration")
    private String contactOutcomeConfiguration;

    @JacksonXmlElementWrapper(localName = "Visibilities")
    private List<Visibility> visibilities;

    @JacksonXmlElementWrapper(localName = "Referents")
    private List<Referent> referents;

    public CampaignDto() {
    }

    public String getCampaign() {
        return campaign;
    }

    public void setCampaign(String campaign) {
        this.campaign = campaign;
    }

    public String getCampaignLabel() {
        return campaignLabel;
    }

    public void setCampaignLabel(String campaignLabel) {
        this.campaignLabel = campaignLabel;
    }

    public List<Visibility> getVisibilities() {
        return visibilities;
    }

    public void setVisibilities(List<Visibility> visibilities) {
        this.visibilities = visibilities;
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<Referent> getReferents() {
        return this.referents;
    }

    public void setReferents(List<Referent> referents) {
        this.referents = referents;
    }

    public String getIdentificationConfiguration() {
        return this.identificationConfiguration;
    }

    public void setIdentificationConfiguration(String identificationConfiguration) {
        this.identificationConfiguration = identificationConfiguration;
    }

    public String getContactAttemptConfiguration() {
        return this.contactAttemptConfiguration;
    }

    public void setContactAttemptConfiguration(String contactAttemptConfiguration) {
        this.contactAttemptConfiguration = contactAttemptConfiguration;
    }

    public String getContactOutcomeConfiguration() {
        return this.contactOutcomeConfiguration;
    }

    public void setContactOutcomeConfiguration(String contactOutcomeConfiguration) {
        this.contactOutcomeConfiguration = contactOutcomeConfiguration;
    }

}
