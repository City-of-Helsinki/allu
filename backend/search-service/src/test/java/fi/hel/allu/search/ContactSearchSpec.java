package fi.hel.allu.search;

import com.greghaskins.spectrum.Spectrum;
import fi.hel.allu.search.config.ElasticSearchMappingConfig;
import fi.hel.allu.search.domain.ContactES;
import fi.hel.allu.search.domain.QueryParameters;
import fi.hel.allu.search.service.GenericSearchService;
import org.elasticsearch.client.Client;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestContextManager;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.greghaskins.spectrum.Spectrum.*;
import static org.junit.Assert.assertEquals;

@RunWith(Spectrum.class)
@ContextConfiguration(classes = AppTestConfig.class)
public class ContactSearchSpec {

  @Autowired
  private Client client;

  private GenericSearchService contactSearchService;
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
  }
}
