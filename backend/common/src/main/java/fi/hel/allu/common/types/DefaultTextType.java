package fi.hel.allu.common.types;

public enum DefaultTextType {
  // cable reports - johtoselvitykset
  TELECOMMUNICATION, // Tietoliikenne
  ELECTRICITY, // Sähkö
  WATER_AND_SEWAGE, // Vesi ja viemäri
  DISTRICT_HEATING_COOLING, // Kaukolämpö/jäähdytys
  GAS, // Kaasu
  UNDERGROUND_STRUCTURE, // Maanalainen rakenne/tila
  TRAMWAY, // Raitiotie
  STREET_HEATING, // Katulämmitys
  SEWAGE_PIPE, // Jäteputki
  GEOTHERMAL_WELL, // Maalämpökaivo
  GEOTECHNICAL_OBSERVATION_POST, // Geotekninen tarkkailupiste
  TERMS, // Ehdot
  TRAFFIC_ARRANGEMENT,
  NOT_BILLABLE, // Ei laskuteta peruste
  OTHER // Yleisesti/muut
}
