export enum NegligenceFeeType {
  START_BEFORE_PERMIT, // Aloitus ennen päätöksen saamista
  NEGLEGTING_NOTIFICATION_OBLIGATION, // Ilmoitusvelvollisuuden laiminlyönti
  AREA_UNCLEAN, // Alue epäsiisti
  EXCAVATION_MAP_RESTRICTION_NOT_UPDATED,// Kaivun karttarajaus päivittämättä
  WORKING_ZONE_RESTRICTION_NOT_UPDATED,//  Työalueen rajaus päivittämättä,
  ACTIONS_AGAINST_TRAFFIC_ARRANGEMENT, // Liikennejärjestelypäätöksen vastainen toiminta
  UNAUTHORIZED_PARKING, // Auton luvaton pysäköinti alueella
  WORK_ZONE_RESTORATION_INADEQUATE, // Työalueen ennallistaminen puutteellinen
  NEGLEGTING_MAINTENANCE, // Työnaikaisten kunnossapitovelvoitteiden laiminlyönti
  NEGLECTING_PERMIT_TERMS, // Päätösehtojen laiminlyönti
  OTHER // Muu syy
}
