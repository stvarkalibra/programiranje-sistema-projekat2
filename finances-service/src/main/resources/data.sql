-- ================================================
-- Poreske stope
-- ================================================
INSERT INTO tax_rate (tax_name, percentage) VALUES
    ('PDV 20%',   20.0),
    ('PDV 10%',   10.0),
    ('Bez PDV-a',  0.0);

-- ================================================
-- Načini plaćanja
-- ================================================
INSERT INTO payment_method (method_name, is_active) VALUES
    ('Gotovina',              true),
    ('Kreditna kartica',      true),
    ('Bankovna transakcija',  true),
    ('PayPal',                false);

-- ================================================
-- Fakture (booking_id referenciše rezervacije-service)
--   subtotal  = osnova
--   total_amount = subtotal * (1 + percentage/100)
-- ================================================
INSERT INTO invoice (booking_id, tax_rate_id, arrival_date, subtotal, total_amount, status) VALUES
    (2, 1, '2026-03-25', 18000.00, 21600.00, 'PAID'),       -- id=1 Ana,    PDV 20%
    (4, 3, '2026-02-17', 12000.00, 12000.00, 'CANCELLED'),  -- id=2 Jelena, bez PDV
    (6, 2, '2026-03-05', 15000.00, 16500.00, 'PAID'),       -- id=3 Nikola, PDV 10%
    (1, 1, '2026-04-15', 25000.00, 30000.00, 'UNPAID'),     -- id=4 Marko,  PDV 20%
    (5, 1, '2026-06-10', 63000.00, 75600.00, 'UNPAID'),     -- id=5 Marko,  PDV 20%
    (3, 2, '2026-05-07', 42000.00, 46200.00, 'OVERDUE');    -- id=6 Stefan, PDV 10%

-- ================================================
-- Transakcije (samo za plaćene fakture + delimično plaćena)
-- ================================================
INSERT INTO "transaction" (invoice_id, payment_method_id, amount, transaction_date) VALUES
    (1, 2, 21600.00, '2026-03-25 12:30:00'),  -- Ana platila kreditnom karticom
    (3, 1, 16500.00, '2026-03-05 14:15:00'),  -- Nikola platio gotovinom
    (6, 3, 20000.00, '2026-04-01 09:00:00');  -- Stefan delimično platio (bankovni prenos) -> OVERDUE
