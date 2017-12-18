package fi.hel.allu.search;

import fi.hel.allu.search.config.ElasticSearchMappingConfig;
import fi.hel.allu.search.domain.ApplicationES;
import fi.hel.allu.search.domain.ProjectES;
import fi.hel.allu.search.domain.QueryParameters;
import fi.hel.allu.search.service.ApplicationIndexConductor;
import fi.hel.allu.search.service.ApplicationSearchService;
import fi.hel.allu.search.service.ProjectSearchService;

import org.elasticsearch.client.Client;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.time.ZonedDateTime;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = AppTestConfig.class)
public class ProjectSearchTest {

  @Autowired
  private Client client;
  private ProjectSearchService projectSearchService;
  private ApplicationSearchService applicationSearchService;

  private static final String projectName = "testiname";

  @Before
  public void setUp() throws Exception {
    ElasticSearchMappingConfig elasticSearchMappingConfig = SearchTestUtil.searchIndexSetup(client);
    ApplicationIndexConductor conductor = new ApplicationIndexConductor();
    projectSearchService = new ProjectSearchService(
        elasticSearchMappingConfig,
        client,
        conductor);
    applicationSearchService = new ApplicationSearchService(
        elasticSearchMappingConfig,
        client,
        conductor);
  }

  @Test
  public void testInsertProject() {
    ProjectES projectES = createProject(1, 2);
    projectSearchService.insert(projectES);
  }

  @Test
  public void testInsertApplicationAndProject() {
    ProjectES projectES = createProject(1, 2);
    ApplicationES applicationES = ApplicationSearchTest.createApplication(123);
    // insert both project and application to catch possible property type mismatches: project first and then application
    projectSearchService.insert(projectES);
    applicationSearchService.insert(applicationES);
  }

  @Test
  public void testInsertProjectAndApplication() {
    ProjectES projectES = createProject(1, 2);
    ApplicationES applicationES = ApplicationSearchTest.createApplication(123);
    // insert both project and application to catch possible property type mismatches: : application first and then project
    applicationSearchService.insert(applicationES);
    projectSearchService.insert(projectES);
  }

  @Test
  public void testFindByField() {
    ProjectES projectES = createProject(1, 2);
    projectSearchService.insert(projectES);
    QueryParameters params = SearchTestUtil.createQueryParameters("name", projectName);
    projectSearchService.refreshIndex();
    List<Integer> appList = projectSearchService.findByField(params);
    assertNotNull(appList);
    assertEquals(1, appList.size());
  }


  private ProjectES createProject(int projectId, int projectParentId) {
    ProjectES projectES = new ProjectES();
    projectES.setId(projectId);
    projectES.setParentId(projectParentId);
    projectES.setName(projectName);
    projectES.setOwnerName("owner");
    projectES.setAdditionalInfo("Additional info");
    projectES.setCustomerReference("Customer reference");
    projectES.setPhone("03012345");
    projectES.setContactName("Concact name");
    projectES.setEmail("email@email.fi");
    projectES.setStartTime(ZonedDateTime.parse("2016-07-05T06:23:04.000Z").toInstant().toEpochMilli());
    projectES.setEndTime(ZonedDateTime.parse("2016-07-06T06:23:04.000Z").toInstant().toEpochMilli());
    return projectES;
  }
}
