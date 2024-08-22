package fr.insee.sabianedata.ws.model.queen;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@JacksonXmlRootElement(localName = "Variable")
@NoArgsConstructor
@Getter
@Setter
public class Variable {

    @JacksonXmlProperty(localName = "Name")
    private String name;

    @JacksonXmlProperty(localName = "Value")
    private String value;

}
