package fi.hel.allu.scheduler.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import fi.hel.allu.scheduler.service.ApplicantReminderService;
import fi.hel.allu.scheduler.service.InvoicingService;
import fi.hel.allu.scheduler.service.SapCustomerService;


/**
 * The launcher class. Scheduled tasks are collected here.
 */
@Component
public class ScheduleRunner {
  private ApplicantReminderService applicantReminderService;
  private InvoicingService invoicingService;
  private SapCustomerService sapCustomerService;

  @Autowired
  public ScheduleRunner(ApplicantReminderService applicantReminderService, InvoicingService invoicingService,
      SapCustomerService sapCustomerService) {
    this.applicantReminderService = applicantReminderService;
    this.invoicingService = invoicingService;
    this.sapCustomerService = sapCustomerService;
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
}
