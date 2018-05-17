package fi.hel.allu.external.mapper;

import fi.hel.allu.external.domain.ContactExt;
import fi.hel.allu.servicecore.domain.ContactJson;

public class ContactExtMapper {
  public static ContactJson mapContactJson(ContactExt contactExt) {
    ContactJson contactJson = new ContactJson();
    contactJson.setId(contactExt.getId());
    contactJson.setName(contactExt.getName());
    if (contactExt.getPostalAddress() != null) {
      contactJson.setStreetAddress(contactExt.getPostalAddress().getStreetAddressAsString());
      contactJson.setPostalCode(contactExt.getPostalAddress().getPostalCode());
      contactJson.setCity(contactExt.getPostalAddress().getCity());
    }
    contactJson.setEmail(contactExt.getEmail());
    contactJson.setPhone(contactExt.getPhone());
    contactJson.setActive(true);
    return contactJson;
  }
}
