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
      CABLE_REPORT: 'Johtoselvitys',
      EXCAVATION_ANNOUNCEMENT: 'Kaivuilmoitus',
      AREA_RENTAL: 'Aluevuokraus',
      TEMPORARY_TRAFFIC_ARRANGEMENTS: 'Tilapäinen liikennejärjestely',
      PLACEMENT_PERMIT: 'Sijoituslupa',
      NOTE: 'Muistiinpano'
    },
    kind: {
      OUTDOOREVENT: 'Ulkoilmatapahtuma',
      PROMOTION: 'Promootio',
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
      STREET_AND_GREEN: 'Katu- ja vihertyöt',
      WATER_AND_SEWAGE: 'Vesi / viemäri',
      ELECTRICITY: 'Sähkö',
      DATA_TRANSFER: 'Tiedonsiirto',
      HEATING_COOLING: 'Lämmitys/viilennys',
      CONSTRUCTION: 'Rakennus',
      YARD: 'Piha',
      GEOLOGICAL_SURVEY: 'Pohjatutkimus',
      OTHER_CABLE_REPORT: 'Muut',
      CHRISTMAS_TREE_SALES_AREA: 'Joulukuusenmyyntipaikka',
      CITY_CYCLING_AREA: 'Kaupunkipyöräpaikka',
      AGILE_KIOSK_AREA: 'Ketterien kioskien myyntipaikka',
      STATEMENT: 'Lausunto',
      SNOW_HEAP_AREA: 'Lumenkasauspaikka',
      SNOW_GATHER_AREA: 'Lumenvastaanottopaikka',
      OTHER_SUBVISION_OF_STATE_AREA: 'Muun hallintokunnan alue',
      MILITARY_EXCERCISE: 'Sotaharjoitus',
      WINTER_PARKING: 'Talvipysäköinti',
      REPAVING: 'Uudelleenpäällystykset',
      ELECTION_ADD_STAND: 'Vaalimainosteline',
      NOTE_OTHER: 'Muu'
    },
    specifier: {
      ASPHALT: 'Asfaltointityö',
      INDUCTION_LOOP: 'Induktiosilmukka',
      COVER_STRUCTURE: 'Kansisto',
      STREET_OR_PARK: 'Katu tai puisto',
      PAVEMENT: 'Kiveystyö',
      TRAFFIC_LIGHT: 'Liikennevalo',
      COMMERCIAL_DEVICE: 'Mainoslaite',
      TRAFFIC_STOP: 'Pysäkkikatos',
      BRIDGE: 'Silta',
      OUTDOOR_LIGHTING: 'Ulkovalaistus',
      STORM_DRAIN: 'Hulevesi',
      WELL: 'Kaivo',
      UNDERGROUND_DRAIN: 'Salaoja',
      WATER_PIPE: 'Vesijohto',
      DRAIN: 'Viemäri',
      DISTRIBUTION_CABINET: 'Jakokaappi',
      ELECTRICITY_CABLE: 'Kaapeli',
      ELECTRICITY_WELL: 'Kaivo',
      DISTRIBUTION_CABINET_OR_PILAR: 'Jakokaappi/-pilari',
      DATA_CABLE: 'Kaapeli',
      DATA_WELL: 'Kaivo',
      STREET_HEATING: 'Katulämmitys',
      DISTRICT_HEATING: 'Kaukolämpö',
      DISTRICT_COOLING: 'Kaukokylmä',
      GROUND_ROCK_ANCHOR: 'Maa- / kallioankkuri',
      UNDERGROUND_STRUCTURE: 'Maanalainen rakenne',
      UNDERGROUND_SPACE: 'Maanalainen tila',
      BASE_STRUCTURES: 'Perusrakenteet',
      DRILL_PILE: 'Porapaalu',
      CONSTRUCTION_EQUIPMENT: 'Rakennuksen laite/varuste',
      CONSTRUCTION_PART: 'Rakennuksen osa',
      GROUND_FROST_INSULATION: 'Routaeriste',
      SMOKE_HATCH_OR_PIPE:  'Savunpoistoluukku/-putki, IV-putki',
      STOP_OR_TRANSITION_SLAB: 'Sulku-/siirtymälaatta',
      SUPPORTING_WALL_OR_PILE:  'Tukiseinä/-paalu',
      FENCE_OR_WALL: 'Aita, muuri, penger',
      DRIVEWAY: 'Kulkutie',
      STAIRS_RAMP:  'Portaat, luiska tms.',
      SUPPORTING_WALL_OR_BANK: 'Tukimuuri/-penger, lujitemaamuuri',
      DRILLING: 'Kairaus',
      TEST_HOLE: 'Koekuoppa',
      GROUND_WATER_PIPE: 'Pohjavesiputki',
      ABSORBING_SEWAGE_SYSTEM: 'Imujätejärjestelmä',
      GAS_PIPE: 'Kaasujohto',
      OTHER: 'Muu'
    },
    tag: {
      type: {
        ADDITIONAL_INFORMATION_REQUESTED: 'täydennyspyyntö lähetetty',
        STATEMENT_REQUESTED: 'lausunnolla',
        DEPOSIT_REQUESTED: 'vakuus määritetty',
        DEPOSIT_PAID: 'vakuus suoritettu',
        PRELIMINARY_INSPECTION_REQUESTED: 'aloituskatselmointipyyntö lähetetty',
        PRELIMINARY_INSPECTION_DONE: 'aloituskatselmus suoritettu',
        FINAL_INSPECTION_AGREED: 'loppukatselmus sovittu',
        FINAL_INSPECTION_DONE: 'loppukatselmus suoritettu',
        WAITING: 'odottaa lisätietoa',
        COMPENSATION_CLARIFICATION: 'hyvitysselvitys',
        PAYMENT_BASIS_CORRECTION: 'maksuperusteet korjattava'
      }
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
    },
    excavationAnnouncement: {
      field: {
        validityStartTimeMissing: 'Voimassaolon aloitus puuttuu',
        validityEndTimeMissing: 'Voimassaolon lopetus puuttuu',
        validityStartBeforeEnd: 'Voimassaolon lopetus ei voi olla ennen aloitusta',
        unauthorizedWorkStartBeforeEnd: 'Voimassaolon lopetus ei voi olla ennen aloitusta'
      }
    },
    note: {
      field: {
        validityStartTimeMissing: 'Alkupäivämäärä puuttuu',
        validityEndTimeMissing: 'Loppupäivämäärä puuttuu'
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
      PROPERTY: {
        name: 'Kiinteistö',
        nameLabel: 'Kiinteistön nimi',
        id: 'Kiinteistötunnus'
      },
      OTHER: {
        name: 'Muu',
        nameLabel: 'Hakijan nimi',
        id: 'Y-tunnus'
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
  },
  map: {
    zoomIn: 'Lähennä',
    zoomOut: 'Loitonna',
    draw: {
      toolbar: {
        actions: {
          title: 'Peruuta piirtäminen',
          text: 'Peruuta'
        },
        finish: {
          title: 'Lopeta piirtäminen',
          text: 'Lopeta'
        },
        undo: {
          title: 'Poista viimeisin piirretty piste',
          text: 'Poista viimeisin piste'
        },
        buttons: {
          polyline: 'Piirrä viiva',
          polygon: 'Piirrä monikulmio',
          rectangle: 'Piirrä suorakaide',
          circle: 'Piirrä ympyrä',
          marker: 'Piirrä piste'
        }
      },
      handlers: {
        circle: {
          tooltip: {
            start: 'Klikkaa ja raahaa piirtääksesi ympyrän.'
          },
          radius: 'Säde'
        },
        marker: {
          tooltip: {
            start: 'Klikkaa karttaa lisätäksesi pisteen.'
          }
        },
        polygon: {
          tooltip: {
            start: 'Aloita kuvion piirtäminen klikkaamalla.',
            cont: 'Jatka kuvion piirtämistä klikkaamalla.',
            end: 'Klikkaa ensimmäistä pistettä kuvion päättämiseksi.'
          }
        },
        polyline: {
          error: '<strong>Virhe:</strong> kuvion reunat eivät saa leikata toisiaan!',
          tooltip: {
            start: 'Aloita viivan piirtäminen klikkaamalla.',
            cont: 'Jatka viivan piirtämistä klikkaamalla.',
            end: 'Klikkaa viimeistä pistettä viivan päättämiseksi'
          }
        },
        rectangle: {
          tooltip: {
            start: 'Klikkaa ja raahaa piirtääksesi suorakulmion.'
          }
        },
        simpleshape: {
          tooltip: {
            end: 'Vapauta hiiren nappi päättääksesi piirtämisen.'
          }
        }
      }
    },
    edit: {
      toolbar: {
        actions: {
          save: {
            title: 'Tallenna muutokset.',
            text: 'Tallenna'
          },
          cancel: {
            title: 'Peruuta muokkaukset, hylkää kaikki muutokset.',
            text: 'Peruuta'
          }
        },
        buttons: {
          edit: 'Muokkaa kuvioita.',
          editDisabled: 'Ei muokattavia kuvioita.',
          remove: 'Poista kuvioita.',
          removeDisabled: 'Ei poistettavia kuvioita.'
        }
      },
      handlers: {
        edit: {
          tooltip: {
            text: 'Muokkaa aluetta tai pistettä raahaamalla.',
            subtext: 'Paina peruuta peruaksesi muutokset.'
          }
        },
        remove: {
          tooltip: {
            text: 'Poista piirretty alue klikkaamalla.'
          }
        }
      }
    }
  },
  attachment: {
    type: {
      ADDED_BY_CUSTOMER: 'Asiakkaan lisäämä liite',
      ADDED_BY_HANDLER: 'Käsittelijän lisäämä liite',
      DEFAULT: 'Hakemustyyppikohtainen vakioliite',
      DEFAULT_IMAGE: 'Hakemustyyppikohtainen tyyppikuvaliite',
      DEFAULT_TERMS: 'Hakemustyyppikohtainen ehtoliite'
    }
  },
  comment: {
    type: {
      INTERNAL: 'Sisäinen kommentti',
      INVOICING: 'Laskutuksen kommentti',
      DECISION: 'Päätöksentekijän kommentti'
    }
  },
  workqueue: {
    tab: {
      OWN: 'Omat',
      COMMON:  'Yhteiset',
      WAITING: 'Odottaa'
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
