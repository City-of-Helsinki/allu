update allu.application_tag set type='FINAL_SUPERVISION_REQUESTED' where type='WORK_READY_REPORTED';
update allu.application_tag set type='FINAL_SUPERVISION_ACCEPTED' where type='WORK_READY_ACCEPTED';
update allu.application_tag set type='FINAL_SUPERVISION_REJECTED' where type='WORK_READY_REJECTED';

update allu.application_comment set type='FINAL_SUPERVISION_ACCEPTED' where type='WORK_READY_ACCEPTED';
update allu.application_comment set type='FINAL_SUPERVISION_REJECTED' where type='WORK_READY_REJECTED';

update allu.attribute_meta set name='FINAL_SUPERVISION_REQUESTED', ui_name='Loppuvalvontapyyntö lähetetty' where name='WORK_READY_REPORTED';
update allu.attribute_meta set name='FINAL_SUPERVISION_ACCEPTED', ui_name='Loppuvalvonta hyväksytty' where name='WORK_READY_ACCEPTED';
update allu.attribute_meta set name='FINAL_SUPERVISION_REJECTED', ui_name='Loppuvalvonta hyväksytty' where name='WORK_READY_REJECTED';
