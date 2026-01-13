-- Päivitetän kaivuilmoituksen hinnasto

-- 1) Päätetään nykyinen avoin hintakausi 1.3.2026
UPDATE allu.pricing
SET validity = daterange('2025-03-01', '2026-02-28', '[]')
WHERE application_type = 'EXCAVATION_ANNOUNCEMENT'
  AND validity = daterange('2025-03-01', NULL, '[]');

-- 2) Luodaan uusi hintakausi uusilla hinnoilla alkaen 1.3.2026
INSERT INTO allu.pricing (key, payment_class, value, application_type, validity)
VALUES
  -- < 60 m²
  ('LESS_THAN_60M2', '1', 12000, 'EXCAVATION_ANNOUNCEMENT', daterange('2026-03-01', NULL, '[]')),
  ('LESS_THAN_60M2', '2', 9000, 'EXCAVATION_ANNOUNCEMENT', daterange('2026-03-01', NULL, '[]')),
  ('LESS_THAN_60M2', '3', 6000, 'EXCAVATION_ANNOUNCEMENT', daterange('2026-03-01', NULL, '[]')),
  ('LESS_THAN_60M2', '4', 3000, 'EXCAVATION_ANNOUNCEMENT', daterange('2026-03-01', NULL, '[]')),
  ('LESS_THAN_60M2', '5', 1500, 'EXCAVATION_ANNOUNCEMENT', daterange('2026-03-01', NULL, '[]')),

  -- 60–120 m²
  ('FROM_60_TO_120M2', '1', 15600, 'EXCAVATION_ANNOUNCEMENT', daterange('2026-03-01', NULL, '[]')),
  ('FROM_60_TO_120M2', '2', 11700, 'EXCAVATION_ANNOUNCEMENT', daterange('2026-03-01', NULL, '[]')),
  ('FROM_60_TO_120M2', '3', 7800, 'EXCAVATION_ANNOUNCEMENT', daterange('2026-03-01', NULL, '[]')),
  ('FROM_60_TO_120M2', '4', 3900, 'EXCAVATION_ANNOUNCEMENT', daterange('2026-03-01', NULL, '[]')),
  ('FROM_60_TO_120M2', '5', 2000, 'EXCAVATION_ANNOUNCEMENT', daterange('2026-03-01', NULL, '[]')),

  -- 121–250 m²
  ('FROM_121_TO_250M2', '1', 20300, 'EXCAVATION_ANNOUNCEMENT', daterange('2026-03-01', NULL, '[]')),
  ('FROM_121_TO_250M2', '2', 15200, 'EXCAVATION_ANNOUNCEMENT', daterange('2026-03-01', NULL, '[]')),
  ('FROM_121_TO_250M2', '3', 10100, 'EXCAVATION_ANNOUNCEMENT', daterange('2026-03-01', NULL, '[]')),
  ('FROM_121_TO_250M2', '4', 5100, 'EXCAVATION_ANNOUNCEMENT', daterange('2026-03-01', NULL, '[]')),
  ('FROM_121_TO_250M2', '5', 2500, 'EXCAVATION_ANNOUNCEMENT', daterange('2026-03-01', NULL, '[]')),

  -- 251–500 m²
  ('FROM_251_TO_500M2', '1', 26400, 'EXCAVATION_ANNOUNCEMENT', daterange('2026-03-01', NULL, '[]')),
  ('FROM_251_TO_500M2', '2', 19800, 'EXCAVATION_ANNOUNCEMENT', daterange('2026-03-01', NULL, '[]')),
  ('FROM_251_TO_500M2', '3', 13200, 'EXCAVATION_ANNOUNCEMENT', daterange('2026-03-01', NULL, '[]')),
  ('FROM_251_TO_500M2', '4', 6600, 'EXCAVATION_ANNOUNCEMENT', daterange('2026-03-01', NULL, '[]')),
  ('FROM_251_TO_500M2', '5', 3300, 'EXCAVATION_ANNOUNCEMENT', daterange('2026-03-01', NULL, '[]')),

  -- 501–1000 m²
  ('FROM_501_TO_1000M2', '1', 34300, 'EXCAVATION_ANNOUNCEMENT', daterange('2026-03-01', NULL, '[]')),
  ('FROM_501_TO_1000M2', '2', 25700, 'EXCAVATION_ANNOUNCEMENT', daterange('2026-03-01', NULL, '[]')),
  ('FROM_501_TO_1000M2', '3', 17100, 'EXCAVATION_ANNOUNCEMENT', daterange('2026-03-01', NULL, '[]')),
  ('FROM_501_TO_1000M2', '4', 8600, 'EXCAVATION_ANNOUNCEMENT', daterange('2026-03-01', NULL, '[]')),
  ('FROM_501_TO_1000M2', '5', 4300, 'EXCAVATION_ANNOUNCEMENT', daterange('2026-03-01', NULL, '[]')),

  -- > 1000 m²
  ('MORE_THAN_1000M2', '1', 44600, 'EXCAVATION_ANNOUNCEMENT', daterange('2026-03-01', NULL, '[]')),
  ('MORE_THAN_1000M2', '2', 33400, 'EXCAVATION_ANNOUNCEMENT', daterange('2026-03-01', NULL, '[]')),
  ('MORE_THAN_1000M2', '3', 22300, 'EXCAVATION_ANNOUNCEMENT', daterange('2026-03-01', NULL, '[]')),
  ('MORE_THAN_1000M2', '4', 11100, 'EXCAVATION_ANNOUNCEMENT', daterange('2026-03-01', NULL, '[]')),
  ('MORE_THAN_1000M2', '5', 5600, 'EXCAVATION_ANNOUNCEMENT', daterange('2026-03-01', NULL, '[]')),

  -- Kaivuilmoituksen käsittely- ja valvontamaksut (LT = LESS THAN, GE = GREATER OR EQUAL)
  ('HANDLING_FEE_LT_6_MONTHS', NULL, 24000, 'EXCAVATION_ANNOUNCEMENT', daterange('2026-03-01', NULL, '[]')),
  ('HANDLING_FEE_GE_6_MONTHS', NULL, 40000, 'EXCAVATION_ANNOUNCEMENT', daterange('2026-03-01', NULL, '[]'));
