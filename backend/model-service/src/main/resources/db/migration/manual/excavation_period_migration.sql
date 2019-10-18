-- Functions to create invoicing periods for existing excavation announcements.
-- All finished, archived, replaced and cancelled excavation announcements are left untouched
-- Doesn't create periods for winter time jobs having invoicing for both phases (operational condition and finished).

-- NOT FINISHED, NOT WINTER TIME JOB
CREATE OR REPLACE FUNCTION allu.update_non_wintertimejobs() RETURNS void
LANGUAGE plpgsql
AS $BODY$
DECLARE
  period_id int;
  app_id int;
  nr_of_updated int;
BEGIN
  nr_of_updated := 0;
  FOR app_id IN select id from allu.application where
            type = 'EXCAVATION_ANNOUNCEMENT'
            and extension::json ->> 'winterTimeOperation' is null
            and status not in ('FINISHED', 'ARCHIVED', 'REPLACED', 'CANCELLED')

  LOOP
    insert into allu.invoicing_period (application_id, closed, invoicable_status) values (app_id, false, 'FINISHED') returning id into period_id;
    update allu.charge_basis set invoicing_period_id = period_id where application_id = app_id;
    update allu.invoice set invoicing_period_id = period_id where application_id = app_id;
    nr_of_updated := nr_of_updated + 1;
  END LOOP;
  raise notice 'Updated % applications', nr_of_updated;
END
$BODY$
;
-- NOT FINISHED, WINTER TIME JOB, NO INVOICING FOR FINISHED-PHASE
CREATE OR REPLACE FUNCTION allu.update_wintertime_jobs_no_finished_invoicing() RETURNS void
LANGUAGE 'plpgsql'
AS $BODY$
DECLARE
  period_id int;
  app record;
  operational_period_locked boolean;
  nr_of_updated int;
BEGIN
  nr_of_updated := 0;
  FOR app IN select a.id, a.status, a.target_state from allu.application a where
            a.type = 'EXCAVATION_ANNOUNCEMENT'
            and a.extension::json ->> 'winterTimeOperation' is not null
            and a.status not in ('FINISHED', 'ARCHIVED', 'REPLACED', 'CANCELLED')
           -- Check that winter time operation has only one area fee (has only operational condition phase invoicing)
           -- and one handling fee
           and not exists (
                select c.id from allu.charge_basis c where c.tag <> 'EAHF' and c.tag <> 'EADF#1' and c.application_id = a.id
          )
  LOOP
    operational_period_locked := (app.status = 'OPERATIONAL_CONDITION') OR (app.status ='DECISIONMAKING' and app.target_state = 'FINISHED');
    insert into allu.invoicing_period (application_id, closed, invoicable_status) values (app.id, operational_period_locked, 'OPERATIONAL_CONDITION') returning id into period_id;
    insert into allu.invoicing_period (application_id, closed, invoicable_status) values (app.id, false, 'FINISHED');
    update allu.charge_basis set invoicing_period_id = period_id where application_id = app.id;
    update allu.invoice set invoicing_period_id = period_id where application_id = app.id;
    nr_of_updated := nr_of_updated + 1;
  END LOOP;
  raise notice 'Updated % applications', nr_of_updated;
END
$BODY$;

DO $$ BEGIN
   PERFORM allu.update_wintertime_jobs_no_finished_invoicing();
END $$;
DO $$ BEGIN
   PERFORM allu.update_non_wintertimejobs();
END $$;
