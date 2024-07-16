package fr.insee.sabianedata.ws.model.queen;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import java.util.List;

@JacksonXmlRootElement(localName = "Nomenclatures")
public class Nomenclatures {

    @JacksonXmlProperty(localName = "Nomenclature")
    @JacksonXmlElementWrapper(useWrapping = false)
    private List<Nomenclature> nomenclatures;

    public Nomenclatures() {
    }

    public List<Nomenclature> getNomenclatures() {
        return nomenclatures;
    }

    public void setNomenclatures(List<Nomenclature> nomenclatures) {
        this.nomenclatures = nomenclatures;
    }
}
