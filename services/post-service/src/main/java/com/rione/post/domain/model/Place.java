package com.rione.post.domain.model;

import com.rione.common.domain.DDDValueObject;

@DDDValueObject
public record Place(double longitude, double latitude) {

	private static final double MIN_LONGITUDE = -180.0;
	private static final double MAX_LONGITUDE = 180.0;
	private static final double MIN_LATITUDE = -90.0;
	private static final double MAX_LATITUDE = 90.0;

	public Place {
		validateCoordinate(longitude, MIN_LONGITUDE, MAX_LONGITUDE, "Longitude");
		validateCoordinate(latitude, MIN_LATITUDE, MAX_LATITUDE, "Latitude");
	}

	private static void validateCoordinate(double value, double minimum, double maximum, String name) {
		if (!Double.isFinite(value) || value < minimum || value > maximum) {
			throw new DomainException(name + " must be between " + minimum + " and " + maximum);
		}
	}
}
