package fi.hel.allu.search;

import com.greghaskins.spectrum.Spectrum;

import fi.hel.allu.common.domain.types.CustomerRoleType;
import fi.hel.allu.search.config.ElasticSearchMappingConfig;
import fi.hel.allu.search.domain.*;
import fi.hel.allu.search.service.*;
import fi.hel.allu.search.util.CustomersIndexUtil;

import org.elasticsearch.client.Client;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.junit.runner.RunWith;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.test.context.TestContextManager;
import org.testcontainers.elasticsearch.ElasticsearchContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.net.InetAddress;
import java.util.*;

import static com.greghaskins.spectrum.dsl.specification.Specification.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(Spectrum.class)
@Testcontainers
public class ContactSearchSpec {



  private static final String CLUSTER_NAME = "allu-cluster";
  private static final String NODE_NAME = "allu-node";
  private static final String ELASTIC_IMAGE = "docker.elastic.co/elasticsearch/elasticsearch:6.0.0";

  @Container
  private static final ElasticsearchContainer container = new ElasticsearchContainer(ELASTIC_IMAGE)
      .withExposedPorts(9300, 9200)
      .withEnv("xpack.security" + ".enabled", "false")
      .withEnv("network.host", "_site_")
      .withEnv("network" + ".publish_host", "_local_")
      .withEnv("node.name", NODE_NAME)
      .withEnv("cluster.name", CLUSTER_NAME);
  private Client client;
  private ApplicationSearchService applicationSearchService;

  private ContactSearchService contactSearchService;
  private QueryParameters params;

  private final ContactES testContact = new ContactES(1, "testable name", true);

  ElasticSearchMappingConfig elasticSearchMappingConfig;

  {
    beforeAll(() -> {
      container.start();
      new TestContextManager(getClass()).prepareTestInstance(this);
      TransportAddress transportAddress = new TransportAddress(InetAddress.getByName(container.getHost()),
                                                               container.getMappedPort(9300));
      Settings settings = Settings.builder().put("cluster.name", CLUSTER_NAME).build();
      client = new PreBuiltTransportClient(settings).addTransportAddress(transportAddress);
      elasticSearchMappingConfig = SearchTestUtil.searchIndexSetup(client);
    });

    describe("contactSearchService", () -> {
      beforeEach(() -> contactSearchService = new ContactSearchService(
              elasticSearchMappingConfig,
          client, new ContactIndexConductor()));
      describe("findByField", ()-> {
        context("with single inserted contact", ()-> {
          beforeEach(() -> {
            contactSearchService.insert(testContact);
            contactSearchService.refreshIndex();
          });

          context("when searching with existing name", ()-> {
            it("should find contact by full name", () -> {
              params = SearchTestUtil.createQueryParameters("name", testContact.getName());
              List<Integer> contacts = contactSearchService.findByField(params, null).getContent();
              assertEquals(1, contacts.size());
              assertEquals(1, (int) contacts.get(0));
            });
            it("should find contact by partial name", () -> {
              params = SearchTestUtil.createQueryParameters("name", testContact.getName().substring(0, 3));
              List<Integer> contacts = contactSearchService.findByField(params, null).getContent();
              assertEquals(1, contacts.size());
              assertEquals(1, (int) contacts.get(0));
            });
          });
          context("when searching with non-existing name", ()-> it("should not find contact", () -> {
            params = SearchTestUtil.createQueryParameters("name", "non-existent");
            List<Integer> contacts = contactSearchService.findByField(params, null).getContent();
            assertEquals(0, contacts.size());
          }));

          afterEach(() -> contactSearchService.delete(Integer.toString(testContact.getId())));
        });

        context("with multiple separately inserted contacts", ()-> {
          ContactES contact1 = new ContactES(1, "alpha one searchstr", true);
          ContactES contact2 = new ContactES(2, "beta two searchstr", true);
          ContactES contact3 = new ContactES(3, "gamma three searchstr", true);

          beforeEach(() -> {
            params = SearchTestUtil.createQueryParameters("name", "searchstr");
            contactSearchService.insert(contact1);
            contactSearchService.insert(contact2);
            contactSearchService.insert(contact3);
            contactSearchService.refreshIndex();
          });

          it("should find all inserted contacts", ()-> {
            List<Integer> contacts = contactSearchService.findByField(params, null).getContent();
            assertEquals(3, contacts.size());
          });
          it("should sort found contacts by name in alphabetical order with ASC sorting parameter", () -> {
            List<Integer> contacts = contactSearchService.findByField(params,
                PageRequest.of(0, 100, Direction.ASC, "name")).getContent();
            assertEquals(Arrays.asList(1, 2, 3), contacts);
          });
          it("should sort found contacts by name in inverted alphabetical order with DESC sorting parameter", () -> {
            List<Integer> contacts = contactSearchService.findByField(params,
                PageRequest.of(0, 100, Direction.DESC, "name")).getContent();
            assertEquals(Arrays.asList(3, 2, 1), contacts);
          });

          afterEach(() -> {
            contactSearchService.delete(Integer.toString(contact1.getId()));
            contactSearchService.delete(Integer.toString(contact2.getId()));
            contactSearchService.delete(Integer.toString(contact3.getId()));
          });
        });

        context("with bulk inserted contacts", ()-> {
          beforeEach(()-> {
            List<ContactES> contacts = new ArrayList<>();
            contacts.add(new ContactES(1, "alpha one searchstr", true));
            contacts.add(new ContactES(2, "beta two searchstr", true));
            contacts.add(new ContactES(3, "gamma three searchstr", true));
            contactSearchService.bulkInsert(contacts);
            contactSearchService.refreshIndex();
          });
          it("should find all inserted contacts", () -> {
            List<Integer> contacts = contactSearchService.findByField(params, null).getContent();
            assertEquals(3, contacts.size());
          });
          it("should sort found contacts by name in alphabetical order with ASC sorting parameter", ()->{
            QueryParameters params = SearchTestUtil.createQueryParameters("name", "searchstr");
            List<Integer> contacts = contactSearchService.findByField(params,
                PageRequest.of(0, 100, Direction.ASC, "name")).getContent();
            assertEquals(Arrays.asList(1, 2, 3), contacts);
          });
        });
      });
    });

    describe("applicationSearchService", ()-> {
      beforeEach(() -> applicationSearchService = new ApplicationSearchService(
              elasticSearchMappingConfig,
          client, new ApplicationIndexConductor()));

      describe("findByField", () -> {
        List<ContactES> contacts = ApplicationSearchIT.createContacts();
        ApplicationES applicationES = ApplicationSearchIT.createApplication(1);

        beforeEach(()-> {
          CustomerES customerES = new CustomerES();
          RoleTypedCustomerES roleTypedCustomerES =
                  new RoleTypedCustomerES(Collections.singletonMap(CustomerRoleType.APPLICANT,
                          SearchTestUtil.createCustomerWithContacts(customerES, contacts)));
          applicationES.setCustomers(roleTypedCustomerES);
          applicationSearchService.insert(applicationES);
          applicationSearchService.refreshIndex();
        });

        it("should find contacts stored in ElasticSearch", ()-> {
          ApplicationQueryParameters params = SearchTestUtil.createApplicationQueryParameters("customers.applicant.contacts.name", "kontakti");
          List<Integer> appList = applicationSearchService.findByField(params, null).getContent();
          assertNotNull(appList);
          assertEquals(1, appList.size());
        });

        context("when updating application with new and updated contacts", ()-> {
          beforeEach(()-> {
            contacts.get(0).setName("updated 1");

            ApplicationWithContactsES applicationWithContactsES1 =
                    new ApplicationWithContactsES(applicationES.getId(), CustomerRoleType.APPLICANT, contacts);
            ApplicationWithContactsES applicationWithContactsES2 = new ApplicationWithContactsES(
                    applicationES.getId(), CustomerRoleType.CONTRACTOR, Collections.singletonList(new ContactES(100, "kontraktori", true)));
            Map contactsMap =
                    CustomersIndexUtil.getContactsUpdateStructure(Arrays.asList(applicationWithContactsES1, applicationWithContactsES2));
            applicationSearchService.partialUpdate(contactsMap, false);
            applicationSearchService.refreshIndex();
          });

          it("should find applications updated contact of a customer", () -> {
            ApplicationQueryParameters appParams = SearchTestUtil.createApplicationQueryParameters("customers.applicant.contacts.name", "updated 1");
            List<Integer> appList = applicationSearchService.findByField(appParams, null).getContent();
            assertNotNull(appList);
            assertEquals(1, appList.size());
          });

          it("should find applications freshly inserted contact of a customer", () -> {
            ApplicationQueryParameters appParams = SearchTestUtil.createApplicationQueryParameters("customers.contractor.contacts.name", "kontraktori");
            List<Integer> appList = applicationSearchService.findByField(appParams, null).getContent();
            assertNotNull(appList);
            assertEquals(1, appList.size());
          });
        });

        afterEach(()-> applicationSearchService.delete("1"));
      });
    });
  }
}
