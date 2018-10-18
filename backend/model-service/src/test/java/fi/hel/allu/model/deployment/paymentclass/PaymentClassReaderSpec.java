package fi.hel.allu.model.deployment.paymentclass;

import com.greghaskins.spectrum.Spectrum;

import org.junit.runner.RunWith;

import java.util.List;
import java.util.stream.Collectors;

import static com.greghaskins.spectrum.dsl.specification.Specification.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

@RunWith(Spectrum.class)
public class PaymentClassReaderSpec {

  private static final String XML_FILE = "src/test/java/fi/hel/allu/model/deployment/maksuvyohykkeet_pretty.xml";

  PaymentClassXml paymentClassXml;

  {
    describe("PaymentClassReader", () -> {

      context("Parsing the sample XML", () -> {
        beforeEach(() -> {
          paymentClassXml = PaymentClassReader.readPaymentClasses(XML_FILE);
        });

        it("Should contain 4 payment class zones", () -> {
          assertEquals(4, paymentClassXml.featureMember.size());
        });

        it("One zone should have holes", () -> {
          assertEquals(1, paymentClassXml.featureMember.stream()
              .filter(fm -> fm.paymentClass.geometry.polygon.innerBoundary != null).count());
        });

        it("Can map every payment class name to valid payment class", () -> {
          List<String> pcs = paymentClassXml.featureMember.stream()
              .map(fm -> fm.paymentClass.paymentClass)
              .collect(Collectors.toList());
          assertFalse(pcs.isEmpty());
        });
      });
    });
  }

}
