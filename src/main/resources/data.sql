truncate table account cascade;

insert into account (id, user_id, first_name, last_name)
values (gen_random_uuid(), 'user', 'Jose', 'da Silva');

insert into account (id, user_id, first_name, last_name)
values (gen_random_uuid(), 'user2', 'Maria', 'da Silva');

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
where user_id = 'user';

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
where user_id = 'user2';

insert into gig (id, title, summary, account_id, gig_area_id, gig_mode_id, gig_visibility, open_for_negotiation,
                 other_gig_area, reference_price, remote_only)
select gen_random_uuid(),
       'gig do ' || user_id,
       'a gig do ' || user_id || ' eh bem legal',
       id,
       null,
       null,
       'PUBLIC',
       true,
       null,
       null,
       false
from account;

insert into gig_tags (gig_id, tag)
select id, 'teste'
from gig;

insert into gig_tags (gig_id, tag)
select id, 'foobar'
from gig;