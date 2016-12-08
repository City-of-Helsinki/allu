import {Some, Option} from './option';
export const translations = {
  application: {
    error: {
      statusChangeFailed: 'Päätöksen tallentaminen epäonnistui',
      handlerChangeFailed: 'Hakemuksen käsittelijän vaihto epäonnistui',
      searchFailed: 'Hakemuksen hakeminen epäonnistui',
      saveFailed: 'Hakemuksen tallentaminen epäonnistui'
    },
    status: {
      PRE_RESERVED: 'Alustava varaus',
      PENDING: 'Hakemus saapunut',
      HANDLING: 'Käsittelyssä',
      RETURNED_TO_PREPARATION: 'Palautettu käsittelyyn',
      DECISIONMAKING: 'Odottaa päätöstä',
      DECISION: 'Päätetty',
      REJECTED: 'Hylätty päätös',
      FINISHED: 'Valmis',
      CANCELLED: 'Peruttu'
    },
    type: {
      EVENT: 'Tapahtuma',
      SHORT_TERM_RENTAL: 'Lyhytaikainen maanvuokraus',
      CABLE_REPORT: 'Johtoselvitykset',
      EXCAVATION_ANNOUNCEMENT: 'Kaivuilmoitus',
      AREA_RENTAL: 'Aluevuokraus',
      TEMPORARY_TRAFFIC_ARRANGEMENTS: 'Tilapäinen liikennejärjestely',
      PLACEMENT_PERMIT: 'Sijoitusluvat',
      NOTE: 'Muistiinpano'
    },
    kind: {
      OUTDOOREVENT: 'Ulkoilmatapahtuma',
      PROMOTION: 'Promootio',
      ELECTION: 'Vaalit',
      EXCAVATION_ANNOUNCEMENT: 'Kaivuuilmoitus',
      AREA_RENTAL: 'Aluevuokraus',
      TEMPORARY_TRAFFIC_ARRANGEMENTS: 'Tilapäiset liikennejärjestelyt',
      BRIDGE_BANNER: 'Banderollit silloissa',
      BENJI: 'Benji-hyppylaite',
      PROMOTION_OR_SALES: 'Esittely- tai myyntitila liikkeen edustalla',
      URBAN_FARMING: 'Kaupunkiviljelypaikka',
      MAIN_STREET_SALES: 'Keskuskadun myyntipaikka',
      SUMMER_THEATER: 'Kesäteatterit',
      DOG_TRAINING_FIELD: 'Koirakoulutuskentät',
      DOG_TRAINING_EVENT: 'Koirakoulutustapahtuma',
      CARGO_CONTAINER: 'Kontti',
      SMALL_ART_AND_CULTURE: 'Pienimuotoinen taide- ja kulttuuritoiminta',
      SEASON_SALE: 'Sesonkimyynti',
      CIRCUS: 'Sirkus/tivolivierailu',
      ART: 'Taideteos',
      STORAGE_AREA: 'Varastoalue',
      OTHER_SHORT_TERM_RENTAL: 'Muu lyhytaikainen maanvuokraus',
      CITY_STREET_AND_GREEN: 'Kaupungin katu- ja vihertyöt',
      WATER_AND_SEWAGE: 'HKR, HSY Vesijohto ja viemärityöt',
      HKL: 'HKL:n työt',
      ELECTRIC_CABLE: 'Sähkökaapelityöt',
      DISTRICT_HEATING: 'Kaukolämpötyöt',
      DISTRICT_COOLING: 'Kaukokylmätyöt',
      TELECOMMUNICATION: 'Tiedonsiirron kaapelityöt',
      GAS: 'Kaasujohdot',
      AD_PILLARS_AND_STOPS: 'Ulkomainospilarit ja pysäkkikatokset',
      PROPERTY_MERGER: 'Kiinteistö-/tonttiliitokset',
      SOIL_INVESTIGATION: 'Maaperätutkimukset',
      JOINT_MUNICIPAL_INFRASTRUCTURE: 'Yhteinen kunnallistekninen työmaa',
      ABSORBING_SEWAGE_SYSTEM: 'Imujätejärjestelmä',
      UNDERGROUND_CONSTRUCTION: 'Maanalainen rakentaminen',
      OTHER_CABLE_REPORT: 'Muut'
    },
    specifier: {
      CITY_STREET_AND_GREEN_CONSTRUCTION: 'Kadun tai puiston rakentaminen',
      CITY_STREET_AND_GREEN_MAINTENANCE: 'Kadun tai puiston kunnossapito',
      ASPHALT: 'Asfaltointityö',
      PAVEMENT: 'Kiveystyö',
      BRIDGE: 'Siltatyö',
      STORM_DRAIN_CONSTRUCTION: 'Hulevesiviemärin rakentaminen',
      STORM_DRAIN_MAINTENANCE: 'Hulevesiviemärin kunnossapito',
      WATER_PIPE_CONSTRUCTION: 'Vesijohdon rakentaminen',
      WATER_PIPE_MAINTENANCE: 'Vesijohdon kunnossapito',
      WATER_PIPE_LEAK_REPAIR: 'Vesijohdon vuotokorjaus (äkillinen)',
      DRAIN_CONSTRUCTION: 'Viemärin rakentaminen',
      DRAIN_MAINTENANCE: 'Viemärin kunnossapito',
      DRAIN_LEAK_REPAIR: 'Viemärin vuotokorjaus (äkillinen)',
      HKL_STOP_WORK: 'Pysäkkityö',
      HKL_OTHER_WORK: 'Muu työ',
      CABLE_CONSTRUCTION: 'Kaapelityö rakentaminen',
      CABLE_MAINTENANCE: 'Kaapelityö kunnossapito',
      CABLE_REPAIR: 'Kaapelityö vikakorjaus (äkillinen)',
      OUTDOOR_LIGHTING: 'Ulkovalaistustyö',
      TRAFFIC_LIGHTS: 'Liikennevalotyö',
      DISTRICT_HEATING_CONSTRUCTION: 'Verkon rakentaminen',
      DISTRICT_HEATING_MAINTENANCE: 'Verkon kunnossapito',
      DISTRICT_HEATING_REPAIR: 'Verkon vikakorjaus (äkillinen)',
      DISTRICT_COOLING_CONSTRUCTION: 'Verkon rakentaminen',
      DISTRICT_COOLING_MAINTENANCE: 'Verkon kunnossapito',
      DISTRICT_COOLING_REPAIR: 'Verkon vikakorjaus (äkillinen)',
      TELECOMMUNICATION_CONSTRUCTION: 'Kaapelityö (rakentaminen)',
      TELECOMMUNICATION_MAINTENANCE: 'Kaapelityö (kunnossapito)',
      TELECOMMUNICATION_REPAIR: 'Vikakorjaus (äkillinen)',
      GAS_CONSTRUCTION: 'Verkon rakentaminen',
      GAS_MAINTENANCE: 'Verkon kunnossapito',
      GAS_REPAIR: 'Vikakorjaus (äkillinen)',
      AD_STOPS: 'Pysäkkikatokset',
      AD_BILLBOARDS_AND_PILLARS: 'Mainostaulut ja -pilarit',
      PROPERTY_MERGER_WATER: 'Vesi',
      PROPERTY_MERGER_DRAIN: 'Viemäri',
      PROPERTY_MERGER_DISTRICT_HEATING: 'Kaukolämpö',
      PROPERTY_MERGER_DISTRICT_COOLING: 'Kaukokylmä',
      PROPERTY_MERGER_ELECTRICITY: 'Sähkö',
      PROPERTY_MERGER_TELECOMMUNICATION: 'Teleyhteydet',
      PROPERTY_MERGER_ENTRANCE: 'Sisäänajo',
      PROPERTY_MERGER_GAS: 'Kaasu',
      PROPERTY_MERGER_STORM_DRAIN: 'Hulevesi',
      PROPERTY_MERGER_ABSORBING_SEWAGE_SYSTEM: 'Imujätejärjestelmä',
      SOIL_INVESTIGATION_DRILLING: 'Kairaukset',
      SOIL_INVESTIGATION_PIPING: 'Pohjavesiputket',
      SOIL_INVESTIGATION_TEST_HOLES: 'Koekuopat',
      SOIL_INVESTIGATION_OTHER: 'Muut',
      JOINT_MUNICIPAL_INFRASTRUCTURE_CONSTRUCTION: 'Rakentaminen',
      JOINT_MUNICIPAL_INFRASTRUCTURE_MAINTENANCE: 'Kunnossapito',
      ABSORBING_SEWAGE_SYSTEM_CONSTRUCTION: 'Verkon rakentaminen',
      ABSORBING_SEWAGE_SYSTEM_MAINTENANCE: 'Verkon kunnossapito',
      ABSORBING_SEWAGE_SYSTEM_REPAIR: 'Verkon vikakorjaus (äkillinen)',
      UNDERGROUND_CONSTRUCTION_SPACE: 'Maanalainen tila',
      UNDERGROUND_CONSTRUCTION_STRUCTURE: 'Maanalainen rakenne',
      OTHER_CABLE_WORK_DEMOUNTABLE_PLATFORM: 'Vaihtolava',
      OTHER_CABLE_WORK_LIFT: 'Nostotyö',
      OTHER_CABLE_WORK_SNOW_CLEARING: 'Lumenpudotus',
      OTHER_CABLE_WORK_PUBLIC_OCCASION: 'Yleisötilaisuus',
      OTHER_CABLE_WORK_MAPPING: 'Kuvaus',
      OTHER_CABLE_WORK_RELOCATION: 'Muutto',
      OTHER_CABLE_WORK_ESTATE_REPAIR: 'Kiinteistöremontti',
      OTHER_CABLE_WORK_OTHER_TRAFFIC_ARRANGEMENTS: 'Muu liikennejärjestelyä vaativa työ'
    },
    field: {
      nameMissing: 'Tapahtuman nimi puuttuu',
      nameShort: 'Tapahtuman nimi on liian lyhyt'
    },
    event: {
      billingType: {
        CASH: 'Käteinen',
        INVOICE: 'Lasku'
      },
      nature: {
        PUBLIC_FREE: 'Avoin',
        PUBLIC_NONFREE: 'Maksullinen',
        CLOSED: 'Suljettu'
      },
      noPriceReason: {
        CHARITY: 'Hyväntekeväisyys- tai kansalaisjärjestö tai oppilaitoksen tapahtuma',
        ART_OR_CULTURE: 'Taide- tai kulttuuritapahtuma',
        NO_FEE_SPORTING: 'Avoin ja maksuton urheilutapahtuma',
        RESIDENT_OR_CITY: 'Asukas- tai kaupunginosayhdistyksen tapahtuma',
        SPIRITUAL: 'Aatteellinen, hengellinen tai yhteiskunnallinen tapahtuma',
        CITY: 'Kaupunki isäntänä tai järjestäjäkumppanina',
        ART: 'Tilataideteos',
        YOUTH: 'Nuorisojärjestön tapahtuma',
        PRIVATE: 'Yksityishenkilön järjestämä merkkipäiväjuhla tai vastaava',
        DEFENCE_OR_POLICE: 'Puolustus- tai poliisivoimien tapahtuma'
      },
      field: {
        natureMissing: 'Tapahtuman luonne puuttuu',
        descriptionMissing: 'Tapahtuman kuvaus puuttuu',
        typeMissing: 'Tapahtuman tyyppi puuttuu',
        eventStartTimeMissing: 'Tapahtuman alkuaika puuttuu',
        eventEndTimeMissing: 'Tapahtuman loppuaika puuttuu',
        startBeforeEnd: 'Loppumispäivä ei voi olla ennen alkamispäivää',
        attendeesGreaterThanOrEqual: 'Yleisömäärä ei voi olla negatiivinen',
        eventEntryFeeThanOrEqual: 'Osallistumismaksu ei voi negatiivinen',
        structureAreaGreaterThanOrEqual: 'Rakenteiden kokonaisneliömäärä ei voi olla negatiivinen',
        priceOverrideGreaterThanOrEqual: 'Hinta ei voi olla negatiivinen'
      }
    },
    shortTermRental: {
      field: {
        descriptionMissing: 'Vuokrauksen kuvaus puuttuu',
        rentalStartTimeMissing: 'Vuokrauksen alkuaika puuttuu',
        rentalEndTimeMissing: 'Vuokrauksen loppuaika puuttuu',
        startBeforeEnd: 'Loppumispäivä ei voi olla ennen alkamispäivää'
      }
    },
    cableReport: {
      cableInfo: {
        type: {
          TELECOMMUNICATION: 'Tietoliikenne',
          ELECTRICITY: 'Sähkö',
          WATER_AND_SEWAGE: 'Vesi ja viemäri',
          DISTRICT_HEATING_COOLING: 'Kaukolämpö/jäähdytys',
          GAS: 'Kaasu',
          UNDERGROUND_STRUCTURE: 'Maanalainen rakenne/tila',
          TRAMWAY: 'Raitiotie',
          STREET_HEATING: 'Katulämmitys',
          SEWAGE_PIPE: 'Jäteputki',
          GEOTHERMAL_WELL: 'Maalämpökaivo',
          GEOTECHNICAL_OBSERVATION_POST: 'Geotekninen tarkkailupiste',
          OTHER: 'Yleisesti/muut'
        }
      },
      field: {
        startBeforeEnd: 'Lopetuspäivä ei voi olla ennen aloituspäivää'
      }
    }
  },
  project: {
    field: {
      nameMissing: 'Hankkeen nimi puuttuu',
      ownerNameMissing: 'Hankkeen omistaja puuttu',
      contactNameMissing: 'Hankkeen yhteyshenkilö puuttuu',
      customerReferenceMissing: 'Asiakkaan viite/työnumero puuttuu'
    },
    error: {
      saveFailed: 'Projektin tallentaminen epäonnistui',
      searchFailed: 'Projektien hakeminen epäonnistui'
    }
  },
  applicant: {
    type: {
      COMPANY: {
        name: 'Yritys',
        nameLabel: 'Yrityksen nimi',
        id: 'Y-tunnus'
      },
      ASSOCIATION: {
        name: 'Yhdistys',
        nameLabel: 'Yhdistyksen nimi',
        id: 'Y-tunnus'
      },
      PERSON: {
        name: 'Yksityishenkilö',
        nameLabel: 'Henkilön nimi',
        id: 'Henkilötunnus'
      },
      DEFAULT: {
        name: 'Hakija',
        nameLabel: 'Hakijan nimi',
        id: 'Y-tunnus'
      }
    },
    field: {
      typeMissing: 'Tyyppi puuttuu',
      nameMissing: 'Nimi puuttuu',
      nameShort: 'Nimi on liian lyhyt',
      registryKeyMissing: 'Tunniste puuttuu',
      registryKeyShort: 'Tunniste on liian lyhyt',
      emailInvalid: 'Virheellinen sähköpostiosoite',
      phoneShort: 'Puhelinnumero on liian lyhyt'
    }
  },
  contact: {
    field: {
      nameMissing: 'Nimi puuttuu',
      nameShort: 'Nimi on liian lyhyt',
      emailInvalid: 'Virheellinen sähköpostiosoite',
      phoneShort: 'Puhelinnumero on liian lyhyt'
    }
  },
  geolocation: {
    error: {
      searchFailed: 'Osoitteen haku epäonnistui'
    }
  },
  decision: {
    type: {
      DECISION: 'Hakemus päätetty',
      RETURNED_TO_PREPARATION: 'Hakemus palautettu valmisteluun',
      REJECTED: 'Hakemus hylätty'
    },
    error: {
      generatePdf: 'Pdf:n muodostaminen epäonnistui'
    }
  },
  user: {
    role: {
      ROLE_CREATE_APPLICATION: 'Hakemuksen luominen',
      ROLE_PROCESS_APPLICATION: 'Hakemuksen käsittely',
      ROLE_DECISION: 'Päätöksen teko',
      ROLE_SUPERVISE: 'Valvonta',
      ROLE_INVOICING: 'Laskutus',
      ROLE_VIEW: 'Katselu',
      ROLE_ADMIN: 'Ylläpito'
    }
  },
  defaultText: {
    error: {
      saveFailed: 'Vakiotekstin tallentaminen epäonnistui'
    }
  },
  common: {
    field: {
      emailInvalid: 'Virheellinen sähköpostiosoite'
    }
  }
};

const toKey = (path: string | Array<string>): Option<Array<string>> => {
  return Some(path).map(p => {
    let pathString = '';
    if (Array.isArray(p)) {
      pathString = p.join('.');
    } else {
      pathString = p;
    }
    return pathString.split('.');
  });
};

/**
 * Finds translation for given path
 * @param path path to translation eg. application.status.HANDLED
 * @returns translation if found with path, otherwise returns path
 */
export const findTranslation = (path: string | Array<string>): string => {
  return toKey(path)
    .map(pathParts => pathParts.reduce((acc, cur) => Some(acc[cur]).orElse(pathParts.join('.')) , translations))
    .orElse('');
};
