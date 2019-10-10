create table security.SECURITY
(
  ID bigint not null
    primary key,
      SECURITY_CODE varchar(255) null
);

create table security.hibernate_sequence
(
  next_val bigint null
);

INSERT INTO security.hibernate_sequence (next_val) VALUES (1);