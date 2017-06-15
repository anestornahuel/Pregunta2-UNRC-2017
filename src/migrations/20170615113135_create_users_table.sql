CREATE TABLE users (
  id int(11) auto_increment PRIMARY KEY,
  password VARCHAR(50),
  name VARCHAR(50),
  score int(11),
  esadmin boolean,
  created_at DATETIME,
  updated_at DATETIME
)ENGINE=InnoDB;