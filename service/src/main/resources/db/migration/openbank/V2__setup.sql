/*
 * Copyright 2021 JSquad AB
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

create table openbank.CLIENT
(
  ID bigint not null
    primary key
);

create table openbank.ACCOUNT
(
  ID             bigint       not null
    primary key,
  ACCOUNT_NUMBER varchar(255) null,
  BALANCE        bigint       null,
  client_ID      bigint       null,
  constraint FK9ra1gbw2kf4c7wucgj77rw0y0
    foreign key (client_ID) references CLIENT (ID)
);

create table openbank.ACCOUNTTRANSACTION
(
  ID              bigint       not null
    primary key,
  MESSAGE         varchar(255) null,
  TRANSACTIONTYPE varchar(255) null,
  account_ID      bigint       null,
  constraint FKdoq4tcr5ddy1oiacvvarhy46p
    foreign key (account_ID) references ACCOUNT (ID)
);

create table openbank.ACCOUNT_ACCOUNTTRANSACTION
(
  Account_ID               bigint not null,
  accountTransactionSet_ID bigint not null,
  primary key (Account_ID, accountTransactionSet_ID),
  constraint UK_f5gufq6gwgbk27lv72k5b5a72
    unique (accountTransactionSet_ID),
  constraint FKf5ywripclom7vc1cc6vid31i9
    foreign key (Account_ID) references ACCOUNT (ID),
  constraint FKkmdn79x24jf5mvyl04j1m7lfh
    foreign key (accountTransactionSet_ID) references ACCOUNTTRANSACTION (ID)
);

create table openbank.CLIENTTYPE
(
  CTYPE         varchar(31)  not null,
  ID            bigint       not null
    primary key,
  PREMIUMRATING bigint       null,
  SPECIALOFFERS varchar(255) null,
  COUNTRY       varchar(255) null,
  RATING        bigint       null,
  CLIENT_FK     bigint       null,
  constraint FKonmn33btu9x2rwxc7e97wu6ct
    foreign key (CLIENT_FK) references CLIENT (ID)
);

create table openbank.CLIENT_ACCOUNT
(
  Client_ID     bigint not null,
  accountSet_ID bigint not null,
  primary key (Client_ID, accountSet_ID),
  constraint UK_dtjmewc6i8q0r2ixxk7ls36v9
    unique (accountSet_ID),
  constraint FK9542mmkfnduwurk8cx53vvevs
    foreign key (accountSet_ID) references ACCOUNT (ID),
  constraint FKarhi77upy615vcqorxojvhadw
    foreign key (Client_ID) references CLIENT (ID)
);

create table openbank.Person
(
  ID                   bigint       not null
    primary key,
  FIRSTNAME            varchar(255) null,
  LASTNAME             varchar(255) null,
  MAIL                 varchar(255) null,
  PERSONIDENTIFICATION varchar(255) null,
  CLIENT_FK            bigint       null,
  constraint FKiwif8m1iq92e01wl5v6mn7okw
    foreign key (CLIENT_FK) references CLIENT (ID)
);

create table openbank.SYSTEMPROPERTY
(
  ID    bigint       not null
    primary key,
  NAME  varchar(255) null,
  VALUE varchar(255) null
);

create table openbank.hibernate_sequence
(
  next_val bigint null
);

INSERT INTO openbank.hibernate_sequence (next_val) VALUES (1);