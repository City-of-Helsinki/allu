package fi.hel.allu.common.domain;

public class RequiredTasks {
  private boolean compactionAndBearingCapacityMeasurement;
  private boolean qualityAssuranceTest;

  public RequiredTasks() {
  }

  public RequiredTasks(boolean compactionAndBearingCapacityMeasurement, boolean qualityAssuranceTest) {
    this.compactionAndBearingCapacityMeasurement = compactionAndBearingCapacityMeasurement;
    this.qualityAssuranceTest = qualityAssuranceTest;
  }

  public boolean getCompactionAndBearingCapacityMeasurement() {
    return compactionAndBearingCapacityMeasurement;
  }

  public void setCompactionAndBearingCapacityMeasurement(boolean compactionAndBearingCapacityMeasurement) {
    this.compactionAndBearingCapacityMeasurement = compactionAndBearingCapacityMeasurement;
  }

  public boolean getQualityAssuranceTest() {
    return qualityAssuranceTest;
  }

  public void setQualityAssuranceTest(boolean qualityAssuranceTest) {
    this.qualityAssuranceTest = qualityAssuranceTest;
  }
}
