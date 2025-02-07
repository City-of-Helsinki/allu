const rp = require('request-promise');

module.exports.beforeAll = beforeAll;
module.exports.login = login;
module.exports.tryToCreateUsers = tryToCreateUsers;
module.exports.checkSearchService = checkSearchService;
module.exports.addAuthorization = addAuthorization;
module.exports.getPostOptions = getPostOptions;
module.exports.tryRetryPromise = tryRetryPromise;
module.exports.getStartDateString = getStartDateString;
module.exports.getEndDateString = getEndDateString;

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

function beforeAll(beforeAllFn) {
  return tryRetryPromise(tryToCreateUsers, 10, 10000)
    .then(() => tryRetryPromise(checkSearchService, 10, 10000))
    .then(beforeAllFn);
}

function checkSearchService() {
  let customerSearch = getPostOptions('/api/customers/search', {queryParameters: [{fieldName: "active", fieldValue: "true"}]});
  return login('kasittelija')
    .then(token => request(customerSearch, token));
}

function tryToCreateUsers() {
  let kasittelijaUserName = 'kasittelija';
  let kasittelija = {
    'userName': kasittelijaUserName,
    'realName':'Käsittelijä Käyttäjä',
    'emailAddress':'kasittelija@helsinki.fi',
    'title':'Käsittelijä',
    'allowedApplicationTypes':[
      'EXCAVATION_ANNOUNCEMENT',
      'AREA_RENTAL',
      'TEMPORARY_TRAFFIC_ARRANGEMENTS',
      'CABLE_REPORT',
      'PLACEMENT_CONTRACT',
      'EVENT',
      'SHORT_TERM_RENTAL',
      'NOTE'
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
    .then(() => request(findUserOptions, loginToken))
    .catch(() => request(createUserOptions, loginToken));
}

function login(username) {
  let options = getPostOptions('/api/auth/login', {'userName': username});
  return rp(options);
}

/**
 * Tries to repeatedly execute promise created by the factory function.
 *
 * @param fn  Function that generates the Promise which may have to be retried.
 * @param times how many times are tried before giving up
 * @param delay how much delay between each try
 * @return {Promise.<T>}
 */
function tryRetryPromise(fn, times, delay) {
  let counter = 1;
  return new Promise(function(resolve, reject){
    let error;
    const attempt = function() {
      if (counter > times) {
        console.error('Waited server too long, giving up after ' + counter + ' retries');
        reject(error.message);
        console.error(error)
      } else {
        fn().then(resolve)
          .catch((e) => {
            error = e;
            console.log(e.message);
            console.log('waiting for retry...', counter);
            ++counter;
            setTimeout(() => {
              console.log('... retrying');
              attempt();
            }, delay);
          });
      }
    };
    attempt();
  });
};

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

function request(options, token) {
  addAuthorization(options, token);
  return rp(options);
}

/**
 * Returns ISO date string (such as 2017-01-01T22:00:00Z) of current date adjusted with the given day offset
 * at the begin of the day.
 * @param dayOffset   Day offset where positive is future and negative is past.
 */
function getStartDateString(dayOffset) {
  let date = getDate(dayOffset);
  date.setHours(0, 0, 0, 0);
  return date.toISOString()
}

/**
 * Returns ISO date string (such as 2017-01-01T22:00:00Z) of current date adjusted with the given day offset
 * at the end of the day.
 * @param dayOffset   Day offset where positive is future and negative is past.
 */
function getEndDateString(dayOffset) {
  let date = getDate(dayOffset);
  date.setHours(23, 59, 59, 999);
  return date.toISOString()
}

function getDate(dayOffset) {
  let milliOffset = dayOffset * 24 * 60 * 60 * 1000;
  return new Date(Date.now() + milliOffset);
}
