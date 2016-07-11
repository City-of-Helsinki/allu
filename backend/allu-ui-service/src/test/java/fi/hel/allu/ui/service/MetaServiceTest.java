package fi.hel.allu.ui.service;

import fi.hel.allu.common.types.ApplicationType;
import fi.hel.allu.ui.domain.StructureMetaJson;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

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
    initSearchMocks();
  }

  @Test
  public void testReadMetadata() {
    StructureMetaJson structureMetaJson = metaService.findMetadataForApplication(ApplicationType.OutdoorEvent);
    assertEquals(1, structureMetaJson.getVersion());
    assertEquals(1, structureMetaJson.getAttributes().size());
    assertEquals("test_attribute", structureMetaJson.getAttributes().get(0).getName());
  }
}
