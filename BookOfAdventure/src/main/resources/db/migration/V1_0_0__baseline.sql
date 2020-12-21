-- BookOfAdventure
CREATE TABLE Player(
 id INT AUTO_INCREMENT PRIMARY KEY,
 name VARCHAR(64) NOT NULL,
 exp INT NOT NULL,
 gold INT NOT NULL,
 namespace TEXT NOT NULL
);

CREATE TABLE Player_Status(
  player_id INT PRIMARY KEY,
  max_hp INT NOT NULL,
  hp INT NOT NULL,
  max_mp INT NOT NULL,
  mp INT NOT NULL,
  strength INT NOT NULL,
  current_strength INT NOT NULL
);

CREATE TABLE Location (
  player_id INT PRIMARY KEY,
  dungeon_id INT NOT NULL,
  level INT NOT NULL,
  x INT NOT NULL,
  y INT NOT NULL,
  FOREIGN KEY (player_id) REFERENCES Player(id)
);

CREATE TABLE Player_Item (
  id INT AUTO_INCREMENT PRIMARY KEY,
  player_id INT NOT NULL,
  item_id INT NOT NULL,
  FOREIGN KEY (player_id) REFERENCES Player(id)
);
