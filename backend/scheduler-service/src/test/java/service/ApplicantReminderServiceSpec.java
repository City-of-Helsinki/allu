package service;

import com.greghaskins.spectrum.Spectrum;

import fi.hel.allu.common.domain.types.CustomerRoleType;
import fi.hel.allu.model.domain.Application;
import fi.hel.allu.model.domain.Customer;
import fi.hel.allu.model.domain.CustomerWithContacts;
import fi.hel.allu.scheduler.config.ApplicationProperties;
import fi.hel.allu.scheduler.service.AlluMailService;
import fi.hel.allu.scheduler.service.ApplicantReminderService;

import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.client.RestTemplate;

import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;

import static com.greghaskins.spectrum.dsl.specification.Specification.*;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

@RunWith(Spectrum.class)
public class ApplicantReminderServiceSpec {
  private ApplicantReminderService applicantReminderService;
  @Mock
  private RestTemplate restTemplate;
  @Mock
  private ApplicationProperties applicationProperties;
  @Mock
  private AlluMailService alluMailService;

  static final String DEADLINE_CHECK_URL = "deadline-check-url";
  static final String GET_APPLICANT_BY_ID_URL = "get-applicant-by-id-url";
  static final String MARK_REMINDER_SENT_URL = "mark-reminder-sent-url";

  {
    describe("Applicant reminder service", () -> {
      beforeEach(() -> {
        MockitoAnnotations.initMocks(this);
        applicantReminderService = new ApplicantReminderService(restTemplate, applicationProperties, alluMailService);
        when(applicationProperties.getDeadlineCheckUrl()).thenReturn(DEADLINE_CHECK_URL);
        when(applicationProperties.getMarkReminderSentUrl()).thenReturn(MARK_REMINDER_SENT_URL);
      });

      describe("sendReminders", ()-> {

        context("with no applications expiring", () -> {
          beforeEach(() -> {
            when(restTemplate.postForObject(eq(DEADLINE_CHECK_URL), anyObject(), eq(Application[].class)))
                    .thenReturn(new Application[0]);
            applicantReminderService.sendReminders();
          });
          it("should not send any email", () -> {
            verify(alluMailService, never()).sendEmail(anyListOf(String.class), anyString(), anyString());
          });
          it("should not mark any reminders as sent", () -> {
            verify(restTemplate, never()).postForObject(eq(MARK_REMINDER_SENT_URL), any(), eq(Void.class));
          });
        });

        context("with one application expiring", () -> {
          final Integer APP_ID = 123;
          final String EMAIL = "ee-mail@mail.ee";
          final Application[] expiring = new Application[] { dummyApplication(APP_ID, EMAIL) };

          beforeEach(() -> {
            when(restTemplate.postForObject(eq(DEADLINE_CHECK_URL), anyObject(), eq(Application[].class)))
                    .thenReturn(expiring);
            applicantReminderService.sendReminders();
          });
          it("should send email to the applicant", () -> {
            ArgumentCaptor<List> captor = ArgumentCaptor.forClass(List.class);
            verify(alluMailService, times(1)).sendEmail(captor.capture(), anyString(), anyString());
            List<String> captured = captor.<List<String>> getValue();
            assertEquals(1, captured.size());
            assertEquals(EMAIL, captured.get(0));
          });
          it("should mark reminder as sent for the application", () -> {
            ArgumentCaptor<List> captor = ArgumentCaptor.forClass(List.class);
            verify(restTemplate, times(1)).postForObject(eq(MARK_REMINDER_SENT_URL), captor.capture(), eq(Void.class));
            List<Integer> captured = captor.<List<Integer>> getValue();
            assertEquals(1, captured.size());
            assertEquals(APP_ID, captured.get(0));
          });
        });

      });

    });
  }

  private Application dummyApplication(Integer id, String email) {
    Application a = mock(Application.class);
    when(a.getEndTime()).thenReturn(ZonedDateTime.now());
    when(a.getId()).thenReturn(id);
    when(a.getCustomersWithContacts()).thenReturn(dummyCustomersWithContacts(email));
    return a;
  }

  private List<CustomerWithContacts> dummyCustomersWithContacts(String email) {
    Customer customer = new Customer();
    customer.setEmail(email);
    CustomerWithContacts cwc = new CustomerWithContacts(CustomerRoleType.APPLICANT, customer, Collections.emptyList());
    return Collections.singletonList(cwc);
  }
}
