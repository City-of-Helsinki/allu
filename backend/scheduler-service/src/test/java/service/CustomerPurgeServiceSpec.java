package service;

import com.greghaskins.spectrum.Spectrum;
import fi.hel.allu.scheduler.config.ApplicationProperties;
import fi.hel.allu.scheduler.service.AuthenticationService;
import fi.hel.allu.scheduler.service.CustomerPurgeService;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;

import static com.greghaskins.spectrum.Spectrum.*;
import static org.mockito.Mockito.*;

@RunWith(Spectrum.class)
public class CustomerPurgeServiceSpec {

  private static final String PURGEABLE_CUSTOMERS_URL = "http://model-service/customers/purgeable";
  private static final String PURGE_CUSTOMERS_URL     = "http://model-service/customers/purge";

  private CustomerPurgeService purgeService;

  @Mock
  private RestTemplate restTemplate;
  @Mock
  private ApplicationProperties applicationProperties;
  @Mock
  private AuthenticationService authenticationService;

  {
    describe("CustomerPurgeService", () -> {

      beforeEach(() -> {
        MockitoAnnotations.initMocks(this);

        when(applicationProperties.getPurgeableCustomersUrl()).thenReturn(PURGEABLE_CUSTOMERS_URL);
        when(applicationProperties.getPurgeCustomersUrl()).thenReturn(PURGE_CUSTOMERS_URL);
        when(authenticationService.createAuthenticationHeader()).thenReturn(new HttpHeaders());

        purgeService = new CustomerPurgeService(restTemplate, applicationProperties, authenticationService);
      });

      describe("purgeObsoleteCustomers", () -> {

        it("should do nothing when no purgeable customers are found", () -> {
          mockPurgeablePage(Collections.emptyList());

          purgeService.purgeObsoleteCustomers();

          // DELETE endpoint must never be called when there is nothing to purge
          verify(restTemplate, never()).exchange(
            eq(PURGE_CUSTOMERS_URL),
            eq(HttpMethod.DELETE),
            any(),
            eq(Integer.class)
          );
        });

        it("should call purge endpoint once for a single batch smaller than BATCH_SIZE", () -> {
          List<Integer> ids = List.of(1, 2, 3);
          mockPurgeablePage(ids);
          mockPurgeResponse(ids.size());

          purgeService.purgeObsoleteCustomers();

          verify(restTemplate, times(1)).exchange(
            eq(PURGE_CUSTOMERS_URL),
            eq(HttpMethod.DELETE),
            any(HttpEntity.class),
            eq(Integer.class)
          );
        });

        it("should split IDs into multiple batches when count exceeds BATCH_SIZE (50)", () -> {
          // 120 IDs → 3 batches: 50 + 50 + 20
          List<Integer> ids = IntStream.rangeClosed(1, 120).boxed().toList();
          mockPurgeablePage(ids);
          when(restTemplate.exchange(
            eq(PURGE_CUSTOMERS_URL),
            eq(HttpMethod.DELETE),
            any(HttpEntity.class),
            eq(Integer.class)
          )).thenReturn(new ResponseEntity<>(50, HttpStatus.OK))
            .thenReturn(new ResponseEntity<>(50, HttpStatus.OK))
            .thenReturn(new ResponseEntity<>(20, HttpStatus.OK));

          purgeService.purgeObsoleteCustomers();

          verify(restTemplate, times(3)).exchange(
            eq(PURGE_CUSTOMERS_URL),
            eq(HttpMethod.DELETE),
            any(HttpEntity.class),
            eq(Integer.class)
          );
        });

        it("should paginate through multiple pages when first page is full (PAGE_SIZE = 500)", () -> {
          // The first page returns exactly 500 items → service must fetch another page
          // Second page returns fewer than 500 → stop
          List<Integer> firstPage  = IntStream.rangeClosed(1, 500).boxed().toList();
          List<Integer> secondPage = List.of(501, 502, 503);

          mockPurgeablePageSequence(firstPage, secondPage);
          when(restTemplate.exchange(
            eq(PURGE_CUSTOMERS_URL),
            eq(HttpMethod.DELETE),
            any(HttpEntity.class),
            eq(Integer.class)
          )).thenReturn(new ResponseEntity<>(50, HttpStatus.OK));

          purgeService.purgeObsoleteCustomers();

          // 500 IDs → 10 batches of 50; 3 IDs → 1 batch → 11 purge calls total
          verify(restTemplate, times(11)).exchange(
            eq(PURGE_CUSTOMERS_URL),
            eq(HttpMethod.DELETE),
            any(HttpEntity.class),
            eq(Integer.class)
          );
        });

        it("should continue processing remaining batches when one batch fails", () -> {
          // 3 IDs → 3 batches of 1 each (use a tiny list so each is its own batch via the split logic)
          // We provide 110 IDs so we get at least 3 batches (50+50+10), the middle one fails
          List<Integer> ids = IntStream.rangeClosed(1, 110).boxed().toList();
          mockPurgeablePage(ids);

          when(restTemplate.exchange(
            eq(PURGE_CUSTOMERS_URL),
            eq(HttpMethod.DELETE),
            any(HttpEntity.class),
            eq(Integer.class)
          ))
            .thenReturn(new ResponseEntity<>(50, HttpStatus.OK))        // batch 1 — ok
            .thenThrow(new RestClientException("connection error"))      // batch 2 — fails
            .thenReturn(new ResponseEntity<>(10, HttpStatus.OK));        // batch 3 — ok

          purgeService.purgeObsoleteCustomers();

          // All 3 batches must have been attempted despite the failure in batch 2
          verify(restTemplate, times(3)).exchange(
            eq(PURGE_CUSTOMERS_URL),
            eq(HttpMethod.DELETE),
            any(HttpEntity.class),
            eq(Integer.class)
          );
        });

        it("should handle null body in purge response without throwing", () -> {
          List<Integer> ids = List.of(1, 2);
          mockPurgeablePage(ids);

          // Model service returns 200 but with a null body
          when(restTemplate.exchange(
            eq(PURGE_CUSTOMERS_URL),
            eq(HttpMethod.DELETE),
            any(HttpEntity.class),
            eq(Integer.class)
          )).thenReturn(new ResponseEntity<>(null, HttpStatus.OK));

          // Should not throw
          purgeService.purgeObsoleteCustomers();
        });

        it("should handle null body in purgeable page response without throwing", () -> {
          // GET /customers/purgeable returns 200 with null body
          when(restTemplate.exchange(
            contains("purgeable"),
            eq(HttpMethod.GET),
            any(HttpEntity.class),
            any(ParameterizedTypeReference.class)
          )).thenReturn(new ResponseEntity<>(null, HttpStatus.OK));

          purgeService.purgeObsoleteCustomers();

          // No purge calls expected — an empty result treated as no work to do
          verify(restTemplate, never()).exchange(
            eq(PURGE_CUSTOMERS_URL),
            eq(HttpMethod.DELETE),
            any(),
            eq(Integer.class)
          );
        });
      });
    });
  }

  // --- helpers ---

  /** Stubs GET /customers/purgeable to return the given list on the first call,
   *  then an empty list so pagination stops. */
  @SuppressWarnings("unchecked")
  private void mockPurgeablePage(List<Integer> ids) {
    when(restTemplate.exchange(
      contains("purgeable"),
      eq(HttpMethod.GET),
      any(HttpEntity.class),
      any(ParameterizedTypeReference.class)
    ))
      .thenReturn(new ResponseEntity<>(ids, HttpStatus.OK))
      .thenReturn(new ResponseEntity<>(Collections.emptyList(), HttpStatus.OK));
  }

  /** Stubs GET /customers/purgeable to return firstPage then secondPage then empty. */
  @SuppressWarnings("unchecked")
  private void mockPurgeablePageSequence(List<Integer> firstPage, List<Integer> secondPage) {
    when(restTemplate.exchange(
      contains("purgeable"),
      eq(HttpMethod.GET),
      any(HttpEntity.class),
      any(ParameterizedTypeReference.class)
    ))
      .thenReturn(new ResponseEntity<>(firstPage, HttpStatus.OK))
      .thenReturn(new ResponseEntity<>(secondPage, HttpStatus.OK))
      .thenReturn(new ResponseEntity<>(Collections.emptyList(), HttpStatus.OK));
  }

  private void mockPurgeResponse(int deleted) {
    when(restTemplate.exchange(
      eq(PURGE_CUSTOMERS_URL),
      eq(HttpMethod.DELETE),
      any(HttpEntity.class),
      eq(Integer.class)
    )).thenReturn(new ResponseEntity<>(deleted, HttpStatus.OK));
  }
}
