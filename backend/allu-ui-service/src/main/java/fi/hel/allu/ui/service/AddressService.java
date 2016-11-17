package fi.hel.allu.ui.service;

import fi.hel.allu.common.exception.NoSuchEntityException;
import fi.hel.allu.ui.config.ApplicationProperties;
import fi.hel.allu.ui.domain.CoordinateJson;
import fi.hel.allu.ui.domain.PostalAddressJson;
import fi.hel.allu.ui.geocode.WfsFeatureCollection;
import fi.hel.allu.ui.util.WfsRestTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for querying Helsinki street addresses and for geocoding them.
 */
@Service
public class AddressService {

  private static final Logger logger = LoggerFactory.getLogger(AddressService.class);
  private ApplicationProperties applicationProperties;
  private WfsRestTemplate restTemplate;

  @Autowired
  public AddressService(ApplicationProperties applicationProperties, WfsRestTemplate restTemplate) {
    this.applicationProperties = applicationProperties;
    this.restTemplate = restTemplate;
  }

  /**
   * Geocodes given street address and number.
   *
   * @param   streetName    Name of the street to be geocoded.
   * @param   streetNumber  Street number.
   * @return  Coordinates of the given address.
   *
   * @throws NoSuchEntityException in case given address does not exist.
   */
  public CoordinateJson geocodeAddress(String streetName, int streetNumber) {

    HttpEntity<String> requestEntity = createRequestEntity();
    HttpEntity<String> wfsXmlEntity = restTemplate.exchange(
        applicationProperties.getStreetGeocodeUrl(),
        HttpMethod.GET,
        requestEntity,
        String.class,
        streetName,
        Integer.toString(streetNumber));

      WfsFeatureCollection wfsFeatureCollection = unmarshalWfs(wfsXmlEntity.getBody());
      if (wfsFeatureCollection.featureMember == null || wfsFeatureCollection.featureMember.size() < 1) {
        throw new NoSuchEntityException("Geocoded address not found");
      } else {
        // assuming that any address geocoding query returns at most a single address
        return new CoordinateJson(
            wfsFeatureCollection.featureMember.get(0).geocodedAddress.x, wfsFeatureCollection.featureMember.get(0).geocodedAddress.y);
      }
  }

  /**
   * List street addresses that start with the given street name.
   *
   * @param   partialStreetName   Street name to be searched.
   * @return  List of street addresses starting with the given string.
   */
  public List<PostalAddressJson> findMatchingStreet(String partialStreetName) {
    HttpEntity<String> requestEntity = createRequestEntity();
    HttpEntity<String> wfsXmlEntity = restTemplate.exchange(
        applicationProperties.getStreetSearchUrl(),
        HttpMethod.GET,
        requestEntity,
        String.class,
        partialStreetName);

    logger.debug("For street search {}, WFS service returned {}", partialStreetName, wfsXmlEntity.getBody());
    WfsFeatureCollection wfsFeatureCollection = unmarshalWfs(wfsXmlEntity.getBody());
    List<PostalAddressJson> addresses = new ArrayList<>();
    if (wfsFeatureCollection.featureMember != null && wfsFeatureCollection.featureMember.size() > 0) {
      addresses =
          wfsFeatureCollection.featureMember.stream().map(fm -> mapFeatureMemberToAddress(fm)).collect(Collectors.toList());
    }
    logger.debug("For street search {}, the following addresses were found {}", partialStreetName, addresses);
    return addresses;
  }

  private HttpEntity<String> createRequestEntity() {
    HttpHeaders httpHeaders = new HttpHeaders();

    String auth = applicationProperties.getWfsUsername() + ":" + applicationProperties.getWfsPassword();
    byte[] encodedAuth = Base64.getEncoder().encode(auth.getBytes());
    String authHeader = "Basic " + new String( encodedAuth );
    httpHeaders.set(HttpHeaders.AUTHORIZATION, authHeader);
    return new HttpEntity<>(httpHeaders);
  }

  private WfsFeatureCollection unmarshalWfs(String wfsXml) {
    try {
      JAXBContext jc = JAXBContext.newInstance(WfsFeatureCollection.class);
      Unmarshaller unmarshaller = jc.createUnmarshaller();
      return (WfsFeatureCollection) unmarshaller.unmarshal(new StringReader(wfsXml));
    } catch (JAXBException e) {
      logger.error("Unexpected exception while parsing WFS response\n{}", wfsXml);
      throw new RuntimeException(e);
    }
  }

  private PostalAddressJson mapFeatureMemberToAddress(WfsFeatureCollection.FeatureMember featureMember) {
    PostalAddressJson postalAddressJson = new PostalAddressJson();
    postalAddressJson.setCity(featureMember.geocodedAddress.city);
    postalAddressJson.setPostalCode(featureMember.geocodedAddress.postalCode);
    postalAddressJson.setStreetAddress(featureMember.geocodedAddress.streetName + " " + featureMember.geocodedAddress.streetNumber);
    return postalAddressJson;
  }
}
