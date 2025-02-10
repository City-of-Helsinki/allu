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
  NOTE(7), // Muistiinpano
  RETURNED_TO_PREPARATION(8), // Palautettu valmisteluun
  WAITING_CONTRACT_APPROVAL(9), // Odottaa sopimuksen hyväksyntää
  DECISIONMAKING(10), // Odottaa päätöstä
  DECISION(11), // Päätetty
  REJECTED(12), // Hylätty päätös
  OPERATIONAL_CONDITION(13), // Toiminnallinen kunto
  TERMINATED(14), // Irtisanottu
  FINISHED(15), // Valmis
  CANCELLED(16), // Peruttu
  REPLACED(17), // Korvattu
  ARCHIVED(18), // Arkistoitu
  ANONYMIZED(19); // Anonymisoitu

  // Status order in application process flow
  private int orderNumber;

  private StatusType(int orderNumber) {
    this.orderNumber = orderNumber;
  }

  public boolean isBeforeDecision() {
    return orderNumber < DECISION.orderNumber;
  }

  public boolean isBeforeDecisionMaking() {
    return orderNumber < DECISIONMAKING.orderNumber;
  }

  public int getOrderNumber() {
    return orderNumber;
  }
}
