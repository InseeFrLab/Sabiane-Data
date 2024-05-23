package fr.insee.sabianedata.ws.model.queen;

import java.util.List;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JacksonXmlRootElement(localName = "MetadataValue")
public class MetadataValue {

    @JacksonXmlProperty(localName = "InseeContext")
    private String inseeContext;

    @JacksonXmlElementWrapper(localName = "Variables")
    private List<Variable> variables;

}
