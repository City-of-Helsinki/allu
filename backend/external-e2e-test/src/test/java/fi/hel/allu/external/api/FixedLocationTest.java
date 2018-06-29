package fi.hel.allu.external.api;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.MultiValueMap;

import fi.hel.allu.common.domain.types.ApplicationKind;
import fi.hel.allu.external.domain.FixedLocationExt;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

@RunWith(SpringJUnit4ClassRunner.class)
public class FixedLocationTest extends BaseExternalApiTest {

  private static final String RESOURCE_PATH = "/fixedlocations";

  @Test
  public void shouldReturnFixedLocations() throws Exception {
    MultiValueMap<String, String> params = requestParam("applicationKind", ApplicationKind.BRIDGE_BANNER.name());
    ResponseEntity<List<FixedLocationExt>> response = restTemplate.exchange(
            getExtServiceUrl(RESOURCE_PATH, params),
            HttpMethod.GET,
            httpEntityWithHeaders(),
            new ParameterizedTypeReference<List<FixedLocationExt>>() {
    });
    assertFalse(response.getBody().isEmpty());
    assertEquals(HttpStatus.OK, response.getStatusCode());
  }

}
