package com.rione.user.application.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.rione.user.application.port.in.CityService;
import com.rione.user.application.port.out.CityRepository;
import com.rione.user.application.port.out.UserRepository;
import com.rione.user.domain.model.City;
import com.rione.user.domain.model.CityId;
import com.rione.user.domain.model.Neighborhood;
import com.rione.user.domain.model.User;
import com.rione.user.domain.model.UserId;

@Service
public class CityServiceImpl implements CityService {

	private final CityRepository cityRepository;
	private final UserRepository userRepository;

	public CityServiceImpl(CityRepository cityRepository, UserRepository userRepository) {
		this.cityRepository = cityRepository;
		this.userRepository = userRepository;
	}

	@Override
	public CityResponse createCity(CreateCityCommand command) {
		requireAdmin(command.actingUserId(), "insert a city");
		return toResponse(cityRepository.save(City.create(command.name(), command.neighborhoods())));
	}

	@Override
	public List<CityResponse> listCities() {
		return cityRepository.findAll().stream().map(this::toResponse).toList();
	}

	@Override
	public CityResponse getCity(Long cityId) {
		return toResponse(cityRepository.findById(new CityId(cityId))
			.orElseThrow(() -> new UserApplicationException("City not found")));
	}

	@Override
	public void removeCity(RemoveCityCommand command) {
		requireAdmin(command.actingUserId(), "remove a city");
		City city = cityRepository.findById(new CityId(command.cityId()))
			.orElseThrow(() -> new UserApplicationException("City not found"));
		if (userRepository.existsByNeighborhoodIdIn(city.neighborhoods().stream().map(Neighborhood::id).toList())) {
			throw new UserApplicationException("City cannot be removed while users belong to its neighborhoods");
		}
		cityRepository.delete(city);
	}

	private void requireAdmin(Long actingUserId, String action) {
		User actingUser = userRepository.findById(new UserId(actingUserId))
			.orElseThrow(() -> new UserApplicationException("Acting user not found"));
		if (!actingUser.isAdmin()) {
			throw new UserApplicationException("Only admin users can " + action);
		}
	}

	private CityResponse toResponse(City city) {
		return new CityResponse(city.id().value(), city.name(), city.neighborhoods().stream().map(this::toResponse).toList());
	}

	private NeighborhoodResponse toResponse(Neighborhood neighborhood) {
		Long id = neighborhood.id() == null ? null : neighborhood.id().value();
		return new NeighborhoodResponse(id, neighborhood.name());
	}
}
