
CREATE TABLE target
(
	id                   INTEGER NOT NULL,
	description          VARCHAR(20) NULL,
	testcase_count       INTEGER NULL
);



CREATE UNIQUE INDEX XPKtarget ON target
(
	id
);



ALTER TABLE target
ADD PRIMARY KEY (id);



CREATE UNIQUE INDEX XIF1target ON target
(
	id
);



CREATE TABLE user
(
	id                   INTEGER NOT NULL,
	name                 VARCHAR(20) NULL,
	password             VARCHAR(20) NULL
);



CREATE UNIQUE INDEX XPKuser ON user
(
	id
);



ALTER TABLE user
ADD PRIMARY KEY (id);



CREATE UNIQUE INDEX XIF1user ON user
(
	id
);



CREATE TABLE user_target
(
	id                   INTEGER NOT NULL,
	user                 INTEGER NULL,
	target               INTEGER NULL,
	state                VARCHAR(20) NULL
);



CREATE UNIQUE INDEX XPKuser_target ON user_target
(
	id
);



ALTER TABLE user_target
ADD PRIMARY KEY (id);



ALTER TABLE target
ADD FOREIGN KEY R_6 (id) REFERENCES user_target (id);



ALTER TABLE user
ADD FOREIGN KEY R_3 (id) REFERENCES user_target (id);


