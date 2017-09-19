
const rp = require('request-promise');

module.exports.login = login;
module.exports.tryToCreateUsers = tryToCreateUsers;
module.exports.addAuthorization = addAuthorization;
module.exports.getPostOptions = getPostOptions;

module.exports.assertEnv = function() {
  if (!process.env.TEST_TARGET) {
    console.error('Test expects TEST_TARGET to be defined!');
    console.error('For example:');
    console.error('TEST_TARGET=http://localhost:9000 npm test');
    process.exit(255);
  }
}

function tryToCreateUsers() {
  let kasittelija = {
    'userName':'kasittelija',
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

  let options = getPostOptions('/api/users', kasittelija);

  return login('admin')
    .then((token) => { addAuthorization(options, token); return rp(options); })
    .catch(() => console.log('user creation failed, ignoring'));
}

function login(username) {
  let options = getPostOptions('/api/auth/login', {'userName': username});
  return rp(options);
}

function getPostOptions(path, body) {
  return {
    'method': 'POST',
    'uri': process.env.TEST_TARGET + path,
    'body': body,
    'json': true // Automatically stringifies the body to JSON
  };
}

function addAuthorization(options, token) {
  options.headers = { 'Authorization': 'Bearer ' + token };
}
