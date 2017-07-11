package fi.hel.allu.servicecore.service;

/**
 * Interface for providing identity information about current user.
 */
public interface IdentityServiceInterface {
  /**
   * Returns the username of the current user.
   *
   * @return the username of the current user.
   */
  String getUsername();
}
