package fi.hel.allu.ui.domain;

import com.greghaskins.spectrum.Spectrum;
import org.junit.runner.RunWith;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import java.time.ZonedDateTime;
import java.util.Set;

import static com.greghaskins.spectrum.Spectrum.*;
import static org.junit.Assert.assertEquals;

@RunWith(Spectrum.class)
public class LocationJsonSpec {

  private static Validator validator;

  {
    describe("Location time validation", () -> {
      beforeEach(() -> {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
      });
      it("should validate start time before end time ok", () -> {
        LocationJson locationJson = new LocationJson();
        locationJson.setStartTime(ZonedDateTime.parse("2016-11-12T08:00:00+02:00[Europe/Helsinki]"));
        locationJson.setEndTime(ZonedDateTime.parse("2016-11-13T08:00:00+02:00[Europe/Helsinki]"));
        Set<ConstraintViolation<LocationJson>> constraintViolations = validator.validate(locationJson);
        assertEquals(0, constraintViolations.size() );
      });
      it("should validate end time before start time not ok", () -> {
        LocationJson locationJson = new LocationJson();
        locationJson.setStartTime(ZonedDateTime.parse("2016-11-13T08:00:00+02:00[Europe/Helsinki]"));
        locationJson.setEndTime(ZonedDateTime.parse("2016-11-12T08:00:00+02:00[Europe/Helsinki]"));
        Set<ConstraintViolation<LocationJson>> constraintViolations = validator.validate(locationJson);
        assertEquals(1, constraintViolations.size());
      });
    });
  }
}
