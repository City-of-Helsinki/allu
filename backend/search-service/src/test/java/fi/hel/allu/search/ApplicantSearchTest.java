package fi.hel.allu.search;

import fi.hel.allu.search.config.ElasticSearchMappingConfig;
import fi.hel.allu.search.domain.ApplicantES;
import fi.hel.allu.search.domain.ApplicationES;
import fi.hel.allu.search.domain.QueryParameters;
import fi.hel.allu.search.service.GenericSearchService;
import org.elasticsearch.client.Client;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;
import java.util.*;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = AppTestConfig.class)
public class ApplicantSearchTest {

  private static final String TEST_NAME = "foo name";

  @Autowired
  private Client client;

  private GenericSearchService applicantSearchService;
  private GenericSearchService applicationSearchService;


  @Before
  public void setUp() throws Exception {
    ElasticSearchMappingConfig elasticSearchMappingConfig = SearchTestUtil.searchIndexSetup(client);
    applicantSearchService = new GenericSearchService(
        elasticSearchMappingConfig,
        client,
        ElasticSearchMappingConfig.CUSTOMER_INDEX_NAME,
        ElasticSearchMappingConfig.APPLICANT_TYPE_NAME);
    applicationSearchService = new GenericSearchService(
        elasticSearchMappingConfig,
        client,
        ElasticSearchMappingConfig.APPLICATION_INDEX_NAME,
        ElasticSearchMappingConfig.APPLICATION_TYPE_NAME);
  }

  @Test
  public void testInsertApplication() {
    ApplicantES applicantES = createApplicant(TEST_NAME, 1);
    applicantSearchService.insert(applicantES.getId().toString(), applicantES);
  }

  @Test
  public void testFindByField() {
    ApplicantES applicantES = createApplicant(TEST_NAME, 1);
    applicantSearchService.insert(applicantES.getId().toString(), applicantES);
    QueryParameters params = SearchTestUtil.createQueryParameters("name", TEST_NAME);
    applicantSearchService.refreshIndex();
    List<Integer> appList = applicantSearchService.findByField(params);
    assertNotNull(appList);
    assertEquals(1, appList.size());
    assertEquals(1, (int) appList.get(0));
    applicantSearchService.delete(applicantES.getId().toString());
  }

  @Test
  public void testFindById() {
    ApplicantES applicantES = createApplicant(TEST_NAME, 1);
    applicantSearchService.insert(applicantES.getId().toString(), applicantES);
    applicantSearchService.refreshIndex();
    Optional<ApplicantES> insertedApplicantES = applicantSearchService.findObjectById("1", ApplicantES.class);
    assertTrue(insertedApplicantES.isPresent());
    assertEquals(applicantES.getName(), insertedApplicantES.get().getName());
    assertEquals(applicantES.getRegistryKey(), insertedApplicantES.get().getRegistryKey());
    applicantSearchService.delete(applicantES.getId().toString());
  }

  @Test
  public void testFindPartialWithNonPartialSearch() {
    ApplicantES applicantES = createApplicant(TEST_NAME, 1);
    applicantSearchService.insert(applicantES.getId().toString(), applicantES);

    applicantSearchService.refreshIndex();
    List<Integer> appList = applicantSearchService.findPartial("name", TEST_NAME);
    assertNotNull(appList);
    assertEquals(1, appList.size());
    assertEquals(1, (int) appList.get(0));
    applicantSearchService.delete(Integer.toString(applicantES.getId()));
  }

  @Test
  public void testFindPartial() {
    ApplicantES applicantES1 = createApplicant("partial matching test", 1);
    ApplicantES applicantES2 = createApplicant("tset gnihctam laitrap", 2);
    applicantSearchService.insert(applicantES1.getId().toString(), applicantES1);
    applicantSearchService.insert(applicantES2.getId().toString(), applicantES2);

    applicantSearchService.refreshIndex();

    List<Integer> appList = applicantSearchService.findPartial("name", "mat");
    assertNotNull(appList);
    assertEquals(1, appList.size());
    assertEquals(1, (int) appList.get(0));

    // should find complete word
    appList = applicantSearchService.findPartial("name", "matching");
    assertNotNull(appList);
    assertEquals(1, appList.size());
    assertEquals(1, (int) appList.get(0));

    // should find, starts complete word
    appList = applicantSearchService.findPartial("name", "t");
    assertNotNull(appList);
    assertEquals(2, appList.size());
    assertTrue(appList.containsAll(Arrays.asList(1, 2)));

    // should not find, because inside word
    appList = applicantSearchService.findPartial("name", "a");
    assertNotNull(appList);
    assertEquals(0, appList.size());

    applicantSearchService.delete(Integer.toString(applicantES1.getId()));
    applicantSearchService.delete(Integer.toString(applicantES2.getId()));
  }

  @Test
  public void testFindByFieldSorted() {
    ApplicantES applicantES1 = createApplicant("zyzzy baabeli", 1);
    ApplicantES applicantES2 = createApplicant("baabeli aapeli", 2);
    ApplicantES applicantES3 = createApplicant("aapeli baabeli", 3);
    ApplicantES applicantES4 = createApplicant("ei löydy", 4);
    applicantSearchService.insert(applicantES1.getId().toString(), applicantES1);
    applicantSearchService.insert(applicantES2.getId().toString(), applicantES2);
    applicantSearchService.insert(applicantES3.getId().toString(), applicantES3);
    applicantSearchService.insert(applicantES4.getId().toString(), applicantES4);

    applicantSearchService.refreshIndex();

    QueryParameters params = SearchTestUtil.createQueryParameters("name", "baabeli");
    params.setSort(new QueryParameters.Sort("name.alphasort", QueryParameters.Sort.Direction.ASC));

    List<Integer> appList = applicantSearchService.findByField(params);
    assertEquals(3, appList.size());
    assertEquals(Arrays.asList(3, 2, 1), appList);
  }

    @Test
  public void testUpdateApplicationApplicantPartially() throws IOException {
    ApplicationES applicationES = ApplicationSearchTest.createApplication(100);
    ApplicantES applicantES = createApplicant(TEST_NAME, 123);
    applicationES.setApplicant(applicantES);
    applicationSearchService.insert(applicationES.getId().toString(), applicationES);

    final String updatedName = "updated name";
    final String updatedKey = "updated key";
    applicantES.setName(updatedName);
    applicantES.setRegistryKey(updatedKey);
    applicationES.setApplicant(applicantES);

    applicationSearchService.bulkUpdate(Collections.singletonMap(applicationES.getId().toString(), Collections.singletonMap("applicant", applicantES)));
    applicationSearchService.refreshIndex();

    // should find by updated name
    QueryParameters params = SearchTestUtil.createQueryParameters("applicant.name", updatedName);
    List<Integer> appList = applicationSearchService.findByField(params);
    assertEquals(1, appList.size());

    // should find by updated registry key
    params = SearchTestUtil.createQueryParameters("applicant.registryKey", updatedKey);
    appList = applicationSearchService.findByField(params);
    assertEquals(1, appList.size());

    applicationSearchService.delete("100");
  }

  @Test
  public void testBulkUpdateApplicationApplicantPartially() throws IOException {
    ApplicationES applicationES1 = ApplicationSearchTest.createApplication(100);
    ApplicantES applicantES1 = createApplicant(TEST_NAME, 123);
    applicationES1.setApplicant(applicantES1);
    applicationSearchService.insert(applicationES1.getId().toString(), applicationES1);

    ApplicationES applicationES2 = ApplicationSearchTest.createApplication(101);
    ApplicantES applicantES2 = createApplicant("second applicant", 321);
    applicationES2.setApplicant(applicantES2);
    applicationSearchService.insert(applicationES2.getId().toString(), applicationES2);

    final String updatedName1 = "updated name1";
    final String updatedKey1 = "updated key1";
    final String updatedName2 = "updated name2";
    final String updatedKey2 = "updated key2";
    applicantES1.setName(updatedName1);
    applicantES1.setRegistryKey(updatedKey1);
    applicationES1.setApplicant(applicantES1);
    applicantES2.setName(updatedName2);
    applicantES2.setRegistryKey(updatedKey2);
    applicationES2.setApplicant(applicantES2);

    HashMap<String, Object> idToUpdateData = new HashMap<>();
    idToUpdateData.put(applicationES1.getId().toString(), Collections.singletonMap("applicant", applicantES1));
    idToUpdateData.put(applicationES2.getId().toString(), Collections.singletonMap("applicant", applicantES2));
    applicationSearchService.bulkUpdate(idToUpdateData);
    applicationSearchService.refreshIndex();

    // should find by updated name
    QueryParameters params = SearchTestUtil.createQueryParameters("applicant.name", updatedName1);
    List<Integer> appList = applicationSearchService.findByField(params);
    assertEquals(1, appList.size());
    params = SearchTestUtil.createQueryParameters("applicant.name", updatedName2);
    appList = applicationSearchService.findByField(params);
    assertEquals(1, appList.size());

    // should find by updated registry key
    params = SearchTestUtil.createQueryParameters("applicant.registryKey", updatedKey1);
    appList = applicationSearchService.findByField(params);
    assertEquals(1, appList.size());
    params = SearchTestUtil.createQueryParameters("applicant.registryKey", updatedKey2);
    appList = applicationSearchService.findByField(params);
    assertEquals(1, appList.size());

    applicationSearchService.delete("100");
  }

  private ApplicantES createApplicant(String name, int id) {
    ApplicantES applicantES = new ApplicantES();
    applicantES.setId(id);
    applicantES.setName(name);
    applicantES.setRegistryKey("bar key");
    return applicantES;
  }
}
