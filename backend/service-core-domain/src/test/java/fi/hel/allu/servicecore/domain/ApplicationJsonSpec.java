package fi.hel.allu.servicecore.domain;

import com.greghaskins.spectrum.Spectrum;

import fi.hel.allu.common.domain.types.ApplicationKind;
import fi.hel.allu.common.domain.types.ApplicationSpecifier;
import fi.hel.allu.common.domain.types.ApplicationType;
import fi.hel.allu.common.types.PublicityType;

import fi.hel.allu.common.domain.types.SurfaceHardness;
import org.junit.runner.RunWith;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.Set;

import static com.greghaskins.spectrum.dsl.specification.Specification.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

@RunWith(Spectrum.class)
public class ApplicationJsonSpec {

  private static Validator validator;
  ApplicationJson applicationJson;

  {
    describe("ApplicationJson validation", () -> {
      beforeEach(() -> {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
        applicationJson = new ApplicationJson();
      });
      context("Empty application", () -> {
        it("should not validate ok", () -> {
          Set<ConstraintViolation<ApplicationJson>> constraintViolations = validator.validate(applicationJson);
          assertNotEquals(0, constraintViolations.size());
        });
      });

      describe("Minimally valid application", () -> {
        beforeEach(() -> {
          applicationJson.setType(ApplicationType.CABLE_REPORT);
          applicationJson.setName("Test application");
          CableReportJson extension = new CableReportJson();
          applicationJson.setExtension(extension);
          applicationJson.setKind(ApplicationKind.STREET_AND_GREEN);
          applicationJson.setDecisionPublicityType(PublicityType.CONFIDENTIAL);
          applicationJson.setInvoicingDate(ZonedDateTime.now());
          CustomerWithContactsJson customer = new CustomerWithContactsJson();
          applicationJson.setCustomersWithContacts(Collections.singletonList(customer));
          LocationJson locationJson = new LocationJson();
          locationJson.setStartTime(ZonedDateTime.now().minusDays(1));
          locationJson.setEndTime(ZonedDateTime.now());
          applicationJson.setLocations(Collections.singletonList(locationJson));
          applicationJson.setNotBillable(false);
        });
        context("Without added errors", () -> {
          it("should validate ok", () -> {
            Set<ConstraintViolation<ApplicationJson>> constraintViolations = validator.validate(applicationJson);
            assertEquals(0, constraintViolations.size());
          });
        });

        context("With incompatible application kind", () -> {
          beforeEach(() -> {
            applicationJson.setKind(ApplicationKind.LIFTING);
          });
          it("Should not validate", () -> {
            Set<ConstraintViolation<ApplicationJson>> constraintViolations = validator.validate(applicationJson);
            assertNotEquals(0, constraintViolations.size());
          });
        });

        context("With incompatible application specifier", () -> {
          beforeEach(() -> {
            applicationJson
                .setKindsWithSpecifiers(
              Collections.singletonMap(ApplicationKind.STREET_AND_GREEN,
                Arrays.asList(
                  ApplicationSpecifier.INDUCTION_LOOP, ApplicationSpecifier.BRIDGE, ApplicationSpecifier.DATA_WELL)));
          });
          it("Should not validate", () -> {
            Set<ConstraintViolation<ApplicationJson>> constraintViolations = validator.validate(applicationJson);
            assertNotEquals(0, constraintViolations.size());
          });
        });

      });

      describe("Outdoor event without nature", () -> {
        beforeEach(() -> {
          applicationJson.setType(ApplicationType.EVENT);
          applicationJson.setName("Test application");
          EventJson extension = new EventJson();
          extension.setDescription("Outdoor happening");
          ZonedDateTime start = ZonedDateTime.now();
          extension.setEventStartTime(start);
          extension.setEventEndTime(start.plusDays(2));
          extension.setSurfaceHardness(SurfaceHardness.HARD);
          applicationJson.setExtension(extension);
          applicationJson.setKind(ApplicationKind.OUTDOOREVENT);
          applicationJson.setDecisionPublicityType(PublicityType.CONFIDENTIAL);
          applicationJson.setInvoicingDate(ZonedDateTime.now());
          CustomerWithContactsJson customer = new CustomerWithContactsJson();
          applicationJson.setCustomersWithContacts(Collections.singletonList(customer));
          LocationJson locationJson = new LocationJson();
          locationJson.setStartTime(ZonedDateTime.now().minusDays(1));
          locationJson.setEndTime(ZonedDateTime.now());
          applicationJson.setLocations(Collections.singletonList(locationJson));
          applicationJson.setNotBillable(false);
        });
        context("With added errors", () -> {
          it("shouldn't validate", () -> {
            Set<ConstraintViolation<ApplicationJson>> constraintViolations = validator.validate(applicationJson);
            assertEquals(1, constraintViolations.size());
          });
        });
      });
    });
  }
}
