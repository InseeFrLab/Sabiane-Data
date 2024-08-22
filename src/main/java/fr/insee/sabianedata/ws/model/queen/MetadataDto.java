package fr.insee.sabianedata.ws.model.queen;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import lombok.Getter;
import lombok.Setter;

@JacksonXmlRootElement(localName = "Metadata")
@Getter
@Setter
public class MetadataDto {

    @JacksonXmlProperty(localName = "MetadataValue")
    private MetadataValue value;

}
