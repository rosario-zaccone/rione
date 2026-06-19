package com.rione.user.infrastructure.web;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.rione.user.application.port.in.CityService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

@RestController
@RequestMapping("/cities")
public class CityController {

	private final CityService cityService;

	public CityController(CityService cityService) {
		this.cityService = cityService;
	}

	@PostMapping
	ResponseEntity<CityService.CityResponse> createCity(@RequestHeader("X-User-Id") @Positive Long actingUserId,
			@Valid @RequestBody CreateCityRequest request) {
		CityService.CityResponse response = cityService
			.createCity(new CityService.CreateCityCommand(actingUserId, request.name(), request.neighborhoods()));
		return ResponseEntity.created(URI.create("/cities/" + response.id())).body(response);
	}

	@GetMapping("/{cityId}")
	CityService.CityResponse getCity(@PathVariable @Positive Long cityId) {
		return cityService.getCity(cityId);
	}

	@DeleteMapping("/{cityId}")
	ResponseEntity<Void> removeCity(@RequestHeader("X-User-Id") @Positive Long actingUserId,
			@PathVariable @Positive Long cityId) {
		cityService.removeCity(new CityService.RemoveCityCommand(actingUserId, cityId));
		return ResponseEntity.noContent().build();
	}

	record CreateCityRequest(@NotBlank @Size(max = 120) String name,
			@NotEmpty List<@NotBlank @Size(max = 120) String> neighborhoods) {
	}
}
