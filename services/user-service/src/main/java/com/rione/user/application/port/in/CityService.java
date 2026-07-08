package com.rione.user.application.port.in;

import java.util.List;

import com.rione.common.application.InPort;

@InPort
public interface CityService {

	CityResponse createCity(CreateCityCommand command);

	List<CityResponse> listCities();

	CityResponse getCity(Long cityId);

	void removeCity(RemoveCityCommand command);

	CityResponse addNeighborhood(AddNeighborhoodCommand command);

	record CreateCityCommand(Long actingUserId, String name, List<String> neighborhoods) {
	}

	record RemoveCityCommand(Long actingUserId, Long cityId) {
	}

	record AddNeighborhoodCommand(Long actingUserId, Long cityId, String neighborhoodName) {
	}

	record CityResponse(Long id, String name, List<NeighborhoodResponse> neighborhoods) {
	}

	record NeighborhoodResponse(Long id, String name) {
	}
}
