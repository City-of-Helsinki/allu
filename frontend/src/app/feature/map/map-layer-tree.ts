import {applicationLayers} from '@feature/map/map-layer.service';

export const commonLayers = {
  'Karttasarja': null,
  'Kantakartta': null,
  'Ajantasa-asemakaava': null,
  'Kiinteistökartta': null,
  'Ortoilmakuva': null,
  'Winkin kartat': {
    'Tulevat katutyöt': null,
    'Aktiiviset katutyöt': null,
    'Tulevat vuokraukset': null,
    'Aktiiviset vuokraukset': null,
  },
  'Muut': {
    'Kaupunginosat': null
  }
};

export const restrictedLayers = {
  'Kaivot': {
    'Maalämpökaivot': null,
    'Porakaivot': null
  },
  'Kiinteistökartat': {
    'Maanomistus ja vuokraus yhdistelmä': null,
    'Maanomistus vuokrausalueet': null,
    'Maanomistus sisäinen vuokraus': null
  },
  'Maanalaiset tilat': {
    'Maanalaiset tilat reunaviivat': null,
    'Maanalaiset tilat alueet': null,
  },
  'Johtokartat': {
    'Imujätehuolto': null,
    'Sähkö': null,
    'Tietoliikenne': null,
    'Kaukolämpö': null,
    'Kaukojäähdytys': null,
    'Kaasu': null,
    'Vesijohto': null,
    'Viemari': null,
    'Yhdistelmäjohtokartta': null
  },
};

export const baseLayerTree = {
  'Karttatasot': commonLayers
};

export const restrictedBaseLayerTree = {
  'Karttatasot': {
    ...commonLayers,
    ...restrictedLayers
  }
};

export const applicationLayerTree = applicationLayers.reduce((tree, layer) => {
  tree[layer] = null;
  return tree;
}, {});

export function createLayerTree(withRestricted: boolean, withApplications: boolean) {
  const tree = withRestricted ? restrictedBaseLayerTree : baseLayerTree;
  return withApplications
    ? { ...tree, 'Hakemustyypit': applicationLayerTree }
    : { ...tree };
}
