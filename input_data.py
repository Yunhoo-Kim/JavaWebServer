import urllib3
import time
import urllib
import json
from pygrok import Grok

def inputa():
    req_pattern = "%{WORD:method} %{URIPATHPARAM:url} %{WORD:version}"
    grok = Grok(req_pattern)
    f_n = 'access.log-2017-10-17-1508216401.log'
    f = open(f_n, "r")
    http = urllib3.PoolManager()
    while True:
        line = f.readline()
        if not line: break
        try:
            _l = json.loads(line)
        except:
            continue
        _l.update(_l.pop('fields'))
        _l['time'] = _l.pop('time')
        req_t = _l.pop("request")
        remote_addr = _l.get("remote_addr")

        #if remote_addr == '-':
        #    continue
        #response = reader.city(remote_addr)
        #_l['coordinates'] = {
        #    "lon" : response.location.longitude,
        #    "lat" : response.location.latitude
        #}
        #_l['geoip'] = {
        #    "country_name" : response.country.name,
        #    "region_name" : response.city.name
        #}
        _l.update(grok.match(req_t))
        #_l["@timestamp"] = datetime.now().isoformat()
        #_l["url"] = _l.get("url").replace("/app","")
        #if _l.get("url").startswith("/app/search/all/"):
        #    _l["url"] = urllib.parse.unquote(_l.get("url"))
        body = json.dumps(_l)
        print(_l)
        r = http.request('post','127.0.0.1:8885/master/data/input/',headers={"Content-Type" : "application/json"},body=json.dumps(_l))


if __name__=="__main__":
    inputa()
