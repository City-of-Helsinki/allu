const TestUtil = require('../util/test-util');
const rp = require('request-promise');

TestUtil.assertEnv();

describe('Event application', () => {

  let application1StartTime = TestUtil.getISODateString(-15);
  let application1EndTime = TestUtil.getISODateString(1);
  let event1StartTime = TestUtil.getISODateString(-13);
  let event1EndTime = TestUtil.getISODateString(0);
  let structure1StartTime = application1StartTime;
  let structure1EndTime = application1EndTime;

  let application2StartTime = TestUtil.getISODateString(-30);
  let application2EndTime = TestUtil.getISODateString(-16);
  let event2StartTime = TestUtil.getISODateString(-29);
  let event2EndTime = TestUtil.getISODateString(-17);
  let structure2StartTime = application2StartTime;
  let structure2EndTime = application2EndTime;


  let herneCustomersWithContactsCreated;
  let tervaCustomersWithContactsCreated;

  function createCustomers() {
    const herneContactNew = {
      'id': null,
      'name': 'Heikki Herne',
      'streetAddress': 'Hernikka 1',
      'postalCode': '33000',
      'city': 'Tampere',
      'email': 'heikki@hernerokka.fi',
      'phone': '03-12121212',
      'active': true
    };

    const herneCustomerWithContactsNew = {
      'roleType': 'APPLICANT',
      'customer': {
        'id': null,
        'type': 'COMPANY',
        'person': null,
        'name': 'Hakija Inc.',
        'registryKey': '7654321-6',
        'postalAddress': {
          'streetAddress': 'Hernarikatu 10',
          'postalCode': '00430',
          'city': 'Vantaa'
        },
        'email': 'hernari@hernerokka.fi',
        'phone': '03-1234567',
        'active': true
      },
      'contacts': [herneContactNew]
    };

    const tervaContactNew = {
      'id': null,
      'applicantId': null,
      'name': 'Terva Miettinen',
      'streetAddress': 'Tervatie 1',
      'postalCode': '00300',
      'city': 'Helsinki',
      'email': 'tervaaja@tervaajat.fi',
      'phone': '09-9876543',
      'active': true
    };

    const tervaCustomerWithContactsNew = {
      'roleType': 'APPLICANT',
      'customer': {
        'type': 'COMPANY',
        'person': null,
        'id': null,
        'name': 'Tervaajat Oy',
        'registryKey': '1234567-1',
        'postalAddress': {
          'streetAddress': 'Tervatie 1',
          'postalCode': '00300',
          'city': 'Helsinki'
        },
        'email': 'info@tervaajat.fi',
        'phone': '09-1234567',
        'active': true
      },
      'contacts': [tervaContactNew]
    };

    let herneOptions = TestUtil.getPostOptions('/api/customers/withcontacts', herneCustomerWithContactsNew);
    let tervaOptions = TestUtil.getPostOptions('/api/customers/withcontacts', tervaCustomerWithContactsNew);
    return TestUtil.login('kasittelija')
      .then(token => {
        TestUtil.addAuthorization(herneOptions, token);
        TestUtil.addAuthorization(tervaOptions, token);
      })
      .then(() => rp(herneOptions))
      .then(cwc => herneCustomersWithContactsCreated = cwc)
      .then(() => rp(tervaOptions))
      .then(cwc => tervaCustomersWithContactsCreated = cwc);
  }

  beforeAll(done => {
    TestUtil.beforeAll(createCustomers)
    .then(done)
    .catch(err => done.fail(err));
  });

  describe('Hernesaari', () => {

    it('Create', done => {
      const hernesaari = {
        'handler': null,
        'status': null,
        'type': 'EVENT',
        'notBillable': 'false',
        'kindsWithSpecifiers': {'OUTDOOREVENT' : []},
        'name': 'Hernesaaren hytinät',
        'decisionPublicityType': 'PUBLIC',
        'decisionDistributionList': [
          {
            'id': null,
            'name': 'test',
            'distributionType': 'PAPER',
            'email': null,
            'postalAddress': {
              'streetAddress': 'Katu 1',
              'postalCode': '00332',
              'city': 'Hki'
            }
          }
        ],
        'creationTime': '2016-07-15T10:36:09.721Z',
        'customersWithContacts':[
          herneCustomersWithContactsCreated
        ],
        'locations': [
          {
            'id': null,
            'startTime': application1StartTime,
            'endTime': application1EndTime,
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
                    2.54956725E7,
                    6670728.999998969,
                    2.54961765E7,
                    6671301.999998963
                  ],
                  'coordinates': [
                    [
                      [
                        2.54958865E7,
                        6671301.999998963
                      ],
                      [
                        2.54958675E7,
                        6671192.999998967
                      ],
                      [
                        2.54958485E7,
                        6671194.999998967
                      ],
                      [
                        2.54956725E7,
                        6670865.999998967
                      ],
                      [
                        2.54959335E7,
                        6670728.999998969
                      ],
                      [
                        2.54959495E7,
                        6670734.999998969
                      ],
                      [
                        2.54960795E7,
                        6670969.999998965
                      ],
                      [
                        2.54960785E7,
                        6670999.999998964
                      ],
                      [
                        2.54961765E7,
                        6671187.999998966
                      ],
                      [
                        2.54960985E7,
                        6671228.999998964
                      ],
                      [
                        2.54958865E7,
                        6671301.999998963
                      ]
                    ]
                  ]
                }
              ]
            },
            'underpass': false,
            'postalAddress': {
              'streetAddress': null,
              'postalCode': null,
              'city': null
            }
          }
        ],
        'extension': {
          'applicationType': 'EVENT',
          'nature': 'PUBLIC_FREE',
          'description': 'Hernekeittopäivä pakolliseksi kansallisella tasolla',
          'url': 'http://www.hernerokka.fi',
          'eventStartTime': event1StartTime,
          'eventEndTime': event1EndTime,
          'attendees': 5,
          'entryFee': 0,
          'ecoCompass': false,
          'pricing': 'Spiritual',
          'foodSales': true,
          'foodProviders': 'Tapahtumassa saattaa olla elintarviketoimijoita',
          'marketingProviders': 'Tapahtumassa ei luultavimmin ole markkinointitoimintaa',
          'structureArea': 54.0,
          'structureDescription': 'Paikalle rakennetaan linna',
          'structureStartTime': structure1StartTime,
          'structureEndTime': structure1EndTime,
          'timeExceptions': 'Pyhäpäivinä tauko'
        },
        'decisionTime': null,
        'invoicingDate': '2018-12-22T22:00:00Z'
      };

      let options = TestUtil.getPostOptions('/api/applications', hernesaari);
      TestUtil.login('kasittelija')
        .then(token => TestUtil.addAuthorization(options, token))
        .then(() => rp(options))
        .then(done);
    });
  });

  describe('Tervasaari', () => {
    it('Create', done => {

      const tervasaari = {
        'id': null,
        'project': {
          'id': null,
          'name': 'Mock Project',
          'type': null,
          'information': null
        },
        'handler': null,
        'status': null,
        'type': 'EVENT',
        'kindsWithSpecifiers': {'OUTDOOREVENT': []},
        'notBillable': 'false',
        'applicationTags': [
          {
            'addedBy': 1,
            'type': 'ADDITIONAL_INFORMATION_REQUESTED',
            'creationTime': '2016-07-15T05:08:10.514Z'
          }
        ],
        'name': 'Tervasaaren tohinat',
        'decisionPublicityType': 'PUBLIC',
        'creationTime': '2016-07-15T05:08:10.514Z',
        'customersWithContacts':[tervaCustomersWithContactsCreated],
        'locations': [
          {
            'id': null,
            'startTime': application2StartTime,
            'endTime': application2EndTime,
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
                    2.5498173375E7,
                    6673336.2499989625,
                    2.5498281625E7,
                    6673438.999998962
                  ],
                  'coordinates': [
                    [
                      [
                        2.549817625E7,
                        6673431.499998962
                      ],
                      [
                        2.5498210875E7,
                        6673436.749998962
                      ],
                      [
                        2.5498222875E7,
                        6673438.7499989625
                      ],
                      [
                        2.5498228125E7,
                        6673438.999998962
                      ],
                      [
                        2.5498233875E7,
                        6673438.2499989625
                      ],
                      [
                        2.5498246875E7,
                        6673432.2499989625
                      ],
                      [
                        2.5498244625E7,
                        6673418.7499989625
                      ],
                      [
                        2.5498246875E7,
                        6673408.999998961
                      ],
                      [
                        2.5498251625E7,
                        6673399.74999896
                      ],
                      [
                        2.5498256125E7,
                        6673393.2499989625
                      ],
                      [
                        2.5498256625E7,
                        6673388.499998962
                      ],
                      [
                        2.5498250125E7,
                        6673382.499998961
                      ],
                      [
                        2.5498245375E7,
                        6673382.4999989625
                      ],
                      [
                        2.5498245125E7,
                        6673363.249998964
                      ],
                      [
                        2.5498253375E7,
                        6673363.499998965
                      ],
                      [
                        2.5498265125E7,
                        6673360.499998961
                      ],
                      [
                        2.5498269375E7,
                        6673347.249998961
                      ],
                      [
                        2.5498281625E7,
                        6673340.499998962
                      ],
                      [
                        2.5498273625E7,
                        6673336.2499989625
                      ],
                      [
                        2.5498226875E7,
                        6673345.999998961
                      ],
                      [
                        2.5498188875E7,
                        6673375.249998964
                      ],
                      [
                        2.5498187375E7,
                        6673387.999998962
                      ],
                      [
                        2.5498180125E7,
                        6673413.4999989625
                      ],
                      [
                        2.5498181625E7,
                        6673422.999998961
                      ],
                      [
                        2.5498175625E7,
                        6673420.999998962
                      ],
                      [
                        2.5498173375E7,
                        6673425.499998963
                      ],
                      [
                        2.5498175625E7,
                        6673427.749998964
                      ],
                      [
                        2.549817625E7,
                        6673431.499998962
                      ]
                    ]
                  ]
                }
              ]
            },
            'underpass': false,
            'postalAddress': {
              'streetAddress': null,
              'postalCode': null,
              'city': null
            }
          }
        ],
        'extension': {
          'applicationType': 'EVENT',
          'nature': 'PUBLIC_FREE',
          'description': 'Tutustutaan tervan nykyiseen käyttöön',
          'url': 'http://www.terva.fi',
          'eventStartTime': event2StartTime,
          'eventEndTime': event2EndTime,
          'attendees': 1200,
          'entryFee': 0,
          'ecoCompass': true,
          'pricing': 'ArtOrCulture',
          'foodSales': true,
          'foodProviders': 'Tervanakit Oy',
          'marketingProviders': 'Markkinoidaan tervaa ja myydään tynnyreitä',
          'structureArea': 205.0,
          'structureDescription': 'Paikalla tervan polttohauta',
          'structureStartTime': structure2StartTime,
          'structureEndTime': structure2EndTime,
          'timeExceptions': 'Tapahtuma-ajalla ei ole poikkeuksia'
        },
        'decisionTime': null,
        'invoicingDate': '2018-12-22T22:00:00Z'
      };

      let options = TestUtil.getPostOptions('/api/applications', tervasaari);
      TestUtil.login('kasittelija')
      .then(token => TestUtil.addAuthorization(options, token))
      .then(() => rp(options))
      .then(done, done.fail);
    });
  });
});
