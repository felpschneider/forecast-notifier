package com.meli.notifier.forecast.application.service;

import com.meli.notifier.forecast.adapter.out.persistence.repository.CityRepository;
import com.meli.notifier.forecast.application.port.in.CityService;
import com.meli.notifier.forecast.domain.entity.CityEntity;
import com.meli.notifier.forecast.domain.mapper.CityMapper;
import com.meli.notifier.forecast.domain.model.database.City;
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
    public List<City> findByName(String cityName) {
        log.info("Finding cities with name: {}", cityName);
        return cityRepository.findCityEntitiesByNameIgnoreCase(cityName)
                .stream()
                .map(cityMapper::toModel)
                .toList();
    }

    @Transactional
    @Override
    public List<City> saveCitiesToDatabase(List<City> cities) {
        log.info("Starting batch save operation for {} cities", cities.size());
        return cities.stream()
                .map(this::saveIfNotExists)
                .toList();
    }

    @Transactional
    @Override
    public City saveCity(City city) {
        log.info("Saving city to database: {}", city.getName());
        CityEntity cityEntity = cityMapper.toEntity(city);
        var savedEntity = cityRepository.save(cityEntity);
        log.debug("City successfully saved to database: {}", city.getName());
        return cityMapper.toModel(savedEntity);
    }

    @Override
    public Optional<City> findById(Long cityId) {
        log.info("Finding city by ID: {}", cityId);
        return cityRepository.findById(cityId)
                .map(cityMapper::toModel);
    }

    @Override
    public City saveIfNotExists(City city) {
        return findById(city.getIdCptec())
                .orElseGet(() -> {
                    log.debug("City {} not found in database, saving...", city.getName());
                    return saveCity(city);
                });
    }
}
