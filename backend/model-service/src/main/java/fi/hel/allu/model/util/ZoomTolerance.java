package fi.hel.allu.model.util;

public class ZoomTolerance {

  public static int getTolerance(Integer minZoomLevel) {
    if (minZoomLevel == 1) {
      return 100;
    } else if (minZoomLevel == 4) {
      return 10;
    } else {
      return 3;
    }
  }
}
