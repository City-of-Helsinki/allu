package fi.hel.allu.servicecore.service;

import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;
import org.springframework.http.ResponseEntity;

import fi.hel.allu.common.domain.types.ApplicationType;
import fi.hel.allu.model.domain.meta.StructureMeta;
import fi.hel.allu.servicecore.domain.StructureMetaJson;

@RunWith(MockitoJUnitRunner.class)
public class MetaServiceTest extends MockServices {

  private MetaService metaService;

  @BeforeClass
  public static void setUpBeforeClass() {
  }

  @Before
  public void setUp() {
    metaService = new MetaService(TestProperties.getProperties(), restTemplate);
    Mockito
        .when(restTemplate.postForEntity(
            Mockito.anyString(),
            Mockito.anyMap(),
            Mockito.eq(StructureMeta.class),
            Mockito.anyString()))
        .thenAnswer((Answer<ResponseEntity<StructureMeta>>) invocation -> createMockStructureMetaResponse());
    Mockito
        .when(restTemplate.postForEntity(
            Mockito.anyString(),
            Mockito.anyMap(),
            Mockito.eq(StructureMeta.class),
            Mockito.anyString(),
            Mockito.anyInt()))
        .thenAnswer((Answer<ResponseEntity<StructureMeta>>) invocation -> createMockStructureMetaResponse());

  }

  @Test
  public void testReadMetadata() {
    StructureMetaJson structureMetaJson = metaService.findMetadataForApplication(ApplicationType.EVENT);
    Assert.assertEquals(1, structureMetaJson.getVersion());
    Assert.assertEquals(1, structureMetaJson.getAttributes().size());
    Assert.assertEquals("test_attribute", structureMetaJson.getAttributes().get(0).getName());
  }

  @Test
  public void testReadMetadataWithVersion() {
    StructureMetaJson structureMetaJson = metaService.findMetadataForApplication(ApplicationType.EVENT, 1);
    Assert.assertEquals(1, structureMetaJson.getVersion());
    Assert.assertEquals(1, structureMetaJson.getAttributes().size());
    Assert.assertEquals("test_attribute", structureMetaJson.getAttributes().get(0).getName());
  }
}
