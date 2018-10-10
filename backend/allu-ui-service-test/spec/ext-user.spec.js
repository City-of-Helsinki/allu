const TestUtil = require('../util/test-util');
const rp = require('request-promise');

TestUtil.assertEnv();

describe('Create external user', () => {
  it('Create', done => {
    const extUser1 = {
      'id': null,
      'username': 'ext-user',
      'name': 'Ext User',
      'password': '#alk#%355245lAAA_z24',
      'active': 'true',
      'expirationTime': '2022-07-07T00:00:00.000Z',
      'assignedRoles': ['ROLE_TRUSTED_PARTNER']
    }
    const extUser2 = {
      'id': null,
      'username': 'ext-user-rkj',
      'name': 'RKJ Ext User',
      'password': 'Alk354_3#085pU_24YpA',
      'active': 'true',
      'expirationTime': '2022-07-07T00:00:00.000Z',
      'assignedRoles': ['ROLE_TRUSTED_PARTNER']
    }

    let optionsTestUser1 = TestUtil.getPostOptions('/api/externalusers', extUser1);
    let optionsTestUser2 = TestUtil.getPostOptions('/api/externalusers', extUser2);
    TestUtil.login('admin')
      .then(token => {
        TestUtil.addAuthorization(optionsTestUser1, token);
        TestUtil.addAuthorization(optionsTestUser2, token);
      })
      .then(() => rp(optionsTestUser1))
      .then(() => rp(optionsTestUser2))
      .then(done, done.fail);
  });

});
