const TestUtil = require('../util/test-util');
const rp = require('request-promise');

TestUtil.assertEnv();

describe('Excavation announcement application', () => {

  let applicationStartTime = TestUtil.getISODateString(-5);
  let applicationEndTime = TestUtil.getISODateString(0);
  let guaranteeEndTime = TestUtil.getISODateString(365*2);
  let customerStartTime = applicationStartTime;
  let customerEndTime = applicationEndTime;
  let customerWinterTimeOperation = null;
  let customerWorkFinished = TestUtil.getISODateString(-1);

  let applicantCustomersWithContactsCreated;
  let contractorCustomersWithContactsCreated;

  function createCustomers() {
    const applicantContactNew = {
      'id': null,
      'applicantId': null,
      'name': 'Pena Puhallus',
      'streetAddress': 'Porkkalantie 1',
      'postalCode': '00110',
      'city': 'Helsinki',
      'email': 'pena@puhallus.fi',
      'phone': '0441212121',
      'active': true
    };

    const contractorContactNew = {
      'id': null,
      'applicantId': null,
      'name': 'Make Morottaja',
      'streetAddress': 'Kaivokuja 6',
      'postalCode': '00111',
      'city': 'Helsinki',
      'email': 'make@kaivu.fi',
      'phone': '04431313131',
      'active': true
    };

    const applicantCustomerWithContactsNew = {
      'roleType': 'APPLICANT',
      'customer': {
        'type': 'COMPANY',
        'person': null,
        'id': null,
        'name': 'Penan putki ja puhallus',
        'registryKey': '1212121-6',
        'postalAddress': {
          'streetAddress': 'Mannerheimintie 1',
          'postalCode': '00100',
          'city': 'Helsinki'
        },
        'email': 'pena@puhallus.fi',
        'phone': '0441212121',
        'active': true
      },
      'contacts': [applicantContactNew]
    };

    const contractorCustomerWithContactsNew = {
      'roleType': 'CONTRACTOR',
      'customer': {
        'type': 'COMPANY',
        'person': null,
        'id': null,
        'name': 'Maken kaivu',
        'registryKey': '1313131-6',
        'postalAddress': {
          'streetAddress': 'Kaivokuja 5',
          'postalCode': '00100',
          'city': 'Helsinki'
        },
        'active': true
      },
      'contacts': [contractorContactNew]
    };

    let applicantOptions = TestUtil.getPostOptions('/api/customers/withcontacts', applicantCustomerWithContactsNew);
    let contractorOptions = TestUtil.getPostOptions('/api/customers/withcontacts', contractorCustomerWithContactsNew);
    return TestUtil.login('kasittelija')
      .then(token => {
        TestUtil.addAuthorization(applicantOptions, token);
        TestUtil.addAuthorization(contractorOptions, token)
      })
      .then(() => rp(applicantOptions))
      .then(cwc => applicantCustomersWithContactsCreated = cwc)
      .then(() => rp(contractorOptions))
      .then(cwc => contractorCustomersWithContactsCreated = cwc);
  }

  beforeAll(done => {
    TestUtil.beforeAll(createCustomers)
    .then(done)
    .catch(err => done.fail(err));
  });

  it('Create', done => {

    const excavationAnnouncement = {
        'id': null,
        'project': null,
        'handler': null,
        'status': null,
        'type': 'EXCAVATION_ANNOUNCEMENT',
        'kindsWithSpecifiers': {'HEATING_COOLING': []},
        'notBillable': 'false',
        'name': 'Manskun lämpö ja puhallus',
        'decisionPublicityType': 'PUBLIC',
        'creationTime': '2016-07-15T10:36:09.721Z',
        'customersWithContacts':[applicantCustomersWithContactsCreated, contractorCustomersWithContactsCreated],
        'locations': [
          {
            'id': null,
            'startTime': applicationStartTime,
            'endTime': applicationEndTime,
            'geometry': {
              'type': 'GeometryCollection',
              'crs': {
                'properties': {
                  'name': 'EPSG:3879'
                },
                'type': 'name'
              },
              'bbox': null,
              'geometries': [
                {
                  'type': 'Polygon',
                  'crs': null,
                  'bbox': [
                    2.549589275E7,
                    6674232.999998959,
                    2.549606025E7,
                    6674407.499998961
                  ],
                  'coordinates': [
                    [
                      [
                        2.549589975E7,
                        6674390.49999896
                      ],
                      [
                        2.549592575E7,
                        6674407.499998961
                      ],
                      [
                        2.549596925E7,
                        6674348.99999896
                      ],
                      [
                        2.549603675E7,
                        6674393.499998959
                      ],
                      [
                        2.549606025E7,
                        6674357.999998961
                      ],
                      [
                        2.549599975E7,
                        6674320.99999896
                      ],
                      [
                        2.549602225E7,
                        6674239.999998959
                      ],
                      [
                        2.549597825E7,
                        6674232.999998959
                      ],
                      [
                        2.549596175E7,
                        6674303.499998958
                      ],
                      [
                        2.549590825E7,
                        6674251.999998959
                      ],
                      [
                        2.549589275E7,
                        6674283.999998958
                      ],
                      [
                        2.549594775E7,
                        6674327.999998961
                      ],
                      [
                        2.549589975E7,
                        6674390.49999896
                      ]
                    ]
                  ]
                }
              ]
            },
            'area': 12229.125000074739,
            'areaOverride': null,
            'underpass': false,
            'postalAddress': {
              'streetAddress': null,
              'postalCode': null,
              'city': null
            },
            'fixedLocationIds': [
            ]
          }
        ],
        'extension': {
          'applicationType': 'EXCAVATION_ANNOUNCEMENT',
          'guaranteeEndTime': guaranteeEndTime,
          'customerStartTime': customerStartTime,
          'customerEndTime': customerEndTime,
          'customerWinterTimeOperation': customerWinterTimeOperation,
          'customerWorkFinished': customerWorkFinished,
          'additionalInfo': 'Automaagisesti tehty kaivuilmoitus',
          'traffixArrangements': 'Mannerheimintie kokonaan poikki. Koittakaa selvitä!',
          'trafficArrangementImpedimentType': 'SIGNIFICANT_IMPEDIMENT',
          'pksCard': true
        }
      }
    ;


    let options = TestUtil.getPostOptions('/api/applications', excavationAnnouncement);
    TestUtil.login('kasittelija')
      .then(token => TestUtil.addAuthorization(options, token))
      .then(() => rp(options))
      .then(done, done.fail);
  });

});
