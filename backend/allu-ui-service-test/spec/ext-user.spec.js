const TestUtil = require('../util/test-util');
const rp = require('request-promise');

TestUtil.assertEnv();

describe('Create external user', () => {
  it('Create', done => {
    const extUser = {
      'id': null,
      'username': 'ext-user',
      'name': 'Ext User',
      'password': '#alk#%355245lAAA_z24',
      'active': 'true',
      'expirationTime': '2022-07-07T00:00:00.000Z',
      'assignedRoles': ['ROLE_TRUSTED_PARTNER']
    }
    let options = TestUtil.getPostOptions('/api/externalusers', extUser);
    TestUtil.login('admin')
      .then(token => TestUtil.addAuthorization(options, token))
      .then(() => rp(options))
      .then(done, done.fail);
  });

});
