package fi.hel.allu.model.dao;

import fi.hel.allu.common.domain.types.*;
import fi.hel.allu.common.types.*;
import fi.hel.allu.model.ModelApplication;
import fi.hel.allu.model.domain.meta.AttributeDataType;
import fi.hel.allu.model.domain.meta.AttributeMeta;
import fi.hel.allu.model.domain.meta.StructureMeta;
import fi.hel.allu.servicecore.domain.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = ModelApplication.class)
@WebAppConfiguration
@Transactional
public class StructureMetaDaoTest {
  @Autowired
  private StructureMetaDao structureMetaDao;

  @Test
  public void testfindCompleteByType() {

    String APPLICATION_TYPE = "Application";
    Map<String, String> typeOverrides = new HashMap<>();
    typeOverrides.put("/extension", "EVENT");

    Optional<StructureMeta> structureMeta = structureMetaDao.findCompleteByType(APPLICATION_TYPE, 1, typeOverrides);
    assertTrue(structureMeta.isPresent());
    // Check that there's one event-specific attribute in the metadata
    assertEquals(1,
        structureMeta.get().getAttributes().stream().filter(a -> a.getName().equals("/extension/foodSales"))
            .count());
  }

  @Test
  public void testFindContactMeta() {
    Optional<StructureMeta> meta = structureMetaDao.findCompleteByType("Contact", 1, Collections.emptyMap());
    assertTrue(meta.isPresent());
    assertStructureAttributes(ContactJson.class, meta.get());
  }

  @Test
  public void testFindPostalAddressMeta() {
    Optional<StructureMeta> meta = structureMetaDao.findCompleteByType("PostalAddress", 1, Collections.emptyMap());
    assertTrue(meta.isPresent());
    assertStructureAttributes(PostalAddressJson.class, meta.get());
  }

  @Test
  public void testFindCustomerMeta() {
    Optional<StructureMeta> meta = structureMetaDao.findCompleteByType("Customer", 1, Collections.emptyMap());
    assertTrue(meta.isPresent());
    assertStructureAttributes(CustomerJson.class, meta.get());
  }

  @Test
  public void testFindLocationMeta() {
    Optional<StructureMeta> meta = structureMetaDao.findCompleteByType("Location", 1, Collections.emptyMap());
    assertTrue(meta.isPresent());
    assertStructureAttributes(LocationJson.class, meta.get());
  }

  @Test
  public void testFindAttachmentMeta() {
    Optional<StructureMeta> meta = structureMetaDao.findCompleteByType("Attachment", 1, Collections.emptyMap());
    assertTrue(meta.isPresent());
    assertStructureAttributes(AttachmentInfoJson.class, meta.get());
  }

  @Test
  public void testFindDistributionEntryMeta() {
    Optional<StructureMeta> meta = structureMetaDao.findCompleteByType("DistributionEntry", 1, Collections.emptyMap());
    assertTrue(meta.isPresent());
    assertStructureAttributes(DistributionEntryJson.class, meta.get());
  }

  @Test
  public void testFindCommentMeta() {
    Optional<StructureMeta> meta = structureMetaDao.findCompleteByType("Comment", 1, Collections.emptyMap());
    assertTrue(meta.isPresent());
    assertStructureAttributes(CommentJson.class, meta.get());
  }

  @Test
  public void testApplicationTypeEnumMeta() {
    Optional<StructureMeta> meta = structureMetaDao.findCompleteByType("ApplicationType", 1, Collections.emptyMap());
    assertTrue(meta.isPresent());
    assertEnumAttributes(ApplicationType.class, meta.get());
  }

  @Test
  public void testApplicationKindEnumMeta() {
    Optional<StructureMeta> meta = structureMetaDao.findCompleteByType("ApplicationKind", 1, Collections.emptyMap());
    assertTrue(meta.isPresent());
    assertEnumAttributes(ApplicationKind.class, meta.get());
  }

  @Test
  public void testApplicationSpecifierEnumMeta() {
    Optional<StructureMeta> meta = structureMetaDao.findCompleteByType("ApplicationSpecifier", 1, Collections.emptyMap());
    assertTrue(meta.isPresent());
    assertEnumAttributes(ApplicationSpecifier.class, meta.get());
  }

  @Test
  public void testApplicationTagEnumMeta() {
    Optional<StructureMeta> meta = structureMetaDao.findCompleteByType("ApplicationTagType", 1, Collections.emptyMap());
    assertTrue(meta.isPresent());
    assertEnumAttributes(ApplicationTagType.class, meta.get());
  }

  @Test
  public void testAttachmentTypeEnumMeta() {
    Optional<StructureMeta> meta = structureMetaDao.findCompleteByType("AttachmentType", 1, Collections.emptyMap());
    assertTrue(meta.isPresent());
    assertEnumAttributes(AttachmentType.class, meta.get());
  }

  @Test
  public void testCableInfoTypeEnumMeta() {
    Optional<StructureMeta> meta = structureMetaDao.findCompleteByType("CableInfoType", 1, Collections.emptyMap());
    assertTrue(meta.isPresent());
    assertEnumAttributes(DefaultTextType.class, meta.get());
  }

  @Test
  public void testChangeTypeEnumMeta() {
    Optional<StructureMeta> meta = structureMetaDao.findCompleteByType("ChangeType", 1, Collections.emptyMap());
    assertTrue(meta.isPresent());
    assertEnumAttributes(ChangeType.class, meta.get());
  }

  @Test
  public void testCommentTypeEnumMeta() {
    Optional<StructureMeta> meta = structureMetaDao.findCompleteByType("CommentType", 1, Collections.emptyMap());
    assertTrue(meta.isPresent());
    assertEnumAttributes(CommentType.class, meta.get());
  }

  @Test
  public void testEventNatureEnumMeta() {
    Optional<StructureMeta> meta = structureMetaDao.findCompleteByType("EventNature", 1, Collections.emptyMap());
    assertTrue(meta.isPresent());
    assertEnumAttributes(EventNature.class, meta.get());
  }

  @Test
  public void testRoleTypeEnumMeta() {
    Optional<StructureMeta> meta = structureMetaDao.findCompleteByType("RoleType", 1, Collections.emptyMap());
    assertTrue(meta.isPresent());
    assertEnumAttributes(RoleType.class, meta.get());
  }

  @Test
  public void testStatusTypeEnumMeta() {
    Optional<StructureMeta> meta = structureMetaDao.findCompleteByType("StatusType", 1, Collections.emptyMap());
    assertTrue(meta.isPresent());
    assertEnumAttributes(StatusType.class, meta.get());
  }

  @Test
  public void testDistributionTypeEnumMeta() {
    Optional<StructureMeta> meta = structureMetaDao.findCompleteByType("DistributionType", 1, Collections.emptyMap());
    assertTrue(meta.isPresent());
    assertEnumAttributes(DistributionType.class, meta.get());
  }

  @Test
  public void testPublicityTypeEnumMeta() {
    Optional<StructureMeta> meta = structureMetaDao.findCompleteByType("PublicityType", 1, Collections.emptyMap());
    assertTrue(meta.isPresent());
    assertEnumAttributes(PublicityType.class, meta.get());
  }

  @Test
  public void testTrafficArrangementImpedimentTypeEnumMeta() {
    Optional<StructureMeta> meta = structureMetaDao.findCompleteByType("TrafficArrangementImpedimentType", 1,
        Collections.emptyMap());
    assertTrue(meta.isPresent());
    assertEnumAttributes(TrafficArrangementImpedimentType.class, meta.get());
  }

  @Test
  public void testSupervisionTaskTypeEnumMeta() {
    Optional<StructureMeta> meta = structureMetaDao.findCompleteByType("SupervisionTaskType", 1,
        Collections.emptyMap());
    assertTrue(meta.isPresent());
    assertEnumAttributes(SupervisionTaskType.class, meta.get());
  }

  @Test
  public void testSupervisionTaskStatusTypeEnumMeta() {
    Optional<StructureMeta> meta = structureMetaDao.findCompleteByType("SupervisionTaskStatusType", 1,
        Collections.emptyMap());
    assertTrue(meta.isPresent());
    assertEnumAttributes(SupervisionTaskStatusType.class, meta.get());
  }

  private void assertStructureAttributes(Class extensionJsonClass, StructureMeta sMeta) {
    List<String> jsonAttributes =
        Arrays.stream(extensionJsonClass.getDeclaredFields()).map(df -> df.getName()).map(name -> "/" + name).collect(Collectors.toList());
    HashSet<String> metaAttributes = sMeta.getAttributes().stream().map(a -> attributeName(a))
        .collect(Collectors.toCollection(HashSet::new));
    jsonAttributes.forEach(a -> assertTrue("metadata is missing: " + a, metaAttributes.contains(a)));
  }

  private void assertEnumAttributes(Class extensionJsonClass, StructureMeta sMeta) {
    List<String> jsonAttributes =
        Arrays.stream(extensionJsonClass.getEnumConstants()).map(name -> "/" + name).collect(Collectors.toList());
    HashSet<String> metaAttributes = sMeta.getAttributes().stream().map(a -> attributeName(a))
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
