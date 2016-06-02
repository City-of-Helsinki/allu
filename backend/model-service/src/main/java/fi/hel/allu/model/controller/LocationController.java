package fi.hel.allu.model.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import fi.hel.allu.NoSuchEntityException;
import fi.hel.allu.model.dao.LocationDao;
import fi.hel.allu.model.domain.Location;

@RestController
@RequestMapping("/locations")
public class LocationController {

  @Autowired
  private LocationDao locationDao;

  @RequestMapping(value = "/{id}", method = RequestMethod.GET)
  public ResponseEntity<Location> find(@PathVariable int id) {
    Optional<Location> location = locationDao.findById(id);
    Location locationValue = location
        .orElseThrow(() -> new NoSuchEntityException("Location not found", Integer.toString(id)));
    return new ResponseEntity<Location>(locationValue, HttpStatus.OK);
  }

  @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
  public ResponseEntity<Location> update(@PathVariable int id, @RequestBody(required = true) Location location) {
    return new ResponseEntity<Location>(locationDao.update(id, location), HttpStatus.OK);
  }

  @RequestMapping(method = RequestMethod.POST)
  public ResponseEntity<Location> insert(@RequestBody(required = true) Location location) {
    if (location.getId() != null) {
      throw new IllegalArgumentException("Id must be null for insert");
    }
    return new ResponseEntity<Location>(locationDao.insert(location), HttpStatus.OK);
  }
}
