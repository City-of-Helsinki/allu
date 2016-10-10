import {ApplicationType} from '../../model/application/type/application-type';
export const applicationTypes = [
  {
    value: 'Street',
    name: 'Katutyö',
    subtypes: [
      {name: 'Kaivuilmoitus', value: 'promotion-event', type: ApplicationType.PROMOTION},
      {name: 'Aluevuokraus', value: 'promotion-event', type: ApplicationType.PROMOTION},
      {name: 'Tilapäiset liikennejärjestelyt', value: 'promotion-event', type: ApplicationType.PROMOTION}
    ]
  },
  {
    value: 'Event',
    name: 'Tapahtuma',
    subtypes: [
      {name: 'Promootio', value: 'promotion-event', type: ApplicationType.PROMOTION},
      {name: 'Ulkoilmatapahtuma', value: 'outdoor-event', type: ApplicationType.OUTDOOREVENT},
      {name: 'Vaalit', value: 'promotion-event', type: ApplicationType.PROMOTION}
    ]
  }
];
