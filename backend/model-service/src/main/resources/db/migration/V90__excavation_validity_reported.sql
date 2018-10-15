update allu.application set extension = extension::jsonb || jsonb_build_object('validityReported', null) where type='EXCAVATION_ANNOUNCEMENT';
