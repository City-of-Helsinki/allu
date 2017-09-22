const TestUtil = require('../util/test-util');
const rp = require('request-promise');

TestUtil.assertEnv();

describe('Short term rental application', () => {

  let applicantCustomersWithContactsCreated;

  function createCustomers() {
    const applicantNew = {
      'id': null,
      'applicantId': null,
      'name': 'Poika Porkkalan',
      'streetAddress': 'Porkkalantie 1',
      'postalCode': '00110',
      'city': 'Helsinki',
      'email': 'porkka@porkkala.fi',
      'phone': '03-12121212',
      'active': true
    };

    const applicantCustomerWithContactsNew = {
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
      'contacts': [applicantNew]
    };

    let applicantOptions = TestUtil.getPostOptions('/api/customers/withcontacts', applicantCustomerWithContactsNew);
    return TestUtil.login('kasittelija')
      .then(token => {
        TestUtil.addAuthorization(applicantOptions, token);
      })
      .then(() => rp(applicantOptions))
      .then(cwc => applicantCustomersWithContactsCreated = cwc);
  }

  beforeAll(done => {
    TestUtil.tryRetryPromise(1, TestUtil.tryToCreateUsers).then(createCustomers, done.fail).then(done);
  });

  it('Create', done => {

    const shortTermRentalApplication = {
        'id': null,
        'project': {
          'id': null,
          'name': 'Mock Project',
          'type': null,
          'information': null
        },
        'handler': null,
        'status': null,
        'type': 'SHORT_TERM_RENTAL',
        'kindsWithSpecifiers': {'BRIDGE_BANNER' : []},
        'notBillable': 'false',
        'name': 'Siltamainos Porkkalassa',
        'decisionPublicityType': 'PUBLIC',
        'decisionDistributionType': 'EMAIL',
        'creationTime': '2016-07-15T10:36:09.721Z',
        'customersWithContacts':[applicantCustomersWithContactsCreated],
        'locations': [
          {
            'id': null,
            'startTime': '2016-12-23T22:00:00Z',
            'endTime': '2016-12-25T22:00:00Z',
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
                  'type': 'Point',
                  'crs': null,
                  'bbox': [
                    2.5495613592870224E7,
                    6672428.889279648,
                    2.5495613592870224E7,
                    6672428.889279648
                  ],
                  'coordinates': [
                    2.5495613592870224E7,
                    6672428.889279648
                  ]
                }
              ]
            },
            'area': 0.0,
            'areaOverride': null,
            'underpass': false,
            'postalAddress': {
              'streetAddress': null,
              'postalCode': null,
              'city': null
            },
            'fixedLocationIds': [
              62
            ]
          }
        ],
        'extension': {
          'applicationType': 'SHORT_TERM_RENTAL',
          'description': 'Porkkalan siltamainos',
          'commercial': true
        }
      };


    let options = TestUtil.getPostOptions('/api/applications', shortTermRentalApplication);
    TestUtil.login('kasittelija')
      .then(token => TestUtil.addAuthorization(options, token))
      .then(() => rp(options))
      .then(done, done.fail);
  });

});
