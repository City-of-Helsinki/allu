export enum NegligenceFeeType {
  START_BEFORE_PERMIT, // Aloitus ennen luvan saantia
  AREA_UNCLEAN, // Alue epäsiisti
  ACTIONS_AGAINST_TRAFFIC_ARRANGEMENT, // Liikennejärjestelypäätöksen vastainen toiminta
  UNAUTHORIZED_PARKING, // Auton luvaton pysäköinti alueella
  LATE_NOTIFICATION_OF_COMPLETION, // Valmistumisilmoitus tullut myöhässä
  LATE_NOTIFICATION_OF_OPERATION_STATE, // Toiminnallinen tila ilmoitettu myöhässä
  LATE_REQUEST_OF_EXTRA_TIME, // Lisäaikaa haettu myöhässä
  OTHER // Muu syy
}
