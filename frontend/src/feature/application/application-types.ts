import {ApplicationType} from '../../model/application/type/application-type';
export const applicationTypes = [
  {
    value: 'Street',
    name: 'Katutyö',
    subtypes: [
      {name: 'Kaivuilmoitus', value: 'promotion-event', type: ApplicationType.PROMOTIONEVENT},
      {name: 'Aluevuokraus', value: 'promotion-event', type: ApplicationType.PROMOTIONEVENT},
      {name: 'Tilapäiset liikennejärjestelyt', value: 'promotion-event', type: ApplicationType.PROMOTIONEVENT}
    ]
  },
  {
    value: 'Event',
    name: 'Tapahtuma',
    subtypes: [
      {name: 'Promootio', value: 'promotion-event', type: ApplicationType.PROMOTIONEVENT},
      {name: 'Ulkoilmatapahtuma', value: 'outdoor-event', type: ApplicationType.OUTDOOREVENT},
      {name: 'Vaalit', value: 'promotion-event', type: ApplicationType.PROMOTIONEVENT}
    ]
  }
];
