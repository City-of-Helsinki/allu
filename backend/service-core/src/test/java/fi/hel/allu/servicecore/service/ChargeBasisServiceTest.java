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

  private ChargeBasisService chargeBasisService;

  {
    describe("ChargeBasis service", () -> {
      beforeEach(() -> {
        MockitoAnnotations.initMocks(this);
        chargeBasisService = new ChargeBasisService(applicationProperties, restTemplate);
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
    });
  }
}
