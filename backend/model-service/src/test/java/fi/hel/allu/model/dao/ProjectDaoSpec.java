package fi.hel.allu.model.dao;

import java.util.List;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.web.WebAppConfiguration;

import com.greghaskins.spectrum.Spectrum;

import fi.hel.allu.model.ModelApplication;
import fi.hel.allu.model.domain.Project;
import fi.hel.allu.model.testUtils.SpeccyTestBase;

import static com.greghaskins.spectrum.dsl.specification.Specification.*;
import static org.junit.Assert.*;

@RunWith(Spectrum.class)
@SpringBootTest(classes = ModelApplication.class)
@WebAppConfiguration
public class ProjectDaoSpec extends SpeccyTestBase {
  private static int projectNbr = 0;

  @Autowired
  private ProjectDao projectDao;

  {
    describe("ProjectDao.findAll", () -> {
      beforeEach(() -> {
        for (int i = 0; i < 15; ++i) {
          Project p = projectDao.insert(dummyProject(i));
          assertNotNull(p.getId());
        }
      });

      it("Can fetch 5 projects in ascendind ID order", () -> {
        Page<Project> page = projectDao.findAll(PageRequest.of(1, 5));
        assertEquals(5, page.getSize());
        List<Project> elements = page.getContent();
        assertEquals(5, elements.size());
        int prevId = Integer.MIN_VALUE;
        for (int i = 0; i < elements.size(); ++i) {
          int id = elements.get(i).getId();
          assertTrue(prevId < id);
          prevId = id;
        }
      });
    });
  }

  private Project dummyProject(int i) {
    Project project = new Project();
    project.setName("Project " + i);
    project.setAdditionalInfo("AdditionalInfo " + i);
    project.setCustomerId(testCommon.insertPerson().getId());
    project.setContactId(testCommon.insertContact(project.getCustomerId()).getId());
    project.setIdentifier("DaoSpecProject" + (projectNbr++));
    project.setCreatorId(testCommon.insertUser(RandomStringUtils.randomAlphabetic(12)).getId());
    return project;
  }

}
