package fr.insee.sabianedata.ws.model.queen;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@JacksonXmlRootElement(localName = "StateData")
@NoArgsConstructor
@Getter
@Setter
public class StateData {

    @JacksonXmlProperty(localName = "State")
    private String state;

    @JacksonXmlProperty(localName = "Date")
    private Long date;

    @JacksonXmlProperty(localName = "CurrentPage")
    private String currentPage;

}
