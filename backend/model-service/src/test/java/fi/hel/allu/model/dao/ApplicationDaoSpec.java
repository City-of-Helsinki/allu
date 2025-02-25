package fi.hel.allu.model.dao;

import com.greghaskins.spectrum.Spectrum;
import com.greghaskins.spectrum.Variable;

import fi.hel.allu.common.domain.types.StatusType;
import fi.hel.allu.common.types.AttachmentType;
import fi.hel.allu.model.ModelApplication;
import fi.hel.allu.model.domain.Application;
import fi.hel.allu.model.domain.AttachmentInfo;
import fi.hel.allu.model.domain.Location;
import fi.hel.allu.model.testUtils.SpeccyTestBase;
import fi.hel.allu.model.testUtils.TestCommon;

import org.geolatte.geom.builder.DSL.Polygon2DToken;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.web.WebAppConfiguration;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.greghaskins.spectrum.dsl.specification.Specification.*;
import static org.geolatte.geom.builder.DSL.*;
import static org.junit.Assert.*;

@RunWith(Spectrum.class)
@SpringBootTest(classes = ModelApplication.class)
@WebAppConfiguration
public class ApplicationDaoSpec extends SpeccyTestBase {

  @Autowired
  private TestCommon testCommon;
  @Autowired
  private ApplicationDao applicationDao;
  @Autowired
  private AttachmentDao attachmentDao;
  @Autowired
  private LocationDao locationDao;

  private Application application;
  private AttachmentInfo attachmentInfo;
  private List<Location> applicationLocations;

  {
    describe("ApplicationDao.deleteNote", () -> {
      context("Application is not note", () -> {
        beforeEach(() -> {
          application = applicationDao.insert(testCommon.dummyOutdoorApplication("name", "owner"));
          assertNotNull(application.getId());
        });
        it("Can't be deleted", () -> {
          assertThrows(IllegalArgumentException.class).when(() -> {
            applicationDao.deleteNote(application.getId());
          });
        });
      });
      context("Application is NOTE", () -> {
        beforeEach(() -> {
          application = applicationDao.insert(testCommon.dummyNoteApplication("name", "owner"));
          assertNotNull(application.getId());
        });
        it("Can be deleted", () -> {
          applicationDao.deleteNote(application.getId());
        });

        context("Has attachment", () -> {
          beforeEach(() -> {
            attachmentInfo = new AttachmentInfo(null, 1, AttachmentType.ADDED_BY_CUSTOMER, "mimeTYpe", "Test.dat",
                "Test attachment", 2, ZonedDateTime.parse("2017-07-03T10:15:30+03:00[Europe/Helsinki]"), true);
            attachmentInfo = attachmentDao.insert(application.getId(), attachmentInfo, new byte[123]);
            assertNotNull(attachmentInfo.getId());
            assertTrue(attachmentDao.findById(attachmentInfo.getId()).isPresent());
          } );

          it("Deletion deletes attachments", () -> {
            applicationDao.deleteNote(application.getId());
            assertFalse(attachmentDao.findById(attachmentInfo.getId()).isPresent());
          });

          context("Other attachments exist", () -> {
            final Variable<Integer> otherApplicationId = new Variable<>();
            final Variable<Integer> otherAttachmentId = new Variable<>();

            beforeEach(() -> {
              otherApplicationId.set(testCommon.insertApplication("Other", "Other owner"));
              AttachmentInfo ai = new AttachmentInfo(null, 1, AttachmentType.ADDED_BY_HANDLER, "mimeTYpe",
                  "TestToo.dat", "Test attachment, too", 12,
                  ZonedDateTime.parse("2017-02-15T16:43:12+02:00[Europe/Helsinki]"), true);
              otherAttachmentId.set(attachmentDao.insert(otherApplicationId.get(), ai, new byte[123]).getId());
            });

            it("Deletion leaves other attachment", () -> {
              applicationDao.deleteNote(application.getId());
              assertTrue(attachmentDao.findById(otherAttachmentId.get()).isPresent());
            });
          });
        });

        context("Has locations", () -> {
          beforeEach(() -> {
            applicationLocations = locationDao.updateApplicationLocations(application.getId(),
                dummyLocations(application.getId()));
            applicationLocations.forEach(al -> assertEquals(application.getId(), al.getApplicationId()));
          });
          it("Deletion deletes locations", () -> {
            applicationDao.deleteNote(application.getId());
            applicationLocations.forEach(al -> assertNull(locationDao.findById(al.getId()).orElse(null)));
          });
        });
      });
    });
    describe("ApplicationDao.findAll", () -> {
      beforeEach(() -> {
        for (int i = 0; i < 15; ++i) {
          application = applicationDao.insert(testCommon.dummyOutdoorApplication("Dummy " + i, "Handler I" + i));
          assertNotNull(application.getId());
        }
        for (int i = 0; i < 5; ++i) {
          application = testCommon.dummyOutdoorApplication("Dummy ANONYMIZED " + i, "Handler II" + i);
          application.setStatus(StatusType.ANONYMIZED);
          application = applicationDao.insert(application);
          assertNotNull(application.getId());
        }
      });

      it("Can fetch 5 applications in ascending ID order", () -> {
        Page<Application> page = applicationDao.findAll(PageRequest.of(1, 5));
        assertEquals(5, page.getSize());
        List<Application> elements = page.getContent();
        assertEquals(5, elements.size());
        int prevId = Integer.MIN_VALUE;
        for (Application element : elements) {
          int id = element.getId();
          assertTrue(prevId < id);
          prevId = id;
        }
      });

      it("Can fetch all applications excluding applications with status ANONYMIZED", () -> {
        Page<Application> page = applicationDao.findAll(PageRequest.of(0, 100));
        assertEquals(100, page.getSize());
        List<Application> elements = page.getContent();
        for (Application element : elements) {
          StatusType unexpected = StatusType.ANONYMIZED;
          StatusType actual = element.getStatus();
          assertNotEquals(unexpected, actual);
        }
      });
    });
  }

  private List<Location> dummyLocations(Integer applicationId) {
    List<Location> locations = new ArrayList<>();
    Arrays.asList(Sq_0_0, Sq_1_1).forEach(sq -> {
      Location l = new Location();
      l.setGeometry(geometrycollection(3879, sq));
      l.setApplicationId(applicationId);
      l.setStartTime(ZonedDateTime.parse("2017-06-03T10:00:30+03:00[Europe/Helsinki]"));
      l.setEndTime(ZonedDateTime.parse("2017-06-09T17:00:30+03:00[Europe/Helsinki]"));
      l.setUnderpass(false);
      locations.add(l);
    });
    return locations;
  }

  private static final Polygon2DToken Sq_0_0 = polygon(ring(c(0, 0), c(0, 2), c(2, 2), c(2, 0), c(0, 0)));
  private static final Polygon2DToken Sq_1_1 = polygon(ring(c(1, 1), c(1, 3), c(3, 3), c(3, 1), c(1, 1)));
}
