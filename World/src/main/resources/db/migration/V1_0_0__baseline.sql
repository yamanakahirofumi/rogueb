CREATE TABLE World_Type (
  id INT PRIMARY KEY,
  name VARCHAR(64) NOT NULL
);

CREATE TABLE World (
  id INT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(64) NOT NULL,
  namespace TEXT NOT NULL,
  type INT NOT NULL,
  endpoint VARCHAR(65532) NOT NULL,
  FOREIGN KEY (type) REFERENCES World_Type(id)
);

CREATE TABLE Service_Type (
  id INT PRIMARY KEY,
  name VARCHAR (64) NOT NULL
);

CREATE TABLE Service (
  id INT AUTO_INCREMENT PRIMARY KEY,
  service_type INT NOT NULL,
  world_id INT NOT NULL,
  endpoint VARCHAR(65532) NOT NULL,
  FOREIGN KEY (service_type) REFERENCES Service_Type(id),
  FOREIGN KEY (world_id) REFERENCES World(id)
);

-- Master Data
INSERT INTO World_Type
(id, name)
VALUES
(1, 'Self'),
(2, 'From'),
(20, 'To'),
(22, 'Mutual'),
(30, 'Known'),
(99, 'Unknown');


INSERT INTO Service_Type
(id, name)
VALUES
(100, 'World'),
(200, 'BookOfAdventure'),
(300, 'Dungeon'),
(400, 'Objects'),
(500, 'PlayerOperations'),
(99999, 'Others');
