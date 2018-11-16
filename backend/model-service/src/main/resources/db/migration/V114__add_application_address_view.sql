create or replace view allu.application_with_addresses as
  select distinct a.id as application_id, case when la.name is null then pa.street_address else la.name end as address
    from application a
    left join location l on a.id=l.application_id
    left join postal_address pa on l.postal_address_id=pa.id
    left join location_flids lf on l.id=lf.location_id
    left join fixed_location fl on lf.fixed_location_id=fl.id
    left join location_area la on fl.area_id=la.id;

create or replace view allu.application_address as
  select application_id, string_agg(address, ', ') as address from application_with_addresses group by application_id;

create or replace view allu.supervision_task_with_address as
  select s.id,s.application_id,s.type,s.creator_id,s.owner_id,s.creation_time,s.planned_finishing_time,s.actual_finishing_time,s.status,s.description,s.result,s.location_id,p.street_address as address
    from supervision_task s
    inner join location l on s.location_id=l.id
    left join postal_address p on l.postal_address_id=p.id
  union
  select s.id,s.application_id,s.type,s.creator_id,s.owner_id,s.creation_time,s.planned_finishing_time,s.actual_finishing_time,s.status,s.description,s.result,s.location_id,a.address
    from supervision_task s
    left join application_address a on s.application_id=a.application_id
    where s.location_id is null;
