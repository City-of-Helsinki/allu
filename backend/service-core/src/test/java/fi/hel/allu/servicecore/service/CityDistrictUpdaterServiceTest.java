package fi.hel.allu.servicecore.service;

import fi.hel.allu.model.domain.CityDistrict;
import fi.hel.allu.servicecore.config.ApplicationProperties;
import fi.hel.allu.servicecore.util.WfsRestTemplate;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CityDistrictUpdaterServiceTest {

  @Mock
  private ApplicationProperties applicationProperties;
  @Mock
  private RestTemplate restTemplate;
  @Mock
  private WfsRestTemplate wfsRestTemplate;

  @Captor
  private ArgumentCaptor<List<CityDistrict>> cityDistrictsCaptor;

  private CityDistrictUpdaterService cityDistrictUpdaterService;

  @Before
  public void setup() throws Exception {
    cityDistrictUpdaterService = new CityDistrictUpdaterService(applicationProperties, restTemplate, wfsRestTemplate);
    byte[] encoded = Files.readAllBytes(Paths.get("src/test/java/fi/hel/allu/servicecore/service/kaupunginosat_pretty.xml"));
    String wfsXml = new String(encoded, "UTF-8");
    when(wfsRestTemplate.getForEntity(anyString(), any())).thenReturn(new ResponseEntity<>(wfsXml, HttpStatus.OK));
  }

  @Test
  public void shouldRequestUpdateWithFetchedCityDistricts() {
    cityDistrictUpdaterService.update();
    verify(restTemplate).put(anyString(), cityDistrictsCaptor.capture());
    List<CityDistrict> cityDistricts = cityDistrictsCaptor.getValue();
    CityDistrict haaga = cityDistricts.stream()
      .filter(cd -> cd.getName().trim().equals("29 HAAGA"))
      .findFirst()
      .orElseThrow(() -> new RuntimeException("Expected Haaga but not found"));

    assertEquals(Integer.valueOf(29), haaga.getDistrictId());
  }
}
