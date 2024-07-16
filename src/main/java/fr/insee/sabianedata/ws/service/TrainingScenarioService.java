package fr.insee.sabianedata.ws.service;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import fr.insee.sabianedata.ws.model.massiveAttack.ScenarioType;
import fr.insee.sabianedata.ws.model.massiveAttack.TrainingScenario;
import fr.insee.sabianedata.ws.model.pearl.CampaignDto;

@Service
public class TrainingScenarioService {

    private static final Logger LOGGER = LoggerFactory.getLogger(TrainingScenarioService.class);

    @Autowired
    PearlExtractEntities pearlExtractEntities;

    @Autowired
    ResourceLoader resourceLoader;

    public TrainingScenario getTrainingScenario(File scenariiFolder, String tsId) {

        try {
            File scenarioDirectory = new File(scenariiFolder, tsId);
            File infoFile = new File(scenarioDirectory, "info.json");
            ObjectMapper objectMapper = new ObjectMapper();

            TrainingScenario ts = objectMapper.readValue(infoFile, TrainingScenario.class);

            List<CampaignDto> campaigns = Arrays.stream(scenarioDirectory.listFiles()).filter(File::isDirectory)
                    .map(f -> {
                        try {
                            return pearlExtractEntities
                                    .getPearlCampaignFromFods(new File(f, "pearl/pearl_campaign.fods"));
                        } catch (Exception e) {
                            LOGGER.warn("Error when extracting campaign from {} ", f.getAbsolutePath());
                            LOGGER.warn(e.getMessage());
                            return null;
                        }
                    }).collect(Collectors.toList());
            if (campaigns.contains(null)) {
                throw new RuntimeException("extraction error");
            }

            ts.setCampaigns(campaigns);
            return ts;

        } catch (Exception e) {
            LOGGER.warn("Error when getting scenario {}", tsId);
            LOGGER.warn(e.getMessage());
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
            LOGGER.warn("Error when getting scenario type {}", tsId);
            LOGGER.warn(e.getMessage());
            return null;
        }
    }

    public List<TrainingScenario> getTrainingScenarii(File scenariiFolder) {
        Stream<File> folders = Arrays.stream(scenariiFolder.listFiles());
        return folders.map(f -> getTrainingScenario(scenariiFolder, f.getName())).collect(Collectors.toList());
    }

}
