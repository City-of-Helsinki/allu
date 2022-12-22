package fi.hel.allu.ui.controller;

import fi.hel.allu.servicecore.domain.CoordinateJson;
import fi.hel.allu.servicecore.domain.PostalAddressJson;
import fi.hel.allu.ui.service.AddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/address")
public class AddressController {
  @Autowired
  private AddressService addressService;

  @GetMapping(value = {"/geocode/{city}/{street}",
          "/geocode/{city}/{street}/{specifier}"})
  @PreAuthorize("hasAnyRole('ROLE_VIEW')")
  public ResponseEntity<CoordinateJson> geocode(@PathVariable final String city,
                                                @PathVariable final String street,
                                                @PathVariable(name = "specifier", required = false) final Optional<String> specifier) {
    return new ResponseEntity<>(addressService.geocodeAddress(street, specifier), HttpStatus.OK);
  }



  @GetMapping(value = "/search/{partialStreetName}")
  @PreAuthorize("hasAnyRole('ROLE_VIEW')")
  public ResponseEntity<List<PostalAddressJson>> findMatchingStreet(@PathVariable final String partialStreetName) {
    return new ResponseEntity<>(addressService.findMatchingStreet(partialStreetName), HttpStatus.OK);
  }

}