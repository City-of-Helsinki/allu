package fi.hel.allu.model.common;

import fi.hel.allu.model.domain.Application;

public class ApplicationUtil {
  /**
   * Checks whether or not the application is a recurring rental
   * @param application to be checked
   * @return Whether or not the application is a recurring rental
   */
  public static boolean isRecurringRental(Application application) {
    return application.getKind().isTerrace() &&
      application.getRecurringEndTime() != null &&
      application.getEndTime() != null &&
      application.getRecurringEndTime().getYear() > application.getEndTime().getYear();
  }
}
