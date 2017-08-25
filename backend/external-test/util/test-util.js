
const Swagger = require('swagger-client');

module.exports.assertEnv = function() {
  if (!process.env.SWAGGER_HOST || !process.env.SWAGGER_JSON_URL) {
    console.error('Test expects SWAGGER_HOST AND SWAGGER_JSON_URL envinronment variables to be defined!');
    console.error('For example:');
    console.error('SWAGGER_HOST=localhost:9040 SWAGGER_JSON_URL=http://localhost:9040/api-docs/swagger.json npm test');
    process.exit(255);
  }
}

module.exports.swaggerClient = function swaggerClient() {
  return (new Swagger({
    url: process.env.SWAGGER_JSON_URL,
    usePromise: true,
    authorizations: {
      // TODO: replace with something else than this hard coded pre-generated JWT. Perhaps another environment variable or secret key, which can be used to generate the secret?
      api_key: 'Bearer eyJhbGciOiJIUzUxMiJ9.eyJleHAiOjE4MTg5MzQyOTYsInN1YiI6InRlc3RzdWJqZWN0IiwicHVibGljQWxsdVJvbGVzIjpbIlJPTEVfSU5URVJOQUwiXX0.2bapNVFXNkgJ16mSo_ur9vfHXw5eiraYDFoPOxbx83McKP7Y0e7wAzbV-BFUlnjTFdESjxYaMGNI4YEmYlOvPg'
    }
  })).then(client => prepareClient(client));
}

function prepareClient(client) {
  client.spec.host = process.env.SWAGGER_HOST;
  client.spec.basePath = null;
  return client;
}


