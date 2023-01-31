create table if not exists extensions
(
    name    varchar(255)  CHARACTER SET ascii COLLATE ascii_bin not null,
    data    longblob,
    version bigint,
    primary key (name)
);
