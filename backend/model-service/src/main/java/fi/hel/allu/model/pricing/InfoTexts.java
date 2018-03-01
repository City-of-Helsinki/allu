package fi.hel.allu.model.pricing;

/**
 * Miscellanous information texts that should get into charge basis entries.
 * Collected here to avoid passing millions of strings as method parameters.
 */
public class InfoTexts {
  /**
   * Printable string for location's address
   */
  public String locationAddress;
  /**
   * Printable string for fixed location's address.
   */
  public String fixedLocation;
  /**
   * Printable string for event time period
   */
  public String eventPeriod;
  /**
   * Printable string for build+teardown periods
   */
  public String buildPeriods;
}
