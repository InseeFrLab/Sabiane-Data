package fr.insee.sabianedata.ws.service;

import fr.insee.sabianedata.ws.service.xsl.PearlCampaignTransformer;
import org.springframework.stereotype.Service;

import java.io.File;

@Service
public class PearlTransformService {

	private final PearlCampaignTransformer pearlCampaignTransformer = new PearlCampaignTransformer();

	public File getPearlCampaign(File fodsInput) throws Exception {
		return pearlCampaignTransformer.extractCampaign(fodsInput);
	}

	public File getPearlSurveyUnits(File fodsInput) throws Exception {
		return pearlCampaignTransformer.extractSurveyUnits(fodsInput);
	}

	public File getPearlInterviewers(File fodsInput) throws Exception {
		return pearlCampaignTransformer.extractInterviewers(fodsInput);
	}

	public File getPearlContext(File fodsInput) throws Exception {
		return pearlCampaignTransformer.extractContext(fodsInput);
	}

	public File getPearlAssignement(File fodsInput) throws Exception {
		return pearlCampaignTransformer.extractAssignement(fodsInput);
	}

}
