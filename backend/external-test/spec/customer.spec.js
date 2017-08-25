const Swagger = require('swagger-client');
const ComparisonUtil = require('../util/comparison-util');
const TestUtil = require('../util/test-util');

TestUtil.assertEnv();

describe('Customer', () => {

  let customerNew;

  beforeEach(() => {
    customerNew = {
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
  });

  describe('Metadata', () => {
    it('should contain all swagger defined properties in customer', (done) => {
      TestUtil.swaggerClient()
      .then(client => ComparisonUtil.compareAgainstSwaggerSpec(
        [
          {definition: client.spec.definitions.CustomerExt.properties, data: customerNew}
        ]))
      .then(diff => expect(diff).toEqual([]))
      .then(done)
      .catch(err => done.fail(err));
    });
  });

  describe('Create', () => {
    it('should be successful', (done) => {
      TestUtil.swaggerClient()
      .then(client => client.apis.customers.customersCreate({body: customerNew}))
      .then(customer => expect(ComparisonUtil.deepCompareNonNull('', customerNew, customer.obj)).toEqual([]))
      .then(done)
      .catch(err => {
        console.log('Error', err);
        done.fail(err);
      });
    });
  });

  describe('Find', () => {
    it('by id', (done) => {

      let createFind = function(client) {
        return client.apis.customers.customersCreate({body: customerNew})
        .then(customer => client.apis.customers.customersFindById({id: customer.obj.id}));
      };

      let createdCustomer;
      TestUtil.swaggerClient()
      .then(client => createFind(client))
      .then(customer => expect(ComparisonUtil.deepCompareNonNull('', customerNew, customer.obj)).toEqual([]))
      .then(done)
      .catch(err => {
        console.log('Error', err);
        done.fail(err);
      });
    });
  });
});
