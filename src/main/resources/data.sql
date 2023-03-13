truncate table account cascade;

insert into account (id, first_name, last_name)
values (gen_random_uuid(), 'Jose', 'da Silva');

insert into account (id, first_name, last_name)
values (gen_random_uuid(), 'Maria', 'da Silva');

insert into profile (id, account_id, bio, language, address_id, email, country_code, dial_code, phone_number)
select gen_random_uuid(),
       id,
       'Bio do usuario',
       'PT-BR',
       null,
       'jose@test.com',
       'BR',
       '+55',
       '123456789'
from account
where first_name = 'Jose';

insert into profile (id, account_id, bio, language, address_id, email, country_code, dial_code, phone_number)
select gen_random_uuid(),
       id,
       'Bio do usuario',
       'PT-BR',
       null,
       'maria@test.com',
       'BR',
       '+55',
       '01122336699'
from account
where first_name = 'Maria';

insert into gig (id, title, summary, account_id, gig_area_id, gig_mode_id, gig_visibility, open_for_negotiation,
                 other_gig_area, reference_price, remote_only, gig_type)
select gen_random_uuid(),
       'gig do ' || id,
       'a gig do ' || id || ' eh bem legal',
       id,
       null,
       null,
       'PUBLIC',
       true,
       null,
       null,
       false,
       'OFFER'
from account;

insert into gig_tags (gig_id, tag)
select id, 'teste'
from gig;

insert into gig_tags (gig_id, tag)
select id, 'foobar'
from gig;