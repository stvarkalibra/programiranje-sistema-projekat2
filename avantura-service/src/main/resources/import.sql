-- ADVENTURE_CATEGORY (15 torki)
INSERT INTO adventure_category (name, description) VALUES ('Water Sports', 'All water-based adventures');
INSERT INTO adventure_category (name, description) VALUES ('Mountain Hiking', 'Challenging hikes in mountains');
INSERT INTO adventure_category (name, description) VALUES ('City Tours', 'Explore cities with guides');
INSERT INTO adventure_category (name, description) VALUES ('Extreme Sports', 'High adrenaline experiences');
INSERT INTO adventure_category (name, description) VALUES ('Camping', 'Nature overnight adventures');
INSERT INTO adventure_category (name, description) VALUES ('Caving', 'Explore caves safely');
INSERT INTO adventure_category (name, description) VALUES ('Safari', 'Wildlife adventures');
INSERT INTO adventure_category (name, description) VALUES ('Cycling', 'Adventure on bicycles');
INSERT INTO adventure_category (name, description) VALUES ('Skiing', 'Snow and ski adventures');
INSERT INTO adventure_category (name, description) VALUES ('Kayaking', 'Paddling and kayaking experiences');
INSERT INTO adventure_category (name, description) VALUES ('Paragliding', 'Flying over landscapes');
INSERT INTO adventure_category (name, description) VALUES ('Rock Climbing', 'Scaling rock walls');
INSERT INTO adventure_category (name, description) VALUES ('Fishing', 'Fishing in rivers and lakes');
INSERT INTO adventure_category (name, description) VALUES ('Horse Riding', 'Equestrian adventures');
INSERT INTO adventure_category (name, description) VALUES ('Cultural Trips', 'Learn and explore cultures');

-- ADVENTURE (DODAT GUIDE_ID ZA TESTIRANJE)
-- Vodič 1 (Marko): 2 avanture, ukupna cena 200, nije overload
INSERT INTO adventure (title, description, difficulty_level, base_price, adventure_category_id, guide_id) VALUES ('Tara Rafting', 'Wild river rafting', 'Hard', 120.0, 1, 1);
INSERT INTO adventure (title, description, difficulty_level, base_price, adventure_category_id, guide_id) VALUES ('Durmitor Trek', 'Peak climbing', 'Hard', 80.0, 2, 1);

-- Vodič 3 (Igor): 4 avanture, 3 su EXTREME -> OVO ĆE BITI OVERLOAD
INSERT INTO adventure (title, description, difficulty_level, base_price, adventure_category_id, guide_id) VALUES ('Bungee Bridge', 'High jump', 'Extreme', 150.0, 4, 3);
INSERT INTO adventure (title, description, difficulty_level, base_price, adventure_category_id, guide_id) VALUES ('Tara Flight', 'Paragliding Tara', 'Extreme', 200.0, 11, 3);
INSERT INTO adventure (title, description, difficulty_level, base_price, adventure_category_id, guide_id) VALUES ('Extreme Caving', 'Deep exploration', 'Extreme', 300.0, 6, 3);
INSERT INTO adventure (title, description, difficulty_level, base_price, adventure_category_id, guide_id) VALUES ('Iron Gate Cycle', 'Danube cycling', 'Hard', 90.0, 8, 3);

-- Ostali (razbacani guide_id od 5 do 15)
INSERT INTO adventure (title, description, difficulty_level, base_price, adventure_category_id, guide_id) VALUES ('Belgrade Underground', 'History tour', 'Easy', 30.0, 3, 6);
INSERT INTO adventure (title, description, difficulty_level, base_price, adventure_category_id, guide_id) VALUES ('Berlin Underground', 'Aquarium tour', 'Easy', 80.0, 1, 6);
INSERT INTO adventure (title, description, difficulty_level, base_price, adventure_category_id, guide_id) VALUES ('Zlatibor Camp', 'Star gazing', 'Medium', 60.0, 5, 5);
INSERT INTO adventure (title, description, difficulty_level, base_price, adventure_category_id, guide_id) VALUES ('Resava Cave', 'Cave exploration', 'Medium', 40.0, 6, 2);
INSERT INTO adventure (title, description, difficulty_level, base_price, adventure_category_id, guide_id) VALUES ('Serengeti Watch', 'Lion watching', 'Easy', 500.0, 7, 7);
INSERT INTO adventure (title, description, difficulty_level, base_price, adventure_category_id, guide_id) VALUES ('Kopaonik Ski', 'Winter fun', 'Medium', 110.0, 9, 9);
INSERT INTO adventure (title, description, difficulty_level, base_price, adventure_category_id, guide_id) VALUES ('Drina Kayak', 'Peaceful rowing', 'Easy', 55.0, 10, 10);
INSERT INTO adventure (title, description, difficulty_level, base_price, adventure_category_id, guide_id) VALUES ('Stol Climb', 'Free climbing', 'Hard', 75.0, 12, 12);
INSERT INTO adventure (title, description, difficulty_level, base_price, adventure_category_id, guide_id) VALUES ('Danube Fishing', 'Big fish hunt', 'Easy', 40.0, 13, 13);
INSERT INTO adventure (title, description, difficulty_level, base_price, adventure_category_id, guide_id) VALUES ('Fruska Gora Ride', 'Horse riding', 'Medium', 65.0, 14, 14);

-- GEAR_REQUIREMENT (15 torki - mapirano na avanture 1-15)
INSERT INTO gear_requirement (item_name, is_mandatory, adventure_id) VALUES ('Neoprene Suit', TRUE, 1);
INSERT INTO gear_requirement (item_name, is_mandatory, adventure_id) VALUES ('Helmet', TRUE, 1);
INSERT INTO gear_requirement (item_name, is_mandatory, adventure_id) VALUES ('GoPro', FALSE, 1);
INSERT INTO gear_requirement (item_name, is_mandatory, adventure_id) VALUES ('Hiking Boots', TRUE, 2);
INSERT INTO gear_requirement (item_name, is_mandatory, adventure_id) VALUES ('Flashlight', FALSE, 3);
INSERT INTO gear_requirement (item_name, is_mandatory, adventure_id) VALUES ('Harness', TRUE, 4);
INSERT INTO gear_requirement (item_name, is_mandatory, adventure_id) VALUES ('Tent', TRUE, 5);
INSERT INTO gear_requirement (item_name, is_mandatory, adventure_id) VALUES ('Helmet', TRUE, 6);
INSERT INTO gear_requirement (item_name, is_mandatory, adventure_id) VALUES ('Binoculars', FALSE, 7);
INSERT INTO gear_requirement (item_name, is_mandatory, adventure_id) VALUES ('Repair Kit', TRUE, 8);
INSERT INTO gear_requirement (item_name, is_mandatory, adventure_id) VALUES ('Skis', TRUE, 9);
INSERT INTO gear_requirement (item_name, is_mandatory, adventure_id) VALUES ('Life Jacket', TRUE, 10);
INSERT INTO gear_requirement (item_name, is_mandatory, adventure_id) VALUES ('Gloves', TRUE, 11);
INSERT INTO gear_requirement (item_name, is_mandatory, adventure_id) VALUES ('Rope', TRUE, 12);
INSERT INTO gear_requirement (item_name, is_mandatory, adventure_id) VALUES ('Fishing Rod', TRUE, 13);
INSERT INTO gear_requirement (item_name, is_mandatory, adventure_id) VALUES ('Boots', TRUE, 14);
INSERT INTO gear_requirement (item_name, is_mandatory, adventure_id) VALUES ('Notebook', FALSE, 15);

-- TIME_SLOT (15 torki - termini za 2026. godinu)
INSERT INTO time_slot (start_time, end_time, max_participants, current_occupancy, adventure_id) VALUES ('2026-06-01T10:00:00', '2026-06-01T14:00:00', 12, 3, 1);
INSERT INTO time_slot (start_time, end_time, max_participants, current_occupancy, adventure_id) VALUES ('2026-06-02T08:00:00', '2026-06-02T16:00:00', 8, 8, 2);
INSERT INTO time_slot (start_time, end_time, max_participants, current_occupancy, adventure_id) VALUES ('2026-06-03T18:00:00', '2026-06-03T20:00:00', 20, 20, 3);
INSERT INTO time_slot (start_time, end_time, max_participants, current_occupancy, adventure_id) VALUES ('2026-06-04T12:00:00', '2026-06-04T13:00:00', 2, 2, 4);
INSERT INTO time_slot (start_time, end_time, max_participants, current_occupancy, adventure_id) VALUES ('2026-06-05T19:00:00', '2026-06-07T10:00:00', 10, 4, 5);
INSERT INTO time_slot (start_time, end_time, max_participants, current_occupancy, adventure_id) VALUES ('2026-06-06T10:00:00', '2026-06-06T13:00:00', 15, 15, 6);
INSERT INTO time_slot (start_time, end_time, max_participants, current_occupancy, adventure_id) VALUES ('2026-06-07T05:00:00', '2026-06-07T18:00:00', 6, 2, 7);
INSERT INTO time_slot (start_time, end_time, max_participants, current_occupancy, adventure_id) VALUES ('2026-06-08T09:00:00', '2026-06-08T17:00:00', 12, 5, 8);
INSERT INTO time_slot (start_time, end_time, max_participants, current_occupancy, adventure_id) VALUES ('2026-12-15T09:00:00', '2026-12-15T16:00:00', 25, 1, 9);
INSERT INTO time_slot (start_time, end_time, max_participants, current_occupancy, adventure_id) VALUES ('2026-07-01T10:00:00', '2026-07-01T14:00:00', 10, 0, 10);
INSERT INTO time_slot (start_time, end_time, max_participants, current_occupancy, adventure_id) VALUES ('2026-07-02T11:00:00', '2026-07-02T13:00:00', 4, 0, 11);
INSERT INTO time_slot (start_time, end_time, max_participants, current_occupancy, adventure_id) VALUES ('2026-07-03T08:00:00', '2026-07-03T15:00:00', 5, 3, 12);
INSERT INTO time_slot (start_time, end_time, max_participants, current_occupancy, adventure_id) VALUES ('2026-07-04T06:00:00', '2026-07-04T12:00:00', 8, 8, 13);
INSERT INTO time_slot (start_time, end_time, max_participants, current_occupancy, adventure_id) VALUES ('2026-07-05T09:00:00', '2026-07-05T12:00:00', 10, 6, 14);
INSERT INTO time_slot (start_time, end_time, max_participants, current_occupancy, adventure_id) VALUES ('2026-07-06T10:00:00', '2026-07-06T14:00:00', 30, 7, 15);

-- ADVENTURE_REVIEW (15 torki)
INSERT INTO adventure_review (rating, comment, created_at, adventure_id) VALUES (5, 'Amazing!', '2026-03-01T12:00:00', 1);
INSERT INTO adventure_review (rating, comment, created_at, adventure_id) VALUES (4, 'Amazing!', '2026-03-01T12:00:00', 1);
INSERT INTO adventure_review (rating, comment, created_at, adventure_id) VALUES (2, 'Amazing!', '2026-03-01T12:00:00', 1);
INSERT INTO adventure_review (rating, comment, created_at, adventure_id) VALUES (4, 'Amazing!', '2026-03-01T12:00:00', 1);
INSERT INTO adventure_review (rating, comment, created_at, adventure_id) VALUES (4, 'Good', '2026-03-02T12:00:00', 2);
INSERT INTO adventure_review (rating, comment, created_at, adventure_id) VALUES (3, 'OK', '2026-03-03T12:00:00', 3);
INSERT INTO adventure_review (rating, comment, created_at, adventure_id) VALUES (5, 'Scary but fun', '2026-03-04T12:00:00', 4);
INSERT INTO adventure_review (rating, comment, created_at, adventure_id) VALUES (4, 'Cold night', '2026-03-05T12:00:00', 5);
INSERT INTO adventure_review (rating, comment, created_at, adventure_id) VALUES (5, 'Deep!', '2026-03-06T12:00:00', 6);
INSERT INTO adventure_review (rating, comment, created_at, adventure_id) VALUES (5, 'Saw a lion', '2026-03-07T12:00:00', 7);
INSERT INTO adventure_review (rating, comment, created_at, adventure_id) VALUES (2, 'Too hard', '2026-03-08T12:00:00', 8);
INSERT INTO adventure_review (rating, comment, created_at, adventure_id) VALUES (4, 'Nice snow', '2026-03-09T12:00:00', 9);
INSERT INTO adventure_review (rating, comment, created_at, adventure_id) VALUES (5, 'Relaxing', '2026-03-10T12:00:00', 10);
INSERT INTO adventure_review (rating, comment, created_at, adventure_id) VALUES (5, 'High up', '2026-03-11T12:00:00', 11);
INSERT INTO adventure_review (rating, comment, created_at, adventure_id) VALUES (4, 'Sturdy rocks', '2026-03-12T12:00:00', 12);
INSERT INTO adventure_review (rating, comment, created_at, adventure_id) VALUES (3, 'No fish today', '2026-03-13T12:00:00', 13);
INSERT INTO adventure_review (rating, comment, created_at, adventure_id) VALUES (5, 'Love horses', '2026-03-14T12:00:00', 14);
INSERT INTO adventure_review (rating, comment, created_at, adventure_id) VALUES (4, 'Very educational', '2026-03-15T12:00:00', 15);