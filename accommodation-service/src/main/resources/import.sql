-- Adrese
INSERT INTO addresses (city, zip_code, street_address) VALUES ('Zabljak', '84210', 'Durmitorska bb');
INSERT INTO addresses (city, zip_code, street_address) VALUES ('Kolasin', '84325', 'Bjelopavlicka 12');
INSERT INTO addresses (city, zip_code, street_address) VALUES ('Biogradska Gora', '84205', 'Nacionalni park bb');
INSERT INTO addresses (city, zip_code, street_address) VALUES ('Tara', '31315', 'Kaludjerske Bare bb');
INSERT INTO addresses (city, zip_code, street_address) VALUES ('Prokletije', '84305', 'Gusinje bb');

-- Smjestajne jedinice
INSERT INTO units (name, unit_type, base_price_per_night, address_id) VALUES ('Koliba Zeleni mir', 'CABIN', 85.0, 1);
INSERT INTO units (name, unit_type, base_price_per_night, address_id) VALUES ('Eko-sator Durmitor', 'TENT', 35.0, 1);
INSERT INTO units (name, unit_type, base_price_per_night, address_id) VALUES ('Glamping vila Bjelopavlic', 'GLAMPING', 120.0, 2);
INSERT INTO units (name, unit_type, base_price_per_night, address_id) VALUES ('Kuca na drvetu Biogradska', 'TREEHOUSE', 95.0, 3);
INSERT INTO units (name, unit_type, base_price_per_night, address_id) VALUES ('Eko-loza Tara', 'ECO_LODGE', 110.0, 4);
INSERT INTO units (name, unit_type, base_price_per_night, address_id) VALUES ('Planinski sator Prokletije', 'TENT', 40.0, 5);

-- Sadrzaji
INSERT INTO facilities (name, extra_cost) VALUES ('Privatni bazen', 30.0);
INSERT INTO facilities (name, extra_cost) VALUES ('Sauna', 15.0);
INSERT INTO facilities (name, extra_cost) VALUES ('WiFi', 0.0);
INSERT INTO facilities (name, extra_cost) VALUES ('Parking', 5.0);
INSERT INTO facilities (name, extra_cost) VALUES ('Jacuzzi', 20.0);
INSERT INTO facilities (name, extra_cost) VALUES ('Kuhinja', 0.0);
INSERT INTO facilities (name, extra_cost) VALUES ('Roshtilj', 0.0);

-- Veza smjestaj - sadrzaji (unit_facilities)
INSERT INTO unit_facilities (unit_id, facility_id) VALUES (1, 3);
INSERT INTO unit_facilities (unit_id, facility_id) VALUES (1, 4);
INSERT INTO unit_facilities (unit_id, facility_id) VALUES (1, 6);
INSERT INTO unit_facilities (unit_id, facility_id) VALUES (1, 7);
INSERT INTO unit_facilities (unit_id, facility_id) VALUES (2, 3);
INSERT INTO unit_facilities (unit_id, facility_id) VALUES (2, 7);
INSERT INTO unit_facilities (unit_id, facility_id) VALUES (3, 1);
INSERT INTO unit_facilities (unit_id, facility_id) VALUES (3, 2);
INSERT INTO unit_facilities (unit_id, facility_id) VALUES (3, 3);
INSERT INTO unit_facilities (unit_id, facility_id) VALUES (3, 4);
INSERT INTO unit_facilities (unit_id, facility_id) VALUES (3, 5);
INSERT INTO unit_facilities (unit_id, facility_id) VALUES (4, 3);
INSERT INTO unit_facilities (unit_id, facility_id) VALUES (4, 6);
INSERT INTO unit_facilities (unit_id, facility_id) VALUES (5, 2);
INSERT INTO unit_facilities (unit_id, facility_id) VALUES (5, 3);
INSERT INTO unit_facilities (unit_id, facility_id) VALUES (5, 6);

-- Sezonski cjenovnici
INSERT INTO price_tiers (unit_id, name, multiplier, start_date, end_date) VALUES (1, 'Ljetna sezona', 1.5, '2025-07-01', '2025-08-31');
INSERT INTO price_tiers (unit_id, name, multiplier, start_date, end_date) VALUES (1, 'Zimska sezona', 1.3, '2025-12-15', '2026-01-15');
INSERT INTO price_tiers (unit_id, name, multiplier, start_date, end_date) VALUES (3, 'Ljetna sezona', 1.6, '2025-07-01', '2025-08-31');
INSERT INTO price_tiers (unit_id, name, multiplier, start_date, end_date) VALUES (5, 'Proljecna sezona', 1.2, '2025-04-01', '2025-05-31');

-- Kalendar dostupnosti (blokirani periodi)
INSERT INTO availability_calendar (unit_id, start_date, end_date, reason) VALUES (1, '2025-06-01', '2025-06-07', 'BOOKED');
INSERT INTO availability_calendar (unit_id, start_date, end_date, reason) VALUES (1, '2025-07-15', '2025-07-22', 'BOOKED');
INSERT INTO availability_calendar (unit_id, start_date, end_date, reason) VALUES (2, '2025-06-10', '2025-06-14', 'BOOKED');
INSERT INTO availability_calendar (unit_id, start_date, end_date, reason) VALUES (3, '2025-07-01', '2025-07-10', 'BOOKED');
INSERT INTO availability_calendar (unit_id, start_date, end_date, reason) VALUES (4, '2025-05-20', '2025-05-25', 'MAINTENANCE');