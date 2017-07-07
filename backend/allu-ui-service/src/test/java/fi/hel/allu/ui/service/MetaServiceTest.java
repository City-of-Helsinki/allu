package fi.hel.allu.ui.service;

import fi.hel.allu.common.domain.types.ApplicationKind;
import fi.hel.allu.common.domain.types.ApplicationType;
import fi.hel.allu.model.domain.meta.StructureMeta;
import fi.hel.allu.ui.domain.StructureMetaJson;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;
import org.springframework.http.ResponseEntity;

import static org.junit.Assert.assertEquals;

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
    assertEquals(1, structureMetaJson.getVersion());
    assertEquals(1, structureMetaJson.getAttributes().size());
    assertEquals("test_attribute", structureMetaJson.getAttributes().get(0).getName());
  }

  @Test
  public void testReadMetadataWithVersion() {
    StructureMetaJson structureMetaJson = metaService.findMetadataForApplication(ApplicationType.EVENT, 1);
    assertEquals(1, structureMetaJson.getVersion());
    assertEquals(1, structureMetaJson.getAttributes().size());
    assertEquals("test_attribute", structureMetaJson.getAttributes().get(0).getName());
  }
}
