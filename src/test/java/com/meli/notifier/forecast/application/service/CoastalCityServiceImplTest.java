package com.meli.notifier.forecast.application.service;

import com.meli.notifier.forecast.adapter.out.integration.client.CptecFeignClient;
import com.meli.notifier.forecast.application.port.in.CityService;
import com.meli.notifier.forecast.domain.model.database.City;
import com.meli.notifier.forecast.domain.model.forecast.wave.WaveForecastResponseDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CoastalCityServiceImplTest {

    @Mock
    private CptecFeignClient cptecClient;

    @Mock
    private CityService cityService;

    private CoastalCityServiceImpl coastalCityService;

    @BeforeEach
    void setUp() {
        coastalCityService = new CoastalCityServiceImpl(cptecClient, cityService);
    }

    @Test
    void givenNonExistentCity_whenIsCityCoastal_thenReturnFalse() {
        // Arrange
        Long cityId = 999L;
        when(cityService.findById(cityId)).thenReturn(Optional.empty());

        // Act
        Boolean result = coastalCityService.isCityCoastal(cityId);

        // Assert
        assertFalse(result);
        verify(cityService).findById(cityId);
        verifyNoInteractions(cptecClient);
    }

    @Test
    void givenCityWithExistingCoastalStatus_whenIsCityCoastal_thenReturnStatus() {
        // Arrange
        Long cityId = 1L;
        City city = City.builder()
                .idCptec(cityId)
                .name("Rio de Janeiro")
                .isCoastal(true)
                .build();

        when(cityService.findById(cityId)).thenReturn(Optional.of(city));

        // Act
        Boolean result = coastalCityService.isCityCoastal(cityId);

        // Assert
        assertTrue(result);
        verify(cityService).findById(cityId);
        verifyNoInteractions(cptecClient);
    }

    @Test
    void givenCoastalCity_whenIsCityCoastal_thenSetStatusAndReturnTrue() {
        // Arrange
        Long cityId = 1L;
        String cityName = "Rio de Janeiro";
        City city = City.builder()
                .idCptec(cityId)
                .name(cityName)
                .isCoastal(null) // No status set yet
                .build();

        City updatedCity = city.toBuilder().isCoastal(true).build();

        WaveForecastResponseDTO waveForecast = new WaveForecastResponseDTO();
        waveForecast.setName("Praia de Copacabana"); // Different name indicates coastal city

        when(cityService.findById(cityId)).thenReturn(Optional.of(city));
        when(cptecClient.getWaveForecast(cityId, 0)).thenReturn(waveForecast);

        // Act
        Boolean result = coastalCityService.isCityCoastal(cityId);

        // Assert
        assertTrue(result);

        verify(cityService).findById(cityId);
        verify(cptecClient).getWaveForecast(cityId, 0);

        // Verify city was updated with coastal status
        verify(cityService).saveCity(argThat(c ->
                c.getIdCptec().equals(cityId) &&
                        c.getName().equals(cityName) &&
                        Boolean.TRUE.equals(c.getIsCoastal())
        ));
    }

    @Test
    void givenNonCoastalCity_whenIsCityCoastal_thenSetStatusAndReturnFalse() {
        // Arrange
        Long cityId = 1L;
        String cityName = "Brasília";
        City city = City.builder()
                .idCptec(cityId)
                .name(cityName)
                .isCoastal(null) // No status set yet
                .build();

        WaveForecastResponseDTO waveForecast = new WaveForecastResponseDTO();
        waveForecast.setName(cityName); // Same name indicates non-coastal city

        when(cityService.findById(cityId)).thenReturn(Optional.of(city));
        when(cptecClient.getWaveForecast(cityId, 0)).thenReturn(waveForecast);

        // Act
        Boolean result = coastalCityService.isCityCoastal(cityId);

        // Assert
        assertFalse(result);

        verify(cityService).findById(cityId);
        verify(cptecClient).getWaveForecast(cityId, 0);

        // Verify city was updated with non-coastal status
        verify(cityService).saveCity(argThat(c ->
                c.getIdCptec().equals(cityId) &&
                        c.getName().equals(cityName) &&
                        Boolean.FALSE.equals(c.getIsCoastal())
        ));
    }

    @Test
    void givenApiError_whenIsCityCoastal_thenPropagateException() {
        // Arrange
        Long cityId = 1L;
        String cityName = "Rio de Janeiro";
        City city = City.builder()
                .idCptec(cityId)
                .name(cityName)
                .isCoastal(null) // No status set yet
                .build();

        Exception expectedException = new RuntimeException("API Error");

        when(cityService.findById(cityId)).thenReturn(Optional.of(city));
        when(cptecClient.getWaveForecast(cityId, 0)).thenThrow(expectedException);

        // Act & Assert
        Exception thrownException = assertThrows(RuntimeException.class,
                () -> coastalCityService.isCityCoastal(cityId));

        assertEquals(expectedException, thrownException);
        verify(cityService).findById(cityId);
        verify(cptecClient).getWaveForecast(cityId, 0);
        verify(cityService, never()).saveCity(any());
    }
}
