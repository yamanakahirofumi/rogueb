-- BookOfAdventure
CREATE TABLE Player(
 id INT AUTO_INCREMENT PRIMARY KEY,
 name VARCHAR(64) NOT NULL,
 namespace TEXT NOT NULL
);

CREATE TABLE Location (
  player_id INT PRIMARY KEY,
  dungeon_id INT NOT NULL,
  level INT NOT NULL,
  x INT NOT NULL,
  y INT NOT NULL
);

-- WOLRD
CREATE TABLE WORLD (
  id INT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(64) NOT NULL,
  namespace TEXT NOT NULL,
  endpoint VARCHAR(65532) NOT NULL
);

-- Dungeon
CREATE TABLE Dungeon (
  id INT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(64) NOT NULL,
  maxLevel INT NOT NULL,
  namespace TEXT NOT NULL
);

CREATE TABLE Dungeon_Player (
  id INT AUTO_INCREMENT PRIMARY KEY,
  dungeon_id INT,
  player_id INT NOT NULL,
  max_level INT NOT NULL,
  FOREIGN KEY (dungeon_id) REFERENCES Dungeon(id)
);

-- Object
CREATE TABLE ObjectInfo_type(
  id INT PRIMARY KEY,
  name VARCHAR(64) NOT NULL
);

CREATE TABLE ObjectInfo (
  id INT AUTO_INCREMENT PRIMARY KEY,
  type_id INT NOT NULL,
  probability INT NOT NULL,
  worth INT NOT NULL ,
  name VARCHAR(64) NOT NULL,
  FOREIGN KEY (type_id) REFERENCES ObjectInfo_type(id)
);


-- Master Data
INSERT INTO objectinfo_type
(id, name)
VALUES
(1,'armor'),
(2,'ring'),
(3,'scroll'),
(4,'weapon'),
(5,'stick');

INSERT INTO objectinfo
(type_id, id, name, probability, worth)
VALUES
(1,1,'leather armor',20,20),
(1,2,'ring mail',15,25),
(1,3,'studded leather armor',15,20),
(1,4,'scale mail',13,30),
(1,5,'chain mail',12,75),
(1,6,'splint mail',10,80),
(1,7,'banded mail',10,90),
(1,8,'plate mail',5,150),
(2,9,'protection',9,400),
(2,10,'add strength',9,400),
(2,11,'sustain strength',5,280),
(2,12,'searching',10,420),
(2,13,'see invisible',10,310),
(2,14,'adornment',1,10),
(2,15,'aggravate monster',10,10),
(2,16,'dexterity',8,440),
(2,17,'increase damage',8,400),
(2,18,'regeneration',4,460),
(2,19,'slow digestion',9,240),
(2,20,'teleportation',5,30),
(2,21,'stealth',7,470),
(2,22,'maintain armor',5, 80);
