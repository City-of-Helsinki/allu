update allu.pricing set value = 1321
where application_type = 'SHORT_TERM_RENTAL' and "key" = 'SUMMER_TERRACE' and payment_class = '1';

update allu.pricing set value = 991
where application_type = 'SHORT_TERM_RENTAL' and "key" = 'SUMMER_TERRACE' and payment_class = '2';

update allu.pricing set value = 661
where application_type = 'SHORT_TERM_RENTAL' and "key" = 'WINTER_TERRACE' and payment_class = '1';

update allu.pricing set value = 496
where application_type = 'SHORT_TERM_RENTAL' and "key" = 'WINTER_TERRACE' and payment_class = '2';

update allu.pricing set value = 1321
where application_type = 'SHORT_TERM_RENTAL' and "key" = 'PARKLET' and payment_class = '1';

update allu.pricing set value = 991
where application_type = 'SHORT_TERM_RENTAL' and "key" = 'PARKLET' and payment_class = '2';
