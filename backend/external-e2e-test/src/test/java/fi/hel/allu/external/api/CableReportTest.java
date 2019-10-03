package fi.hel.allu.external.api;

import org.geolatte.geom.Geometry;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import fi.hel.allu.external.domain.CableReportExt;

import static fi.hel.allu.external.api.data.TestData.CABLE_REPORT_GEOMETRY;
import static fi.hel.allu.external.api.data.TestData.CONTRACTOR_WITH_CONTACTS;

@RunWith(SpringJUnit4ClassRunner.class)
public class CableReportTest extends BaseApplicationTest<CableReportExt> {

  private static final String RESOURCE_PATH = "/cablereports";
  private static final String NAME = "Johtoselvitys - ext";

  @Test
  public void shouldCreateCableReport() {
    validateApplicationCreationSuccessful();
  }

  @Override
  protected Geometry getGeometry() {
    return CABLE_REPORT_GEOMETRY;
  }

  @Override
  protected CableReportExt getApplication() {
    CableReportExt cableReport = new CableReportExt();
    cableReport.setWorkDescription("Johtoselvityksen työn kuvaus");
    cableReport.setContractorWithContacts(CONTRACTOR_WITH_CONTACTS);
    cableReport.setClientApplicationKind("Tiedonsiirto");
    setCommonFields(cableReport);
    cableReport.getCustomerWithContacts().getContacts().get(0).setOrderer(Boolean.TRUE);
    return cableReport;
  }

  @Override
  protected String getApplicationName() {
    return NAME;
  }

  @Override
  protected String getResourcePath() {
    return RESOURCE_PATH;
  }

}
