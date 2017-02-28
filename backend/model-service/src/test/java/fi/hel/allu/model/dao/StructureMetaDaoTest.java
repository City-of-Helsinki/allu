package fi.hel.allu.model.dao;

import fi.hel.allu.model.ModelApplication;
import fi.hel.allu.model.domain.meta.StructureMeta;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = ModelApplication.class)
@WebAppConfiguration
@Transactional
public class StructureMetaDaoTest {
  @Autowired
  private StructureMetaDao structureMetaDao;

  @Test
  public void testfindCompleteByApplicationType() {

    String APPLICATION_TYPE = "Application";
    Map<String, String> typeOverrides = new HashMap<>();
    typeOverrides.put("/extension", "EVENT");

    Optional<StructureMeta> structureMeta = structureMetaDao.findCompleteByApplicationType(APPLICATION_TYPE,
        typeOverrides);
    assertTrue(structureMeta.isPresent());
    // Check that there's one event-specific attribute in the metadata
    assertEquals(1,
        structureMeta.get().getAttributes().stream().filter(a -> a.getName().equals("/extension/heavyStructure"))
            .count());
  }

}
