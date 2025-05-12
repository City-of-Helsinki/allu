ALTER TABLE allu.pricing
  DROP CONSTRAINT pricing_application_type_key_payment_class_key;

-- Lisää validity-kenttä pricing-tauluun
ALTER TABLE allu.pricing
  ADD COLUMN validity daterange;

ALTER TABLE allu.pricing
  ADD CONSTRAINT pricing_application_type_key_payment_class_validity_key UNIQUE(application_type, key, payment_class, validity);

-- Päivitä validity-kentän päättymispäiväksi 28.2.2025 vanhoille kaivuilmoitusten ja aluevuokrausten hinnastoille
UPDATE allu.pricing
SET validity = daterange(NULL, '2025-02-28', '[]')
WHERE application_type IN ('EXCAVATION_ANNOUNCEMENT', 'AREA_RENTAL')
  AND NOT (
  (application_type = 'EXCAVATION_ANNOUNCEMENT' AND key = 'HANDLING_FEE_SELF_SUPERVISION')
      OR
  (application_type = 'AREA_RENTAL' AND key IN ('UNDERPASS_DICOUNT_PERCENTAGE', 'AREA_UNIT_M2'))
  );

UPDATE allu.pricing
SET validity = daterange(NULL, NULL, '[]')
WHERE
    (application_type = 'SHORT_TERM_RENTAL')
   OR
    (application_type = 'AREA_RENTAL' AND key IN ('UNDERPASS_DICOUNT_PERCENTAGE', 'AREA_UNIT_M2'))
   OR
    (application_type = 'EXCAVATION_ANNOUNCEMENT' AND key = 'HANDLING_FEE_SELF_SUPERVISION');


-- Lisää uudet kaivuilmoitusten ja aluevuokrausten hinnastotiedot
INSERT INTO allu.pricing (key, payment_class, value, application_type, validity)
VALUES
  -- < 60 m²
  ('LESS_THAN_60M2', '1', 10000, 'EXCAVATION_ANNOUNCEMENT', daterange('2025-03-01', NULL, '[]')),
  ('LESS_THAN_60M2', '2', 7500, 'EXCAVATION_ANNOUNCEMENT', daterange('2025-03-01', NULL, '[]')),
  ('LESS_THAN_60M2', '3', 5000, 'EXCAVATION_ANNOUNCEMENT', daterange('2025-03-01', NULL, '[]')),
  ('LESS_THAN_60M2', '4', 2500, 'EXCAVATION_ANNOUNCEMENT', daterange('2025-03-01', NULL, '[]')),
  ('LESS_THAN_60M2', '5', 1300, 'EXCAVATION_ANNOUNCEMENT', daterange('2025-03-01', NULL, '[]')),

  -- 60–120 m²
  ('FROM_60_TO_120M2', '1', 13000, 'EXCAVATION_ANNOUNCEMENT', daterange('2025-03-01', NULL, '[]')),
  ('FROM_60_TO_120M2', '2', 9800, 'EXCAVATION_ANNOUNCEMENT', daterange('2025-03-01', NULL, '[]')),
  ('FROM_60_TO_120M2', '3', 6500, 'EXCAVATION_ANNOUNCEMENT', daterange('2025-03-01', NULL, '[]')),
  ('FROM_60_TO_120M2', '4', 3300, 'EXCAVATION_ANNOUNCEMENT', daterange('2025-03-01', NULL, '[]')),
  ('FROM_60_TO_120M2', '5', 1600, 'EXCAVATION_ANNOUNCEMENT', daterange('2025-03-01', NULL, '[]')),

  -- 121–250 m²
  ('FROM_121_TO_250M2', '1', 16900, 'EXCAVATION_ANNOUNCEMENT', daterange('2025-03-01', NULL, '[]')),
  ('FROM_121_TO_250M2', '2', 12700, 'EXCAVATION_ANNOUNCEMENT', daterange('2025-03-01', NULL, '[]')),
  ('FROM_121_TO_250M2', '3', 8500, 'EXCAVATION_ANNOUNCEMENT', daterange('2025-03-01', NULL, '[]')),
  ('FROM_121_TO_250M2', '4', 4200, 'EXCAVATION_ANNOUNCEMENT', daterange('2025-03-01', NULL, '[]')),
  ('FROM_121_TO_250M2', '5', 2100, 'EXCAVATION_ANNOUNCEMENT', daterange('2025-03-01', NULL, '[]')),

  -- 251–500 m²
  ('FROM_251_TO_500M2', '1', 22000, 'EXCAVATION_ANNOUNCEMENT', daterange('2025-03-01', NULL, '[]')),
  ('FROM_251_TO_500M2', '2', 16500, 'EXCAVATION_ANNOUNCEMENT', daterange('2025-03-01', NULL, '[]')),
  ('FROM_251_TO_500M2', '3', 11000, 'EXCAVATION_ANNOUNCEMENT', daterange('2025-03-01', NULL, '[]')),
  ('FROM_251_TO_500M2', '4', 5500, 'EXCAVATION_ANNOUNCEMENT', daterange('2025-03-01', NULL, '[]')),
  ('FROM_251_TO_500M2', '5', 2700, 'EXCAVATION_ANNOUNCEMENT', daterange('2025-03-01', NULL, '[]')),

  -- 501–1000 m²
  ('FROM_501_TO_1000M2', '1', 28600, 'EXCAVATION_ANNOUNCEMENT', daterange('2025-03-01', NULL, '[]')),
  ('FROM_501_TO_1000M2', '2', 21400, 'EXCAVATION_ANNOUNCEMENT', daterange('2025-03-01', NULL, '[]')),
  ('FROM_501_TO_1000M2', '3', 14300, 'EXCAVATION_ANNOUNCEMENT', daterange('2025-03-01', NULL, '[]')),
  ('FROM_501_TO_1000M2', '4', 7100, 'EXCAVATION_ANNOUNCEMENT', daterange('2025-03-01', NULL, '[]')),
  ('FROM_501_TO_1000M2', '5', 3600, 'EXCAVATION_ANNOUNCEMENT', daterange('2025-03-01', NULL, '[]')),

  -- > 1000 m²
  ('MORE_THAN_1000M2', '1', 37100, 'EXCAVATION_ANNOUNCEMENT', daterange('2025-03-01', NULL, '[]')),
  ('MORE_THAN_1000M2', '2', 27800, 'EXCAVATION_ANNOUNCEMENT', daterange('2025-03-01', NULL, '[]')),
  ('MORE_THAN_1000M2', '3', 18600, 'EXCAVATION_ANNOUNCEMENT', daterange('2025-03-01', NULL, '[]')),
  ('MORE_THAN_1000M2', '4', 9300, 'EXCAVATION_ANNOUNCEMENT', daterange('2025-03-01', NULL, '[]')),
  ('MORE_THAN_1000M2', '5', 4600, 'EXCAVATION_ANNOUNCEMENT', daterange('2025-03-01', NULL, '[]')),

  -- Kaivuilmoituksen käsittely- ja valvontamaksu
  ('HANDLING_FEE', NULL, 24000, 'EXCAVATION_ANNOUNCEMENT', daterange('2025-03-01', NULL, '[]')),

  -- Alkava 15 m²
  ('UNIT_PRICE', '1', 1200, 'AREA_RENTAL', daterange('2025-03-01', NULL, '[]')),
  ('UNIT_PRICE', '2', 1000, 'AREA_RENTAL', daterange('2025-03-01', NULL, '[]')),
  ('UNIT_PRICE', '3', 700, 'AREA_RENTAL', daterange('2025-03-01', NULL, '[]')),
  ('UNIT_PRICE', '4', 400, 'AREA_RENTAL', daterange('2025-03-01', NULL, '[]')),
  ('UNIT_PRICE', '5', 200, 'AREA_RENTAL', daterange('2025-03-01', NULL, '[]')),

  -- Aluevuokrauksen käsittely- ja valvontamaksu
  ('MINOR_DISTURBANCE_HANDLING_FEE', NULL, 8000, 'AREA_RENTAL', daterange('2025-03-01', NULL, '[]')),
  ('MAJOR_DISTURBANCE_HANDLING_FEE', NULL, 24000, 'AREA_RENTAL', daterange('2025-03-01', NULL, '[]'));

ALTER TABLE allu.pricing
  ALTER COLUMN validity SET NOT NULL;

