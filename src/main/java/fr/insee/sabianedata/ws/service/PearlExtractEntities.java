package fr.insee.sabianedata.ws.service;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import fr.insee.sabianedata.ws.model.pearl.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PearlExtractEntities {

    @Autowired
    PearlTransformService pearlTransformService;

    public List<SurveyUnitDto> getPearlSurveyUnitsFromFods(File fods) throws Exception {
        File file = pearlTransformService.getPearlSurveyUnits(fods);
        XmlMapper xmlMapper = new XmlMapper();
        SurveyUnits surveyUnits = xmlMapper.readValue(file, SurveyUnits.class);
        return surveyUnits.getSurveyUnits() == null ? new ArrayList<>() :
                surveyUnits.getSurveyUnits().stream().peek(SurveyUnitDto::cleanAttributes).toList();
    }

    public CampaignDto getPearlCampaignFromFods(File fods) throws Exception {
        File file = pearlTransformService.getPearlCampaign(fods);
        XmlMapper xmlMapper = new XmlMapper();
        CampaignDto campaignDto = xmlMapper.readValue(file, CampaignDto.class);
        List<Visibility> visibilities = campaignDto.getVisibilities();
        List<Visibility> newVisibilities = visibilities.stream().map(Visibility::new).collect(Collectors.toList());
        campaignDto.setVisibilities(newVisibilities);
        return campaignDto;
    }

    public List<Assignement> getAssignementsFromFods(File fods) throws Exception {
        File file = pearlTransformService.getPearlAssignement(fods);
        XmlMapper xmlMapper = new XmlMapper();
        Assignements assignementList = xmlMapper.readValue(file, Assignements.class);
        return assignementList.getAssignements() != null ? assignementList.getAssignements() : new ArrayList<>();
    }

}
