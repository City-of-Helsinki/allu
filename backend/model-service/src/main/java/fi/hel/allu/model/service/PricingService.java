package fi.hel.allu.model.service;

import fi.hel.allu.common.types.OutdoorEventNature;
import fi.hel.allu.model.dao.LocationDao;
import fi.hel.allu.model.dao.PricingDao;
import fi.hel.allu.model.domain.Application;
import fi.hel.allu.model.domain.Location;
import fi.hel.allu.model.pricing.Pricing;
import fi.hel.allu.model.pricing.PricingConfiguration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Component
public class PricingService {

  private PricingDao pricingDao;
  private LocationDao LocationDao;

  @Autowired
  public PricingService(PricingDao pricingDao, LocationDao locationDao) {
    this.pricingDao = pricingDao;
    this.LocationDao = locationDao;
  }

  @Transactional
  public int calculatePrice(Application application) {
    Optional<Location> loca = LocationDao.findById(application.getLocationId());

    String square = null;
    String section = null;
    OutdoorEventNature nature = OutdoorEventNature.PUBLIC_FREE;
    Optional<PricingConfiguration> pricingConfiguration =
        pricingDao.findByLocationAndNature(square, section, nature);
    Pricing pricing = new Pricing();
    int eventDays = 0;
    int buildDays = 0;
    double structureArea = 0.0;
    double area = 0.0;
    long priceInFunnyUnits = pricing.calculateFullPrice(pricingConfiguration.get(), eventDays, buildDays, structureArea,
        area);
    return (int) ((priceInFunnyUnits + 50) / 100);
  }
}
