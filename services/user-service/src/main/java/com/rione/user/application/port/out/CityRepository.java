package com.rione.user.application.port.out;

import java.util.List;
import java.util.Optional;

import com.rione.common.application.OutPort;
import com.rione.user.domain.model.City;
import com.rione.user.domain.model.CityId;

@OutPort
public interface CityRepository {

	City save(City city);

	List<City> findAll();

	Optional<City> findById(CityId cityId);

	void delete(City city);
}
