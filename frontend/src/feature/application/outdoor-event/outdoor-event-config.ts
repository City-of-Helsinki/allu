const events = [
  {name: 'Ulkoilmatapahtuma', value: 'OutdoorEvent'},
  {name: 'Muu', value: 'Other'}
];

const applicantType = [
  {name: 'Yritys', value: 'COMPANY', text: 'Yrityksen nimi'},
  {name: 'Yhdistys', value: 'ASSOCIATION', text: 'Yhdistyksen nimi'},
  {name: 'Yksityishenkilö', value: 'PERSON', text: 'Yksityishenkilön nimi'}
];

const applicantText = {
  'DEFAULT': {
    name: 'Hakijan nimi',
    id: 'Y-tunnus'},
  'COMPANY': {
    name: 'Yrityksen nimi',
    id: 'Y-tunnus'},
  'ASSOCIATION': {
    name: 'Yhdistyksen nimi',
    id: 'Y-tunnus'},
  'PERSON': {
    name: 'Henkilön nimi',
    id: 'Henkilötunnus'}
};

const countries = [
  {name: 'Suomi', value: 'Finland'},
  {name: 'Ruotsi', value: 'Sweden'},
  {name: 'Venäjä', value: 'Russia'},
  {name: 'Viro', value: 'Estonia'}
];

const billingTypes = [
  {name: 'Käteinen', value: 'Cash'},
  {name: 'Lasku', value: 'Invoice'}
];

const eventNatures = [
  {name: 'Avoin', value: 'Open'},
  {name: 'Maksullinen', value: 'Paid'},
  {name: 'Suljettu', value: 'Closed'}
];

const noPriceReasons = [
  {name: 'Hyväntekeväisyys- tai kansalaisjärjestö tai oppilaitoksen tapahtuma', value: 'Charity'},
  {name: 'Taide- tai kulttuuritapahtuma', value: 'ArtOrCulture'},
  {name: 'Avoin ja maksuton urheilutapahtuma', value: 'NoFeeSporting'},
  {name: 'Asukas- tai kaupunginosayhdistyksen tapahtuma', value: 'ResidentOrCity'},
  {name: 'Aatteellinen, hengellinen tai yhteiskunnallinen tapahtuma', value: 'Spiritual'},
  {name: 'Kaupunki isäntänä tai järjestäjäkumppanina', value: 'City'},
  {name: 'Tilataideteos', value: 'Art'},
  {name: 'Nuorisojärjestön tapahtuma', value: 'Youth'},
  {name: 'Yksityishenkilön järjestämä merkkipäiväjuhla tai vastaava', value: 'PrivateFunction'},
  {name: 'Puolustus- tai poliisivoimien tapahtuma', value: 'DefenceOrPolice'}
];

export const DEFAULT_APPLICANT = 'DEFAULT';

export const outdoorEventConfig = {
  events: events,
  applicantType: applicantType,
  applicantText: applicantText,
  countries: countries,
  billingTypes: billingTypes,
  eventNatures: eventNatures,
  noPriceReasons: noPriceReasons
};

export function applicantNameSelection(type: string): string {
  return applicationTextBy(type).name;
}

export function applicantIdSelection(type: string): string {
  return applicationTextBy(type).id;
}

function applicationTextBy(type: string) {
  return applicantType ? applicantText[type] : applicantText[DEFAULT_APPLICANT];
}
