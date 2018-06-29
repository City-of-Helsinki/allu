package fi.hel.allu.external.api;
import org.geolatte.geom.Geometry;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import fi.hel.allu.external.domain.PlacementContractExt;

import static fi.hel.allu.external.api.data.TestData.PLACEMENT_CONTRACT_GEOMETRY;

@RunWith(SpringJUnit4ClassRunner.class)
public class PlacementContractTest extends BaseApplicationTest<PlacementContractExt> {

  private static final String RESOURCE_PATH = "/placementcontracts";
  private static final String NAME = "Sijoitussopimus - ext";

  @Test
  public void shouldCreatePlacementContract() {
    validateApplicationCreationSuccessful();
  }

  @Override
  protected PlacementContractExt getApplication() {
    PlacementContractExt placementContract = new PlacementContractExt();
    placementContract.setClientApplicationKind("Pohjatutkimus");
    placementContract.setWorkDescription("Pohjatutkimuksen kuvaus");
    setCommonFields(placementContract);
    return placementContract;
  }

  @Override
  protected Geometry getGeometry() {
    return PLACEMENT_CONTRACT_GEOMETRY;
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
