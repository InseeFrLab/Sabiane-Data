package fr.insee.sabianedata.ws.service;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import fr.insee.sabianedata.ws.model.queen.CampaignDto;
import fr.insee.sabianedata.ws.model.queen.Nomenclature;
import fr.insee.sabianedata.ws.model.queen.NomenclatureDto;
import fr.insee.sabianedata.ws.model.queen.Nomenclatures;
import fr.insee.sabianedata.ws.model.queen.QuestionnaireModel;
import fr.insee.sabianedata.ws.model.queen.QuestionnaireModelDto;
import fr.insee.sabianedata.ws.model.queen.QuestionnaireModels;
import fr.insee.sabianedata.ws.model.queen.SurveyUnitDto;
import fr.insee.sabianedata.ws.model.queen.SurveyUnits;

@Service
public class QueenExtractEntities {

    @Autowired
    QueenTransformService queenTransformService;

    public CampaignDto getQueenCampaignFromXMLFile(File file) throws Exception {
        XmlMapper xmlMapper = new XmlMapper();
        CampaignDto campaign = xmlMapper.readValue(file, CampaignDto.class);
        return campaign;
    }

    public CampaignDto getQueenCampaignFromFods(File fods) throws Exception {
        File file = queenTransformService.getQueenCampaign(fods);
        return getQueenCampaignFromXMLFile(file);
    }

    public List<SurveyUnitDto> getQueenSurveyUnitsFromFods(File fods, String folder) throws Exception {
        File file = queenTransformService.getQueenSurveyUnits(fods);
        XmlMapper xmlMapper = new XmlMapper();
        SurveyUnits surveyUnits = xmlMapper.readValue(file, SurveyUnits.class);
        List<SurveyUnitDto> surveyUnitsList = surveyUnits.getSurveyUnits().stream().map(s -> {
            SurveyUnitDto suDto = new SurveyUnitDto(s);
            suDto.extractJsonFromFiles(folder);
            return suDto;
        }).collect(Collectors.toList());
        return surveyUnitsList != null ? surveyUnitsList : Collections.emptyList();
    }

    public List<QuestionnaireModel> getQueenQuestionnaireModelsFromFods(File fods, String folder) throws Exception {
        ArrayList<QuestionnaireModel> lists = new ArrayList<>();
        File file = queenTransformService.getQueenQuestionnaires(fods);
        XmlMapper xmlMapper = new XmlMapper();
        QuestionnaireModels questionnaireModels = xmlMapper.readValue(file, QuestionnaireModels.class);
        return questionnaireModels != null && questionnaireModels.getQuestionnaireModels() != null
                ? questionnaireModels.getQuestionnaireModels()
                : lists;
    }

    public List<QuestionnaireModelDto> getQueenQuestionnaireModelsDtoFromFods(File fods, String folder)
            throws Exception {
        List<QuestionnaireModel> questionnaireModels = getQueenQuestionnaireModelsFromFods(fods, folder);
        return questionnaireModels.stream()
                .map(q -> new QuestionnaireModelDto(q, folder)).collect(Collectors.toList());
    }

    public List<Nomenclature> getQueenNomenclatureFromFods(File fods) throws Exception {
        ArrayList<Nomenclature> lists = new ArrayList<>();
        File file = queenTransformService.getQueenNomenclatures(fods);
        XmlMapper xmlMapper = new XmlMapper();
        Nomenclatures nomenclatures = xmlMapper.readValue(file, Nomenclatures.class);
        return nomenclatures != null && nomenclatures.getNomenclatures() != null ? nomenclatures.getNomenclatures()
                : lists;
    }

    public List<NomenclatureDto> getQueenNomenclaturesDtoFromFods(File fods, String folder) throws Exception {
        List<Nomenclature> nomenclatures = getQueenNomenclatureFromFods(fods);
        return nomenclatures.stream().map(n -> new NomenclatureDto(n, folder))
                .collect(Collectors.toList());
    }

}
