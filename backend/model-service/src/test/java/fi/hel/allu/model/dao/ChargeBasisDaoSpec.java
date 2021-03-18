package fi.hel.allu.model.dao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import fi.hel.allu.model.service.chargeBasis.UpdateChargeBasisService;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.web.WebAppConfiguration;

import com.greghaskins.spectrum.Spectrum;

import fi.hel.allu.common.domain.types.ChargeBasisUnit;
import fi.hel.allu.common.types.ChargeBasisType;
import fi.hel.allu.model.ModelApplication;
import fi.hel.allu.model.domain.ChargeBasisEntry;
import fi.hel.allu.model.testUtils.SpeccyTestBase;

import static com.greghaskins.spectrum.dsl.specification.Specification.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(Spectrum.class)
@SpringBootTest(classes = ModelApplication.class)
@WebAppConfiguration
public class ChargeBasisDaoSpec extends SpeccyTestBase {

  @Autowired
  private ChargeBasisDao chargeBasisDao;
  @Autowired
  private UpdateChargeBasisService updateChargeBasisService;

  {
    describe("ChargeBasisDao", () -> {
      beforeEach(() -> {
        testCommon.deleteAllData();
      });

      context("when DB is empty", ()-> {
        describe("getChargeBasis", () -> {
          it("should get empty result", () -> {
            List<ChargeBasisEntry> entries = chargeBasisDao.getChargeBasis(123);
            assertEquals(0, entries.size());
            });
        });
      });

      context("when application does not exist", () -> {
        describe("setChargeBasis", () -> {
          it("should throw error", () -> {
            assertThrows(RuntimeException.class)
                .when(() -> chargeBasisDao.setChargeBasis(modification(123, generateTestEntries(12, "Fail", null), true)));
          });
        });
      });

      context("when application exists", ()-> {
        final Supplier<Integer> appId1 = let(() -> testCommon.insertApplication("Hakemus", "Käsittelijä"));
        final Supplier<Integer> appId2 = let(() -> testCommon.insertApplication("Ansökning", "Handläggare"));

        describe("setChargeBasis", () -> {
          it("shouldn't throw error with valid entries",
              () -> chargeBasisDao.setChargeBasis(modification(appId1.get(), generateTestEntries(15, "test", null), true)));

          it("shouldn't throw error with empty list",
                  () -> chargeBasisDao.setChargeBasis(modification(appId2.get(), Collections.emptyList(), false)));
        });
        context("when setting two entries for two separate applications", () -> {
          final int NUM_1ST_ENTRIES = 12;
          final int NUM_2ND_ENTRIES = 23;
          beforeEach(() -> {
            chargeBasisDao.setChargeBasis(modification(appId1.get(),  generateTestEntries(NUM_1ST_ENTRIES, "First entries", null), true));
            chargeBasisDao.setChargeBasis(modification(appId2.get(), generateTestEntries(NUM_2ND_ENTRIES, "Second entries", null), true));
          });

          describe("getChargeBasis", () -> {
            final Supplier<List<ChargeBasisEntry>> first = let(() -> chargeBasisDao.getChargeBasis(appId1.get()));
            final Supplier<List<ChargeBasisEntry>> second = let(() -> chargeBasisDao.getChargeBasis(appId2.get()));

            it("Returns right number of entries", () -> {
              assertEquals(NUM_1ST_ENTRIES, first.get().size());
              assertEquals(NUM_2ND_ENTRIES, second.get().size());
            });

            it("Returns the right entries", () -> {
              checkTestEntries(first.get(), "First entries");
              checkTestEntries(second.get(), "Second entries");
            });
          });




        });
        describe("getModifications", () -> {
          final List<ChargeBasisEntry> testEntries = new ArrayList<>();
          beforeEach(() -> {
            clearEntries();
            testEntries.clear();
            chargeBasisDao.setChargeBasis(modification(appId1.get(), generateTestEntries(2, "manual", null), true));
            testEntries.addAll(chargeBasisDao.getChargeBasis(appId1.get()));
          });
          it("Should return ID of deleted entry", () -> {
            testEntries.remove(0);
            ChargeBasisModification modification = updateChargeBasisService.getModifications(appId1.get(), testEntries, true);
            assertEquals(1, modification.getEntryIdsToDelete().size());
            assertTrue(modification.getEntriesToUpdate().isEmpty());
            assertTrue(modification.getEntriesToInsert().isEmpty());

          });
          it("Should return inserted entry", () -> {
            testEntries.addAll(generateTestEntries(1, "added", null));
            ChargeBasisModification modification = updateChargeBasisService.getModifications(appId1.get(), testEntries, true);
            assertEquals(1, modification.getEntriesToInsert().size());
            assertTrue(modification.getEntryIdsToDelete().isEmpty());
            assertTrue(modification.getEntriesToUpdate().isEmpty());
          });
          it("Should return updated entry", () -> {
            ChargeBasisEntry modified = testEntries.get(0);
            modified.setUnitPrice(modified.getUnitPrice() + 2);
            ChargeBasisModification modification = updateChargeBasisService.getModifications(appId1.get(), testEntries, true);
            assertEquals(1, modification.getEntriesToUpdate().size());
            assertTrue(modification.getEntriesToInsert().isEmpty());
            assertTrue(modification.getEntryIdsToDelete().isEmpty());
          });
        });
        context("when setting both manual and calculated entries", () -> {
          final int NUM_MANUAL_ENTRIES = 12;
          final int NUM_CALCULATED_ENTRIES = 23;
          final String MANUAL_ENTRY_PREFIX = "Manual entries";
          final String CALCULATED_ENTRY_PREFIX = "Calculated entries";

          beforeEach(() -> {
            clearEntries();
            chargeBasisDao.setChargeBasis(modification(appId1.get(), generateTestEntries(NUM_MANUAL_ENTRIES, MANUAL_ENTRY_PREFIX, null),
                true));
            chargeBasisDao.setChargeBasis(modification(appId1.get(),
                generateTestEntries(NUM_CALCULATED_ENTRIES, CALCULATED_ENTRY_PREFIX, null),
                    false));
          });

          describe("getChargeBasis", () -> {
            it("Should return both manual and calculated entries", () -> {
              final List<ChargeBasisEntry> entries = chargeBasisDao.getChargeBasis(appId1.get());
              assertEquals(NUM_MANUAL_ENTRIES+NUM_CALCULATED_ENTRIES, entries.size());
              checkTestEntries(entries.stream().filter(r -> r.getManuallySet() == true).collect(Collectors.toList()),
                  MANUAL_ENTRY_PREFIX);
              checkTestEntries(entries.stream().filter(r -> r.getManuallySet() == false).collect(Collectors.toList()),
                  CALCULATED_ENTRY_PREFIX);
            });

            context("when clearing manual entries", () -> {
              it("should leave the calculated entries in place", () -> {
                ChargeBasisModification modification = updateChargeBasisService.getModifications(appId1.get(), Collections.emptyList(), true);
                chargeBasisDao.setChargeBasis(modification);
                final List<ChargeBasisEntry> entries = chargeBasisDao.getChargeBasis(appId1.get());
                assertEquals(NUM_CALCULATED_ENTRIES, entries.size());
                checkTestEntries(entries.stream().filter(r -> r.getManuallySet() == false).collect(Collectors.toList()),
                    CALCULATED_ENTRY_PREFIX);
              });
            });

            context("when clearing calculated entries", () -> {
              it("should leave the manual entries in place", () -> {
                ChargeBasisModification modification = updateChargeBasisService.getModifications(appId1.get(), Collections.emptyList(), false);
                chargeBasisDao.setChargeBasis(modification);
                final List<ChargeBasisEntry> entries = chargeBasisDao.getChargeBasis(appId1.get());
                assertEquals(NUM_MANUAL_ENTRIES, entries.size());
                checkTestEntries(entries.stream().filter(r -> r.getManuallySet() == false).collect(Collectors.toList()),
                    MANUAL_ENTRY_PREFIX);
              });
            });
          });

        context("when setting both manual and calculated entries", () -> {

          beforeEach(() -> {
            clearEntries();
            chargeBasisDao.setChargeBasis(modification(appId1.get(), generateTestEntries(1, "auto", null), false));
            List<ChargeBasisEntry> entries = chargeBasisDao.getChargeBasis(appId1.get());
            chargeBasisDao.setChargeBasis(modification(appId1.get(), generateTestEntries(1, "man", "ARF"), true));

            chargeBasisDao.setChargeBasis(modification(appId2.get(), generateTestEntries(1, "auto", null), false));
            entries = chargeBasisDao.getChargeBasis(appId2.get());
            chargeBasisDao.setChargeBasis(modification(appId2.get(), generateTestEntries(1, "man", entries.get(0).getTag()), true));

          });

            context("when clearing calculated entries", () -> {
              it("should remove dangling manual entries for correct application", () -> {
                ChargeBasisModification modification = updateChargeBasisService.getModifications(appId1.get(), Collections.emptyList(), false);
                chargeBasisDao.setChargeBasis(modification);
                List<ChargeBasisEntry> entries = chargeBasisDao.getChargeBasis(appId1.get());
                assertEquals(0, entries.size());
                entries = chargeBasisDao.getChargeBasis(appId2.get());
                assertEquals(2, entries.size());
              });
            });
          });

        });
      });
    });
  }

  private List<ChargeBasisEntry> generateTestEntries(int numEntries, String text, String referredTag) {
    List<ChargeBasisEntry> entries = new ArrayList<>();
    for (int r = 1; r <= numEntries; ++r) {
      ChargeBasisEntry entry = new ChargeBasisEntry();
      entry.setText(String.format("%s (%d)", text, r));
      entry.setTag(String.format("%s-tag-%d", text, r));
      if (referredTag != null) {
        entry.setReferredTag(referredTag);
      }
      entry.setUnitPrice(r * 100);
      entry.setNetPrice(r * 200);
      entry.setType(ChargeBasisType.CALCULATED);
      entry.setUnit(ChargeBasisUnit.SQUARE_METER);
      entry.setQuantity(r * 20.0);
      entries.add(entry);
    }
    return entries;
  }

  private void clearEntries() throws SQLException {
    testCommon.deleteFrom("charge_basis");
  }

  private void checkTestEntries(final Collection<ChargeBasisEntry> entries, String text) {
    int r = 1;
    for (ChargeBasisEntry entry : entries) {
      assertTrue(entry.getText().startsWith(text));
      assertEquals(String.format("%s-tag-%d", text, r), entry.getTag());
      assertEquals(r * 100, entry.getUnitPrice());
      assertEquals(r * 200, entry.getNetPrice());
      assertEquals(ChargeBasisUnit.SQUARE_METER, entry.getUnit());
      assertTrue(Math.abs(r * 20.0 - entry.getQuantity()) < 0.00001);
      ++r;
    }
  }

  private ChargeBasisModification modification(int applicationId, List<ChargeBasisEntry> newEntries, boolean manuallySet) {
    return new ChargeBasisModification(applicationId, newEntries, Collections.emptySet(), Collections.emptyMap(), manuallySet);
  }

}
