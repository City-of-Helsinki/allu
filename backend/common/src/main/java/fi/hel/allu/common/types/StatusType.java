package fi.hel.allu.common.types;

public enum StatusType {
  PRE_RESERVED, // Alustava varaus
  CANCELLED, // Peruttu
  PENDING, // Vireillä
  HANDLING, // Käsittelyssä
  DECISIONMAKING, // Odottaa päätöstä
  DECISION, // Päätetty
  REJECTED, // Hylätty päätös
  RETURNED_TO_PREPARATION, // Palautettu valmisteluun
  FINISHED // Valmis
}
