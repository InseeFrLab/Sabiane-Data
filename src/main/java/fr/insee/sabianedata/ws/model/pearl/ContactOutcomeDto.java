package fr.insee.sabianedata.ws.model.pearl;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import fr.insee.sabianedata.ws.utils.DateParser;
import lombok.NoArgsConstructor;

@JacksonXmlRootElement(localName = "ContactOutcome")
@NoArgsConstructor
public class ContactOutcomeDto {

    @JacksonXmlProperty(localName = "Value")
    private String type;
    @JacksonXmlProperty(localName = "AttemptsNumber")
    private String attemptsNumber;
    @JacksonXmlProperty(localName = "Date")
    private String dateString;

    private Long date;
    private int totalNumberOfContactAttempts;

    public ContactOutcomeDto(ContactOutcomeDto co, Long reference) throws IllegalArgumentException {
        this.type = co.getType();
        this.dateString = co.getDateString();
        this.date = DateParser.relativeDateParse(co.getDateString(), reference);
        this.totalNumberOfContactAttempts = co.getTotalNumberOfContactAttempts();
    }

    public Long getDate() {
        return date;
    }

    public void setDate(Long date) {
        this.date = date;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getTotalNumberOfContactAttempts() {
        return totalNumberOfContactAttempts;
    }

    public void setAttemptsNumber(String attemptsNumber) {
        this.totalNumberOfContactAttempts = Integer.parseInt(attemptsNumber);
    }

    public String getDateString() {
        return dateString;
    }

    public void setDateString(String dateString) {
        this.dateString = dateString;
    }

}
