package fi.hel.allu.servicecore.mapper.extension;

import fi.hel.allu.model.domain.ExcavationAnnouncement;
import fi.hel.allu.servicecore.domain.ExcavationAnnouncementJson;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class ExcavationAnnouncementMapperTest {

  private static final Boolean NO_AREA_USAGE_FEE = true;
  private static final String NO_AREA_USAGE_FEE_REASON = "Kaupungin kaavahankkeen toteuttaminen";

  @Test
  public void modelToJson_shouldMapNoAreaUsageFee() {
    ExcavationAnnouncement model = createModel();

    ExcavationAnnouncementJson json = ExcavationAnnouncementMapper.modelToJson(model);

    assertEquals(NO_AREA_USAGE_FEE, json.getNoAreaUsageFee());
    assertEquals(NO_AREA_USAGE_FEE_REASON, json.getNoAreaUsageFeeReason());
  }

  @Test
  public void jsonToModel_shouldMapNoAreaUsageFee() {
    ExcavationAnnouncementJson json = createJson();

    ExcavationAnnouncement model = ExcavationAnnouncementMapper.jsonToModel(json);

    assertEquals(NO_AREA_USAGE_FEE, model.getNoAreaUsageFee());
    assertEquals(NO_AREA_USAGE_FEE_REASON, model.getNoAreaUsageFeeReason());
  }

  @Test
  public void modelToJson_shouldHandleNullNoAreaUsageFee() {
    ExcavationAnnouncement model = new ExcavationAnnouncement();

    ExcavationAnnouncementJson json = ExcavationAnnouncementMapper.modelToJson(model);

    assertNull(json.getNoAreaUsageFee());
    assertNull(json.getNoAreaUsageFeeReason());
  }

  @Test
  public void jsonToModel_shouldHandleNullNoAreaUsageFee() {
    ExcavationAnnouncementJson json = new ExcavationAnnouncementJson();

    ExcavationAnnouncement model = ExcavationAnnouncementMapper.jsonToModel(json);

    assertNull(model.getNoAreaUsageFee());
    assertNull(model.getNoAreaUsageFeeReason());
  }

  @Test
  public void roundTrip_modelToJsonToModel_preservesNoAreaUsageFee() {
    ExcavationAnnouncement original = createModel();

    ExcavationAnnouncementJson json = ExcavationAnnouncementMapper.modelToJson(original);
    ExcavationAnnouncement roundTripped = ExcavationAnnouncementMapper.jsonToModel(json);

    assertEquals(original.getNoAreaUsageFee(), roundTripped.getNoAreaUsageFee());
    assertEquals(original.getNoAreaUsageFeeReason(), roundTripped.getNoAreaUsageFeeReason());
  }

  private ExcavationAnnouncement createModel() {
    ExcavationAnnouncement model = new ExcavationAnnouncement();
    model.setNoAreaUsageFee(NO_AREA_USAGE_FEE);
    model.setNoAreaUsageFeeReason(NO_AREA_USAGE_FEE_REASON);
    return model;
  }

  private ExcavationAnnouncementJson createJson() {
    ExcavationAnnouncementJson json = new ExcavationAnnouncementJson();
    json.setNoAreaUsageFee(NO_AREA_USAGE_FEE);
    json.setNoAreaUsageFeeReason(NO_AREA_USAGE_FEE_REASON);
    return json;
  }
}
