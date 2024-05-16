package fi.hel.allu.model.service;

import java.time.ZonedDateTime;

import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import com.greghaskins.spectrum.Spectrum;

import fi.hel.allu.common.domain.types.ApplicationTagType;
import fi.hel.allu.common.domain.types.DepositStatusType;
import fi.hel.allu.model.dao.ApplicationDao;
import fi.hel.allu.model.dao.DepositDao;
import fi.hel.allu.model.domain.ApplicationTag;
import fi.hel.allu.model.domain.Deposit;

import static com.greghaskins.spectrum.dsl.specification.Specification.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.junit.Assert.*;

@RunWith(Spectrum.class)
public class DepositServiceSpec {

  private DepositService depositService;
  private DepositDao depositDao;
  private ApplicationDao applicationDao;

  {
    describe("DepositDao", () -> {
      beforeEach(() -> {
        depositDao = mock(DepositDao.class);
        applicationDao = mock(ApplicationDao.class);
        depositService = new DepositService(depositDao, applicationDao);
      });
      describe("Create", () -> {
        final Deposit deposit = createDeposit(DepositStatusType.UNPAID_DEPOSIT);
        it("should insert given deposit", () -> {
          depositService.create(deposit);
          Mockito.verify(depositDao).insert(eq(deposit));
        });
        it("should create a deposit requested tag", () -> {
          ArgumentCaptor<ApplicationTag> captor = ArgumentCaptor.forClass(ApplicationTag.class);
          depositService.create(deposit);
          Mockito.verify(applicationDao).addTag(eq(deposit.getApplicationId()), captor.capture());
          assertEquals(ApplicationTagType.DEPOSIT_REQUESTED, captor.getValue().getType());
        });
      });
      describe("Update", () -> {
        final Deposit deposit = createDeposit(DepositStatusType.UNPAID_DEPOSIT);
        it("should update given deposit", () -> {
          depositService.update(deposit.getId(), deposit);
          Mockito.verify(depositDao).update(eq(deposit));
        });
        it("should update tags for paid deposit", () -> {
          ArgumentCaptor<ApplicationTag> captor = ArgumentCaptor.forClass(ApplicationTag.class);
          deposit.setStatus(DepositStatusType.PAID_DEPOSIT);
          depositService.update(deposit.getId(), deposit);
          Mockito.verify(applicationDao).removeTagByTypes(deposit.getApplicationId(), DepositService.depositTags);
          Mockito.verify(applicationDao).addTag(eq(deposit.getApplicationId()), captor.capture());
          assertEquals(ApplicationTagType.DEPOSIT_PAID, captor.getValue().getType());
        });
        it("should update tags for unpaid deposit", () -> {
          ArgumentCaptor<ApplicationTag> captor = ArgumentCaptor.forClass(ApplicationTag.class);
          deposit.setStatus(DepositStatusType.UNPAID_DEPOSIT);
          depositService.update(deposit.getId(), deposit);
          Mockito.verify(applicationDao).removeTagByTypes(deposit.getApplicationId(), DepositService.depositTags);
          Mockito.verify(applicationDao).addTag(eq(deposit.getApplicationId()), captor.capture());
          assertEquals(ApplicationTagType.DEPOSIT_REQUESTED, captor.getValue().getType());
        });
        it("should update tags for returned deposit", () -> {
          deposit.setStatus(DepositStatusType.RETURNED_DEPOSIT);
          depositService.update(deposit.getId(), deposit);
          Mockito.verify(applicationDao).removeTagByTypes(deposit.getApplicationId(), DepositService.depositTags);
          Mockito.verify(applicationDao, Mockito.never()).addTag(any(Integer.class), any(ApplicationTag.class));
        });
      });
      describe("Delete", () -> {
        final Deposit deposit = createDeposit(DepositStatusType.UNPAID_DEPOSIT);
        beforeEach(() -> when(depositDao.findById(deposit.getId())).thenReturn(deposit));
        it("should delete given deposit", () -> {
          depositService.delete(deposit.getId());
          Mockito.verify(depositDao).delete(eq(deposit.getId()));
        });
        it("should remove deposit tag", () -> {
          depositService.delete(deposit.getId());
          Mockito.verify(applicationDao).removeTagByTypes(deposit.getApplicationId(), DepositService.depositTags);
        });
      });
    });
  }

  private Deposit createDeposit(DepositStatusType status) {
     return new Deposit(2, 3, 100, "reason", status, ZonedDateTime.now(), 99);
  }
}