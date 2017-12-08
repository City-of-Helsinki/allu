package fi.hel.allu.servicecore.mapper;

import fi.hel.allu.model.domain.Deposit;
import fi.hel.allu.servicecore.domain.DepositJson;
import fi.hel.allu.servicecore.domain.UserJson;

public class DepositMapper {

  public static Deposit createDepositModel(DepositJson depositJson) {
    return new Deposit(
        depositJson.getId(),
        depositJson.getApplicationId(),
        depositJson.getAmount(),
        depositJson.getReason(),
        depositJson.getStatus(),
        depositJson.getCreationTime(),
        null
     );
  }

  public static DepositJson createDepositJson(Deposit deposit, UserJson creator) {
    if (deposit == null) {
      return null;
    }
    return new DepositJson(
        deposit.getId(),
        deposit.getApplicationId(),
        deposit.getAmount(),
        deposit.getReason(),
        deposit.getStatus(),
        deposit.getCreationTime(),
        creator
     );
  }
}
