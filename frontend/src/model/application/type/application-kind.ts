import {ApplicationSpecifier} from './application-specifier';
import {ArrayUtil} from '../../../util/array-util';

export enum ApplicationKind {
  // EVENTS
  OUTDOOREVENT, // Ulkoilmatapahtuma
  PROMOTION, // Promootio
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
    // CABLE REPORTS
  STREET_AND_GREEN, // Katu- ja vihertyöt
  WATER_AND_SEWAGE, // Vesi / viemäri
  ELECTRICITY, // Sähkö
  DATA_TRANSFER, // Tiedonsiirto
  HEATING_COOLING, // Lämmitys/viilennys
  CONSTRUCTION, // Rakennus
  YARD, // Piha
  GEOLOGICAL_SURVEY, // Pohjatutkimus
  // AREA RENTAL
  PROPERTY_RENOVATION,        // kiinteistöremontti
  CONTAINER_BARRACK,          // kontti/parakki
  PHOTO_SHOOTING,             // kuvaus
  SNOW_WORK,                  // lumenpudotus
  RELOCATION,                 // muutto
  LIFTING,                    // nostotyö
  NEW_BUILDING_CONSTRUCTION,  // uudisrakennuksen työmaa-alue
  ROLL_OFF,                   // vaihtolava
  // NOTES
  CHRISTMAS_TREE_SALES_AREA, // Joulukuusenmyyntipaikka
  CITY_CYCLING_AREA, // Kaupunkipyöräpaikka
  AGILE_KIOSK_AREA, // Ketterien kioskien myyntipaikka
  STATEMENT, // Lausunto
  SNOW_HEAP_AREA, // Lumenkasauspaikka
  SNOW_GATHER_AREA, // Lumenvastaanottopaikka
  OTHER_SUBVISION_OF_STATE_AREA, // Muun hallintokunnan alue
  MILITARY_EXCERCISE, 	// Sotaharjoitus
  WINTER_PARKING, // Talvipysäköinti
  REPAVING, 	// Uudelleenpäällystykset
  ELECTION_ADD_STAND, // Vaalimainosteline
  // TEMPORARY TRAFFIC ARRANGEMENTS
  PUBLIC_EVENT, // Yleisötilaisus
  OTHER // Muu
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

export function drawingAllowedForKind(kind: ApplicationKind): boolean {
  return ![ApplicationKind.BRIDGE_BANNER, ApplicationKind.DOG_TRAINING_EVENT, ApplicationKind.DOG_TRAINING_FIELD]
    .some(k => k === kind);
}
