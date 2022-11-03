package fr.insee.sabianedata.ws.model.pearl;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

@JacksonXmlRootElement(localName = "SurveyUnitIdentification")

public class Identification {

    @JacksonXmlProperty(localName = "Identification")
    @JsonProperty(value = "identification")
    private String identification;
    @JacksonXmlProperty(localName = "Access")
    @JsonProperty(value = "access")
    private String access;
    @JacksonXmlProperty(localName = "Situation")
    @JsonProperty(value = "situation")
    private String situation;
    @JacksonXmlProperty(localName = "Category")
    @JsonProperty(value = "category")
    private String category;
    @JacksonXmlProperty(localName = "Occupant")
    @JsonProperty(value = "occupant")
    private String occupant;

    public Identification() {
    }

    public String getIdentification() {
        return this.identification;
    }

    public void setIdentification(String identification) {
        if (StringUtils.isNotBlank(identification))
            this.identification = identification;
    }

    public String getAccess() {
        return this.access;
    }

    public void setAccess(String access) {
        if (StringUtils.isNotBlank(access))
            this.access = access;
    }

    public String getSituation() {
        return this.situation;
    }

    public void setSituation(String situation) {
        if (StringUtils.isNotBlank(situation))
            this.situation = situation;
    }

    public String getCategory() {
        return this.category;
    }

    public void setCategory(String category) {
        if (StringUtils.isNotBlank(category))
            this.category = category;
    }

    public String getOccupant() {
        return this.occupant;
    }

    public void setOccupant(String occupant) {
        if (StringUtils.isNotBlank(occupant))
            this.occupant = occupant;
    }

}
