package fi.hel.allu.scheduler.controller;

import fi.hel.allu.scheduler.config.ApplicationProperties;
import fi.hel.allu.scheduler.service.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


/**
 * The launcher class. Scheduled tasks are collected here.
 */
@Component
public class ScheduleRunner {
  private static final Logger logger = LoggerFactory.getLogger(ScheduleRunner.class);

  private final ApplicantReminderService applicantReminderService;
  private final InvoicingService invoicingService;
  private final SapCustomerService sapCustomerService;
  private final SapCustomerNotificationService sapCustomerNotificationService;
  private final SearchSynchService searchSyncService;
  private final ApplicationStatusUpdaterService applicationStatusUpdaterService;
  private final CityDistrictUpdaterService cityDistrictUpdaterService;
  private final ApplicationProperties applicationProperties;

  @Autowired
  public ScheduleRunner(ApplicantReminderService applicantReminderService, InvoicingService invoicingService,
      SapCustomerService sapCustomerService, SapCustomerNotificationService sapCustomerNotificationService,
      SearchSynchService searchSynchService,
      ApplicationStatusUpdaterService applicationStatusUpdaterService,
      CityDistrictUpdaterService cityDistrictUpdaterService,
      ApplicationProperties applicationProperties
      ) {
    this.applicantReminderService = applicantReminderService;
    this.invoicingService = invoicingService;
    this.sapCustomerService = sapCustomerService;
    this.sapCustomerNotificationService = sapCustomerNotificationService;
    this.searchSyncService = searchSynchService;
    this.applicationStatusUpdaterService = applicationStatusUpdaterService;
    this.cityDistrictUpdaterService = cityDistrictUpdaterService;
    this.applicationProperties = applicationProperties;
  }

  @EventListener(ApplicationReadyEvent.class)
  private void scheduleSearchDataSync() {
    final ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);
    scheduledExecutorService.schedule(
      () -> {
        try {
          syncSearchData();
        } catch (Exception e) {
          logger.error("Initial search data sync failed!", e);
        }
      },
        applicationProperties.getSearchSyncStartupDelay(),
        TimeUnit.SECONDS);
  }

  @Scheduled(cron = "${applicantReminder.cronstring}")
  public void remindApplicants() {
    applicantReminderService.sendReminders();
  }

  @Scheduled(cron = "${invoice.cronstring}")
  public void sendInvoices() {
    if (invoicingService.isInvoiceSendingEnabled()) {
      invoicingService.sendInvoices();
    }
  }

  @Scheduled(cron = "${customer.update.cronstring}")
  public void updateCustomers() {
    if (sapCustomerService.isUpdateEnabled()) {
      sapCustomerService.updateCustomers();
    }
  }

  @Scheduled(cron = "${customer.notification.cronstring}")
  public void sendCustomerNotifications() {
    sapCustomerNotificationService.sendSapCustomerNotificationEmails();
  }

  @Scheduled(cron = "${search.sync.cronstring}")
  public void syncSearchData() {
    searchSyncService.startSearchSync();
  }

  @Scheduled(cron = "${application.status.update.cronstring}")
  public void updateApplicationStatuses() {
    applicationStatusUpdaterService.updateApplicationStatuses();
    applicationStatusUpdaterService.archiveApplicationStatusesForTerminated();
  }

  @Scheduled(cron = "${cityDistricts.update.cronstring}")
  public void updateCityDistricts() {
    cityDistrictUpdaterService.updateCityDistricts();
  }

  @Scheduled(cron = "${anonymization.update.cronstring}")
  public void checkAnonymizableApplications() {
    applicationStatusUpdaterService.checkAnonymizableApplications();
  }
}
