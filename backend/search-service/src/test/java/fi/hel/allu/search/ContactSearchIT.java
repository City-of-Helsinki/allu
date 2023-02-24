package fi.hel.allu.search;

import fi.hel.allu.common.domain.types.CustomerRoleType;
import fi.hel.allu.search.config.ElasticSearchMappingConfig;
import fi.hel.allu.search.domain.*;
import fi.hel.allu.search.indexConductor.ApplicationIndexConductor;
import fi.hel.allu.search.service.ApplicationSearchService;
import fi.hel.allu.search.indexConductor.ContactIndexConductor;
import fi.hel.allu.search.service.ContactSearchService;
import fi.hel.allu.search.util.CustomersIndexUtil;
import org.apache.http.HttpHost;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.IOException;
import java.util.*;

import static fi.hel.allu.search.util.Constants.CONTACT_INDEX_ALIAS;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests of this class share same container because it saves time on testing.
 * Might change after integration and unit tests are separeted
 */
@ExtendWith(SpringExtension.class)
public class ContactSearchIT extends BaseIntegrationTest {

    private RestHighLevelClient client;

    private ContactSearchService contactSearchService;

    private ElasticSearchMappingConfig elasticSearchMappingConfig;

    @BeforeEach
    void setUp() {
        client = new RestHighLevelClient(RestClient.builder(HttpHost.create(container.getHttpHostAddress())));
        elasticSearchMappingConfig = SearchTestUtil.searchIndexSetup(client);
        contactSearchService = new ContactSearchService(elasticSearchMappingConfig, client,
                                                        new ContactIndexConductor());
    }

    @Test
    void testInsertContact() throws IOException {
        Integer id = 42;
        ContactES contactES = createContact(id, "goku");
        contactSearchService.insert(contactES);
        contactSearchService.refreshIndex();
        GetRequest getRequest = new GetRequest(CONTACT_INDEX_ALIAS, "_doc", id.toString());
        GetResponse getResponse = client.get(getRequest, RequestOptions.DEFAULT);
        assertTrue(getResponse.isExists());
        contactSearchService.delete(id.toString());
    }

    @ParameterizedTest
    @ValueSource(strings = {"Schwarzenegger", "Schwarzen"})
    void testFindByFieldPartial(String queryParameter) {
        Integer contactId = 42;
        ContactES contactES = createContact(contactId, "Schwarzenegger");
        contactSearchService.insert(contactES);

        verifyResult("name", queryParameter, contactId, 1);

        contactSearchService.delete(contactId.toString());
    }

    @Test
    void testNotFindByFieldPartial() {
        ApplicationQueryParameters params = SearchTestUtil.createApplicationQueryParameters("name",
                                                                                            "definetlyNotRightParameter");
        contactSearchService.refreshIndex();
        List<Integer> appList = contactSearchService.findByField(params, null).getContent();
        assertNotNull(appList);
        assertEquals(0, appList.size());
    }

    @Test
    void testFindMultipleResult() {
        ContactES contact1 = createContact(1, "alpha one searchstr");
        ContactES contact2 = createContact(2, "beta two searchstr");
        ContactES contact3 = createContact(3, "gamma three searchstr");
        contactSearchService.insert(contact1);
        contactSearchService.insert(contact2);
        contactSearchService.insert(contact3);
        contactSearchService.refreshIndex();
        QueryParameters params = SearchTestUtil.createQueryParameters("name", "searchstr");
        List<Integer> contacts = contactSearchService.findByField(params, null).getContent();
        assertEquals(3, contacts.size());
        contactSearchService.delete("1");
        contactSearchService.delete("2");
        contactSearchService.delete("3");
    }

    @Test
    void testAlphabeticalSort() {
        ContactES contact1 = createContact(1, "beta two searchstr");
        ContactES contact2 = createContact(2, "alpha one searchstr");
        ContactES contact3 = createContact(3, "gamma three searchstr");
        contactSearchService.insert(contact1);
        contactSearchService.insert(contact2);
        contactSearchService.insert(contact3);
        contactSearchService.refreshIndex();
        QueryParameters params = SearchTestUtil.createQueryParameters("name", "searchstr");
        List<Integer> contacts = contactSearchService.findByField(params,
                                                                  PageRequest.of(0, 100, Sort.Direction.ASC, "name"))
                .getContent();
        assertEquals(Arrays.asList(2, 1, 3), contacts);
        contactSearchService.delete("1");
        contactSearchService.delete("2");
        contactSearchService.delete("3");
    }

    @Test
    void testBulkInsert() {
        List<ContactES> contacts = new ArrayList<>();
        contacts.add(createContact(1, "beta two searchstr"));
        contacts.add(createContact(2, "alpha one searchstr"));
        contacts.add(createContact(3, "gamma three searchstr"));
        contactSearchService.bulkInsert(contacts);
        contactSearchService.refreshIndex();
        QueryParameters params = SearchTestUtil.createQueryParameters("name", "searchstr");
        List<Integer> actualContacts = contactSearchService.findByField(params, null).getContent();
        assertEquals(3, actualContacts.size());
        contactSearchService.delete("1");
        contactSearchService.delete("2");
        contactSearchService.delete("3");
    }

    @Test
    void testFindByQuery() {
        ApplicationSearchService applicationSearchService = new ApplicationSearchService(elasticSearchMappingConfig,
                                                                                         client,
                                                                                         new ApplicationIndexConductor());
        List<ContactES> contacts = ApplicationSearchIT.createContacts();
        ApplicationES applicationES = ApplicationSearchIT.createApplication(1);

        CustomerES customerES = new CustomerES();
        RoleTypedCustomerES roleTypedCustomerES = new RoleTypedCustomerES(
                Collections.singletonMap(CustomerRoleType.APPLICANT,
                                         SearchTestUtil.createCustomerWithContacts(customerES, contacts)));
        applicationES.setCustomers(roleTypedCustomerES);
        applicationSearchService.insert(applicationES);
        applicationSearchService.refreshIndex();
        ApplicationQueryParameters params = SearchTestUtil.createApplicationQueryParameters(
                "customers.applicant.contacts.name", "kontakti");
        List<Integer> appList = applicationSearchService.findByField(params, null).getContent();
        assertNotNull(appList);
        assertEquals(1, appList.size());
    }

    @Test
    void testUpdate() {
        ApplicationSearchService applicationSearchService = new ApplicationSearchService(elasticSearchMappingConfig,
                                                                                         client,
                                                                                         new ApplicationIndexConductor());
        List<ContactES> contacts = ApplicationSearchIT.createContacts();
        ApplicationES applicationES = ApplicationSearchIT.createApplication(1);

        CustomerES customerES = new CustomerES();
        RoleTypedCustomerES roleTypedCustomerES = new RoleTypedCustomerES(
                Collections.singletonMap(CustomerRoleType.APPLICANT,
                                         SearchTestUtil.createCustomerWithContacts(customerES, contacts)));
        applicationES.setCustomers(roleTypedCustomerES);
        applicationSearchService.insert(applicationES);
        applicationSearchService.refreshIndex();

        contacts.get(0).setName("updated 1");

        ApplicationWithContactsES applicationWithContactsES1 = new ApplicationWithContactsES(applicationES.getId(),
                                                                                             CustomerRoleType.APPLICANT,
                                                                                             contacts);
        ApplicationWithContactsES applicationWithContactsES2 = new ApplicationWithContactsES(applicationES.getId(),
                                                                                             CustomerRoleType.CONTRACTOR,
                                                                                             Collections.singletonList(
                                                                                                     new ContactES(100,
                                                                                                                   "kontraktori",
                                                                                                                   true)));
        Map contactsMap = CustomersIndexUtil.getContactsUpdateStructure(
                Arrays.asList(applicationWithContactsES1, applicationWithContactsES2));
        applicationSearchService.partialUpdate(contactsMap, false);
        applicationSearchService.refreshIndex();

        ApplicationQueryParameters appParams = SearchTestUtil.createApplicationQueryParameters(
                "customers.applicant.contacts.name", "updated 1");
        List<Integer> appList = applicationSearchService.findByField(appParams, null).getContent();
        assertNotNull(appList);
        assertEquals(1, appList.size());
    }

    private void verifyResult(String fieldName, String parameter, Integer id, int size) {
        ApplicationQueryParameters params = SearchTestUtil.createApplicationQueryParameters(fieldName, parameter);
        contactSearchService.refreshIndex();
        List<Integer> appList = contactSearchService.findByField(params, null).getContent();
        assertNotNull(appList);
        assertEquals(size, appList.size());
        assertEquals(appList.get(0), id);
    }

    private ContactES createContact(int id, String name) {
        ContactES contactES = new ContactES();
        contactES.setId(id);
        contactES.setName(name);
        contactES.setActive(true);
        return contactES;
    }
}