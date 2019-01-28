import random
import requests
from sets import Set;

restrictedLayers = Set([
'helsinki_maanomistus_vuokrausalueet_yhdistelma',
'helsinki_maanomistus_vuokrausalueet',
'helsinki_maanomistus_sisainen',
'helsinki_maanalaiset_tilat',
'helsinki_maanalaiset_tilat_alueet',
'helsinki_maalampokaivot',
'helsinki_johtokartta_sahko',
'helsinki_johtokartta_tietoliikenne',
'helsinki_johtokartta_kaukolampo',
'helsinki_johtokartta_kaasu',
'helsinki_johtokartta_vesijohto',
'helsinki_johtokartta_viemari',
'helsinki_johtokartta_yhdistelma'])

class TokenAuthFilter(object):
    def __init__(self, app):
        self.app = app

    def __call__(self, environ, start_response):
        environ['mapproxy.authorize'] = self.authorize
        return self.app(environ, start_response)

    def authorize(self, service, layers=[], environ=None, **kw):
        restricted = False
        auth_layers = {}
        for layer in layers:
            if layer in restrictedLayers:
                restricted = True

        if not restricted:
            return {'authorized': 'full'}
        else:
            if environ.has_key('HTTP_AUTHORIZATION'):
              headers = {'Authorization': environ['HTTP_AUTHORIZATION']}
              # call backend to validate the given token
              r = requests.get('http://10.1.2.157:9000/users/isauthenticated', headers=headers)
              if r.status_code == 200:
                return {'authorized': 'full'}
              else:
                return {'authorized': 'none'}
            else:
              return {'authorized': 'unauthenticated'}