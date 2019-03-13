drop view allu.supervision_task_with_address;
drop view allu.application_address;

create view allu.application_address as
  select application_id, array_agg(address) filter (where address is not null) as address from allu.application_with_addresses group by application_id;

create view allu.supervision_task_with_address as
  select s.id,s.application_id,s.type,s.creator_id,s.owner_id,s.creation_time,s.planned_finishing_time,s.actual_finishing_time,s.status,s.description,s.result,s.location_id,
    case
        when p.street_address is not null then array[]::text[] || p.street_address
        else null
    end as address
    from allu.supervision_task s
    inner join allu.location l on s.location_id=l.id
    left join allu.postal_address p on l.postal_address_id=p.id
  union
  select s.id,s.application_id,s.type,s.creator_id,s.owner_id,s.creation_time,s.planned_finishing_time,s.actual_finishing_time,s.status,s.description,s.result,s.location_id,a.address
    from allu.supervision_task s
    left join allu.application_address a on s.application_id=a.application_id
    where s.location_id is null;