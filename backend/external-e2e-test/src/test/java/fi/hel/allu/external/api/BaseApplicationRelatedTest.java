package fi.hel.allu.external.api;

import org.geolatte.geom.Geometry;
import org.junit.Before;

import fi.hel.allu.common.domain.types.ApplicationKind;
import fi.hel.allu.external.api.data.TestData;
import fi.hel.allu.external.domain.ShortTermRentalExt;

import static fi.hel.allu.external.api.data.TestData.SHORT_TERM_RENTAL_GEOMETRY;

/**
 * Base class for tests creating application related data (attachments, comments, etc)
 * requiring application creation before tests.
 *
 * Creates one short term rental application.
 *
 */
public abstract class BaseApplicationRelatedTest extends BaseApplicationTest<ShortTermRentalExt> {

  private static final String RESOURCE_PATH = "/shorttermrentals";
  private static final ApplicationKind KIND = ApplicationKind.BRIDGE_BANNER;
  private static final String DESCRIPTION = "Vuokrauksen kuvaus";

  private Integer applicationId;

  @Before
  public void setup() {
    applicationId = validateApplicationCreationSuccessful();
  }

  protected Integer getApplicationId() {
    return applicationId;
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
  protected Geometry getGeometry() {
    return SHORT_TERM_RENTAL_GEOMETRY;
  }

  @Override
  protected String getResourcePath() {
    return RESOURCE_PATH;
  }


}
