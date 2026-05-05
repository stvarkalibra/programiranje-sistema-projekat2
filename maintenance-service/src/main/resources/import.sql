-- Radnici
INSERT INTO workers (first_name, last_name, specialty) VALUES ('Edward', 'Elric', 'ELECTRICIAN');
INSERT INTO workers (first_name, last_name, specialty) VALUES ('Christina', 'Makise', 'CLEANER');
INSERT INTO workers (first_name, last_name, specialty) VALUES ('Eren', 'Yeager', 'PLUMBER');
INSERT INTO workers (first_name, last_name, specialty) VALUES ('Satoru', 'Gojo', 'CLEANER');
INSERT INTO workers (first_name, last_name, specialty) VALUES ('Light', 'Yagami', 'HVAC');
INSERT INTO workers (first_name, last_name, specialty) VALUES ('Joseph', 'Joestar', 'HANDYMAN');

-- Tipovi zadataka
INSERT INTO maintenance_tasks (task_name, estimated_duration_hours) VALUES ('Generalno ciscenje', 3);
INSERT INTO maintenance_tasks (task_name, estimated_duration_hours) VALUES ('Popravka klime', 4);
INSERT INTO maintenance_tasks (task_name, estimated_duration_hours) VALUES ('Zamjena elektricnih osiguraca', 2);
INSERT INTO maintenance_tasks (task_name, estimated_duration_hours) VALUES ('Popravka vodovodnih cijevi', 5);
INSERT INTO maintenance_tasks (task_name, estimated_duration_hours) VALUES ('Kosilje i uredjenje okoline', 2);
INSERT INTO maintenance_tasks (task_name, estimated_duration_hours) VALUES ('Dezinfekcija prostora', 4);
INSERT INTO maintenance_tasks (task_name, estimated_duration_hours) VALUES ('Kontrola i servis bojlera', 3);

-- Radni nalozi
INSERT INTO work_orders (unit_id, worker_id, task_id, status, scheduled_for, completed_at) VALUES (1, 2, 1, 'COMPLETED', '2025-05-10T09:00:00', '2025-05-10T12:00:00');
INSERT INTO work_orders (unit_id, worker_id, task_id, status, scheduled_for, completed_at) VALUES (1, 1, 3, 'COMPLETED', '2025-05-11T10:00:00', '2025-05-11T12:00:00');
INSERT INTO work_orders (unit_id, worker_id, task_id, status, scheduled_for, completed_at) VALUES (2, 4, 1, 'COMPLETED', '2025-05-12T08:00:00', '2025-05-12T11:00:00');
INSERT INTO work_orders (unit_id, worker_id, task_id, status, scheduled_for, completed_at) VALUES (3, 5, 2, 'IN_PROGRESS', '2025-05-20T09:00:00', NULL);
INSERT INTO work_orders (unit_id, worker_id, task_id, status, scheduled_for, completed_at) VALUES (3, 3, 4, 'PENDING', '2025-05-22T10:00:00', NULL);
INSERT INTO work_orders (unit_id, worker_id, task_id, status, scheduled_for, completed_at) VALUES (4, 2, 6, 'PENDING', '2025-05-25T08:00:00', NULL);
INSERT INTO work_orders (unit_id, worker_id, task_id, status, scheduled_for, completed_at) VALUES (5, 6, 5, 'PENDING', '2025-05-26T09:00:00', NULL);
INSERT INTO work_orders (unit_id, worker_id, task_id, status, scheduled_for, completed_at) VALUES (1, 5, 7, 'PENDING', '2025-06-01T10:00:00', NULL);

-- Historija odrzavanja
INSERT INTO maintenance_logs (unit_id, task_description, worker_name, completed_at, notes) VALUES (1, 'Generalno ciscenje objekta', 'Ana Jovanovic', '2025-04-15T12:00:00', 'Ocisceni svi prostori ukljucujuci terasu i kupatilo.');
INSERT INTO maintenance_logs (unit_id, task_description, worker_name, completed_at, notes) VALUES (1, 'Zamjena elektricnih osiguraca', 'Marko Petrovic', '2025-05-11T12:00:00', 'Zamijenjeni osiguraci 16A i 25A. Preporucuje se inspekcija za 6 mjeseci.');
INSERT INTO maintenance_logs (unit_id, task_description, worker_name, completed_at, notes) VALUES (2, 'Generalno ciscenje objekta', 'Jelena Markovic', '2025-05-12T11:00:00', 'Dubinsko ciscenje svих povrsina i zamjena posteljine.');
INSERT INTO maintenance_logs (unit_id, task_description, worker_name, completed_at, notes) VALUES (3, 'Kontrola vodovodne instalacije', 'Stefan Nikolic', '2025-04-20T15:00:00', 'Pronadjena manja curenja u kupatilu. Zaptiveni svi spojevi.');
INSERT INTO maintenance_logs (unit_id, task_description, worker_name, completed_at, notes) VALUES (4, 'Uredjenje okoline i kosilje', 'Milica Stojanovic', '2025-04-25T13:00:00', 'Pokosena trava oko objekta i ociscene staze.');