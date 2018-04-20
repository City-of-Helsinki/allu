const TestUtil = require('../util/test-util');
const rp = require('request-promise');

TestUtil.assertEnv();

describe('Project', () => {

  function createCustomers() {
    const applicantContactNew = {
      'email': null,
      'name': 'Kalle',
      'applicantId': null,
      'id': null,
      'postalCode': null,
      'streetAddress': null,
      'city': null,
      'phone': '040-1234567',
      'email': 'kalle.kaivuri@kallenkaivuri.fi',
      'active': true
    };

    const applicantCustomerWithContactsNew = {
      'roleType': 'APPLICANT',
      'customer': {
        'postalAddress': {
          'streetAddress': '',
          'city': '',
          'postalCode': ''
        },
        'id': null,
        'name': 'Kallen Kaivuri',
        'email': '',
        'phone': '',
        'registryKey': '123',
        'representative': null,
        'type': 'COMPANY',
        'active': true,
        'country': 'FI'
      },
      'contacts': [applicantContactNew]
    };

    let applicantOptions = TestUtil.getPostOptions('/api/customers/withcontacts', applicantCustomerWithContactsNew);
    return TestUtil.login('kasittelija')
      .then(token => {
        TestUtil.addAuthorization(applicantOptions, token);
      })
      .then(() => rp(applicantOptions))
      .then(cwc => applicantCustomersWithContactsCreated = cwc)
  }

  beforeAll(done => {
    TestUtil.beforeAll(createCustomers)
    .then(done)
    .catch(err => done.fail(err));
  });

  beforeAll(done => {
    TestUtil.beforeAll(createCustomers)
    .then(() => TestUtil.tryRetryPromise(TestUtil.tryToCreateUsers, 10, 10000).then(done, done.fail));
  });

  it('Create', done => {

    const project = {
      'name':'projekti esimerkki',
      'customer':applicantCustomersWithContactsCreated,
      'contact':applicantCustomersWithContactsCreated.contacts[0],
      'customerReference':'asiakkaan viite esimerkki',
      'additionalInfo':'Lisätieto esimerkki',
      'identifier': 'KAIVURI1'
    };


    let options = TestUtil.getPostOptions('/api/projects', project);
    TestUtil.login('kasittelija')
      .then(token => TestUtil.addAuthorization(options, token))
      .then(() => rp(options))
      .then(done, done.fail);
  });

});
