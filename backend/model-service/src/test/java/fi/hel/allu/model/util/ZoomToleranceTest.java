package fi.hel.allu.model.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ZoomToleranceTest {

  @Test
  void getToleranceReturns50() {
    assertEquals(50, ZoomTolerance.getTolerance(1));
  }

  @Test
  void getToleranceReturns10() {
    assertEquals(10, ZoomTolerance.getTolerance(4));
  }

  @Test
  void getToleranceReturns3() {
    assertEquals(3, ZoomTolerance.getTolerance(6));
    assertEquals(3, ZoomTolerance.getTolerance(9));
  }

}
