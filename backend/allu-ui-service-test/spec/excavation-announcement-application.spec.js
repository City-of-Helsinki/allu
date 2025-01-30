const TestUtil = require('../util/test-util');
const rp = require('request-promise');

TestUtil.assertEnv();

describe('Excavation announcement application', () => {

  let applicationStartTime = TestUtil.getStartDateString(-5);
  let applicationEndTime = TestUtil.getEndDateString(0);
  let guaranteeEndTime = TestUtil.getEndDateString(365*2);
  let customerStartTime = applicationStartTime;
  let customerEndTime = applicationEndTime;
  let customerWinterTimeOperation = null;
  let customerWorkFinished = TestUtil.getEndDateString(-1);

  let applicantCustomersWithContactsCreated;
  let contractorCustomersWithContactsCreated;
  let applicationCreated;

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
        'active': true,
        'country': 'FI'
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
        'active': true,
        'country': 'FI'
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
          'workPurpose': 'Kaivaa mahdollisimman syvä kuoppa',
          'traffixArrangements': 'Mannerheimintie kokonaan poikki. Koittakaa selvitä!',
          'trafficArrangementImpedimentType': 'SIGNIFICANT_IMPEDIMENT',
          'pksCard': true,
          "compactionAndBearingCapacityMeasurement": true,
          "qualityAssuranceTest": true
        },
        'invoicingDate': '2018-12-22T22:00:00Z',
	'startTime': applicationStartTime,
	'endTime': applicationEndTime
      }
    ;


    let options = TestUtil.getPostOptions('/api/applications', excavationAnnouncement);
    TestUtil.login('kasittelija')
      .then(token => TestUtil.addAuthorization(options, token))
      .then(() => rp(options))
      .then(app => applicationCreated = app)
      .then(done, done.fail);
  });

  it('Create supervisionTasks', done => {
      const preliminarySupervision = {
        'id': null,
        'applicationId': applicationCreated.id,
        'type': 'PRELIMINARY_SUPERVISION',
        'status': 'OPEN',
        'description': 'Aloitusvalvonnan kuvaus',
        'plannedFinishingTime': '2019-01-01T00:00:00.000Z'
      };
      const operationalSupervision = {
        'id': null,
        'applicationId': applicationCreated.id,
        'type': 'OPERATIONAL_CONDITION',
        'status': 'OPEN',
        'description': 'Toiminnallisen kunnon valvonnan kuvaus',
        'plannedFinishingTime': '2019-03-03T00:00:00.000Z'
      };
      const finalSupervision = {
        'id': null,
        'applicationId': applicationCreated.id,
        'type': 'FINAL_SUPERVISION',
        'status': 'OPEN',
        'description': 'Loppuvalvonnan kuvaus',
        'plannedFinishingTime': '2019-05-14T00:00:00.000Z'
      };

      let preliminaryOptions = TestUtil.getPostOptions('/api/supervisiontask', preliminarySupervision);
      let operationalOptions = TestUtil.getPostOptions('/api/supervisiontask', operationalSupervision);
      let finalOptions = TestUtil.getPostOptions('/api/supervisiontask', finalSupervision);

      TestUtil.login('kasittelija')
        .then(token => {
          TestUtil.addAuthorization(preliminaryOptions, token);
          TestUtil.addAuthorization(operationalOptions, token);
          TestUtil.addAuthorization(finalOptions, token);
        })
        .then(() => rp(preliminaryOptions))
        .then(() => rp(operationalOptions))
        .then(() => rp(finalOptions))
        .then(done, done.fail);
  });
});
