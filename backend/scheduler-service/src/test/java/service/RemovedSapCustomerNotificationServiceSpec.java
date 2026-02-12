package service;

import com.greghaskins.spectrum.Spectrum;
import fi.hel.allu.model.domain.ArchivedCustomer;
import fi.hel.allu.model.domain.Configuration;
import fi.hel.allu.model.domain.ConfigurationKey;
import fi.hel.allu.model.domain.ConfigurationType;
import fi.hel.allu.scheduler.config.ApplicationProperties;
import fi.hel.allu.scheduler.service.AlluMailService;
import fi.hel.allu.scheduler.service.AuthenticationService;
import fi.hel.allu.scheduler.service.RemovedSapCustomerNotificationService;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.greghaskins.spectrum.Spectrum.*;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

@RunWith(Spectrum.class)
public class RemovedSapCustomerNotificationServiceSpec {

  private static final String REMOVED_CUSTOMERS_URL = "removed_customers_url";
  private static final String MARK_NOTIFIED_URL = "mark_notified_url";
  private static final String CUSTOMER_NOTIFICATION_SUBJECT = "Mail subject";
  private static final String CUSTOMER_NOTIFICATION_RECEIVER = "foo@bar.com";
  private static final String CUSTOMER_NOTIFICATION_EMAIL_URL = "/configurations/" + ConfigurationKey.CUSTOMER_NOTIFICATION_RECEIVER_EMAIL;

  private RemovedSapCustomerNotificationService notificationService;

  @Mock
  private RestTemplate restTemplate;
  @Mock
  private ApplicationProperties applicationProperties;
  @Mock
  private AlluMailService alluMailService;
  @Mock
  private AuthenticationService authenticationService;

  {
    describe("Removed SAP customer notification service", () -> {

      beforeEach(() -> {
        MockitoAnnotations.initMocks(this);

        List<Configuration> configs = new ArrayList<>();
        configs.add(new Configuration(
          ConfigurationType.EMAIL,
          ConfigurationKey.CUSTOMER_NOTIFICATION_RECEIVER_EMAIL,
          CUSTOMER_NOTIFICATION_RECEIVER
        ));

        when(authenticationService.getBearerToken()).thenReturn("");
        when(applicationProperties.getRemovedSapCustomersUrl()).thenReturn(REMOVED_CUSTOMERS_URL);
        when(applicationProperties.getMarkRemovedSapCustomersNotifiedUrl()).thenReturn(MARK_NOTIFIED_URL);
        when(applicationProperties.getRemovedSapCustomersSubject()).thenReturn(CUSTOMER_NOTIFICATION_SUBJECT);
        when(applicationProperties.getCustomerNotificationReceiverEmailsUrl()).thenReturn(CUSTOMER_NOTIFICATION_EMAIL_URL);

        when(restTemplate.exchange(
          eq(CUSTOMER_NOTIFICATION_EMAIL_URL),
          eq(HttpMethod.GET),
          any(),
          any(ParameterizedTypeReference.class)
        )).thenReturn(new ResponseEntity<>(configs, HttpStatus.OK));

        notificationService = new RemovedSapCustomerNotificationService(
          restTemplate,
          applicationProperties,
          alluMailService,
          authenticationService
        );
      });

      describe("sendRemovedSapCustomerNotifications", () -> {

        it("should not send email if receiver email list is empty", () -> {
          when(restTemplate.exchange(
            eq(CUSTOMER_NOTIFICATION_EMAIL_URL),
            eq(HttpMethod.GET),
            any(),
            any(ParameterizedTypeReference.class)
          )).thenReturn(new ResponseEntity<>(Collections.emptyList(), HttpStatus.OK));

          notificationService.sendRemovedSapCustomerNotifications();

          verify(alluMailService, never())
            .sendEmail(anyList(), anyString(), anyString(), any(), any());
        });

        it("should not send email if no removed sap customers found", () -> {
          when(restTemplate.exchange(
            eq(REMOVED_CUSTOMERS_URL),
            eq(HttpMethod.GET),
            any(HttpEntity.class),
            eq(ArchivedCustomer[].class)
          )).thenReturn(new ResponseEntity<>(new ArchivedCustomer[0], HttpStatus.OK));

          notificationService.sendRemovedSapCustomerNotifications();

          verify(alluMailService, never()).sendEmail(anyList(), anyString(), anyString(), anyString(), anyList());
        });

        it("should send email if removed sap customers exist", () -> {
          ArgumentCaptor<String> txtCaptor = ArgumentCaptor.forClass(String.class);
          ArgumentCaptor<String> htmlCaptor = ArgumentCaptor.forClass(String.class);

          ArchivedCustomer dto = new ArchivedCustomer();
          dto.setCustomerId(123);
          dto.setSapCustomerNumber("SAP123");
          dto.setId(1);

          when(restTemplate.exchange(
            eq(REMOVED_CUSTOMERS_URL),
            eq(HttpMethod.GET),
            any(HttpEntity.class),
            eq(ArchivedCustomer[].class)
          )).thenReturn(new ResponseEntity<>(new ArchivedCustomer[]{dto}, HttpStatus.OK));

          notificationService.sendRemovedSapCustomerNotifications();

          verify(alluMailService, times(1)).sendEmail(
            eq(Collections.singletonList(CUSTOMER_NOTIFICATION_RECEIVER)),
            eq(CUSTOMER_NOTIFICATION_SUBJECT),
            txtCaptor.capture(),
            htmlCaptor.capture(),
            isNull()
          );

          assertTrue(txtCaptor.getValue().contains("SAP123"));
          assertTrue(txtCaptor.getValue().contains("123"));

          assertTrue(htmlCaptor.getValue().contains("SAP123"));
          assertTrue(htmlCaptor.getValue().contains("<td>123</td>"));

          verify(restTemplate).postForObject(
            eq(MARK_NOTIFIED_URL),
            eq(Collections.singletonList(1)),
            eq(Void.class)
          );
        });
      });
    });
  }
}
