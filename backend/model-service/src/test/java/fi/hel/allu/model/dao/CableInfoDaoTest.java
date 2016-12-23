package fi.hel.allu.model.dao;

import fi.hel.allu.common.types.CableInfoType;
import fi.hel.allu.model.ModelApplication;
import fi.hel.allu.model.domain.CableInfoText;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = ModelApplication.class)
@WebAppConfiguration
@Transactional
public class CableInfoDaoTest {

  @Autowired
  private CableInfoDao cableInfoDao;

  @Test
  public void testCreateCableInfoText() {
    CableInfoText result = cableInfoDao.createCableInfoText(CableInfoType.GAS, "Gasoline is good");
    assertEquals(CableInfoType.GAS, result.getCableInfoType());
    assertEquals("Gasoline is good", result.getTextValue());
  }

  @Test
  public void testUpdateCableInfoText() {
    CableInfoText original = cableInfoDao.createCableInfoText(CableInfoType.TRAMWAY, "Raitsikka!");
    CableInfoText updated = cableInfoDao.updateCableInfoText(original.getId(), "Spåra");
    assertEquals(CableInfoType.TRAMWAY, updated.getCableInfoType());
    assertEquals("Spåra", updated.getTextValue());
  }

  @Test
  public void testGetCableInfoTexts() {
    cableInfoDao.createCableInfoText(CableInfoType.GAS, "Kaasua, komisario Palmu!");
    cableInfoDao.createCableInfoText(CableInfoType.ELECTRICITY, "Iskee kuin miljoona volttia");
    cableInfoDao.createCableInfoText(CableInfoType.STREET_HEATING, "Katu kuuma kaupungin");
    List<CableInfoText> texts = cableInfoDao.getCableInfoTexts();
    assertEquals(3, texts.size());
    assertEquals(1, texts.stream().filter(ci -> ci.getCableInfoType() == CableInfoType.GAS).count());
    assertEquals(1, texts.stream().filter(ci -> ci.getTextValue().startsWith("Kaasua")).count());
  }

  @Test
  public void testDeleteCableInfoText() {
    cableInfoDao.createCableInfoText(CableInfoType.SEWAGE_PIPE, "Viemäri");
    cableInfoDao.createCableInfoText(CableInfoType.TELECOMMUNICATION, "Puhelinlanka");
    cableInfoDao.createCableInfoText(CableInfoType.OTHER, "Joku ihan muu");
    List<CableInfoText> texts = cableInfoDao.getCableInfoTexts();
    int deleteId = texts.stream().filter(ci -> ci.getTextValue().equals("Viemäri")).findAny().get().getId();
    cableInfoDao.deleteCableInfoText(deleteId);
    List<CableInfoText> newTexts = cableInfoDao.getCableInfoTexts();
    assertEquals(texts.size() - 1, newTexts.size());
    assertEquals(0, newTexts.stream().filter(ci -> ci.getTextValue().equals("Viemäri")).count());
  }
}
