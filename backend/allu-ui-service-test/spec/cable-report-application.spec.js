const TestUtil = require('../util/test-util');
const rp = require('request-promise');

TestUtil.assertEnv();

describe('Cable report application', () => {

  let applicationStartTime = TestUtil.getStartDateString(0);
  let applicationEndTime = TestUtil.getEndDateString(30);

  let applicantCustomersWithContactsCreated;
  let propertyDeveloperCustomersWithContactsCreated;

  function createCustomers() {
    const applicantContactNew = {
      'email': null,
      'name': 'Kalle',
      'applicantId': null,
      'id': null,
      'postalCode': null,
      'streetAddress': null,
      'city': null,
      'phone': null,
      'active': true
    };

    const propertyDeveloperContactNew = {
      'email': null,
      'name': 'Olli',
      'applicantId': null,
      'id': null,
      'postalCode': null,
      'streetAddress': null,
      'city': null,
      'phone': null,
      'active': true
    };

    const applicantCustomerWithContactsNew = {
      'roleType': 'APPLICANT',
      'customer': {
        'postalAddress': {
          'streetAddress': '',
          'city': '',
          'postalCode': ''
        },
        'id': null,
        'name': 'Kallen Kaivuri',
        'email': '',
        'phone': '',
        'registryKey': '123',
        'representative': null,
        'type': 'COMPANY',
        'active': true,
        'country': 'FI'
      },
      'contacts': [applicantContactNew]
    };

    const propertyDeveloperCustomerWithContactsNew = {
      'roleType': 'PROPERTY_DEVELOPER',
      'customer': {
        'registryKey': '234',
        'phone': '',
        'representative': null,
        'type': 'COMPANY',
        'id': null,
        'postalAddress': {
          'city': '',
          'streetAddress': '',
          'postalCode': ''
        },
        'email': '',
        'name': 'Ollin Ojitus',
        'active': true,
        'country': 'FI'
      },
      'contacts': [propertyDeveloperContactNew]
    };

    let applicantOptions = TestUtil.getPostOptions('/api/customers/withcontacts', applicantCustomerWithContactsNew);
    let propertyDeveloperOptions = TestUtil.getPostOptions('/api/customers/withcontacts', propertyDeveloperCustomerWithContactsNew);
    return TestUtil.login('kasittelija')
      .then(token => {
        TestUtil.addAuthorization(applicantOptions, token);
        TestUtil.addAuthorization(propertyDeveloperOptions, token)
      })
      .then(() => rp(applicantOptions))
      .then(cwc => applicantCustomersWithContactsCreated = cwc)
      .then(() => rp(propertyDeveloperOptions))
      .then(cwc => propertyDeveloperCustomersWithContactsCreated = cwc);
  }

  beforeAll(done => {
    TestUtil.beforeAll(createCustomers)
    .then(done)
    .catch(err => done.fail(err));
  });

  it('Create', done => {
    const cableReport = {
      'customersWithContacts':[
        applicantCustomersWithContactsCreated,
        propertyDeveloperCustomersWithContactsCreated
      ],
      'type': 'CABLE_REPORT',
      'kindsWithSpecifiers': {
        'STREET_AND_GREEN': ['STREET_OR_PARK', 'BRIDGE'],
        'DATA_TRANSFER': ['DATA_CABLE']},
      'notBillable': 'false',
      'applicationTags': [
        {
          'addedBy': 1,
          'type': 'SURVEY_REQUIRED',
          'creationTime': '2016-07-15T05:08:10.514Z'
        }
      ],
      'extension': {
        'mapExtractCount': 0,
        'applicationType': 'CABLE_REPORT',
        'infoEntries': [
          {
            'type': 'WATER_AND_SEWAGE',
            'additionalInfo': ' Tilattava näyttö.'
          }
        ],
        'maintenanceWork': true
      },
      'locations': [
        {
          'startTime': applicationStartTime,
          'endTime': applicationEndTime,
          'additionalInfo': 'Näiltä nurkin pitäisi kaivella',
          'underpass': false,
          'postalAddress': {},
          'fixedLocationIds': [],
          'geometry': {
            'type': 'GeometryCollection',
            'geometries': [
              {
                'type': 'Polygon',
                'coordinates': [
                  [
                    [
                      25496023.25,
                      6672779.99999896
                    ],
                    [
                      25496114.25,
                      6672754.49999896
                    ],
                    [
                      25496108.75,
                      6672735.99999896
                    ],
                    [
                      25496014.75,
                      6672759.99999896
                    ],
                    [
                      25496023.25,
                      6672779.99999896
                    ]
                  ]
                ]
              }
            ],
            'crs': {
              'type': 'name',
              'properties': {
                'name': 'EPSG:3879'
              }
            }
          }
        }
      ],
      'name': 'Johtoselvitys',
      'decisionPublicityType': 'PUBLIC',
      'invoicingDate': '2018-12-22T22:00:00Z'
    }


    let options = TestUtil.getPostOptions('/api/applications', cableReport);
    TestUtil.login('kasittelija')
      .then(token => TestUtil.addAuthorization(options, token))
      .then(() => rp(options))
      .then(done, done.fail);
  });

});
