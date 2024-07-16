package fr.insee.sabianedata.ws.model.pearl;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@JacksonXmlRootElement(localName = "Interviewer")
@NoArgsConstructor
@Getter
@Setter
public class InterviewerDto {

    @JacksonXmlProperty(localName = "Id")
    private String id;
    @JacksonXmlProperty(localName = "FirstName")
    private String firstName;
    @JacksonXmlProperty(localName = "LastName")
    private String lastName;
    @JacksonXmlProperty(localName = "Email")
    private String email;
    @JacksonXmlProperty(localName = "PhoneNumber")
    private String phoneNumber;
    @JacksonXmlProperty(localName = "Title")
    private String title;

    @Override
    public String toString() {
        return "InterviewerDto{" +
                "id='" + id + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", title='" + title + '\'' +
                '}';
    }
}
