package fi.hel.allu.servicecore.service;

import com.greghaskins.spectrum.Spectrum;
import fi.hel.allu.model.domain.ChargeBasisEntry;
import fi.hel.allu.servicecore.config.ApplicationProperties;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;

import static com.greghaskins.spectrum.Spectrum.*;
import static org.junit.Assert.assertEquals;

@RunWith(Spectrum.class)
public class ChargeBasisServiceTest {

  @Mock
  private ApplicationProperties applicationProperties;
  @Mock
  private RestTemplate restTemplate;
  @Mock
  private ApplicationService applicationService;

  private ChargeBasisService chargeBasisService;

  {
    describe("ChargeBasis service", () -> {
      beforeEach(() -> {
        MockitoAnnotations.initMocks(this);
        chargeBasisService = new ChargeBasisService(applicationProperties, restTemplate, applicationService);
      });

      it("Retrieves charge basis entries", () -> {
        ChargeBasisEntry entry = new ChargeBasisEntry();
        entry.setText("Row row row your boat");
        Mockito.when(restTemplate.getForEntity(Mockito.anyString(), Mockito.eq(ChargeBasisEntry[].class), Mockito.anyInt()))
            .then(invocation -> new ResponseEntity<>(new ChargeBasisEntry[] { entry }, HttpStatus.OK));

        List<ChargeBasisEntry> result = chargeBasisService.getChargeBasis(99);

        assertEquals(1, result.size());
        assertEquals("Row row row your boat", result.get(0).getText());
      });

      it("Stores charge basis entries", () -> {
        Mockito.when(restTemplate.exchange(Mockito.anyString(), Mockito.eq(HttpMethod.PUT), Mockito.any(),
            Mockito.eq(ChargeBasisEntry[].class), Mockito.eq(99)))
            .then(invocation -> new ResponseEntity<>(new ChargeBasisEntry[] {}, HttpStatus.OK));

        chargeBasisService.setChargeBasis(99, Collections.emptyList());
        Mockito.verify(restTemplate).exchange(Mockito.anyString(), Mockito.eq(HttpMethod.PUT), Mockito.any(),
            Mockito.eq(ChargeBasisEntry[].class), Mockito.eq(99));
      });

      it("Retrieves sorted charge basis entries", () -> {
        ChargeBasisEntry entry1 = createEntryWithTag("tag1");
        ChargeBasisEntry entry1ReferringTo1 = createEntryWithReferredTag(entry1.getTag());
        ChargeBasisEntry entry2ReferringTo1 = createEntryWithReferredTag(entry1.getTag());

        ChargeBasisEntry entry2 = createEntryWithTag("tag2");
        ChargeBasisEntry entry2ReferringTo2 = createEntryWithReferredTag(entry2.getTag());

        Mockito.when(restTemplate.getForEntity(Mockito.anyString(), Mockito.eq(ChargeBasisEntry[].class), Mockito.anyInt()))
            .then(invocation -> new ResponseEntity<>(new ChargeBasisEntry[] {
                entry2,  entry2ReferringTo1, entry1ReferringTo1, entry1, entry2ReferringTo2
            }, HttpStatus.OK));

        List<ChargeBasisEntry> result = chargeBasisService.getChargeBasis(99);

        assertEquals(5, result.size());
        // Main level is not sorted (eg. entry2 is before entry1 in this test)
        assertEquals(entry2, result.get(0));
        assertEquals(entry2ReferringTo2, result.get(1));
        assertEquals(entry1, result.get(2));
        assertEquals(entry2ReferringTo1, result.get(3));
        assertEquals(entry1ReferringTo1, result.get(4));
      });
    });
  }

  private ChargeBasisEntry createEntryWithTag(String tag) {
    ChargeBasisEntry entry = new ChargeBasisEntry();
    entry.setTag(tag);
    return entry;
  }

  private ChargeBasisEntry createEntryWithReferredTag(String refersTo) {
    ChargeBasisEntry entry = new ChargeBasisEntry();
    entry.setReferredTag(refersTo);
    return entry;
  }
}
