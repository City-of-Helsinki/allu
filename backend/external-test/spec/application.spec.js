const ComparisonUtil = require('../util/comparison-util');
const TestUtil = require('../util/test-util');

TestUtil.assertEnv();

describe('Application', () => {

  let customerCreated;
  let contactCreated;

  beforeAll((done) => {

    let contactNew = {
      'id': null,
      'customerId': null,
      'name': 'testi kontakti',
      'postalAddress': {
        'streetAddress': 'Testitie kontaktille 1',
        'postalCode': '00900',
        'city': 'Testikontaktikaupunki'
      },
      'email': 'testkontaktemail@test.se',
      'phone': '090 9090'
    };

    let customerNew = {
      'id': null,
      'type': 'COMPANY',
      'name': 'testi firma',
      'postalAddress': {
        'streetAddress': 'Testitie 1',
        'postalCode': '00100',
        'city': 'Testikaupunki'
      },
      'email': 'testemail@test.fi',
      'phone': '010 1010',
      'registryKey': '1234-123'
    };

    // create customer and related contact for the customer to be used in this spec
    TestUtil.swaggerClient()
    .then(client =>
        client.apis.customers.customersCreate({ body: customerNew})
        .then(customer => {
          customerCreated = customer.obj;
          contactNew.customerId = customerCreated.id;
          return client.apis.contacts.contactsCreate({ body: contactNew}).then(contact => contactCreated = contact.obj);
        })
    )
    .then(() => done());
  });

  describe('Create Excavation Announcement', () => {
    let applicationExtNew;
    let extension;
    let customerWithContactsExt;
    let locationExt;

    beforeEach(() => {

      extension = {
        'applicationType': 'EXCAVATION_ANNOUNCEMENT',
        'terms': 'Some terms for application',
        'pksCard': true,
        'constructionWork': false,
        'maintenanceWork': false,
        'emergencyWork': null,
        'propertyConnectivity': false,
        'winterTimeOperation': null,
        'workFinished': null,
        'unauthorizedWorkStartTime': null,
        'unauthorizedWorkEndTime': null,
        'guaranteeEndTime': null,
        'cableReportId': null,
        'additionalInfo': 'Some additional info on extension',
        'trafficArrangements': 'No problem with traffic arrangements',
        'trafficArrangementImpedimentType': 'NO_IMPEDIMENT'
      };
      customerWithContactsExt = {
        'customer': customerCreated.id,
        'roleType': 'APPLICANT',
        'contacts': [contactCreated.id]
      };
      locationGeometry = {
        "type": "GeometryCollection",
        "crs": {
          "properties": {
            "name": "EPSG:3879"
          },
          "type": "name"
        },
        "bbox": null,
        "geometries": [
          {
            "type": "Polygon",
            "crs": null,
            "bbox": [
              2.549589275E7,
              6674232.999998959,
              2.549606025E7,
              6674407.499998961
            ],
            "coordinates": [
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
      };
      locationExt = {
        'id': null,
        'locationKey': null,
        'locationVersion': null,
        'startTime': '2017-07-17T10:42:48.315Z',
        'endTime': '2017-08-17T10:42:48.315Z',
        'additionalInfo': null,
        'geometry': locationGeometry,
        'area': null,
        'areaOverride': null,
        'postalAddress': null,
        'paymentTariff': null,
        'paymentTariffOverride': null,
        'underpass': null
      };
      applicationExtNew = {
        'id': null,
        'projectId': null,
        'customersWithContacts': [customerWithContactsExt],
        'locations': [locationExt],
        'status': 'PENDING',
        'type': 'EXCAVATION_ANNOUNCEMENT',
        'kind': 'REPAVING',
        'applicationTags': null,
        'name': 'test application',
        'creationTime': null,
        'startTime': null,
        'endTime': null,
        'extension': extension
      }
    });

    it('should contain all swagger defined properties in application', (done) => {
      TestUtil.swaggerClient()
      .then(client => ComparisonUtil.compareAgainstSwaggerSpec(
        [
          {definition: client.spec.definitions.ApplicationExt.properties, data: applicationExtNew},
          {definition: client.spec.definitions.CustomerWithContactsExt.properties, data: customerWithContactsExt},
          {definition: client.spec.definitions.LocationExt.properties, data: locationExt},
          {definition: client.spec.definitions.ExcavationAnnouncementExt.properties, data: extension},
        ]))
      .then(diff => expect(diff).toEqual([]))
      .then(done)
      .catch(err => done.fail(err));
    });

    it('should create an application', (done) => {
      TestUtil.swaggerClient()
      .then(client => client.apis.applications.applicationsCreate({body: applicationExtNew}))
      .then(application => expect(ComparisonUtil.deepCompareNonNull('', applicationExtNew, application.obj)).toEqual([]))
      .then(done)
      .catch(err => {
        console.log('Error', err);
        done.fail(err);
      });
    });
  });
});
