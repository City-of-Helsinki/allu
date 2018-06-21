package fi.hel.allu.common.domain.types;

/**
 * The status types of applications. The order of these enum values is used also in ordering the applications in UI. If you add a new value,
 * make sure it's added after status that precedes the new status in application processing.
 */
public enum StatusType {

  PENDING_CLIENT(1), // Vireillä asiakassovelluksessa
  PRE_RESERVED(2), // Alustava varaus
  PENDING(3), // Vireillä
  WAITING_INFORMATION(4), // Odottaa täydennystä
  INFORMATION_RECEIVED(5), // Täydennys vastaanotettu
  HANDLING(6), // Käsittelyssä
  RETURNED_TO_PREPARATION(7), // Palautettu valmisteluun
  WAITING_CONTRACT_APPROVAL(8), // Odottaa sopimuksen hyväksyntää
  DECISIONMAKING(9), // Odottaa päätöstä
  DECISION(10), // Päätetty
  REJECTED(11), // Hylätty päätös
  FINISHED(12), // Valmis
  CANCELLED(13), // Peruttu
  REPLACED(14), // Korvattu
  ARCHIVED(15); // Arkistoitu

  // Status order in application process flow
  private int orderNumber;

  private StatusType(int orderNumber) {
    this.orderNumber = orderNumber;
  }

  public boolean isBeforeDecision() {
    return orderNumber < DECISION.orderNumber;
  }


}
