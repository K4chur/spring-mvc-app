delete from user_role;
delete from usr;

insert into usr(id, active, password, username) values
                                                    (1, true, '$2a$08$Md5bwFFwFjXznV3KaRIHu.SBkp4ZwbOSWHn4fO64TXvhgUUQ/uPVS','admin'),
                                                    (2,true,'$2a$08$Md5bwFFwFjXznV3KaRIHu.SBkp4ZwbOSWHn4fO64TXvhgUUQ/uPVS','mike');
insert into user_role (user_id, roles)
values (1, 'USER'), (1, 'ADMIN'), (2, 'USER');
