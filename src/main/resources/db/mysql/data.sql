-- One admin user, named admin1 with passwor 4dm1n and authority admin
INSERT IGNORE INTO users(username,password,enabled) VALUES ('admin1','admin',TRUE);
INSERT IGNORE INTO authorities VALUES ('admin1','admin');
-- One owner user, named owner1 with passwor 0wn3r
INSERT IGNORE INTO users(username,password,enabled) VALUES ('owner1','owner',TRUE);
INSERT IGNORE INTO users(username,password,enabled) VALUES ('owner2','owner',TRUE);
INSERT IGNORE INTO users(username,password,enabled) VALUES ('owner3','owner',TRUE);
INSERT IGNORE INTO users(username,password,enabled) VALUES ('owner4','owner',TRUE);
INSERT IGNORE INTO users(username,password,enabled) VALUES ('owner5','owner',TRUE);
INSERT IGNORE INTO users(username,password,enabled) VALUES ('owner6','owner',TRUE);
INSERT IGNORE INTO users(username,password,enabled) VALUES ('owner7','owner',TRUE);
INSERT IGNORE INTO users(username,password,enabled) VALUES ('owner8','owner',TRUE);
INSERT IGNORE INTO users(username,password,enabled) VALUES ('owner9','owner',TRUE);
INSERT IGNORE INTO users(username,password,enabled) VALUES ('owner10','owner',TRUE);
INSERT IGNORE INTO authorities VALUES ('owner1','owner');
-- One vet user, named vet1 with passwor v3t
INSERT IGNORE INTO users(username,password,enabled) VALUES ('vet1','v3t',TRUE);
INSERT IGNORE INTO authorities VALUES ('vet1','veterinarian');

INSERT IGNORE INTO vets VALUES (1, 'James', 'Carter');
INSERT IGNORE INTO vets VALUES (2, 'Helen', 'Leary');
INSERT IGNORE INTO vets VALUES (3, 'Linda', 'Douglas');
INSERT IGNORE INTO vets VALUES (4, 'Rafael', 'Ortega');
INSERT IGNORE INTO vets VALUES (5, 'Henry', 'Stevens');
INSERT IGNORE INTO vets VALUES (6, 'Sharon', 'Jenkins');

INSERT IGNORE INTO specialties VALUES (1, 'radiology');
INSERT IGNORE INTO specialties VALUES (2, 'surgery');
INSERT IGNORE INTO specialties VALUES (3, 'dentistry');

INSERT IGNORE INTO vet_specialties VALUES (2, 1);
INSERT IGNORE INTO vet_specialties VALUES (3, 2);
INSERT IGNORE INTO vet_specialties VALUES (3, 3);
INSERT IGNORE INTO vet_specialties VALUES (4, 2);
INSERT IGNORE INTO vet_specialties VALUES (5, 1);

INSERT IGNORE INTO types VALUES (1, 'cat');
INSERT IGNORE INTO types VALUES (2, 'dog');
INSERT IGNORE INTO types VALUES (3, 'lizard');
INSERT IGNORE INTO types VALUES (4, 'snake');
INSERT IGNORE INTO types VALUES (5, 'bird');
INSERT IGNORE INTO types VALUES (6, 'hamster');

INSERT IGNORE INTO owners(id,first_name,last_name,address,city,telephone,username) VALUES (1, 'George', 'Franklin', '110 W. Liberty St.', 'Madison', '6085551023', 'owner1');
INSERT IGNORE INTO owners(id,first_name,last_name,address,city,telephone,username) VALUES (2, 'Betty', 'Davis', '638 Cardinal Ave.', 'Sun Prairie', '6085551749', 'owner2');
INSERT IGNORE INTO owners(id,first_name,last_name,address,city,telephone,username) VALUES (3, 'Eduardo', 'Rodriquez', '2693 Commerce St.', 'McFarland', '6085558763', 'owner3');
INSERT IGNORE INTO owners(id,first_name,last_name,address,city,telephone,username) VALUES (4, 'Harold', 'Davis', '563 Friendly St.', 'Windsor', '6085553198', 'owner4');
INSERT IGNORE INTO owners(id,first_name,last_name,address,city,telephone,username) VALUES (5, 'Peter', 'McTavish', '2387 S. Fair Way', 'Madison', '6085552765', 'owner5');
INSERT IGNORE INTO owners(id,first_name,last_name,address,city,telephone,username) VALUES (6, 'Jean', 'Coleman', '105 N. Lake St.', 'Monona', '6085552654', 'owner6');
INSERT IGNORE INTO owners(id,first_name,last_name,address,city,telephone,username) VALUES (7, 'Jeff', 'Black', '1450 Oak Blvd.', 'Monona', '6085555387', 'owner7');
INSERT IGNORE INTO owners(id,first_name,last_name,address,city,telephone,username) VALUES (8, 'Maria', 'Escobito', '345 Maple St.', 'Madison', '6085557683', 'owner8');
INSERT IGNORE INTO owners(id,first_name,last_name,address,city,telephone,username) VALUES (9, 'David', 'Schroeder', '2749 Blackhawk Trail', 'Madison', '6085559435', 'owner9');
INSERT IGNORE INTO owners(id,first_name,last_name,address,city,telephone,username) VALUES (10, 'Carlos', 'Estaban', '2335 Independence La.', 'Waunakee', '6085555487', 'owner10');

INSERT IGNORE INTO pets(id,name,birth_date,type_id,owner_id) VALUES (1, 'Leo', '2010-09-07', 1, 1);
INSERT IGNORE INTO pets(id,name,birth_date,type_id,owner_id) VALUES (2, 'Basil', '2012-08-06', 6, 2);
INSERT IGNORE INTO pets(id,name,birth_date,type_id,owner_id) VALUES (3, 'Rosy', '2011-04-17', 2, 3);
INSERT IGNORE INTO pets(id,name,birth_date,type_id,owner_id) VALUES (4, 'Jewel', '2010-03-07', 2, 3);
INSERT IGNORE INTO pets(id,name,birth_date,type_id,owner_id) VALUES (5, 'Iggy', '2010-11-30', 3, 4);
INSERT IGNORE INTO pets(id,name,birth_date,type_id,owner_id) VALUES (6, 'George', '2010-01-20', 4, 5);
INSERT IGNORE INTO pets(id,name,birth_date,type_id,owner_id) VALUES (7, 'Samantha', '2012-09-04', 1, 6);
INSERT IGNORE INTO pets(id,name,birth_date,type_id,owner_id) VALUES (8, 'Max', '2012-09-04', 1, 6);
INSERT IGNORE INTO pets(id,name,birth_date,type_id,owner_id) VALUES (9, 'Lucky', '2011-08-06', 5, 7);
INSERT IGNORE INTO pets(id,name,birth_date,type_id,owner_id) VALUES (10, 'Mulligan', '2007-02-24', 2, 8);
INSERT IGNORE INTO pets(id,name,birth_date,type_id,owner_id) VALUES (11, 'Freddy', '2010-03-09', 5, 9);
INSERT IGNORE INTO pets(id,name,birth_date,type_id,owner_id) VALUES (12, 'Lucky', '2010-06-24', 2, 10);
INSERT IGNORE INTO pets(id,name,birth_date,type_id,owner_id) VALUES (13, 'Sly', '2012-06-08', 1, 10);

INSERT IGNORE INTO visits(id,pet_id,visit_date,description) VALUES (1, 7, '2013-01-01', 'rabies shot');
INSERT IGNORE INTO visits(id,pet_id,visit_date,description) VALUES (2, 8, '2013-01-02', 'rabies shot');
INSERT IGNORE INTO visits(id,pet_id,visit_date,description) VALUES (3, 8, '2013-01-03', 'neutered');
INSERT IGNORE INTO visits(id,pet_id,visit_date,description) VALUES (4, 7, '2013-01-04', 'spayed');

INSERT IGNORE INTO beauty_solutions(id,title,type_id,vet_id,enabled,price) VALUES (1, 'Nail clipping', 1, 1, true, 10.0);
INSERT IGNORE INTO beauty_solutions(id,title,type_id,vet_id,enabled,price) VALUES (2, 'Full haircut', 1, 1, true, 10.0);
INSERT IGNORE INTO beauty_solutions(id,title,type_id,vet_id,enabled,price) VALUES (3, 'Modern haircut', 1, 1, true, 10.0);
INSERT IGNORE INTO beauty_solutions(id,title,type_id,vet_id,enabled,price) VALUES (4, 'Nail clipping', 2, 2, true, 30.0);
INSERT IGNORE INTO beauty_solutions(id,title,type_id,vet_id,enabled,price) VALUES (5, 'Nail clipping', 3, 1, true, 0.0);
INSERT IGNORE INTO beauty_solutions(id,title,type_id,vet_id,enabled,price) VALUES (6, 'Nail clipping', 4, 1, false, 99999.0);

INSERT IGNORE INTO beauty_solution_visits(id,beauty_solution_id,pet_id,date,final_price,cancelled) VALUES (1, 1, 1, '2013-01-01 22:00:00', 10.0, false);

