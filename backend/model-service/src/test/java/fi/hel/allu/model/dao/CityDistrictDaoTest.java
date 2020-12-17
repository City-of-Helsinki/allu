package fi.hel.allu.model.dao;

import fi.hel.allu.model.ModelApplication;
import fi.hel.allu.model.domain.CityDistrict;
import fi.hel.allu.model.testUtils.TestCommon;
import org.geolatte.geom.Geometry;
import org.geolatte.geom.builder.DSL;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

import static org.geolatte.geom.builder.DSL.*;
import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = ModelApplication.class)
@WebAppConfiguration
@Transactional
public class CityDistrictDaoTest {

  @Autowired
  CityDistrictDao cityDistrictDao;

  @Autowired
  TestCommon testCommon;

  private static final DSL.Polygon2DToken square1 = polygon(ring(c(0, 0), c(0, 2), c(2, 2), c(2, 0), c(0, 0)));
  private static final DSL.Polygon2DToken square2 = polygon(ring(c(1, 1), c(1, 3), c(3, 3), c(3, 1), c(1, 1)));

  @Before
  public void setup() throws Exception {
    testCommon.deleteFrom("user_city_district");
    testCommon.deleteFrom("invoice_row");
    testCommon.deleteFrom("supervision_task");
    testCommon.deleteFrom("location");
    testCommon.deleteFrom("city_district");
  }

  @Test
  public void shouldInsertNonExisting() {
    CityDistrict cityDistrict = createCityDistrict(1, "districtName", geometrycollection(3879, square1));
    cityDistrictDao.upsert(Collections.singletonList(cityDistrict));
    assertEquals(1, cityDistrictDao.getCityDistricts().size());
  }

  @Test
  public void shouldUpdateExisting() {
    CityDistrict cityDistrict = createCityDistrict(1, "districtName", geometrycollection(3879, square1));
    cityDistrictDao.upsert(Collections.singletonList(cityDistrict));

    CityDistrict update = createCityDistrict(1, "districtName", geometrycollection(3879, square2));
    cityDistrictDao.upsert(Collections.singletonList(update));

    List<CityDistrict> districts = cityDistrictDao.getCityDistricts();
    assertEquals(1, districts.size());
    assertEquals(districts.get(0).getGeometry(), update.getGeometry());
  }

  private CityDistrict createCityDistrict(Integer districtId, String name, Geometry geometry) {
    CityDistrict cityDistrict = new CityDistrict();
    cityDistrict.setDistrictId(districtId);
    cityDistrict.setName(name);
    cityDistrict.setGeometry(geometry);
    return cityDistrict;
  }
}
