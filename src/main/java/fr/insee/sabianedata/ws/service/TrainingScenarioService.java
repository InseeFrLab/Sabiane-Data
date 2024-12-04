package fr.insee.sabianedata.ws.service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import fr.insee.sabianedata.ws.model.pearl.PearlCampaign;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.stereotype.Service;

import fr.insee.sabianedata.ws.model.massiveAttack.ScenarioType;
import fr.insee.sabianedata.ws.model.massiveAttack.TrainingScenario;

@Service
@Slf4j
@RequiredArgsConstructor
public class TrainingScenarioService {

    private final PearlExtractEntities pearlExtractEntities;

    public TrainingScenario getTrainingScenario(File scenariiFolder, String tsId) {

        Path scenarioDirectory = scenariiFolder.toPath().resolve(tsId);
        Path infoFilePath = scenarioDirectory.resolve("info.json");
        ObjectMapper objectMapper = new ObjectMapper();

        TrainingScenario trainingScenario;
        try (InputStream inputStream = Files.newInputStream(infoFilePath)) {
            trainingScenario = objectMapper.readValue(inputStream, TrainingScenario.class);
        } catch (IOException e) {
            log.warn("Unable to load TrainingScenario from {}", infoFilePath, e);
            return null;
        }

        try (Stream<Path> paths = Files.list(scenarioDirectory)) {
            List<PearlCampaign> campaigns = paths
                    .filter(Files::isDirectory)
                    .map(path -> processCampaign(path.toFile()))
                    .toList();

            trainingScenario.setCampaigns(campaigns);
        } catch (IOException e) {
            log.warn("Error while listing directories in {}", scenarioDirectory, e);
            throw new RuntimeException("Failed to process campaigns", e);
        } catch (RuntimeException e) {
            log.warn("Error when processing campaigns for scenario {}", tsId, e);
            return null;
        }

        return trainingScenario;


    }

    private PearlCampaign processCampaign(File campaignDirectory) {
        try {
            File pearlCampaignFile = new File(campaignDirectory, "pearl/pearl_campaign.fods");
            return pearlExtractEntities.getPearlCampaignFromFods(pearlCampaignFile);
        } catch (Exception e) {
            log.warn("Error when extracting campaign from {}", campaignDirectory.getAbsolutePath(), e);
            throw new RuntimeException("Campaign extraction failed", e);
        }
    }

    public ScenarioType getScenarioType(File scenariiFolder, String tsId) {
        try {
            File scenarioDirectory = new File(scenariiFolder, tsId);
            File infoFile = new File(scenarioDirectory, "info.json");
            ObjectMapper objectMapper = new ObjectMapper();
            TrainingScenario ts = objectMapper.readValue(infoFile, TrainingScenario.class);
            return ts.getType();
        } catch ( IOException e ) {
            log.warn("Error when getting scenario type {}", tsId, e);
            return null;
        }
    }

    // TODO : create an in-memory persistence layer to persist all scenario at start-up
    //  move this currently unused function there,
    //  and call it instead of generating scenario from scratch for each call ;)
    public List<TrainingScenario> getTrainingScenarii(File scenariiFolder) throws IOException {
        File[] files = scenariiFolder.listFiles();
        if(files == null){
            throw new IOException(String.format("%s is not a folder",scenariiFolder));
        }
        Stream<File> folders = Arrays.stream(files);
        return folders.map(f -> getTrainingScenario(scenariiFolder, f.getName())).toList();
    }

}
