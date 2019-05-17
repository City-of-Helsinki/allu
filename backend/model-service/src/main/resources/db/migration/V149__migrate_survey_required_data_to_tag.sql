insert into
allu.application_tag
(application_id, added_by, type, creation_time)

(select
 a.id,
 case
  when a.handler IS NOT NULL
   then a.handler
  else a.owner
 end,
 'SURVEY_REQUIRED',
 case
  when a.decision_time IS NOT NULL
   then a.decision_time
  else a.creation_time
 end
 from allu.application a

 where
 a.type = 'CABLE_REPORT' and
 a.extension::jsonb ->> 'cableSurveyRequired' = 'true' and
 not exists
  (select 1
   from allu.application_tag t
   where
   t.type = 'SURVEY_REQUIRED' and
   t.application_id = a.id
  )
);
update allu.application set extension = extension::jsonb - 'cableSurveyRequired' - 'mapUpdated';
