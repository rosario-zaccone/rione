package com.rione.user.infrastructure.persistence;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.rione.user.application.port.out.CityRepository;
import com.rione.user.domain.model.City;
import com.rione.user.domain.model.CityId;
import com.rione.user.domain.model.Neighborhood;
import com.rione.user.domain.model.NeighborhoodId;
import com.rione.user.infrastructure.persistence.entity.CityJpaEntity;
import com.rione.user.infrastructure.persistence.entity.NeighborhoodJpaEntity;

@Repository
class JpaCityRepositoryAdapter implements CityRepository {

	private final SpringDataCityJpaRepository repository;

	JpaCityRepositoryAdapter(SpringDataCityJpaRepository repository) {
		this.repository = repository;
	}

	@Override
	public City save(City city) {
		return toDomain(repository.save(toEntity(city)));
	}

	@Override
	public Optional<City> findById(CityId cityId) {
		return repository.findById(cityId.value()).map(this::toDomain);
	}

	@Override
	public void delete(City city) {
		repository.deleteById(city.id().value());
	}

	private CityJpaEntity toEntity(City city) {
		Long id = city.id() == null ? null : city.id().value();
		CityJpaEntity entity = new CityJpaEntity(id, city.name());
		city.neighborhoods()
			.stream()
			.map(neighborhood -> new NeighborhoodJpaEntity(
					neighborhood.id() == null ? null : neighborhood.id().value(), neighborhood.name()))
			.forEach(entity::addNeighborhood);
		return entity;
	}

	private City toDomain(CityJpaEntity entity) {
		return City.restore(new CityId(entity.id()), entity.name(), entity.neighborhoods()
			.stream()
			.map(neighborhood -> Neighborhood.restore(new NeighborhoodId(neighborhood.id()), neighborhood.name()))
			.toList());
	}
}
