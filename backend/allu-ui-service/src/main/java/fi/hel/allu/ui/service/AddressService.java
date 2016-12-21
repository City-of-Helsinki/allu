package fi.hel.allu.ui.service;

import fi.hel.allu.common.exception.NoSuchEntityException;
import fi.hel.allu.common.wfs.WfsUtil;
import fi.hel.allu.ui.config.ApplicationProperties;
import fi.hel.allu.ui.domain.CoordinateJson;
import fi.hel.allu.ui.domain.PostalAddressJson;
import fi.hel.allu.ui.geocode.StreetAddressXml;
import fi.hel.allu.ui.util.WfsRestTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Service for querying Helsinki street addresses and for geocoding them.
 */
@Service
public class AddressService {

  private static final Logger logger = LoggerFactory.getLogger(AddressService.class);
  private static final String NUMBER_REGEX = "\\d+";
  private static final Pattern NUMBER_PATTERN = Pattern.compile(NUMBER_REGEX);
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

    HttpEntity<String> requestEntity =
        WfsUtil.createRequestEntity(applicationProperties.getWfsUsername(), applicationProperties.getWfsPassword());
    HttpEntity<String> wfsXmlEntity = restTemplate.exchange(
        applicationProperties.getStreetGeocodeUrl(),
        HttpMethod.GET,
        requestEntity,
        String.class,
        streetName,
        Integer.toString(streetNumber));

      StreetAddressXml streetAddressXml = WfsUtil.unmarshalWfs(wfsXmlEntity.getBody(), StreetAddressXml.class);
      if (streetAddressXml.featureMember == null || streetAddressXml.featureMember.size() < 1) {
        throw new NoSuchEntityException("Geocoded address not found");
      } else {
        // assuming that any address geocoding query returns at most a single address
        return new CoordinateJson(
            streetAddressXml.featureMember.get(0).geocodedAddress.x, streetAddressXml.featureMember.get(0).geocodedAddress.y);
      }
  }

  /**
   * List street addresses that start with the given street name.
   *
   * @param   partialStreetName   Street name to be searched.
   * @return  List of street addresses starting with the given string.
   */
  public List<PostalAddressJson> findMatchingStreet(String partialStreetName) {

    Optional<String> optionalStreetNumber = findStreetNumber(partialStreetName);
    String partialStreetNameNoNumber = partialStreetName.replaceAll(NUMBER_REGEX,"").trim();

    HttpEntity<String> requestEntity =
        WfsUtil.createRequestEntity(applicationProperties.getWfsUsername(), applicationProperties.getWfsPassword());
    HttpEntity<String> wfsXmlEntity = restTemplate.exchange(
        applicationProperties.getStreetSearchUrl(),
        HttpMethod.GET,
        requestEntity,
        String.class,
        partialStreetNameNoNumber);

    logger.debug("For street search {}, WFS service returned {}", partialStreetName, wfsXmlEntity.getBody());
    StreetAddressXml streetAddressXml = WfsUtil.unmarshalWfs(wfsXmlEntity.getBody(), StreetAddressXml.class);
    List<PostalAddressJson> addresses = new ArrayList<>();
    if (streetAddressXml.featureMember != null && streetAddressXml.featureMember.size() > 0) {
      addresses =
          streetAddressXml.featureMember.stream().map(fm -> mapFeatureMemberToAddress(fm)).collect(Collectors.toList());
    }
    logger.debug("For street search {}, the following addresses were found {}", partialStreetName, addresses);

    return addresses.stream().filter(a -> filterByStreetNumber(a, optionalStreetNumber)).distinct().collect(Collectors.toList());
  }

  private Optional<String> findStreetNumber(String partialStreetName) {
    Matcher matcher = NUMBER_PATTERN.matcher(partialStreetName);
    if (matcher.find()) {
      return Optional.of(matcher.group());
    } else {
      return Optional.empty();
    }
  }

  private boolean filterByStreetNumber(PostalAddressJson postalAddressJson, Optional<String> optionalStreetNumber) {
    if (optionalStreetNumber.isPresent()) {
      Optional<String> filteredStreetNumber = findStreetNumber(postalAddressJson.getStreetAddress());
      // street numbers that start with given number will return true. If filtered address does not contain street number at all,
      // it will be skipped
      return filteredStreetNumber.map(streetNumber -> streetNumber.startsWith(optionalStreetNumber.get())).orElse(false);
    } else {
      return true;
    }
  }

  private PostalAddressJson mapFeatureMemberToAddress(StreetAddressXml.FeatureMember featureMember) {
    PostalAddressJson postalAddressJson = new PostalAddressJson();
    postalAddressJson.setCity(featureMember.geocodedAddress.city);
    postalAddressJson.setPostalCode(featureMember.geocodedAddress.postalCode);
    postalAddressJson.setStreetAddress(featureMember.geocodedAddress.streetName + " " + featureMember.geocodedAddress.streetNumber);
    return postalAddressJson;
  }
}
