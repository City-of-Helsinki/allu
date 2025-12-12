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
  private final CustomerService customerService;

  @Autowired
  public ScheduleRunner(
    ApplicantReminderService applicantReminderService,
    InvoicingService invoicingService,
    SapCustomerService sapCustomerService,
    SapCustomerNotificationService sapCustomerNotificationService,
    SearchSynchService searchSynchService,
    ApplicationStatusUpdaterService applicationStatusUpdaterService,
    CityDistrictUpdaterService cityDistrictUpdaterService,
    ApplicationProperties applicationProperties,
    CustomerService customerService
      ) {
    this.applicantReminderService = applicantReminderService;
    this.invoicingService = invoicingService;
    this.sapCustomerService = sapCustomerService;
    this.sapCustomerNotificationService = sapCustomerNotificationService;
    this.searchSyncService = searchSynchService;
    this.applicationStatusUpdaterService = applicationStatusUpdaterService;
    this.cityDistrictUpdaterService = cityDistrictUpdaterService;
    this.applicationProperties = applicationProperties;
    this.customerService = customerService;
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
      logger.info("Invoice sending job started.");
      try {
        invoicingService.sendInvoices();
        logger.info("Invoice sending job ended.");
      } catch (Exception e) {
        logger.error("Invoice sending job failed.", e);
      }
    }
  }

  @Scheduled(cron = "${customer.update.cronstring}")
  public void updateCustomers() {
    if (sapCustomerService.isUpdateEnabled()) {
      logger.info("Customer update job started.");
      try {
        sapCustomerService.updateCustomers();
        logger.info("Customer update job ended.");
      } catch (Exception e) {
        logger.error("Customer update job failed.", e);
      }
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

  /**
   * Scheduled method that identifies and stores deletable customers.
   *
   * This method is triggered at fixed intervals defined by the cron expression provided
   * in the configuration property 'deletable.customer.scan.cronstring'. The purpose of
   * this method is to scan for customers eligible for deletion and store them for further
   * processing. The actual logic is delegated to the corresponding service.
   */
  @Scheduled(cron = "${deletable.customer.scan.cronstring}")
  public void checkAndStoreDeletableCustomers() {
    logger.info("Deletable customer scan started");
    try {
      customerService.checkAndStoreDeletableCustomers();
      logger.info("Deletable customer scan completed");
    } catch (Exception e) {
      logger.error("Deletable customer scan failed", e);
    }
  }
}
