const TestUtil = require('../util/test-util');
const rp = require('request-promise');

TestUtil.assertEnv();
// set timeout to 1 hour
jasmine.DEFAULT_TIMEOUT_INTERVAL = 1000 * 60 * 60;

describe('Event application mass insert', () => {

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
        'active': true,
        'country': 'FI'
      },
      'contacts': [herneContactNew]
    };

    let herneOptions = TestUtil.getPostOptions('/api/customers/withcontacts', herneCustomerWithContactsNew);
    return TestUtil.login('kasittelija')
      .then(token => {
        TestUtil.addAuthorization(herneOptions, token);
      })
      .then(() => rp(herneOptions))
      .then(cwc => herneCustomersWithContactsCreated = cwc);
  }

  beforeAll(done => {
      TestUtil.tryRetryPromise(TestUtil.tryToCreateUsers, 10, 10000)
      .then(createCustomers, done.fail)
      .then(done)
          .catch(err => done.fail(err));
  });


  describe('Event application', () => {

    it('Create', done => {
      const eventApplication = {
        'handler': null,
        'status': null,
        'type': 'EVENT',
        'notBillable': 'false',
        'kindsWithSpecifiers': {'OUTDOOREVENT' : []},
        'name': 'Hernesaaren hytinät',
        'decisionPublicityType': 'PUBLIC',
        'invoicingDate': '2018-12-22T22:00:00Z',
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
            'startTime': '2016-12-22T22:00:00Z',
            'endTime': '2016-12-26T22:00:00Z',
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
                      [2.5492099430907298E7,6672550.01914402],
                      [2.549209947802318E7,6672571.967585488],
                      [2.5492121523949243E7,6672571.920326323],
                      [2.5492121476964835E7,6672549.971884665],
                      [2.5492099430907298E7,6672550.01914402]
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
          'eventStartTime': '2016-12-23T22:00:00Z',
          'eventEndTime': '2016-12-25T22:00:00Z',
          'attendees': 5,
          'entryFee': 0,
          'ecoCompass': false,
          'pricing': 'Spiritual',
          'foodSales': true,
          'foodProviders': 'Tapahtumassa saattaa olla elintarviketoimijoita',
          'marketingProviders': 'Tapahtumassa ei luultavimmin ole markkinointitoimintaa',
          'structureArea': 54.0,
          'structureDescription': 'Paikalle rakennetaan linna',
          'structureStartTime': '2016-12-22T22:00:00Z',
          'structureEndTime': '2016-12-26T22:00:00Z',
          'timeExceptions': 'Pyhäpäivinä tauko',
          'surfaceHardness': 'HARD'
        },
        'decisionTime': null
      };

      let callCounter = 0;
      let optionsFactory = function(token) {
        return () => {
          console.log(++callCounter);

          setApplicationDates(eventApplication, callCounter % 100);

          const maxY = 6682117;
          const minY = 6672549;
          const xAddition = 22;
          const yAddition = 20;
          eventApplication.name = callCounter + ' Mass test';
          if (eventApplication.locations[0].geometry.geometries[0].coordinates[0][0][1] > maxY) {
            // move all Y coordinates back to minimum
            eventApplication.locations[0].geometry.geometries[0].coordinates[0].forEach(coordArr => coordArr[1] = coordArr[1] - (maxY - minY));
            // start next "column" i.e. increase X a bit
            eventApplication.locations[0].geometry.geometries[0].coordinates[0].forEach(coordArr => coordArr[0] += xAddition);
          } else {
            eventApplication.locations[0].geometry.geometries[0].coordinates[0].forEach(coordArr => coordArr[1] += yAddition);
          }
          eventApplication.locations[0].geometry.geometries[0].coordinates[0].forEach(coordArr => coordArr[1] += yAddition);
          let options = TestUtil.getPostOptions('/api/applications', eventApplication);
          TestUtil.addAuthorization(options, token);
          return options;
        }
      };

      TestUtil.login('kasittelija')
        .then((token) => repeatRequest(2000, optionsFactory(token)))
        .then(done);
    });
  });
});

function repeatRequest(times, optionsFactory) {
  return rp(optionsFactory()).then(() => {
    if (times > 0) {
      return repeatRequest(--times, optionsFactory);
    }
  })
}

function setApplicationDates(eventApplication, offsetBase) {
  eventApplication.locations[0].startTime = TestUtil.getStartDateString(offsetBase);
  eventApplication.locations[0].endTime = TestUtil.getEndDateString(offsetBase + 1);
  eventApplication.extension.eventStartTime = eventApplication.locations[0].startTime;
  eventApplication.extension.eventEndTime = eventApplication.locations[0].endTime;
  eventApplication.extension.structureStartTime = eventApplication.locations[0].startTime;
  eventApplication.extension.structureEndTime = eventApplication.locations[0].endTime;
}
