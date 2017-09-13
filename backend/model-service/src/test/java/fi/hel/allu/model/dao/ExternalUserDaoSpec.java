package fi.hel.allu.model.dao;

import com.greghaskins.spectrum.Spectrum;
import fi.hel.allu.common.domain.types.CustomerType;
import fi.hel.allu.common.domain.types.ExternalRoleType;
import fi.hel.allu.common.exception.NonUniqueException;
import fi.hel.allu.model.ModelApplication;
import fi.hel.allu.model.domain.Customer;
import fi.hel.allu.model.domain.ExternalUser;
import fi.hel.allu.model.testUtils.SpeccyTestBase;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.web.WebAppConfiguration;

import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static com.greghaskins.spectrum.dsl.specification.Specification.*;
import static org.junit.Assert.*;

@RunWith(Spectrum.class)
@SpringBootTest(classes = ModelApplication.class)
@WebAppConfiguration
public class ExternalUserDaoSpec extends SpeccyTestBase {
  @Autowired
  private ExternalUserDao externalUserDao;
  @Autowired
  private CustomerDao customerDao;
  private ExternalUser testUser;
  private ExternalUser insertedUser;
  private Customer insertedCustomer1;
  private Customer insertedCustomer2;
  {
    // manual transaction handling done in SpeccyTestBase
    beforeEach(() -> {
      testCommon.deleteAllData();
      Customer testCustomer = new Customer();
      testCustomer.setType(CustomerType.COMPANY);
      testCustomer.setName("Testiyritysasiakas");
      testCustomer.setActive(true);
      testUser = new ExternalUser(
          null,
          "uuusername",
          "Testiyritys OyAb liittymä",
          "nothing@testiyritys.net",
          "testtoken",
          true,
          null,
          null,
          Collections.emptyList(),
          Collections.emptyList());
      insertedUser = externalUserDao.insert(testUser);
      insertedCustomer1 = customerDao.insert(testCustomer);
      testCustomer.setName("toinen testiyritys");
      insertedCustomer2 = customerDao.insert(testCustomer);
    });

    describe("ExternalUserDao", () -> {
      context("find", () -> {
        describe("by id", () -> {
          it("should find existing", () -> {
            Optional<ExternalUser> externalUser = externalUserDao.findById(insertedUser.getId());
            assertTrue(externalUser.isPresent());
          });
          it("should not find non-existent", () -> {
            Optional<ExternalUser> externalUser = externalUserDao.findById(-1);
            assertFalse(externalUser.isPresent());
          });
        });
        describe("by username", () -> {
          it("should find existing", () -> {
            Optional<ExternalUser> externalUser = externalUserDao.findByUsername(insertedUser.getUsername());
            assertTrue(externalUser.isPresent());
          });
          it("should not find non-existent", () -> {
            Optional<ExternalUser> externalUser = externalUserDao.findByUsername("non-existent");
            assertFalse(externalUser.isPresent());
          });
        });
        describe("all", () -> {
          it("should find all", () -> {
            List<ExternalUser> externalUsers = externalUserDao.findAll();
            assertEquals(1, externalUsers.size());
          });
        });
      });
      context("insert with connected customers", () -> {
        it("should work", () -> {
          testUser.setUsername("connected_test");
          testUser.setConnectedCustomers(Arrays.asList(insertedCustomer1.getId(), insertedCustomer2.getId()));
          insertedUser = externalUserDao.insert(testUser);
          assertEquals(testUser.getName(), insertedUser.getName());
          assertEquals(2, insertedUser.getConnectedCustomers().size());
          assertTrue(insertedUser.getConnectedCustomers().containsAll(Arrays.asList(insertedCustomer1.getId(), insertedCustomer2.getId())));
        });
        it("should complain if duplicate username", () -> {
            assertThrows(NonUniqueException.class).when(() -> externalUserDao.insert(testUser));
        });
      });
      context("insert with roles", () -> {
        it("should work", () -> {
          testUser.setUsername("roles_test");
          testUser.setAssignedRoles(Arrays.asList(ExternalRoleType.ROLE_INTERNAL, ExternalRoleType.ROLE_TRUSTED_PARTNER));
          insertedUser = externalUserDao.insert(testUser);
          assertEquals(testUser.getName(), insertedUser.getName());
          assertEquals(2, insertedUser.getAssignedRoles().size());
          assertTrue(insertedUser.getAssignedRoles().containsAll(Arrays.asList(ExternalRoleType.ROLE_INTERNAL, ExternalRoleType.ROLE_TRUSTED_PARTNER)));
        });
      });
      context("update", () -> {
        it("should work", () -> {
          insertedUser.setConnectedCustomers(Arrays.asList(insertedCustomer1.getId()));
          insertedUser.setAssignedRoles(Arrays.asList(ExternalRoleType.ROLE_TRUSTED_PARTNER));
          externalUserDao.update(insertedUser);
          Optional<ExternalUser> updatedUser = externalUserDao.findById(insertedUser.getId());
          assertTrue(updatedUser.isPresent());
          assertEquals(1, updatedUser.get().getConnectedCustomers().size());
          assertEquals(1, updatedUser.get().getAssignedRoles().size());
        });
        it("should complain if duplicate username", () -> {
          testUser.setUsername("updateduplicate");
          externalUserDao.insert(testUser);
          insertedUser.setUsername("updateduplicate");
          assertThrows(NonUniqueException.class).when(() -> externalUserDao.update(insertedUser));
        });
        it("should update last login", () -> {
          ZonedDateTime now = ZonedDateTime.now();
          ExternalUser notUpdatedLastLogin = externalUserDao.findById(insertedUser.getId()).get();
          assertNull(notUpdatedLastLogin.getLastLogin());
          externalUserDao.setLastLogin(insertedUser.getId(), now);
          ExternalUser updatedLastLogin = externalUserDao.findById(insertedUser.getId()).get();
          assertEquals(now, updatedLastLogin.getLastLogin());
        });
      });
    });
  }

}
