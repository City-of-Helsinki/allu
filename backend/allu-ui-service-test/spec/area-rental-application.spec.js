const TestUtil = require('../util/test-util');
const rp = require('request-promise');

TestUtil.assertEnv();

describe('Area rental application', () => {

  let area1StartTime = TestUtil.getStartDateString(10);
  let area1EndTime = TestUtil.getEndDateString(20);

  let area2StartTime = TestUtil.getStartDateString(15);
  let area2EndTime = TestUtil.getEndDateString(25);

  let reimaCustomersWithContactsCreated;
  let bulvaaniCustomersWithContactsCreated;

  function createCustomers() {
    const applicantContactNew = {
      'id': null,
      'applicantId': null,
      'name': 'Raimo Remontoija',
      'streetAddress': 'Bulvaanitie 42',
      'postalCode': '21200',
      'city': 'Turku',
      'email': 'raimo.remontoija@bulvaaniremontit.com',
      'phone': '0245223432',
      'active': true
    };

    const contractorContactNew = {
      'id': null,
      'applicantId': null,
      'name': 'Bo Bulvaani',
      'streetAddress': 'Salontie 1',
      'postalCode': '20100',
      'city': 'PL 200',
      'email': 'bo.bulvaani@bulvaaniremontit.com',
      'phone': '0232195450',
      'active': true
    };

    const applicantCustomerWithContactsNew = {
      'roleType': 'APPLICANT',
      'customer': {
        'id': null,
        'type': 'PERSON',
        'representative': null,
        'name': 'Reima Remontoija',
        'registryKey': '43124123',
        'postalAddress': {
          'streetAddress': 'Remonttikatu 123',
          'postalCode': '20200',
          'city': 'Turku'
        },
        'email': 'reima.remontoija@reimanremontit.net',
        'phone': '02213412',
        'active': true,
        'country': 'FI'
      },
      'contacts': [applicantContactNew]
    };

    const contractorCustomerWithContactsNew = {
      'roleType': 'CONTRACTOR',
      'customer': {
        'id': null,
        'type': 'COMPANY',
        'representative': null,
        'name': 'bulvaaniremontit.com',
        'registryKey': '342312-1',
        'postalAddress': {
          'streetAddress': 'Turuntie 10',
          'postalCode': '20100',
          'city': 'Turkku'
        },
        'email': 'suorittaja@bulvaaniremontit.com',
        'phone': '0421431234',
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
      .then(cwc => reimaCustomersWithContactsCreated = cwc)
      .then(() => rp(contractorOptions))
      .then(cwc => bulvaaniCustomersWithContactsCreated = cwc);
  }

  beforeAll(done => {
    TestUtil.beforeAll(createCustomers)
      .then(done)
      .catch(err => done.fail(err));
  });


  it('Create', done => {

    const areaRental = {
      'type': 'AREA_RENTAL',
      'notBillable': 'false',
      'kindsWithSpecifiers': {'PROPERTY_RENOVATION' : []},
      'name': 'Aluevuokraus',
      'decisionPublicityType': 'PUBLIC',
      'customersWithContacts':[
        reimaCustomersWithContactsCreated, bulvaaniCustomersWithContactsCreated
      ],
      'locations': [
        {
          'locationKey': 1,
          'locationVersion': 1,
          'startTime': area1StartTime,
          'endTime': area1EndTime,
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
                'coordinates': [
                  [
                    [
                      2.5496289375E7,
                      6673376.749999999
                    ],
                    [
                      2.5496333375E7,
                      6673244.249999997
                    ],
                    [
                      2.5496343875E7,
                      6673249.749999999
                    ],
                    [
                      2.5496302875E7,
                      6673383.249999999
                    ],
                    [
                      2.5496302875E7,
                      6673383.249999999
                    ],
                    [
                      2.54962871875E7,
                      6673379.374999997
                    ],
                    [
                      2.5496289375E7,
                      6673376.749999999
                    ]
                  ]
                ]
              }
            ]
          },
          'area': 1875.8281250381842,
          'areaOverride': null,
          'postalAddress': {
            'streetAddress': 'Mannerheimintie 30',
            'postalCode': '00100',
            'city': 'Helsinki'
          },
          'fixedLocationIds': [
          ],
          'underpass': false
        },
        {
          'locationKey': 2,
          'locationVersion': 1,
          'startTime': area2StartTime,
          'endTime': area2EndTime,
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
                'coordinates': [
                  [
                    [
                      2.54963136875E7,
                      6673153.749999998
                    ],
                    [
                      2.54963121875E7,
                      6673164.999999999
                    ],
                    [
                      2.54963156875E7,
                      6673180.500000001
                    ],
                    [
                      2.54963246875E7,
                      6673190.999999999
                    ],
                    [
                      2.54963384375E7,
                      6673197.749999999
                    ],
                    [
                      2.54963564375E7,
                      6673195.750000002
                    ],
                    [
                      2.54963669375E7,
                      6673187.249999999
                    ],
                    [
                      2.54963776875E7,
                      6673194.499999999
                    ],
                    [
                      2.54963586875E7,
                      6673205.999999998
                    ],
                    [
                      2.54963456875E7,
                      6673207.75
                    ],
                    [
                      2.54963291875E7,
                      6673203.5
                    ],
                    [
                      2.54963179375E7,
                      6673197.25
                    ],
                    [
                      2.54963081875E7,
                      6673182.499999997
                    ],
                    [
                      2.54963046875E7,
                      6673166.249999999
                    ],
                    [
                      2.54963136875E7,
                      6673153.749999998
                    ]
                  ]
                ]
              }
            ]
          },
          'area': 848.6249999612337,
          'areaOverride': null,
          'postalAddress': {
            'streetAddress': 'Arkadiankatu 1',
            'postalCode': '00101',
            'city': null
          },
          'fixedLocationIds': [],
          'underpass': false
        }
      ],
      'extension': {
        'applicationType': 'AREA_RENTAL',
        'workPurpose': 'Tehdään remonttia alueilla',
        'additionalInfo': 'Remontti valmis, kun se on valmis',
        'trafficArrangements': 'Eduskuntaan ei pääse, mene muualle',
        'trafficArrangementImpedimentType': 'INSIGNIFICANT_IMPEDIMENT'
      },
      'applicationTags': [
      ],
      'invoicingDate': '2018-12-22T22:00:00Z',
      'invoiceRecipientId': reimaCustomersWithContactsCreated.id,
      'startTime': area1StartTime,
      'endTime': area2EndTime
    };

    let options = TestUtil.getPostOptions('/api/applications', areaRental);
    TestUtil.login('kasittelija')
      .then(token => TestUtil.addAuthorization(options, token))
      .then(() => rp(options))
      .then(done, done.fail);
  });

});
