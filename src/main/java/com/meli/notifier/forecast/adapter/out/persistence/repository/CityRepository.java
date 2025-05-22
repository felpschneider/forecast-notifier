package com.meli.notifier.forecast.adapter.out.persistence.repository;

import com.meli.notifier.forecast.domain.entity.CityEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CityRepository extends JpaRepository<CityEntity, Long> {

    List<CityEntity> findCityEntitiesByNameIgnoreCase(String cityName);
}
