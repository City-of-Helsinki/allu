package fi.hel.allu.model.dao;

import com.greghaskins.spectrum.Spectrum;

import fi.hel.allu.model.ModelApplication;
import fi.hel.allu.model.domain.Contact;
import fi.hel.allu.model.domain.Project;
import fi.hel.allu.model.testUtils.SpeccyTestBase;

import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.ArrayList;
import java.util.List;

import static com.greghaskins.spectrum.dsl.specification.Specification.beforeEach;
import static com.greghaskins.spectrum.dsl.specification.Specification.describe;
import static com.greghaskins.spectrum.dsl.specification.Specification.it;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(Spectrum.class)
@SpringBootTest(classes = ModelApplication.class)
@WebAppConfiguration
public class ProjectDaoSpec extends SpeccyTestBase {
  @Autowired
  ProjectDao projectDao;

  {
    describe("ProjectDao.findAll", () -> {
      beforeEach(() -> {
        List<Contact> contacts = new ArrayList<>();
        for (int i = 0; i < 15; ++i) {
          Project p = projectDao.insert(dummyProject(i));
          assertNotNull(p.getId());
        }
      });

      it("Can fetch 5 projects in ascendind ID order", () -> {
        Page<Project> page = projectDao.findAll(new PageRequest(1, 5));
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
    project.setOwnerName("Owner " + i);
    project.setAdditionalInfo("AdditionalInfo " + i);
    return project;
  }

}
