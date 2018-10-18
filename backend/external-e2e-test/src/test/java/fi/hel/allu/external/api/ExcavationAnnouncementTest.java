package fi.hel.allu.external.api;

import org.geolatte.geom.Geometry;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import fi.hel.allu.external.domain.ExcavationAnnouncementExt;

import static fi.hel.allu.external.api.data.TestData.*;

@RunWith(SpringJUnit4ClassRunner.class)
public class ExcavationAnnouncementTest extends BaseApplicationTest<ExcavationAnnouncementExt> {

  private static final String RESOURCE_PATH = "/excavationannouncements";
  private static final String NAME = "Kaivuilmoitus - ext";

  @Test
  public void shouldCreateExcavationAnnouncement() {
    validateApplicationCreationSuccessful();
  }

  @Override
  protected Geometry getGeometry() {
    return EXCAVATION_ANNOUNCEMENT_GEOMETRY;
  }

  @Override
  protected ExcavationAnnouncementExt getApplication() {
    ExcavationAnnouncementExt excavation = new ExcavationAnnouncementExt();
    excavation.setClientApplicationKind("Pohjatutkimus");
    excavation.setWorkPurpose("Tarkoituksena tutkia pohjia");
    excavation.setContractorWithContacts(CONTRACTOR_WITH_CONTACTS);
    setCommonFields(excavation);
    return excavation;
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
