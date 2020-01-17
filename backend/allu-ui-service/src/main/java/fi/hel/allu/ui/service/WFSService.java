package fi.hel.allu.ui.service;

import fi.hel.allu.common.wfs.WfsFilter;
import fi.hel.allu.common.wfs.WfsUtil;
import fi.hel.allu.servicecore.domain.UserJson;
import fi.hel.allu.servicecore.service.UserService;
import fi.hel.allu.servicecore.util.AsyncWfsRestTemplate;
import fi.hel.allu.ui.config.ApplicationProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.concurrent.Future;

@Service
public class WFSService {

  private static final String USERNAME = "kayttajanimi";

  private final ApplicationProperties applicationProperties;
  private final AsyncWfsRestTemplate restTemplate;
  private final UserService userService;

  @Autowired
  public WFSService(
      ApplicationProperties applicationProperties,
      AsyncWfsRestTemplate restTemplate,
      UserService userService) {
    this.applicationProperties = applicationProperties;
    this.restTemplate = restTemplate;
    this.userService = userService;
  }

  public Future<ResponseEntity<String>> getUserAreas() {
    UserJson user = userService.getCurrentUser();
    WfsFilter wfsFilter = new WfsFilter(USERNAME, user.getUserName());
    return sendRequest(wfsFilter);
  }

  private Future<ResponseEntity<String>> sendRequest(WfsFilter wfsFilter) {
    final HttpHeaders headers = WfsUtil.createAuthHeaders(
        applicationProperties.getWfsUsername(),
        applicationProperties.getWfsPassword());

    UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(applicationProperties.getWfsUserAreasUrl());
    URI uri = builder.queryParam(WfsFilter.GEOCODE_FILTER, wfsFilter.build())
        .buildAndExpand()
        .encode()
        .toUri();

    final HttpEntity requestEntity = new HttpEntity<>(headers);
    return restTemplate.exchange(
        uri,
        HttpMethod.GET,
        requestEntity,
        String.class);
  }
}
