package fr.insee.sabianedata.ws.service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;

import fr.insee.sabianedata.ws.model.queen.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class QueenExtractEntities {

    private final QueenTransformService queenTransformService;

    private final XmlMapper xmlMapper = new XmlMapper();

    public QueenCampaign getQueenCampaignFromXMLFile(File file) throws IOException {
        return xmlMapper.readValue(file, QueenCampaign.class);
    }

    public QueenCampaign getQueenCampaignFromFods(File fods) throws Exception {
        File file = queenTransformService.getQueenCampaign(fods);
        return getQueenCampaignFromXMLFile(file);
    }

    public List<QueenSurveyUnit> getQueenSurveyUnitsFromFods(File fods, String folder) throws Exception {
        File file = queenTransformService.getQueenSurveyUnits(fods);
        SurveyUnits surveyUnits = xmlMapper.readValue(file, SurveyUnits.class);
        return surveyUnits.getSurveyUnits().stream().map(s -> {
            QueenSurveyUnit suDto = new QueenSurveyUnit(s);
            suDto.extractJsonFromFiles(folder);
            return suDto;
        }).collect(Collectors.toList());
    }

    private List<QuestionnaireModel> getQueenQuestionnaireModelsFromFods(File fods) throws Exception {
        File file = queenTransformService.getQueenQuestionnaires(fods);
        QuestionnaireModels questionnaireModels = xmlMapper.readValue(file, QuestionnaireModels.class);
        return questionnaireModels != null && questionnaireModels.getQuestionnaireModels() != null ?
                questionnaireModels.getQuestionnaireModels() : List.of();
    }

    public List<QuestionnaireModelDto> getQueenQuestionnaireModelsDtoFromFods(File fods, String folder) throws Exception {
        List<QuestionnaireModel> questionnaireModels = getQueenQuestionnaireModelsFromFods(fods);
        return questionnaireModels.stream().map(q -> new QuestionnaireModelDto(q, folder)).collect(Collectors.toList());
    }

    public List<Nomenclature> getQueenNomenclatureFromFods(File fods) throws Exception {
        ArrayList<Nomenclature> lists = new ArrayList<>();
        File file = queenTransformService.getQueenNomenclatures(fods);
        Nomenclatures nomenclatures = xmlMapper.readValue(file, Nomenclatures.class);
        return nomenclatures != null && nomenclatures.getNomenclatures() != null ? nomenclatures.getNomenclatures() :
                lists;
    }

    public List<NomenclatureDto> getQueenNomenclaturesDtoFromFods(File fods, String folder) throws Exception {
        List<Nomenclature> nomenclatures = getQueenNomenclatureFromFods(fods);
        return nomenclatures.stream().map(n -> new NomenclatureDto(n, folder)).collect(Collectors.toList());
    }

}
