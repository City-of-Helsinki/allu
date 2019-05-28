package fi.hel.allu.model.deployment.citydistrict;

import com.querydsl.sql.Configuration;
import com.querydsl.sql.SQLQueryFactory;
import com.querydsl.sql.SQLTemplates;
import com.querydsl.sql.spatial.PostGISTemplates;
import fi.hel.allu.common.domain.serialization.helsinkixml.CityDistrictXml;
import fi.hel.allu.common.wfs.WfsUtil;
import fi.hel.allu.model.domain.CityDistrict;
import org.geolatte.geom.Polygon;
import org.geolatte.geom.builder.DSL;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static fi.hel.allu.QCityDistrict.cityDistrict;

/**
 * Converts WFS city district data to SQL, which can be used to insert the data to database.
 * To get the input XML file, download it from here:
 * http://kartta.hel.fi/ws/geoserver/helsinki/wfs?SERVICE=WFS&VERSION=1.0.0&REQUEST=GetFeature&TYPENAME=helsinki:Kaupunginosajako&SRSNAME=EPSG:3879
 */
public class CityDistrictReader {

  private static final String USAGE = "Required arguments missing.\n"
      + "Specify one input xml file and the name of output sql file!";

  private static SQLQueryFactory queryFactory;

  public static void main(String[] argv) throws IOException {
    if (argv.length < 2) {
      System.out.println(USAGE);
      return;
    } else {
      SQLTemplates templates = PostGISTemplates.builder().printSchema().build();
      Configuration configuration = new Configuration(templates);
      configuration.setUseLiterals(true);

      queryFactory = new SQLQueryFactory(configuration, (DataSource) null);
      List<String> sqlInserts = createSqlInserts(readCityDistricts(argv[0]));

      try (OutputStream output = Files.newOutputStream(Paths.get(argv[1]))) {
        for (String insert : sqlInserts) {
          output.write((insert + ";\n").getBytes());
        }
      }
    }
  }

  private static CityDistrictXml readCityDistricts(String path) throws IOException {
    byte[] encoded = Files.readAllBytes(Paths.get(path));
    String wfsXml = new String(encoded, "UTF-8");
    return WfsUtil.unmarshalWfs(wfsXml, CityDistrictXml.class);
  }

  private static List<String> createSqlInserts(CityDistrictXml districts) throws IOException {
    List<CityDistrictXml.FeatureMember> featureMembers = districts.featureMember;
    Collections.sort(featureMembers, (left, right) -> left.cityDistrict.districtId - right.cityDistrict.districtId);
    List<String> sqlInserts = featureMembers.stream().map(fm -> createSql(fm.cityDistrict)).collect(Collectors.toList());
    return sqlInserts;
  }

  private static String createSql(CityDistrictXml.HelsinkiKaupunginosajako xmlDistrict) {
    List<DSL.Vertex2DToken> vertex2DTokens =
        Arrays.stream(xmlDistrict.geometry.polygon.outerBoundary.linearRing.coordinates.split(" "))
        .map(c -> c.split(","))
        .map(xy -> DSL.c(Double.parseDouble(xy[0]), Double.parseDouble(xy[1])))
        .collect(Collectors.toList());
    Polygon polygon = DSL.polygon(3879, DSL.ring(vertex2DTokens.toArray(new DSL.Vertex2DToken[vertex2DTokens.size()])));
    CityDistrict district = new CityDistrict();
    district.setDistrictId(xmlDistrict.districtId);
    district.setName(xmlDistrict.districtId + " " + xmlDistrict.districtName.trim());
    district.setGeometry(polygon);
    String insert = queryFactory.insert(cityDistrict)
        .populate(district)
        .getSQL().get(0).getSQL();
    return insert;
  }
}
