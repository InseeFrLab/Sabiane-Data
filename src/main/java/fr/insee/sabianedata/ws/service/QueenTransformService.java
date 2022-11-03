package fr.insee.sabianedata.ws.service;

import java.io.File;

import org.springframework.stereotype.Service;

import fr.insee.sabianedata.ws.service.xsl.QueenCampaignTransformer;
import fr.insee.sabianedata.ws.service.xsl.SampleProcessingXMLTransformer;

@Service
public class QueenTransformService {

	private QueenCampaignTransformer queenCampaignTransformer = new QueenCampaignTransformer();
	private SampleProcessingXMLTransformer sampleProcessingXMLTransformer = new SampleProcessingXMLTransformer();

	public File getQueenCampaign(File fodsInput) throws Exception {
		return queenCampaignTransformer.extractCampaign(fodsInput);
	}

	public File getQueenQuestionnaires(File fodsInput) throws Exception {
		return queenCampaignTransformer.extractQuestionnaireModels(fodsInput);
	}

	public File getQueenSurveyUnits(File fodsInput) throws Exception {
		return queenCampaignTransformer.extractSurveyUnits(fodsInput);
	}

	public File getQueenNomenclatures(File fodsInput) throws Exception {
		return queenCampaignTransformer.extractNomenclatures(fodsInput);
	}

	public File sampleProcessing(File input, int repetitions) throws Exception {
		return sampleProcessingXMLTransformer.transform(input, repetitions);
	}

}
