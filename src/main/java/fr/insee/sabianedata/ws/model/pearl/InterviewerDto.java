package fr.insee.sabianedata.ws.model.pearl;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

@JacksonXmlRootElement(localName = "Interviewer")
public class InterviewerDto {

    @JacksonXmlProperty(localName="Id")
    private String id;
    @JacksonXmlProperty(localName="FirstName")
    private String firstName;
    @JacksonXmlProperty(localName="LastName")
    private String lastName;
    @JacksonXmlProperty(localName="Email")
    private String email;
    @JacksonXmlProperty(localName="PhoneNumber")
    private String phoneNumber; // TODO : correct to phoneNumber !

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public InterviewerDto() {
    }

    @Override
    public String toString() {
        return "InterviewerDto{" +
                "id='" + id + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                '}';
    }
}
