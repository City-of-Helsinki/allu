package fi.hel.allu.scheduler.controller;

import fi.hel.allu.scheduler.service.ApplicantReminderService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;


/**
 * The launcher class. Scheduled tasks are collected here.
 */
@Component
public class ScheduleRunner {
  private ApplicantReminderService applicantReminderService;

  @Autowired
  public ScheduleRunner(ApplicantReminderService applicantReminderService) {
    this.applicantReminderService = applicantReminderService;
  }

  @Scheduled(cron = "${applicantReminder.cronstring}")
  public void remindApplicants() {
    applicantReminderService.sendReminders();
  }

}
