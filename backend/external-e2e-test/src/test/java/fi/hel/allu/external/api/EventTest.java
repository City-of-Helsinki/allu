package fi.hel.allu.external.api;

import java.time.ZonedDateTime;

import org.geolatte.geom.Geometry;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import fi.hel.allu.external.domain.EventExt;

import static fi.hel.allu.external.api.data.TestData.EVENT_GEOMETRY;

@RunWith(SpringJUnit4ClassRunner.class)
public class EventTest extends BaseApplicationTest<EventExt> {

  private static final String RESOURCE_PATH = "/events";
  private static final String NAME = "Tapahtuma - ext";
  private static final ZonedDateTime EVENT_START_TIME = ZonedDateTime.now().plusDays(3);
  private static final ZonedDateTime EVENT_END_TIME = ZonedDateTime.now().plusDays(10);
  private static final Integer STRUCTURE_AREA = Integer.valueOf(10);
  private static final String STRUCTURE_DESCRIPTION = "Tapahtuman rakenteiden kuvaus";
  private static final String DESCRIPTION = "Tapahtuman kuvaus";


  @Test
  public void shouldCreateEvent() {
    validateApplicationCreationSuccessful();
  }

  @Override
  protected Geometry getGeometry() {
    return EVENT_GEOMETRY;
  }

  @Override
  protected EventExt getApplication() {
    EventExt event = new EventExt();
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
