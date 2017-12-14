package fi.hel.allu.search;

import fi.hel.allu.common.domain.types.CustomerRoleType;
import fi.hel.allu.search.config.ElasticSearchMappingConfig;
import fi.hel.allu.search.domain.ApplicationES;
import fi.hel.allu.search.domain.CustomerES;
import fi.hel.allu.search.domain.QueryParameters;
import fi.hel.allu.search.domain.RoleTypedCustomerES;
import fi.hel.allu.search.service.ApplicationIndexConductor;
import fi.hel.allu.search.service.ApplicationSearchService;
import fi.hel.allu.search.service.CustomerIndexConductor;
import fi.hel.allu.search.service.CustomerSearchService;
import fi.hel.allu.search.util.CustomersIndexUtil;

import org.elasticsearch.client.Client;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;
import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = AppTestConfig.class)
public class CustomerSearchTest {

  private static final String TEST_NAME = "foo name";

  @Autowired
  private Client client;

  private CustomerSearchService customerSearchService;
  private ApplicationSearchService applicationSearchService;


  @Before
  public void setUp() throws Exception {
    ElasticSearchMappingConfig elasticSearchMappingConfig = SearchTestUtil.searchIndexSetup(client);
    customerSearchService = new CustomerSearchService(
        elasticSearchMappingConfig,
        client,
        new CustomerIndexConductor());
    applicationSearchService = new ApplicationSearchService(
        elasticSearchMappingConfig,
        client,
        new ApplicationIndexConductor());
  }

  @Test
  public void testInsertApplication() {
    CustomerES customerES = createCustomer(TEST_NAME, 1);
    customerSearchService.insert(customerES.getId().toString(), customerES);
  }

  @Test
  public void testFindByField() {
    CustomerES customerES = createCustomer(TEST_NAME, 1);
    customerSearchService.insert(customerES.getId().toString(), customerES);
    QueryParameters params = SearchTestUtil.createQueryParameters("name", TEST_NAME);
    customerSearchService.refreshIndex();
    List<Integer> appList = customerSearchService.findByField(params);
    assertNotNull(appList);
    assertEquals(1, appList.size());
    assertEquals(1, (int) appList.get(0));
    customerSearchService.delete(customerES.getId().toString());
  }

  @Test
  public void testFindById() {
    CustomerES customerES = createCustomer(TEST_NAME, 1);
    customerSearchService.insert(customerES.getId().toString(), customerES);
    customerSearchService.refreshIndex();
    Optional<CustomerES> insertedCustomerES = customerSearchService.findObjectById("1", CustomerES.class);
    assertTrue(insertedCustomerES.isPresent());
    assertEquals(customerES.getName(), insertedCustomerES.get().getName());
    assertEquals(customerES.getRegistryKey(), insertedCustomerES.get().getRegistryKey());
    customerSearchService.delete(customerES.getId().toString());
  }

  @Test
  public void testFindByFieldSorted() {
    CustomerES customerES1 = createCustomer("Zyzzy baabeli", 1);
    CustomerES customerES2 = createCustomer("baabeli aapeli", 2);
    CustomerES customerES3 = createCustomer("aapeli baabeli", 3);
    CustomerES customerES4 = createCustomer("ei löydy", 4);
    customerSearchService.insert(customerES1.getId().toString(), customerES1);
    customerSearchService.insert(customerES2.getId().toString(), customerES2);
    customerSearchService.insert(customerES3.getId().toString(), customerES3);
    customerSearchService.insert(customerES4.getId().toString(), customerES4);

    customerSearchService.refreshIndex();

    QueryParameters params = SearchTestUtil.createQueryParameters("name", "baabeli");
    params.setSort(new QueryParameters.Sort("name.alphasort", QueryParameters.Sort.Direction.ASC));

    List<Integer> appList = customerSearchService.findByField(params);
    assertEquals(3, appList.size());
    assertEquals(Arrays.asList(3, 2, 1), appList);
  }

  @Test
  public void testFindByRegistryKey() {
    CustomerES customerES1 = createCustomer("1", 1);
    customerES1.setRegistryKey("9444-9231");
    CustomerES customerES2 = createCustomer("2", 2);
    customerES2.setRegistryKey("9233-2311");
    CustomerES customerES3 = createCustomer("3", 3);
    customerES3.setRegistryKey("9222-5551");
    customerSearchService.insert(customerES1.getId().toString(), customerES1);
    customerSearchService.insert(customerES2.getId().toString(), customerES2);
    customerSearchService.insert(customerES3.getId().toString(), customerES3);

    customerSearchService.refreshIndex();

    // test finding partial and sorting alphabetically: 9
    QueryParameters params = SearchTestUtil.createQueryParameters("registryKey", "9");
    params.setSort(new QueryParameters.Sort("registryKey.alphasort", QueryParameters.Sort.Direction.ASC));

    List<Integer> appList = customerSearchService.findByField(params);
    assertEquals(3, appList.size());
    assertEquals(Arrays.asList(3, 2, 1), appList);

    // test searching only from beginning of word (dash is part of word)
    params = SearchTestUtil.createQueryParameters("registryKey", "9222");
    params.setSort(new QueryParameters.Sort("registryKey.alphasort", QueryParameters.Sort.Direction.ASC));

    appList = customerSearchService.findByField(params);
    assertEquals(1, appList.size());
    assertEquals(Arrays.asList(3), appList);

    // test full string search
    params = SearchTestUtil.createQueryParameters("registryKey", "9444-9231");
    params.setSort(new QueryParameters.Sort("registryKey.alphasort", QueryParameters.Sort.Direction.ASC));

    appList = customerSearchService.findByField(params);
    assertEquals(1, appList.size());
    assertEquals(Arrays.asList(1), appList);

    // test searching beginning of word after dash
    params = SearchTestUtil.createQueryParameters("registryKey", "23");
    params.setSort(new QueryParameters.Sort("registryKey.alphasort", QueryParameters.Sort.Direction.ASC));

    appList = customerSearchService.findByField(params);
    assertEquals(1, appList.size());
  }

  @Test
  public void testUpdateApplicationCustomerPartially() throws IOException {
    ApplicationES applicationES = ApplicationSearchTest.createApplication(100);
    CustomerES customerES = createCustomer(TEST_NAME, 123);
    RoleTypedCustomerES roleTypedCustomerES =
        new RoleTypedCustomerES(Collections.singletonMap(CustomerRoleType.APPLICANT, SearchTestUtil.createCustomerWithContacts(customerES)));
    applicationES.setCustomers(roleTypedCustomerES);

    applicationSearchService.insert(applicationES.getId().toString(), applicationES);

    final String updatedName = "updated name";
    final String updatedKey = "updated key";
    customerES.setName(updatedName);
    customerES.setRegistryKey(updatedKey);
    applicationES.getCustomers().getApplicant().setCustomer(customerES);
    applicationSearchService.refreshIndex();

    QueryParameters params;
    List<Integer> appList;

    // should find by application name
    params = SearchTestUtil.createQueryParameters("name", applicationES.getName());
    appList = applicationSearchService.findByField(params);
    assertEquals(1, appList.size());

    // should find by inserted name
    params = SearchTestUtil.createQueryParameters("customers.applicant.customer.name", TEST_NAME);
    appList = applicationSearchService.findByField(params);
    assertEquals(1, appList.size());

    Map customersMap = CustomersIndexUtil.getCustomerUpdateStructure(Collections.singletonList(CustomerRoleType.APPLICANT), customerES);
    applicationSearchService.bulkUpdate(Collections.singletonMap(applicationES.getId().toString(), customersMap));
    applicationSearchService.refreshIndex();

    // should find by name
    params = SearchTestUtil.createQueryParameters("customers.applicant.customer.name", updatedName);
    appList = applicationSearchService.findByField(params);
    assertEquals(1, appList.size());

    // should find by updated registry key
    params = SearchTestUtil.createQueryParameters("customers.applicant.customer.registryKey", updatedKey);
    appList = applicationSearchService.findByField(params);
    assertEquals(1, appList.size());

    // should still find by application name
    params = SearchTestUtil.createQueryParameters("name", applicationES.getName());
    appList = applicationSearchService.findByField(params);
    assertEquals(1, appList.size());

    applicationSearchService.delete("100");
  }

  @Test
  public void testBulkUpdateApplicationCustomerPartially() throws IOException {
    ApplicationES applicationES1 = ApplicationSearchTest.createApplication(100);
    CustomerES customerES1 = createCustomer(TEST_NAME, 123);
    RoleTypedCustomerES roleTypedCustomerES =
        new RoleTypedCustomerES(Collections.singletonMap(CustomerRoleType.APPLICANT, SearchTestUtil.createCustomerWithContacts(customerES1)));
    applicationES1.setCustomers(roleTypedCustomerES);
    applicationSearchService.insert(applicationES1.getId().toString(), applicationES1);

    ApplicationES applicationES2 = ApplicationSearchTest.createApplication(101);
    CustomerES customerES2 = createCustomer("second customer", 321);
    roleTypedCustomerES =
        new RoleTypedCustomerES(Collections.singletonMap(CustomerRoleType.PROPERTY_DEVELOPER, SearchTestUtil.createCustomerWithContacts(customerES2)));
    applicationES2.setCustomers(roleTypedCustomerES);
    applicationSearchService.insert(applicationES2.getId().toString(), applicationES2);

    final String updatedName1 = "updated name1";
    final String updatedKey1 = "updated key1";
    final String updatedName2 = "updated name2";
    final String updatedKey2 = "updated key2";
    customerES1.setName(updatedName1);
    customerES1.setRegistryKey(updatedKey1);
    applicationES1.getCustomers().getApplicant().setCustomer(customerES1);
    customerES2.setName(updatedName2);
    customerES2.setRegistryKey(updatedKey2);
    applicationES2.getCustomers().getPropertyDeveloper().setCustomer(customerES2);

    HashMap<String, Object> idToUpdateData = new HashMap<>();
    idToUpdateData.put(
        applicationES1.getId().toString(),
        CustomersIndexUtil.getCustomerUpdateStructure(Collections.singletonList(CustomerRoleType.APPLICANT), customerES1));
    idToUpdateData.put(
        applicationES2.getId().toString(),
        CustomersIndexUtil.getCustomerUpdateStructure(Collections.singletonList(CustomerRoleType.PROPERTY_DEVELOPER), customerES2));
    applicationSearchService.bulkUpdate(idToUpdateData);
    applicationSearchService.refreshIndex();

    // should find by updated name
    QueryParameters params = SearchTestUtil.createQueryParameters("customers.applicant.customer.name", updatedName1);
    List<Integer> appList = applicationSearchService.findByField(params);
    assertEquals(1, appList.size());
    params = SearchTestUtil.createQueryParameters("customers.propertyDeveloper.customer.name", updatedName2);
    appList = applicationSearchService.findByField(params);
    assertEquals(1, appList.size());

    // should find by updated registry key
    params = SearchTestUtil.createQueryParameters("customers.applicant.customer.registryKey", updatedKey1);
    appList = applicationSearchService.findByField(params);
    assertEquals(1, appList.size());
    params = SearchTestUtil.createQueryParameters("customers.propertyDeveloper.customer.registryKey", updatedKey2);
    appList = applicationSearchService.findByField(params);
    assertEquals(1, appList.size());

    applicationSearchService.delete("100");
  }

  private CustomerES createCustomer(String name, int id) {
    CustomerES customerES = new CustomerES();
    customerES.setId(id);
    customerES.setName(name);
    customerES.setRegistryKey("bar key");
    return customerES;
  }
}
