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

  describe('Update', () => {
    it('should be successful', (done) => {
      let createUpdateFind = function(client) {
        return client.apis.customers.customersCreate({body: customerNew})
        .then(customer => {customerNew.name = 'updated'; customer.obj.name = 'updated'; return customer.obj;})
        .then(customer => client.apis.customers.customersUpdate({body: customer}))
        .then(customer => client.apis.customers.customersFindById({id: customer.obj.id}));
      };

      TestUtil.swaggerClient()
      .then(client => createUpdateFind(client))
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
    it('by business id', (done) => {
      customerNew.registryKey = 'testfindbybusinessid';
      let createFind = function(client) {
        return client.apis.customers.customersCreate({body: customerNew})
        .then(customer => client.apis.customers.customersFindByBusinessId({businessId: customer.obj.registryKey}));
      };

      let createdCustomer;
      TestUtil.swaggerClient()
      .then(client => createFind(client))
      // find the latest (highest database id) created customer to make sure it's the one we just inserted in this test
      .then(customers => customers.obj.reduce((acc, curr) => acc.id > curr.id ? acc : curr))
      .then(customer => expect(ComparisonUtil.deepCompareNonNull('', customerNew, customer)).toEqual([]))
      .then(done)
      .catch(err => {
        console.log('Error', err);
        done.fail(err);
      });
    });
  });
});
