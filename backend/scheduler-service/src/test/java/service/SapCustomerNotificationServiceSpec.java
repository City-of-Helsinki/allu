package service;

import java.util.Collections;

import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.greghaskins.spectrum.Spectrum;

import fi.hel.allu.scheduler.config.ApplicationProperties;
import fi.hel.allu.scheduler.service.AlluMailService;
import fi.hel.allu.scheduler.service.AuthenticationService;
import fi.hel.allu.scheduler.service.SapCustomerNotificationService;

import static com.greghaskins.spectrum.dsl.specification.Specification.*;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

@RunWith(Spectrum.class)
public class SapCustomerNotificationServiceSpec {

  private static final String COUNT_URL = "count_url";
  private static final String CUSTOMER_DOWNLOAD_URL = "download_url";
  private static final String CUSTOMER_NOTIFICATION_SUBJECT = "Mail subject";
  private static final String CUSTOMER_NOTIFICATION_RECEIVER = "foo@bar.com";

  private SapCustomerNotificationService notificationService;
  @Mock
  private RestTemplate restTemplate;
  @Mock
  private ApplicationProperties applicationProperties;
  @Mock
  private AlluMailService alluMailService;
  @Mock
  private AuthenticationService authenticationService;

  {
    describe("Applicant reminder service", () -> {
      beforeEach(() -> {
        MockitoAnnotations.initMocks(this);
        notificationService = new SapCustomerNotificationService(restTemplate, applicationProperties, alluMailService, authenticationService);
        when(authenticationService.getBearerToken()).thenReturn("");
        when(applicationProperties.getNrOfInvoiceRecipientsWithoutSapNumberUrl()).thenReturn(COUNT_URL);
        when(applicationProperties.getCustomerDownloadUrl()).thenReturn(CUSTOMER_DOWNLOAD_URL);
        when(applicationProperties.getCustomerNotificationMailSubject()).thenReturn(CUSTOMER_NOTIFICATION_SUBJECT);
        when(applicationProperties.getCustomerNotificationReceiverEmail()).thenReturn(CUSTOMER_NOTIFICATION_RECEIVER);
      });

      describe("sendNotifications", () -> {
        beforeEach(() -> {
          when(applicationProperties.getCustomerDownloadUrl()).thenReturn(CUSTOMER_DOWNLOAD_URL);
        });
        it("should not send email if receiver email empty", () -> {
          when(applicationProperties.getCustomerNotificationReceiverEmail()).thenReturn("");
          notificationService.sendSapCustomerNotificationEmails();
          verify(alluMailService, never()).sendEmail(anyListOf(String.class), anyString(), anyString());
        });
        it("should not send email if no customers without sap number", () -> {
          when(applicationProperties.getCustomerNotificationReceiverEmail()).thenReturn(CUSTOMER_NOTIFICATION_RECEIVER);
          when(restTemplate.exchange(eq(COUNT_URL), eq(HttpMethod.GET), any(HttpEntity.class), eq(Integer.class))).thenReturn(responseWithValue(0));
          notificationService.sendSapCustomerNotificationEmails();
                 verify(alluMailService, never()).sendEmail(anyListOf(String.class), anyString(), anyString());
        });
        it("should send email if customers without sap number", () -> {
          ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
          when(applicationProperties.getCustomerNotificationReceiverEmail()).thenReturn(CUSTOMER_NOTIFICATION_RECEIVER);
          when(restTemplate.exchange(eq(COUNT_URL), eq(HttpMethod.GET), any(HttpEntity.class), eq(Integer.class))).thenReturn(responseWithValue(7));
          notificationService.sendSapCustomerNotificationEmails();
          verify(alluMailService, times(1)).sendEmail(eq(Collections.singletonList(CUSTOMER_NOTIFICATION_RECEIVER)), eq(CUSTOMER_NOTIFICATION_SUBJECT), captor.capture());
          assertTrue(captor.getValue().contains(CUSTOMER_DOWNLOAD_URL));
        });
    });
    });
  }

  private ResponseEntity<Integer> responseWithValue(int i) {
    return new ResponseEntity<>(Integer.valueOf(i), HttpStatus.OK);
  }

}
