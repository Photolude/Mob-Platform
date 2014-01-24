insert into user_source(name) values ('mob');
insert into user_source(name) values ('google');

call addUser('anonymous', 'mob', '35b2a314-8168-4745-98da-fd9a2fdf3618', CURRENT_DATE, null);
call addUser('defaultuser', 'mob', '', CURRENT_DATE, null);