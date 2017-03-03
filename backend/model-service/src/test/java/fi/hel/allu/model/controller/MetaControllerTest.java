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

import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
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
    assertEquals("Application", sMetaInResult.getTypeName());

    // Exactly one of the attributes should be "/extension/nature"
    List<AttributeMeta> natures = sMetaInResult.getAttributes().stream()
        .filter(am -> am.getName().equals("/extension/nature")).collect(Collectors.toList());
    assertEquals(1, natures.size());
    AttributeMeta nature = natures.get(0);
    // It should be an enumeration with proper UI name
    assertEquals("Tapahtuman luonne", nature.getUiName());
    assertEquals(AttributeDataType.ENUMERATION, nature.getDataType());
    assertNull(nature.getListType());

    // Make sure all attributes have UI names
    for (AttributeMeta attributeMeta : sMetaInResult.getAttributes()) {
      assertNotNull(attributeMeta.getUiName());
    }
  }
}
