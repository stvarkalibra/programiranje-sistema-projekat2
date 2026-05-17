-- ================================================
-- Gosti
-- ================================================
INSERT INTO guest (first_name, last_name, email, phone_number) VALUES
    ('Marko',   'Nikolić',    'marko.nikolic@gmail.com',    '+381601234567'),
    ('Ana',     'Jovanović',  'ana.jovanovic@gmail.com',    '+381602345678'),
    ('Stefan',  'Petrović',   'stefan.petrovic@gmail.com',  '+381603456789'),
    ('Jelena',  'Mihajlović', 'jelena.mihajlovic@gmail.com','+381604567890'),
    ('Nikola',  'Stojanović', 'nikola.stojanovic@gmail.com','+381605678901');

-- ================================================
-- Politike otkazivanja
-- ================================================
INSERT INTO cancellation_policy (policy_name, refund_percentage) VALUES
    ('Besplatni otkaz (do 48h pre dolaska)', 100),
    ('Delimičan povrat (50%)',               50),
    ('Bez povrata',                          0);

-- ================================================
-- Rezervacije
-- ================================================
INSERT INTO booking (unit_id, guest_id, cancellation_policy_id, start_date, end_date, total_price, status) VALUES
    (3, 1, 1, '2026-04-10', '2026-04-15', 25000.00, 'CONFIRMED'),   -- id=1 Marko, apartman 3
    (7, 2, 2, '2026-03-20', '2026-03-25', 18000.00, 'COMPLETED'),   -- id=2 Ana, apartman 7
    (1, 3, 1, '2026-05-01', '2026-05-07', 42000.00, 'PENDING'),     -- id=3 Stefan, apartman 1
    (5, 4, 3, '2026-02-14', '2026-02-17', 12000.00, 'CANCELLED'),   -- id=4 Jelena, apartman 5
    (2, 1, 2, '2026-06-01', '2026-06-10', 63000.00, 'CONFIRMED'),   -- id=5 Marko, apartman 2
    (9, 5, 1, '2026-03-01', '2026-03-05', 15000.00, 'COMPLETED');   -- id=6 Nikola, apartman 9

-- ================================================
-- Dodatne usluge po rezervacijama
-- ================================================
INSERT INTO add_on_item (booking_id, service_name, price) VALUES
    (1, 'Doručak',           2500.00),
    (1, 'Transfer aerodrom', 3000.00),
    (2, 'Spa tretman',       4500.00),
    (5, 'Doručak',           2500.00),
    (5, 'Ručak',             3500.00),
    (5, 'Večera',            4000.00),
    (6, 'Doručak',           2500.00);

-- ================================================
-- Log promena statusa rezervacija
-- ================================================
INSERT INTO booking_status_log (booking_id, old_status, new_status, changed_date) VALUES
    (1, 'PENDING',    'CONFIRMED',  '2026-04-01 10:15:00'),
    (2, 'PENDING',    'CONFIRMED',  '2026-03-18 09:30:00'),
    (2, 'CONFIRMED',  'COMPLETED',  '2026-03-25 12:00:00'),
    (3, 'PENDING',    'PENDING',    '2026-04-20 14:00:00'),
    (4, 'PENDING',    'CONFIRMED',  '2026-02-10 11:00:00'),
    (4, 'CONFIRMED',  'CANCELLED',  '2026-02-12 16:45:00'),
    (5, 'PENDING',    'CONFIRMED',  '2026-05-25 08:00:00'),
    (6, 'PENDING',    'CONFIRMED',  '2026-02-27 13:20:00'),
    (6, 'CONFIRMED',  'COMPLETED',  '2026-03-05 11:00:00');

-- Rezervacije koje treba da se otkažu kada se obriše TimeSlot 1:
-- TimeSlot 1 = 2026-06-01 10:00:00 do 2026-06-01 14:00:00

INSERT INTO booking (unit_id, guest_id, cancellation_policy_id, start_date, end_date, total_price, status) VALUES
                                                                                                               (99, 1, 1, '2026-06-01 10:00:00', '2026-06-01 14:00:00', 12000.00, 'CONFIRMED'),
                                                                                                               (88, 2, 1, '2026-06-01 11:00:00', '2026-06-01 13:00:00', 8000.00, 'PENDING'),
                                                                                                               (77, 3, 2, '2026-06-01 12:00:00', '2026-06-01 13:30:00', 6000.00, 'CONFIRMED');

-- Rezervacije koje NE treba da se otkažu:
INSERT INTO booking (unit_id, guest_id, cancellation_policy_id, start_date, end_date, total_price, status) VALUES
                                                                                                               (66, 4, 1, '2026-06-01 15:00:00', '2026-06-01 16:00:00', 5000.00, 'CONFIRMED'),
                                                                                                               (55, 5, 1, '2026-06-02 10:00:00', '2026-06-02 14:00:00', 7000.00, 'CONFIRMED');
