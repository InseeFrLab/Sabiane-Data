package fr.insee.sabianedata.ws.model.pearl;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import java.util.ArrayList;

@JacksonXmlRootElement(localName = "Person")
public class Person {

    @JacksonXmlProperty(localName = "FirstName")
    private String firstName;
    @JacksonXmlProperty(localName = "LastName")
    private String lastName;
    @JacksonXmlProperty(localName = "Title")
    private String title;
    @JacksonXmlProperty(localName = "Email")
    private String email;
    @JacksonXmlProperty(localName = "Privileged")
    private boolean privileged;
    @JacksonXmlProperty(localName = "FavoriteEmail")
    private boolean favoriteEmail;
    @JacksonXmlProperty(localName = "BirthDate")
    private String birthdateString;
    private Long birthdate;

    @JacksonXmlElementWrapper(localName = "PhoneNumbers")
    private ArrayList<PhoneNumber> phoneNumbers;

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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isPrivileged() {
        return privileged;
    }

    public void setPrivileged(boolean privileged) {
        this.privileged = privileged;
    }

    public boolean isFavoriteEmail() {
        return favoriteEmail;
    }

    public void setFavoriteEmail(boolean favoriteEmail) {
        this.favoriteEmail = favoriteEmail;
    }

    public ArrayList<PhoneNumber> getPhoneNumbers() {
        return phoneNumbers;
    }

    public void setPhoneNumbers(ArrayList<PhoneNumber> phoneNumbers) {
        this.phoneNumbers = phoneNumbers;
    }

    public Long getBirthdate() {
        return this.birthdate;
    }

    public void setBirthdate(Long birthdate) {
        this.birthdate = birthdate;
    }

    public String getBirthdateString() {
        return this.birthdateString;
    }

    public void setBirthdateString(String birthdateString) {
        this.birthdateString = birthdateString;
        this.birthdate = Long.parseLong(birthdateString);
    }

    public Person() {
    }
}
