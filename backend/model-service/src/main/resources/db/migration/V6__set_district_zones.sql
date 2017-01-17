-- Set zone ids to various city districts as specified in
-- http://www.hel.fi/static/hkr/luvat/maksut_tapahtumat.pdf

update allu.city_district set zone_id = 1 where district_id in
 (1, 2, 3, 4, 5, 6, 7, 8, 9, 13, 14, 20);
update allu.city_district set zone_id = 2 where district_id in
 (10, 11, 12, 15, 16, 17, 18, 19, 21, 22, 23, 24, 25, 26, 27, 30, 31, 42);
update allu.city_district set zone_id = 3 where district_id in
 (28, 29, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 43, 44, 45, 46, 47, 48,
  49, 50, 51, 52, 53, 54, 55, 56, 57, 58, 59);