package fi.hel.allu.ui.controller;

import fi.hel.allu.servicecore.domain.CoordinateJson;
import fi.hel.allu.servicecore.domain.PostalAddressJson;
import fi.hel.allu.ui.service.AddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/address")
public class AddressController {
  @Autowired
  private AddressService addressService;

  @RequestMapping(value = {
          "/geocode/{city}/{street}",
          "/geocode/{city}/{street}/{number}",
          "/geocode/{city}/{street}/{number}/{letter}"
  }, method = RequestMethod.GET)
  @PreAuthorize("hasAnyRole('ROLE_VIEW')")
  public ResponseEntity<CoordinateJson> geocode(@PathVariable final String city,
                                                @PathVariable final String street,
                                                @PathVariable(name = "number", required = false) final Optional<Integer> number,
                                                @PathVariable(name = "letter", required = false) final Optional<String> letter) {
    return new ResponseEntity<>(addressService.geocodeAddress(street, number, letter), HttpStatus.OK);
  }



  @RequestMapping(value = "/search/{partialStreetName}", method = RequestMethod.GET)
  @PreAuthorize("hasAnyRole('ROLE_VIEW')")
  public ResponseEntity<List<PostalAddressJson>> findMatchingStreet(@PathVariable final String partialStreetName) {
    return new ResponseEntity<>(addressService.findMatchingStreet(partialStreetName), HttpStatus.OK);
  }

}
