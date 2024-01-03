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
      'expirationTime': '2100-12-31T00:00:00.000Z',
      'assignedRoles': ['ROLE_TRUSTED_PARTNER']
    }
    const extUserRkj = {
      'id': null,
      'username': 'ext-user-rkj',
      'name': 'RKJ Ext User',
      'password': 'Alk354_3#085pU_24YpA',
      'active': 'true',
      'expirationTime': '2100-12-31T00:00:00.000Z',
      'assignedRoles': ['ROLE_TRUSTED_PARTNER']
    }
    const extUserHsy = {
      'id': null,
      'username': 'ext-user-hsy',
      'name': 'HSY Ext User',
      'password': 'as#33_24DFgh_AEfott3',
      'active': 'true',
      'expirationTime': '2100-12-31T00:00:00.000Z',
      'assignedRoles': ['ROLE_TRUSTED_PARTNER']
    }
    const extUserInternal  = {
      'id': null,
      'username': 'ext-user-internal',
      'name': 'Internal Ext User',
      'password': 'asdk#AER5320_23GtuOi',
      'active': 'true',
      'expirationTime': '2100-12-31T00:00:00.000Z',
      'assignedRoles': ['ROLE_INTERNAL']
    }
    const extUserIbmE  = {
      'id': null,
      'username': 'ext-user-ibm_e-asiointi',
      'name': 'IBM e-asiointi',
      'password': 'Edk#60$YpP74qrPKvXAe',
      'active': 'true',
      'expirationTime': '2100-12-31T00:00:00.000Z',
      'assignedRoles': ['ROLE_TRUSTED_PARTNER']
    }
    const extUserHelen  = {
      'id': null,
      'username': 'ext-user-helen_oy',
      'name': 'Helen Oy',
      'password': 'v859Bw9$MAugtwsqV8$u',
      'active': 'true',
      'expirationTime': '2100-12-31T00:00:00.000Z',
      'assignedRoles': ['ROLE_TRUSTED_PARTNER']
    }
    const extUserHaitaton = {
      'id': null,
      'username': 'ext-user-haitaton',
      'name': 'Haitaton Ext User',
      'password': 'h5Q492A*iJVSpUufSFO6',
      'active': 'true',
      'expirationTime': '2100-12-31T00:00:00.000Z',
      'assignedRoles': ['ROLE_TRUSTED_PARTNER']
    }
    const extUserETapahtuma = {
      'id': null,
      'username': 'ext-user-etapahtuma',
      'name': 'eTapahtuma Ext User',
      'password': 'Ks2^2bwCUyAQ!otbhykF',
      'active': 'true',
      'expirationTime': '2100-12-31T00:00:00.000Z',
      'assignedRoles': ['ROLE_TRUSTED_PARTNER']
    }

    let optionsTestUser = TestUtil.getPostOptions('/api/externalusers', extUser);
    let optionsTestUserRkj = TestUtil.getPostOptions('/api/externalusers', extUserRkj);
    let optionsTestUserHsy = TestUtil.getPostOptions('/api/externalusers', extUserHsy);
    let optionsTestUserInternal = TestUtil.getPostOptions('/api/externalusers', extUserInternal);
    let optionsTestUserIbmE = TestUtil.getPostOptions('/api/externalusers', extUserIbmE);
    let optionsTestUserHelen = TestUtil.getPostOptions('/api/externalusers', extUserHelen);
    let optionsTestUserHaitaton = TestUtil.getPostOptions('/api/externalusers', extUserHaitaton);
    let optionsTestUserETapahtuma = TestUtil.getPostOptions('/api/externalusers', extUserETapahtuma);
    TestUtil.login('allute')
      .then(token => {
        TestUtil.addAuthorization(optionsTestUser, token);
        TestUtil.addAuthorization(optionsTestUserRkj, token);
        TestUtil.addAuthorization(optionsTestUserHsy, token);
        TestUtil.addAuthorization(optionsTestUserInternal, token);
        TestUtil.addAuthorization(optionsTestUserIbmE, token);
        TestUtil.addAuthorization(optionsTestUserHelen, token);
        TestUtil.addAuthorization(optionsTestUserHaitaton, token);
        TestUtil.addAuthorization(optionsTestUserETapahtuma, token);
      })
      .then(() => rp(optionsTestUser))
      .then(() => rp(optionsTestUserRkj))
      .then(() => rp(optionsTestUserHsy))
      .then(() => rp(optionsTestUserInternal))
      .then(() => rp(optionsTestUserIbmE))
      .then(() => rp(optionsTestUserHelen))
      .then(() => rp(optionsTestUserHaitaton))
      .then(() => rp(optionsTestUserETapahtuma))
      .then(done, done.fail);
  });

});
