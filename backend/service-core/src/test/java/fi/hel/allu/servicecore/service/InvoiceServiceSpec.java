package fi.hel.allu.servicecore.service;

import com.greghaskins.spectrum.Spectrum;

import fi.hel.allu.common.domain.types.ChargeBasisUnit;
import fi.hel.allu.model.domain.Invoice;
import fi.hel.allu.model.domain.InvoiceRow;
import fi.hel.allu.servicecore.config.ApplicationProperties;
import fi.hel.allu.servicecore.domain.InvoiceJson;

import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.web.client.RestTemplate;

import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.greghaskins.spectrum.dsl.specification.Specification.beforeEach;
import static com.greghaskins.spectrum.dsl.specification.Specification.describe;
import static com.greghaskins.spectrum.dsl.specification.Specification.it;
import static org.junit.Assert.assertEquals;

@RunWith(Spectrum.class)
public class InvoiceServiceSpec {
  private static final int APPLICATION_ID = 11;

  @Mock
  private ApplicationProperties applicationProperties;
  @Mock
  private RestTemplate restTemplate;
  private InvoiceService invoiceService;

  {
    describe("Invoice service", () -> {
      beforeEach(() -> {
        MockitoAnnotations.initMocks(this);
        invoiceService = new InvoiceService(applicationProperties, restTemplate);
      });

      it("Retrieves invoices", () -> {
        final Invoice[] modelInvoices = mockInvoices();
        Mockito.when(restTemplate.getForObject(Mockito.any(), Mockito.eq(Invoice[].class), Mockito.anyInt()))
            .thenReturn(modelInvoices);
        List<InvoiceJson> invoices = invoiceService.findByApplication(123);
        assertEquals(modelInvoices.length, invoices.size());
        assertEquals(modelInvoices[0].getRows().get(0).getText(), invoices.get(0).getRows().get(0).getText());
      });
    });
  }

  private Invoice[] mockInvoices() {
    return new Invoice[] {
        new Invoice(1, APPLICATION_ID, ZonedDateTime.parse("2017-12-05T09:15:30+02:00"), false, false,
            Arrays.asList(
                new InvoiceRow(ChargeBasisUnit.DAY, 1, "One day", new String[] {"24 hours"}, 1000, 1000),
                new InvoiceRow(ChargeBasisUnit.DAY, 2, "Two days", new String[] {"48 hours"}, 1000, 2000)),
            1),
        new Invoice(2, APPLICATION_ID, ZonedDateTime.parse("2018-01-15T09:15:30+02:00"), false, false,
            Collections.singletonList(new InvoiceRow(ChargeBasisUnit.WEEK, 1, "One week",
                new String[] { "168 hours" }, 1000, 1000)),
            1) };
  }
}
