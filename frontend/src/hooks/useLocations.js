import { useCallback, useEffect, useMemo, useState } from "react";
import * as locationsApi from "../api/locationsApi";

function upsertCity(cities, city) {
  const exists = cities.some((item) => item.id === city.id);
  if (exists) {
    return cities.map((item) => (item.id === city.id ? city : item));
  }
  return [...cities, city];
}

export function useLocations(token) {
  const [cities, setCities] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");

  const loadCities = useCallback(async () => {
    const controller = new AbortController();
    setLoading(true);
    setError("");
    try {
      const result = await locationsApi.listCities(token, controller.signal);
      setCities(Array.isArray(result) ? result : []);
    } catch (locationsError) {
      setCities([]);
      setError(`${locationsError.message}. Le località non sono temporaneamente disponibili.`);
    } finally {
      setLoading(false);
    }
    return () => controller.abort();
  }, [token]);

  useEffect(() => {
    loadCities();
  }, [loadCities]);

  async function loadCityById(cityId) {
    setLoading(true);
    setError("");
    try {
      const city = await locationsApi.getCity(token, cityId);
      setCities((items) => upsertCity(items, city));
      return city;
    } catch (cityError) {
      setError(cityError.message);
      throw cityError;
    } finally {
      setLoading(false);
    }
  }

  async function createCity(payload) {
    setLoading(true);
    setError("");
    try {
      const city = await locationsApi.createCity(token, payload);
      setCities((items) => upsertCity(items, city));
      return city;
    } catch (cityError) {
      setError(cityError.message);
      throw cityError;
    } finally {
      setLoading(false);
    }
  }

  async function removeCity(cityId) {
    setLoading(true);
    setError("");
    try {
      await locationsApi.removeCity(token, cityId);
      setCities((items) => items.filter((item) => item.id !== Number(cityId)));
    } catch (cityError) {
      setError(cityError.message);
      throw cityError;
    } finally {
      setLoading(false);
    }
  }

  async function addNeighborhood(cityId, name) {
    setLoading(true);
    setError("");
    try {
      const city = await locationsApi.addNeighborhood(token, cityId, { name });
      setCities((items) => upsertCity(items, city));
      return city;
    } catch (neighborhoodError) {
      setError(neighborhoodError.message);
      throw neighborhoodError;
    } finally {
      setLoading(false);
    }
  }

  const neighborhoods = useMemo(
    () =>
      cities.flatMap((city) =>
        (city.neighborhoods ?? []).map((neighborhood) => ({
          ...neighborhood,
          cityId: city.id,
          cityName: city.name,
        })),
      ),
    [cities],
  );

  function neighborhoodLabel(neighborhoodId) {
    const neighborhood = neighborhoods.find((item) => item.id === Number(neighborhoodId));
    return neighborhood ? `${neighborhood.name}, ${neighborhood.cityName}` : "Quartiere non disponibile";
  }

  return {
    cities,
    neighborhoods,
    loading,
    error,
    setError,
    loadCities,
    loadCityById,
    createCity,
    removeCity,
    addNeighborhood,
    neighborhoodLabel,
  };
}
