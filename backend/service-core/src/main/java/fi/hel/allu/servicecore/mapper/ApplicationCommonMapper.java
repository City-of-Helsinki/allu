package fi.hel.allu.servicecore.mapper;

import fi.hel.allu.model.domain.PostalAddress;
import fi.hel.allu.servicecore.domain.PostalAddressJson;

public class ApplicationCommonMapper {
  public static PostalAddress createPostalAddressModel(PostalAddressJson postalAddressJson) {
    if (postalAddressJson != null) {
      return new PostalAddress(postalAddressJson.getStreetAddress(), postalAddressJson.getPostalCode(), postalAddressJson.getCity());
    } else {
      return null;
    }
  }

  public static PostalAddressJson createPostalAddressJson(PostalAddress postalAddress) {
    if (postalAddress != null) {
      PostalAddressJson postalAddressJson = new PostalAddressJson();
      postalAddressJson.setStreetAddress(postalAddress.getStreetAddress());
      postalAddressJson.setPostalCode(postalAddress.getPostalCode());
      postalAddressJson.setCity(postalAddress.getCity());
      return postalAddressJson;
    } else {
      return null;
    }
  }
}
