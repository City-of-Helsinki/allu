package fi.hel.allu.search;

import fi.hel.allu.common.domain.types.CustomerRoleType;
import fi.hel.allu.search.config.ElasticSearchMappingConfig;
import fi.hel.allu.search.domain.*;
import fi.hel.allu.search.service.ApplicationIndexConductor;
import fi.hel.allu.search.service.ApplicationSearchService;
import fi.hel.allu.search.service.CustomerIndexConductor;
import fi.hel.allu.search.service.CustomerSearchService;
import fi.hel.allu.search.util.CustomersIndexUtil;
import org.elasticsearch.action.admin.indices.stats.IndicesStatsResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;

import static fi.hel.allu.search.util.Constants.CUSTOMER_INDEX_ALIAS;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
class CustomerSearchTest extends BaseIntegrationTest {

	private static final String TEST_NAME = "foo name";

	private CustomerSearchService customerSearchService;
	private ApplicationSearchService applicationSearchService;

	private Client client;

	@BeforeEach
	void setUp() throws UnknownHostException {
		TransportAddress transportAddress = new TransportAddress(InetAddress.getByName(container.getHost()),
																														 container.getMappedPort(9300));
		Settings settings = Settings.builder().put("cluster.name", CLUSTER_NAME).build();
		client = new PreBuiltTransportClient(settings).addTransportAddress(transportAddress);
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
	void testInsertApplication() {
		CustomerES customerES = createCustomer(TEST_NAME, 1);
		customerSearchService.insert(customerES);
		customerSearchService.refreshIndex();
		IndicesStatsResponse indicesStatsResponse = client.admin().indices().prepareStats(CUSTOMER_INDEX_ALIAS).get();
		assertEquals(1, indicesStatsResponse.getIndices().get(CUSTOMER_INDEX_ALIAS).getTotal().docs.getCount());
	}

	@Test
	void testFindByField() {
		CustomerES customerES = createCustomer(TEST_NAME, 1);
		customerSearchService.insert(customerES);
		QueryParameters params = SearchTestUtil.createQueryParameters("name", TEST_NAME);
		customerSearchService.refreshIndex();
		List<Integer> appList = customerSearchService.findByField(params, null).getContent();
		assertNotNull(appList);
		assertEquals(1, appList.size());
		assertEquals(1, (int) appList.get(0));
		customerSearchService.delete(customerES.getId().toString());
	}

	@Test
	void testFindById() {
		CustomerES customerES = createCustomer(TEST_NAME, 1);
		customerSearchService.insert(customerES);
		customerSearchService.refreshIndex();
		Optional<CustomerES> insertedCustomerES = customerSearchService.findObjectById("1");
		assertTrue(insertedCustomerES.isPresent());
		assertEquals(customerES.getName(), insertedCustomerES.get().getName());
		assertEquals(customerES.getRegistryKey(), insertedCustomerES.get().getRegistryKey());
		customerSearchService.delete(customerES.getId().toString());
	}

	@Test
	void testFindByFieldSorted() {
		CustomerES customerES1 = createCustomer("Zyzzy baabeli", 1);
		CustomerES customerES2 = createCustomer("baabeli aapeli", 2);
		CustomerES customerES3 = createCustomer("aapeli baabeli", 3);
		CustomerES customerES4 = createCustomer("ei löydy", 4);
		customerSearchService.insert(customerES1);
		customerSearchService.insert(customerES2);
		customerSearchService.insert(customerES3);
		customerSearchService.insert(customerES4);

		customerSearchService.refreshIndex();

		QueryParameters params = SearchTestUtil.createQueryParameters("name", "baabeli");

		List<Integer> appList = customerSearchService.findByField(params,
																															PageRequest.of(0, 100, Direction.ASC, "name"))
				.getContent();
		assertEquals(3, appList.size());
		assertEquals(Arrays.asList(3, 2, 1), appList);
	}

	@Test
	void testFindByRegistryKey() {
		CustomerES customerES1 = createCustomer("1", 1);
		customerES1.setRegistryKey("9444-9231");
		CustomerES customerES2 = createCustomer("2", 2);
		customerES2.setRegistryKey("9433-2311");
		CustomerES customerES3 = createCustomer("3", 3);
		customerES3.setRegistryKey("9422-5551");
		customerSearchService.insert(customerES1);
		customerSearchService.insert(customerES2);
		customerSearchService.insert(customerES3);

		customerSearchService.refreshIndex();

		// test finding partial and sorting alphabetically: 9
		QueryParameters params = SearchTestUtil.createQueryParameters("registryKey", "94");

		List<Integer> appList = customerSearchService.findByField(params,
																															PageRequest.of(0, 100, Direction.ASC, "registryKey"))
				.getContent();
		assertEquals(3, appList.size());
		assertEquals(Arrays.asList(3, 2, 1), appList);

		// test searching only from beginning of word (dash is part of word)
		params = SearchTestUtil.createQueryParameters("registryKey", "9422");

		appList = customerSearchService.findByField(params,
																								PageRequest.of(0, 100, Direction.ASC, "registryKey")).getContent();
		assertEquals(1, appList.size());
		assertEquals(Collections.singletonList(3), appList);

		// test full string search
		params = SearchTestUtil.createQueryParameters("registryKey", "9444-9231");

		appList = customerSearchService.findByField(params,
																								PageRequest.of(0, 100, Direction.ASC, "registryKey")).getContent();
		assertEquals(1, appList.size());
		assertEquals(Collections.singletonList(1), appList);
	}

	@Test
	void registryKey() {
		CustomerES customerES1 = createCustomer("1", 1);
		customerES1.setRegistryKey("123456");
		CustomerES customerES2 = createCustomer("2", 2);
		customerES2.setRegistryKey("123456-1");
		CustomerES customerES3 = createCustomer("3", 3);
		customerES3.setRegistryKey("1234567-1");
		customerSearchService.insert(customerES1);
		customerSearchService.insert(customerES2);
		customerSearchService.insert(customerES3);

		customerSearchService.refreshIndex();

		QueryParameters params = SearchTestUtil.createQueryParameters("registryKey", "123456");

		List<Integer> appList = customerSearchService.findByField(params,
																															PageRequest.of(0, 100, Direction.ASC, "registryKey"))
				.getContent();
		assertEquals(3, appList.size());

		params = SearchTestUtil.createQueryParameters("registryKey", "123456-1");

		appList = customerSearchService.findByField(params,
																								PageRequest.of(0, 100, Direction.ASC, "registryKey")).getContent();
		assertEquals(1, appList.size());
		assertEquals(Collections.singletonList(2), appList);
	}

	@Test
	void testUpdateApplicationCustomerPartially() {
		ApplicationES applicationES = ApplicationSearchIT.createApplication(100);
		CustomerES customerES = createCustomer(TEST_NAME, 123);
		RoleTypedCustomerES roleTypedCustomerES =
				new RoleTypedCustomerES(Collections.singletonMap(CustomerRoleType.APPLICANT,
																												 SearchTestUtil.createCustomerWithContacts(customerES)));
		applicationES.setCustomers(roleTypedCustomerES);

		applicationSearchService.insert(applicationES);

		final String updatedName = "updated name";
		final String updatedKey = "updated key";
		customerES.setName(updatedName);
		customerES.setRegistryKey(updatedKey);
		applicationES.getCustomers().getApplicant().setCustomer(customerES);
		applicationSearchService.refreshIndex();

		ApplicationQueryParameters params;
		List<Integer> appList;

		// should find by application name
		params = SearchTestUtil.createApplicationQueryParameters("name", applicationES.getName());
		appList = applicationSearchService.findByField(params, null).getContent();
		assertEquals(1, appList.size());

		// should find by inserted name
		params = SearchTestUtil.createApplicationQueryParameters("customers.applicant.customer.name", TEST_NAME);
		appList = applicationSearchService.findByField(params, null).getContent();
		assertEquals(1, appList.size());

		Map customersMap = CustomersIndexUtil.getCustomerUpdateStructure(
				Collections.singletonList(CustomerRoleType.APPLICANT), customerES);
		applicationSearchService.partialUpdate(Collections.singletonMap(applicationES.getId(), customersMap), false);
		applicationSearchService.refreshIndex();

		// should find by name
		params = SearchTestUtil.createApplicationQueryParameters("customers.applicant.customer.name", updatedName);
		appList = applicationSearchService.findByField(params, null).getContent();
		assertEquals(1, appList.size());

		// should find by updated registry key
		params = SearchTestUtil.createApplicationQueryParameters("customers.applicant.customer.registryKey", updatedKey);
		appList = applicationSearchService.findByField(params, null).getContent();
		assertEquals(1, appList.size());

		// should still find by application name
		params = SearchTestUtil.createApplicationQueryParameters("name", applicationES.getName());
		appList = applicationSearchService.findByField(params, null).getContent();
		assertEquals(1, appList.size());

		applicationSearchService.delete("100");
	}

	@Test
	void testBulkUpdateApplicationCustomerPartially() {
		ApplicationES applicationES1 = ApplicationSearchIT.createApplication(100);
		CustomerES customerES1 = createCustomer(TEST_NAME, 123);
		RoleTypedCustomerES roleTypedCustomerES =
				new RoleTypedCustomerES(Collections.singletonMap(CustomerRoleType.APPLICANT,
																												 SearchTestUtil.createCustomerWithContacts(customerES1)));
		applicationES1.setCustomers(roleTypedCustomerES);
		applicationSearchService.insert(applicationES1);

		ApplicationES applicationES2 = ApplicationSearchIT.createApplication(101);
		CustomerES customerES2 = createCustomer("second customer", 321);
		roleTypedCustomerES =
				new RoleTypedCustomerES(Collections.singletonMap(CustomerRoleType.PROPERTY_DEVELOPER,
																												 SearchTestUtil.createCustomerWithContacts(customerES2)));
		applicationES2.setCustomers(roleTypedCustomerES);
		applicationSearchService.insert(applicationES2);

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

		HashMap<Integer, Object> idToUpdateData = new HashMap<>();
		idToUpdateData.put(
				applicationES1.getId(),
				CustomersIndexUtil.getCustomerUpdateStructure(Collections.singletonList(CustomerRoleType.APPLICANT),
																											customerES1));
		idToUpdateData.put(
				applicationES2.getId(),
				CustomersIndexUtil.getCustomerUpdateStructure(Collections.singletonList(CustomerRoleType.PROPERTY_DEVELOPER),
																											customerES2));
		applicationSearchService.partialUpdate(idToUpdateData, false);
		applicationSearchService.refreshIndex();

		// should find by updated name
		ApplicationQueryParameters params = SearchTestUtil.createApplicationQueryParameters(
				"customers.applicant.customer.name", updatedName1);
		Page<Integer> appPage = applicationSearchService.findByField(params, null);
		assertEquals(1, appPage.getNumberOfElements());
		params = SearchTestUtil.createApplicationQueryParameters("customers.propertyDeveloper.customer.name",
																														 updatedName2);
		appPage = applicationSearchService.findByField(params, null);
		assertEquals(1, appPage.getNumberOfElements());

		// should find by updated registry key
		params = SearchTestUtil.createApplicationQueryParameters("customers.applicant.customer.registryKey", updatedKey1);
		appPage = applicationSearchService.findByField(params, null);
		assertEquals(1, appPage.getNumberOfElements());
		params = SearchTestUtil.createApplicationQueryParameters("customers.propertyDeveloper.customer.registryKey",
																														 updatedKey2);
		appPage = applicationSearchService.findByField(params, null);
		assertEquals(1, appPage.getNumberOfElements());

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
