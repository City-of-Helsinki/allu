import random
import requests

class TokenAuthFilter(object):
    def __init__(self, app):
        self.app = app

    def __call__(self, environ, start_response):
        if environ.has_key('HTTP_AUTHORIZATION'):
          headers = {'Authorization': environ['HTTP_AUTHORIZATION']}
          # call backend to validate the given token
          r = requests.get('{{ proxypass_api_target }}/users/isauthenticated', headers=headers)
          if r.status_code == 200:
            return self.app(environ, start_response)
          else:
            start_response('403 Forbidden',
              [('content-type', 'text/plain')])
            return ['Provided authorization token is invalid']
        else:
          start_response('401 Forbidden',
            [('content-type', 'text/plain')])
          return ['Authorization token required']
