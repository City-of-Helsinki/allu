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
      PENDING: 'Vireillä',
      HANDLING: 'Käsittelyssä',
      RETURNED_TO_PREPARATION: 'Palautettu käsittelyyn',
      DECISIONMAKING: 'Odottaa päätöstä',
      DECISION: 'Päätetty',
      REJECTED: 'Hylätty päätös',
      FINISHED: 'Valmis',
      CANCELLED: 'Peruttu'
    },
    type: {
      OUTDOOREVENT: 'Ulkoilmatapahtuma',
      PROMOTION: 'Promootio'
    },
    field: {
      nameMissing: 'Tapahtuman nimi puuttuu',
      nameShort: 'Tapahtuman nimi on liian lyhyt'
    },
    outdoorEvent: {
      field: {
        natureMissing: 'Tapahtuman luonne puuttuu',
        descriptionMissing: 'Tapahtuman kuvaus puuttuu',
        typeMissing: 'Tapahtuman tyyppi puuttuu',
        eventStartTimeMissing: 'Tapahtuman alkuaika puuttuu',
        eventEndTimeMissing: 'Tapahtuman loppuaika puuttuu',
        startBeforeEnd: 'Tapahtuman loppupäivä ei voi olla ennen alkupäivää'
      }
    }
  },
  applicant: {
    field: {
      typeMissing: 'Hakijan tyyppi puuttuu',
      nameMissing: 'Hakijan nimi puuttuu',
      nameShort: 'Hakijan nimi on liian lyhyt',
      identifierMissing: 'Hakijan tunniste puuttuu',
      identifierShort: 'Hakijan tunniste on liian lyhyt',
      emailInvalid: 'Virheellinen sähköpostiosoite',
      phoneShort: 'Puhelinnumero on liian lyhyt'
    }
  },
  contact: {
    field: {
      nameMissing: 'Yhteyshenkilön nimi puuttuu',
      nameShort: 'Yhteyshenkilön nimi on liian lyhyt',
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
  }
};
