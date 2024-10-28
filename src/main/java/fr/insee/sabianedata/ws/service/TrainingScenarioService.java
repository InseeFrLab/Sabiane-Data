package fr.insee.sabianedata.ws.service;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import lombok.extern.slf4j.Slf4j;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import fr.insee.sabianedata.ws.model.massiveAttack.ScenarioType;
import fr.insee.sabianedata.ws.model.massiveAttack.TrainingScenario;
import fr.insee.sabianedata.ws.model.pearl.CampaignDto;

@Service
@Slf4j
public class TrainingScenarioService {

    @Autowired
    PearlExtractEntities pearlExtractEntities;

    public TrainingScenario getTrainingScenario(File scenariiFolder, String tsId) {

        try {
            File scenarioDirectory = new File(scenariiFolder, tsId);
            File infoFile = new File(scenarioDirectory, "info.json");
            ObjectMapper objectMapper = new ObjectMapper();

            TrainingScenario ts = objectMapper.readValue(infoFile, TrainingScenario.class);

            List<CampaignDto> campaigns = Arrays.stream(scenarioDirectory.listFiles())
                    .filter(File::isDirectory)
                    .map(f -> {
                        try {
                            return pearlExtractEntities.getPearlCampaignFromFods(new File(f, "pearl/pearl_campaign" +
                                    ".fods"));
                        } catch (Exception e) {
                            log.warn("Error when extracting campaign from {} ", f.getAbsolutePath());
                            log.warn(e.getMessage());
                            return null;
                        }
                    }).collect(Collectors.toList());
            if (campaigns.contains(null)) {
                throw new RuntimeException("extraction error");
            }

            ts.setCampaigns(campaigns);
            return ts;

        } catch (Exception e) {
            log.warn("Error when getting scenario {}", tsId, e);
            return null;
        }

    }

    public ScenarioType getScenarioType(File scenariiFolder, String tsId) {
        try {
            File scenarioDirectory = new File(scenariiFolder, tsId);
            File infoFile = new File(scenarioDirectory, "info.json");
            ObjectMapper objectMapper = new ObjectMapper();
            TrainingScenario ts = objectMapper.readValue(infoFile, TrainingScenario.class);
            return ts.getType();
        } catch (Exception e) {
            log.warn("Error when getting scenario type {}", tsId, e);
            return null;
        }
    }

    public List<TrainingScenario> getTrainingScenarii(File scenariiFolder) {
        Stream<File> folders = Arrays.stream(scenariiFolder.listFiles());
        return folders.map(f -> getTrainingScenario(scenariiFolder, f.getName())).collect(Collectors.toList());
    }

}
