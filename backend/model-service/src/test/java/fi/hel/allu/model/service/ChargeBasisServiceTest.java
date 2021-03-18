package fi.hel.allu.model.service;

import fi.hel.allu.common.domain.types.StatusType;
import fi.hel.allu.model.dao.ApplicationDao;
import fi.hel.allu.model.dao.ChargeBasisDao;
import fi.hel.allu.model.domain.ChargeBasisEntry;
import fi.hel.allu.model.service.chargeBasis.ChargeBasisService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChargeBasisServiceTest {

  @Mock
  ChargeBasisDao chargeBasisDao;

  @Mock
  ApplicationDao applicationDao;

  @InjectMocks
  private ChargeBasisService chargeBasisService;

  ChargeBasisEntry returnChargeBasisEntry;

  @BeforeEach
  void setUp(){
    returnChargeBasisEntry =  new ChargeBasisEntry();
    returnChargeBasisEntry.setId(1);
    returnChargeBasisEntry.setTag("Arv4");
    returnChargeBasisEntry.setInvoicable(false);
  }

  @Test
  void setInvocable() {
    when(chargeBasisDao.setInvoicable(anyInt(),anyBoolean())).thenReturn(returnChargeBasisEntry);
    when(applicationDao.getStatus(anyInt())).thenReturn(StatusType.PENDING);
    chargeBasisService.setInvoicable(1,100, true);
    verify(chargeBasisDao, times(1)).setInvoicable(anyInt(),anyBoolean());
  }

  @Test
  void setSubInvocable() {
    when(chargeBasisDao.setInvoicable(anyInt(),anyBoolean())).thenReturn(returnChargeBasisEntry);
    when(applicationDao.getStatus(anyInt())).thenReturn(StatusType.PENDING);
    chargeBasisService.setInvoicable(1,100, true);
    verify(chargeBasisDao, times(1)).setSubChargesInvoicable(anyBoolean(),anyString());
  }

  @Test
  void NotCallingsSubInvocable() {
    returnChargeBasisEntry.setTag(null);
    when(chargeBasisDao.setInvoicable(anyInt(),anyBoolean())).thenReturn(returnChargeBasisEntry);
    when(applicationDao.getStatus(anyInt())).thenReturn(StatusType.PENDING);
    chargeBasisService.setInvoicable(1,100, true);
    verify(chargeBasisDao, times(0)).setSubChargesInvoicable(anyBoolean(),anyString());
  }
}
