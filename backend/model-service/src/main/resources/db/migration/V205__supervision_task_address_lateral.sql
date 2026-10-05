-- The second branch of supervision_task_with_address (tasks without own
-- location) joined allu.application_address, which aggregates addresses for
-- every application in the database. The planner badly underestimated the
-- aggregate's row count (200 vs ~159 000) and chose a nested loop over the
-- materialized result for every task, making supervision task searches take
-- over 100 seconds in production.
--
-- Resolve addresses with a LATERAL subquery only for each task's own
-- application instead. The result matches application_address: distinct,
-- non-null addresses in explicit alphabetical order, or null when there are none.

drop view allu.supervision_task_with_address;

create view allu.supervision_task_with_address as
  select s.id,s.application_id,s.type,s.creator_id,s.owner_id,s.creation_time,s.planned_finishing_time,s.actual_finishing_time,s.status,s.description,s.result,s.location_id,
    case
        when p.street_address is not null then array[]::text[] || p.street_address
        else null
    end as address
    from allu.supervision_task s
    inner join allu.location l on s.location_id=l.id
    left join allu.postal_address p on l.postal_address_id=p.id
  union all
  select s.id,s.application_id,s.type,s.creator_id,s.owner_id,s.creation_time,s.planned_finishing_time,s.actual_finishing_time,s.status,s.description,s.result,s.location_id,a.address
    from allu.supervision_task s
    left join lateral (
      select array_agg(distinct x.address order by x.address) filter (where x.address is not null) as address
        from (
          select case when la.name is null then pa.street_address else la.name end as address
            from allu.location l
            left join allu.postal_address pa on l.postal_address_id=pa.id
            left join allu.location_flids lf on l.id=lf.location_id
            left join allu.fixed_location fl on lf.fixed_location_id=fl.id
            left join allu.location_area la on fl.area_id=la.id
            where l.application_id=s.application_id
        ) x
    ) a on true
    where s.location_id is null;
