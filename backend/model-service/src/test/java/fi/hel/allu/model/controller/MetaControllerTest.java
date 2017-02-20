package fi.hel.allu.model.controller;

import fi.hel.allu.model.ModelApplication;
import fi.hel.allu.model.dao.StructureMetaDao;
import fi.hel.allu.model.domain.meta.AttributeDataType;
import fi.hel.allu.model.domain.meta.AttributeMeta;
import fi.hel.allu.model.domain.meta.StructureMeta;
import fi.hel.allu.model.testUtils.WebTestCommon;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = ModelApplication.class)
@WebAppConfiguration
@Transactional
public class MetaControllerTest {

  @Autowired
  WebTestCommon wtc;

  @Autowired
  StructureMetaDao structureMetaDao;

  @Before
  public void setup() throws Exception {
    wtc.setup();
  }

  @Test
  public void loadNonExistentMeta() throws Exception {
    @SuppressWarnings("unused")
    ResultActions resultActions = wtc.perform(get("/meta/NonExistent")).andExpect(status().is4xxClientError());
  }

  @Test
  public void testLoadEventMeta() throws Exception {
    ResultActions resultActions = wtc.perform(get("/meta/EVENT")).andExpect(status().isOk());
    StructureMeta sMetaInResult = wtc.parseObjectFromResult(resultActions, StructureMeta.class);
    assertEventAttributes(sMetaInResult);
  }

  @Test
  public void testLoadEventMetaWithVersion() throws Exception {
    ResultActions resultActions = wtc.perform(get("/meta/EVENT/1")).andExpect(status().isOk());
    StructureMeta sMetaInResult = wtc.parseObjectFromResult(resultActions, StructureMeta.class);
    assertEventAttributes(sMetaInResult);
  }

  private void assertEventAttributes(StructureMeta sMetaInResult) {
    assertEquals("EVENT", sMetaInResult.getApplicationType());
    System.out.println(sMetaInResult);

    Optional<AttributeMeta> natureOpt = sMetaInResult.getAttributes().stream().filter(am -> am.getName().equals("nature")).findFirst();
    AttributeMeta nature = natureOpt.orElseThrow(() -> new RuntimeException("Nature not found"));
    assertEquals("Tapahtuman luonne", nature.getUiName());
    assertEquals(AttributeDataType.STRING, nature.getDataType());
    assertNull(nature.getListType());
    assertNull(nature.getStructureMeta());
    assertNull(nature.getValidationRule());

  }
}
