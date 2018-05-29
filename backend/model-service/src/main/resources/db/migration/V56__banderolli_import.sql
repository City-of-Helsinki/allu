create function import_bridge_banners(customer_id integer, contact_id integer) returns void as
$$
declare
    r record;
    app_id integer;
    address_id integer;
    loc_id integer;
    app_customer_id integer;
    description text;
begin
    set search_path to default;
    for r in
        select tapahtumatunnus, osoite, tarkennettu_sijainti, "alkupäivä", "loppupäivä", kuvaus, kaupunginosa, name, location from "WINKKI"."SIIRTO_BANDE"
    loop
        select regexp_replace(r.kuvaus, E'[\\n\\r]+', ' ', 'g' ) into description;
        insert into allu.application(application_id, name, type, status, metadata_version, creation_time, extension, decision_publicity_type, not_billable, start_time, end_time)
            values (
                r.tapahtumatunnus,
                r.tapahtumatunnus,
                'NOTE',
                'PENDING',
                1,
                now(),
                '{"applicationType": "NOTE", "description": "' || description || '"}',
                'PUBLIC',
                true,
                r.alkupäivä::timestamptz,
                r.loppupäivä::timestamptz
            ) returning id into app_id;

        insert into allu.application_kind(application_id, kind) values (app_id, 'OTHER');
        insert into allu.application_customer(customer_id, application_id, customer_role_type) values (customer_id, app_id, 'APPLICANT') returning id into app_customer_id;
        insert into allu.application_customer_contact(application_customer_id, contact_id) values (app_customer_id, contact_id);
        insert into allu.postal_address(street_address) values (r.osoite) returning id into address_id;

        insert into allu.location(application_id, location_key, location_version, start_time, end_time, postal_address_id, city_district_id, area, additional_info)
            values (
                app_id,
                1,
                1,
                r.alkupäivä::timestamptz,
                r.loppupäivä::timestamptz,
                address_id,
                r.kaupunginosa,
                st_area(r.location),
                r.tarkennettu_sijainti
            ) returning id into loc_id;

        insert into allu.location_geometry(geometry, location_id)
            values (
                (ST_DUMP(r.location)).geom::geometry(Polygon, 3879),
                loc_id);
    end loop;
end;
$$
LANGUAGE plpgsql;
