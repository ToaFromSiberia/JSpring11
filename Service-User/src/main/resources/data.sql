insert into roles (name, description)
values ('ROLE_USER', 'для авторизированных пользователей'),
       ('ROLE_ADMIN', 'для админов'),
       ('ROLE_DEVELOPER', 'для разработчиков'),
       ('ROLE_PRODUCER', 'для манагеров и прочей шушеры');

insert into users (username, password, email)
values ('ИП Рога и копыта', '12345', 'everything.is.fair@deception.no'),
       ('Andrey', '12345', 'andnot@yandex.ru'),
       ('Ivan', '12345', 'ivan-gold-ruki@yandex.ru'),
       ('Sveta', '12345', 'confeta@yandex.ru');

insert into users_roles (user_id, role_id)
values (1, 2), (1, 4),
       (2, 2), (2, 3),
       (3, 1),
       (4, 1);

insert into accounts (name, amount, creation, user_id)
values ('123-274-927', 10000.00, '2024-01-01', 1),
       ('381-238-280', 10000.00, '2024-08-27', 2),
       ('911-029-194', 10000.00, '2024-06-23', 3),
       ('872-938-023', 10000.00, '2024-10-03', 4);
