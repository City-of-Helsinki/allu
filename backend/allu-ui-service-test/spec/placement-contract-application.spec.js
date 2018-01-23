const TestUtil = require('../util/test-util');
const rp = require('request-promise');

TestUtil.assertEnv();

describe('Placement contract application', () => {

  let applicationStartTime = TestUtil.getISODateString(-100);
  let applicationEndTime = TestUtil.getISODateString(100);

  let applicantWithContactsCreated;
  let representativeCustomersWithContactsCreated;

  function createCustomers() {
    const applicantContactNew = {
      'id': null,
      'applicantId': null,
      'name': 'Petri Placement',
      'streetAddress': '321',
      'postalCode': '00300',
      'city': 'Helsinki',
      'email': 'petri.placement@placementcontract.fi',
      'phone': '031234567',
      'active': true
    };

    const representativeContactNew = {
      'id': null,
      'applicantId': null,
      'name': 'Simo Sijoittaja',
      'streetAddress': 'Kouvolantie 4',
      'postalCode': '50321',
      'city': 'Kouvola',
      'email': 'simo.sijoittaja@sijoitusfirmaoyab.com',
      'phone': '03123352',
      'active': true
    };

    const applicantCustomerWithContactsNew = {
      'roleType': 'APPLICANT',
      'customer': {
        'id': null,
        'type': 'PERSON',
        'representative': null,
        'name': 'Seppo Sijoittaja',
        'registryKey': '123',
        'postalAddress': {
          'streetAddress': 'Sijoitusosoite 13',
          'postalCode': '00200',
          'city': 'Helsinki'
        },
        'email': 'seppo.sijoittaja@sijoitussopimus.com',
        'phone': '040095634',
        'active': true
      },
      'contacts': [applicantContactNew]
    };

    const representativeCustomerWithContactsNew = {
      'roleType': 'REPRESENTATIVE',
      'customer': {
        'id': null,
        'type': 'COMPANY',
        'representative': null,
        'name': 'sijotusfirmaoyab.com',
        'registryKey': '45432-1',
        'postalAddress': {
          'streetAddress': 'Kouvolankatu 1',
          'postalCode': '50022',
          'city': 'Kouvola'
        },
        'email': 'sijoittaja@sijoitusfirmaoyab.com',
        'phone': '0421457564',
        'active': true
      },
      'contacts': [representativeContactNew]
    };

    let applicantOptions = TestUtil.getPostOptions('/api/customers/withcontacts', applicantCustomerWithContactsNew);
    let representativeOptions = TestUtil.getPostOptions('/api/customers/withcontacts', representativeCustomerWithContactsNew);
    return TestUtil.login('kasittelija')
      .then(token => {
        TestUtil.addAuthorization(applicantOptions, token);
        TestUtil.addAuthorization(representativeOptions, token)
      })
      .then(() => rp(applicantOptions))
      .then(cwc => applicantWithContactsCreated = cwc)
      .then(() => rp(representativeOptions))
      .then(cwc => representativeCustomersWithContactsCreated = cwc);
  }

  beforeAll(done => {
    TestUtil.beforeAll(createCustomers)
    .then(done)
    .catch(err => done.fail(err));
  });

  it('Create', done => {

    const placementContract = {
        'type': 'PLACEMENT_CONTRACT',
        'kindsWithSpecifiers': {'WATER_AND_SEWAGE' : []},
        'notBillable': 'false',
        'name': 'Sijoitussopimus',
        'decisionPublicityType': 'PUBLIC',
        'customersWithContacts':[applicantWithContactsCreated, representativeCustomersWithContactsCreated],
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
              'bbox': null,
              'geometries': [
                {
                  'type': 'Polygon',
                  'crs': null,
                  'bbox': [
                    2.54970459375E7,
                    6672733.874998962,
                    2.54971329375E7,
                    6672741.624998964
                  ],
                  'coordinates': [
                    [
                      [
                        2.54970459375E7,
                        6672736.874998965
                      ],
                      [
                        2.54971326875E7,
                        6672741.624998964
                      ],
                      [
                        2.54971329375E7,
                        6672738.6249989625
                      ],
                      [
                        2.54970461875E7,
                        6672733.874998962
                      ],
                      [
                        2.54970459375E7,
                        6672736.874998965
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
          'applicationType': 'PLACEMENT_CONTRACT',
          'diaryNumber': '290117/AB',
          'additionalInfo': 'Lisätietoja ei juuri ole, mutta sijoitettava on',
          'generalTerms': 'Ei saa rikkoa puistoa!'
        },
        'applicationTags': [
        ],
        'invoicingDate': '2018-12-22T22:00:00Z'
      };


    let options = TestUtil.getPostOptions('/api/applications', placementContract);
    TestUtil.login('kasittelija')
      .then(token => TestUtil.addAuthorization(options, token))
      .then(() => rp(options))
      .then(done, fail);
  });

});
