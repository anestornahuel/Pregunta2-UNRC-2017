CREATE TABLE users (
  id int(11) auto_increment PRIMARY KEY,
  password varchar(50),
  name varchar(50),
  score int(11),
  globalLives int(11),
  lastupdate DATETIME,
  created_at DATETIME,
  updated_at DATETIME
)ENGINE=InnoDB;