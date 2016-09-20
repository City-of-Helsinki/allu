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

import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = ModelApplication.class)
@WebAppConfiguration
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
  public void testLoadOutdoorEventMeta() throws Exception {
    ResultActions resultActions = wtc.perform(get("/meta/OUTDOOREVENT")).andExpect(status().isOk());
    StructureMeta sMetaInResult = wtc.parseObjectFromResult(resultActions, StructureMeta.class);
    assertOutdoorEventAttributes(sMetaInResult);
  }

  @Test
  public void testLoadOutdoorEventMetaWithVersion() throws Exception {
    ResultActions resultActions = wtc.perform(get("/meta/OUTDOOREVENT/1")).andExpect(status().isOk());
    StructureMeta sMetaInResult = wtc.parseObjectFromResult(resultActions, StructureMeta.class);
    assertOutdoorEventAttributes(sMetaInResult);
  }

  private void assertOutdoorEventAttributes(StructureMeta sMetaInResult) {
    assertEquals("OUTDOOREVENT", sMetaInResult.getApplicationType());
    System.out.println(sMetaInResult);

    Optional<AttributeMeta> natureOpt = sMetaInResult.getAttributes().stream().filter(am -> am.getName().equals("nature")).findFirst();
    AttributeMeta nature = natureOpt.orElseThrow(() -> new RuntimeException("Nature not found"));
    assertEquals("Tapahtuman luonne", nature.getUiName());
    assertEquals(AttributeDataType.STRING, nature.getDataType());
    assertNull(nature.getListType());
    assertNull(nature.getStructureMeta());
    assertNull(nature.getValidationRule());

    Optional<AttributeMeta> contactOpt = sMetaInResult.getAttributes().stream().filter(am -> am.getName().equals("contact")).findFirst();
    AttributeMeta contact = contactOpt.orElseThrow(() -> new RuntimeException("Contact not found"));
    assertEquals("Yhteyshenkilö", contact.getUiName());
    assertNotNull(contact.getStructureMeta());
    StructureMeta contactStructureMeta = contact.getStructureMeta();

    Set<String> contactNames = contactStructureMeta.getAttributes().stream().map(am -> am.getName()).collect(Collectors.toSet());
    assertEquals(6, contactNames.size());
    Set<String> expectedNames = new HashSet<>(
        Arrays.asList(new String[] { "contactName", "address", "postalCode", "postOffice", "phoneNumber", "email" }));
    assertTrue(contactNames.containsAll(expectedNames));

    assertTrue(contactStructureMeta.getAttributes().stream().allMatch(am -> am.getDataType().equals(AttributeDataType.STRING)));
    assertTrue(contactStructureMeta.getAttributes().stream().allMatch(am -> am.getStructureAttribute() == null));
    assertTrue(contactStructureMeta.getAttributes().stream().allMatch(am -> am.getListType() == null));
    assertTrue(contactStructureMeta.getAttributes().stream().allMatch(am -> am.getStructureMeta() == null));


    Optional<AttributeMeta> applicantOpt = sMetaInResult.getAttributes().stream().filter(am -> am.getName().equals("applicant")).findFirst();
    AttributeMeta applicant = applicantOpt.orElseThrow(() -> new RuntimeException("Applicant not found"));
    assertEquals("Hakija", applicant.getUiName());
    assertNotNull(applicant.getStructureMeta());
    StructureMeta applicantStructureMeta = applicant.getStructureMeta();

    Set<String> applicantNames = applicantStructureMeta.getAttributes().stream().map(am -> am.getName()).collect(Collectors.toSet());
    assertEquals(10, applicantNames.size());
    expectedNames = new HashSet<>(
        Arrays.asList(new String[] { "personName", "companyName", "organizationName", "businessId", "ssn", "address",
            "postalCode", "postOffice", "phoneNumber", "email" }));
    assertTrue(applicantNames.containsAll(expectedNames));
  }
}
