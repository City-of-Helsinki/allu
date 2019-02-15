import {Some, Option} from './option';
import {StringUtil} from './string.util';
export const translations = {
  logout: {
    header: 'Olet kirjautunut ulos',
    infoText: 'Kirjauduit ulos Allu-järjestelmästä. ' +
    'Mikäli haluat varmistaa uloskirjautumisen myös Helsingin kaupungin AD-tunnistuksesta, tyhjennä selaimen välimuisti.'
  },
  nav: {
    home: 'ETUSIVU',
    workqueue: 'TYÖJONO',
    supervisionTasks: 'VALVONTATEHTÄVÄT',
    applications: 'HAKEMUKSET',
    projects: 'HANKKEET',
    customers: 'ASIAKKAAT',
    admin: 'YLLÄPITO'
  },
  application: {
    newApplication: 'Uusi hakemus',
    draft: 'Alustava varaus',
    owner: 'Omistaja',
    handler: 'Käsittelijä',
    name: 'Nimi',
    applicationId: 'Tunnus',
    applicant: 'Hakija',
    contact: 'Yhteyshenkilö',
    streetAddress: 'Katuosoite',
    cityDistrict: 'Kaupunginosa',
    creationTime: 'Saapunut',
    startTime: 'Aloitus',
    endTime: 'Lopetus',
    comments: 'Kommentit',
    tags: 'Tunnisteet',
    pricingBasis: 'Hinnoitteluperusteet',
    replaced: 'Korvatut',
    invoiced: 'Laskutettu',
    receivedTime: 'Hakemus saapunut',
    orderer: 'Hakemuksen tilaaja',
    relatedApplications: 'Liittyvät hakemukset',
    error: {
      fetch: 'Hakemuksen hakeminen epäonnistui',
      statusChangeFailed: 'Päätöksen tallentaminen epäonnistui',
      ownerChangeFailed: 'Hakemuksen omistajan vaihto epäonnistui',
      searchFailed: 'Hakemuksen hakeminen epäonnistui',
      saveFailed: 'Hakemuksen tallentaminen epäonnistui',
      removeFailed: 'Hakemuksen poistaminen epäonnistui',
      convertToApplicationFailed: 'Alustavan varauksen muuttaminen hakemukseksi epäonnistui',
      replaceFailed: 'Hakemuksen korvaaminen epäonnistui',
      replacementHistory: 'Hakemuksen korvaushistorian haku epäonnistui',
      deleteFailed: 'Hakemuksen poistaminen epäonnistui',
      tagUpdateFailed: 'Hakemuksen tarkenteiden tallentaminen epäonnistui',
      saveTagFailed: 'Hakemuksen tarkenteen lisääminen epäonnistui',
      removeTagFailed: 'Hakemuksen tarkenteen poistaminen epäonnistui',
      tagFetchFailed: 'Hakemuksen tarkenteiden hakeminen epäonnistui',
      toHandling: 'Hakemuksen käsittelyyn siirtäminen epäonnistui',
      toDecisionmaking: 'Hakemuksen siirtäminen odottamaan päätöstä epäonnistui',
      cancel: 'Hakemuksen peruminen epäonnistui',
      reportOperationalCondition: 'Toiminnallisen kunnon ilmoittaminen epäonnistui',
      reportWorkFinished: 'Työn valmistumisen ilmoittaminen epäonnistui',
      reportCustomerOperationalCondition: 'Asiakkaan ilmoittaman toiminnallisen kunnon päivittäminen epäonnistui',
      reportCustomerWorkFinished: 'Asiakkaan ilmoittaman työn valmistumisen päivittäminen epäonnistui',
      reportCustomerValidity: 'Asiakkaan ilmoittaman voimassaolon päivittäminen epäonnistui',
      confirmClientApplicationData: 'Asiakkaalta saapuneiden tietojen hyväksyminen epäonnistui'
    },
    action: {
      saved: 'Hakemus tallennettu',
      replaced: 'Korvaava hakemus luotu',
      deleted: 'Hakemus poistettu',
      reportOperationalCondition: 'Toiminnallinen kunto ilmoitettu',
      reportWorkFinished: 'Työn valmistuminen ilmoitettu',
      reportCustomerOperationalCondition: 'Asiakkaan ilmoittama toiminnallinen kunto päivitetty',
      reportCustomerWorkFinished: 'Asiakkaan ilmoittama työn valmistuminen päivitetty',
      reportCustomerValidity: 'Asiakkaan ilmoittama voimassaolo päivitetty',
    },
    progress: {
      location: 'Sijainti',
      basicInfo: 'Perustiedot',
      summary: 'Yhteenveto',
      handling: 'Käsittely',
      decision: 'Päätös',
      supervision: 'Valvonta'
    },
    status: {
      title: 'Tila',
      PENDING_CLIENT: 'Vireillä asiakasjärjestelmässä',
      PRE_RESERVED: 'Alustava varaus',
      PENDING: 'Hakemus saapunut',
      WAITING_INFORMATION: 'Odottaa täydennystä',
      INFORMATION_RECEIVED: 'Täydennys vastaanotettu',
      HANDLING: 'Käsittelyssä',
      NOTE: 'Muistiinpano',
      RETURNED_TO_PREPARATION: 'Palautettu käsittelyyn',
      WAITING_CONTRACT_APPROVAL: 'Odottaa sopimusta',
      DECISIONMAKING: 'Odottaa päätöstä',
      DECISION: 'Päätetty',
      REJECTED: 'Hylätty päätös',
      OPERATIONAL_CONDITION: 'Toiminnallinen kunto',
      FINISHED: 'Valmis',
      CANCELLED: 'Peruttu',
      REPLACED: 'Korvattu',
      ARCHIVED: 'Arkistoitu'
    },
    statusChange: {
      PRE_RESERVED: 'Alustava varaus luotu',
      PENDING: 'Hakemus saapunut',
      WAITING_INFORMATION: 'Hakemus siirretty odottamaan täydennystä',
      HANDLING: 'Hakemus siirretty käsittelyyn',
      NOTE: 'Hakemus siirretty muistiinpanoksi',
      RETURNED_TO_PREPARATION: 'Hakemus palautettu valmisteluun',
      DECISIONMAKING: 'Hakemus siirretty odottamaan päätöstä',
      DECISION: 'Hakemus päätetty',
      REJECTED: 'Hakemus hylätty',
      OPERATIONAL_CONDITION: 'Toiminnallinen kunto hyväksytty',
      FINISHED: 'Työn valmistuminen hyväksytty',
      CANCELLED: 'Hakemus peruttu',
      REPLACED: 'Hakemus korvattu',
      ARCHIVED: 'Hakemus arkistoitu'
    },
    confirmDiscard: {
      title: 'Hylätäänkö muutokset',
      description: 'Sinulla on keskeneräisiä muutoksia. Hylätäänkö muutokset?',
      confirmText: 'Hylkää',
      cancelText: 'Palaa muokkaamaan'
    },
    type: {
      title: 'Tyyppi',
      titleLong: 'Hakemuksen tyyppi',
      EVENT: 'Tapahtuma',
      SHORT_TERM_RENTAL: 'Lyhytaikainen maanvuokraus',
      CABLE_REPORT: 'Johtoselvitys',
      EXCAVATION_ANNOUNCEMENT: 'Kaivuilmoitus',
      AREA_RENTAL: 'Aluevuokraus',
      TEMPORARY_TRAFFIC_ARRANGEMENTS: 'Tilapäinen liikennejärjestely',
      PLACEMENT_CONTRACT: 'Sijoitussopimus',
      NOTE: 'Muistiinpano'
    },
    kind: {
      placeholder: {
        single: 'Hakemuksen laji',
        multiple: 'Hakemuksen lajit'
      },
      suggestion: {
        single: 'Ehdotettu laji',
        multiple: 'Ehdotetut lajit'
      },
      OUTDOOREVENT: 'Ulkoilmatapahtuma',
      BIG_EVENT: 'Suuret tapahtumat',
      PROMOTION: 'Promootio',
      AREA_RENTAL: 'Aluevuokraus',
      TEMPORARY_TRAFFIC_ARRANGEMENTS: 'Tilapäiset liikennejärjestelyt',
      BRIDGE_BANNER: 'Banderollit silloissa',
      BENJI: 'Benji-hyppylaite',
      PROMOTION_OR_SALES: 'Esittely- tai myyntitila liikkeen edustalla',
      URBAN_FARMING: 'Kaupunkiviljelypaikka',
      KESKUSKATU_SALES: 'Keskuskadun myyntipaikka',
      SUMMER_THEATER: 'Kesäteatterit',
      DOG_TRAINING_FIELD: 'Koirakoulutuskentät',
      DOG_TRAINING_EVENT: 'Koirakoulutustapahtuma',
      CARGO_CONTAINER: 'Kontti',
      SMALL_ART_AND_CULTURE: 'Pienimuotoinen taide- ja kulttuuritoiminta',
      SEASON_SALE: 'Sesonkimyynti',
      CIRCUS: 'Sirkus/tivolivierailu',
      ART: 'Taideteos',
      STORAGE_AREA: 'Varastoalue',
      STREET_AND_GREEN: 'Katu- ja vihertyöt',
      WATER_AND_SEWAGE: 'Vesi / viemäri',
      ELECTRICITY: 'Sähkö',
      DATA_TRANSFER: 'Tiedonsiirto',
      HEATING_COOLING: 'Lämmitys/viilennys',
      CONSTRUCTION: 'Rakennus',
      YARD: 'Piha',
      GEOLOGICAL_SURVEY: 'Pohjatutkimus',
      PROPERTY_RENOVATION: 'Kiinteistöremontti',
      CONTAINER_BARRACK: 'Kontti/parakki',
      PHOTO_SHOOTING: 'Kuvaus',
      SNOW_WORK: 'Lumenpudotus',
      RELOCATION: 'Muutto',
      LIFTING: 'Nostotyö',
      NEW_BUILDING_CONSTRUCTION: 'Työmaa-alue',
      ROLL_OFF: 'Vaihtolava',
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
      PUBLIC_EVENT: 'Yleisötilaisuus',
      OTHER: 'Muu'
    },
    specifier: {
      placeholder: 'Työn tarkenne',
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
    pricing: {
      ecoCompass: 'Hakijalla Ekokompassi tapahtuma -sertifikaatti (-30 %)',
      salesActivity: 'Tapahtuma sisältää kaupallista toimintaa (veloitetaan 50% normaalista hinnasta)',
      heavyStructure: 'Urheilutapahtuma sisältää raskaita rakenteita tai osallistujille maksullinen (veloitetaan 50% normaalista hinnasta)',
      commercial: 'Kaupallinen',
      calculatedPrice: 'Hinta',
      distanceFromWallOver80: 'Yli 80cm seinästä'
    },
    tag: {
      listTitle: 'Tunnisteet',
      add: 'Lisää tunniste',
      type: {
        ADDITIONAL_INFORMATION_REQUESTED: 'Täydennyspyyntö lähetetty',
        STATEMENT_REQUESTED: 'Lausunnolla',
        DEPOSIT_REQUESTED: 'Vakuus määritetty',
        DEPOSIT_PAID: 'Vakuus suoritettu',
        PRELIMINARY_SUPERVISION_REQUESTED: 'Aloitusvalvontapyyntö lähetetty',
        PRELIMINARY_SUPERVISION_DONE: 'Aloitusvalvonta suoritettu',
        PRELIMINARY_SUPERVISION_REJECTED: 'Aloitusvalvonta hylätty',
        SUPERVISION_REQUESTED: 'Valvontapyyntö lähetetty',
        SUPERVISION_REJECTED: 'Valvonta hylätty',
        SUPERVISION_DONE: 'Valvonta suoritettu',
        WAITING: 'Odottaa lisätietoa',
        COMPENSATION_CLARIFICATION: 'Hyvitysselvitys',
        PAYMENT_BASIS_CORRECTION: 'Maksuperusteet korjattava',
        OPERATIONAL_CONDITION_REPORTED: 'Toiminnallinen kunto ilmoitettu',
        SAP_ID_MISSING: 'Laskutettavan SAP-tunnus puuttuu',
        DECISION_NOT_SENT: 'Päätös lähettämättä',
        CONTRACT_REJECTED: 'Sopimusta ei hyväksytty',
        DATE_CHANGE: 'Aikamuutos',
        OTHER_CHANGES: 'Muut muutokset'
      },
      action: {
        PRELIMINARY_SUPERVISION_REQUESTED: 'Aloitusvalvontapyyntö',
        PRELIMINARY_SUPERVISION_DONE: 'Aloitusvalvonta suoritettu',
        SUPERVISION_REQUESTED: 'Valvontapyyntö',
        SUPERVISION_DONE: 'Valvonta suoritettu'
      }
    },
    publicityType: {
      PUBLIC: 'Julkinen',
      NON_PUBLIC: 'Ei-julkinen',
      CONFIDENTIAL_PARTIALLY: 'Osittain salassa pidettävä',
      CONFIDENTIAL: 'Salassa pidettävä'
    },
    trafficArrangementImpedimentType: {
      NO_IMPEDIMENT: 'Ei haittaa',
      SIGNIFICANT_IMPEDIMENT: 'Merkittävä haitta',
      IMPEDIMENT_FOR_HEAVY_TRAFFIC: 'Haittaa raskasta liikennettä',
      INSIGNIFICANT_IMPEDIMENT: 'Vähäinen haitta'
    },
    field: {
      nameMissing: 'Tapahtuman nimi puuttuu',
      nameShort: 'Tapahtuman nimi on liian lyhyt',
      recurringEndYearNotBetween: 'Virheellinen toistuvuuden päättymisvuosi',
      invoiceRecipientMissing: 'Laskutettavaa asiakasta ei ole valittu',
      kindMissing: 'Hakemuksen laji puuttuu',
      receivedTimeMissing: 'Hakemuksen saapumispäivä puuttuu'
    },
    common: {
      startTime: 'Voimassaolon aloitus',
      endTime: 'Voimassaolon lopetus',
      area: 'Pinta-ala',
      pksCard: 'Pks-kortti',
      propertyConnectivity: 'Tontti- / Kiinteistöliitos',
      selfSupervision: 'Omavalvonta',
      constructionWork: 'Rakentaminen',
      maintenanceWork: 'Kunnossapito',
      emergencyWork: 'Hätätyö',
      additionalInfo: 'Lisätiedot',
      workPurpose: 'Työn tarkoitus',
      trafficArrangements: 'Suoritettavat liikennejärjestelyt',
      field: {
        validityStartTimeMissing: 'Voimassaolon aloitus puuttuu',
        validityEndTimeMissing: 'Voimassaolon lopetus puuttuu',
        validityStartBeforeEnd: 'Voimassaolon lopetus ei voi olla ennen aloitusta'
      }
    },
    event: {
      title: 'Tapahtuman tiedot',
      name: 'Tapahtuman nimi',
      startTime: 'Tapahtuman alkupäivämäärä',
      endTime: 'Tapahtuman loppupäivämäärä',
      description: 'Tapahtuman kuvaus',
      timeExceptions: 'Tapahtuma-ajan poikkeukset',
      url: 'Tapahtuman www-sivu',
      attendees: 'Yleisömäärä',
      entryFee: 'Osallistumismaksu, jos urheilutapahtuma',
      structures: 'Rakenteet',
      hasStructures: 'Tapahtuma sisältää rakenteita',
      structureArea: 'Rakenteiden kokonaisneliömäärä',
      structureDescription: 'Rakenteiden kuvaus',
      structureTimeRequired: 'Tapahtuma vaatii erillisiä rakennus- tai purkupäiviä',
      structureStartTime: 'Rakennuspäivämäärä',
      structureEndTime: 'Purkupäivämäärä',
      marketingProvidersTitle: 'Myyntitoiminta',
      hasMarketingProviders: 'Tapahtuma sisältää myynti- tai mainostoimintaa',
      marketingProvidersDescription: 'Myynti- tai mainostoiminnan kuvaus',
      hasFoodSales: 'Tapahtuma sisältää elintarvikemyyntiä tai tarjoilua',
      foodProviders: 'Tapahtuma sisältää elintarvikemyyntiä tai tarjoilua',
      billingType: {
        CASH: 'Käteinen',
        INVOICE: 'Lasku'
      },
      nature: {
        placeholder: 'Tapahtuman luonne',
        PUBLIC_FREE: 'Avoin',
        PUBLIC_NONFREE: 'Maksullinen',
        CLOSED: 'Suljettu'
      },
      surface: {
        SOFT: 'Pehmeäpintainen',
        HARD: 'Kovapintainen'
      },
      notBillableReason: {
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
        buildBeforeEventStart: 'Rakennuspäivien tulee olla ennen tapahtumaa',
        teardownAfterEventEnd: 'Purkupäivien tulee olla tapahtuman jälkeen'
      }
    },
    shortTermRental: {
      info: 'Vuokrauksen tiedot',
      name: 'Vuokrauksen nimi',
      description: 'Vuokrauksen kuvaus',
      commercial: 'Kaupallinen',
      nonCommercial: 'Ei kaupallinen',
      field: {
        descriptionMissing: 'Vuokrauksen kuvaus puuttuu',
        rentalStartTimeMissing: 'Vuokrauksen alkuaika puuttuu',
        rentalEndTimeMissing: 'Vuokrauksen loppuaika puuttuu',
        startBeforeEnd: 'Loppumispäivä ei voi olla ennen alkamispäivää'
      },
      notBillableReason: 'Myyntitila alle 0.8m seinästä veloituksetta'
    },
    cableReport: {
      cableInfo: {
        title: 'Johtotiedot',
        selectInfoType: 'Valitse tyyppi',
        mapExtractCount: 'Karttaotteiden määrä',
        additionalInfo: 'Lisätietoja'
      },
      field: {
        startBeforeEnd: 'Lopetuspäivä ei voi olla ennen aloituspäivää'
      }
    },
    excavationAnnouncement: {
      validity: 'Kaivuilmoituksen voimassaolo',
      setToWinterTimeEnd: 'Aseta loppupäivämääräksi talviajan loppupäivä (14.5.)',
      unauthorizedStartTime: 'Luvattoman kaivun aloitus',
      unauthorizedEndTime: 'Luvattoman kaivun lopetus',
      winterTimeOperation: 'Talvityön toiminn. kunto',
      workFinished: 'Työ valmis',
      guaranteeEndTime: 'Takuu päättyy (katutyö)',
      validityReportedByCustomer: 'Asiakkaan ilmoittama voimassaolo',
      excavationInfo: 'Kaivuilmoituksen tiedot',
      cableReports: 'Johtoselvitykset',
      placementContracts: 'Sijoitussopimukset',
      compactionAndBearingCapacityMeasurement: 'Tiiveys- ja kantavuusmittaus',
      qualityAssuranceTest: 'Päällysteen laadunvarmistuskoe',
      field: {
        inWinterTime: 'Hakemus päättyy talvityöaikana',
        unauthorizedWorkStartBeforeEnd: 'Voimassaolon lopetus ei voi olla ennen aloitusta'
      },
      error: {
        setRequiredTasks: 'Vaadittujen toimenpiteiden päivittäminen epäonnistui',
      }
    },
    note: {
      field: {
        validityStartTimeMissing: 'Alkupäivämäärä puuttuu',
        validityEndTimeMissing: 'Loppupäivämäärä puuttuu',
        validityDurationAtMax: 'Vuosittain toistuvan muistiinpanon keston täytyy olla alle vuosi'
      }
    },
    trafficArrangement: {
      workPurpose: 'Työn tarkoitus',
      field: {
        validityStartTimeMissing: 'Voimassaolon aloitus puuttuu',
        validityEndTimeMissing: 'Voimassaolon lopetus puuttuu',
        validityStartBeforeEnd: 'Voimassaolon lopetus ei voi olla ennen aloitusta'
      }
    },
    placementContract: {
      contractText: 'Sopimusteksti',
      propertyIdentificationNumber: 'Kiinteistötunnus',
      terminationDate: 'Irtisanomispäivä'
    },
    areaRental: {
      workFinished: 'Työ valmis',
      customerWorkFinished: 'Työ valmis (Asiakkaan ilmoittama)',
      majorDisturbance: 'Vähäistä suurempaa haittaa aiheuttava työ',
      error: {
        reportWorkFinished: 'Työn valmistumisen ilmoittaminen epäonnistui'
      },
      action: {
        reportWorkFinished: 'Työn valmistuminen ilmoitettu'
      }
    },
    button: {
      copy: 'Kopioi uudeksi',
      replace: 'Korvaava päätös',
      toHandling: 'Käsittelyyn',
      informationRequest: 'Täydennyspyyntö',
      cancelInformationRequest: 'Peru täydennyspyyntö',
      cancel: 'Peru hakemus',
      convertToApplication: 'Vahvista varaus',
      toDecision: 'Päättämiseen',
      showPending: 'Näytä uudet tiedot',
      handleInformationRequestResponse: 'Käsittele täydennyspyyntö',
      toDecisionMaking: 'Ehdota päätettäväksi',
      basicInfo: 'Jatka perustietoihin'
    },
    confirmCancel: {
      title: 'Haluatko varmasti perua hakemuksen?',
      confirmText: 'Hyväksy',
      cancelText: 'Hylkää'
    },
    clientData: {
      newValue: 'Kortti sisältää uusia tietoja: {{value}}',
      newData: 'Kortti sisältää uusia tietoja.',
      show: 'Näytä'
    },
    notification: {
      type: {
        INFORMATION_REQUEST_DRAFT: 'Hakemuksella on lähettämätön täydennyspyyntö.',
        INFORMATION_REQUEST_PENDING: 'Hakemus odottaa täydennystä.',
        INFORMATION_REQUEST_RESPONSE: 'Hakemus sisältää tietopäivityksiä.',
        PENDING_CLIENT_DATA: 'Hakemus sisältää tietopäivityksiä.'
      },
      show: 'Näytä'
    },
    identifiers: {
      searchAndAdd: 'Etsi ja lisää hakemustunnuksia',
      removeFromApplication: 'Poista hakemukselta',
      field: {
        isNumber: 'Tunnus saa sisältää vain numeroita',
        requiredLength: 'Sallittu tunnuksen pituus on {{length}} numeroa'
      }
    }
  },
  applicationSearch: {
    title: 'Hakemukset',
    applicationId: 'Hakemuksen tunnus',
    type: 'Hakemuksen tyyppi',
    status: 'Hakemuksen tila',
    owner: 'Hakemuksen omistaja',
    address: 'Osoite',
    startTime: 'Alkupäivä',
    endTime: 'Loppupäivä',
  },
  applicationInfo: {
    title: 'Hakemuksen perustiedot',
    type: 'Hakemuksen tyyppi',
    creationTime: 'Hakemus saapunut',
    kinds: 'Hakemuksen laji(t)',
    startTime: 'Arvioitu aloitus',
    endTime: 'Arvioitu päättyminen',
    name: 'Hakemuksen nimi',
    location: 'Sijainti',
    handler: 'Käsittelijä'
  },
  dateReporting: {
    title: {
      customer: {
        winterTimeOperation: 'Asiakkaan ilmoittama toiminnallinen kunto',
        workFinished: 'Asiakkaan ilmoittama työ valmis',
        validity: 'Asiakkaan ilmoittama voimassaolo'
      },
      official: {
        winterTimeOperation: 'Virallinen toiminnallinen kunto',
        workFinished: 'Virallinen työ valmis'
      }
    },
    reportedInfo: {
      winterTimeOperation: 'Talvityön toiminnallinen kunto',
      workFinished: 'Työ valmis'
    },
    dateField: {
      reportedDate: 'Päivä',
      winterTimeOperation: 'Toiminnallinen kunto',
      workFinished: 'Työ valmis',
      validity: 'Voimassaolon aloitus',
      reportedStartDate: 'Voimassaolon aloitus',
      reportedEndDate: 'Voimassaolon lopetus',
      reportingDate: 'Ilmoituspäivä'
    },
    field: {
      reportedDateMissing: 'Päivä puuttuu',
      reportingDateMissing: 'Ilmoituspäivä puuttuu'
    },
    customerReportedDate: 'Asiakkaan ilmoittama päivä'
  },
  applicationBasket: {
    createNewProject: 'Luo uusi hanke',
    addToExistingProject: 'Hae kohdehanke',
    clear: 'Tyhjennä kori',
    empty: 'Hakemuskori on tyhjä',
    addTo: 'Lisää koriin',
    applicationAdded: 'Hakemus lisätty koriin',
    applicationsAdded: 'Hakemukset lisätty koriin',
    applicationRemoved: 'Hakemus poistettu korista'
  },
  TERMS: {
    title: 'Ehdot',
    selectTerm: 'Valitse ehto',
    applicationTerms: 'Hakemuksen ehdot',
    missing: 'Ehdot puuttuvat'
  },
  NOT_BILLABLE: {
    title: 'Ei laskuteta peruste',
    selectTerm: 'Valitse valmis peruste',
    applicationTerms: 'Peruste',
    missing: 'Peruste puuttuu'
  },
  OTHER: {
    title: 'Päätöksen perustelut',
    selectTerm: 'Valitse perustelu',
    applicationTerms: 'Päätöksen perustelut',
    missing: 'Päätöksen perustelut puuttuvat'
  },
  TRAFFIC_ARRANGEMENT: {
    title: 'Suoritettavat liikennejärjestelyt',
    selectTerm: 'Valitse liikennejärjestely',
    applicationTerms: 'Suoritettavat liikennejärjestelyt',
    missing: 'Suoritettavat liikennejärjestelyt puuttuvat'
  },
  informationRequestResponse: {
    error: {
      fetch: 'Täydennyspyynnön hakeminen epäonnistui',
      close: 'Täydennyspyynnön sulkeminen epäonnistui'
    }
  },
  informationRequest: {
    acceptance: {
      title: 'Hyväksy tietopäivitykset',
      readonlyTitle: 'Ehdotetut tietopäivitykset',
      noChange: 'Älä vaihda',
      change: 'Vaihda',
      close: 'Sulje',
      existingInfo: 'Nykyiset tiedot',
      providedInfo: 'Tarjotut tiedot'
    },
    request: {
      title: 'Hyväksy tiedot täydennyspyyntöön',
      readonlyTitle: 'Pyydetyt täydennykset',
      description: `Merkitse tiedot jotka puuttuvat, ovat virheellisiä tai jotka haluat muusta syystä päivittää.
       Huomaa, että et voi päivittää hakemusta niin kauan kuin sillä on vastaamattomia täydennyspyyntöjä.`,
      guide: 'Selite',
      send: 'Lähetä pyyntö',
      draft: 'Tallenna luonnos'
    },
    field: {
      CUSTOMER: 'Hakija',
      PROPERTY_DEVELOPER: 'Rakennuttaja',
      CONTRACTOR: 'Työn suorittaja',
      REPRESENTATIVE: 'Asiamies',
      INVOICING_CUSTOMER: 'Laskutettava',
      GEOMETRY: 'Geometria',
      AREA: 'Pinta-ala',
      START_TIME: 'Aloituspäivämäärä',
      END_TIME: 'Lopetuspäivämäärä',
      IDENTIFICATION_NUMBER: 'Asiointitunnus',
      CLIENT_APPLICATION_KIND: 'Asiakasjärjestelmän hakemuksen laji',
      APPLICATION_KIND: 'Hakemuksen laji',
      POSTAL_ADDRESS: 'Postiosoite',
      WORK_DESCRIPTION: 'Työn kuvaus',
      PROPERTY_IDENTIFICATION_NUMBER: 'Kiinteistötunnus',
      ATTACHMENT: 'Liitteet',
      OTHER: 'Muu',
      others: 'Muut tiedot',
      valueDiffers: 'Kentän sisältö poikkeaa nykyisestä',
      descriptionMissing: 'Selite puuttuu'
    },
    error: {
      create: 'Täydennyspyynnön luominen epäonnistui',
      update: 'Täydennyspyynnön päivittäminen epäonnistui',
      delete: 'Täydennyspyynnön poistaminen epäonnistui',
    }
  },
  project: {
    title: 'Hanke',
    activeApplications: 'Aktiivista hakemusta',
    decidedApplications: 'Päätettyä hakemusta',
    showBasicInfo: 'Näytä perustiedot',
    info: {
      title: 'Perustiedot',
      identifier: 'Hanketunnus',
      ownerType: 'Omistajan tyyppi',
      ownerName: 'Hankkeen omistaja',
      contactName: 'Yhteyshenkilön nimi',
      contactPhone: 'Puhelin',
      contactEmail: 'Sähköpostiosoite',
      contactInfo: 'Yhteystiedot',
      cityDistricts: 'Kaupunginosa(t)',
      email: 'Sähköposti',
      phone: 'Puhelinnumero',
      name: 'Hankkeen nimi',
      customerReference: 'Asiakkaan viite/työnumero',
      additionalInfo: 'Lisätietoja'
    },
    field: {
      identifierMissing: 'Hanketunnus puuttuu',
      ownerMissing: 'Hankkeen omistaja puuttu',
      contactMissing: 'Hankkeen yhteyshenkilö puuttuu',
      customerReferenceMissing: 'Asiakkaan viite/työnumero puuttuu'
    },
    id: 'Tunnus',
    ownerName: 'Omistaja',
    state: 'Tila',
    active: {
      'title': 'Tila',
      'false': 'Inaktiivinen',
      'true': 'Aktiivinen'
    },
    startTime: 'Alku',
    endTime: 'Loppu',
    cityDistricts: 'Kaupunginosa(t)',
    error: {
      saveFailed: 'Hankkeen tallentaminen epäonnistui',
      searchFailed: 'Hankkeiden hakeminen epäonnistui',
      fetchFailed: 'Hankkeen hakeminen epäonnistui',
      applicationFetchFailed: 'Hankkeen hakemusten hakeminen epäonnistui',
      applicationAddFailed: 'Hakemuksen lisääminen hankkeelle epäonnistui',
      applicationRemoveFailed: 'Hakemuksen poistaminen hankkeelta epäonnistui',
      updateParentFailed: 'Hankkeen päivitys epäonnistui',
      removeParentFailed: 'Hankkeen poistaminen epäonnistui',
      removeFailed: 'Hankkeen poistaminen epäonnistui'
    },
    applications: {
      title: 'Hakemukset',
      removeFromProject: 'Poista hankkeesta',
      searchAndAdd: 'Etsi ja lisää hakemuksia',
      moveFromAnother: 'Hakemus kuuluu hankkeeseen {{project}} ja siirretään uuteen hankkeeseen',
      fromBasket: 'Tuo korista'
    },
    relatedProjects: {
      title: 'Liittyvät hankkeet',
      count: 'Liittyvää hanketta',
      searchAndAdd: 'Etsi ja lisää hankkeita',
      moveFromAnother: 'Hanke kuuluu toiseen hankkeeseen ja siirretään uuteen hankkeeseen'
    },
    button: {
      addApplication: 'Lisää hakemus',
      remove: 'Poista hanke'
    },
    confirmCancel: {
      title: 'Haluatko varmasti poistaa hankkeen?',
      description: 'Hanke ja kaikki siihen liittyvät hankkeet poistetaan.',
      confirmText: 'Poista hanke',
      cancelText: 'Hylkää'
    }
  },
  customer: {
    newCustomer: 'Luo uusi asiakas',
    nameLabel: 'Nimi',
    registryKey: 'Tunniste',
    email: 'Sähköposti',
    phone: 'Puhelin',
    postalAddress: 'Osoite',
    addNew: 'Lisää uusi asiakas',
    invoiceRecipient: 'Laskun saaja',
    handledByRepresentative: 'Asiaa hoitaa asiamies',
    hasPropertyDeveloper: 'Hakemuksella on rakennuttaja',
    sapCustomerNumber: 'Sap-numero',
    invoicingProhibited: 'Asiakkaalla laskutuskielto',
    invoicingOnly: 'Vain laskutusasiakas',
    projectIdentifierPrefix: 'Projektitunnuksen alkuosa',
    country: 'Maa',
    type: {
      pick: 'Valitse tyyppi',
      title: 'Tyyppi',
      COMPANY: {
        name: 'Yritys',
        nameLabel: 'Yrityksen nimi',
        id: 'Y-tunnus',
        ovt: 'OVT-tunnus',
        invoicingOperator: 'Välittäjän tunnus'
      },
      ASSOCIATION: {
        name: 'Yhdistys',
        nameLabel: 'Yhdistyksen nimi',
        id: 'Y-tunnus',
        ovt: 'OVT-tunnus',
        invoicingOperator: 'Välittäjän tunnus'
      },
      PERSON: {
        name: 'Yksityishenkilö',
        nameLabel: 'Henkilön nimi',
        id: 'Henkilötunnus',
        ovt: 'OVT-tunnus',
        invoicingOperator: 'Välittäjän tunnus'
      },
      PROPERTY: {
        name: 'Kiinteistö',
        nameLabel: 'Kiinteistön nimi',
        id: 'Kiinteistötunnus',
        ovt: 'OVT-tunnus',
        invoicingOperator: 'Välittäjän tunnus'
      },
      OTHER: {
        name: 'Muu',
        nameLabel: 'Asiakkaan nimi',
        id: 'Y-tunnus',
        ovt: 'OVT-tunnus',
        invoicingOperator: 'Välittäjän tunnus'
      },
      DEFAULT: {
        name: 'Asiakas',
        nameLabel: 'Asiakkaan nimi',
        id: 'Y-tunnus',
        ovt: 'OVT-tunnus',
        invoicingOperator: 'Välittäjän tunnus'
      }
    },
    role: {
      APPLICANT: {
        title: 'Hakija'
      },
      PROPERTY_DEVELOPER: {
        title: 'Rakennuttaja'
      },
      CONTRACTOR: {
        title: 'Työn suorittaja'
      },
      REPRESENTATIVE: {
        title: 'Asiamies'
      },
    },
    field: {
      typeMissing: 'Tyyppi puuttuu',
      nameMissing: 'Nimi puuttuu',
      nameShort: 'Nimi on liian lyhyt',
      registryKeyShort: 'Tunniste on liian lyhyt',
      ovtShort: 'OVT-tunnus liian lyhyt',
      ovtLong: 'OVT-tunnus liian pitkä',
      emailInvalid: 'Virheellinen sähköpostiosoite',
      phoneShort: 'Puhelinnumero on liian lyhyt',
      invalidSsn: 'Virheellinen henkilötunnus',
      countryMissing: 'Maa puuttuu'
    },
    action: {
      save: 'Asiakas tallennettu',
      removeFromRegistry: 'Asiakas poistettu rekisteristä'
    },
    error: {
      fetch: 'Asiakkaan hakeminen epäonnistui',
      fetchContacts: 'Asiakkaan yhteyshenkilöiden hakeminen epäonnistui',
      save: 'Asiakkaan tallentaminen epäonnistui',
      remove: 'Asiakkaan poistaminen rekisteristä epäonnistui'
    },
    search: {
      name: 'Asiakkaan nimi',
      registryKey: 'Asiakkaan tunniste',
      type: 'Asiakkaan tyyppi',
      search: 'Hae asiakasta',
    },
    suggestion: {
      title: 'Ehdotettu asiakas',
      titleInvoice: 'Ehdotettu laskutusasiakas',
      info: 'Nimi yhdistetään samoilla tiedoilla löytyneeseen asiakkaaseen. Voit myös hakea vaihtoehtoa tai luoda nimellä uuden asiakkaan.'
    },
    useForInvoicing: 'Käytä laskutettavana asiakkaana',
    createNew: 'Luo uusi asiakas'
  },
  contact: {
    title: 'Yhteyshenkilö',
    name: 'Yhteyshenkilön nimi',
    orderer: 'Tilaaja',
    role: {
      APPLICANT: {
        title: 'Yhteyshenkilö'
      },
      PROPERTY_DEVELOPER: {
        title: 'Yhteyshenkilö'
      },
      CONTRACTOR: {
        title: 'Vastuuhenkilö'
      },
      REPRESENTATIVE: {
        title: 'Yhteyshenkilö'
      }
    },
    field: {
      nameMissing: 'Nimi puuttuu',
      nameShort: 'Nimi on liian lyhyt',
      emailInvalid: 'Virheellinen sähköpostiosoite',
      phoneShort: 'Puhelinnumero on liian lyhyt'
    },
    error: {
      remove: 'Yhteyshenkilön poistaminen epäonnistui',
      fetch: 'Yhteyshenkilön hakeminen epäonnistui'
    },
    action: {
      remove: 'Yhteyshenkilö poistettu',
      save: 'Yhteyshenkilö tallennettu'
    },
    suggestion: {
      title: 'Ehdotettu yhteyshenkilö',
      info: `Nimi yhdistetään samoilla tiedoilla löytyneeseen yhteyshenkilöön.
       Voit myös hakea vaihtoehtoa tai luoda nimellä uuden yhteyshenkilön.`
    },
    createNew: 'Luo uusi yhteyshenkilö',
    search: 'Hae yhteyshenkilöä',
    addToDistribution: 'Lisää jakeluun'
  },
  geolocation: {
    error: {
      searchFailed: 'Osoitteen haku epäonnistui'
    }
  },
  decision: {
    selectHandler: 'Valitse käsittelijä',
    decisionAttachments: 'Päätöksen liitteet',
    contractAttachments: 'Sopimuksen liitteet',
    reason: 'Perustelut',
    emailMessage: 'Sähköpostiviesti',
    sendDecision: 'Lähetä päätös',
    cannotShow: 'Päätöksen näyttäminen ei onnistu selaimellasi.',
    toOperationalCondition: 'Päätä toiminnalliseen kuntoon',
    type: {
      DECISIONMAKING: {
        title: 'Siirrä odottamaan päätöstä',
        confirmText: 'Päätettäväksi',
        confirmation: 'Hakemus siirretty odottamaan päätöstä'
      },
      DECISION: {
        title: 'Päätä hakemus',
        confirmText: 'Päätä',
        confirmation: 'Hakemus päätetty'
      },
      OPERATIONAL_CONDITION: {
        title: 'Hyväksy toiminnallinen kunto',
        confirmText: 'Hyväksy',
        confirmation: 'Toiminnallinen kunto hyväksytty'
      },
      FINISHED: {
        title: 'Työn valmistumisen hyväksyminen',
        confirmText: 'Hyväksy',
        confirmation: 'Työn valmistuminen hyväksytty'
      },
      RETURNED_TO_PREPARATION: {
        title: 'Palauta hakemus',
        confirmText: 'Palauta',
        confirmation: 'Hakemus palautettu valmisteluun'
      },
      REJECTED:  {
        title: 'Hylkää hakemus',
        confirmText: 'Hylkää',
        confirmation: 'Hakemus hylätty'
      },
      RESEND_EMAIL: {
        title: 'Lähetä päätös',
        confirmText: 'Lähetä',
        confirmation: 'Päätös lähetetty'
      }
    },
    tab: {
      DECISION: 'Päätös',
      CONTRACT: 'Sopimus',
      OPERATIONAL_CONDITION: 'Toiminnallinen kunto',
      WORK_FINISHED: 'Työ valmis'
    },
    field: {
      handlerMissing: 'Käsittelijä puuttuuu',
      commentMissing: 'Perustelut puuttuvat'
    },
    proposal: {
      selectDecisionMaker: 'Valitse päättäjä',
      comment: 'Perustelut',
      type: {
        PROPOSE_APPROVAL: 'Ehdota hyväksymistä',
        PROPOSE_REJECT: 'Ehdota hylkäystä'
      },
      field: {
        commentMissing: 'Perustelut puuttuvat',
        handlerMissing: 'Päättäjä puuttuu'
      }
    },
    distribution: {
      title: 'Päätöksen jakelu',
      type: {
        EMAIL: 'Sähköposti',
        PAPER: 'Paperi'
      },
      error: {
        fetch: 'Jakelutietojen hakeminen epäonnistui',
        save: 'Jakelutietojen päivittäminen epäonnistui',
        remove: 'Jakelutietojen poistaminen'
      }
    },
    error: {
      generatePdf: 'Pdf:n muodostaminen epäonnistui',
      send: 'Päätöksen lähettäminen sähköpostijakeluna epäonnistui'
    },
    action: {
      send: 'Päätös lähettetty'
    }
  },
  contract: {
    createProposal: 'Hyväksytä asiakkaalla',
    proposeApproval: 'Ehdota hyväksymistä',
    approval: {
      title: 'Ehdota hyväksymistä',
      basis: {
        contractAsAttachment: 'Asiakkaan allekirjoittama sopimus liitteenä',
        frameAgreementExists: 'Asiakkaan kanssa muu sopimusjärjestely'
      },
      selectDecisionMaker: 'Valitse päättäjä',
      comment: 'Perustelut',
      field: {
        basisMissing: 'Peruste puuttuu',
        commentMissing: 'Perustelut puuttuvat'
      },
    },
    error: {
      createProposalFailed: 'Sopimusluonnoksen luonti epäonnistui',
      approveFailed: 'Sopimuksen hyväksyntä epäonnistui'
    }
  },
  approvalDocument: {
    error: {
      fetchFailed: 'Hyväksyntädokumentin hakeminen epäonnistui'
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
      ROLE_DECLARANT: 'Lausunnonantaja',
      ROLE_ADMIN: 'Ylläpito'
    },
    userInfo: 'Käyttäjän tiedot',
    addNew: 'Lisää uusi käyttäjä',
    username: 'Käyttäjänimi',
    name: 'Nimi',
    title: 'Tehtävänimike',
    email: 'Sähköpostiosoite',
    phone: 'Puhelinnumero',
    active: 'Aktiivinen',
    lastLogin: 'Viimeisin kirjautuminen',
    roles: 'Roolit',
    applicationTypes: 'Hakemustyypit',
    cityDistricts: 'Kaupunginosat',
    error: {
      search: 'Käyttäjien etsiminen epäonnistui'
    }
  },
  externalUser: {
    username: 'Käyttäjänimi',
    name: 'Nimi',
    email: 'Sähköposti',
    lastLogin: 'Viimeisin kirjautuminen',
    roles: 'Roolit',
    expirationTime: 'Voimassa',
    role: {
      ROLE_INTERNAL: 'Sisäinen',
      ROLE_TRUSTED_PARTNER: 'Luotettu kumppani'
    },
    field: {
      expirationTimeMissing: 'Voimassaoloaika puuttuu'
    },
    actions: {
      saved: 'Rajapintakäyttäjä tallennettu.',
      customerTokenGenerated: 'Asiakasavain luotiin uudestaan. <br> Luotu avain tulee toimittaa asiakkaalle kirjatumista varten.'
    }
  },
  storedFilter: {
    selectPlaceholder: 'Pikavalinnat',
    addCurrent: 'Lisää nykyinen näkymä',
    action: {
      save: 'Suodatin tallennettu',
      remove: 'Suodatin poistettu',
    },
    error: {
      fetch: 'Suodattimien hakeminen epäonnistui',
      create: 'Suodattimen luonti epäonnistui',
      update: 'Suodattimen päivitys epäonnistui',
      remove: 'Suodattimen poistaminen epäonnistui',
      setDefault: 'Oletussuodattimen asettaminen epäonnistui'
    }
  },
  storedFilterModal: {
    title: 'Lisää pikavalinta',
    description: {
      MAP: 'Pikanvalintaan tallentuu nykyinen karttataso, tehty haku ja käytetyt suodattimet.',
      WORKQUEUE: 'Pikanvalintaan tallentuu nykyinen suodatus',
      SUPERVISION_WORKQUEUE: 'Pikanvalintaan tallentuu nykyinen suodatus',
      APPLICATION_SEARCH: 'Pikanvalintaan tallentuu nykyinen suodatus',
      PROJECT_SEARCH: 'Pikanvalintaan tallentuu nykyinen suodatus'
    },
    namePlaceholder: 'Erottuva nimi pikavalinnalle',
    saveAsDefault: 'Tallenna oletusnäkymäksi'
  },
  defaultText: {
    placeholder: 'Vakiotekstit',
    actions: {
      saved: 'Vakiotekstit tallennettu'
    },
    error: {
      fetch: 'Vakiotekstien hakeminen epäonnistui',
      saveFailed: 'Vakiotekstin tallentaminen epäonnistui',
      remove: 'Vakiotekstin poistaminen epäonnistui'
    },
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
      TERMS: 'Ehdot',
      TRAFFIC_ARRANGEMENT: 'Suoritettavat liikennejärjestelyt',
      NOT_BILLABLE: 'Ei laskuteta peruste',
      OTHER: 'Yleisesti/muut'
    }
  },
  location: {
    title: 'Sijainti',
    stored: 'Tallennetut alueet',
    identifier: 'Tunniste',
    startTime: 'Aloitus',
    endTime: 'Lopetus',
    postalAddress: 'Osoite',
    additionalInfo: 'Lisätietoja paikasta',
    fixedArea: 'Alue',
    sections: 'Lohkot',
    area: 'Alueen pinta-ala',
    areaOverride: 'Käsittelijän syöttämä pinta-ala',
    cityDistrict: 'Kaupunginosa',
    cityDistrictOverride: 'Käsittelijän valitsema kaupunginosa',
    underpass: 'Altakuljettava',
    paymentTariff: 'Maksuluokka',
    paymentTariffOverride: 'Käsittelijän valitsema maksuluokka',
    paymentTariffValue: 'Maksuluokka {{tariff}}',
    paymentTariffUndefined: 'Maksuluokka tuntematon',
    locationKey: 'Sijainti {{key}}',
    customerStartTime: 'Asiakkaan ilmoittama aloitus',
    customerEndTime: 'Asiakkaan ilmoittama lopetus',
    fixedLocationNotAllowed: 'Kiinteää aluetta ei voi valita, mikäli kartalle on piirretty',
    noAreaSelected: 'Ei valittua sijaintia',
    geometry: 'Alue',
    error: {
      fetchFixedLocations: 'Vakiosijaintien hakeminen epäonnistui',
      fetchCityDistricts: 'Kaupunginosien hakeminen epäonnistui',
      addressSearch: 'Osoitehaku epäonnistui'
    },
    button: {
      save: 'Tallenna sijaintitiedot'
    },
    action: {
      saved: 'Sijaintitiedot tallennettu'
    }
  },
  postalAddress: {
    streetAddress: 'Katuosoite',
    postalCode: 'Postinumero',
    postalOffice: 'Postitoimipaikka'
  },
  emailAddress: 'Sähköpostiosoite',
  phone: 'Puhelin',
  name: 'Nimi',
  common: {
    freeTextSearch: 'Vapaa tekstihaku',
    select: 'Valitse',
    selectAll: 'Valitse kaikki',
    saving: 'Tallennus',
    field: {
      usernameMissing: 'Käyttäjänimi puuttuu',
      nameMissing: 'Nimi puuttuu',
      emailInvalid: 'Virheellinen sähköpostiosoite',
      postalCode: 'Tarkista postinumero',
      dayInvalid: 'Virheellinen päivä',
      monthInvalid: 'Virheellinen kuukausi'
    },
    boolean: {
      'true': 'Kyllä',
      'false': 'Ei'
    },
    action: {
      remove: 'poistettu',
      canDownload: 'Voit ladata sen'
    },
    button: {
      ok: 'HYVÄKSY',
      save: 'TALLENNA',
      cancel: 'PERUUTA',
      remove: 'POISTA',
      edit: 'MUOKKAA',
      home: 'Etusivulle',
      add: 'Lisää',
      show: 'Näytä',
      search: 'Hae',
      toSelf: 'Omaksi',
      moveTo: 'Siirrä',
      close: 'Sulje'
    },
    time: {
      today: 'Tänään',
      withinWeek: 'Viikon sisällä',
      older: 'Vanhemmat',
    },
    error: {
      downloadFailed: 'Tiedoston lataus epäonnistui'
    },
    paginator: {
      itemsPerPage: 'Tuloksia sivulla',
      nextPage: 'Seuraava',
      previousPage: 'Edellinen',
      of: '/'
    },
    fromHere: 'tästä',
    totalPrice: 'Kokonaissumma'
  },
  map: {
    zoomIn: 'Lähennä',
    zoomOut: 'Loitonna',
    areasIntersect: 'Alue leikkaa toisen alueen',
    areaIntersects: 'Alue leikkaa itsensä',
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
        },
        lineWidth: {
          title: 'Viivan leveys',
          text: 'Leveys'
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
        circlemarker: {
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
          },
          clearAll: {
            title: 'Poista kaikki',
            text: 'Poista kaikki'
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
    },
    measure: {
      lang: {
        totalLength: 'Kokonaispituus',
        totalArea: 'Kokonaispinta-ala (arvio)',
        segmentLength: 'Sivun pituus'
      }
    },
    popup: {
      titleMultipleFeatures: '{{count}} hakemusta'
    },
    layers: {
      placeholder: 'Taustakartat'
    }
  },
  mapsearch: {
    advanced: 'Tarkennettu haku',
    results: 'Hakutulokset',
    loading: 'Ladataan hakemuksia',
    newApplication: 'Uusi hakemus'
  },
  searchbar: {
    address: 'Kirjoita osoite',
    types: 'Valitse tyypit',
    status: {
      PRELIMINARY: 'Alustava',
      HANDLING: 'Käsittelyssä',
      DECISION: 'Päätetty',
      HISTORY: 'Historia'
    }
  },
  sidebar: {
    title:  {
      BASIC_INFO: 'Perustiedot',
      ATTACHMENTS: 'Liitteet',
      INVOICING: 'Laskutus',
      COMMENTS: 'Kommentit',
      EMAIL: 'Sähköposti',
      HISTORY: 'Historia',
      SUPERVISION: 'Valvonta',
      PROJECTS: 'Hankkeet',
      DECISION: 'Päätös'
    }
  },
  attachments: 'Liitteet',
  attachment: {
    title: 'Liite',
    addNew: 'Lisää liite',
    select: 'Valitse liite',
    description: 'Liitteen kuvaus',
    decisionAttachment: 'Päätöksen liite',
    unknownHandler: 'Tuntematon lisääjä',
    noArea: 'Ei aluetta',
    onlyPdfForDecision: 'Vain pdf-tiedostoja voidaan lisätä päätöksen liitteeksi',
    type: {
      title: 'Liitteen tyyppi',
      ADDED_BY_CUSTOMER: 'Asiakkaan lisäämä liite',
      ADDED_BY_HANDLER: 'Käsittelijän lisäämä liite',
      DEFAULT: 'Hakemustyyppikohtainen vakioliite',
      DEFAULT_IMAGE: 'Hakemustyyppikohtainen tyyppikuvaliite',
      DEFAULT_TERMS: 'Hakemustyyppikohtainen ehtoliite',
      SUPERVISION: 'Valvontaliite',
      STATEMENT: 'Lausunto',
      OTHER: 'Muu'
    },
    selectionTitle: {
      DEFAULT: 'Vakioliitteet',
      DEFAULT_IMAGE: 'Tyyppikuvat'
    },
    action: {
      added: 'Liite {{name}} lisätty hakemukselle',
      deleted: 'Liite {{name}} poistettu',
      confirmDelete: 'Haluatko varmasti poistaa liitteen'
    },
    error: {
      defaultAttachmentByArea: 'Vakioliitteen automaattinen lisääminen alueen perusteella epäonnistui. ' +
      'Voit lisätä vakioliitteen Liitteet-välilehdeltä',
      addFailed: 'Liiteen {{name}} tallennus epäonnistui',
      deleteFailed: 'Liiteen {{name}} poistaminen epäonnistui',
      maxSizeExceeded: 'Liitteen maksimikoko on {{size}}'
    },
    confirmDiscard: {
      title: 'Hylätäänkö muutokset',
      description: 'Sinulla on keskeneräisiä muutoksia. Hylätäänkö muutokset?',
      confirmText: 'Hylkää',
      cancelText: 'Palaa muokkaamaan'
    }
  },
  comment: {
    title: 'Kommentit',
    noProjectComments: 'Hanketta ei ole kommentoitu',
    showComments: 'Näytä kommentit',
    type: {
      INTERNAL: 'Sisäinen kommentti',
      INVOICING: 'Laskutuksen kommentti',
      RETURN: 'Valmisteluun palauttajan kommentti',
      REJECT: 'Hylkääjän kommentti',
      PRELIMINARY_SUPERVISION: 'Aloitusvalvonta',
      SUPERVISION: 'Valvonta',
      PROPOSE_APPROVAL: 'Ehdotettu hyväksyttäväksi',
      PROPOSE_REJECT: 'Ehdotettu hylättäväksi',
      EXTERNAL_SYSTEM: 'Ulkoinen järjestelmä'
    },
    addComment: 'LISÄÄ KOMMENTTI',
    newComment: 'Uusi kommentti',
    commentType: 'Kommentin tyyppi',
    myComment: 'Oma kommenttini on...',
    updated: 'Päivitetty',
    error: {
      fetch: 'Kommenttien hakeminen epäonnistui',
      save: 'Kommentin tallentaminen epäonnistui',
      remove: 'Kommentin poistaminen epäonnistui'
    },
    confirmDiscard: {
      title: 'Hylätäänkö muutokset',
      description: 'Sinulla on keskeneräisiä muutoksia. Hylätäänkö muutokset?',
      confirmText: 'Hylkää',
      cancelText: 'Palaa muokkaamaan'
    },
    field: {
      emptyText: 'Kommentti ei voi olla tyhjä'
    }
  },
  supervision: {
    title: 'Valvonnan tehtävät',
    addTask: 'LISÄÄ VALVONTAPYYNTÖ',
    task: {
      newTask: 'Uusi valvonnan tehtävä',
      description: 'Tehtävän kuvaus',
      result: 'Valvojan merkinnät',
      createdBy: 'Luonut',
      approve: 'HYVÄKSY',
      reject: 'HYLKÄÄ',
      newSupervisionDate: 'Uusi valvonta-aika',
      needsInvoicingChanges: 'Lisää ylimääräisiä maksuja ',
      toInvoicing:  'laskutuksen kautta.',
      type: {
        title: 'Toimenpide',
        PRELIMINARY_SUPERVISION: 'Aloitusvalvonta',
        OPERATIONAL_CONDITION: 'Toiminnallisen kunnon valvonta',
        SUPERVISION: 'Valvonta',
        WORK_TIME_SUPERVISION: 'Työnaikainen valvonta',
        FINAL_SUPERVISION: 'Loppuvalvonta',
        WARRANTY: 'Takuuvalvonta'
      },
      status: {
        OPEN: 'Avoin',
        APPROVED: 'Hyväksytty',
        REJECTED: 'Hylätty',
        CANCELLED: 'Peruttu'
      },
      owner: 'Valvoja',
      plannedFinishingTime: 'Ajankohta',
      actualFinishingTime: 'Valmistunut',
      action: {
        save: 'Valvontatehtävä tallennettu',
        remove: 'Valvontatehtävä poistettu',
        approve: 'Valvontatehtävä hyväksytty',
        reject: 'Valvontatehtävä hylätty',
        handlerChanged: 'Valvoja vaihdettu valituille tehtäville',
        hanlerRemoved: 'Valvoja poistettu valituilta tehtäviltä',
        movedToSelf: 'Valvontatehtävä siirretty omaksi'
      },
      error: {
        save: 'Valvontatehtävän tallentaminen epäonnistui',
        remove: 'Valvontatehtävän poistaminen epäonnistui',
        approve: 'Valvontatehtävän hyväksyminen epäonnistui',
        reject: 'Valvontatehtävän hylkääminen epäonnistui',
        handlerChange: 'Valvojan vaihtaminen epäonnistui',
        hanlerRemove: 'Valvojan poistaminen tehtäviltä epäonnistui',
        fetch: 'Valvontatehtävien hakeminen epäonnistui'
      },
      field: {
        plannedFinishingTimeInThePast: 'Ajankohta ei voi olla menneisyydessä',
        plannedFinishingTimeMissing: 'Ajankohta puuttuu',
        resultMissing: 'Valvojan merkinnät puuttuvat',
        newSupervisionDateMissing: 'Uusi valvonta-aika puuttuu',
        newSupervisionDateInThePast: 'Uusi valvonta-aika ei voi olla menneisyydessä'
      },
      resolution: {
        APPROVE: 'Hyväksy',
        REJECT: 'Hylkää'
      }
    }
  },
  supervisionWorkqueue: {
    title: 'Valvontatehtävät',
    filter: {
      taskTypes: 'Toimenpide',
      applicationId: 'Hakemustunnus',
      applicationStatus: 'Hakemuksen tila',
      applicationType: 'Hakemustyyppi',
      cityDistrict: 'Kaupunginosa',
      after: 'Jälkeen',
      before: 'Ennen'
    },
    content: {
      taskType: 'Toimenpide',
      applicationId: 'Hakemustunnus',
      applicationStatus: 'Hakemuksen tila',
      owner: 'Valvoja',
      creator: 'Valvontapyynnön jättäjä',
      plannedFinishingTime: 'Ajankohta',
      address: 'Osoite',
      project: 'Hanke'
    }
  },
  supervisionApprovalModal: {
    APPROVE: {
      title: 'Hyväksy valvontatehtävä'
    },
    REJECT: {
      title: 'Hylkää valvontatehtävä'
    }
  },
  invoicing: {
    period: {
      title: 'Laskutusjakso',
      periodSelection: 'Laskutuksen jaksotus',
      noPeriod: 'Ei jaksotusta',
      name: '{{period}}kk',
      error: {
        fetch: 'Laskutusjaksojen hakeminen epäonnistui',
        create: 'Laskutuksen jaksotus epäonnistui',
        update: 'Laskutuksen jaksotuksen muuttaminen epäonnistui',
        remove: 'Laskutuksen jaksotuksen poistaminen epäonnistui'
      }
    }
  },
  invoice: {
    invoiced: 'Laskutettu',
    notInvoiced: 'Ei laskutettu',
    open: 'Avoin',
    info: {
      address: 'Laskutusosoite',
      depositAndOthers: 'Vakuus ja muut tiedot',
      notBillable: 'Ei laskuteta',
      notBillableReason: 'Peruste',
      customerReference: 'Asiakkaan viite',
      invoicingDate: 'Laskutuspäivä',
      skipPriceCalculation: 'Ei automaattista hinnanlaskentaa'
    },
    group: {
      open: 'Avoimet laskut',
      pending: 'Odottaa laskutusta',
      invoiced: 'Laskutetut'
    },
    negligenceFeeType: {
      START_BEFORE_PERMIT: 'Aloitus ennen luvan saantia',
      AREA_UNCLEAN: 'Alue epäsiisti',
      ACTIONS_AGAINST_TRAFFIC_ARRANGEMENT: 'Liikennejärjestelypäätöksen vastainen toiminta',
      UNAUTHORIZED_PARKING: 'Auton luvaton pysäköinti alueella',
      LATE_NOTIFICATION_OF_COMPLETION: 'Valmistumisilmoitus tullut myöhässä',
      LATE_NOTIFICATION_OF_OPERATION_STATE: 'Toiminnallinen tila ilmoitettu myöhässä',
      LATE_REQUEST_OF_EXTRA_TIME: 'Lisäaikaa haettu myöhässä',
      OTHER: 'Muu syy'
    },
    partition: {
      NONE: 'Ei ositusta',
      MONTH: '1kk'
    },
    action: {
      save: 'Laskutustiedot tallennettu',
      cancel: 'Laskutustietojen muutokset peruttu'
    },
    error: {
      save: 'Laskutustietojen tallentaminen epäonnistui',
      invoiceRecipientSave: 'Laskutusasiakkaan tallentaminen epäonnistui',
      fetch: 'Laskujen hakeminen epäonnistui'
    },
    field: {
      workIdMissing: 'Työnumero puuttuu',
      invoiceReferenceMissing: 'Laskutusviite puuttuu',
      notBillableReasonMissing: 'Korvauksettomuuden peruste puuttuu',
      invoicingDateMissing: 'Laskutuspäivä puuttuu'
    },
    confirmSave: {
      title: 'Tallennetaanko muutokset',
      description: 'Sinulla on tallentamattomia muutoksia. Haluatko tallentaa ne?',
      confirmText: 'Tallenna',
      cancelText: 'Hylkää'
    },
    confirmDiscard: {
      title: 'Hylätäänkö muutokset',
      description: 'Sinulla on keskeneräisiä muutoksia. Hylätäänkö muutokset?',
      confirmText: 'Hylkää',
      cancelText: 'Palaa muokkaamaan'
    }
  },
  deposit: {
    modal: {
      title: 'Vakuus'
    },
    amount: 'Vakuus',
    reason: 'Vakuuden peruste',
    status: {
      UNPAID_DEPOSIT: 'Asetettu',
      PAID_DEPOSIT: 'Maksettu',
      RETURNED_DEPOSIT: 'Palautettu'
    },
    add: 'Aseta vakuus',
    edit: 'Muokkaa vakuutta',
    changeStatus: {
      UNPAID_DEPOSIT: 'Merkitse maksetuksi',
      PAID_DEPOSIT: 'Merkitse palautetuksi'
    },
    field: {
      amountMissing: 'Vakuus puuttuu'
    },
    action: {
      save: 'Vakuus tallennettu'
    },
    error: {
      fetch: 'Vakuuden hakeminen epäonnistui',
      save: 'Vakuuden tallentaminen epäonnistui',
      remove: 'Vakuuden poistaminen epäonnistui'
    }
  },
  chargeBasis: {
    title: 'Laskuperusteet',
    newEntry: 'Uusi laskuperuste',
    invoicable: 'Laskutettava',
    type: {
      title: 'Laskuperusteen tyyppi',
      CALCULATED: 'Laskettu',
      AREA_USAGE_FEE: 'Alueenkäyttömaksu',
      NEGLIGENCE_FEE: 'Laiminlyöntimaksu',
      ADDITIONAL_FEE: 'Ylimääräinen maksu',
      DISCOUNT: 'Alennus'
    },
    basis: {
      CALCULATED: 'Peruste',
      AREA_USAGE_FEE: 'Alueenkäyttömaksun peruste',
      NEGLIGENCE_FEE: 'Laiminlyöntimaksun peruste',
      ADDITIONAL_FEE: 'Ylimääräisen maksun peruste',
      DISCOUNT: 'Alennusperuste'
    },
    unit: {
      placeholder: 'Yksikkö',
      PIECE: 'kpl',
      METER: 'm',
      SQUARE_METER: 'm²',
      HOUR: 't',
      DAY: 'pv',
      WEEK: 'vk',
      MONTH: 'kk',
      YEAR: 'v',
      PERCENT: '%'
    },
    discountUnit: {
      PERCENT: '%',
      PIECE: '€'
    },
    quantity: 'Määrä',
    unitPrice: 'Yksikköhinta',
    discount: 'Alennus',
    netPrice: 'Kokonaishinta',
    explanation: 'Selite',
    discountFor: {
      placeholder: 'Alennuksen kohde',
      wholeInvoice: 'Koko lasku'
    },
    field: {
      rowTextMissing: 'Laskuperuste puuttuu',
      quantityMissing: 'Määrä puuttuu',
      unitMissing: 'Yksikkö puuttuu',
      unitPriceMissing: 'Yksikköhinta puuttuu',
      discountMissing: 'Alennus puuttuu',
      discountPercentageMissing: 'Alennusprosentti puuttuu',
      tooManyRows: 'Selitteessä voi olla korkeintaan 5 riviä',
      tooLongRows: 'Selitteen maksimirivipituus on 70 merkkiä',
      textMaxLength: 'Maksimipituus on 70 merkkiä'
    },
    action: {
      save: 'Laskuperusteet tallennettu'
    },
    error: {
      fetch: 'Laskuperusteiden hakeminen epäonnistui',
      save: 'Laskuperusteiden tallentaminen epäonnistui',
      setInvoicable: 'Laskuperusteen laskutettavuuden päivitys epäonnistui'
    }
  },
  workqueue: {
    title: 'Työjono',
    tab: {
      OWN: 'Omat',
      COMMON:  'Yhteiset'
    },
    commonTypes: 'Yhteiset hakemustyypit',
    commonStatuses: 'Yhteiset tilat',
    error: {
      searchFailed: 'Työjonon hakeminen epäonnistui'
    },
    notifications: {
      ownerChanged: 'Hakemuksien omistaja vaihdettu',
      ownerChangeFailed: 'Hakemuksien omistajan vaihtaminen epäonnistui',
      ownerRemoved: 'Omistaja poistettu hakemuksilta',
      ownerRemoveFailed: 'Omistajan poistaminen hakemuksilta epäonnistui'
    }
  },
  history: {
    title: 'Historia',
    changeTime: 'Muutettu',
    changer: 'Tekijä',
    showFieldChanges: 'Näytä kenttämuutokset',
    noHistory: 'Ei näytettävää historiaa',
    showHistory: 'Näytä historia',
    error: {
      metadata: 'Historian kenttien käännöksien lataus epäonnistui'
    },
    change: {
      title: 'Muutos',
      type: {
        CREATED: 'Luotu',
        STATUS_CHANGED: 'Siirretty tilaan',
        CONTENTS_CHANGED: 'Tietoja päivitetty',
        REPLACED: 'Korvattu',
        APPLICATION_ADDED: 'Hakemus lisätty',
        APPLICATION_REMOVED: 'Hakemus poistettu',
        CUSTOMER_CHANGED: 'Asiakas muutettu',
        CONTACT_CHANGED: 'Yhteyshenkilö muutettu',
      },
      typeWithSpecifier: {
        STATUS_CHANGED: {
          PENDING_CLIENT: 'Vireille asiakasjärjestelmässä',
          PRE_RESERVED: 'Alustava varaus luotu',
          PENDING: 'Hakemus saapunut',
          WAITING_INFORMATION: 'Odottaa täydennystä',
          INFORMATION_RECEIVED: 'Täydennys vastaanotettu',
          HANDLING: 'Hakemus siirretty käsittelyyn',
          RETURNED_TO_PREPARATION: 'Hakemus palautettu käsittelyyn',
          WAITING_CONTRACT_APPROVAL: 'Odottaa sopimuksen hyväksyntää',
          DECISIONMAKING: 'Hakemus siirretty odottamaan päätöstä',
          DECISION: 'Hakemus päätetty',
          REJECTED: 'Hakemuksen päätös hylätty',
          OPERATIONAL_CONDITION: 'Hakemus toiminnallisessa kunnossa',
          FINISHED: 'Hakemus valmistunut',
          CANCELLED: 'Hakemus peruttu',
          REPLACED: 'Hakemus korvattu',
          ARCHIVED: 'Hakemus arkistoitu'
        },
        CUSTOMER_CHANGED: {
          APPLICANT: 'Hakija muutettu',
          PROPERTY_DEVELOPER: 'Rakennuttaja muutettu',
          CONTRACTOR: 'Työn suorittaja muutettu',
          REPRESENTATIVE: 'Asiamies muutettu',
          INVOICE_RECIPIENT: 'Laskutusasiakas muutettu'
        },
        CONTACT_CHANGED: {
          APPLICANT: 'Hakijan yhteyshenkilö muutettu',
          PROPERTY_DEVELOPER: 'Rakennuttajan yhteyshenkilö muutettu',
          CONTRACTOR: 'Työn suorittajan yhteyshenkilö muutettu',
          REPRESENTATIVE: 'Asiamiehen yhteyshenkilö muutettu'
        },
        LOCATION_CHANGED: 'Aluetta {{locationKey}} päivitetty'
      },
      field: {
        CUSTOMER: 'Asiakas',
        CONTACT: 'Yhteyshenkilö'
      },
      operation: {
        ADD: 'lisätty',
        REMOVE: 'poistettu'
      }
    }
  },
  customers: {
    newContact: 'UUSI YHTEYSHENKILÖ',
    removeFromRegistry: 'POISTA REKISTERISTÄ',
    notifications: {
      contactRemoved: 'Yhteyshenkilö poistettu',
      contactRemoveFailed: 'Yhteyshenkilön poistaminen epäonnistui'
    }

  },
  ownerModal: {
    OWNER: {
      title: 'Siirrä omistajalle',
      selectedUser: 'Omistaja',
      noSelectedUser: 'Ei omistajaa'
    },
    SUPERVISOR: {
      title: 'Siirrä valvojalle',
      selectedUser: 'Valvoja',
      noSelectedUser: 'Ei valvojaa'
    }
  },
  config: {
    action: {
      save: 'Asetus tallennettu'
    },
    error: {
      fetch: 'Sovelluksen asetusten hakeminen epäonnistui'
    }
  },
  httpStatus: {
    BAD_REQUEST: 'Toiminto epäonnistui',
    UNAUTHORIZED: 'Et ole kirjautunut',
    FORBIDDEN: 'Oikeutesi eivät riitä',
    NOT_FOUND: 'Haku epäonnistui',
    INTERNAL_SERVER_ERROR: 'Palvelinvirhe',
    GATEWAY_TIMEOUT: 'Palvelimeen ei saatu yhteyttä',
    CONFLICT: 'Tallennus epäonnistui',
    UNKNOWN: 'Tuntematon virhe'
  },
  sort: {
    time: {
      asc: 'Vanhimmat ensin',
      desc: 'Uusimmat ensin'
    }
  },
  admin: {
    nav: {
      users: 'Käyttäjät',
      externalUsers: 'Rajapintakäyttäjät',
      defaultAttachments: 'Vakioliitteet',
      defaultImages: 'Tyyppikuvat',
      defaultRecipients: 'Päätöksen vastaanottajat',
      configuration: 'Asetukset',
    }
  },
  configuration: {
    key: {
      CUSTOMER_NOTIFICATION_RECEIVER_EMAIL: ' Sähköpostiosoite uusista asiakkaista ilmoittamiseen',
      INVOICE_NOTIFICATION_RECEIVER_EMAIL: 'Sähköpostiosoite laskuista ilmoittamiseen',
      EXCAVATION_ANNOUNCEMENT_DECISION_MAKER: 'Kaivuilmoituksen oletuspäättäjä',
      AREA_RENTAL_DECISION_MAKER: 'Aluevuokrauksen oletuspäättäjä',
      TEMPORARY_TRAFFIC_ARRANGEMENTS_DECISION_MAKER: 'Tilapäisen liikennejärjestelyn oletuspäättäjä',
      PLACEMENT_CONTRACT_DECISION_MAKER: 'Sijoitussopimuksen oletuspäättäjä',
      EVENT_DECISION_MAKER: 'Tapahtuman oletuspäättäjä',
      SHORT_TERM_RENTAL_DECISION_MAKER: 'Lyhytaikaisen maanvuokrauksen oletuspäättäjä',
      WINTER_TIME_START: 'Talviajan alkupäivä',
      WINTER_TIME_END: 'Talviajan loppupäivä'
    },
    field: {
      valueMissing: 'Arvo puuttuu'
    }
  }
};

const toKey = (path: string | Array<string>): Option<Array<string>> => {
  return Some(path).map(p => {
    const pathString = StringUtil.toPath(p, '.');
    return pathString.split('.');
  });
};

type Path = string | Array<string>;
export interface Params { [key: string]: string | number; }

/**
 * Replaces parameters in string with matching key's value
 * @param {string} text string containing translation with tokens
 * @param {Params} params object containing values to replace tokens in translation string
 * @returns {string} text with tokens replaced with matching parameters
 */
function replaceParams(text: string, params: Params): string {
  let replaced = text;
  Object.keys(params).forEach(key => {
    const replacement = `{{${key}}}`;
    const value = '' + params[key];
    replaced = replaced.replace(replacement, value);
  });
  return replaced;
}

/**
 * Finds translation for given path
 * @param path path to translation eg. application.status.HANDLED
 * @param params additional parameters as object
 * @param from object which contains translations (default to translations described in this file)
 *
 * @returns translation if found with path, otherwise returns path
 */
export const findTranslation = (path: Path, params?: Params, from: any = translations): string => {
  const translated = toKey(path)
    .map(pathParts => pathParts.reduce((acc: any, cur: any) => Some(acc[cur]).orElse(pathParts.join('.')) , from))
    .orElse('');

  return params ? replaceParams(translated, params) : translated;
};


export const findTranslationWithDefault = (path: Path, key: string, value: string, defaultTranslation: string = ''): string => {
  if (value) {
    return findTranslation(path, {[key]: value});
  } else {
    return defaultTranslation;
  }
};

/**
 * Translates array of key values with given prefix to array of translated values
 *
 * @param pathPrefix prefix for all keys
 * @param pathValues values to be translated
 * @returns {string[]} array of translated values from prefix + value for all values
 */
export const translateArray = (pathPrefix: string, pathValues: Array<string>): Array<string> => {
  return pathValues.map(val => findTranslation([pathPrefix, val]));
};
