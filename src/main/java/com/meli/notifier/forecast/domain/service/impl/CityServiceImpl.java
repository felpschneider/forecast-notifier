package com.meli.notifier.forecast.domain.service.impl;

import com.meli.notifier.forecast.adapter.out.persistence.entity.CityEntity;
import com.meli.notifier.forecast.adapter.out.persistence.repository.CityRepository;
import com.meli.notifier.forecast.domain.mapper.CityMapper;
import com.meli.notifier.forecast.domain.model.database.City;
import com.meli.notifier.forecast.domain.service.CityService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class CityServiceImpl implements CityService {

    private final CityRepository cityRepository;
    private final CityMapper cityMapper;

    @Override
    public List<City> findCities(String cityName) {
        log.info("Finding cities with name: {}", cityName);
        return cityRepository.findCityEntitiesByName(cityName).stream().map(cityMapper::toModel).toList();
    }

    @Transactional
    @Override
    public List<City> saveCitiesToDatabase(List<City> cities) {
        log.info("Saving cities to database: {}", cities.size());
        for (City city : cities) {
            if (cityRepository.findById(city.getIdCptec()).isPresent()) {
                log.debug("City already exists in database: {}", city.getName());
                continue;
            }

            saveCity(city);
        }
        return cities;
    }

    @Transactional
    @Override
    public void saveCity(City city) {
        log.info("Saving city to database: {}", city.getName());
        CityEntity cityEntity = cityMapper.toEntity(city);
        cityRepository.save(cityEntity);
        log.debug("City saved to database: {}", city.getName());
    }

    @Transactional
    @Override
    public void saveCity(Long id) {
        findById(id).ifPresent(this::saveCity);
    }

    @Override
    public Optional<City> findById(Long cityId) {
        log.info("Finding city by ID: {}", cityId);
        return cityRepository.findById(cityId).map(cityMapper::toModel);
    }

}
