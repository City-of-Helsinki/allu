package fi.hel.allu.model.dao;

import fi.hel.allu.common.domain.types.ApplicationType;
import fi.hel.allu.common.types.DefaultTextType;
import fi.hel.allu.model.ModelApplication;
import fi.hel.allu.model.domain.DefaultText;
import fi.hel.allu.model.testUtils.TestCommon;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;
import java.util.List;

import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = ModelApplication.class)
@WebAppConfiguration
@Transactional
public class DefaultTextDaoTest {

  private final ApplicationType APPLICATION_TYPE = ApplicationType.CABLE_REPORT;

  @Autowired
  private DefaultTextDao defaultTextDao;

  @Autowired
  TestCommon testCommon;

  @Test
  public void testCreateCableInfoText() {
    DefaultText result =
        defaultTextDao.create(new DefaultText(null, APPLICATION_TYPE, DefaultTextType.GAS, "Gasoline is good"));
    assertEquals(APPLICATION_TYPE, result.getApplicationType());
    assertEquals(DefaultTextType.GAS, result.getTextType());
    assertEquals("Gasoline is good", result.getTextValue());
  }

  @Test
  public void testUpdateCableInfoText() {
    DefaultText original =
        defaultTextDao.create(new DefaultText(null, APPLICATION_TYPE, DefaultTextType.TRAMWAY, "Raitsikka!"));
    DefaultText updated =
        defaultTextDao.update(original.getId(), new DefaultText(null, APPLICATION_TYPE, DefaultTextType.TRAMWAY, "Spåra"));
    assertEquals(DefaultTextType.TRAMWAY, updated.getTextType());
    assertEquals("Spåra", updated.getTextValue());
  }

  @Test
  public void testGetCableInfoTexts() throws SQLException {
    testCommon.deleteAllData();
    defaultTextDao.create(
        new DefaultText(null, APPLICATION_TYPE, DefaultTextType.GAS, "Kaasua, komisario Palmu!"));
    defaultTextDao.create(
        new DefaultText(null, APPLICATION_TYPE, DefaultTextType.ELECTRICITY, "Iskee kuin miljoona volttia"));
    defaultTextDao.create(
        new DefaultText(null, APPLICATION_TYPE, DefaultTextType.STREET_HEATING, "Katu kuuma kaupungin"));
    List<DefaultText> texts = defaultTextDao.getDefaultTexts(APPLICATION_TYPE);
    assertEquals(3, texts.size());
    assertEquals(1, texts.stream().filter(ci -> ci.getTextType() == DefaultTextType.GAS).count());
    assertEquals(1, texts.stream().filter(ci -> ci.getTextValue().startsWith("Kaasua")).count());
  }

  @Test
  public void testDeleteCableInfoText() {
    defaultTextDao.create(new DefaultText(null, APPLICATION_TYPE, DefaultTextType.SEWAGE_PIPE, "Viemäri"));
    defaultTextDao.create(new DefaultText(null, APPLICATION_TYPE, DefaultTextType.TELECOMMUNICATION, "Puhelinlanka"));
    defaultTextDao.create(new DefaultText(null, APPLICATION_TYPE, DefaultTextType.OTHER, "Joku ihan muu"));
    List<DefaultText> texts = defaultTextDao.getDefaultTexts(APPLICATION_TYPE);
    int deleteId = texts.stream().filter(ci -> ci.getTextValue().equals("Viemäri")).findAny().get().getId();
    defaultTextDao.delete(deleteId);
    List<DefaultText> newTexts = defaultTextDao.getDefaultTexts(APPLICATION_TYPE);
    assertEquals(texts.size() - 1, newTexts.size());
    assertEquals(0, newTexts.stream().filter(ci -> ci.getTextValue().equals("Viemäri")).count());
  }
}
