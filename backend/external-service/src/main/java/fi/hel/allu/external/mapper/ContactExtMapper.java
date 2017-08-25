package fi.hel.allu.external.mapper;

import fi.hel.allu.external.domain.ContactExt;
import fi.hel.allu.external.domain.PostalAddressExt;
import fi.hel.allu.servicecore.domain.ContactJson;

public class ContactExtMapper {
  public static ContactJson mapContactJson(ContactExt contactExt) {
    ContactJson contactJson = new ContactJson();
    contactJson.setId(contactExt.getId());
    contactJson.setCustomerId(contactExt.getCustomerId());
    contactJson.setName(contactExt.getName());
    if (contactExt.getPostalAddress() != null) {
      contactJson.setStreetAddress(contactExt.getPostalAddress().getStreetAddress());
      contactJson.setPostalCode(contactExt.getPostalAddress().getPostalCode());
      contactJson.setCity(contactExt.getPostalAddress().getCity());
    }
    contactJson.setEmail(contactExt.getEmail());
    contactJson.setPhone(contactExt.getPhone());
    contactJson.setActive(true);
    return contactJson;
  }

  public static ContactExt mapContactExt(ContactJson contactJson) {
    ContactExt contactExt = new ContactExt();
    contactExt.setId(contactJson.getId());
    contactExt.setCustomerId(contactJson.getCustomerId());
    contactExt.setName(contactJson.getName());
    contactExt.setPostalAddress(new PostalAddressExt(contactJson.getStreetAddress(), contactJson.getPostalCode(), contactJson.getCity()));
    contactExt.setEmail(contactJson.getEmail());
    contactExt.setPhone(contactJson.getPhone());
    return contactExt;
  }
}
