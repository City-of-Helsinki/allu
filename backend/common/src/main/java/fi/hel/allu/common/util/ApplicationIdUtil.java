package fi.hel.allu.common.util;

public class ApplicationIdUtil {
  private static final String VERSION_NUMBER_SEPARATOR = "-";

  public static String generateReplacingApplicationId(String applicationId) {
    int currentVersion = 1;
    final int separatorIndex = applicationId.lastIndexOf(VERSION_NUMBER_SEPARATOR);
    if (separatorIndex != -1) {
      // Replaced application has replaced some other application, parse and increase version number
      currentVersion = Integer.valueOf(applicationId.substring(separatorIndex + 1));
      applicationId = applicationId.substring(0, separatorIndex);
    }

    return applicationId + VERSION_NUMBER_SEPARATOR + (++currentVersion);
  }

  public static String getBaseApplicationId(String currentApplicationId) {
    int separatorIndex = currentApplicationId.lastIndexOf(VERSION_NUMBER_SEPARATOR);
    return separatorIndex >= 0
        ? currentApplicationId.substring(0, separatorIndex)
        : currentApplicationId;
  }
}
