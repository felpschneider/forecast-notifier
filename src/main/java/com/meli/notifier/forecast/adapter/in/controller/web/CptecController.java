package com.meli.notifier.forecast.adapter.in.controller.web;

import com.meli.notifier.forecast.application.port.in.CityService;
import com.meli.notifier.forecast.application.port.in.CptecService;
import com.meli.notifier.forecast.domain.dto.response.CityResponseDTO;
import com.meli.notifier.forecast.domain.mapper.CityMapper;
import com.meli.notifier.forecast.domain.mapper.ForecastMapper;
import com.meli.notifier.forecast.domain.model.forecast.wave.WaveForecastResponseDTO;
import com.meli.notifier.forecast.domain.model.forecast.weather.ForecastResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/v1/cptec/cities")
@Tag(name = "CPTEC", description = "CPTEC City and Forecast APIs")
@RequiredArgsConstructor
@Slf4j
public class CptecController {

    private final CptecService cptecService;
    private final CityService cityService;
    private final CityMapper cityMapper;
    private final ForecastMapper forecastMapper;

    @Operation(summary = "Search cities by name", description = "Search for cities in the CPTEC database by name")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cities found successfully"),
            @ApiResponse(responseCode = "503", description = "CPTEC API is unavailable")
    })
    @GetMapping
    public ResponseEntity<List<CityResponseDTO>> searchCities(@RequestParam String name) {
        log.info("Searching for cities with name: {}", name);
        var cities = cptecService.findCities(name);

        cityService.saveCitiesToDatabase(cities);

        List<CityResponseDTO> cityResponseDTOS = cities.stream().map(cityMapper::toDTO).collect(Collectors.toList());
        return ResponseEntity.ok(cityResponseDTOS);
    }

    @Operation(summary = "Get city forecast",
            description = "Get weather forecast for the next 4 days")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Forecast retrieved successfully",
                    content = @Content(schema = @Schema(implementation = ForecastResponseDTO.class))),
            @ApiResponse(responseCode = "503", description = "CPTEC API is unavailable")
    })
    @GetMapping("/{idCptec}/forecasts")
    public ResponseEntity<ForecastResponseDTO> getWeatherForecast(@PathVariable Long idCptec) {
        log.info("Getting forecast for city ID: {}", idCptec);
        ForecastResponseDTO forecast = cptecService.getWeatherForecast(idCptec);

        return ResponseEntity.ok(forecast);
    }

    @Operation(summary = "Get waves forecast",
            description = "Get waves forecast for a specific day for a city")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Wave forecast retrieved successfully",
                    content = @Content(schema = @Schema(implementation = WaveForecastResponseDTO.class))),
            @ApiResponse(responseCode = "503", description = "CPTEC API is unavailable")
    })
    @GetMapping("/{idCptec}/day/{day}/waves")
    public ResponseEntity<WaveForecastResponseDTO> getWaveForecast(@PathVariable Long idCptec,
                                                                   @PathVariable Integer day) {
        log.info("Getting wave forecast for city ID: {}", idCptec);
        WaveForecastResponseDTO forecast = cptecService.getWaveForecast(idCptec, day);

        return ResponseEntity.ok(forecast);
    }

}
