-- Dungeon
CREATE TABLE Dungeon (
  id INT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(64) NOT NULL,
  maxLevel INT NOT NULL,
  itemSeed INT NOT NULL,
  namespace TEXT NOT NULL
);

CREATE TABLE Dungeon_Player (
  id INT AUTO_INCREMENT PRIMARY KEY,
  dungeon_id INT,
  player_id INT NOT NULL,
  max_level INT NOT NULL,
  FOREIGN KEY (dungeon_id) REFERENCES Dungeon(id)
);

-- Master Data
-- Dungeon
INSERT INTO Dungeon
(name, maxLevel, itemSeed, namespace)
VALUES
('dungeon', 2, 3, 'localhost');
