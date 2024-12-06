package fr.insee.sabianedata.ws.model.queen;

import java.io.File;

import com.fasterxml.jackson.databind.JsonNode;

import fr.insee.sabianedata.ws.utils.JsonFileToJsonNode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NomenclatureDto extends Nomenclature {

    private JsonNode value;
    private static final String NOMENCLATURES = "nomenclatures";

    public NomenclatureDto(Nomenclature nomenclature, String folder) {
        super(nomenclature.getId(), nomenclature.getLabel());
        File nomenclatureFile = new File(
                folder + File.separator + NOMENCLATURES + File.separator + nomenclature.getFileName());
        this.value = JsonFileToJsonNode.getJsonNodeFromFile(nomenclatureFile);
    }

}
