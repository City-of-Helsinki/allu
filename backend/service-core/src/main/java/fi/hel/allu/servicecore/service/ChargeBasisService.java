package fi.hel.allu.servicecore.service;

import fi.hel.allu.common.domain.types.ChargeBasisUnit;
import fi.hel.allu.model.domain.ChargeBasisEntry;
import fi.hel.allu.servicecore.config.ApplicationProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class ChargeBasisService {

  private final ApplicationProperties applicationProperties;
  private final RestTemplate restTemplate;

  @Autowired
  public ChargeBasisService(ApplicationProperties applicationProperties, RestTemplate restTemplate) {
    this.applicationProperties = applicationProperties;
    this.restTemplate = restTemplate;
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
  public List<ChargeBasisEntry> setChargeBasis(int applicationId, List<ChargeBasisEntry> chargeBasisEntries) {
    HttpEntity<List<ChargeBasisEntry>> requestEntity = new HttpEntity<>(chargeBasisEntries);
    ResponseEntity<ChargeBasisEntry[]> restResult = restTemplate.exchange(applicationProperties.setChargeBasisUrl(),
        HttpMethod.PUT, requestEntity, ChargeBasisEntry[].class, applicationId);
    return sortEntries(Arrays.asList(restResult.getBody()));
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

}
