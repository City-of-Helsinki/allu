package fi.hel.allu.external.mapper;

import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import fi.hel.allu.common.domain.types.ApplicationType;
import fi.hel.allu.common.domain.types.CustomerRoleType;
import fi.hel.allu.common.types.PublicityType;
import fi.hel.allu.external.domain.NoteExt;
import fi.hel.allu.model.domain.ConfigurationKey;
import fi.hel.allu.servicecore.domain.*;
import fi.hel.allu.servicecore.service.ConfigurationService;
import fi.hel.allu.servicecore.service.ContactService;
import fi.hel.allu.servicecore.service.CustomerService;
import freemarker.cache.StrongCacheStorage;

@Component
public class NoteExtMapper {

  @Autowired
  private CustomerService customerService;

  @Autowired
  private ContactService contactService;

  @Autowired
  private ConfigurationService configurationService;

  public ApplicationJson mapExtNote(NoteExt note) {
    ApplicationJson applicationJson = new ApplicationJson();
    applicationJson.setType(ApplicationType.NOTE);
    applicationJson.setExtension(getExtension(note));
    applicationJson.setKind(note.getApplicationKind());
    applicationJson.setName(note.getName());
    applicationJson.setStartTime(note.getStartTime());
    applicationJson.setEndTime(note.getEndTime());
    applicationJson.setRecurringEndTime(getRecurringEndTime(note));
    applicationJson.setLocations(getLocations(note));
    applicationJson.setCustomersWithContacts(getDefaultApplicant());
    applicationJson.setDecisionPublicityType(PublicityType.PUBLIC);
    applicationJson.setNotBillable(true);
    return applicationJson;
  }

  private List<CustomerWithContactsJson> getDefaultApplicant() {
    String defaultContactId = configurationService.getSingleValue(ConfigurationKey.DEFAULT_CONTACT);
    return StringUtils.isNotBlank(defaultContactId) ?
        getApplicantByContactId(Integer.valueOf(defaultContactId))
        : Collections.emptyList();
  }

  private List<CustomerWithContactsJson> getApplicantByContactId(Integer contactId) {
    ContactJson contact = contactService.findById(Integer.valueOf(contactId));
    CustomerJson customer = customerService.findCustomerById(contact.getCustomerId());
    return Collections.singletonList(new CustomerWithContactsJson(CustomerRoleType.APPLICANT,
        customer, Collections.singletonList(contact)));
  }

  private List<LocationJson> getLocations(NoteExt note) {
    LocationJson location = new LocationJson();
    location.setGeometry(note.getGeometry());
    location.setStartTime(note.getStartTime());
    location.setEndTime(note.getEndTime());
    location.setPostalAddress(getPostalAddress(note));
    location.setAreaOverride(note.getArea());
    return Collections.singletonList(location);
  }


  private PostalAddressJson getPostalAddress(NoteExt note) {
    return Optional.ofNullable(note.getPostalAddress())
        .map(a -> new PostalAddressJson(a.getStreetAddressAsString(), a.getPostalCode(), a.getCity()))
        .orElse(null);
  }

  private ApplicationExtensionJson getExtension(NoteExt note) {
    NoteJson extension = new NoteJson();
    extension.setDescription(note.getDescription());
    return extension;
  }

  private ZonedDateTime getRecurringEndTime(NoteExt note) {
    return Optional.ofNullable(note.getRecurringEndYear())
        .map(recurringEndYear -> note.getEndTime().withYear(recurringEndYear))
        .orElse(null);
  }

}
