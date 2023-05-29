INSERT INTO profile (id, picture_id, public_name, profession, highlight_title, profile_category, working_mode,
                     city, public_profile, available, amount, currency, description, professional_record, slug,
                     created_at, created_by, modified_at, modified_by, last_activity)
VALUES ('32804943-a9c2-463e-a3ce-218e32244c43', null, 'Joélisson Santos',
        'Desenvolvedor', 'React', 'PET_SITTER', 1, 'Lorena - SP', null, null, 1, 0, null, null, '97jsantos',
        '2023-05-25 16:41:11.520971 +00:00', '32804943-a9c2-463e-a3ce-218e32244c43',
        '2023-05-26 15:04:38.349343 +00:00', '32804943-a9c2-463e-a3ce-218e32244c43',
        '2023-05-26 15:04:38.253855 +00:00');
INSERT INTO profile (id, picture_id, public_name, profession, highlight_title, profile_category, working_mode,
                     city, public_profile, available, amount, currency, description, professional_record, slug,
                     created_at, created_by, modified_at, modified_by, last_activity)
VALUES ('3ebe5926-e7c4-42d1-bf96-ca893c7b4a44', null, 'Boda Bidinga',
        'Faco de tudo, e com prazer', 'A melhor que tem', 'BEAUTY_CARE', 0, 'Berlin', true, true, 3000, 0,
        'Sou a bodona', 'eh, nao eh muito, mas por enquanto eh o que tem', 'bodinha', null, null, null, null,
        '2023-05-27 10:14:38.673912 +00:00');
INSERT INTO profile (id, picture_id, public_name, profession, highlight_title, profile_category, working_mode,
                     city, public_profile, available, amount, currency, description, professional_record, slug,
                     created_at, created_by, modified_at, modified_by, last_activity)
VALUES ('a925feb9-14ff-4cc7-9ff0-444af3def147', null, 'Bodinha Jurema', 'cabeleleila',
        'Testnb asfb ksjdb hkasjd haskjdh aks jdh askj dhaskjdh akjdh kdjhas kdj hdkjh', 'BEAUTY_CARE', 0, 'Berlin',
        null, null, 1000, 0, null, null, 'jureminha', null, null, null, null, '2023-05-26 11:47:25.979069 +00:00');
INSERT INTO profile (id, picture_id, public_name, profession, highlight_title, profile_category, working_mode,
                     city, public_profile, available, amount, currency, description, professional_record, slug,
                     created_at, created_by, modified_at, modified_by, last_activity)
VALUES ('2eb0b7be-6bf5-47f8-8a51-3a19e14f290d', null, 'Ricardo Baumann',
        'Garoto de programa', 'Atendo casais e eventos', 'PET_SITTER', 0, 'Berlin', null, null, 150, 0, null, null,
        'ricardobaumann', '2023-05-16 07:41:19.874339 +00:00', '2eb0b7be-6bf5-47f8-8a51-3a19e14f290d',
        '2023-05-27 10:16:33.692378 +00:00', '2eb0b7be-6bf5-47f8-8a51-3a19e14f290d',
        '2023-05-27 10:16:33.481853 +00:00');
INSERT INTO skill (id, name)
VALUES ('16c85ffb-7748-46b2-add1-a13fc5cce725', 'cabelo');
INSERT INTO skill (id, name)
VALUES ('3a463d76-0de9-4d06-a333-19c979eb3f9f', 'Tingimento de cabelos loiros');
INSERT INTO skill (id, name)
VALUES ('c10b309a-9410-44d8-a61b-5a6500df7000', 'Descoloração');
INSERT INTO skill (id, name)
VALUES ('b1db1ebb-a1c6-4705-baee-8dd0e3985573', 'Alisamento');
INSERT INTO skill (id, name)
VALUES ('3c7c0901-6534-4d80-973d-0b62efcb56b7', 'Corte');
INSERT INTO skill (id, name)
VALUES ('755226f4-3973-4612-8d54-b43b88f17645', 'Alongamento de fios');
INSERT INTO skill (id, name)
VALUES ('ce9cf5e0-20a5-4c1d-bbd4-b4d7409eba67', 'Hidratação capilar');
INSERT INTO availability (id, profile_id, day_of_week)
VALUES ('1d70a9b1-9866-47ca-9f23-5a222e40d843', '32804943-a9c2-463e-a3ce-218e32244c43', 'WEDNESDAY');
INSERT INTO availability (id, profile_id, day_of_week)
VALUES ('5dbcddeb-f368-4730-84e3-b993af76eb7d', '32804943-a9c2-463e-a3ce-218e32244c43', 'SATURDAY');
INSERT INTO availability (id, profile_id, day_of_week)
VALUES ('24cedf23-5b9c-4096-ae7a-efb888b589ce', '32804943-a9c2-463e-a3ce-218e32244c43', 'TUESDAY');
INSERT INTO availability (id, profile_id, day_of_week)
VALUES ('f243b50c-029f-423c-bc2d-7edfb16af0bd', '32804943-a9c2-463e-a3ce-218e32244c43', 'THURSDAY');
INSERT INTO availability (id, profile_id, day_of_week)
VALUES ('0f4d051a-fc1a-40d1-8893-bfe4c7dd4d0a', '32804943-a9c2-463e-a3ce-218e32244c43', 'FRIDAY');
INSERT INTO availability (id, profile_id, day_of_week)
VALUES ('de133a70-5672-4da5-8485-31d815d92d18', '32804943-a9c2-463e-a3ce-218e32244c43', 'MONDAY');
INSERT INTO availability_availability_type (availability_id, availability_type)
VALUES ('1d70a9b1-9866-47ca-9f23-5a222e40d843', 'EVENING');
INSERT INTO availability_availability_type (availability_id, availability_type)
VALUES ('1d70a9b1-9866-47ca-9f23-5a222e40d843', 'MORNING');
INSERT INTO availability_availability_type (availability_id, availability_type)
VALUES ('5dbcddeb-f368-4730-84e3-b993af76eb7d', 'MORNING');
INSERT INTO availability_availability_type (availability_id, availability_type)
VALUES ('24cedf23-5b9c-4096-ae7a-efb888b589ce', 'EVENING');
INSERT INTO availability_availability_type (availability_id, availability_type)
VALUES ('24cedf23-5b9c-4096-ae7a-efb888b589ce', 'AFTERNOON');
INSERT INTO availability_availability_type (availability_id, availability_type)
VALUES ('24cedf23-5b9c-4096-ae7a-efb888b589ce', 'MORNING');
INSERT INTO availability_availability_type (availability_id, availability_type)
VALUES ('f243b50c-029f-423c-bc2d-7edfb16af0bd', 'EVENING');
INSERT INTO availability_availability_type (availability_id, availability_type)
VALUES ('f243b50c-029f-423c-bc2d-7edfb16af0bd', 'AFTERNOON');
INSERT INTO availability_availability_type (availability_id, availability_type)
VALUES ('f243b50c-029f-423c-bc2d-7edfb16af0bd', 'MORNING');
INSERT INTO availability_availability_type (availability_id, availability_type)
VALUES ('0f4d051a-fc1a-40d1-8893-bfe4c7dd4d0a', 'EVENING');
INSERT INTO availability_availability_type (availability_id, availability_type)
VALUES ('0f4d051a-fc1a-40d1-8893-bfe4c7dd4d0a', 'AFTERNOON');
INSERT INTO availability_availability_type (availability_id, availability_type)
VALUES ('de133a70-5672-4da5-8485-31d815d92d18', 'AFTERNOON');
INSERT INTO availability_availability_type (availability_id, availability_type)
VALUES ('de133a70-5672-4da5-8485-31d815d92d18', 'MORNING');
INSERT INTO profile_skill (id, skill_id, certifications, level, top, profile_id)
VALUES ('f7a3023a-84c1-4d56-ba47-84c9fe29d737', '16c85ffb-7748-46b2-add1-a13fc5cce725', 1, 0, false,
        '3ebe5926-e7c4-42d1-bf96-ca893c7b4a44');
INSERT INTO profile_skill (id, skill_id, certifications, level, top, profile_id)
VALUES ('f99152be-1558-4f10-8770-bd8a63b48965', 'ce9cf5e0-20a5-4c1d-bbd4-b4d7409eba67', 1, 2, false,
        '32804943-a9c2-463e-a3ce-218e32244c43');
INSERT INTO profile_skill (id, skill_id, certifications, level, top, profile_id)
VALUES ('921a606b-d222-4a7b-99ce-731b3ca2c03a', '3a463d76-0de9-4d06-a333-19c979eb3f9f', 7, 1, true,
        '32804943-a9c2-463e-a3ce-218e32244c43');
INSERT INTO profile_skill (id, skill_id, certifications, level, top, profile_id)
VALUES ('e9b13309-f097-4d43-b680-c3708b778f4a', '3c7c0901-6534-4d80-973d-0b62efcb56b7', 3, 2, true,
        '32804943-a9c2-463e-a3ce-218e32244c43');
INSERT INTO profile_skill (id, skill_id, certifications, level, top, profile_id)
VALUES ('789e137f-2afc-437c-b359-3b528f30f09d', '755226f4-3973-4612-8d54-b43b88f17645', 1, 0, false,
        '32804943-a9c2-463e-a3ce-218e32244c43');
INSERT INTO profile_skill (id, skill_id, certifications, level, top, profile_id)
VALUES ('2b21e855-2b80-4b96-88ed-ad3eb5794f20', 'c10b309a-9410-44d8-a61b-5a6500df7000', 1, 1, true,
        '32804943-a9c2-463e-a3ce-218e32244c43');
INSERT INTO profile_skill (id, skill_id, certifications, level, top, profile_id)
VALUES ('87f70e70-ece3-43c9-8b03-32ed4f7b3023', 'b1db1ebb-a1c6-4705-baee-8dd0e3985573', 1, 0, false,
        '32804943-a9c2-463e-a3ce-218e32244c43');