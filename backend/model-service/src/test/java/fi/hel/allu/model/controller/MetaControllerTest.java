package fi.hel.allu.model.controller;

import fi.hel.allu.model.ModelApplication;
import fi.hel.allu.model.dao.StructureMetaDao;
import fi.hel.allu.model.domain.AreaRental;
import fi.hel.allu.model.domain.meta.AttributeDataType;
import fi.hel.allu.model.domain.meta.AttributeMeta;
import fi.hel.allu.model.domain.meta.StructureMeta;
import fi.hel.allu.model.testUtils.WebTestCommon;
import fi.hel.allu.servicecore.domain.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = ModelApplication.class)
@WebAppConfiguration
@Transactional
public class MetaControllerTest {

  private static final String EXTENSION = "/extension";

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
    Map<String, String> overrides = Collections.singletonMap(EXTENSION, "EVENT");
    ResultActions resultActions = wtc.perform(post("/meta/Application"), overrides).andExpect(status().isOk());
    StructureMeta sMetaInResult = wtc.parseObjectFromResult(resultActions, StructureMeta.class);
    assertEventAttributes(sMetaInResult);
  }

  @Test
  public void testLoadEventMetaWithVersion() throws Exception {
    Map<String, String> overrides = Collections.singletonMap(EXTENSION, "EVENT");
    ResultActions resultActions = wtc.perform(post("/meta/Application/1"), overrides).andExpect(status().isOk());
    StructureMeta sMetaInResult = wtc.parseObjectFromResult(resultActions, StructureMeta.class);
    assertEventAttributes(sMetaInResult);
  }

  @Test
  public void testCheckApplicationMetaAgainstJsonClass() throws Exception {
    Map<String, String> overrides = Collections.singletonMap(EXTENSION, "EVENT");
    ResultActions resultActions = wtc.perform(post("/meta/Application/1"), overrides).andExpect(status().isOk());
    StructureMeta sMetaInResult = wtc.parseObjectFromResult(resultActions, StructureMeta.class);
    List<String> applicationAttributes =
        Arrays.stream(ApplicationJson.class.getDeclaredFields()).map(df -> df.getName()).map(name -> "/" + name).collect(Collectors.toList());
    HashSet<String> metaAttributes =
        sMetaInResult.getAttributes().stream().map(a -> attributeName(a)).collect(Collectors.toCollection(HashSet::new));
    applicationAttributes.remove("/metadataVersion");
    applicationAttributes.forEach(aa -> assertTrue("metadata is missing:" + aa, metaAttributes.contains(aa))); //
  }

  @Test
  public void testCheckExcavationAnnouncementMetaAgainstJsonClass() throws Exception {
    Map<String, String> overrides = Collections.singletonMap(EXTENSION, "EXCAVATION_ANNOUNCEMENT");
    ResultActions resultActions = wtc.perform(post("/meta/Application/1"), overrides).andExpect(status().isOk());
    StructureMeta sMetaInResult = wtc.parseObjectFromResult(resultActions, StructureMeta.class);
    assertExtensionAttributes(ExcavationAnnouncementJson.class, sMetaInResult);
  }

  @Test
  public void testCheckTemporaryTrafficArrangementMetaAgainstJsonClass() throws Exception {
    Map<String, String> overrides = Collections.singletonMap(EXTENSION, "TEMPORARY_TRAFFIC_ARRANGEMENTS");
    ResultActions resultActions = wtc.perform(post("/meta/Application/1"), overrides).andExpect(status().isOk());
    StructureMeta sMetaInResult = wtc.parseObjectFromResult(resultActions, StructureMeta.class);
    assertExtensionAttributes(TrafficArrangementJson.class, sMetaInResult);
  }

  @Test
  public void testCheckCableReportMetaAgainstJsonClass() throws Exception {
    Map<String, String> overrides = Collections.singletonMap(EXTENSION, "CABLE_REPORT");
    ResultActions resultActions = wtc.perform(post("/meta/Application/1"), overrides).andExpect(status().isOk());
    StructureMeta sMetaInResult = wtc.parseObjectFromResult(resultActions, StructureMeta.class);
    assertExtensionAttributes(CableReportJson.class, sMetaInResult);
  }

  @Test
  public void testCheckPlacementContractMetaAgainstJsonClass() throws Exception {
    Map<String, String> overrides = Collections.singletonMap(EXTENSION, "PLACEMENT_CONTRACT");
    ResultActions resultActions = wtc.perform(post("/meta/Application/1"), overrides).andExpect(status().isOk());
    StructureMeta sMetaInResult = wtc.parseObjectFromResult(resultActions, StructureMeta.class);
    assertExtensionAttributes(PlacementContractJson.class, sMetaInResult);
  }

  @Test
  public void testCheckEventMetaAgainstJsonClass() throws Exception {
    Map<String, String> overrides = Collections.singletonMap(EXTENSION, "EVENT");
    ResultActions resultActions = wtc.perform(post("/meta/Application/1"), overrides).andExpect(status().isOk());
    StructureMeta sMetaInResult = wtc.parseObjectFromResult(resultActions, StructureMeta.class);
    assertExtensionAttributes(EventJson.class, sMetaInResult);
  }

  @Test
  public void testCheckShortTermRentalMetaAgainstJsonClass() throws Exception {
    Map<String, String> overrides = Collections.singletonMap(EXTENSION, "SHORT_TERM_RENTAL");
    ResultActions resultActions = wtc.perform(post("/meta/Application/1"), overrides).andExpect(status().isOk());
    StructureMeta sMetaInResult = wtc.parseObjectFromResult(resultActions, StructureMeta.class);
    assertExtensionAttributes(ShortTermRentalJson.class, sMetaInResult);
  }

  @Test
  public void testCheckAreaRentalAgainstJsonClass() throws Exception {
    Map<String, String> overrides = Collections.singletonMap(EXTENSION, "AREA_RENTAL");
    ResultActions resultActions = wtc.perform(post("/meta/Application/1"), overrides).andExpect(status().isOk());
    StructureMeta sMetaInResult = wtc.parseObjectFromResult(resultActions, StructureMeta.class);
    assertExtensionAttributes(AreaRental.class, sMetaInResult);
  }

  @Test
  public void testCheckNoteMetaAgainstJsonClass() throws Exception {
    Map<String, String> overrides = Collections.singletonMap(EXTENSION, "NOTE");
    ResultActions resultActions = wtc.perform(post("/meta/Application/1"), overrides).andExpect(status().isOk());
    StructureMeta sMetaInResult = wtc.parseObjectFromResult(resultActions, StructureMeta.class);
    assertExtensionAttributes(NoteJson.class, sMetaInResult);
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

  private void assertExtensionAttributes(Class extensionJsonClass, StructureMeta sMeta) {
    List<String> jsonAttributes =
        Arrays.stream(extensionJsonClass.getDeclaredFields()).map(df -> df.getName()).map(name -> "/extension/" + name).collect(Collectors.toList());
    HashSet<String> metaAttributes =
        sMeta.getAttributes().stream().map(a -> attributeName(a)).filter(name -> name.startsWith("/extension"))
            .collect(Collectors.toCollection(HashSet::new));
    jsonAttributes.forEach(a -> assertTrue("metadata is missing: " + a, metaAttributes.contains(a)));
  }

  /*
   * List-type attributes have "/*" at the end. For verification, it needs to be
   * stripped away:
   */
  private String attributeName(AttributeMeta attribute) {
    if (attribute.getDataType() == AttributeDataType.LIST) {
      String metaName = attribute.getName();
      assertTrue("List metadata name doesn't end with /*: " + metaName, metaName.endsWith("/*"));
      return metaName.substring(0, metaName.length() - 2);
    } else {
      return attribute.getName();
    }
  }
}
