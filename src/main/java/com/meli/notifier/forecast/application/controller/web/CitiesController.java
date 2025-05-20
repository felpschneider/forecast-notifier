package com.meli.notifier.forecast.application.controller.web;

import com.meli.notifier.forecast.application.dto.response.CityResponseDTO;
import com.meli.notifier.forecast.application.mapper.ForecastMapper;
import com.meli.notifier.forecast.domain.mapper.CityMapper;
import com.meli.notifier.forecast.domain.model.forecast.CombinedForecastDTO;
import com.meli.notifier.forecast.domain.service.CityService;
import com.meli.notifier.forecast.domain.service.CptecService;
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

@RestController
@RequestMapping("/v1/cities")
@Tag(name = "Cities", description = "CPTEC City and Forecast APIs")
@RequiredArgsConstructor
@Slf4j
public class CitiesController {

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
        var cities = cityService.findCities(name);

        List<CityResponseDTO> cityResponseDTOS = cityMapper.toResponseDTOs(cities);
        return ResponseEntity.ok(cityResponseDTOS);
    }

    @Operation(summary = "Get city forecast",
            description = "Get weather forecast for the next 4 days and wave forecast for coastal cities")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Forecast retrieved successfully",
                    content = @Content(schema = @Schema(implementation = CombinedForecastDTO.class))),
            @ApiResponse(responseCode = "503", description = "CPTEC API is unavailable")
    })
    @GetMapping("/{idCptec}/forecast")
    public ResponseEntity<CombinedForecastDTO> getCityForecast(@PathVariable Long idCptec) {
        log.info("Getting forecast for city ID: {}", idCptec);
        CombinedForecastDTO forecast = cptecService.getCombinedForecast(idCptec);

        return ResponseEntity.ok(forecast);
    }

}
