package service;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.client.RestTemplate;

import com.greghaskins.spectrum.Spectrum;

import fi.hel.allu.common.domain.types.CustomerRoleType;
import fi.hel.allu.model.domain.Application;
import fi.hel.allu.model.domain.Contact;
import fi.hel.allu.model.domain.Customer;
import fi.hel.allu.model.domain.CustomerWithContacts;
import fi.hel.allu.scheduler.config.ApplicationProperties;
import fi.hel.allu.scheduler.service.AlluMailService;
import fi.hel.allu.scheduler.service.ApplicantReminderService;

import static com.greghaskins.spectrum.dsl.specification.Specification.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
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

  private static final List<String> APPLICANT_CONTACT_EMAILS = Arrays.asList("applicant.contact@mail.fi", "applicant.contact2@mail.fi", null);
  private static final List<String> CONTRACTOR_CONTACT_EMAILS = Arrays.asList("representative.contact@mail.fi", "representative.contact2@mail.fi");
  private static final List<String> REPRESENTATIVE_CONTACT_EMAILS = Arrays.asList("contractor.contact@mail.fi", "contractor.contact2@mail.fi", "");


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
            verify(alluMailService, never()).sendEmail(anyListOf(String.class), anyString(), anyString(), anyString(), anyList());
          });
          it("should not mark any reminders as sent", () -> {
            verify(restTemplate, never()).postForObject(eq(MARK_REMINDER_SENT_URL), any(), eq(Void.class));
          });
        });

        context("with one application expiring", () -> {
          final Integer APP_ID = 123;
          final String EMAIL = "ee-mail@mail.ee";
          final Application[] expiring = new Application[] { dummyApplication(APP_ID) };

          beforeEach(() -> {
            when(restTemplate.postForObject(eq(DEADLINE_CHECK_URL), anyObject(), eq(Application[].class)))
                    .thenReturn(expiring);
            applicantReminderService.sendReminders();
          });
          it("should send email to the applicant", () -> {
            ArgumentCaptor<List> captor = ArgumentCaptor.forClass(List.class);
            verify(alluMailService, times(1)).sendEmail(captor.capture(), anyString(), anyString(), anyString(), anyList());
            List<String> captured = captor.<List<String>> getValue();
            validateReminderReceivers(captured);
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

  private Application dummyApplication(Integer id) {
    Application a = mock(Application.class);
    when(a.getEndTime()).thenReturn(ZonedDateTime.now());
    when(a.getId()).thenReturn(id);
    when(a.getCustomersWithContacts()).thenReturn(dummyCustomersWithContacts());
    return a;
  }

  private void validateReminderReceivers(List<String> receivers) {
    List<String> expected = Stream.of(APPLICANT_CONTACT_EMAILS, REPRESENTATIVE_CONTACT_EMAILS, CONTRACTOR_CONTACT_EMAILS)
        .flatMap(Collection::stream)
        .filter(s -> StringUtils.isNotBlank(s))
        .collect(Collectors.toList());
    assertEquals(expected.size(), receivers.size());
    assertTrue(receivers.containsAll(expected));
  }

  private List<CustomerWithContacts> dummyCustomersWithContacts() {
    List<CustomerWithContacts> result = new ArrayList<>();
    result.add(new CustomerWithContacts(CustomerRoleType.APPLICANT, new Customer(), createContacts(APPLICANT_CONTACT_EMAILS)));
    result.add(new CustomerWithContacts(CustomerRoleType.REPRESENTATIVE, new Customer(), createContacts(REPRESENTATIVE_CONTACT_EMAILS)));
    result.add(new CustomerWithContacts(CustomerRoleType.CONTRACTOR, new Customer(), createContacts(CONTRACTOR_CONTACT_EMAILS)));
    return result;
  }

  private List<Contact> createContacts(List<String> emails) {
   return emails.stream().map(e ->  {
     Contact c = new Contact();
     c.setEmail(e);
     return c;
   }).collect(Collectors.toList());
  }
}
