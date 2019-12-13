package fi.hel.allu.external.api;

import fi.hel.allu.external.domain.PromotionExt;
import org.geolatte.geom.Geometry;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.time.ZonedDateTime;

import static fi.hel.allu.external.api.data.TestData.EVENT_GEOMETRY;

@RunWith(SpringJUnit4ClassRunner.class)
public class PromotionTest extends BaseApplicationTest<PromotionExt> {

  private static final String RESOURCE_PATH = "/events/promotions";
  private static final String NAME = "Promootio - ext";
  private static final ZonedDateTime EVENT_START_TIME = ZonedDateTime.now().plusDays(3);
  private static final ZonedDateTime EVENT_END_TIME = ZonedDateTime.now().plusDays(10);
  private static final Integer STRUCTURE_AREA = Integer.valueOf(10);
  private static final String STRUCTURE_DESCRIPTION = "Promootion rakenteiden kuvaus";
  private static final String DESCRIPTION = "Promootion kuvaus";


  @Test
  public void shouldCreatePromotion() {
    validateApplicationCreationSuccessful();
  }

  @Override
  protected Geometry getGeometry() {
    return EVENT_GEOMETRY;
  }

  @Override
  protected PromotionExt getApplication() {
    PromotionExt event = new PromotionExt();
    event.setEventStartTime(EVENT_START_TIME);
    event.setEventEndTime(EVENT_END_TIME);
    event.setStructureArea(STRUCTURE_AREA);
    event.setStructureDescription(STRUCTURE_DESCRIPTION);
    event.setDescription(DESCRIPTION);
    setCommonFields(event);
    return event;
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
