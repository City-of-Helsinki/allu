const TestUtil = require('../util/test-util');
const rp = require('request-promise');

TestUtil.assertEnv();

describe('Project', () => {


  beforeAll(done => {
    TestUtil.tryRetryPromise(1, TestUtil.tryToCreateUsers).then(done, done.fail);
  });

  it('Create', done => {

    const project = {
      'name':'projekti esimerkki',
      'ownerName':'omistaja esimerkki',
      'contactName':'kontakti esimerkki',
      'email':'projekti-esimerkki@esimerkki.fi',
      'phone':'010 1234567',
      'customerReference':'asiakkaan viite esimerkki',
      'additionalInfo':'Lisätieto esimerkki'
    };


    let options = TestUtil.getPostOptions('/api/projects', project);
    TestUtil.login('kasittelija')
      .then(token => TestUtil.addAuthorization(options, token))
      .then(() => rp(options))
      .then(done, done.fail);
  });

});
