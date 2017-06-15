CREATE TABLE questions (
  id int(11) auto_increment PRIMARY KEY,
  category_id int(11),
  question varchar(200),
  answer1 varchar(50),
  answer2 varchar(50),
  answer3 varchar(50),
  correct int(11)
)ENGINE=InnoDB;