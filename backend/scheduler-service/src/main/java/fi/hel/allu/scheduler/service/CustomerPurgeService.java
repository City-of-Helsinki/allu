package fi.hel.allu.scheduler.service;

import fi.hel.allu.scheduler.config.ApplicationProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Service for permanently deleting inactive customers whose data retention period (5 years) has elapsed.
 *
 * The process:
 * 1. Fetch purgeable customer IDs page by page from model-service (cursor-based pagination)
 * 2. Split IDs into batches of BATCH_SIZE
 * 3. For each batch, call DELETE /customers/purge — failures are caught, logged, and skipped
 * 4. Abort early if the failure rate exceeds 20% (indicates a systemic problem)
 * 5. Log a structured summary of the run
 */
@Service
public class CustomerPurgeService {

  private static final Logger logger = LoggerFactory.getLogger(CustomerPurgeService.class);

  private static final int PAGE_SIZE = 500;
  private static final int BATCH_SIZE = 50;

  private final RestTemplate restTemplate;
  private final ApplicationProperties applicationProperties;
  private final AuthenticationService authenticationService;

  @Autowired
  public CustomerPurgeService(
      RestTemplate restTemplate,
      ApplicationProperties applicationProperties,
      AuthenticationService authenticationService) {
    this.restTemplate = restTemplate;
    this.applicationProperties = applicationProperties;
    this.authenticationService = authenticationService;
  }

  /**
   * Fetches all purgeable customer IDs and permanently deletes them in batches.
   * A failure in one batch does not prevent later batches from being processed
   * unless the failure rate exceeds 20% of the total attempted, which triggers an early abort.
   */
  public void purgeObsoleteCustomers() {
    logger.info("Customer purge job started");

    List<Integer> allIds = fetchAllPurgeableIds();

    if (allIds.isEmpty()) {
      logger.info("Customer purge job finished: no customers eligible for permanent deletion");
      return;
    }

    int totalEligible = allIds.size();
    logger.info("Found {} customers eligible for permanent deletion", totalEligible);

    int successCount = 0;
    int failedCount = 0;
    List<List<Integer>> batches = partition(allIds, BATCH_SIZE);

    for (List<Integer> batch : batches) {
      try {
        int deleted = purgeBatch(batch);
        successCount += deleted;
        logger.debug("Purged batch of {} customers ({} confirmed deleted)", batch.size(), deleted);
      } catch (Exception e) {
        failedCount += batch.size();
        String errorDetail;
        try {
          errorDetail = e.getMessage();
          if (e instanceof HttpStatusCodeException httpEx) {
            errorDetail += " | Response body: " + httpEx.getResponseBodyAsString();
          }
        } catch (Exception ignored) {
          errorDetail = "(error extracting exception message)";
        }
        logger.error("Failed to purge batch of {} customer IDs {}: {}", batch.size(), batch, errorDetail);

        // Failure-rate abort: if more than 20% of attempted customers have failed,
        // this signals a systemic problem rather than isolated data issues.
        int totalAttempted = successCount + failedCount;
        if (failedCount > 0 && (double) failedCount / totalAttempted >= 0.20) {
          logger.error(
            "Purge abort: failure rate exceeded threshold ({}/{} = {}%). Remaining batches will not be attempted.",
            failedCount, totalAttempted, (int) ((double) failedCount / totalAttempted * 100)
          );
          break;
        }
      }
    }

    logger.info("Customer purge job finished. Total eligible: {}, Successfully deleted: {}, Failed: {}",
      totalEligible, successCount, failedCount);
  }

  /**
   * Fetches all purgeable customer IDs using cursor/keyset pagination.
   * Tracks the last seen customer ID and passes it as the cursor for each subsequent page.
   * This is stable under concurrent deletions (unlike offset-based pagination).
   */
  private List<Integer> fetchAllPurgeableIds() {
    long startMs = System.currentTimeMillis();
    logger.info("fetchAllPurgeableIds started");

    List<Integer> allIds = new ArrayList<>();
    int lastSeenId = 0;

    while (true) {
      List<Integer> page = fetchPurgeablePage(lastSeenId, PAGE_SIZE);
      allIds.addAll(page);
      if (page.size() < PAGE_SIZE) {
        break;
      }
      lastSeenId = page.get(page.size() - 1);
    }

    logger.info("fetchAllPurgeableIds finished in {}ms, found {} IDs",
      System.currentTimeMillis() - startMs, allIds.size());
    return allIds;
  }

  /**
   * Fetches a single page of purgeable customer IDs using cursor-based pagination.
   *
   * @param afterId  cursor: return only customers with id greater than this value
   * @param pageSize maximum number of IDs to return
   */
  private List<Integer> fetchPurgeablePage(int afterId, int pageSize) {
    String url = UriComponentsBuilder
        .fromUriString(applicationProperties.getPurgeableCustomersUrl())
        .queryParam("pageSize", pageSize)
        .queryParam("afterId", afterId)
        .toUriString();

    List<Integer> result = restTemplate.exchange(
        url,
        HttpMethod.GET,
        new HttpEntity<>(authenticationService.createAuthenticationHeader()),
        new ParameterizedTypeReference<List<Integer>>() {}
    ).getBody();

    return result != null ? result : Collections.emptyList();
  }

  private int purgeBatch(List<Integer> ids) {
    Integer deleted = restTemplate.exchange(
        applicationProperties.getPurgeCustomersUrl(),
        HttpMethod.DELETE,
        new HttpEntity<>(ids, authenticationService.createAuthenticationHeader()),
        Integer.class
    ).getBody();

    return deleted != null ? deleted : 0;
  }

  /**
   * Splits a list into sublists of the given size.
   */
  private static <T> List<List<T>> partition(List<T> list, int batchSize) {
    List<List<T>> partitions = new ArrayList<>();
    for (int i = 0; i < list.size(); i += batchSize) {
      partitions.add(list.subList(i, Math.min(i + batchSize, list.size())));
    }
    return partitions;
  }
}
