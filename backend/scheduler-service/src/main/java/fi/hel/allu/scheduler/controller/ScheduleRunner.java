package fi.hel.allu.scheduler.controller;

import fi.hel.allu.scheduler.service.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;


/**
 * The launcher class. Scheduled tasks are collected here.
 */
@Component
public class ScheduleRunner {
  private ApplicantReminderService applicantReminderService;
  private InvoicingService invoicingService;
  private SapCustomerService sapCustomerService;
  private SapCustomerNotificationService sapCustomerNotificationService;
  private SearchSynchService searchSyncService;

  @Autowired
  public ScheduleRunner(ApplicantReminderService applicantReminderService, InvoicingService invoicingService,
      SapCustomerService sapCustomerService, SapCustomerNotificationService sapCustomerNotificationService,
      SearchSynchService searchSynchService) {
    this.applicantReminderService = applicantReminderService;
    this.invoicingService = invoicingService;
    this.sapCustomerService = sapCustomerService;
    this.sapCustomerNotificationService = sapCustomerNotificationService;
    this.searchSyncService = searchSynchService;
  }

  @Scheduled(cron = "${applicantReminder.cronstring}")
  public void remindApplicants() {
    applicantReminderService.sendReminders();
  }

  @Scheduled(cron = "${invoice.cronstring}")
  public void sendInvoices() {
    invoicingService.sendInvoices();
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

}
