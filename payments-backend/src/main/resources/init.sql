create table if not exists payments(
    id serial primary key not null,
    user_from text,
    user_to text,
    money numeric
);