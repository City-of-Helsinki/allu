package fi.hel.allu.model.domain;

/**
 * Model classes using postal address should implement this interface to allow easier relational mapping.
 */
public interface PostalAddressItem {
  PostalAddress getPostalAddress();
  void setPostalAddress(PostalAddress postalAddress);
}
