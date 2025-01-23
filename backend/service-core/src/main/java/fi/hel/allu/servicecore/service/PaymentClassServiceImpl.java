package fi.hel.allu.servicecore.service;

import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

import fi.hel.allu.common.util.TimeUtil;
import fi.hel.allu.servicecore.domain.ApplicationJson;
import fi.hel.allu.servicecore.service.geocode.featuremember.FeatureClassMember;
import fi.hel.allu.servicecore.service.geocode.paymentclass.PaymentClassXml;
import fi.hel.allu.servicecore.service.geocode.paymentclass.PaymentClassXmlPost2022;
import fi.hel.allu.servicecore.service.geocode.paymentclass.PaymentClassXmlPost2025;
import org.geolatte.geom.*;
import org.geolatte.geom.crs.CrsId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import fi.hel.allu.common.wfs.WfsUtil;
import fi.hel.allu.servicecore.config.ApplicationProperties;
import fi.hel.allu.servicecore.domain.LocationJson;
import fi.hel.allu.servicecore.service.geocode.paymentclass.PaymentClassXmlPre2022;
import fi.hel.allu.servicecore.util.AsyncWfsRestTemplate;

import static java.lang.Math.max;

@Profile("!DEV")
@Service
public class PaymentClassServiceImpl extends AbstractWfsPaymentDataService implements PaymentClassService {


  private static final String FEATURE_TYPE_NAME = "Katutoiden_maksuluokat";
  private static final String FEATURE_PROPERTY_NAME = "maksuluokka";

  @Autowired
  public PaymentClassServiceImpl(ApplicationProperties applicationProperties, AsyncWfsRestTemplate restTemplate) {
    super(applicationProperties, restTemplate);
  }

  @Override
  public String getPaymentClass(LocationJson location, ApplicationJson applicationJson) {
    return executeWfsRequest(location, applicationJson);

  }


  @Override
  protected String parseResult(List<String> responses, ApplicationJson applicationJson, LocationJson location) {
    if (applicationJson.getStartTime().withZoneSameInstant(TimeUtil.HelsinkiZoneId).isAfter(ZonedDateTime.of(POST_2025_PAYMENT_DATE, TimeUtil.HelsinkiZoneId)))
      return parseResultPost2025(responses, location);
    else return parseResultPre2025(responses, applicationJson);
  }

  protected String parseResultPre2025(List<String> responses, ApplicationJson applicationJson) {
    String paymentClass = UNDEFINED;
    for (String response : responses) {
      final PaymentClassXml paymentClassXml = getPaymentClassPre2025(response, applicationJson);
      final List<FeatureClassMember> paymentClasses = paymentClassXml.getFeatureMemeber().stream()
          .sorted(Comparator.comparing(f -> f.getMaksuluokka().getPayment()))
          .collect(Collectors.toList());
      if (!paymentClasses.isEmpty()) {
        final String pc = paymentClasses.get(0).getMaksuluokka().getPayment();
        if (pc.compareTo(paymentClass) < 0) {
          paymentClass = pc;
        }
      }
    }
    return paymentClass;
  }

  protected String parseResultPost2025(List<String> responses, LocationJson location) {
    HashMap<String, List<String>> combinedMap = new HashMap<String, List<String>>();
    for (String response : responses) {
      final PaymentClassXmlPost2025 paymentClassXml = getPaymentClassPost2025(response);
      HashMap<String, List<String>> paymentMap = paymentClassXml.getPaymentLevels();
      for (String level : paymentMap.keySet()) {
        if (!combinedMap.containsKey(level)) combinedMap.put(level, new ArrayList<String>());
        combinedMap.get(level).addAll(paymentMap.get(level));
      }
    }
    HashMap<String, List<Polygon>> polygonMap = coordinatesToPolygons(combinedMap);
    HashMap<String, List<Double>> areaMap = polygonsToAreas(polygonMap);
    HashMap<String, Double> areaSumMap = sumAreas(areaMap);

    Double totalArea = location.getArea();
    if (totalArea == null || totalArea == 0.0) totalArea = computeLocationAreaLocally(location);

    return computePaymentLevel(totalArea, areaSumMap);
  }

  HashMap<String, List<Polygon>> coordinatesToPolygons(HashMap<String, List<String>> coordinateMap) {
    HashMap<String, List<Polygon>> polygonMap = new HashMap<>();

    for (String paymentLevel : coordinateMap.keySet()) {
      polygonMap.put(paymentLevel, new ArrayList<>());

      for (String coordinateString : coordinateMap.get(paymentLevel)) {

        List<Double> parsedCoordinates = new ArrayList<>();

        String[] coordinatePairs = coordinateString.split(" ");
        for (String coordinatePair : coordinatePairs) {
          String[] coordinates = coordinatePair.split(",");
          parsedCoordinates.add(Double.valueOf(coordinates[0]));
          parsedCoordinates.add(Double.valueOf(coordinates[1]));
        }

        double[] doublePrimitives = new double[parsedCoordinates.size()];
        int i = 0;
        for (Double element : parsedCoordinates) {
          doublePrimitives[i++] = element;
        }

        PointSequenceBuilder builder = PointSequenceBuilders.fixedSized(doublePrimitives.length / 2, DimensionalFlag.d2D, new CrsId("EPSG", 3879));
        for (i = 0; i < doublePrimitives.length; i += 2) builder.add(doublePrimitives[i], doublePrimitives[i+1]);
        PointSequence sequence = builder.toPointSequence();
        Polygon poly = new Polygon(sequence, null);
        polygonMap.get(paymentLevel).add(poly);
      }
    }

    return polygonMap;
  }

  private HashMap<String, List<Double>> polygonsToAreas(HashMap<String, List<Polygon>> polygonMap) {
    HashMap<String, List<Double>> areaMap = new HashMap<>();

    for (String level : polygonMap.keySet()) {
      areaMap.put(level, polygonMap.get(level).stream().map(Polygon::getArea).toList());
    }

    return areaMap;
  }

  private HashMap<String, Double> sumAreas(HashMap<String, List<Double>> areaMap) {
    HashMap<String, Double> resultMap = new HashMap<>();
    for (String level : areaMap.keySet())
      resultMap.put(level, areaMap.get(level).stream().reduce(0.0, Double::sum));
    return resultMap;
  }

  private Double computeLocationAreaLocally(LocationJson location) {

    return computeGeometryAreaLocally(location.getGeometry());
  }

  private Double computeGeometryAreaLocally(Geometry geometry) {
    switch (geometry.getGeometryType()) {
      case GEOMETRY_COLLECTION -> {
        double area = 0.0;
        GeometryCollection collection = (GeometryCollection)geometry;
        for (int i = 0; i < collection.getNumGeometries(); i++)
          area += computeGeometryAreaLocally(collection.getGeometryN(i));
        return area;
      }
      case POLYGON -> {
        return ((Polygon)geometry).getArea();
      }
      default -> {
        // don't know what to to with whatever this is?
        System.out.println("GeometryType was " + geometry.getGeometryType());
        return 0.0;
      }
    }
  }

  private String computePaymentLevel(double totalArea, HashMap<String, Double> areaSumMap) {

    // if there are no areas of other levels then it's just a 5
    if (areaSumMap.isEmpty()) return "5";

    double level5Area = totalArea;
    for (Double area : areaSumMap.values()) level5Area -= area;
    areaSumMap.put("5", max(level5Area, 0.0));

    // find highest level (lowest number) with at least 15 m2 of area, if any
    String highestLevel = null;
    for (String level : areaSumMap.keySet()) {
      if (areaSumMap.get(level) >= 15 && (highestLevel == null || highestLevel.compareTo(level) == 1)) {
        highestLevel = level;
      }
    }

     if (highestLevel != null) {
       return highestLevel;
     }

     // find the level with the highest area
    highestLevel = null;
    double highestArea = 0.0;

    for (String level: areaSumMap.keySet())
      if (highestArea < areaSumMap.get(level)) {
        highestArea = areaSumMap.get(level);
        highestLevel = level;
      }

    return highestLevel;
  }

  private PaymentClassXml getPaymentClassPre2025(String response, ApplicationJson applicationJson){

    if (applicationJson.getStartTime() == null) {
      return WfsUtil.unmarshalWfs(response, PaymentClassXmlPre2022.class);
    }
    ZonedDateTime startTimeHelsinkiZone = applicationJson.getStartTime().withZoneSameInstant(TimeUtil.HelsinkiZoneId);

    if (startTimeHelsinkiZone.isAfter(ZonedDateTime.of(POST_2022_PAYMENT_DATE, TimeUtil.HelsinkiZoneId)))
      return WfsUtil.unmarshalWfs(response, PaymentClassXmlPost2022.class);
    return WfsUtil.unmarshalWfs(response, PaymentClassXmlPre2022.class);
  }

  private PaymentClassXmlPost2025 getPaymentClassPost2025(String response) {
    return WfsUtil.unmarshalWfs(response, PaymentClassXmlPost2025.class);
  }

  @Override
  protected String getFeatureTypeNamePre2022() {
    return FEATURE_TYPE_NAME;
  }

  @Override
  protected String getFeaturePropertyName() {
    return FEATURE_PROPERTY_NAME;
  }

  @Override
  protected String getFeatureTypeNamePost2022() {
    return getFeatureTypeNamePre2022() + "_2022";
  }

  @Override
  protected String getFeatureTypeNamePost2025() {
    return getFeatureTypeNamePre2022() + "_2025";
  }

}
