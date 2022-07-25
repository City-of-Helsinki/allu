import requests

restrictedLayers = set([
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


def authorize(service, layers=[], environ=None, **kw):
    restricted = False
    for layer in layers:
        if layer in restrictedLayers:
            restricted = True

    if not restricted:
        return {'authorized': 'full'}
    else:
        if 'HTTP_AUTHORIZATION' in environ:
            headers = {'Authorization': environ['HTTP_AUTHORIZATION']}
            # call backend to validate the given token
            r = requests.get('{{ proxypass_api_target }}users/isauthenticated', headers=headers)
            if r.status_code == 200:
                return {'authorized': 'full'}
            else:
                return {'authorized': 'none'}
        else:
            return {'authorized': 'unauthenticated'}


class TokenAuthFilter(object):
    def __init__(self, app):
        self.app = app

    def __call__(self, environ, start_response):
        environ['mapproxy.authorize'] = authorize
        return self.app(environ, start_response)
