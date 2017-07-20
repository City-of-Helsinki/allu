
const Swagger = require('swagger-client')
const ComparisonUtil = require('../util/comparison-util')

if (!process.env.SWAGGER_HOST || !process.env.SWAGGER_JSON_URL) {
  console.error('Test expects SWAGGER_HOST AND SWAGGER_JSON_URL envinronment variables to be defined!');
  console.error('For example:');
  console.error('SWAGGER_HOST=localhost:9040 SWAGGER_JSON_URL=http://localhost:9040/api-docs/swagger.json npm test');
  process.exit(255);
}

describe('Application', () => {

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
          // TODO: customer and contact ids won't be available unless insert_test_data.sh has been executed. This should be improved once external-service provides customer creation
          'customer': 210,
          'roleType': 'APPLICANT',
          'contacts': [35]
        };
        locationExt = {
          'id': null,
          'locationKey': null,
          'locationVersion': null,
          'startTime': '2017-07-17T10:42:48.315Z',
          'endTime': '2017-08-17T10:42:48.315Z',
          'geometry': null,
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
        swaggerClient()
          .then(client => compareAgainstSwaggerSpec(
            [
              { definition: client.spec.definitions.ApplicationExt.properties, data: applicationExtNew },
              { definition: client.spec.definitions.CustomerWithContactsExt.properties, data: customerWithContactsExt },
              { definition: client.spec.definitions.LocationExt.properties, data: locationExt },
              { definition: client.spec.definitions.ExcavationAnnouncementExt.properties, data: extension },
            ]))
          .then(diff => expect(diff).toEqual([]))
          .then(done)
          .catch(err => done.fail(err));
      });

      it('should create an application', (done) => {
        swaggerClient()
          .then(client => prepareClient(client))
          .then(client => client.apis.applications.applicationsCreate({ body: applicationExtNew}) )
          .then(application => expect(ComparisonUtil.deepCompareNonNull('', applicationExtNew, application.obj)).toEqual([]))
          .then(done)
          .catch(err => done.fail(err));
      });

    });
});

function swaggerClient() {
  return new Swagger({
    url: process.env.SWAGGER_JSON_URL,
    usePromise: true
  });
}

function equalMapKeys(map1, map2) {
  let keys1 = new Set(Object.keys(map1));
  let keys2 = new Set(Object.keys(map2));
  return equalSets(keys1, keys2);
}

function equalSets(set1, set2) {
  return (set1.length === set2.length && [...set1].every((o) => set2.has(o)));
}

function compareAgainstSwaggerSpec(definitionDataPairs) {
  let diff = definitionDataPairs.reduce(
    (acc, curr) => acc.concat(intersectionComplement(new Set(Object.keys(curr.definition)), new Set(Object.keys(curr.data)))), []);
  return diff;
}

/*
 * Returns values that are not shared by the sets.
 */
function intersectionComplement(set1, set2) {
  let intersectionComplement = [];
  if (!set1 || !set2) {
    throw new Error('intersectionComplement requires sets to be defined');
  }
  [...set1].every((o) => set2.has(o) || intersectionComplement.push(o));
  [...set2].every((o) => set1.has(o) || intersectionComplement.push(o));
  return intersectionComplement;
}

function prepareClient(client) {
  client.spec.host = process.env.SWAGGER_HOST;
  client.spec.basePath = null;
  return client;
}
