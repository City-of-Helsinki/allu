package fi.hel.allu.common.domain.types;

/**
 * The status types of applications. The order of these enum values is used also in ordering the applications in UI. If you add a new value,
 * make sure it's added after status that precedes the new status in application processing.
 */
public enum StatusType {
  PENDING_CLIENT, // Vireillä asiakassovelluksessa

  PRE_RESERVED, // Alustava varaus
  PENDING, // Vireillä
  HANDLING, // Käsittelyssä
  RETURNED_TO_PREPARATION, // Palautettu valmisteluun
  WAITING_CONTRACT_APPROVAL, // Odottaa sopimuksen hyväksyntää
  DECISIONMAKING, // Odottaa päätöstä
  DECISION, // Päätetty
  REJECTED, // Hylätty päätös
  FINISHED, // Valmis
  CANCELLED, // Peruttu
  REPLACED, // Korvattu
  ARCHIVED // Arkistoitu
}
