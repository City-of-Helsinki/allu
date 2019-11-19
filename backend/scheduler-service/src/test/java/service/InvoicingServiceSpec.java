package service;


import com.greghaskins.spectrum.Spectrum;
import fi.hel.allu.common.domain.types.ApplicationType;
import fi.hel.allu.common.domain.types.ChargeBasisUnit;
import fi.hel.allu.model.domain.Application;
import fi.hel.allu.model.domain.Configuration;
import fi.hel.allu.model.domain.Customer;
import fi.hel.allu.model.domain.Invoice;
import fi.hel.allu.model.domain.InvoiceRecipient;
import fi.hel.allu.model.domain.InvoiceRow;
import fi.hel.allu.sap.model.SalesOrder;
import fi.hel.allu.sap.model.SalesOrderContainer;
import fi.hel.allu.scheduler.config.ApplicationProperties;
import fi.hel.allu.scheduler.service.AlluMailService;
import fi.hel.allu.scheduler.service.ApplicationStatusUpdaterService;
import fi.hel.allu.scheduler.service.AuthenticationService;
import fi.hel.allu.scheduler.service.InvoicingService;
import fi.hel.allu.scheduler.service.SftpService;
import org.junit.Assert;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static com.greghaskins.spectrum.Spectrum.beforeEach;
import static com.greghaskins.spectrum.Spectrum.describe;
import static com.greghaskins.spectrum.Spectrum.it;
import static com.greghaskins.spectrum.Spectrum.let;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.isNull;
import static org.mockito.Mockito.*;

@RunWith(Spectrum.class)
@SuppressWarnings("unchecked")
public class InvoicingServiceSpec {

  private static final String PENDING_INVOICES_URL = "pending_invoices_url";
  private static final String FIND_APPLICATIONS_URL = "find_applications_url";
  private static final String FIND_CUSTOMER_URL = "find_customer_url";
  private static final String INVOICE_NOTIFICATION_RECEIVER_EMAILS_URL = "invoice_notification_receiver_emails_url";
  private static final String MARK_INVOICES_SENT_URL = "mark_invoices_sent_url";

  private InvoicingService invoicingService;
  @Mock
  private RestTemplate restTemplate;
  @Mock
  private ApplicationProperties applicationProperties;
  @Mock
  private AlluMailService alluMailService;
  @Mock
  private AuthenticationService authenticationService;
  @Mock
  private ApplicationStatusUpdaterService applicationStatusUpdaterService;

  @Mock
  private SftpService sftpService;

  {
    describe("Invoicing service", () -> {
      beforeEach(() -> MockitoAnnotations.initMocks(this));

      describe("sendInvoices", () -> {

        Supplier<Application> application = let(this::createApplication);
        Supplier<Integer> applicationId = let(() -> application.get().getId());
        Supplier<String> applicationApplicationId = let(() -> application.get().getApplicationId());

        beforeEach(() -> {
          when(applicationProperties.getPendingInvoicesUrl()).thenReturn(PENDING_INVOICES_URL);
          when(applicationProperties.getFindApplicationsUrl()).thenReturn(FIND_APPLICATIONS_URL);
          when(applicationProperties.getFindCustomerUrl()).thenReturn(FIND_CUSTOMER_URL);
          when(applicationProperties.getInvoiceNotificationReceiverEmailsUrl()).thenReturn(INVOICE_NOTIFICATION_RECEIVER_EMAILS_URL);
          when(applicationProperties.getMarkInvoicesSentUrl()).thenReturn(MARK_INVOICES_SENT_URL);

          when(restTemplate.postForObject(eq(FIND_APPLICATIONS_URL), anyList(), any())).thenReturn(new Application[]{application.get()});
          when(restTemplate.getForObject(eq(FIND_CUSTOMER_URL), any(), anyInt())).thenReturn(new Customer());

          when(restTemplate.exchange(eq(INVOICE_NOTIFICATION_RECEIVER_EMAILS_URL), any(), isNull(HttpEntity.class), any(ParameterizedTypeReference.class)))
            .thenReturn(responseWithValue(Collections.singletonList(new Configuration())));

          invoicingService = spy(new InvoicingService(restTemplate, applicationProperties, sftpService, alluMailService, applicationStatusUpdaterService));
        });

        describe("with only non-zero net sum invoices", () -> {
          Supplier<List<Invoice>> invoices = let(() -> createInvoices(applicationId.get(), 10, 100));
          Supplier<List<Integer>> invoiceIds = let(() -> invoices.get().stream().map(Invoice::getId).collect(Collectors.toList()));

          beforeEach(() -> {
            when(restTemplate.getForObject(eq(PENDING_INVOICES_URL), any())).thenReturn(invoices.get().toArray());
            doReturn(true).when(invoicingService).sendToSap(any());
          });

          it("should send invoices to sap", () -> {
            invoicingService.sendInvoices();
            assertSalesOrdersSentToSap(2);
          });

          it("should mark invoices sent", () -> {
            invoicingService.sendInvoices();
            assertInvoicesMarkedSent(invoiceIds.get());
          });

          it("should send email concerning related applications", () -> {
            invoicingService.sendInvoices();
            assertNotificationSentFor(Collections.singletonList(applicationApplicationId.get()));
          });

          it("should request archival of related applications", () -> {
            invoicingService.sendInvoices();
            assertArchivalRequested(Collections.singletonList(applicationId.get()));
          });
        });
      });
    });
  }

  private void assertSalesOrdersSentToSap(int numSalesOrders) {
    ArgumentCaptor<SalesOrderContainer> salesOrderCaptor = ArgumentCaptor.forClass(SalesOrderContainer.class);
    verify(invoicingService, times(1)).sendToSap(salesOrderCaptor.capture());
    assertValidSalesOrderContainer(salesOrderCaptor.getValue(), numSalesOrders);
  }

  private void assertInvoicesMarkedSent(List<Integer> invoiceIds) {
    ArgumentCaptor<List> invoiceIdCaptor = ArgumentCaptor.forClass(List.class);
    verify(restTemplate).postForObject(eq(MARK_INVOICES_SENT_URL), invoiceIdCaptor.capture(), any());
    assertContainSameElements(invoiceIdCaptor.getValue(), invoiceIds);
  }

  private void assertNotificationSentFor(List<String> applicationApplicationIds) {
    ArgumentCaptor<List> applicationIdCaptor = ArgumentCaptor.forClass(List.class);
    verify(invoicingService).sendNotificationEmail(applicationIdCaptor.capture(), any());
    assertContainSameElements(applicationIdCaptor.getValue(), applicationApplicationIds);
  }

  private void assertArchivalRequested(List<Integer> applicationIds) {
    ArgumentCaptor<List> applicationIdCaptor = ArgumentCaptor.forClass(List.class);
    verify(applicationStatusUpdaterService).archiveApplications(applicationIdCaptor.capture());
    assertContainSameElements(applicationIdCaptor.getValue(), applicationIds);
  }

  private void assertValidSalesOrderContainer(SalesOrderContainer salesOrderContainer, Integer numSalesOrders) {
    List<SalesOrder> salesOrders = salesOrderContainer.getSalesOrders();
    if (salesOrders.size() != numSalesOrders) {
      Assert.fail("expected " + numSalesOrders + " order(s) but got " + salesOrders.size());
    } else {
      for (SalesOrder salesOrder : salesOrders) {
        if (salesOrder.getLineItems().size() != 1) {
          Assert.fail("sales order should contain only one line item (in the context of these tests)");
        }
      }
    }
  }

  private void assertContainSameElements(List result, List expectation) {
    Collections.sort(result);
    Collections.sort(expectation);
    if (!expectation.equals(result)) {
      Assert.fail("expected " + expectation + " but got " + result);
    }
  }

  private static int idCounter = 1;

  private Application createApplication() {
    Application application = new Application();
    application.setId(idCounter++);
    application.setApplicationId("ID" + application.getId());
    application.setType(ApplicationType.CABLE_REPORT);
    return application;
  }

  private List<Invoice> createInvoices(Integer applicationId, Integer... netPrices) {
    List<Invoice> invoices = new ArrayList<>();
    for (Integer netPrice : netPrices) {
      invoices.add(createInvoice(applicationId, netPrice));
    }
    return invoices;
  }

  private Invoice createInvoice(Integer applicationId, int netPrice) {
    Invoice invoice = new Invoice();
    invoice.setId(idCounter++);
    invoice.setApplicationId(applicationId);

    ArrayList<InvoiceRow> rows = new ArrayList<>();
    InvoiceRow invoiceRow = createInvoiceRow(netPrice);
    rows.add(invoiceRow);
    invoice.setRows(rows);

    invoice.setInvoiceRecipient(new InvoiceRecipient());
    return invoice;
  }

  private InvoiceRow createInvoiceRow(int netPrice) {
    InvoiceRow invoiceRow = new InvoiceRow();
    invoiceRow.setId(idCounter++);
    invoiceRow.setUnit(ChargeBasisUnit.PIECE);
    invoiceRow.setNetPrice(netPrice);
    return invoiceRow;
  }

  private <T> ResponseEntity<T> responseWithValue(T v) {
    return new ResponseEntity<>(v, HttpStatus.OK);
  }

}
