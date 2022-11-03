package fr.insee.sabianedata.ws.model.pearl;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

@JacksonXmlRootElement(localName = "Adress")
public class AdressDto {

    @JacksonXmlProperty(localName = "L1")
    @JsonProperty(value = "l1")
    private String L1;
    @JsonProperty(value = "l2")
    @JacksonXmlProperty(localName = "L2")
    private String L2;
    @JsonProperty(value = "l3")
    @JacksonXmlProperty(localName = "L3")
    private String L3;
    @JsonProperty(value = "l4")
    @JacksonXmlProperty(localName = "L4")
    private String L4;
    @JsonProperty(value = "l5")
    @JacksonXmlProperty(localName = "L5")
    private String L5;
    @JsonProperty(value = "l6")
    @JacksonXmlProperty(localName = "L6")
    private String L6;
    @JsonProperty(value = "l7")
    @JacksonXmlProperty(localName = "L7")
    private String L7;
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

    public String getL1() {
        return L1;
    }

    public void setL1(String l1) {
        L1 = l1;
    }

    public String getL2() {
        return L2;
    }

    public void setL2(String l2) {
        L2 = l2;
    }

    public String getL3() {
        return L3;
    }

    public void setL3(String l3) {
        L3 = l3;
    }

    public String getL4() {
        return L4;
    }

    public void setL4(String l4) {
        L4 = l4;
    }

    public String getL5() {
        return L5;
    }

    public void setL5(String l5) {
        L5 = l5;
    }

    public String getL6() {
        return L6;
    }

    public void setL6(String l6) {
        L6 = l6;
    }

    public String getL7() {
        return L7;
    }

    public void setL7(String l7) {
        L7 = l7;
    }

    public boolean isElevator() {
        return this.elevator;
    }

    public void setElevator(boolean elevator) {
        this.elevator = elevator;
    }

    public String getBuilding() {
        return this.building;
    }

    public void setBuilding(String building) {
        this.building = building;
    }

    public String getFloor() {
        return this.floor;
    }

    public void setFloor(String floor) {
        this.floor = floor;
    }

    public String getDoor() {
        return this.door;
    }

    public void setDoor(String door) {
        this.door = door;
    }

    public String getStaircase() {
        return this.staircase;
    }

    public void setStaircase(String staircase) {
        this.staircase = staircase;
    }

    public boolean isCityPriorityDistrict() {
        return this.cityPriorityDistrict;
    }

    public void setCityPriorityDistrict(boolean cityPriorityDistrict) {
        this.cityPriorityDistrict = cityPriorityDistrict;
    }

    public AdressDto() {
    }
}
