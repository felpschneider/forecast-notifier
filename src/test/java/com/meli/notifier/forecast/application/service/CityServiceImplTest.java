package com.meli.notifier.forecast.application.service;

import com.meli.notifier.forecast.adapter.out.persistence.repository.CityRepository;
import com.meli.notifier.forecast.domain.entity.CityEntity;
import com.meli.notifier.forecast.domain.mapper.CityMapper;
import com.meli.notifier.forecast.domain.model.database.City;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CityServiceImplTest {

    @Mock
    private CityRepository cityRepository;

    @Mock
    private CityMapper cityMapper;

    private CityServiceImpl cityService;

    @BeforeEach
    void setUp() {
        cityService = new CityServiceImpl(cityRepository, cityMapper);
    }

    @Test
    void givenCityName_whenFindCities_thenReturnMatchingCities() {
        // Arrange
        String cityName = "São Paulo";

        CityEntity entity1 = new CityEntity();
        entity1.setIdCptec(1L);
        entity1.setName(cityName);

        CityEntity entity2 = new CityEntity();
        entity2.setIdCptec(2L);
        entity2.setName("São Paulo do Sul");

        List<CityEntity> cityEntities = Arrays.asList(entity1, entity2);

        City city1 = City.builder().idCptec(1L).name(cityName).build();
        City city2 = City.builder().idCptec(2L).name("São Paulo do Sul").build();

        when(cityRepository.findCityEntitiesByNameIgnoreCase(cityName)).thenReturn(cityEntities);
        when(cityMapper.toModel(entity1)).thenReturn(city1);
        when(cityMapper.toModel(entity2)).thenReturn(city2);

        // Act
        List<City> result = cityService.findCities(cityName);

        // Assert
        assertEquals(2, result.size());
        assertEquals(city1, result.get(0));
        assertEquals(city2, result.get(1));
        verify(cityRepository).findCityEntitiesByNameIgnoreCase(cityName);
        verify(cityMapper).toModel(entity1);
        verify(cityMapper).toModel(entity2);
    }

    @Test
    void givenCityList_whenSaveCitiesToDatabase_thenReturnSavedCities() {
        // Arrange
        City city1 = City.builder().idCptec(1L).name("São Paulo").stateCode("SP").build();
        City city2 = City.builder().idCptec(2L).name("Rio de Janeiro").stateCode("RJ").build();

        List<City> citiesToSave = Arrays.asList(city1, city2);

        // City 1 doesn't exist in DB
        when(cityRepository.findById(1L)).thenReturn(Optional.empty());

        CityEntity entity1 = new CityEntity();
        entity1.setIdCptec(1L);
        entity1.setName("São Paulo");

        when(cityMapper.toEntity(city1)).thenReturn(entity1);
        when(cityRepository.save(entity1)).thenReturn(entity1);
        when(cityMapper.toModel(entity1)).thenReturn(city1);

        // City 2 exists in DB
        CityEntity entity2 = new CityEntity();
        entity2.setIdCptec(2L);
        entity2.setName("Rio de Janeiro");

        when(cityRepository.findById(2L)).thenReturn(Optional.of(entity2));
        when(cityMapper.toModel(entity2)).thenReturn(city2);

        // Act
        List<City> result = cityService.saveCitiesToDatabase(citiesToSave);

        // Assert
        assertEquals(2, result.size());
        verify(cityRepository).findById(1L);
        verify(cityRepository).findById(2L);
        verify(cityMapper).toEntity(city1);
        verify(cityRepository).save(entity1);
        verify(cityMapper, times(2)).toModel(any(CityEntity.class));
    }

    @Test
    void givenCity_whenSaveCity_thenReturnSavedCity() {
        // Arrange
        City city = City.builder().idCptec(1L).name("São Paulo").stateCode("SP").build();

        CityEntity cityEntity = new CityEntity();
        cityEntity.setIdCptec(1L);
        cityEntity.setName("São Paulo");

        CityEntity savedEntity = new CityEntity();
        savedEntity.setIdCptec(1L);
        savedEntity.setName("São Paulo");
        savedEntity.setStateCode("SP");

        City savedCity = City.builder()
                .idCptec(1L)
                .name("São Paulo")
                .stateCode("SP")
                .build();

        when(cityMapper.toEntity(city)).thenReturn(cityEntity);
        when(cityRepository.save(cityEntity)).thenReturn(savedEntity);
        when(cityMapper.toModel(savedEntity)).thenReturn(savedCity);

        // Act
        City result = cityService.saveCity(city);

        // Assert
        assertNotNull(result);
        assertEquals(savedCity, result);
        assertEquals("São Paulo", result.getName());
        assertEquals("SP", result.getStateCode());
        verify(cityMapper).toEntity(city);
        verify(cityRepository).save(cityEntity);
        verify(cityMapper).toModel(savedEntity);
    }

    @Test
    void givenExistingCityId_whenFindById_thenReturnCity() {
        // Arrange
        Long cityId = 1L;

        CityEntity cityEntity = new CityEntity();
        cityEntity.setIdCptec(cityId);
        cityEntity.setName("São Paulo");

        City expectedCity = City.builder()
                .idCptec(cityId)
                .name("São Paulo")
                .build();

        when(cityRepository.findById(cityId)).thenReturn(Optional.of(cityEntity));
        when(cityMapper.toModel(cityEntity)).thenReturn(expectedCity);

        // Act
        Optional<City> result = cityService.findById(cityId);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(expectedCity, result.get());
        verify(cityRepository).findById(cityId);
        verify(cityMapper).toModel(cityEntity);
    }

    @Test
    void givenNonExistingCityId_whenFindById_thenReturnEmptyOptional() {
        // Arrange
        Long cityId = 999L;
        when(cityRepository.findById(cityId)).thenReturn(Optional.empty());

        // Act
        Optional<City> result = cityService.findById(cityId);

        // Assert
        assertFalse(result.isPresent());
        verify(cityRepository).findById(cityId);
        verifyNoInteractions(cityMapper);
    }
}
