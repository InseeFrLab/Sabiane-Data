package fr.insee.sabianedata.ws.model.pearl;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import fr.insee.sabianedata.ws.utils.DateParser;
import lombok.Getter;

@JacksonXmlRootElement(localName = "Visibility")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Visibility {
    // peut-être changer les date en type "long" au lieu de "String"

    @JacksonXmlProperty(localName = "OrganisationalUnit")
    private String organizationalUnit;
    @JacksonXmlProperty(localName = "CollectionStartDate")
    private String collectionStartDateString;
    @JacksonXmlProperty(localName = "CollectionEndDate")
    private String collectionEndDateString;
    @JacksonXmlProperty(localName = "IdentificationPhaseStartDate")
    private String identificationPhaseStartDateString;
    @JacksonXmlProperty(localName = "InterviewerStartDate")
    private String interviewerStartDateString;
    @JacksonXmlProperty(localName = "ManagementStartDate")
    private String managementStartDateString;
    @JacksonXmlProperty(localName = "EndDate")
    private String endDateString;

    private Long collectionStartDate;
    private Long collectionEndDate;
    private Long identificationPhaseStartDate;
    private Long interviewerStartDate;
    private Long managementStartDate;
    private Long endDate;
    @Getter
    private final String mail="no-mail-yet";
    @Getter
    private final String tel = "no-tel-yet";
    @Getter
    private final boolean useLetterCommunication = false;

    public Long getCollectionStartDate() {
        return collectionStartDate;
    }

    public void setCollectionStartDate(Long collectionStartDate) {
        this.collectionStartDate = collectionStartDate;
    }

    public Long getCollectionEndDate() {
        return collectionEndDate;
    }

    public void setCollectionEndDate(Long collectionEndDate) {
        this.collectionEndDate = collectionEndDate;
    }

    public Long getIdentificationPhaseStartDate() {
        return identificationPhaseStartDate;
    }

    public void setIdentificationPhaseStartDate(Long identificationPhaseStartDate) {
        this.identificationPhaseStartDate = identificationPhaseStartDate;
    }

    public Long getInterviewerStartDate() {
        return interviewerStartDate;
    }

    public void setInterviewerStartDate(Long interviewerStartDate) {
        this.interviewerStartDate = interviewerStartDate;
    }

    public Long getManagementStartDate() {
        return managementStartDate;
    }

    public void setManagementStartDate(Long managementStartDate) {
        this.managementStartDate = managementStartDate;
    }

    public Long getEndDate() {
        return endDate;
    }

    public void setEndDate(Long endDate) {
        this.endDate = endDate;
    }

    public Visibility(Visibility visibility) throws IllegalArgumentException {
        this.organizationalUnit = visibility.getOrganizationalUnit();
        this.collectionStartDate = DateParser.fixedDateParse(visibility.getCollectionStartDateString());
        this.collectionEndDate = DateParser.fixedDateParse(visibility.getCollectionEndDateString());
        this.identificationPhaseStartDate = DateParser
                .fixedDateParse(visibility.getIdentificationPhaseStartDateString());
        this.interviewerStartDate = DateParser.fixedDateParse(visibility.getInterviewerStartDateString());
        this.managementStartDate = DateParser.fixedDateParse(visibility.getManagementStartDateString());
        this.endDate = DateParser.fixedDateParse(visibility.getEndDateString());
        this.collectionStartDateString = visibility.getCollectionStartDateString();
        this.collectionEndDateString = visibility.getCollectionEndDateString();
        this.identificationPhaseStartDateString = visibility.getIdentificationPhaseStartDateString();
        this.interviewerStartDateString = visibility.getInterviewerStartDateString();
        this.managementStartDateString = visibility.getManagementStartDateString();
        this.endDateString = visibility.getEndDateString();
    }

    public Visibility(Visibility visibility, Long referenceDate) throws IllegalArgumentException {
        this.organizationalUnit = visibility.getOrganizationalUnit();
        this.collectionStartDate = DateParser.relativeDateParse(visibility.getCollectionStartDateString(),
                referenceDate);
        this.collectionEndDate = DateParser.relativeDateParse(visibility.getCollectionEndDateString(), referenceDate);
        this.identificationPhaseStartDate = DateParser
                .relativeDateParse(visibility.getIdentificationPhaseStartDateString(), referenceDate);
        this.interviewerStartDate = DateParser.relativeDateParse(visibility.getInterviewerStartDateString(),
                referenceDate);
        this.managementStartDate = DateParser.relativeDateParse(visibility.getManagementStartDateString(),
                referenceDate);
        this.endDate = DateParser.relativeDateParse(visibility.getEndDateString(), referenceDate);
    }

    public Visibility() {
    }

    public String getOrganizationalUnit() {
        return organizationalUnit;
    }

    public void setOrganizationalUnit(String organizationalUnit) {
        this.organizationalUnit = organizationalUnit;
    }

    public String getCollectionStartDateString() {
        return collectionStartDateString;
    }

    public void setCollectionStartDateString(String collectionStartDateString) {
        this.collectionStartDateString = collectionStartDateString;
    }

    public String getCollectionEndDateString() {
        return collectionEndDateString;
    }

    public void setCollectionEndDateString(String collectionEndDateString) {
        this.collectionEndDateString = collectionEndDateString;
    }

    public String getIdentificationPhaseStartDateString() {
        return identificationPhaseStartDateString;
    }

    public void setIdentificationPhaseStartDateString(String identificationPhaseStartDateString) {
        this.identificationPhaseStartDateString = identificationPhaseStartDateString;
    }

    public String getInterviewerStartDateString() {
        return interviewerStartDateString;
    }

    public void setInterviewerStartDateString(String interviewerStartDateString) {
        this.interviewerStartDateString = interviewerStartDateString;
    }

    public String getManagementStartDateString() {
        return managementStartDateString;
    }

    public void setManagementStartDateString(String managementStartDateString) {
        this.managementStartDateString = managementStartDateString;
    }

    public String getEndDateString() {
        return endDateString;
    }

    public void setEndDateString(String endDateString) {
        this.endDateString = endDateString;
    }
}
