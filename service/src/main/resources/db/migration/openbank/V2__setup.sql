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

create table client
(
    id bigint not null
        constraint client_pkey
            primary key
);

create table account
(
    id             bigint not null
        constraint account_pkey
            primary key,
    account_number varchar(255),
    balance        bigint,
    client_id      bigint
        constraint fk9ra1gbw2kf4c7wucgj77rw0y0
            references client
);

create table accounttransaction
(
    id              bigint not null
        constraint accounttransaction_pkey
            primary key,
    message         varchar(255),
    transactiontype varchar(255),
    account_id      bigint
        constraint fkdoq4tcr5ddy1oiacvvarhy46p
            references account
);

create table account_accounttransaction
(
    account_id               bigint not null
        constraint fkf5ywripclom7vc1cc6vid31i9
            references account,
    accounttransactionset_id bigint not null
        constraint uk_f5gufq6gwgbk27lv72k5b5a72
            unique
        constraint fkkmdn79x24jf5mvyl04j1m7lfh
            references accounttransaction,
    constraint account_accounttransaction_pkey
        primary key (account_id, accounttransactionset_id)
);

create table client_account
(
    client_id     bigint not null
        constraint fkarhi77upy615vcqorxojvhadw
            references client,
    accountset_id bigint not null
        constraint uk_dtjmewc6i8q0r2ixxk7ls36v9
            unique
        constraint fk9542mmkfnduwurk8cx53vvevs
            references account,
    constraint client_account_pkey
        primary key (client_id, accountset_id)
);

create table clienttype
(
    ctype         varchar(31) not null,
    id            bigint      not null
        constraint clienttype_pkey
            primary key,
    premiumrating bigint,
    specialoffers varchar(255),
    country       varchar(255),
    rating        bigint,
    client_fk     bigint
        constraint fkonmn33btu9x2rwxc7e97wu6ct
            references client
);

create table person
(
    id                   bigint not null
        constraint person_pkey
            primary key,
    firstname            varchar(255),
    lastname             varchar(255),
    mail                 varchar(255),
    personidentification varchar(255),
    client_fk            bigint
        constraint fkiwif8m1iq92e01wl5v6mn7okw
            references client
);

create table systemproperty
(
    id    bigint not null
        constraint systemproperty_pkey
            primary key,
    name  varchar(255),
    value varchar(255)
);

create sequence hibernate_sequence;