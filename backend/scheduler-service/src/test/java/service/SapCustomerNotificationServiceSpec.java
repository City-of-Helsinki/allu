package service;


import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.greghaskins.spectrum.Spectrum;
import static com.greghaskins.spectrum.Spectrum.beforeEach;
import static com.greghaskins.spectrum.Spectrum.describe;
import static com.greghaskins.spectrum.Spectrum.it;
import fi.hel.allu.model.domain.Configuration;

import fi.hel.allu.scheduler.config.ApplicationProperties;
import fi.hel.allu.scheduler.service.AlluMailService;
import fi.hel.allu.scheduler.service.AuthenticationService;
import fi.hel.allu.scheduler.service.SapCustomerNotificationService;
import fi.hel.allu.model.domain.ConfigurationKey;
import fi.hel.allu.model.domain.ConfigurationType;
import static org.junit.Assert.assertTrue;
import org.mockito.ArgumentCaptor;

import static org.mockito.Mockito.*;
import org.mockito.MockitoAnnotations;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@RunWith(Spectrum.class)
public class SapCustomerNotificationServiceSpec {

  private static final String COUNT_URL = "count_url";
  private static final String CUSTOMER_DOWNLOAD_URL = "download_url";
  private static final String CUSTOMER_NOTIFICATION_SUBJECT = "Mail subject";
  private static final String CUSTOMER_NOTIFICATION_RECEIVER = "foo@bar.com";
  private static final String CUSTOMER_NOTIFICATION_EMAIL_URL = "/configurations/" + ConfigurationKey.CUSTOMER_NOTIFICATION_RECEIVER_EMAIL;

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
        List<Configuration> configs = new ArrayList<>();
        configs.add(new Configuration(ConfigurationType.EMAIL, ConfigurationKey.CUSTOMER_NOTIFICATION_RECEIVER_EMAIL, CUSTOMER_NOTIFICATION_RECEIVER));
        notificationService = new SapCustomerNotificationService(restTemplate, applicationProperties, alluMailService, authenticationService);
        when(authenticationService.getBearerToken()).thenReturn("");
        when(applicationProperties.getNrOfInvoiceRecipientsWithoutSapNumberUrl()).thenReturn(COUNT_URL);
        when(applicationProperties.getCustomerDownloadUrl()).thenReturn(CUSTOMER_DOWNLOAD_URL);
        when(applicationProperties.getCustomerNotificationMailSubject()).thenReturn(CUSTOMER_NOTIFICATION_SUBJECT);
        when(applicationProperties.getCustomerNotificationReceiverEmailsUrl()).thenReturn(CUSTOMER_NOTIFICATION_EMAIL_URL);
        when(restTemplate.exchange(eq(CUSTOMER_NOTIFICATION_EMAIL_URL), eq(HttpMethod.GET), any(), any(ParameterizedTypeReference.class))).thenReturn(new ResponseEntity<>(configs, HttpStatus.OK));
      });

      describe("sendNotifications", () -> {
        beforeEach(() -> {
          when(applicationProperties.getCustomerDownloadUrl()).thenReturn(CUSTOMER_DOWNLOAD_URL);
        });
        it("should not send email if receiver email empty", () -> {
          List<Configuration> configs = new ArrayList<>();
          when(restTemplate.exchange(eq(CUSTOMER_NOTIFICATION_EMAIL_URL), eq(HttpMethod.GET), any(), any(ParameterizedTypeReference.class))).thenReturn(new ResponseEntity<>(configs, HttpStatus.OK));
          notificationService.sendSapCustomerNotificationEmails();
          verify(alluMailService, never()).sendEmail(anyListOf(String.class), anyString(), anyString());
        });
        it("should not send email if no customers without sap number", () -> {
          List<Configuration> configs = new ArrayList<>();
          configs.add(new Configuration(ConfigurationType.EMAIL, ConfigurationKey.CUSTOMER_NOTIFICATION_RECEIVER_EMAIL, CUSTOMER_NOTIFICATION_RECEIVER));
          when(restTemplate.exchange(eq(CUSTOMER_NOTIFICATION_EMAIL_URL), eq(HttpMethod.GET), any(), any(ParameterizedTypeReference.class))).thenReturn(new ResponseEntity<>(configs, HttpStatus.OK));
          notificationService = new SapCustomerNotificationService(restTemplate, applicationProperties, alluMailService, authenticationService);
          when(restTemplate.exchange(eq(COUNT_URL), eq(HttpMethod.GET), any(HttpEntity.class), eq(Integer.class))).thenReturn(responseWithValue(0));
          notificationService.sendSapCustomerNotificationEmails();
                 verify(alluMailService, never()).sendEmail(anyListOf(String.class), anyString(), anyString());
        });
        it("should send email if customers without sap number", () -> {
          ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
          List<Configuration> configs = new ArrayList<>();
          when(restTemplate.exchange(eq(CUSTOMER_NOTIFICATION_EMAIL_URL), eq(HttpMethod.GET), any(), any(ParameterizedTypeReference.class))).thenReturn(new ResponseEntity<>(configs, HttpStatus.OK));
          configs.add(new Configuration(ConfigurationType.EMAIL, ConfigurationKey.CUSTOMER_NOTIFICATION_RECEIVER_EMAIL, CUSTOMER_NOTIFICATION_RECEIVER));
          notificationService = new SapCustomerNotificationService(restTemplate, applicationProperties, alluMailService, authenticationService);
          when(restTemplate.exchange(eq(COUNT_URL), eq(HttpMethod.GET), any(HttpEntity.class), eq(Integer.class))).thenReturn(responseWithValue(7));
          notificationService.sendSapCustomerNotificationEmails();
          verify(alluMailService, times(1)).sendEmail(eq(Collections.singletonList(CUSTOMER_NOTIFICATION_RECEIVER)), eq(CUSTOMER_NOTIFICATION_SUBJECT), captor.capture());
          assertTrue(captor.getValue().contains(CUSTOMER_DOWNLOAD_URL));
        });
    });
    });
  }

  private ResponseEntity<Integer> responseWithValue(int i) {
    return new ResponseEntity<>(i, HttpStatus.OK);
  }

}
