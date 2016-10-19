package fi.hel.allu.common.types;

/**
 * The status types of applications. The order of these enum values is used also in ordering the applications in UI. If you add a new value,
 * make sure it's added after status that precedes the new status in application processing.
 */
public enum StatusType {
  PRE_RESERVED, // Alustava varaus
  PENDING, // Vireillä
  HANDLING, // Käsittelyssä
  RETURNED_TO_PREPARATION, // Palautettu valmisteluun
  DECISIONMAKING, // Odottaa päätöstä
  DECISION, // Päätetty
  REJECTED, // Hylätty päätös
  FINISHED, // Valmis
  CANCELLED // Peruttu
}
