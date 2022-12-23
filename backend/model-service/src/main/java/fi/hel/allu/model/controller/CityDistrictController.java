package fi.hel.allu.model.controller;

import fi.hel.allu.model.dao.CityDistrictDao;
import fi.hel.allu.model.domain.CityDistrict;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/citydistricts")
public class CityDistrictController {

  private final CityDistrictDao cityDistrictDao;

  @Autowired
  public CityDistrictController(CityDistrictDao cityDistrictDao) {
    this.cityDistrictDao = cityDistrictDao;
  }

  @PutMapping
  public ResponseEntity updateCityDistricts(@RequestBody List<CityDistrict> cityDistricts) {
    cityDistrictDao.upsert(cityDistricts);
    return new ResponseEntity<Void>(HttpStatus.OK);
  }
}