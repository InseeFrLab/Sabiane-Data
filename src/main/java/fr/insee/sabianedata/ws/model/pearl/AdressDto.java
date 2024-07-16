package fr.insee.sabianedata.ws.model.pearl;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@JacksonXmlRootElement(localName = "Adress")
@NoArgsConstructor
@Getter
@Setter
public class AdressDto {

    @JacksonXmlProperty(localName = "L1")
    private String l1;
    @JacksonXmlProperty(localName = "L2")
    private String l2;
    @JacksonXmlProperty(localName = "L3")
    private String l3;
    @JacksonXmlProperty(localName = "L4")
    private String l4;
    @JacksonXmlProperty(localName = "L5")
    private String l5;
    @JacksonXmlProperty(localName = "L6")
    private String l6;
    @JacksonXmlProperty(localName = "L7")
    private String l7;
    @JacksonXmlProperty(localName = "Elevator")
    private Boolean elevator;
    @JacksonXmlProperty(localName = "Building")
    private String building;
    @JacksonXmlProperty(localName = "Floor")
    private String floor;
    @JacksonXmlProperty(localName = "Door")
    private String door;
    @JacksonXmlProperty(localName = "Staircase")
    private String staircase;
    @JacksonXmlProperty(localName = "CityPriorityDistrict")
    private Boolean cityPriorityDistrict;

}
