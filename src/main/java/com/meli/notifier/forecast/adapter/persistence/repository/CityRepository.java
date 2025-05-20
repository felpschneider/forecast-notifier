package com.meli.notifier.forecast.adapter.persistence.repository;

import com.meli.notifier.forecast.adapter.persistence.entity.CityEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CityRepository extends JpaRepository<CityEntity, Long> {

    List<CityEntity> findCityEntitiesByName(String cityName);
}
