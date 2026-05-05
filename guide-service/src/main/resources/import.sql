-- 1. KATALOG JEZIKA (15 unosa)
INSERT INTO language (language_name, language_code) VALUES ('Engleski', 'EN');
INSERT INTO language (language_name, language_code) VALUES ('Nemački', 'DE');
INSERT INTO language (language_name, language_code) VALUES ('Francuski', 'FR');
INSERT INTO language (language_name, language_code) VALUES ('Italijanski', 'IT');
INSERT INTO language (language_name, language_code) VALUES ('Španski', 'ES');
INSERT INTO language (language_name, language_code) VALUES ('Ruski', 'RU');
INSERT INTO language (language_name, language_code) VALUES ('Mađarski', 'HU');
INSERT INTO language (language_name, language_code) VALUES ('Turski', 'TR');
INSERT INTO language (language_name, language_code) VALUES ('Kineski', 'ZH');
INSERT INTO language (language_name, language_code) VALUES ('Japanski', 'JA');
INSERT INTO language (language_name, language_code) VALUES ('Arapski', 'AR');
INSERT INTO language (language_name, language_code) VALUES ('Grčki', 'EL');
INSERT INTO language (language_name, language_code) VALUES ('Poljski', 'PL');
INSERT INTO language (language_name, language_code) VALUES ('Rumunski', 'RO');
INSERT INTO language (language_name, language_code) VALUES ('Holandski', 'NL');

-- 2. PROFILI VODIČA (15 unosa)
INSERT INTO guide_profile (first_name, last_name, bio, rating, contact_email, phone_number) VALUES ('Marko', 'Marković', 'Specijalista za visokogorstvo.', 4.8, 'marko@guide.rs', '+38164111222');
INSERT INTO guide_profile (first_name, last_name, bio, rating, contact_email, phone_number) VALUES ('Jelena', 'Jović', 'Ljubitelj pećina i speleologije.', 4.5, 'jelena@guide.rs', '+38164333444');
INSERT INTO guide_profile (first_name, last_name, bio, rating, contact_email, phone_number) VALUES ('Igor', 'Stanić', 'Skipper na divljim vodama.', 4.9, 'igor@guide.rs', '+38164555666');
INSERT INTO guide_profile (first_name, last_name, bio, rating, contact_email, phone_number) VALUES ('Pera', 'Perić', 'Planinarski vodič početnik.', 3.2, 'pera@test.rs', '+38164777888');
INSERT INTO guide_profile (first_name, last_name, bio, rating, contact_email, phone_number) VALUES ('Ana', 'Anić', 'Ekspert za botaničke ture.', 5.0, 'ana@guide.rs', '+38164999000');
INSERT INTO guide_profile (first_name, last_name, bio, rating, contact_email, phone_number) VALUES ('Nikola', 'Tasić', 'Istoričar i vodič kroz tvrđave.', 4.7, 'nikola@history.rs', '+38161123456');
INSERT INTO guide_profile (first_name, last_name, bio, rating, contact_email, phone_number) VALUES ('Milica', 'Mikić', 'Instruktor joge u prirodi.', 4.6, 'milica@yoga.rs', '+38162234567');
INSERT INTO guide_profile (first_name, last_name, bio, rating, contact_email, phone_number) VALUES ('Stefan', 'Savić', 'Biciklističke ture po Balkanu.', 4.4, 'stefan@bike.rs', '+38163345678');
INSERT INTO guide_profile (first_name, last_name, bio, rating, contact_email, phone_number) VALUES ('Sofija', 'Sarić', 'Vodič za preživljavanje u šumi.', 4.9, 'sofija@wild.rs', '+38165456789');
INSERT INTO guide_profile (first_name, last_name, bio, rating, contact_email, phone_number) VALUES ('Dragan', 'Dakić', 'Lov i ribolov ekspert.', 3.8, 'dragan@nature.rs', '+38160567890');
INSERT INTO guide_profile (first_name, last_name, bio, rating, contact_email, phone_number) VALUES ('Maja', 'Majkić', 'Degustator vina i gastro vodič.', 4.2, 'maja@gastro.rs', '+38169678901');
INSERT INTO guide_profile (first_name, last_name, bio, rating, contact_email, phone_number) VALUES ('Uroš', 'Umić', 'Fotograf i vodič za foto-safari.', 4.7, 'uros@photo.rs', '+38167789012');
INSERT INTO guide_profile (first_name, last_name, bio, rating, contact_email, phone_number) VALUES ('Lana', 'Lanić', 'Dečije ekološke radionice.', 4.1, 'lana@eko.rs', '+38168890123');
INSERT INTO guide_profile (first_name, last_name, bio, rating, contact_email, phone_number) VALUES ('Bojan', 'Boki', 'Noćno planinarenje i astronomija.', 4.5, 'bojan@stars.rs', '+38166901234');
INSERT INTO guide_profile (first_name, last_name, bio, rating, contact_email, phone_number) VALUES ('Zoran', 'Zoki', 'Stari vuk, poznaje svaku stazu.', 4.3, 'zoran@oldskul.rs', '+38164012345');

-- ==========================================
-- 3. SERTIFIKATI (15 unosa) - ISPRAVLJENO: guide_profile_id
-- ==========================================
INSERT INTO certification (name, issuing_body, expiry_date, guide_profile_id) VALUES ('Gorska Služba Spasavanja', 'GSS Srbija', '2027-01-01', 1);
INSERT INTO certification (name, issuing_body, expiry_date, guide_profile_id) VALUES ('UIAA Mountain Guide', 'UIAA Training', '2026-06-15', 1);
INSERT INTO certification (name, issuing_body, expiry_date, guide_profile_id) VALUES ('Prva Pomoć - Napredni nivo', 'Crveni Krst', '2028-03-20', 1);
INSERT INTO certification (name, issuing_body, expiry_date, guide_profile_id) VALUES ('Speleološka Licenca', 'Speleo Savez', '2027-11-11', 2);
INSERT INTO certification (name, issuing_body, expiry_date, guide_profile_id) VALUES ('Skipper Level 3', 'IRF Global', '2026-09-01', 3);
INSERT INTO certification (name, issuing_body, expiry_date, guide_profile_id) VALUES ('Wilderness First Aid', 'NOLS', '2027-02-10', 9);
INSERT INTO certification (name, issuing_body, expiry_date, guide_profile_id) VALUES ('Eco-Tourism Certificate', 'Green Globe', '2028-10-10', 5);
INSERT INTO certification (name, issuing_body, expiry_date, guide_profile_id) VALUES ('Yoga Instructor RYT200', 'Yoga Alliance', '2029-01-01', 7);
INSERT INTO certification (name, issuing_body, expiry_date, guide_profile_id) VALUES ('Cycling Guide Pro', 'EuroVelo', '2026-12-31', 8);
INSERT INTO certification (name, issuing_body, expiry_date, guide_profile_id) VALUES ('History & Culture Badge', 'Ministry of Culture', '2030-05-05', 6);
INSERT INTO certification (name, issuing_body, expiry_date, guide_profile_id) VALUES ('Sommelier Level 1', 'WSET', '2027-08-15', 11);
INSERT INTO certification (name, issuing_body, expiry_date, guide_profile_id) VALUES ('Survival Expert Rank', 'Bear Grylls Academy', '2026-11-20', 9);
INSERT INTO certification (name, issuing_body, expiry_date, guide_profile_id) VALUES ('Photo Guide Master', 'National Geo Academy', '2027-04-30', 12);
INSERT INTO certification (name, issuing_body, expiry_date, guide_profile_id) VALUES ('Rescue Diver', 'PADI', '2026-07-07', 3);
INSERT INTO certification (name, issuing_body, expiry_date, guide_profile_id) VALUES ('Night Sky Specialist', 'Stargaze Org', '2028-12-12', 14);

-- ==========================================
-- 4. VEZA VODIČ-JEZIK (15 unosa) - ISPRAVLJENO: guide_profile_id
-- ==========================================
INSERT INTO guide_language (guide_profile_id, language_id, proficiency_level) VALUES (1, 1, 'C2');
INSERT INTO guide_language (guide_profile_id, language_id, proficiency_level) VALUES (1, 2, 'B2');
INSERT INTO guide_language (guide_profile_id, language_id, proficiency_level) VALUES (1, 4, 'B1');
INSERT INTO guide_language (guide_profile_id, language_id, proficiency_level) VALUES (2, 4, 'Native');
INSERT INTO guide_language (guide_profile_id, language_id, proficiency_level) VALUES (3, 1, 'C1');
INSERT INTO guide_language (guide_profile_id, language_id, proficiency_level) VALUES (3, 6, 'B2');
INSERT INTO guide_language (guide_profile_id, language_id, proficiency_level) VALUES (5, 1, 'C1');
INSERT INTO guide_language (guide_profile_id, language_id, proficiency_level) VALUES (6, 3, 'B2');
INSERT INTO guide_language (guide_profile_id, language_id, proficiency_level) VALUES (8, 2, 'C1');
INSERT INTO guide_language (guide_profile_id, language_id, proficiency_level) VALUES (9, 1, 'B2');
INSERT INTO guide_language (guide_profile_id, language_id, proficiency_level) VALUES (10, 6, 'Native');
INSERT INTO guide_language (guide_profile_id, language_id, proficiency_level) VALUES (11, 3, 'C1');
INSERT INTO guide_language (guide_profile_id, language_id, proficiency_level) VALUES (12, 1, 'C2');
INSERT INTO guide_language (guide_profile_id, language_id, proficiency_level) VALUES (14, 5, 'B1');
INSERT INTO guide_language (guide_profile_id, language_id, proficiency_level) VALUES (15, 1, 'B2');