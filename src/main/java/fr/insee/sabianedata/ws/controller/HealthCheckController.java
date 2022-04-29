package fr.insee.sabianedata.ws.controller;

import io.swagger.v3.oas.annotations.Operation;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import fr.insee.sabianedata.ws.config.Plateform;
import fr.insee.sabianedata.ws.model.ResponseModel;
import fr.insee.sabianedata.ws.service.PearlApiService;
import fr.insee.sabianedata.ws.service.QueenApiService;

/**
 * HealthCheck is the Controller used to check if own API and Pearl and Queen
 * API are alive
 * 
 * @author Simon Demaziere
 * 
 */
@RestController
@RequestMapping(path = "/api")
public class HealthCheckController {

	@Autowired
	private PearlApiService pearlApiService;

	@Autowired
	private QueenApiService queenApiService;

	@Operation(summary = "Healthcheck, check if api is alive", description = "Healthcheck on Pearl and Queen API")
	@GetMapping(path = "/healthcheck")
	public ResponseEntity<ResponseModel> healthCheck(HttpServletRequest request,
			@RequestParam(value = "plateform") Plateform plateform) {
		boolean pearlApiIsHealthy = pearlApiService.healthCheck(request, plateform);
		boolean queenApiIsHealthy = queenApiService.healthCheck(request, plateform);
		String responseMessage = String.format("Pearl-API : %b - Queen-API : %b", pearlApiIsHealthy, queenApiIsHealthy);
		ResponseModel response = new ResponseModel(pearlApiIsHealthy && queenApiIsHealthy, responseMessage);
		return ResponseEntity.ok().body(response);

	}
}
