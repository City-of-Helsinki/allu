alter sequence allu.VL_application_type_sequence rename to VP_application_type_sequence;

update allu.application set application_id = 'VP' || substr(application_id, 3, length(application_id))
where type = 'SHORT_TERM_RENTAL';

