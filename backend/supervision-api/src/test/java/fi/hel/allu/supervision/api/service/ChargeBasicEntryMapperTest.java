package fi.hel.allu.supervision.api.service;



import fi.hel.allu.common.domain.types.ChargeBasisUnit;
import fi.hel.allu.common.types.ChargeBasisType;
import fi.hel.allu.model.domain.ChargeBasisEntry;
import fi.hel.allu.supervision.api.domain.ChargeBasisEntryJson;
import fi.hel.allu.supervision.api.mapper.ChargeBasisEntryMapper;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


import java.util.ArrayList;
import java.util.List;


public class ChargeBasicEntryMapperTest {

  ChargeBasisEntryJson chargeBasisEntryJson;

  @BeforeEach
  public void setup() {
    chargeBasisEntryJson = new ChargeBasisEntryJson();
    List<String> explanations = new ArrayList<>();
    explanations.add("some explanations");
    chargeBasisEntryJson.setExplanation(explanations);
    chargeBasisEntryJson.setNetPrice(1000);
    chargeBasisEntryJson.setQuantity(0.0);
    chargeBasisEntryJson.setReferredTag("MightyTag");
    chargeBasisEntryJson.setText("Explain something interesting");
    chargeBasisEntryJson.setType(ChargeBasisType.ADDITIONAL_FEE);
    chargeBasisEntryJson.setUnit(ChargeBasisUnit.PIECE);
    chargeBasisEntryJson.setUnitPrice(1000);
    }

    @Test
  public void TestDataWasManuallySet(){
    ChargeBasisEntry updatedData = ChargeBasisEntryMapper.mapToModel(chargeBasisEntryJson);
      assertTrue(updatedData.getManuallySet(), "ManuallySet Should be true");
    }

  @Test
  public void TestUnitWasPercent(){
    chargeBasisEntryJson.setUnit(ChargeBasisUnit.PERCENT);
    ChargeBasisEntry updatedData = ChargeBasisEntryMapper.mapToModel(chargeBasisEntryJson);
    assertEquals(updatedData.getUnit(), ChargeBasisUnit.PERCENT);
    assertEquals(updatedData.getNetPrice(), 0);
    assertEquals(updatedData.getUnitPrice(), 0);
  }

  @Test
  public void TestTypeWasDiscountAndUnitWasPiece(){
    chargeBasisEntryJson.setType(ChargeBasisType.DISCOUNT);
    chargeBasisEntryJson.setUnit(ChargeBasisUnit.PIECE);
    ChargeBasisEntry updatedData = ChargeBasisEntryMapper.mapToModel(chargeBasisEntryJson);
    assertEquals(updatedData.getUnit(), ChargeBasisUnit.PIECE);
    assertEquals(updatedData.getType(), ChargeBasisType.DISCOUNT);
    assertEquals(updatedData.getQuantity(), 1);
  }





}
