package fr.insee.sabianedata.ws.service;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import fr.insee.sabianedata.ws.model.pearl.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PearlExtractEntities {

    private final PearlTransformService pearlTransformService;

    public List<PearlSurveyUnit> getPearlSurveyUnitsFromFods(File fods) throws Exception {
        File file = pearlTransformService.getPearlSurveyUnits(fods);
        XmlMapper xmlMapper = new XmlMapper();
        SurveyUnits surveyUnits = xmlMapper.readValue(file, SurveyUnits.class);
        return surveyUnits.getSurveyUnits() == null ? new ArrayList<>() :
                surveyUnits.getSurveyUnits().stream().peek(PearlSurveyUnit::cleanAttributes).toList();
    }

    public PearlCampaign getPearlCampaignFromFods(File fods) throws Exception {
        File file = pearlTransformService.getPearlCampaign(fods);
        XmlMapper xmlMapper = new XmlMapper();
        PearlCampaign pearlCampaign = xmlMapper.readValue(file, PearlCampaign.class);
        List<Visibility> visibilities = pearlCampaign.getVisibilities();
        List<Visibility> newVisibilities = visibilities.stream().map(Visibility::new).collect(Collectors.toList());
        pearlCampaign.setVisibilities(newVisibilities);
        return pearlCampaign;
    }

    public List<Assignement> getAssignementsFromFods(File fods) throws Exception {
        File file = pearlTransformService.getPearlAssignement(fods);
        XmlMapper xmlMapper = new XmlMapper();
        Assignements assignementList = xmlMapper.readValue(file, Assignements.class);
        return assignementList.getAssignements() != null ? assignementList.getAssignements() : new ArrayList<>();
    }

}
