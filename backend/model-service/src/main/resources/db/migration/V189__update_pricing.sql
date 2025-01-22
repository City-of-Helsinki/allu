begin;

-- Lisää validity-kenttä pricing-tauluun
alter table allu.pricing
  add column validity daterange;

-- Päivitä validity-kentän päättymispäiväksi 28.2.2025 vanhoille kaivuilmoitusten ja aluevuokrausten hinnastoille
update allu.pricing
set validity = '(, 2025-02-28)'
where application_type in ('EXCAVATION_ANNOUNCEMENT', 'AREA_RENTAL')
  and not (
  (application_type = 'EXCAVATION_ANNOUNCEMENT' and key = 'HANDLING_FEE_SELF_SUPERVISION')
      or
  (application_type = 'AREA_RENTAL' and key in ('UNDERPASS_DICOUNT_PERCENTAGE', 'AREA_UNIT_M2'))
  );

update allu.pricing
set validity = daterange(null, null, '[]')
where
    (application_type = 'SHORT_TERM_RENTAL')
   or
    (application_type = 'AREA_RENTAL' and key in ('UNDERPASS_DICOUNT_PERCENTAGE', 'AREA_UNIT_M2'))
   or
    (application_type = 'EXCAVATION_ANNOUNCEMENT' and key = 'HANDLING_FEE_SELF_SUPERVISION');


-- Lisää uudet kaivuilmoitusten ja aluevuokrausten hinnastotiedot
insert into allu.pricing (key, payment_class, value, application_type, validity)
values
  -- < 60 m²
  ('LESS_THAN_60M2', '1', 10000, 'EXCAVATION_ANNOUNCEMENT', daterange('2025-03-01', null, '[]')),
  ('LESS_THAN_60M2', '2', 7500, 'EXCAVATION_ANNOUNCEMENT', daterange('2025-03-01', null, '[]')),
  ('LESS_THAN_60M2', '3', 5000, 'EXCAVATION_ANNOUNCEMENT', daterange('2025-03-01', null, '[]')),
  ('LESS_THAN_60M2', '4', 2500, 'EXCAVATION_ANNOUNCEMENT', daterange('2025-03-01', null, '[]')),
  ('LESS_THAN_60M2', '5', 1300, 'EXCAVATION_ANNOUNCEMENT', daterange('2025-03-01', null, '[]')),

  -- 60–120 m²
  ('FROM_60_TO_120M2', '1', 13000, 'EXCAVATION_ANNOUNCEMENT', daterange('2025-03-01', null, '[]')),
  ('FROM_60_TO_120M2', '2', 9800, 'EXCAVATION_ANNOUNCEMENT', daterange('2025-03-01', null, '[]')),
  ('FROM_60_TO_120M2', '3', 6500, 'EXCAVATION_ANNOUNCEMENT', daterange('2025-03-01', null, '[]')),
  ('FROM_60_TO_120M2', '4', 3300, 'EXCAVATION_ANNOUNCEMENT', daterange('2025-03-01', null, '[]')),
  ('FROM_60_TO_120M2', '5', 1600, 'EXCAVATION_ANNOUNCEMENT', daterange('2025-03-01', null, '[]')),

  -- 121–250 m²
  ('FROM_121_TO_250M2', '1', 16900, 'EXCAVATION_ANNOUNCEMENT', daterange('2025-03-01', null, '[]')),
  ('FROM_121_TO_250M2', '2', 12700, 'EXCAVATION_ANNOUNCEMENT', daterange('2025-03-01', null, '[]')),
  ('FROM_121_TO_250M2', '3', 8500, 'EXCAVATION_ANNOUNCEMENT', daterange('2025-03-01', null, '[]')),
  ('FROM_121_TO_250M2', '4', 4200, 'EXCAVATION_ANNOUNCEMENT', daterange('2025-03-01', null, '[]')),
  ('FROM_121_TO_250M2', '5', 2100, 'EXCAVATION_ANNOUNCEMENT', daterange('2025-03-01', null, '[]')),

  -- 251–500 m²
  ('FROM_251_TO_500M2', '1', 22000, 'EXCAVATION_ANNOUNCEMENT', daterange('2025-03-01', null, '[]')),
  ('FROM_251_TO_500M2', '2', 16500, 'EXCAVATION_ANNOUNCEMENT', daterange('2025-03-01', null, '[]')),
  ('FROM_251_TO_500M2', '3', 11000, 'EXCAVATION_ANNOUNCEMENT', daterange('2025-03-01', null, '[]')),
  ('FROM_251_TO_500M2', '4', 5500, 'EXCAVATION_ANNOUNCEMENT', daterange('2025-03-01', null, '[]')),
  ('FROM_251_TO_500M2', '5', 2700, 'EXCAVATION_ANNOUNCEMENT', daterange('2025-03-01', null, '[]')),

  -- 501–1000 m²
  ('FROM_501_TO_1000M2', '1', 28600, 'EXCAVATION_ANNOUNCEMENT', daterange('2025-03-01', null, '[]')),
  ('FROM_501_TO_1000M2', '2', 21400, 'EXCAVATION_ANNOUNCEMENT', daterange('2025-03-01', null, '[]')),
  ('FROM_501_TO_1000M2', '3', 14300, 'EXCAVATION_ANNOUNCEMENT', daterange('2025-03-01', null, '[]')),
  ('FROM_501_TO_1000M2', '4', 7100, 'EXCAVATION_ANNOUNCEMENT', daterange('2025-03-01', null, '[]')),
  ('FROM_501_TO_1000M2', '5', 3600, 'EXCAVATION_ANNOUNCEMENT', daterange('2025-03-01', null, '[]')),

  -- > 1000 m²
  ('MORE_THAN_1000M2', '1', 37100, 'EXCAVATION_ANNOUNCEMENT', daterange('2025-03-01', null, '[]')),
  ('MORE_THAN_1000M2', '2', 27800, 'EXCAVATION_ANNOUNCEMENT', daterange('2025-03-01', null, '[]')),
  ('MORE_THAN_1000M2', '3', 18600, 'EXCAVATION_ANNOUNCEMENT', daterange('2025-03-01', null, '[]')),
  ('MORE_THAN_1000M2', '4', 9300, 'EXCAVATION_ANNOUNCEMENT', daterange('2025-03-01', null, '[]')),
  ('MORE_THAN_1000M2', '5', 4600, 'EXCAVATION_ANNOUNCEMENT', daterange('2025-03-01', null, '[]')),

  -- Kaivuilmoituksen käsittely- ja valvontamaksu
  ('HANDLING_FEE', null, 24000, 'EXCAVATION_ANNOUNCEMENT', daterange('2025-03-01', null, '[]')),

  -- Alkava 15 m²
  ('FROM_15M2', '1', 1200, 'AREA_RENTAL', daterange('2025-03-01', null, '[]')),
  ('FROM_15M2', '2', 1000, 'AREA_RENTAL', daterange('2025-03-01', null, '[]')),
  ('FROM_15M2', '3', 700, 'AREA_RENTAL', daterange('2025-03-01', null, '[]')),
  ('FROM_15M2', '4', 400, 'AREA_RENTAL', daterange('2025-03-01', null, '[]')),
  ('FROM_15M2', '5', 200, 'AREA_RENTAL', daterange('2025-03-01', null, '[]')),

  -- Aluevuokrauksen käsittely- ja valvontamaksu
  ('MINOR_DISTURBANCE_HANDLING_FEE', null, 8000, 'AREA_RENTAL', daterange('2025-03-01', null, '[]')),
  ('MAJOR_DISTURBANCE_HANDLING_FEE', null, 24000, 'AREA_RENTAL', daterange('2025-03-01', null, '[]'));

commit;
