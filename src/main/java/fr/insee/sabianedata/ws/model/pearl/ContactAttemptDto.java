package fr.insee.sabianedata.ws.model.pearl;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import fr.insee.sabianedata.ws.utils.DateParser;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@JacksonXmlRootElement(localName = "ContactAttempt")
@NoArgsConstructor
@Getter
@Setter
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

}
