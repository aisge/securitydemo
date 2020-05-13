insert into student (userid, firstname, lastname) values ('it0001', 'Max', 'Muster');
insert into student (userid, firstname, lastname) values ('it0002', 'Susi', 'Sonne');
insert into student (userid, firstname, lastname) values ('it0003', 'Hansi', 'Huber');
insert into student (userid, firstname, lastname) values ('it0004', 'Berti', 'Bauer');

drop table quarkus_user;

create table quarkus_user (
  id int,
  username varchar(255),
  password varchar(255),
  role varchar(255)
);

insert into quarkus_user(id, username, password, role) values (1, 'max', 'passme', 'admin');
insert into quarkus_user(id, username, password, role) values (2, 'susi', 'passme', 'user');
