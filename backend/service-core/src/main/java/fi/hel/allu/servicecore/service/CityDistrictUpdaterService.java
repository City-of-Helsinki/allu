package fi.hel.allu.servicecore.service;

import fi.hel.allu.common.domain.serialization.helsinkixml.CityDistrictXml;
import fi.hel.allu.common.wfs.WfsUtil;
import fi.hel.allu.model.domain.CityDistrict;
import fi.hel.allu.servicecore.config.ApplicationProperties;
import fi.hel.allu.servicecore.util.AsyncWfsRestTemplate;
import fi.hel.allu.servicecore.util.WfsRestTemplate;
import org.geolatte.geom.Polygon;
import org.geolatte.geom.builder.DSL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

/**
 * Updates Allu's city districts from Helsinki wfs service
 */
@Service
public class CityDistrictUpdaterService {

  private final ApplicationProperties applicationProperties;
  private final WfsRestTemplate wfsRestTemplate;
  private final RestTemplate restTemplate;

  private static final int CITY_DISTRICT_SRID = 3879;
  private static final Logger logger = LoggerFactory.getLogger(CityDistrictUpdaterService.class);

  @Autowired
  public CityDistrictUpdaterService(ApplicationProperties applicationProperties,
                                    RestTemplate restTemplate,
                                    WfsRestTemplate wfsRestTemplate) {
    this.applicationProperties = applicationProperties;
    this.wfsRestTemplate = wfsRestTemplate;
    this.restTemplate = restTemplate;
  }

  public void update() {
    logger.info("Start updating city districts");
    try {
      ResponseEntity<String> response = sendRequest();
      CityDistrictXml cityDistrictsXml = parse(response.getBody());
      List<CityDistrict> cityDistricts = toCityDistricts(cityDistrictsXml);
      restTemplate.put(applicationProperties.getUpsertCityDistrictsUrl(), cityDistricts);
    } catch (Exception e) {
      logger.error("Updating city districts failed: " + e.getMessage(), e);
    }
    logger.info("Finished updating city districts");
  }

  private ResponseEntity<String> sendRequest() {
    return wfsRestTemplate.getForEntity(applicationProperties.getCityDistrictUpdateUrl(), String.class);
  }

  private CityDistrictXml parse(String responseXml) {
    return WfsUtil.unmarshalWfs(responseXml, CityDistrictXml.class);
  }

  private List<CityDistrict> toCityDistricts(CityDistrictXml districts) {
    return districts.featureMember.stream()
        .sorted((left, right) -> left.cityDistrict.districtId - right.cityDistrict.districtId)
        .map(featureMember -> toCityDistrict(featureMember.cityDistrict))
        .collect(Collectors.toList());
  }

  private CityDistrict toCityDistrict(CityDistrictXml.HelsinkiKaupunginosajako cityDistrictXml) {
    List<DSL.Vertex2DToken> vertex2DTokens =
        Arrays.stream(cityDistrictXml.geometry.polygon.outerBoundary.linearRing.coordinates.split(" "))
            .map(c -> c.split(","))
            .map(xy -> DSL.c(Double.parseDouble(xy[0]), Double.parseDouble(xy[1])))
            .collect(Collectors.toList());
    Polygon polygon = DSL.polygon(CITY_DISTRICT_SRID, DSL.ring(vertex2DTokens.toArray(new DSL.Vertex2DToken[vertex2DTokens.size()])));
    CityDistrict district = new CityDistrict();
    district.setDistrictId(cityDistrictXml.districtId);
    district.setName(cityDistrictXml.districtId + " " + cityDistrictXml.districtName.trim());
    district.setGeometry(polygon);
    return district;
  }
}
