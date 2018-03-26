package fi.hel.allu.model.dao;

import java.util.List;
import java.util.Optional;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.web.WebAppConfiguration;
import com.greghaskins.spectrum.Spectrum;
import fi.hel.allu.common.domain.types.StoredFilterType;
import fi.hel.allu.model.ModelApplication;
import fi.hel.allu.model.domain.StoredFilter;
import fi.hel.allu.model.domain.user.User;
import fi.hel.allu.model.testUtils.SpeccyTestBase;
import fi.hel.allu.model.testUtils.TestCommon;


import static com.greghaskins.spectrum.Spectrum.it;
import static com.greghaskins.spectrum.dsl.specification.Specification.*;
import static org.junit.Assert.*;

@RunWith(Spectrum.class)
@SpringBootTest(classes = ModelApplication.class)
@WebAppConfiguration
public class StoredFilterDaoSpec extends SpeccyTestBase {

  @Autowired
  private StoredFilterDao dao;
  @Autowired
  TestCommon testCommon;

  private User user1;
  private User user2;
  private StoredFilter user1ExistingFilter;

  {
    beforeEach(() -> {
      testCommon.deleteAllData();
      user1 = testCommon.insertUser("user1");
      user2 = testCommon.insertUser("user2");
      user1ExistingFilter = dao.insert(createFilter(StoredFilterType.MAP, "MapFilter", true, "{}", user1.getId()));
    });

    describe("Stored filter", () -> {
      context("Find", () -> {
        it("Find by id", () -> {
          Optional<StoredFilter> filter = dao.findById(user1ExistingFilter.getId());
          assertTrue(filter.isPresent());
          assertFilterEquals(user1ExistingFilter, filter.get());
        });

        it("Find non-existent by id", () -> {
          Optional<StoredFilter> filter = dao.findById(12345);
          assertFalse(filter.isPresent());
        });

        it("Find by type and user", () -> {
          // for user1
          dao.insert(createFilter(StoredFilterType.MAP, "MapFilter", true, "{}", user1.getId()));
          dao.insert(createFilter(StoredFilterType.APPLICATION_SEARCH, "SearchFilter", false, "{}", user1.getId()));

          // for user2
          dao.insert(createFilter(StoredFilterType.MAP, "MapFilter", true, "{}", user2.getId()));
          dao.insert(createFilter(StoredFilterType.SUPERVISION_WORKQUEUE, "SupervisionWorkqueue", false, "{}", user2.getId()));

          List<StoredFilter> user1MapFilters = dao.findByUserAndType(user1.getId(), StoredFilterType.MAP);
          assertEquals(2, user1MapFilters.size());
          assertTrue(user1MapFilters.stream()
              .allMatch(f -> StoredFilterType.MAP.equals(f.getType())));

          List<StoredFilter> user1ApplicationSearchFilters = dao.findByUserAndType(user1.getId(), StoredFilterType.APPLICATION_SEARCH);
          assertEquals(1, user1ApplicationSearchFilters.size());
          assertTrue(user1ApplicationSearchFilters.stream()
              .allMatch(f -> StoredFilterType.APPLICATION_SEARCH.equals(f.getType())));


          List<StoredFilter> user2MapFilters = dao.findByUserAndType(user2.getId(), StoredFilterType.MAP);
          assertEquals(1, user2MapFilters.size());
          assertEquals(StoredFilterType.MAP, user2MapFilters.get(0).getType());

          List<StoredFilter> user2SupervisionFilters = dao.findByUserAndType(user2.getId(), StoredFilterType.SUPERVISION_WORKQUEUE);
          assertEquals(1, user2SupervisionFilters.size());
          assertEquals(StoredFilterType.SUPERVISION_WORKQUEUE, user2SupervisionFilters.get(0).getType());
        });


        it("Find by type and user with no filters", () -> {
          dao.insert(createFilter(StoredFilterType.APPLICATION_SEARCH, "SearchFilter", false, "{}", user1.getId()));
          assertEquals(0, dao.findByUserAndType(user1.getId(), StoredFilterType.WORKQUEUE).size());
        });
      });

      it("Create", () -> {
        StoredFilter newFilter = createFilter(StoredFilterType.MAP, "newFilter", false, "{}", user2.getId());
        StoredFilter created = dao.insert(newFilter);
        assertFilterHasSameContent(newFilter, created);
      });

      it("Update", () -> {
        user1ExistingFilter.setName("Testing was ok!");
        user1ExistingFilter.setFilter("{newField: newValue, otherNewField: 1}");

        StoredFilterType originalType = user1ExistingFilter.getType();
        Integer originalUser = user1ExistingFilter.getUserId();
        // Should ignore updating these
        user1ExistingFilter.setType(StoredFilterType.APPLICATION_SEARCH);
        user1ExistingFilter.setUserId(user2.getId());

        StoredFilter updatedFilter = dao.update(user1ExistingFilter);
        assertEquals(user1ExistingFilter.getName(), updatedFilter.getName());
        assertEquals(user1ExistingFilter.getFilter(), updatedFilter.getFilter());
        assertEquals(originalType, updatedFilter.getType());
        assertEquals(originalUser, updatedFilter.getUserId());
      });


      it("Delete", () -> {
        dao.delete(user1ExistingFilter.getId());
        Optional<StoredFilter> deletedFilter = dao.findById(user1ExistingFilter.getId());
        assertFalse(deletedFilter.isPresent());
      });

      it("New default filter replaces old default filter", () -> {
        StoredFilter newDefaultFilter = createFilter(StoredFilterType.MAP, "newFilter", true, "{}", user1.getId());
        StoredFilter created = dao.insert(newDefaultFilter);
        assertTrue(created.isDefaultFilter());

        StoredFilter oldDefault = dao.findById(user1ExistingFilter.getId()).get();
        assertFalse(oldDefault.isDefaultFilter());
      });

      it("Setting new default filter replaces old default", () -> {
        StoredFilter newFilter = createFilter(StoredFilterType.MAP, "newFilter", false, "{}", user1.getId());
        StoredFilter created = dao.insert(newFilter);
        Integer oldDefaultId = user1ExistingFilter.getId();

        assertTrue(dao.findById(oldDefaultId).get().isDefaultFilter());

        dao.setAsDefault(created.getId());

        assertTrue(dao.findById(created.getId()).get().isDefaultFilter());
        assertFalse(dao.findById(oldDefaultId).get().isDefaultFilter());
      });
    });
  }

  private void assertFilterEquals(StoredFilter expected, StoredFilter actual) {
    assertEquals(expected.getId(), actual.getId());
    assertFilterHasSameContent(expected, actual);
  }

  private void assertFilterHasSameContent(StoredFilter expected, StoredFilter actual) {
    assertEquals(expected.getType(), actual.getType());
    assertEquals(expected.getName(), actual.getName());
    assertEquals(expected.isDefaultFilter(), actual.isDefaultFilter());
    assertEquals(expected.getFilter(), actual.getFilter());
    assertEquals(expected.getUserId(), actual.getUserId());
  }


  private StoredFilter createFilter(StoredFilterType type, String name, boolean isDefault, String filter, Integer userId) {
    return new StoredFilter(
        null,
        type,
        name,
        isDefault,
        filter,
        userId);
  }
}
