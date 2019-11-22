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
    const extUserRkj = {
      'id': null,
      'username': 'ext-user-rkj',
      'name': 'RKJ Ext User',
      'password': 'Alk354_3#085pU_24YpA',
      'active': 'true',
      'expirationTime': '2022-07-07T00:00:00.000Z',
      'assignedRoles': ['ROLE_TRUSTED_PARTNER']
    }
    const extUserHsy = {
      'id': null,
      'username': 'ext-user-hsy',
      'name': 'HSY Ext User',
      'password': 'as#33_24DFgh_AEfott3',
      'active': 'true',
      'expirationTime': '2022-07-07T00:00:00.000Z',
      'assignedRoles': ['ROLE_TRUSTED_PARTNER']
    }
    const extUserInternal  = {
      'id': null,
      'username': 'ext-user-internal',
      'name': 'Internal Ext User',
      'password': 'asdk#AER5320_23GtuOi',
      'active': 'true',
      'expirationTime': '2022-07-07T00:00:00.000Z',
      'assignedRoles': ['ROLE_INTERNAL']
    }

    let optionsTestUser = TestUtil.getPostOptions('/api/externalusers', extUser);
    let optionsTestUserRkj = TestUtil.getPostOptions('/api/externalusers', extUserRkj);
    let optionsTestUserHsy = TestUtil.getPostOptions('/api/externalusers', extUserHsy);
    let optionsTestUserInternal = TestUtil.getPostOptions('/api/externalusers', extUserInternal);
    TestUtil.login('admin')
      .then(token => {
        TestUtil.addAuthorization(optionsTestUser, token);
        TestUtil.addAuthorization(optionsTestUserRkj, token);
        TestUtil.addAuthorization(optionsTestUserHsy, token);
        TestUtil.addAuthorization(optionsTestUserInternal, token);
      })
      .then(() => rp(optionsTestUser))
      .then(() => rp(optionsTestUserRkj))
      .then(() => rp(optionsTestUserHsy))
      .then(() => rp(optionsTestUserInternal))
      .then(done, done.fail);
  });

});
