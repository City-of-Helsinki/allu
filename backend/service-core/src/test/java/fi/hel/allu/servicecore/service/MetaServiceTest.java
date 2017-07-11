package fi.hel.allu.servicecore.service;

import fi.hel.allu.common.domain.types.ApplicationKind;
import fi.hel.allu.common.domain.types.ApplicationType;
import fi.hel.allu.model.domain.meta.StructureMeta;
import fi.hel.allu.servicecore.domain.StructureMetaJson;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;
import org.springframework.http.ResponseEntity;

@RunWith(MockitoJUnitRunner.class)
public class MetaServiceTest extends MockServices {

  private MetaService metaService;

  @BeforeClass
  public static void setUpBeforeClass() {
  }

  @Before
  public void setUp() {
    metaService = new MetaService(props, restTemplate);
    Mockito
        .when(restTemplate.getForEntity(Mockito.any(String.class), Mockito.eq(StructureMeta.class),
            Mockito.any(ApplicationKind.class)))
        .thenAnswer((Answer<ResponseEntity<StructureMeta>>) invocation -> createMockStructureMetaResponse());
    Mockito
        .when(restTemplate.getForEntity(Mockito.any(String.class), Mockito.eq(StructureMeta.class),
            Mockito.any(ApplicationKind.class), Mockito.anyInt()))
        .thenAnswer((Answer<ResponseEntity<StructureMeta>>) invocation -> createMockStructureMetaResponse());

    initSearchMocks();
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
