package fi.hel.allu.search;

import com.greghaskins.spectrum.Spectrum;
import fi.hel.allu.common.types.CustomerRoleType;
import fi.hel.allu.search.config.ElasticSearchMappingConfig;
import fi.hel.allu.search.domain.*;
import fi.hel.allu.search.service.GenericSearchService;
import fi.hel.allu.search.util.CustomersIndexUtil;
import org.elasticsearch.client.Client;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestContextManager;

import java.util.*;

import static com.greghaskins.spectrum.Spectrum.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(Spectrum.class)
@ContextConfiguration(classes = AppTestConfig.class)
public class ContactSearchSpec {

  @Autowired
  private Client client;

  private GenericSearchService contactSearchService;
  private GenericSearchService applicationSearchService;

  private ContactES testContact = new ContactES(1, "testable name", true);

  {
    beforeAll(() -> {
      new TestContextManager(getClass()).prepareTestInstance(this);
    });
    beforeEach(() -> {
      ElasticSearchMappingConfig elasticSearchMappingConfig = SearchTestUtil.searchIndexSetup(client);
      contactSearchService = new GenericSearchService(
          elasticSearchMappingConfig,
          client,
          ElasticSearchMappingConfig.CUSTOMER_INDEX_NAME,
          ElasticSearchMappingConfig.CONTACT_TYPE_NAME);
      applicationSearchService = new GenericSearchService(
          elasticSearchMappingConfig,
          client,
          ElasticSearchMappingConfig.APPLICATION_INDEX_NAME,
          ElasticSearchMappingConfig.APPLICATION_TYPE_NAME);
    });

    describe("search conctacts", () -> {
      beforeEach(() -> {
        contactSearchService.insert(Integer.toString(testContact.getId()), testContact);
        contactSearchService.refreshIndex();
      });
      it("should find contact by full name", () -> {
        QueryParameters params = SearchTestUtil.createQueryParameters("name", testContact.getName());
        List<Integer> contacts = contactSearchService.findByField(params);
        assertEquals(1, contacts.size());
        assertEquals(1, (int) contacts.get(0));
      });
      it("should find contact by partial name", () -> {
        QueryParameters params = SearchTestUtil.createQueryParameters("name", testContact.getName().substring(0, 3));
        List<Integer> contacts = contactSearchService.findByField(params);
        assertEquals(1, contacts.size());
        assertEquals(1, (int) contacts.get(0));
      });
      it("should not find contact", () -> {
        QueryParameters params = SearchTestUtil.createQueryParameters("name", "non-existent");
        List<Integer> contacts = contactSearchService.findByField(params);
        assertEquals(0, contacts.size());
      });
      afterEach(() -> {
        contactSearchService.delete(Integer.toString(testContact.getId()));
      });
    });
    describe("search and sort conctacts", () -> {
      beforeEach(() -> {
        ContactES contact1 = new ContactES(1, "alpha one searchstr", true);
        ContactES contact2 = new ContactES(2, "beta two searchstr", true);
        ContactES contact3 = new ContactES(3, "gamma three searchstr", true);
        contactSearchService.insert(Integer.toString(contact1.getId()), contact1);
        contactSearchService.insert(Integer.toString(contact2.getId()), contact2);
        contactSearchService.insert(Integer.toString(contact3.getId()), contact3);
        contactSearchService.refreshIndex();
      });
      it("should sort contact by name ASC", () -> {
        QueryParameters params = SearchTestUtil.createQueryParameters("name", "searchstr");
        params.setSort(new QueryParameters.Sort("name.alphasort", QueryParameters.Sort.Direction.ASC));
        List<Integer> contacts = contactSearchService.findByField(params);
        assertEquals(3, contacts.size());
        assertEquals(Arrays.asList(1, 2, 3), contacts);
      });
      it("should sort contact by name DESC", () -> {
        QueryParameters params = SearchTestUtil.createQueryParameters("name", "searchstr");
        params.setSort(new QueryParameters.Sort("name.alphasort", QueryParameters.Sort.Direction.DESC));
        List<Integer> contacts = contactSearchService.findByField(params);
        assertEquals(3, contacts.size());
        assertEquals(Arrays.asList(3, 2, 1), contacts);
      });
      afterEach(() -> {
        contactSearchService.delete(Integer.toString(testContact.getId()));
      });
    });
    describe("Bulk insert contacts", () -> {
      it("should bulk insert contacts", () -> {
        Map<String, Object> idToContact = new HashMap<>();
        idToContact.put("1", new ContactES(1, "alpha one searchstr", true));
        idToContact.put("2", new ContactES(2, "beta two searchstr", true));
        idToContact.put("3", new ContactES(3, "gamma three searchstr", true));
        contactSearchService.bulkInsert(idToContact);
        contactSearchService.refreshIndex();
        QueryParameters params = SearchTestUtil.createQueryParameters("name", "searchstr");
        params.setSort(new QueryParameters.Sort("name.alphasort", QueryParameters.Sort.Direction.ASC));
        List<Integer> contacts = contactSearchService.findByField(params);
        assertEquals(3, contacts.size());
        assertEquals(Arrays.asList(1, 2, 3), contacts);
      });
    });
    describe("Update contacts of applications", () -> {
      it("should partially update contacts of a customer", () -> {
        ApplicationES applicationES = ApplicationSearchTest.createApplication(1);
        CustomerES customerES = new CustomerES();
        List<ContactES> contacts = ApplicationSearchTest.createContacts();
        RoleTypedCustomerES roleTypedCustomerES =
            new RoleTypedCustomerES(Collections.singletonMap(CustomerRoleType.APPLICANT,
                SearchTestUtil.createCustomerWithContacts(customerES, contacts)));
        applicationES.setCustomers(roleTypedCustomerES);
        applicationSearchService.insert(applicationES.getId().toString(), applicationES);
        applicationSearchService.refreshIndex();

        // make sure contacts are stored in ElasticSearch as expected by this test
        QueryParameters params = SearchTestUtil.createQueryParameters("customers.applicant.contacts.name", "kontakti");
        List<Integer> appList = applicationSearchService.findByField(params);
        assertNotNull(appList);
        assertEquals(1, appList.size());

        contacts.get(0).setName("updated 1");

        ApplicationWithContactsES applicationWithContactsES1 =
            new ApplicationWithContactsES(applicationES.getId(), CustomerRoleType.APPLICANT, contacts);
        ApplicationWithContactsES applicationWithContactsES2 = new ApplicationWithContactsES(
            applicationES.getId(), CustomerRoleType.CONTRACTOR, Collections.singletonList(new ContactES(100, "kontraktori", true)));
        Map contactsMap =
            CustomersIndexUtil.getContactsUpdateStructure(Arrays.asList(applicationWithContactsES1, applicationWithContactsES2));
        applicationSearchService.bulkUpdate(contactsMap);
        applicationSearchService.refreshIndex();

        params = SearchTestUtil.createQueryParameters("customers.applicant.contacts.name", "updated 1");
        appList = applicationSearchService.findByField(params);
        assertNotNull(appList);
        assertEquals(1, appList.size());

        params = SearchTestUtil.createQueryParameters("customers.contractor.contacts.name", "kontraktori");
        appList = applicationSearchService.findByField(params);
        assertNotNull(appList);
        assertEquals(1, appList.size());

        applicationSearchService.delete("1");
      });
    });
  }
}
