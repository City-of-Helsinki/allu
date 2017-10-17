package fi.hel.allu.scheduler.controller;

import fi.hel.allu.scheduler.service.ApplicantReminderService;
import fi.hel.allu.scheduler.service.InvoicingService;

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

  @Autowired
  public ScheduleRunner(ApplicantReminderService applicantReminderService, InvoicingService invoicingService) {
    this.applicantReminderService = applicantReminderService;
    this.invoicingService = invoicingService;
  }

  @Scheduled(cron = "${applicantReminder.cronstring}")
  public void remindApplicants() {
    applicantReminderService.sendReminders();
  }

  @Scheduled(cron = "${invoice.cronstring}")
  public void sendInvoices() {
    invoicingService.sendInvoices();
  }
}
