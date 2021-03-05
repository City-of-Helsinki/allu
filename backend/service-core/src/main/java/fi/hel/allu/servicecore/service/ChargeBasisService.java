package fi.hel.allu.servicecore.service;

import java.net.URI;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.BooleanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import fi.hel.allu.common.domain.ApplicationStatusInfo;
import fi.hel.allu.common.domain.types.ApplicationType;
import fi.hel.allu.common.domain.types.ChargeBasisUnit;
import fi.hel.allu.common.domain.types.StatusType;
import fi.hel.allu.common.exception.IllegalOperationException;
import fi.hel.allu.common.exception.NoSuchEntityException;
import fi.hel.allu.model.domain.ChargeBasisEntry;
import fi.hel.allu.servicecore.config.ApplicationProperties;

@Service
public class ChargeBasisService {

  private ApplicationService applicationService;
  private final ApplicationProperties applicationProperties;
  private final RestTemplate restTemplate;
  Logger logger = LoggerFactory.getLogger(ChargeBasisService.class);
  @Autowired
  public ChargeBasisService(ApplicationProperties applicationProperties, RestTemplate restTemplate, ApplicationService applicationService) {
    this.applicationProperties = applicationProperties;
    this.restTemplate = restTemplate;
    this.applicationService = applicationService;
  }

  /**
   * Get the charge basis entries for an application
   *
   * @param applicationId the application ID
   * @return the charge basis entries for the application
   */
  public List<ChargeBasisEntry> getChargeBasis(int applicationId) {
    return sortEntries(loadChargeBasis(applicationId));
  }

  public List<ChargeBasisEntry> getInvoicableChargeBasis(int applicationId) {
    return sortEntries(loadChargeBasis(applicationId).stream()
        .filter(e -> e.isInvoicable() && (e.getNetPrice() != 0 || e.getUnit() == ChargeBasisUnit.PERCENT))
        .collect(Collectors.toList()));
  }

  public List<ChargeBasisEntry> getSingleInvoiceChargeBasis(int applicationId) {
    return sortEntries(Arrays.asList(restTemplate.getForEntity(
        applicationProperties.getSingleInvoiceChargeBasisUrl(),
        ChargeBasisEntry[].class, applicationId).getBody()));
  }

  public int getInvoicableSumForLocation(int applicationId, int locationId) {
    return restTemplate.getForObject(applicationProperties.getLocationInvoicableSumUrl(), Integer.class, applicationId, locationId);
  }

  public List<ChargeBasisEntry> getUnlockedAndInvoicableChargeBasis(int applicationId) {
    return sortEntries(loadChargeBasis(applicationId).stream()
        .filter(e -> !Boolean.TRUE.equals(e.getLocked()) && e.isInvoicable())
        .collect(Collectors.toList()));
  }

  /**
   * Set the manual charge basis entries for an application
   *
   * @param applicationId the application ID
   * @param chargeBasisEntries the charge basis entries to store. Only entries
   *          that are marked as manually set will be used
   * @return the new charge basis entries for the application
   */
  public List<ChargeBasisEntry> setChargeBasis(Integer applicationId, List<ChargeBasisEntry> chargeBasisEntries) {
    HttpEntity<List<ChargeBasisEntry>> requestEntity = new HttpEntity<>(chargeBasisEntries);
    ResponseEntity<ChargeBasisEntry[]> restResult = restTemplate.exchange(applicationProperties.getChargeBasisUrl(),
        HttpMethod.PUT, requestEntity, ChargeBasisEntry[].class, applicationId);
    return sortEntries(Arrays.asList(restResult.getBody()));
  }

  public ChargeBasisEntry insertEntry(Integer applicationId, ChargeBasisEntry entry) {
    validateChargeBasisUpdateForApplicationAllowed(applicationId);
    return restTemplate.exchange(applicationProperties.getChargeBasisUrl(), HttpMethod.POST,
        new HttpEntity<>(entry), ChargeBasisEntry.class, applicationId).getBody();
  }

  public ChargeBasisEntry updateEntry(Integer applicationId, Integer entryId, ChargeBasisEntry entry) {
    ChargeBasisEntry existingEntry = getEntry(applicationId, entryId);
    validateModificationAllowed(applicationId, existingEntry);
    updateEntryData(entry, existingEntry);
    return restTemplate.exchange(applicationProperties.getChargeBasisEntryUrl(), HttpMethod.PUT,
        new HttpEntity<>(entry), ChargeBasisEntry.class, applicationId, entryId).getBody();
  }

  public void deleteEntry(Integer applicationId, Integer entryId) {
    ChargeBasisEntry existingEntry = getEntry(applicationId, entryId);
    validateModificationAllowed(applicationId, existingEntry);
    restTemplate.delete(applicationProperties.getChargeBasisEntryUrl(), applicationId, entryId);
  }

  public ChargeBasisEntry[] recalculateEntries(Integer applicationId) {
    return restTemplate.exchange(applicationProperties.getChargeBasisEntriesRecalculateUrl(), HttpMethod.PUT,
      new HttpEntity<>(null), ChargeBasisEntry[].class, applicationId).getBody();
  }

  public ChargeBasisEntry getEntry(int applicationId, int entryId) {
    return restTemplate.getForObject(applicationProperties.getChargeBasisEntryUrl(), ChargeBasisEntry.class, applicationId, entryId);
  }

  /**
   * Set whether charge basis entry is invoicable or not
   *
   */
  public ChargeBasisEntry setInvoicable(int applicationId, int entryId, boolean invoicable) {
    Map<String, Integer> pathVariables = new HashMap<>();
    pathVariables.put("id", applicationId);
    pathVariables.put("entryId", entryId);

    URI uri = UriComponentsBuilder.fromHttpUrl(applicationProperties.getSetChargeBasisInvoicableUrl())
        .queryParam("invoicable", invoicable)
        .buildAndExpand(pathVariables).toUri();

    return restTemplate.exchange(uri, HttpMethod.PUT, new HttpEntity<>(null), ChargeBasisEntry.class).getBody();
  }

  private List<ChargeBasisEntry> loadChargeBasis(int applicationId) {
    return Arrays.asList(restTemplate.getForEntity(
        applicationProperties.getChargeBasisUrl(), ChargeBasisEntry[].class, applicationId).getBody());
  }

  /**
   * Get sorted charge basis entries for an application
   * Entries are sorted so that entries referring to other entry come right after the entry they are referring to
   *
   * @param entries charge basis entries to sort
   * @return the charge basis entries for the application
   */
  private List<ChargeBasisEntry> sortEntries(List<ChargeBasisEntry> entries) {
    Predicate<ChargeBasisEntry> refersToEntry = e -> e.getReferredTag() != null;

    Stream<ChargeBasisEntry> referred = entries.stream().filter(refersToEntry.negate());

    Map<String, List<ChargeBasisEntry>> referring = entries.stream()
        .filter(refersToEntry)
        .collect(Collectors.groupingBy(ChargeBasisEntry::getReferredTag));

    return referred
        .map(ref -> concat(ref, referring.get(ref.getTag())))
        .flatMap(List::stream)
        .collect(Collectors.toList());
  }

  private <T> List<T> concat(T item, List<T> items) {
    List<T> result = new ArrayList<>();
    result.add(item);
    if (items != null) {
      result.addAll(items);
    }
    return result;
  }

  public void validateInvoicableChangeAllowed(Integer applicationId, ChargeBasisEntry existingEntry) {
    validateChargeBasisUpdateForApplicationAllowed(applicationId);
    if (BooleanUtils.isTrue(existingEntry.getLocked())) {
      throw new IllegalOperationException("chargebasis.update.forbidden");
    }
  }

  public void validateModificationAllowed(Integer applicationId, ChargeBasisEntry existingEntry) {
    validateChargeBasisUpdateForApplicationAllowed(applicationId);
    if (BooleanUtils.isTrue(existingEntry.getLocked()) || BooleanUtils.isNotTrue(existingEntry.getManuallySet())) {
      logger.info("Action is forbiden either entry is locked : " +existingEntry.getLocked()
        + ", or It wasn't manuallly set: " +  existingEntry.getManuallySet() );
      throw new IllegalOperationException("chargebasis.update.forbidden");
    }
  }

  private void validateChargeBasisUpdateForApplicationAllowed(Integer applicationId) {
    ApplicationStatusInfo applicationInfo = applicationService.getApplicationStatus(applicationId);
    if (!applicationInfo.getStatus().isBeforeDecisionMaking() && !chargeBasisUpdateAllowedAfterDecisionMaking(applicationInfo)) {
      throw new IllegalOperationException("chargebasis.update.forbidden");
    }
  }

  private boolean chargeBasisUpdateAllowedAfterDecisionMaking(ApplicationStatusInfo applicationInfo) {
    // Invoicing of excavation announcement and area rental allowed in decision and operational condition status
    return (applicationInfo.getType() == ApplicationType.EXCAVATION_ANNOUNCEMENT || applicationInfo.getType() == ApplicationType.AREA_RENTAL)
        && (applicationInfo.getStatus() == StatusType.DECISION || applicationInfo.getStatus() == StatusType.OPERATIONAL_CONDITION );
  }

  /**
   * Set values for fields that can be updated by user
   */
  private void updateEntryData(ChargeBasisEntry entry, ChargeBasisEntry existingEntry) {
    existingEntry.setType(entry.getType());
    existingEntry.setUnit(entry.getUnit());
    existingEntry.setQuantity(entry.getQuantity());
    existingEntry.setText(entry.getText());
    existingEntry.setExplanation(entry.getExplanation());
    existingEntry.setUnitPrice(entry.getUnitPrice());
    existingEntry.setNetPrice(entry.getNetPrice());
    existingEntry.setReferredTag(entry.getReferredTag());
  }

}
