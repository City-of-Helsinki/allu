export enum ApplicationSpecifier {
  // HKR:n katu- ja vihertyöt
  CITY_STREET_AND_GREEN_CONSTRUCTION, // Kadun tai puiston rakentaminen
  CITY_STREET_AND_GREEN_MAINTENANCE, // Kadun tai puiston kunnossapito
  ASPHALT, // Asfaltointityö
  PAVEMENT, // Kiveystyö
  BRIDGE, // Siltatyö
  // HKR, HSY Vesijohto ja viemärityöt
  STORM_DRAIN_CONSTRUCTION, // Hulevesiviemärin rakentaminen
  STORM_DRAIN_MAINTENANCE, // Hulevesiviemärin kunnossapito
  WATER_PIPE_CONSTRUCTION, // Vesijohdon rakentaminen
  WATER_PIPE_MAINTENANCE, // Vesijohdon kunnossapito
  WATER_PIPE_LEAK_REPAIR, // Vesijohdon vuotokorjaus (äkillinen)
  DRAIN_CONSTRUCTION, // Viemärin rakentaminen
  DRAIN_MAINTENANCE, // Viemärin kunnossapito
  DRAIN_LEAK_REPAIR, // Viemärin vuotokorjaus (äkillinen)
  // HKL:n työt
  HKL_STOP_WORK, // Pysäkkityö
  HKL_OTHER_WORK, // Muu työ
  // Sähkökaapelityöt
  CABLE_CONSTRUCTION, // Kaapelityö rakentaminen
  CABLE_MAINTENANCE, // Kaapelityö kunnossapito
  CABLE_REPAIR, // Kaapelityö vikakorjaus (äkillinen)
  OUTDOOR_LIGHTING, // Ulkovalaistustyö
  TRAFFIC_LIGHTS, // Liikennevalotyö
  // Kaukolämpötyöt
  DISTRICT_HEATING_CONSTRUCTION, // Verkon rakentaminen
  DISTRICT_HEATING_MAINTENANCE, // Verkon kunnossapito
  DISTRICT_HEATING_REPAIR, // Verkon vikakorjaus (äkillinen)
  // Kaukokylmätyöt
  DISTRICT_COOLING_CONSTRUCTION, // Verkon rakentaminen
  DISTRICT_COOLING_MAINTENANCE, // Verkon kunnossapito
  DISTRICT_COOLING_REPAIR, // Verkon vikakorjaus (äkillinen)
  // Tiedonsiirron kaapelityöt
  TELECOMMUNICATION_CONSTRUCTION, // Kaapelityö (rakentaminen)
  TELECOMMUNICATION_MAINTENANCE, // Kaapelityö (kunnossapito)
  TELECOMMUNICATION_REPAIR, // Vikakorjaus (äkillinen)
  // Kaasujohdot
  GAS_CONSTRUCTION, // Verkon rakentaminen
  GAS_MAINTENANCE, // Verkon kunnossapito
  GAS_REPAIR, // Vikakorjaus (äkillinen)
  // Ulkomainospilarit ja pysäkkikatokset
  AD_STOPS, // Pysäkkikatokset
  AD_BILLBOARDS_AND_PILLARS, // Mainostaulut ja -pilarit
  // Kiinteistö-/tonttiliitokset
  PROPERTY_MERGER_WATER, // Vesi
  PROPERTY_MERGER_DRAIN, // Viemäri
  PROPERTY_MERGER_DISTRICT_HEATING, // Kaukolämpö
  PROPERTY_MERGER_DISTRICT_COOLING, // Kaukokylmä
  PROPERTY_MERGER_ELECTRICITY, // Sähkö
  PROPERTY_MERGER_TELECOMMUNICATION, // Teleyhteydet
  PROPERTY_MERGER_ENTRANCE, // Sisäänajo
  PROPERTY_MERGER_GAS, // Kaasu
  PROPERTY_MERGER_STORM_DRAIN, // Hulevesi
  PROPERTY_MERGER_ABSORBING_SEWAGE_SYSTEM, // Imujätejärjestelmä
  // Maaperätutkimukset
  SOIL_INVESTIGATION_DRILLING, // Kairaukset
  SOIL_INVESTIGATION_PIPING, // Pohjavesiputket
  SOIL_INVESTIGATION_TEST_HOLES, // Koekuopat
  SOIL_INVESTIGATION_OTHER, // Muut
  // Yhteinen kunnallistekninen työmaa
  JOINT_MUNICIPAL_INFRASTRUCTURE_CONSTRUCTION, // Rakentaminen
  JOINT_MUNICIPAL_INFRASTRUCTURE_MAINTENANCE, // Kunnossapito
  // Imujätejärjestelmä
  ABSORBING_SEWAGE_SYSTEM_CONSTRUCTION, // Verkon rakentaminen
  ABSORBING_SEWAGE_SYSTEM_MAINTENANCE, // Verkon kunnossapito
  ABSORBING_SEWAGE_SYSTEM_REPAIR, // Verkon vikakorjaus (äkillinen)
  // Maanalainen rakentaminen
  UNDERGROUND_CONSTRUCTION_SPACE, // Maanalainen tila
  UNDERGROUND_CONSTRUCTION_STRUCTURE, // Maanalainen rakenne
  // Muut
  OTHER_CABLE_WORK_DEMOUNTABLE_PLATFORM, // Vaihtolava
  OTHER_CABLE_WORK_LIFT, // Nostotyö
  OTHER_CABLE_WORK_SNOW_CLEARING, // Lumenpudotus
  OTHER_CABLE_WORK_PUBLIC_OCCASION, // Yleisötilaisuus
  OTHER_CABLE_WORK_MAPPING, // Kuvaus
  OTHER_CABLE_WORK_RELOCATION, // Muutto
  OTHER_CABLE_WORK_ESTATE_REPAIR, // Kiinteistöremontti
  OTHER_CABLE_WORK_OTHER_TRAFFIC_ARRANGEMENTS, // Muu liikennejärjestelyä vaativa työ
}
