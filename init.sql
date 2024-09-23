DROP
    SCHEMA IF EXISTS pae CASCADE;
CREATE
    SCHEMA pae;

-- Users
CREATE TABLE pae.users
(
    id                SERIAL PRIMARY KEY,
    first_name        TEXT NOT NULL,
    last_name         TEXT NOT NULL,
    password          TEXT NOT NULL,
    phone_number      TEXT NOT NULL,
    email             TEXT NOT NULL,
    registration_date DATE NOT NULL,
    role              TEXT NOT NULL,
    academic_year     TEXT NOT NULL,
    version           INT  NOT NULL
);

-- Companies
CREATE TABLE pae.companies
(
    id               SERIAL PRIMARY KEY,
    name             TEXT    NOT NULL,
    designation      TEXT,
    address          TEXT    NOT NULL,
    email            TEXT,
    phone_number     TEXT,
    is_blacklisted   BOOLEAN NOT NULL,
    blacklist_reason TEXT,
    version          INT     NOT NULL
);

-- Contacts
CREATE TABLE pae.contacts
(
    id             SERIAL PRIMARY KEY,
    state          TEXT NOT NULL,
    meeting_place  TEXT,
    refusal_reason TEXT,
    academic_year  TEXT NOT NULL,
    version        INT  NOT NULL,
    student        INT REFERENCES pae.users (id),
    company        INT REFERENCES pae.companies (id)
);

-- Managers
CREATE TABLE pae.supervisors
(
    id           SERIAL PRIMARY KEY,
    first_name   TEXT NOT NULL,
    last_name    TEXT NOT NULL,
    phone_number TEXT NOT NULL,
    email        TEXT,
    version      INT  NOT NULL,
    company      INT REFERENCES pae.companies (id)
);

-- Internships
CREATE TABLE pae.internships
(
    id             SERIAL PRIMARY KEY,
    student        INT REFERENCES pae.users (id),
    supervisor     INT REFERENCES pae.supervisors (id),
    contact        INT REFERENCES pae.contacts (id),
    subject        TEXT,
    signature_date DATE NOT NULL,
    academic_year  TEXT NOT NULL,
    version        INT  NOT NULL
);

-- INSERTS

-- Inserts pour pae.users
INSERT INTO pae.users (first_name, last_name, password, phone_number, email, role,
                       registration_date, academic_year,
                       version)
VALUES ('Raphaël', 'Baroni', '$2a$10$1sU9o.oivpDZGGRioM75oupQ98Jj4skR0lQQ1/5uhv2fsrJrHIF1a',
        '0481010101', 'raphael.baroni@vinci.be', 'PROFESSEUR', '2020-09-21', '2020-2021', 1),
       ('Brigitte', 'Lehmann', '$2a$10$1sU9o.oivpDZGGRioM75oupQ98Jj4skR0lQQ1/5uhv2fsrJrHIF1a',
        '0482020202', 'brigitte.lehmann@vinci.be', 'PROFESSEUR', '2020-09-21', '2020-2021', 1),
       ('Laurent', 'Leleux', '$2a$10$1sU9o.oivpDZGGRioM75oupQ98Jj4skR0lQQ1/5uhv2fsrJrHIF1a',
        '0483030303', 'laurent.leleux@vinci.be', 'PROFESSEUR', '2020-09-21', '2020-2021', 1),
       ('Annouck', 'Lancaster', '$2a$10$j3FDGkuBQMXkptA9X8Ac4OHNXbKEvlLtnpK3rXt7FSo1OjoDE.F.K',
        '0484040404', 'annouck.lancaster@vinci.be', 'ADMINISTRATIF', '2020-09-21', '2020-2021', 1),
       ('Elle', 'Skile', '$2a$10$CQ6vifKn7flk9DlFS3IvLeznNYzr0fyiEOdS.VtUpf6K1de2lqIXm',
        '0491000001', 'elle.skile@student.vinci.be', 'ETUDIANT', '2021-09-21', '2021-2022', 1),
       ('Basile', 'Ilotie', '$2a$10$CQ6vifKn7flk9DlFS3IvLeznNYzr0fyiEOdS.VtUpf6K1de2lqIXm',
        '0491000011', 'basile.Ilotie@student.vinci.be', 'ETUDIANT', '2021-09-21', '2021-2022', 1),
       ('Basile', 'Frilot', '$2a$10$CQ6vifKn7flk9DlFS3IvLeznNYzr0fyiEOdS.VtUpf6K1de2lqIXm',
        '0491000021', 'basile.frilot@student.vinci.be', 'ETUDIANT', '2021-09-21', '2021-2022', 1),
       ('Basile', 'Ilot', '$2a$10$CQ6vifKn7flk9DlFS3IvLeznNYzr0fyiEOdS.VtUpf6K1de2lqIXm',
        '0492000001', 'basile.Ilot@student.vinci.be', 'ETUDIANT', '2021-09-21', '2021-2022', 1),
       ('Arnaud', 'Dito', '$2a$10$CQ6vifKn7flk9DlFS3IvLeznNYzr0fyiEOdS.VtUpf6K1de2lqIXm',
        '0493000001', 'arnaud.dito@student.vinci.be', 'ETUDIANT', '2021-09-21', '2021-2022', 1),
       ('Arnaud', 'Dilo', '$2a$10$CQ6vifKn7flk9DlFS3IvLeznNYzr0fyiEOdS.VtUpf6K1de2lqIXm',
        '0494000001', 'arnaud.dilo@student.vinci.be', 'ETUDIANT', '2021-09-21', '2021-2022', 1),
       ('Cedric', 'Dilot', '$2a$10$CQ6vifKn7flk9DlFS3IvLeznNYzr0fyiEOdS.VtUpf6K1de2lqIXm',
        '0495000001', 'cedric.dilot@student.vinci.be', 'ETUDIANT', '2021-09-21', '2021-2022', 1),
       ('Auristelle', 'Linot', '$2a$10$CQ6vifKn7flk9DlFS3IvLeznNYzr0fyiEOdS.VtUpf6K1de2lqIXm',
        '0496000001', 'auristelle.linot@student.vinci.be', 'ETUDIANT', '2021-09-21', '2021-2022',
        1),
       ('Basile', 'Demoulin', '$2a$10$CQ6vifKn7flk9DlFS3IvLeznNYzr0fyiEOdS.VtUpf6K1de2lqIXm',
        '0496000002', 'basile.demoulin@student.vinci.be', 'ETUDIANT', '2022-09-23', '2022-2023', 1),
       ('Arthur', 'Moulin', '$2a$10$CQ6vifKn7flk9DlFS3IvLeznNYzr0fyiEOdS.VtUpf6K1de2lqIXm',
        '0497000002', 'arthur.moulin@student.vinci.be', 'ETUDIANT', '2022-09-23', '2022-2023', 1),
       ('Hugo', 'Moulin', '$2a$10$CQ6vifKn7flk9DlFS3IvLeznNYzr0fyiEOdS.VtUpf6K1de2lqIXm',
        '0497000003', 'hugo.moulin@student.vinci.be', 'ETUDIANT', '2022-09-23', '2022-2023', 1),
       ('Jeremy', 'Demoulin', '$2a$10$CQ6vifKn7flk9DlFS3IvLeznNYzr0fyiEOdS.VtUpf6K1de2lqIXm',
        '0497000020', 'jeremy.demoulin@student.vinci.be', 'ETUDIANT', '2022-09-23', '2022-2023', 1),
       ('Aurèle', 'Mile', '$2a$10$CQ6vifKn7flk9DlFS3IvLeznNYzr0fyiEOdS.VtUpf6K1de2lqIXm',
        '0497000021', 'aurele.mile@student.vinci.be', 'ETUDIANT', '2022-09-23', '2022-2023', 1),
       ('Frank', 'Mile', '$2a$10$CQ6vifKn7flk9DlFS3IvLeznNYzr0fyiEOdS.VtUpf6K1de2lqIXm',
        '0497000075', 'frank.mile@student.vinci.be', 'ETUDIANT', '2022-09-27', '2022-2023', 1),
       ('Basile', 'Dumoulin', '$2a$10$CQ6vifKn7flk9DlFS3IvLeznNYzr0fyiEOdS.VtUpf6K1de2lqIXm',
        '0497000058', 'basile.dumoulin@student.vinci.be', 'ETUDIANT', '2022-09-27', '2022-2023', 1),
       ('Axel', 'Dumoulin', '$2a$10$CQ6vifKn7flk9DlFS3IvLeznNYzr0fyiEOdS.VtUpf6K1de2lqIXm',
        '0497000097', 'axel.dumoulin@student.vinci.be', 'ETUDIANT', '2022-09-27', '2022-2023', 1),
       ('Caroline', 'Line', '$2a$10$CQ6vifKn7flk9DlFS3IvLeznNYzr0fyiEOdS.VtUpf6K1de2lqIXm',
        '0486000001', 'caroline.line@student.vinci.be', 'ETUDIANT', '2023-09-18', '2023-2024', 1),
       ('Achille', 'Ile', '$2a$10$CQ6vifKn7flk9DlFS3IvLeznNYzr0fyiEOdS.VtUpf6K1de2lqIXm',
        '0487000001', 'ach.ile@student.vinci.be', 'ETUDIANT', '2023-09-18', '2023-2024', 1),
       ('Basile', 'Ile', '$2a$10$CQ6vifKn7flk9DlFS3IvLeznNYzr0fyiEOdS.VtUpf6K1de2lqIXm',
        '0488000001', 'basile.ile@student.vinci.be', 'ETUDIANT', '2023-09-18', '2023-2024', 1),
       ('Achille', 'Skile', '$2a$10$CQ6vifKn7flk9DlFS3IvLeznNYzr0fyiEOdS.VtUpf6K1de2lqIXm',
        '0490000001', 'achille.skile@student.vinci.be', 'ETUDIANT', '2023-09-18', '2023-2024', 1),
       ('Carole', 'Skile', '$2a$10$CQ6vifKn7flk9DlFS3IvLeznNYzr0fyiEOdS.VtUpf6K1de2lqIXm',
        '0489000001', 'carole.skile@student.vinci.be', 'ETUDIANT', '2023-09-18', '2023-2024', 1),
       ('Théophile', 'Ile', '$2a$10$CQ6vifKn7flk9DlFS3IvLeznNYzr0fyiEOdS.VtUpf6K1de2lqIXm',
        '0488353389', 'theophile.ile@student.vinci.be', 'ETUDIANT', '2024-03-01', '2023-2024', 1);

-- Inserts pour pae.companies
INSERT INTO pae.companies (name, designation, phone_number, address, email, is_blacklisted,
                           blacklist_reason, version)
VALUES ('Assyst Europe', NULL, '026092500', 'Avenue du Japon, 1/B9, 1420 Braine-l''Alleud', NULL,
        FALSE, NULL, 1),
       ('AXIS SRL', NULL, '027521760', 'Avenue de l''Hélianthe, 63, 1180 Uccle', NULL, FALSE, NULL,
        1),
       ('Infrabel', NULL, '025252211', 'Rue Bara, 135, 1070 Bruxelles', NULL, FALSE, NULL, 1),
       ('La route du papier', NULL, '025861665', 'Avenue des Mimosas, 83, 1150 Woluwe-Saint-Pierre',
        NULL, FALSE, NULL, 1),
       ('LetsBuild', NULL, '014546754', 'Chaussée de Bruxelles, 135A, 1310 La Hulpe', NULL, FALSE,
        NULL, 1),
       ('Niboo', NULL, '0487027913', 'Boulevard du Souverain, 24, 1170 Watermael-Boisfort', NULL,
        FALSE, NULL, 1),
       ('Sopra Steria', NULL, '025666666', 'Avenue Arnaud Fraiteur, 15/23, 1050 Bruxelles', NULL,
        FALSE, NULL, 1),
       ('The Bayard Partnership', NULL, '023095245', 'Grauwmeer, 1/57 bte 55, 3001 Leuven', NULL,
        FALSE, NULL, 1);

-- Inserts pour pae.supervisors
INSERT INTO pae.supervisors (first_name, last_name, phone_number, email, company, version)
VALUES ('Stéphanie', 'Dossche', '014546754', 'stephanie.dossche@letsbuild.com', (SELECT id
                                                                                 FROM pae.companies
                                                                                 WHERE name = 'LetsBuild'),
        1),
       ('Roberto', 'Alvarez Corchete', '025666014', NULL, (SELECT id
                                                           FROM pae.companies
                                                           WHERE name = 'Sopra Steria'), 1),
       ('Farid', 'Assal', '0474396909', 'f.assal@assyst-europe.com', (SELECT id
                                                                      FROM pae.companies
                                                                      WHERE name = 'Assyst Europe'),
        1),
       ('Emile', 'Ile', '0489321654', NULL, (SELECT id
                                             FROM pae.companies
                                             WHERE name = 'La route du papier'), 1),
       ('Owln', 'Hibo', '0456678567', NULL, (SELECT id
                                             FROM pae.companies
                                             WHERE name = 'Infrabel'), 1),
       ('Henri', 'Barn', '027521760', NULL, (SELECT id
                                             FROM pae.companies
                                             WHERE name = 'AXIS SRL'), 1);


-- Inserts pour pae.contacts 2023-2024
INSERT INTO pae.contacts (state, meeting_place, refusal_reason, academic_year, student, company,
                          version)
VALUES ('ACCEPTE', 'DISTANCIEL', NULL, '2023-2024', (SELECT id
                                                     FROM pae.users
                                                     WHERE email = 'carole.skile@student.vinci.be'),
        (SELECT id
         FROM pae.companies
         WHERE name = 'LetsBuild'), 1),
       ('ACCEPTE', 'PRESENTIEL', NULL, '2023-2024', (SELECT id
                                                     FROM pae.users
                                                     WHERE email = 'ach.ile@student.vinci.be'),
        (SELECT id
         FROM pae.companies
         WHERE name = 'Sopra Steria'), 1),
       ('REFUSE', 'DISTANCIEL', 'N''ont pas accepté d''avoir un entretien', '2023-2024', (SELECT id
                                                                                          FROM pae.users
                                                                                          WHERE email = 'ach.ile@student.vinci.be'),
        (SELECT id
         FROM pae.companies
         WHERE name = 'Niboo'), 1),
       ('ACCEPTE', 'PRESENTIEL', NULL, '2023-2024', (SELECT id
                                                     FROM pae.users
                                                     WHERE email = 'basile.ile@student.vinci.be'),
        (SELECT id
         FROM pae.companies
         WHERE name = 'Assyst Europe'), 1),
       ('SUSPENDU', 'DISTANCIEL', NULL, '2023-2024', (SELECT id
                                                      FROM pae.users
                                                      WHERE email = 'basile.ile@student.vinci.be'),
        (SELECT id
         FROM pae.companies
         WHERE name = 'LetsBuild'), 1),
       ('SUSPENDU', NULL, NULL, '2023-2024', (SELECT id
                                              FROM pae.users
                                              WHERE email = 'basile.ile@student.vinci.be'),
        (SELECT id
         FROM pae.companies
         WHERE name = 'Sopra Steria'), 1),
       ('REFUSE', 'PRESENTIEL', 'Ne prennent qu''un seul étudiant', '2023-2024', (SELECT id
                                                                                  FROM pae.users
                                                                                  WHERE email = 'basile.ile@student.vinci.be'),
        (SELECT id
         FROM pae.companies
         WHERE name = 'Niboo'), 1),
       ('REFUSE', 'DISTANCIEL', 'Pas d’affinité avec l’ERP Odoo', '2023-2024', (SELECT id
                                                                                FROM pae.users
                                                                                WHERE email = 'caroline.line@student.vinci.be'),
        (SELECT id
         FROM pae.companies
         WHERE name = 'Niboo'), 1),
       ('NON_SUIVI', NULL, NULL, '2023-2024', (SELECT id
                                               FROM pae.users
                                               WHERE email = 'caroline.line@student.vinci.be'),
        (SELECT id
         FROM pae.companies
         WHERE name = 'Sopra Steria'), 1),
       ('PRIS', 'DISTANCIEL', NULL, '2023-2024', (SELECT id
                                                  FROM pae.users
                                                  WHERE email = 'caroline.line@student.vinci.be'),
        (SELECT id
         FROM pae.companies
         WHERE name = 'LetsBuild'), 1),
       ('INITIE', NULL, NULL, '2023-2024', (SELECT id
                                            FROM pae.users
                                            WHERE email = 'theophile.ile@student.vinci.be'),
        (SELECT id
         FROM pae.companies
         WHERE name = 'Sopra Steria'), 1),
       ('INITIE', NULL, NULL, '2023-2024', (SELECT id
                                            FROM pae.users
                                            WHERE email = 'theophile.ile@student.vinci.be'),
        (SELECT id
         FROM pae.companies
         WHERE name = 'Niboo'), 1),
       ('INITIE', NULL, NULL, '2023-2024', (SELECT id
                                            FROM pae.users
                                            WHERE email = 'theophile.ile@student.vinci.be'),
        (SELECT id
         FROM pae.companies
         WHERE name = 'LetsBuild'), 1),
       ('INITIE', NULL, NULL, '2023-2024', (SELECT id
                                            FROM pae.users
                                            WHERE email = 'achille.skile@student.vinci.be'),
        (SELECT id
         FROM pae.companies
         WHERE name = 'Sopra Steria'), 1);

-- Inserts pour pae.contacts 2022-2023
INSERT INTO pae.contacts (state, meeting_place, refusal_reason, academic_year, student, company,
                          version)
VALUES ('ACCEPTE', 'DISTANCIEL', NULL, '2022-2023', (SELECT id
                                                     FROM pae.users
                                                     WHERE email = 'jeremy.demoulin@student.vinci.be'),
        (SELECT id
         FROM pae.companies
         WHERE name = 'Assyst Europe'), 1),
       ('ACCEPTE', 'PRESENTIEL', NULL, '2022-2023', (SELECT id
                                                     FROM pae.users
                                                     WHERE email = 'arthur.moulin@student.vinci.be'),
        (SELECT id
         FROM pae.companies
         WHERE name = 'AXIS SRL'), 1),
       ('ACCEPTE', 'PRESENTIEL', NULL, '2022-2023', (SELECT id
                                                     FROM pae.users
                                                     WHERE email = 'hugo.moulin@student.vinci.be'),
        (SELECT id
         FROM pae.companies
         WHERE name = 'AXIS SRL'), 1),
       ('ACCEPTE', 'DISTANCIEL', NULL, '2022-2023', (SELECT id
                                                     FROM pae.users
                                                     WHERE email = 'aurele.mile@student.vinci.be'),
        (SELECT id
         FROM pae.companies
         WHERE name = 'AXIS SRL'), 1),
       ('ACCEPTE', 'DISTANCIEL', NULL, '2022-2023', (SELECT id
                                                     FROM pae.users
                                                     WHERE email = 'frank.mile@student.vinci.be'),
        (SELECT id
         FROM pae.companies
         WHERE name = 'AXIS SRL'), 1),
       ('REFUSE', 'PRESENTIEL', 'Entretien n''a pas eu lieu', '2022-2023', (SELECT id
                                                                            FROM pae.users
                                                                            WHERE email = 'basile.dumoulin@student.vinci.be'),
        (SELECT id
         FROM pae.companies
         WHERE name = 'AXIS SRL'), 1),
       ('REFUSE', 'PRESENTIEL', 'Entretien n''a pas eu lieu', '2022-2023', (SELECT id
                                                                            FROM pae.users
                                                                            WHERE email = 'basile.dumoulin@student.vinci.be'),
        (SELECT id
         FROM pae.companies
         WHERE name = 'Niboo'), 1),
       ('REFUSE', 'DISTANCIEL', 'Entretien n''a pas eu lieu', '2022-2023', (SELECT id
                                                                            FROM pae.users
                                                                            WHERE email = 'basile.dumoulin@student.vinci.be'),
        (SELECT id
         FROM pae.companies
         WHERE name = 'Sopra Steria'), 1),
       ('ACCEPTE', 'DISTANCIEL', NULL, '2022-2023', (SELECT id
                                                     FROM pae.users
                                                     WHERE email = 'axel.dumoulin@student.vinci.be'),
        (SELECT id
         FROM pae.companies
         WHERE name = 'Sopra Steria'), 1),
       ('REFUSE', 'DISTANCIEL', 'Choix autre étudiant', '2022-2023', (SELECT id
                                                                      FROM pae.users
                                                                      WHERE email = 'basile.frilot@student.vinci.be'),
        (SELECT id
         FROM pae.companies
         WHERE name = 'Sopra Steria'), 1);

-- Inserts pour pae.contacts 2021-2022
INSERT INTO pae.contacts (state, meeting_place, refusal_reason, academic_year, student, company,
                          version)
VALUES ('ACCEPTE', 'DISTANCIEL', NULL, '2021-2022', (SELECT id
                                                     FROM pae.users
                                                     WHERE email = 'elle.skile@student.vinci.be'),
        (SELECT id
         FROM pae.companies
         WHERE name = 'La route du papier'), 1),
       ('NON_SUIVI', NULL, NULL, '2021-2022', (SELECT id
                                               FROM pae.users
                                               WHERE email = 'basile.Ilot@student.vinci.be'),
        (SELECT id
         FROM pae.companies
         WHERE name = 'Sopra Steria'), 1),
       ('REFUSE', 'DISTANCIEL', 'Ne prennent pas de stage', '2021-2022', (SELECT id
                                                                          FROM pae.users
                                                                          WHERE email = 'basile.frilot@student.vinci.be'),
        (SELECT id
         FROM pae.companies
         WHERE name = 'The Bayard Partnership'), 1),
       ('ACCEPTE', 'PRESENTIEL', NULL, '2021-2022', (SELECT id
                                                     FROM pae.users
                                                     WHERE email = 'arnaud.dito@student.vinci.be'),
        (SELECT id
         FROM pae.companies
         WHERE name = 'Sopra Steria'), 1),
       ('ACCEPTE', 'PRESENTIEL', NULL, '2021-2022', (SELECT id
                                                     FROM pae.users
                                                     WHERE email = 'arnaud.dilo@student.vinci.be'),
        (SELECT id
         FROM pae.companies
         WHERE name = 'Sopra Steria'), 1),
       ('ACCEPTE', 'PRESENTIEL', NULL, '2021-2022', (SELECT id
                                                     FROM pae.users
                                                     WHERE email = 'cedric.dilot@student.vinci.be'),
        (SELECT id
         FROM pae.companies
         WHERE name = 'Assyst Europe'), 1),
       ('REFUSE', 'PRESENTIEL', 'Choix autre étudiant', '2021-2022', (SELECT id
                                                                      FROM pae.users
                                                                      WHERE email = 'cedric.dilot@student.vinci.be'),
        (SELECT id
         FROM pae.companies
         WHERE name = 'Sopra Steria'), 1),
       ('ACCEPTE', 'DISTANCIEL', NULL, '2021-2022', (SELECT id
                                                     FROM pae.users
                                                     WHERE email = 'auristelle.linot@student.vinci.be'),
        (SELECT id
         FROM pae.companies
         WHERE name = 'Infrabel'), 1),
       ('SUSPENDU', NULL, NULL, '2021-2022', (SELECT id
                                              FROM pae.users
                                              WHERE email = 'auristelle.linot@student.vinci.be'),
        (SELECT id
         FROM pae.companies
         WHERE name = 'Sopra Steria'), 1),
       ('REFUSE', 'DISTANCIEL', 'Choix autre étudiant', '2021-2022', (SELECT id
                                                                      FROM pae.users
                                                                      WHERE email = 'auristelle.linot@student.vinci.be'),
        (SELECT id
         FROM pae.companies
         WHERE name = 'Niboo'), 1);

-- Inserts pour pae.interships 2023-2024
INSERT INTO pae.internships (student, supervisor, contact, subject, signature_date, academic_year,
                             version)
VALUES ((SELECT id
         FROM pae.users
         WHERE email = 'carole.skile@student.vinci.be'), (SELECT id
                                                          FROM pae.supervisors
                                                          WHERE first_name = 'Stéphanie'
                                                            AND last_name = 'Dossche'), (SELECT id
                                                                                         FROM pae.contacts
                                                                                         WHERE
                                                                                             student =
                                                                                             (SELECT id
                                                                                              FROM pae.users
                                                                                              WHERE email = 'carole.skile@student.vinci.be')
                                                                                           AND
                                                                                             company =
                                                                                             (SELECT id
                                                                                              FROM pae.companies
                                                                                              WHERE name = 'LetsBuild')),
        'Un ERP : Odoo', '2023-10-10', '2023-2024', 1),

       ((SELECT id
         FROM pae.users
         WHERE email = 'ach.ile@student.vinci.be'), (SELECT id
                                                     FROM pae.supervisors
                                                     WHERE first_name = 'Roberto'
                                                       AND last_name = 'Alvarez Corchete'),
        (SELECT id
         FROM pae.contacts
         WHERE student =
               (SELECT id
                FROM pae.users
                WHERE email = 'ach.ile@student.vinci.be')
           AND company =
               (SELECT id
                FROM pae.companies
                WHERE name = 'Sopra Steria')), 'sBMS project - a complex environment', '2023-11-23',
        '2023-2024', 1),

       ((SELECT id
         FROM pae.users
         WHERE email = 'basile.ile@student.vinci.be'), (SELECT id
                                                        FROM pae.supervisors
                                                        WHERE first_name = 'Farid'
                                                          AND last_name = 'Assal'), (SELECT id
                                                                                     FROM pae.contacts
                                                                                     WHERE student =
                                                                                           (SELECT id
                                                                                            FROM pae.users
                                                                                            WHERE email = 'basile.ile@student.vinci.be')
                                                                                       AND company =
                                                                                           (SELECT id
                                                                                            FROM pae.companies
                                                                                            WHERE name = 'Assyst Europe')),
        'CRM : Microsoft Dynamics 365 For Sales', '2023-10-12', '2023-2024', 1);

-- Inserts pour pae.interships 2021-2022
INSERT INTO pae.internships (student, supervisor, contact, subject, signature_date, academic_year,
                             version)
VALUES ((SELECT id
         FROM pae.users
         WHERE email = 'elle.skile@student.vinci.be'), (SELECT id
                                                        FROM pae.supervisors
                                                        WHERE first_name = 'Emile'
                                                          AND last_name = 'Ile'), (SELECT id
                                                                                   FROM pae.contacts
                                                                                   WHERE student =
                                                                                         (SELECT id
                                                                                          FROM pae.users
                                                                                          WHERE email = 'elle.skile@student.vinci.be')
                                                                                     AND company =
                                                                                         (SELECT id
                                                                                          FROM pae.companies
                                                                                          WHERE name = 'La route du papier')),
        'Conservation et restauration d’œuvres d’art', '2021-11-25', '2021-2022', 1),

       ((SELECT id
         FROM pae.users
         WHERE email = 'arnaud.dito@student.vinci.be'), (SELECT id
                                                         FROM pae.supervisors
                                                         WHERE last_name = 'Alvarez Corchete'),
        (SELECT id
         FROM pae.contacts
         WHERE student =
               (SELECT id
                FROM pae.users
                WHERE email = 'arnaud.dito@student.vinci.be')
           AND company =
               (SELECT id
                FROM pae.companies
                WHERE name = 'Sopra Steria')), 'L''analyste au centre du développement',
        '2021-11-17', '2021-2022', 1),

       ((SELECT id
         FROM pae.users
         WHERE email = 'arnaud.dilo@student.vinci.be'), (SELECT id
                                                         FROM pae.supervisors
                                                         WHERE last_name = 'Alvarez Corchete'),
        (SELECT id
         FROM pae.contacts
         WHERE student =
               (SELECT id
                FROM pae.users
                WHERE email = 'arnaud.dilo@student.vinci.be')
           AND company =
               (SELECT id
                FROM pae.companies
                WHERE name = 'Sopra Steria')), 'L''analyste au centre du développement',
        '2021-11-17', '2021-2022', 1),

       ((SELECT id
         FROM pae.users
         WHERE email = 'cedric.dilot@student.vinci.be'), (SELECT id
                                                          FROM pae.supervisors
                                                          WHERE first_name = 'Farid'
                                                            AND last_name = 'Assal'), (SELECT id
                                                                                       FROM pae.contacts
                                                                                       WHERE
                                                                                           student =
                                                                                           (SELECT id
                                                                                            FROM pae.users
                                                                                            WHERE email = 'cedric.dilot@student.vinci.be')
                                                                                         AND
                                                                                           company =
                                                                                           (SELECT id
                                                                                            FROM pae.companies
                                                                                            WHERE name = 'Assyst Europe')),
        'ERP : Microsoft Dynamics 366', '2021-11-23', '2021-2022', 1),

       ((SELECT id
         FROM pae.users
         WHERE email = 'auristelle.linot@student.vinci.be'), (SELECT id
                                                              FROM pae.supervisors
                                                              WHERE first_name = 'Owln'
                                                                AND last_name = 'Hibo'), (SELECT id
                                                                                          FROM pae.contacts
                                                                                          WHERE
                                                                                              student =
                                                                                              (SELECT id
                                                                                               FROM pae.users
                                                                                               WHERE email = 'auristelle.linot@student.vinci.be')
                                                                                            AND
                                                                                              company =
                                                                                              (SELECT id
                                                                                               FROM pae.companies
                                                                                               WHERE name = 'Infrabel')),
        'Entretien des rails', '2021-11-22', '2021-2022', 1);

-- Inserts pour pae.interships 2022-2023
INSERT INTO pae.internships (student, supervisor, contact, subject, signature_date, academic_year,
                             version)
VALUES ((SELECT id
         FROM pae.users
         WHERE email = 'jeremy.demoulin@student.vinci.be'), (SELECT id
                                                             FROM pae.supervisors
                                                             WHERE last_name = 'Assal'), (SELECT id
                                                                                          FROM pae.contacts
                                                                                          WHERE
                                                                                              student =
                                                                                              (SELECT id
                                                                                               FROM pae.users
                                                                                               WHERE email = 'jeremy.demoulin@student.vinci.be')
                                                                                            AND
                                                                                              company =
                                                                                              (SELECT id
                                                                                               FROM pae.companies
                                                                                               WHERE name = 'Assyst Europe')),
        'CRM : Microsoft Dynamics 365 For Sales', '2022-11-23', '2022-2023', 1),

       ((SELECT id
         FROM pae.users
         WHERE email = 'arthur.moulin@student.vinci.be'), (SELECT id
                                                           FROM pae.supervisors
                                                           WHERE first_name = 'Henri'
                                                             AND last_name = 'Barn'), (SELECT id
                                                                                       FROM pae.contacts
                                                                                       WHERE
                                                                                           student =
                                                                                           (SELECT id
                                                                                            FROM pae.users
                                                                                            WHERE email = 'arthur.moulin@student.vinci.be')
                                                                                         AND
                                                                                           company =
                                                                                           (SELECT id
                                                                                            FROM pae.companies
                                                                                            WHERE name = 'AXIS SRL')),
        'Un métier : chef de projet', '2022-10-19', '2022-2023', 1),

       ((SELECT id
         FROM pae.users
         WHERE email = 'hugo.moulin@student.vinci.be'), (SELECT id
                                                         FROM pae.supervisors
                                                         WHERE first_name = 'Henri'
                                                           AND last_name = 'Barn'), (SELECT id
                                                                                     FROM pae.contacts
                                                                                     WHERE student =
                                                                                           (SELECT id
                                                                                            FROM pae.users
                                                                                            WHERE email = 'hugo.moulin@student.vinci.be')
                                                                                       AND company =
                                                                                           (SELECT id
                                                                                            FROM pae.companies
                                                                                            WHERE name = 'AXIS SRL')),
        'Un métier : chef de projet', '2022-10-19', '2022-2023', 1),

       ((SELECT id
         FROM pae.users
         WHERE email = 'aurele.mile@student.vinci.be'), (SELECT id
                                                         FROM pae.supervisors
                                                         WHERE first_name = 'Henri'
                                                           AND last_name = 'Barn'), (SELECT id
                                                                                     FROM pae.contacts
                                                                                     WHERE student =
                                                                                           (SELECT id
                                                                                            FROM pae.users
                                                                                            WHERE email = 'aurele.mile@student.vinci.be')
                                                                                       AND company =
                                                                                           (SELECT id
                                                                                            FROM pae.companies
                                                                                            WHERE name = 'AXIS SRL')),
        'Un métier : chef de projet', '2022-10-19', '2022-2023', 1),

       ((SELECT id
         FROM pae.users
         WHERE email = 'frank.mile@student.vinci.be'), (SELECT id
                                                        FROM pae.supervisors
                                                        WHERE first_name = 'Henri'
                                                          AND last_name = 'Barn'), (SELECT id
                                                                                    FROM pae.contacts
                                                                                    WHERE student =
                                                                                          (SELECT id
                                                                                           FROM pae.users
                                                                                           WHERE email = 'frank.mile@student.vinci.be')
                                                                                      AND company =
                                                                                          (SELECT id
                                                                                           FROM pae.companies
                                                                                           WHERE name = 'AXIS SRL')),
        'Un métier : chef de projet', '2022-10-19', '2022-2023', 1),

       ((SELECT id
         FROM pae.users
         WHERE email = 'axel.dumoulin@student.vinci.be'), (SELECT id
                                                           FROM pae.supervisors
                                                           WHERE first_name = 'Roberto'
                                                             AND last_name = 'Alvarez Corchete'),
        (SELECT id
         FROM pae.contacts
         WHERE student =
               (SELECT id
                FROM pae.users
                WHERE email = 'axel.dumoulin@student.vinci.be')
           AND company =
               (SELECT id
                FROM pae.companies
                WHERE name = 'Sopra Steria')), 'sBMS project - Java Development', '2022-10-17',
        '2022-2023', 1);


------------------------------------------------- REQUÊTES DEMO ---------------------------------------------------------------

-- 1. Comptage du nombre d’utilisateurs, par rôle et par année académique.
SELECT role, academic_year, COUNT(*) AS user_count
FROM pae.users
GROUP BY role, academic_year
ORDER BY role, academic_year;

-- 2. Année académique et comptage du nombre de stages par année académique.
SELECT academic_year, COUNT(*) AS nombre_de_stages
FROM pae.internships
GROUP BY academic_year;

-- 3. Entreprise, année académique, et comptage du nombre de stages
-- par entreprise et année académique.
SELECT c.name AS company_name, years.academic_year, COALESCE(COUNT(i.id), 0) AS students_nbr
FROM (SELECT DISTINCT academic_year
      FROM pae.internships) years
         CROSS JOIN pae.companies c
         LEFT JOIN pae.supervisors s ON c.id = s.company
         LEFT JOIN pae.internships i
                   ON s.id = i.supervisor AND i.academic_year = years.academic_year
GROUP BY c.id, c.name, years.academic_year
ORDER BY c.name, years.academic_year;

-- 4. Année académique et comptage du nombre de contacts par année académique
SELECT academic_year, COUNT(*) AS contact_count
FROM pae.contacts
GROUP BY academic_year
ORDER BY academic_year;

-- 5. Etats (en format lisible par le client) et comptage du nombre de
-- contacts dans chacun des états
WITH etats_possibles AS (SELECT 'Accepté' AS etat
                         UNION
                         SELECT 'Refusé' AS etat
                         UNION
                         SELECT 'Initié' AS etat
                         UNION
                         SELECT 'Pris' AS etat
                         UNION
                         SELECT 'Suspendu' AS etat
                         UNION
                         SELECT 'Non suivi' AS etat
                         UNION
                         SELECT 'Blacklisté' AS etat)

SELECT ep.etat,
       COALESCE(COUNT(c.id), 0) AS contact_count
FROM etats_possibles ep
         LEFT JOIN pae.contacts c ON ep.etat =
                                     CASE
                                         WHEN c.state = 'ACCEPTE' THEN 'Accepté'
                                         WHEN c.state = 'REFUSE' THEN 'Refusé'
                                         WHEN c.state = 'INITIE' THEN 'Initié'
                                         WHEN c.state = 'PRIS' THEN 'Pris'
                                         WHEN c.state = 'SUSPENDU' THEN 'Suspendu'
                                         WHEN c.state = 'NON_SUIVI' THEN 'Non suivi'
                                         WHEN c.state = 'BLACKLISTE' THEN 'Blacklisté'
                                         END
GROUP BY ep.etat;

-- 6. Année académique, états (en format lisible par le client) et comptage du nombre de contacts
-- dans chacun des états par année académique
WITH etats_possibles AS (SELECT 'Accepté' AS etat
                         UNION
                         SELECT 'Refusé' AS etat
                         UNION
                         SELECT 'Initié' AS etat
                         UNION
                         SELECT 'Pris' AS etat
                         UNION
                         SELECT 'Suspendu' AS etat
                         UNION
                         SELECT 'Non suivi' AS etat
                         UNION
                         SELECT 'Blacklisté' AS etat)

SELECT i.academic_year,
       ep.etat,
       COALESCE(COUNT(c.id), 0) AS contact_count
FROM etats_possibles ep
         CROSS JOIN (SELECT DISTINCT academic_year
                     FROM pae.contacts) i
         LEFT JOIN pae.contacts c ON ep.etat =
                                     CASE
                                         WHEN c.state = 'ACCEPTE' THEN 'Accepté'
                                         WHEN c.state = 'REFUSE' THEN 'Refusé'
                                         WHEN c.state = 'INITIE' THEN 'Initié'
                                         WHEN c.state = 'PRIS' THEN 'Pris'
                                         WHEN c.state = 'SUSPENDU' THEN 'Suspendu'
                                         WHEN c.state = 'NON_SUIVI' THEN 'Non suivi'
                                         WHEN c.state = 'BLACKLISTE' THEN 'Blacklisté'
                                         END
    AND c.academic_year = i.academic_year
GROUP BY ep.etat, i.academic_year
ORDER BY i.academic_year;

-- 7. Entreprise, états (en format lisible par le client) et comptage du nombre de contacts dans
-- chacun des états par entreprise.
SELECT companies.name                            AS entreprise,
       CASE
           WHEN states.etat = 'ACCEPTE' THEN 'Accepté'
           WHEN states.etat = 'REFUSE' THEN 'Refusé'
           WHEN states.etat = 'INITIE' THEN 'Initié'
           WHEN states.etat = 'PRIS' THEN 'Pris'
           WHEN states.etat = 'SUSPENDU' THEN 'Suspendu'
           WHEN states.etat = 'NON_SUIVI' THEN 'Non suivi'
           WHEN states.etat = 'BLACKLISTE' THEN 'Blacklisté'
           END                                   AS etat,
       COALESCE(contact_counts.contact_count, 0) AS contact_count
FROM (SELECT DISTINCT state AS etat FROM pae.contacts UNION SELECT 'BLACKLISTE') AS states
         CROSS JOIN pae.companies
         LEFT JOIN (SELECT company,
                           state,
                           COUNT(*) AS contact_count
                    FROM pae.contacts
                    GROUP BY company,
                             state) AS contact_counts
                   ON companies.id = contact_counts.company AND states.etat = contact_counts.state
ORDER BY companies.name,
         states.etat;
