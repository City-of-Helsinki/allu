package fi.hel.allu.model.domain;

import java.time.ZonedDateTime;

public record CustomerSapInfo(
  Integer id,
  String sapCustomerNumber,
  ZonedDateTime notificationSentAt
) { }
