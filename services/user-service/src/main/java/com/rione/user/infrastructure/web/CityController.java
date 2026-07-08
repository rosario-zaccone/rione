package com.rione.user.infrastructure.web;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.rione.user.application.port.in.CityService;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

@RestController
@RequestMapping("/cities")
public class CityController {

	
	private final CityService cityService;
	private final CurrentUser currentUser;

	public CityController(CityService cityService, CurrentUser currentUser) {
		this.cityService = cityService;
		this.currentUser = currentUser;
	}

	@PostMapping
	@PreAuthorize("hasRole('ADMIN')")
	@Operation(summary = "Create a city",
			description = "Only authenticated administrators can access this endpoint. It creates a city and its neighborhoods for location selection.")
	ResponseEntity<CityService.CityResponse> createCity(@Valid @RequestBody CreateCityRequest request) {
		CityService.CityResponse response = cityService
			.createCity(new CityService.CreateCityCommand(currentUser.id(), request.name(), request.neighborhoods()));
		return ResponseEntity.created(URI.create("/cities/" + response.id())).body(response);
	}

	@GetMapping
	@Operation(summary = "List cities",
			description = "Anyone can access this endpoint. It returns the platform-managed city and neighborhood catalog used during signup and profile neighborhood selection.")
	List<CityService.CityResponse> listCities() {
		return cityService.listCities();
	}

	@GetMapping("/{cityId}")
	@Operation(summary = "Get a city",
			description = "Anyone can access this endpoint. It returns public city and neighborhood names for location selection.")
	CityService.CityResponse getCity(@PathVariable @Positive Long cityId) {
		return cityService.getCity(cityId);
	}

	@DeleteMapping("/{cityId}")
	@PreAuthorize("hasRole('ADMIN')")
	@Operation(summary = "Delete a city",
			description = "Only authenticated administrators can access this endpoint. Cities with active residents are rejected to protect user location references.")
	ResponseEntity<Void> removeCity(@PathVariable @Positive Long cityId) {
		cityService.removeCity(new CityService.RemoveCityCommand(currentUser.id(), cityId));
		return ResponseEntity.noContent().build();
	}

	@PostMapping("/{cityId}/neighborhoods")
	@PreAuthorize("hasRole('ADMIN')")
	@Operation(summary = "Add a neighborhood to a city",
			description = "Only authenticated administrators can access this endpoint. It adds a new neighborhood to an existing city.")
	ResponseEntity<CityService.CityResponse> addNeighborhood(@PathVariable @Positive Long cityId,
			@Valid @RequestBody AddNeighborhoodRequest request) {
		CityService.CityResponse response = cityService
			.addNeighborhood(new CityService.AddNeighborhoodCommand(currentUser.id(), cityId, request.name()));
		return ResponseEntity.ok(response);
	}

	record CreateCityRequest(@NotBlank @Size(max = 120) String name,
			@NotEmpty List<@NotBlank @Size(max = 120) String> neighborhoods) {
	}

	record AddNeighborhoodRequest(@NotBlank @Size(max = 120) String name) {
	}
}
