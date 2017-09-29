
const rp = require('request-promise');

module.exports.login = login;
module.exports.tryToCreateUsers = tryToCreateUsers;
module.exports.addAuthorization = addAuthorization;
module.exports.getPostOptions = getPostOptions;
module.exports.tryRetryPromise = tryRetryPromise;
module.exports.getISODateString = getISODateString;

module.exports.assertEnv = function() {
  // If tested server is not available for some reason, tests wait awhile. To support waiting, Jasmine async timeout has to be increased
  jasmine.DEFAULT_TIMEOUT_INTERVAL = 122000;
  if (!process.env.TEST_TARGET) {
    console.error('Test expects TEST_TARGET to be defined!');
    console.error('For example:');
    console.error('TEST_TARGET=http://localhost:3000 npm test');
    process.exit(255);
  }
}

function tryToCreateUsers() {
  let kasittelijaUserName = 'kasittelija';
  let kasittelija = {
    'userName': kasittelijaUserName,
    'realName':'Käsittelijä Käyttäjä',
    'emailAddress':'kasittelija@helsinki.fi',
    'title':'Käsittelijä',
    'allowedApplicationTypes':[
      'EVENT'
    ],
    'assignedRoles':[
      'ROLE_VIEW',
      'ROLE_CREATE_APPLICATION',
      'ROLE_PROCESS_APPLICATION'
    ],
    'cityDistrictIds':[
      1,2,3
    ],
    'active':true
  };

  let findUserOptions = getGetOptions('/api/users/userName/' + kasittelijaUserName);
  let createUserOptions = getPostOptions('/api/users', kasittelija);

  let loginToken;
  return login('admin')
    .then(token => loginToken = token)
    .then(() => { addAuthorization(findUserOptions, loginToken); return rp(findUserOptions); })
    .then(() => { /* do nothing, user exists */ }, () => { addAuthorization(createUserOptions, loginToken); return rp(createUserOptions); });
}

function login(username) {
  let options = getPostOptions('/api/auth/login', {'userName': username});
  return rp(options);
}

/**
 * Tries to repeatedly execute promise created by the factory function.
 *
 * @param counter         Counter
 * @param promiseFactory  Function that generates the Promise which may have to be retried.
 * @return {Promise.<T>}
 */
function tryRetryPromise(counter, promiseFactory) {
  const maxCount = 12;
  if (counter > maxCount) {
    console.error('Waited server too long, giving up after ' + maxCount + ' retries');
    return Promise.reject('too many retries');
  } else {
    return promiseFactory().catch((error) => {
      console.log('waiting for retry...', counter);
      return new Promise((resolve) => setTimeout(resolve, 10000))
        .then(() => console.log('... wait done'))
        .then(() => tryRetryPromise(++counter, promiseFactory));
    });
  }
}

function getPostOptions(path, body) {
  return {
    'method': 'POST',
    'uri': process.env.TEST_TARGET + path,
    'body': body,
    'json': true // Automatically stringifies the body to JSON
  };
}

function getGetOptions(path, body) {
  return {
    'method': 'GET',
    'uri': process.env.TEST_TARGET + path,
    'body': body,
    'json': true // Automatically stringifies the body to JSON
  };
}

function addAuthorization(options, token) {
  options.headers = { 'Authorization': 'Bearer ' + token };
}

/**
 * Returns ISO date string (such as 2017-01-01T22:00:00Z) of current date adjusted with the given day offset.
 *
 * @param dayOffset   Day offset where positive is future and negative is past.
 */
function getISODateString(dayOffset) {
  let milliOffset = dayOffset * 24 * 60 * 60 * 1000;
  return new Date(Date.now() + milliOffset).toISOString();
}
