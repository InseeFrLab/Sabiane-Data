package fr.insee.sabianedata.ws.controller;

import java.io.File;
import java.nio.file.Files;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import fr.insee.sabianedata.ws.Constants;
import fr.insee.sabianedata.ws.utils.FileArchiver;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name="Examples")
@RestController
@RequestMapping("/example")
public class ExampleController {


	@Operation(summary="Get example for pearl campaign")
	@GetMapping(value="pearl-campaign", produces=MediaType.APPLICATION_OCTET_STREAM_VALUE)
	public ResponseEntity<StreamingResponseBody> campaignFods2Xml() throws Exception {
		
		File campaignExample = Constants.EXAMPLE_PEARL_CAMPAIGN;

		StreamingResponseBody stream = out -> out.write(Files.readAllBytes(campaignExample.toPath())) ;

		return  ResponseEntity.ok()
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=\"campaignExample.fods\"")
				.body(stream);
	}

	
	@Operation(summary="Get example for queen campaign",
			description="Vous trouverez un fichier zip qui contient les fichiers nécessaires. \n\n"
					+ "Fichier **LISEZ-MOI.txt** à lire avant d'utiliser le swagger.")
	@GetMapping(value="queen-campaign", produces=MediaType.APPLICATION_OCTET_STREAM_VALUE)
	public ResponseEntity<StreamingResponseBody> operationFods2Xml() throws Exception {
				
		File operationZip = FileArchiver.createZipForExample();

		StreamingResponseBody stream = out -> out.write(Files.readAllBytes(operationZip.toPath())) ;

		return  ResponseEntity.ok()
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=\"queenCampaignExample.zip\"")
				.body(stream);
	}


}