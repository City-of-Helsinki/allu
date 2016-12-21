import {ApplicationSpecifier} from './application-specifier';
import {ArrayUtil} from '../../../util/array-util';

export enum ApplicationKind {
  // EVENTS
  OUTDOOREVENT, // Ulkoilmatapahtuma
  PROMOTION, // Promootio
  ELECTION, // Vaalit
    // SHORT TERM RENTALS
  EXCAVATION_ANNOUNCEMENT, // Kaivuuilmoitus
  AREA_RENTAL, // ALuevuokraus
  TEMPORARY_TRAFFIC_ARRANGEMENTS, // Tilapäiset liikennejärjestelyt
  BRIDGE_BANNER, // Banderollit silloissa
  BENJI, // Benji-hyppylaite
  PROMOTION_OR_SALES, // Esittely- tai myyntitila liikkeen edustalla
  URBAN_FARMING, // Kaupunkiviljelypaikka
  MAIN_STREET_SALES, // Keskuskadun myyntipaikka
  SUMMER_THEATER, // Kesäteatterit
  DOG_TRAINING_FIELD, // Koirakoulutuskentät
  DOG_TRAINING_EVENT, // Koirakoulutustapahtuma
  CARGO_CONTAINER, // Kontti
  SMALL_ART_AND_CULTURE, // Pienimuotoinen taide- ja kulttuuritoiminta
  SEASON_SALE, // Sesonkimyynti
  CIRCUS, // Sirkus/tivolivierailu
  ART, // Taideteos
  STORAGE_AREA, // Varastoalue
  OTHER_SHORT_TERM_RENTAL, // Muu lyhytaikainen maanvuokraus
    // CABLE REPORTS
  STREET_AND_GREEN, // Katu- ja vihertyöt
  WATER_AND_SEWAGE, // Vesi / viemäri
  ELECTRICITY, // Sähkö
  DATA_TRANSFER, // Tiedonsiirto
  HEATING_COOLING, // Lämmitys/viilennys
  CONSTRUCTION, // Rakennus
  YARD, // Piha
  GEOLOGICAL_SURVEY, // Pohjatutkimus
  OTHER_CABLE_REPORT // Muut
}

export class ApplicationKindStructure {
  constructor(public kind: ApplicationKind, private specifiers?: Array<ApplicationSpecifier>) {
    this.specifiers = specifiers || [];
  }

  get applicationSpecifierNames() {
    return this.specifiers.map(s => ApplicationSpecifier[s]);
  }

  get applicationSpecifierNamesSortedByTranslation() {
    return this.applicationSpecifierNames
      .sort(ArrayUtil.naturalSortTranslated(['application.specifier'], (specifier: string) => specifier));
  }
}
