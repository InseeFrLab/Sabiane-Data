package fr.insee.sabianedata.ws.model.pearl;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import fr.insee.sabianedata.ws.utils.DateParser;

@JacksonXmlRootElement(localName = "ContactAttempt")
public class ContactAttemptDto {

    @JacksonXmlProperty(localName = "Value")
    private String status;
    @JacksonXmlProperty(localName = "Date")
    private String dateString;
    @JacksonXmlProperty(localName = "Medium")
    private String medium;

    private Long date;

    public ContactAttemptDto(ContactAttemptDto ca, Long reference, String medium) {
        this.status = ca.getStatus();
        this.date = DateParser.relativeDateParse(ca.getDateString(), reference);
        this.medium = medium;
    }

    public ContactAttemptDto() {
    }

    public Long getDate() {
        return date;
    }

    public void setDate(Long date) {
        this.date = date;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDateString() {
        return dateString;
    }

    public void setDateString(String dateString) {
        this.dateString = dateString;
    }

    public String getMedium() {
        return this.medium;
    }

    public void setMedium(String medium) {
        this.medium = medium;
    }

}
