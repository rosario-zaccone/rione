package com.rione.user.application.port.out;

import java.util.Optional;

import com.rione.common.application.OutPort;
import com.rione.user.domain.model.City;
import com.rione.user.domain.model.CityId;

@OutPort
public interface CityRepository {

	City save(City city);

	Optional<City> findById(CityId cityId);

	void delete(City city);
}
