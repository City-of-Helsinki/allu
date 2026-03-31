-- Change UNION to UNION ALL in supervision_task_with_address view.
-- The two branches are mutually exclusive:
--   branch 1: location_id IS NOT NULL (required by INNER JOIN)
--   branch 2: location_id IS NULL (explicit WHERE clause)
-- A single row cannot satisfy both conditions, so UNION deduplication is a no-op.
-- Replacing it with UNION ALL eliminates the full sort/hash pass over the result set.
--
-- Replaces supervision_task_owner_index (single-column) with a composite index on
-- (owner_id, status, planned_finishing_time). Column order follows the equality-first,
-- range-last rule: owner_id and status are equality predicates in the common search
-- filters; planned_finishing_time is a range predicate. The composite index is a strict
-- superset of the old owner index, so the old index is dropped to avoid redundant write
-- overhead.

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
    left join allu.application_address a on s.application_id=a.application_id
    where s.location_id is null;

drop index allu.supervision_task_owner_index;

create index supervision_task_owner_status_time_index
  on allu.supervision_task (owner_id, status, planned_finishing_time);
