package fi.hel.allu.servicecore.service;

import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

import com.vividsolutions.jts.geom.TopologyException;
import fi.hel.allu.common.util.TimeUtil;
import fi.hel.allu.servicecore.domain.ApplicationJson;
import fi.hel.allu.servicecore.service.geocode.featuremember.FeatureClassMember;
import fi.hel.allu.servicecore.service.geocode.paymentclass.*;
import org.geolatte.geom.*;
import org.geolatte.geom.LinearRing;
import org.geolatte.geom.Polygon;
import org.geolatte.geom.crs.CrsId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import fi.hel.allu.common.wfs.WfsUtil;
import fi.hel.allu.servicecore.config.ApplicationProperties;
import fi.hel.allu.servicecore.domain.LocationJson;
import fi.hel.allu.servicecore.util.AsyncWfsRestTemplate;

import static java.lang.Math.max;

@Profile("!DEV")
@Service
public class PaymentClassServiceImpl extends AbstractWfsPaymentDataService implements PaymentClassService {

  private static final Logger logger = LoggerFactory.getLogger(PaymentClassServiceImpl.class);

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
    return isApplicationPost2025(applicationJson) ? parseResultPost2025(responses, location) : parseResultPre2025(responses, applicationJson);
  }

  protected boolean isApplicationPost2025(ApplicationJson applicationJson) {
    return applicationJson.getStartTime().withZoneSameInstant(TimeUtil.HelsinkiZoneId).isAfter(ZonedDateTime.of(POST_2025_PAYMENT_DATE, TimeUtil.HelsinkiZoneId));
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

  private List<PaymentClassXmlPost2025> responsesToPaymentClasses(List<String> responses) {
    return responses.stream().map(this::getPaymentClassPost2025).toList();
  }

  private List<HashMap<String, List<PolygonCoordinates>>> paymentClassesToPaymentMaps(List<PaymentClassXmlPost2025> paymentClasses) {
    return paymentClasses.stream().map(PaymentClassXmlPost2025::getPaymentLevels).toList();
  }

  private HashMap<String, List<PolygonCoordinates>> combineHashMaps(List<HashMap<String, List<PolygonCoordinates>>> hashMapList) {
    HashMap<String, List<PolygonCoordinates>> combinedMap = new HashMap<String, List<PolygonCoordinates>>();

    for (HashMap<String, List<PolygonCoordinates>> nextMap : hashMapList) {
      for (String level : nextMap.keySet()) {
        if (!combinedMap.containsKey(level)) combinedMap.put(level, new ArrayList<PolygonCoordinates>());
        combinedMap.get(level).addAll(nextMap.get(level));
      }
    }

    return combinedMap;
  }

  protected String parseResultPost2025(List<String> responses, LocationJson location) {
    return computePaymentLevel(getLocationArea(location), sumAreas(intersectionsToAreas(polygonsToIntersections(coordinatesToPolygons(combineHashMaps(paymentClassesToPaymentMaps(responsesToPaymentClasses(responses)))), location))));
  }

  LinearRing coordinatesToLinearRing(String coordinateString) {
    double[] doubleCoords = Arrays.stream(coordinateString.split(" ")).flatMap(str -> Arrays.stream(str.split(","))).mapToDouble(Double::parseDouble).toArray();

    PointSequenceBuilder builder = PointSequenceBuilders.fixedSized(doubleCoords.length / 2, DimensionalFlag.d2D, new CrsId("EPSG", 3879));
    for (int i = 0; i < doubleCoords.length; i += 2) builder.add(doubleCoords[i], doubleCoords[i+1]);
    PointSequence sequence = builder.toPointSequence();

    return new LinearRing(sequence, null);
  }

  Polygon polygonCoordinatesToPolygon(PolygonCoordinates polygonCoordinates) {
    LinearRing[] linearRings = new LinearRing[1 + polygonCoordinates.getInnerBoundaryCoordinates().size()];

    linearRings[0] = coordinatesToLinearRing(polygonCoordinates.getOuterBoundaryCoordinates());

    int i = 1;
    for (String innerBoundaryCoordinates : polygonCoordinates.getInnerBoundaryCoordinates())
      linearRings[i++] = coordinatesToLinearRing(innerBoundaryCoordinates);

    return new Polygon(linearRings);
  }

  HashMap<String, List<Polygon>> coordinatesToPolygons(HashMap<String, List<PolygonCoordinates>> coordinateMap) {
    HashMap<String, List<Polygon>> polygonMap = new HashMap<>();

    for (String paymentLevel : coordinateMap.keySet())
      polygonMap.put(paymentLevel, coordinateMap.get(paymentLevel).stream().map(this::polygonCoordinatesToPolygon).toList());

    return polygonMap;
  }

  private HashMap<String, List<Polygon>> polygonsToIntersections(HashMap<String, List<Polygon>> polygonMap, LocationJson location) {
    Geometry locationGeometry = location.getGeometry();
    List<Polygon> locationPolygons = new ArrayList<Polygon>();
    if (locationGeometry.getGeometryType() == GeometryType.POLYGON) locationPolygons.add((Polygon)locationGeometry);
    else {
      GeometryCollection collection = (GeometryCollection)locationGeometry;
      for (Geometry geom : collection)
        locationPolygons.add((Polygon)geom);
    }

    HashMap<String, List<Polygon>> resultMap = new HashMap<>();
    for (String level : polygonMap.keySet()) {
      resultMap.put(level, new ArrayList<Polygon>());
      for (Polygon polygon : polygonMap.get(level)) {
        for (Polygon locationPolygon : locationPolygons) {
          try {
            Geometry intersection = polygon.intersection(locationPolygon);
            switch (intersection.getGeometryType()) {
              case POLYGON -> {
                resultMap.get(level).add((Polygon) intersection);
              }
              case GEOMETRY_COLLECTION, MULTI_POLYGON -> {
                GeometryCollection collection = (GeometryCollection) intersection;
                for (int i = 0; i < collection.getNumGeometries(); i++)
                  resultMap.get(level).add((Polygon) collection.getGeometryN(i));
              }
              default -> {
                logger.warn("Intersection GeometryType was " + intersection.getGeometryType());
              }
            }
          }
          catch (TopologyException e) {
            logger.warn("Intersection operation threw TopologyException, ignoring polygon from WFS server. Exception was: " + e.getMessage());
          }
        }
      }
    }
    return resultMap;
  }

  private HashMap<String, List<Double>> intersectionsToAreas(HashMap<String, List<Polygon>> polygonMap) {
    HashMap<String, List<Double>> areaMap = new HashMap<>();

    for (String level : polygonMap.keySet())
      areaMap.put(level, polygonMap.get(level).stream().map(Polygon::getArea).toList());

    return areaMap;
  }

  private HashMap<String, Double> sumAreas(HashMap<String, List<Double>> areaMap) {
    HashMap<String, Double> resultMap = new HashMap<>();
    for (String level : areaMap.keySet())
      resultMap.put(level, areaMap.get(level).stream().reduce(0.0, Double::sum));

    return resultMap;
  }

  private Double getLocationArea(LocationJson location) {
    Double totalArea = location.getArea();
    if (totalArea == null || totalArea == 0.0) return computeGeometryAreaLocally(location.getGeometry());
    else return totalArea;
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
        // don't know what to do with whatever this is?
        logger.warn("GeometryType was " + geometry.getGeometryType());
        return 0.0;
      }
    }
  }

  private String computePaymentLevel(double totalArea, HashMap<String, Double> areaSumMap) {

    // if there are no areas of other levels then it's just a 5
    if (areaSumMap.isEmpty()) {
      logger.info("No areas with payment levels 1-4, choosing 5");
      return "5";
    }

    double level5Area = totalArea;
    for (Double area : areaSumMap.values()) level5Area -= area;
    areaSumMap.put("5", max(level5Area, 0.0));

    StringBuilder sb = new StringBuilder();
    sb.append("Payment level area map has these areas:");
    for (String level : areaSumMap.keySet())
      sb.append(" ").append(level).append(" - ").append(areaSumMap.get(level));
    logger.info(sb.toString());

    // find the highest level (lowest number) with at least 15 m2 of area, if any
    String highestLevel = null;
    for (String level : areaSumMap.keySet())
      if (areaSumMap.get(level) >= 15 && (highestLevel == null || highestLevel.compareTo(level) == 1))
        highestLevel = level;

     if (highestLevel != null)
       return highestLevel;

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
