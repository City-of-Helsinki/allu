const TestUtil = require('../util/test-util');
const rp = require('request-promise');

TestUtil.assertEnv();

describe('Temporary traffic announcement application', () => {

  let applicationStartTime = TestUtil.getISODateString(13);
  let applicationEndTime = TestUtil.getISODateString(33);

  let lasseCustomersWithContactsCreated;
  let liikenneCustomersWithContactsCreated;

  function createCustomers() {
    const lasseContactNew = {
      'id': null,
      'applicantId': null,
      'name': 'Lasse Kumminkaima',
      'streetAddress': '321',
      'postalCode': '30100',
      'city': 'Tampere',
      'email': 'lasse.kumminkaima@liikennejarjestely.com',
      'phone': '031234567',
      'active': true
    };

    const vainoContactNew = {
      'id': null,
      'applicantId': null,
      'name': 'Väinö Vastuuton',
      'streetAddress': '234124',
      'postalCode': '03042',
      'city': 'PL 100',
      'email': 'vaino.vastuuton@vastuuttomuus.net',
      'phone': '03123352',
      'active': true
    };

    const lasseCustomerWithContactsNew = {
      'roleType': 'APPLICANT',
      'customer': {
        'id': null,
        'type': 'PERSON',
        'representative': null,
        'name': 'Lasse Liikennejärjestelijä',
        'registryKey': '123',
        'postalAddress': {
          'streetAddress': 'Kotiosoite 1',
          'postalCode': '33440',
          'city': 'Tampere'
        },
        'email': 'lasse.liikennejarjestelija@liikennejarjestely.com',
        'phone': '040098765',
        'active': true
      },
      'contacts': [lasseContactNew]
    };

    const liikenneCustomerWithContactsNew = {
      'roleType': 'CONTRACTOR',
      'customer': {
        'id': null,
        'type': 'COMPANY',
        'representative': null,
        'name': 'Liikennejärjestelyt.com',
        'registryKey': '43124123-1',
        'postalAddress': {
          'streetAddress': 'Tampereentie 1',
          'postalCode': '32312',
          'city': 'Hämeenlinna'
        },
        'email': 'suorittaja@liikennejarjestelyt.com',
        'phone': '0421431234',
        'active': true
      },
      'contacts': [vainoContactNew]
    };

    let lasseOptions = TestUtil.getPostOptions('/api/customers/withcontacts', lasseCustomerWithContactsNew);
    let liikenneOptions = TestUtil.getPostOptions('/api/customers/withcontacts', liikenneCustomerWithContactsNew);
    return TestUtil.login('kasittelija')
      .then(token => {
        TestUtil.addAuthorization(lasseOptions, token);
        TestUtil.addAuthorization(liikenneOptions, token)
      })
      .then(() => rp(lasseOptions))
      .then(cwc => lasseCustomersWithContactsCreated = cwc)
      .then(() => rp(liikenneOptions))
      .then(cwc => liikenneCustomersWithContactsCreated = cwc);
  }

  beforeAll(done => {
    TestUtil.beforeAll(createCustomers)
      .then(done)
      .catch(err => done.fail(err));
  });

  it('Create', done => {

    const temporaryTrafficAnnouncement = {
        'type': 'TEMPORARY_TRAFFIC_ARRANGEMENTS',
        'kindsWithSpecifiers': {'OTHER' : []},
        'notBillable': 'false',
        'name': 'Liikennejärjestely',
        'decisionPublicityType': 'PUBLIC',
        'customersWithContacts':[lasseCustomersWithContactsCreated, liikenneCustomersWithContactsCreated],
        'locations': [
          {
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
              'geometries': [
                {
                  'type': 'Polygon',
                  'coordinates': [
                    [
                      [
                        25495324.625,
                        6674697.49999896
                      ],
                      [
                        25495324.625,
                        6674722.999998959
                      ],
                      [
                        25495378.625,
                        6674754.49999896
                      ],
                      [
                        25495394.875,
                        6674750.749998957
                      ],
                      [
                        25495406.375,
                        6674731.749998958
                      ],
                      [
                        25495414.125,
                        6674715.749998958
                      ],
                      [
                        25495352.125,
                        6674680.249998959
                      ],
                      [
                        25495334.625,
                        6674682.999998959
                      ],
                      [
                        25495324.625,
                        6674697.49999896
                      ]
                    ]
                  ]
                }
              ]
            },
            'underpass': false,
            'postalAddress': {
            },
            'fixedLocationIds': [
            ]
          }
        ],
        'extension': {
          'applicationType': 'TEMPORARY_TRAFFIC_ARRANGEMENTS',
          'pksCard': true,
          'workFinished': '2017-03-31T21:00:00.000Z',
          'trafficArrangements': 'Nordenskiöldin aukio muutetaan suureksi liikenneympyräksi',
          'trafficArrangementImpedimentType': 'SIGNIFICANT_IMPEDIMENT'
        },
        'applicationTags': [
        ],
        'invoicingDate': '2018-12-22T22:00:00Z'
      };


    let options = TestUtil.getPostOptions('/api/applications', temporaryTrafficAnnouncement);
    TestUtil.login('kasittelija')
      .then(token => TestUtil.addAuthorization(options, token))
      .then(() => rp(options))
      .then(done, done.fail);
  });

});
