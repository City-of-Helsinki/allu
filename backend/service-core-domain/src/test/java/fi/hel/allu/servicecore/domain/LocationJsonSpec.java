package fi.hel.allu.servicecore.domain;

import com.greghaskins.spectrum.Spectrum;
import fi.hel.allu.servicecore.domain.LocationJson;
import org.junit.runner.RunWith;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import java.time.ZonedDateTime;
import java.util.Set;

import static com.greghaskins.spectrum.dsl.specification.Specification.beforeEach;
import static com.greghaskins.spectrum.dsl.specification.Specification.describe;
import static com.greghaskins.spectrum.dsl.specification.Specification.context;
import static com.greghaskins.spectrum.dsl.specification.Specification.it;
import static org.junit.Assert.assertEquals;

@RunWith(Spectrum.class)
public class LocationJsonSpec {

  private static Validator validator;
  LocationJson locationJson;

  {
    describe("Location time validation", () -> {
      beforeEach(() -> {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
        locationJson = new LocationJson();
      });
      context("when start time is before end time", () -> {
        it("should validate ok", () -> {
          locationJson.setStartTime(ZonedDateTime.parse("2016-11-12T08:00:00+02:00[Europe/Helsinki]"));
          locationJson.setEndTime(ZonedDateTime.parse("2016-11-13T08:00:00+02:00[Europe/Helsinki]"));
          Set<ConstraintViolation<LocationJson>> constraintViolations = validator.validate(locationJson);
          assertEquals(0, constraintViolations.size() );
        });
      });

      context("when start time is before end time", () -> {
        it("should not validate", () -> {
          locationJson.setStartTime(ZonedDateTime.parse("2016-11-13T08:00:00+02:00[Europe/Helsinki]"));
          locationJson.setEndTime(ZonedDateTime.parse("2016-11-12T08:00:00+02:00[Europe/Helsinki]"));
          Set<ConstraintViolation<LocationJson>> constraintViolations = validator.validate(locationJson);
          assertEquals(1, constraintViolations.size());
        });
      });

      context("when start time is equal to end time", () -> {
        it("should validate ok", () -> {
          ZonedDateTime dateTime = ZonedDateTime.now();
          locationJson.setStartTime(dateTime);
          locationJson.setEndTime(dateTime);
          Set<ConstraintViolation<LocationJson>> constraintViolations = validator.validate(locationJson);
          assertEquals(0, constraintViolations.size());
        });
      });
    });
  }
}
