insert into student (userid, firstname, lastname) values ('it0001', 'Max', 'Muster');
insert into student (userid, firstname, lastname) values ('it0002', 'Susi', 'Sonne');
insert into student (userid, firstname, lastname) values ('it0003', 'Hansi', 'Huber');
insert into student (userid, firstname, lastname) values ('it0004', 'Berti', 'Bauer');

drop table quarkus_user;

create table quarkus_user (
  id int,
  username varchar(255),
  password varchar(255),
  salt varchar(255),
  iteration_count integer,
  role varchar(255)
);

insert into quarkus_user(id, username, password, salt, iteration_count, role) values (1, 'max', 'sjl8xkG1Mc/yQF1Nengx3Ogg57Y5F0c=', 'ZE9EKfds3D7VT/0bTNCIgg==', 10, 'admin');
insert into quarkus_user(id, username, password, salt, iteration_count, role) values (2, 'susi', 'aPnJAMErXgXIR1RlsB1yegY2JmNeXps=','pc9CKhxWJmag8dbHzg7yKA==', 10, 'user');
