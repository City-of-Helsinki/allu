package fi.hel.allu.external.api;

import org.geolatte.geom.Geometry;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import fi.hel.allu.common.domain.types.ApplicationKind;
import fi.hel.allu.external.domain.ShortTermRentalExt;

import static fi.hel.allu.external.api.data.TestData.SHORT_TERM_RENTAL_GEOMETRY;

@RunWith(SpringJUnit4ClassRunner.class)
public class ShortTermRentalTest extends BaseApplicationTest<ShortTermRentalExt>{

  private static final String RESOURCE_PATH = "/shorttermrentals";
  private static final String NAME = "Lyhyt vuokraus - ext";
  private static final String DESCRIPTION = "Vuokrauksen kuvaus";
  private static final ApplicationKind KIND = ApplicationKind.BENJI;

  @Test
  public void shouldCreateShortTermRental() {
    validateApplicationCreationSuccessful();
  }

  @Override
  protected Geometry getGeometry() {
    return SHORT_TERM_RENTAL_GEOMETRY;
  }

  @Override
  protected ShortTermRentalExt getApplication() {
    ShortTermRentalExt shortTermRental = new ShortTermRentalExt();
    shortTermRental.setDescription(DESCRIPTION);
    shortTermRental.setApplicationKind(KIND);
    setCommonFields(shortTermRental);
    return shortTermRental;
  }

  @Override
  protected String getApplicationName() {
    return NAME;
  }

  @Override
  protected String getResourcePath() {
    return RESOURCE_PATH;
  }

}
