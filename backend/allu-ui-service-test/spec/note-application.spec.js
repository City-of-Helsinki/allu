const TestUtil = require('../util/test-util');
const rp = require('request-promise');

TestUtil.assertEnv();

describe('Note application', () => {

  let applicationStartTime = TestUtil.getStartDateString(-1);
  let applicationEndTime = TestUtil.getEndDateString(30);

  let nuupuryCustomersWithContactsCreated;

  function createCustomers() {
    const pekkaContactNew = {
      'postalCode': null,
      'applicantId': null,
      'city': null,
      'phone': null,
      'name': 'Pekka Pihi',
      'id': null,
      'streetAddress': '121282-123X',
      'email': null,
      'active': true
    };

    const nuupuryCustomerWithContactsNew = {
      'roleType': 'APPLICANT',
      'customer': {
        'phone': '',
        'registryKey': '123456',
        'type': 'ASSOCIATION',
        'email': 'nuupury@laihia.org',
        'representative': null,
        'postalAddress': {
          'streetAddress': 'Pihitie 3',
          'postalCode': '66400',
          'city': 'LAIHIA'
        },
        'id': null,
        'name': 'Nuukailun Puolesta Ry',
        'active': true,
        'country': 'FI'
      },
      'contacts': [pekkaContactNew]
    };

    let nuupuryOptions = TestUtil.getPostOptions('/api/customers/withcontacts', nuupuryCustomerWithContactsNew);
    return TestUtil.login('kasittelija')
    .then(token => TestUtil.addAuthorization(nuupuryOptions, token))
    .then(() => rp(nuupuryOptions))
    .then(cwc => nuupuryCustomersWithContactsCreated = cwc);
  }

  beforeAll(done => {
    TestUtil.beforeAll(createCustomers)
    .then(done)
    .catch(err => done.fail(err));
  });

  it('Create', done => {

    const note = {
      'type': 'NOTE',
      'kindsWithSpecifiers': {'CHRISTMAS_TREE_SALES_AREA': []},
      'notBillable': 'true',
      'notBillableReason': 'Ei nyt jouluna kehtaa laskuttaa',
      'locations': [
        {
          'startTime': applicationStartTime,
          'endTime': applicationEndTime,
          'fixedLocationIds': [],
          'geometry': {
            'crs': {
              'type': 'name',
              'properties': {
                'name': 'EPSG:3879'
              }
            },
            'type': 'GeometryCollection',
            'geometries': [
              {
                'type': 'Polygon',
                'coordinates': [
                  [
                    [
                      25496480.875,
                      6673161.74999896
                    ],
                    [
                      25496526.875,
                      6673166.24999896
                    ],
                    [
                      25496533.125,
                      6673122.99999896
                    ],
                    [
                      25496517.375,
                      6673120.24999896
                    ],
                    [
                      25496505.375,
                      6673144.74999896
                    ],
                    [
                      25496480.875,
                      6673161.74999896
                    ]
                  ]
                ]
              }
            ]
          },
          'underpass': false,
          'postalAddress': {}
        }
      ],
      'name': 'Ylijäämäkuusien alennusmyynti',
      'decisionPublicityType': 'PUBLIC',
      'extension': {
        'description': 'Joulusta ylijääneiden kuusien alennusmyynti hintatietoisille.',
        'reoccurring': true,
        'applicationType': 'NOTE'
      },
      'customersWithContacts': [nuupuryCustomersWithContactsCreated]
    };


    let options = TestUtil.getPostOptions('/api/applications', note);
    TestUtil.login('kasittelija')
    .then(token => TestUtil.addAuthorization(options, token))
    .then(() => rp(options))
    .then(done, done.fail);
  });

});
