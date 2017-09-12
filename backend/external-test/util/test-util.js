
const Swagger = require('swagger-client');

module.exports.assertEnv = function() {
  if (!process.env.SWAGGER_HOST || !process.env.SWAGGER_JSON_URL) {
    console.error('Test expects SWAGGER_HOST AND SWAGGER_JSON_URL environment variables to be defined!');
    console.error('For example:');
    console.error('SWAGGER_HOST=localhost:9040 SWAGGER_JSON_URL=http://localhost:9040/api-docs/swagger.json npm test');
    console.error('You may also use SWAGGER_BASE_PATH to define beginning of the interface path such as http://host/base_path')
    process.exit(255);
  }
}

module.exports.swaggerClient = function swaggerClient() {
  return (new Swagger({
    url: process.env.SWAGGER_JSON_URL,
    usePromise: true,
    authorizations: {
      // TODO: replace with something else than this hard coded pre-generated JWT. Perhaps another environment variable or secret key, which can be used to generate the secret?
      api_key: 'Bearer eyJhbGciOiJIUzUxMiJ9.eyJleHAiOjE4OTM0NTYwMDAsInN1YiI6ImV4dGVybmFsX3Rlc3R1c2VyX2ludGVybmFsIiwicHVibGljQWxsdVJvbGVzIjpbIlJPTEVfSU5URVJOQUwiXX0.pTeql_K1W980UrVyEHYMzLcfqAxtId44Twla7l1XS1gtjWJ8gRViCqzP9JqeoK2iMsgBc94V7j_li75zdCO5CA'
    }
  })).then(client => prepareClient(client));
}

function prepareClient(client) {
  client.spec.host = process.env.SWAGGER_HOST;
  client.spec.basePath = process.env.SWAGGER_BASE_PATH;
  return client;
}


