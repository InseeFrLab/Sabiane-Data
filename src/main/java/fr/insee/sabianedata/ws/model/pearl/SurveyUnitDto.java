package fr.insee.sabianedata.ws.model.pearl;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

@JacksonXmlRootElement(localName = "SurveyUnit")
@NoArgsConstructor
@Getter
@Setter
@Slf4j
public class SurveyUnitDto {

    @JacksonXmlProperty(localName = "Id")
    private String id;
    @JacksonXmlElementWrapper(localName = "Persons")
    private ArrayList<Person> persons;
    @JacksonXmlProperty(localName = "Address")
    private AdressDto address;
    @JacksonXmlProperty(localName = "OrganizationUnitId")
    private String organizationUnitId;
    @JacksonXmlProperty(localName = "Priority")
    private boolean priority;
    @JacksonXmlProperty(localName = "Campaign")
    private String campaign;
    @JacksonXmlProperty(localName = "SampleIdentifiers")
    private SampleIdentifiersDto sampleIdentifiers;
    @JacksonXmlProperty(localName = "Comment")
    private String comment;
    private List<CommentDto> comments;
    @JacksonXmlProperty(localName = "Move")
    private Boolean move;
    @JacksonXmlProperty(localName = "ContactOutcome")
    private ContactOutcomeDto contactOutcome;
    @JacksonXmlProperty(localName = "ContactAttempts")
    private ArrayList<ContactAttemptDto> contactAttempts = new ArrayList<>();
    @JacksonXmlProperty(localName = "States")
    private ArrayList<SurveyUnitStateDto> states = new ArrayList<>();
    @JacksonXmlProperty(localName = "SurveyUnitIdentification")
    @JsonProperty(value = "identification")
    private Identification identification;


    public void cleanAttributes(){
        this.states = states == null ? new ArrayList<>() : states;
        this.contactAttempts = contactAttempts == null ? new ArrayList<>() : contactAttempts;
        CommentDto interviewerComment = new CommentDto(CommentType.INTERVIEWER, comment);
        this.comments = List.of(interviewerComment);
    }

    public SurveyUnitDto(SurveyUnitDto su) {
        this.id = su.getId();
        this.persons = su.getPersons();
        this.address = su.getAddress();
        this.organizationUnitId = su.getOrganizationUnitId();
        this.priority = su.isPriority();
        this.campaign = su.getCampaign();
        this.sampleIdentifiers = su.getSampleIdentifiers();
        this.comment = su.getComment();
        this.comments = su.getComments();
        this.contactOutcome = su.getContactOutcome();
        this.contactAttempts = su.getContactAttempts();
        this.states = su.getStates();
        this.identification = su.getIdentification();
        this.move = su.getMove();
    }

    public String toString() {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.writeValueAsString(this);
        } catch (JsonProcessingException e) {
            log.warn("Coudn't stringify survey-unit", e);
        }
        return "";
    }
}
