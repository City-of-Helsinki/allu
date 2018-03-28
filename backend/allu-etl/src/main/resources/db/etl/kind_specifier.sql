WITH tarkenteet (en, fi) AS (
    VALUES
        ('ASPHALT', 'Asfaltointityö'),
        ('INDUCTION_LOOP', 'Induktiosilmukka'),
        ('COVER_STRUCTURE', 'Kansisto'),
        ('STREET_OR_PARK', 'Katu tai puisto'),
        ('PAVEMENT', 'Kiveystyö'),
        ('TRAFFIC_LIGHT', 'Liikennevalo'),
        ('COMMERCIAL_DEVICE', 'Mainoslaite'),
        ('TRAFFIC_STOP', 'Pysäkkikatos'),
        ('BRIDGE', 'Silta'),
        ('OUTDOOR_LIGHTING', 'Ulkovalaistus'),
        ('STORM_DRAIN', 'Hulevesi'),
        ('WELL', 'Kaivo'),
        ('UNDERGROUND_DRAIN', 'Salaoja'),
        ('WATER_PIPE', 'Vesijohto'),
        ('DRAIN', 'Viemäri'),
        ('DISTRIBUTION_CABINET', 'Jakokaappi'),
        ('ELECTRICITY_CABLE', 'Kaapeli'),
        ('ELECTRICITY_WELL', 'Kaivo'),
        ('DISTRIBUTION_CABINET_OR_PILAR', 'Jakokaappi/-pilari'),
        ('DATA_CABLE', 'Kaapeli'),
        ('DATA_WELL', 'Kaivo'),
        ('STREET_HEATING', 'Katulämmitys'),
        ('DISTRICT_HEATING', 'Kaukolämpö'),
        ('DISTRICT_COOLING', 'Kaukokylmä'),
        ('GROUND_ROCK_ANCHOR', 'Maa- / kallioankkuri'),
        ('UNDERGROUND_STRUCTURE', 'Maanalainen rakenne'),
        ('UNDERGROUND_SPACE', 'Maanalainen tila'),
        ('BASE_STRUCTURES', 'Perusrakenteet'),
        ('DRILL_PILE', 'Porapaalu'),
        ('CONSTRUCTION_EQUIPMENT', 'Rakennuksen laite/varuste'),
        ('CONSTRUCTION_PART', 'Rakennuksen osa'),
        ('GROUND_FROST_INSULATION', 'Routaeriste'),
        ('SMOKE_HATCH_OR_PIPE',  'Savunpoistoluukku/-putki, IV-putki'),
        ('STOP_OR_TRANSITION_SLAB', 'Sulku-/siirtymälaatta'),
        ('SUPPORTING_WALL_OR_PILE',  'Tukiseinä/-paalu'),
        ('FENCE_OR_WALL', 'Aita, muuri, penger'),
        ('DRIVEWAY', 'Kulkutie'),
        ('STAIRS_RAMP',  'Portaat, luiska tms.'),
        ('SUPPORTING_WALL_OR_BANK', 'Tukimuuri/-penger, lujitemaamuuri'),
        ('DRILLING', 'Kairaus'),
        ('TEST_HOLE', 'Koekuoppa'),
        ('GROUND_WATER_PIPE', 'Pohjavesiputki'),
        ('ABSORBING_SEWAGE_SYSTEM', 'Imujätejärjestelmä'),
        ('GAS_PIPE', 'Kaasujohto'),
        ('OTHER', 'Muu')
)
INSERT INTO allureport.hakemuslaji_tarkenne
SELECT
    s.id AS id,
    s.kind_id AS hakemuslaji_id,
    t.fi AS tarkenne
FROM allu_operative.kind_specifier s
LEFT JOIN tarkenteet t ON s.specifier = t.en
ON CONFLICT (id) DO UPDATE SET
    hakemuslaji_id = EXCLUDED.hakemuslaji_id,
    tarkenne = EXCLUDED.tarkenne
;

