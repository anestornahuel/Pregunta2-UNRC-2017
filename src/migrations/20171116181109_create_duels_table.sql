CREATE TABLE duels (
  id int(11) auto_increment PRIMARY KEY,
  user1 varchar(50),
  user2 varchar(50),
  score int(11),
  corrects1 int(11),
  corrects2 int(11),
  category int(11)
)ENGINE=InnoDB;