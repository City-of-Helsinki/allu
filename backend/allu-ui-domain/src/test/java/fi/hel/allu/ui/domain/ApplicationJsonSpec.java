package fi.hel.allu.ui.domain;

import com.greghaskins.spectrum.Spectrum;

import fi.hel.allu.common.types.*;

import org.junit.runner.RunWith;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

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
          applicationJson.setKind(ApplicationKind.STREET_AND_GREEN);
          applicationJson.setDecisionDistributionType(DistributionType.EMAIL);
          applicationJson.setName("Test application");
          CableReportJson extension = new CableReportJson();
          applicationJson.setExtension(extension);
          applicationJson.setDecisionPublicityType(PublicityType.CONFIDENTIAL);
          CustomerWithContactsJson customer = new CustomerWithContactsJson();
          applicationJson.setCustomersWithContacts(Collections.singletonList(customer));
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
            NoteJson extension = new NoteJson();
            extension.setSpecifiers(Arrays.asList(ApplicationSpecifier.INDUCTION_LOOP, ApplicationSpecifier.BRIDGE,
                ApplicationSpecifier.DATA_WELL));
            applicationJson.setExtension(extension);
          });
          it("Should not validate", () -> {
            Set<ConstraintViolation<ApplicationJson>> constraintViolations = validator.validate(applicationJson);
            assertNotEquals(0, constraintViolations.size());
          });
        });

      });

    });
  }
}
