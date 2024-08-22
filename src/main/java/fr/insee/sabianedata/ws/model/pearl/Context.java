package fr.insee.sabianedata.ws.model.pearl;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@NoArgsConstructor
@Getter
@Setter
@JacksonXmlRootElement(localName = "Context")
public class Context {

    @JacksonXmlElementWrapper(localName = "OrganisationUnits")
    private List<OrganisationUnitContextDto> organisationUnits;

}
