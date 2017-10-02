const ComparisonUtil = require('../util/comparison-util');
const TestUtil = require('../util/test-util');

TestUtil.assertEnv();

describe('PlacementContractApplication', () => {

	  let customerCreated;
	  let contactCreated;
	  let placementContractExtNew;
	  let extension;
	  let customerWithContactsExt;
	  let locationExt;

	  beforeAll((done) => {

	    let contactNew = {
	        'id': null,
	        'customerId': null,
	        'name': 'Test Contact',
	        'postalAddress': {
	          'streetAddress': 'Test Contact Street',
	          'postalCode': '40320',
	          'city': 'Test Contact City'
	        },
	        'email': 'test@contact.fi',
	        'phone': '090 9090'
	      };

	      let customerNew = {
	        'id': null,
	        'type': 'COMPANY',
	        'name': 'Test Company',
	        'postalAddress': {
	          'streetAddress': 'Test Company Street',
	          'postalCode': '40100',
	          'city': 'Test Company City'
	        },
	        'email': 'test@company.fi',
	        'phone': '010 1010',
	        'registryKey': '1234-111'
	      };

	      extension = {
	          'terms': 'Application terms',
	          'applicationType': 'PLACEMENT_CONTRACT',
	          'diaryNumber': '12312/AB',
	          'additionalInfo': 'Placement contract additional information',
	          'generalTerms': 'Placement contract general terms'
	      };
	      customerWithContactsExt = {
	          'customer': null,
	          'roleType': 'APPLICANT',
	          'contacts': null
	      }

	      let locationGeometry = {
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
	          'underpass': false
	        };
	      placementContractExtNew = {
	          'id': null,
	          'projectId': null,
            'type': 'PLACEMENT_CONTRACT',
            'kindsWithSpecifiers': {'WATER_AND_SEWAGE' : []},
            'name': 'Test Placement Contract',
            'customersWithContacts': [customerWithContactsExt],
	          'locations': [locationExt],
	          'status': 'PENDING',
	          'applicationTags': null,
	          'creationTime': null,
	          'startTime': null,
	          'endTime': null,
	          'extension': extension
	        };

	      // create customer and related contact for the customer to be used in this spec
	      TestUtil.swaggerClient()
	      .then(client =>
	          client.apis.customers.customersCreate({ body: customerNew})
	          .then(customer => {
	            customerCreated = customer.obj;
	            contactNew.customerId = customerCreated.id;
	            return client.apis.contacts.contactsCreate({body: contactNew}).then(contact => contactCreated = contact.obj);
	          })
	      )
	      .then(() => {
	        customerWithContactsExt.customer = customerCreated.id;
	        customerWithContactsExt.contacts = [contactCreated.id];
	      })
	      .then(() => done());
	  });

	  describe('Create Placement Contract', () => {

	    it('should contain all swagger defined properties in application', (done) => {
	      TestUtil.swaggerClient()
	      .then(client => ComparisonUtil.compareAgainstSwaggerSpec(
	        [
	          {definition: client.spec.definitions.ApplicationExt.properties, data: placementContractExtNew},
	          {definition: client.spec.definitions.CustomerWithContactsExt.properties, data: customerWithContactsExt},
	          {definition: client.spec.definitions.LocationExt.properties, data: locationExt},
	          {definition: client.spec.definitions.PlacementContractExt.properties, data: extension},
	        ]))
	      .then(diff => expect(diff).toEqual([]))
	      .then(done)
	      .catch(err => done.fail(err));
	    });

	    it('should create an application', (done) => {
	      TestUtil.swaggerClient()
	      .then(client => client.apis.applications.applicationsCreate({body: placementContractExtNew}))
	      .then(application => expect(ComparisonUtil.deepCompareNonNull('', placementContractExtNew, application.obj)).toEqual([]))
	      .then(done)
	      .catch(err => {
	        console.log('Error', err);
	        done.fail(err);
	      });
	    });
	  });
	  describe('Update Placement Contract Extension', () => {
	    let placementContractExtUpdated;
	    let applicationId;
	    beforeEach((done) => {
	      TestUtil.swaggerClient()
	      .then(client => client.apis.applications.applicationsCreate({body: placementContractExtNew}))
	      .then(application =>  {
	        placementContractExtUpdated = application.obj;
	        placementContractExtUpdated.extension.diaryNumber = '99999/AB';
	        placementContractExtUpdated.extension.additionalInfo = 'Updated additional information';
	        placementContractExtUpdated.extension.generalTerms = 'Updated terms',
	        applicationId = application.obj.id;
	      })
	      .then(done)
	      .catch(err => {
	        console.log('Error', err);
	        done.fail(err);
	      });
	    });
	    it('should update placement contract specific data', (done) => {
	      TestUtil.swaggerClient()
	      .then(client => client.apis.applications.applicationsUpdate({id: applicationId, body: placementContractExtUpdated}))
	      .then(application => expect(ComparisonUtil.deepCompareNonNull('', placementContractExtUpdated, application.obj)).toEqual([]))
	      .then(done)
	      .catch(err => {
	        console.log('Error', err);
	        done.fail(err);
	      });
	    });
	  });
});