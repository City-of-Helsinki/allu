import {ApplicationSpecifier, SpecifierEntry} from './application-specifier';
import {ArrayUtil} from '../../../util/array-util';

export enum ApplicationKind {
  PROMOTION,
  OUTDOOREVENT,
  BRIDGE_BANNER, // Banderollit silloissa
  BENJI, // Benji-hyppylaite
  PROMOTION_OR_SALES, // Esittely- tai myyntitila liikkeen edustalla
  URBAN_FARMING, // Kaupunkiviljelypaikka
  KESKUSKATU_SALES, // Keskuskadun myyntipaikka
  SUMMER_THEATER, // Kesäteatterit
  DOG_TRAINING_FIELD, // Koirakoulutuskentät
  DOG_TRAINING_EVENT, // Koirakoulutustapahtuma
  SMALL_ART_AND_CULTURE, // Pienimuotoinen taide- ja kulttuuritoiminta
  SEASON_SALE, // Sesonkimyynti
  CIRCUS, // Sirkus/tivolivierailu
  ART, // Taideteos
  STORAGE_AREA, // Varastoalue
  STREET_AND_GREEN, // Katu- ja vihertyöt
  WATER_AND_SEWAGE, // Vesi / viemäri
  ELECTRICITY, // Sähkö
  DATA_TRANSFER, // Tiedonsiirto
  HEATING_COOLING, // Lämmitys/viilennys
  CONSTRUCTION, // Rakennus
  YARD, // Piha
  GEOLOGICAL_SURVEY, // Pohjatutkimus
  PROPERTY_RENOVATION, // kiinteistöremontti
  CONTAINER_BARRACK, // kontti/parakki
  PHOTO_SHOOTING, // kuvaus
  SNOW_WORK, // lumenpudotus
  RELOCATION, // muutto
  LIFTING, // nostotyö
  NEW_BUILDING_CONSTRUCTION, // uudisrakennuksen työmaa-alue
  ROLL_OFF, // vaihtolava
  CHRISTMAS_TREE_SALES_AREA, // Joulukuusenmyyntipaikka
  CITY_CYCLING_AREA, // Kaupunkipyöräpaikka
  AGILE_KIOSK_AREA, // Ketterien kioskien myyntipaikka
  STATEMENT, // Lausunto
  SNOW_HEAP_AREA, // Lumenkasauspaikka
  SNOW_GATHER_AREA, // Lumenvastaanottopaikka
  OTHER_SUBVISION_OF_STATE_AREA, // Muun hallintokunnan alue
  MILITARY_EXCERCISE, // Sotaharjoitus
  WINTER_PARKING, // Talvipysäköinti
  REPAVING, // Uudelleenpäällystykset
  ELECTION_ADD_STAND, // Vaalimainosteline
  PUBLIC_EVENT, // Yleisötilaisuus
  OTHER
}

export class ApplicationKindEntry {
  private _specifierNamesSortedByTranslation: Array<SpecifierEntry>;

  constructor(public kind: ApplicationKind, private specifiers?: Array<ApplicationSpecifier>) {
    this.specifiers = specifiers || [];
    this._specifierNamesSortedByTranslation = this.uiSpecifiers
      .sort(ArrayUtil.naturalSortTranslated(['application.specifier'], (specifier: string) => specifier))
      .map(s => new SpecifierEntry(s, this.uiKind));
  }

  get uiKind() {
    return ApplicationKind[this.kind];
  }

  set uiKind(kind: string) {
    this.kind = ApplicationKind[kind];
  }

  get uiSpecifiers() {
    return this.specifiers.map(s => ApplicationSpecifier[s]);
  }

  set uiSpecifiers(specifiers: Array<string>) {
    this.specifiers = specifiers.map(s => ApplicationSpecifier[s]);
  }

  get specifierEntriesSortedByTranslation() {
    return this._specifierNamesSortedByTranslation;
  }

  contains(specifier: string): boolean {
    return this.specifiers.some(s => s === ApplicationSpecifier[specifier]);
  }

  hasSpecifiers(): boolean {
    return this.specifiers.length > 0;
  }
}

export function drawingAllowedForKind(kind: ApplicationKind): boolean {
  return ![ApplicationKind.BRIDGE_BANNER, ApplicationKind.DOG_TRAINING_EVENT, ApplicationKind.DOG_TRAINING_FIELD]
    .some(k => k === kind);
}
