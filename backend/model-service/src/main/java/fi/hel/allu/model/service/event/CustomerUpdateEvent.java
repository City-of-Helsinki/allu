package fi.hel.allu.model.service.event;

import fi.hel.allu.model.domain.Customer;

public class CustomerUpdateEvent {

  private final Customer beforeUpdate;
  private final Customer afterUpdate;

  public CustomerUpdateEvent(Object source, Customer beforeUpdate, Customer afterUpdate) {
    this.beforeUpdate = beforeUpdate;
    this.afterUpdate = afterUpdate;
  }

  public Customer getBeforeUpdate() {
    return beforeUpdate;
  }

  public Customer getAfterUpdate() {
    return afterUpdate;
  }
}
