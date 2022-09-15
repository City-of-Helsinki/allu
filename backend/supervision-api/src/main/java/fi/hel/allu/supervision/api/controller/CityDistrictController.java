package fi.hel.allu.supervision.api.controller;

import fi.hel.allu.servicecore.domain.CityDistrictInfoJson;
import fi.hel.allu.servicecore.service.LocationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/v1/citydistricts")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "City districts")
public class CityDistrictController {

    private final LocationService locationService;

    public CityDistrictController(LocationService locationService) {
        this.locationService = locationService;
    }

    @Operation(summary = "List all city districts")
    @GetMapping(produces = "application/json")
    @PreAuthorize("hasAnyRole('ROLE_SUPERVISE', 'ROLE_VIEW')")
    public ResponseEntity<List<CityDistrictInfoJson>> getAllCityDistricts() {
        return ResponseEntity.ok(locationService.getCityDistrictList());
    }

    @Operation(summary = "Get city district by id")
    @GetMapping(value = "/{id}", produces = "application/json")
    @PreAuthorize("hasAnyRole('ROLE_SUPERVISE', 'ROLE_VIEW')")
    public ResponseEntity<CityDistrictInfoJson> getCityDistrictById(@PathVariable Integer id) {
        return ResponseEntity.ok(locationService.getCityDistrictById(id));
    }
}
