const Swagger = require('swagger-client');
const ComparisonUtil = require('../util/comparison-util');
const TestUtil = require('../util/test-util');

TestUtil.assertEnv();

describe('Contact', () => {

  let contactNew;
  let customerCreated;

  beforeAll((done) => {
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

    TestUtil.swaggerClient()
    .then(client => client.apis.customers.customersCreate({body: customerNew}))
    .then(customer => customerCreated = customer.obj)
    .then(done);
  });

  beforeEach(() => {
    contactNew = {
      'id': null,
      'customerId': customerCreated.id,
      'name': 'testi kontakti',
      'postalAddress': {
        'streetAddress': 'Testitie kontaktille 1',
        'postalCode': '00900',
        'city': 'Testikontaktikaupunki'
      },
      'email': 'testkontaktemail@test.se',
      'phone': '090 9090'
    };
  });

  describe('Metadata', () => {
    it('should contain all swagger defined properties in contact', (done) => {
      TestUtil.swaggerClient()
      .then(client => ComparisonUtil.compareAgainstSwaggerSpec(
        [
          {definition: client.spec.definitions.ContactExt.properties, data: contactNew}
        ]))
      .then(diff => expect(diff).toEqual([]))
      .then(done)
      .catch(err => done.fail(err));
    });
  });

  describe('Create', () => {
    it('should be successful', (done) => {
      TestUtil.swaggerClient()
      .then(client => client.apis.contacts.contactsCreate({body: contactNew}))
      .then(customer => expect(ComparisonUtil.deepCompareNonNull('', contactNew, customer.obj)).toEqual([]))
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
        return client.apis.contacts.contactsCreate({body: contactNew})
        .then(contact => client.apis.contacts.contactsFindById({id: contact.obj.id}));
      };

      let createdCustomer;
      TestUtil.swaggerClient()
      .then(client => createFind(client))
      .then(contact => expect(ComparisonUtil.deepCompareNonNull('', contactNew, contact.obj)).toEqual([]))
      .then(done)
      .catch(err => {
        console.log('Error', err);
        done.fail(err);
      });
    });
  });
});
