package fr.insee.sabianedata.ws.model.queen;

import java.io.File;

import com.fasterxml.jackson.databind.JsonNode;

import fr.insee.sabianedata.ws.Constants;
import fr.insee.sabianedata.ws.utils.JsonFileToJsonNode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NomenclatureDto extends Nomenclature {

    private JsonNode value;

    public NomenclatureDto(Nomenclature nomenclature, String folder) {
        super(nomenclature.getId(), nomenclature.getLabel());
        File nomenclatureFile = new File(
                folder + File.separator + Constants.NOMENCLATURES + File.separator + nomenclature.getFileName());
        this.value = JsonFileToJsonNode.getJsonNodeFromFile(nomenclatureFile);
    }

}
