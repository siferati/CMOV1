
.mode columns
.headers on
.nullvalue NULL

DROP TABLE IF EXISTS Users;
DROP TABLE IF EXISTS CreditCards;
DROP TABLE IF EXISTS Shows;
DROP TABLE IF EXISTS Tickets;
DROP TABLE IF EXISTS Orders;
DROP TABLE IF EXISTS Vouchers;
DROP TABLE IF EXISTS Products;
DROP TABLE IF EXISTS Promotions;
DROP TABLE IF EXISTS ProductOrders;

PRAGMA FOREIGN_KEYS = ON;

CREATE TABLE Users (
	id INTEGER PRIMARY KEY,
	name TEXT NOT NULL,
	username TEXT NOT NULL UNIQUE,
	password TEXT NOT NULL,
	nif TEXT NOT NULL UNIQUE
);

CREATE TABLE CreditCards (
	id INTEGER PRIMARY KEY,
	type TEXT NOT NULL,
	number TEXT NOT NULL UNIQUE,
	validity DATE NOT NULL,
	userId INTEGER UNIQUE REFERENCES Users(id)
);

CREATE TABLE Shows (
	id INTEGER PRIMARY KEY,
	date DATETIME NOT NULL
);

CREATE TABLE Tickets (
	id INTEGER PRIMARY KEY,
	seatNumber INTEGER NOT NULL UNIQUE,
	price DOUBLE NOT NULL,
	showId INTEGER NOT NULL REFERENCES Shows(id),
	userId INTEGER REFERENCES Users(id)
);

CREATE TABLE Orders (
	id INTEGER PRIMARY KEY,
	date DATETIME NOT NULL,
	userId INTEGER NOT NULL REFERENCES Users(id)
);

CREATE TABLE Vouchers (
	id INTEGER PRIMARY KEY,
	available BOOLEAN NOT NULL DEFAULT TRUE,
	userId INTEGER NOT NULL REFERENCES Users(id),
	orderId INTEGER REFERENCES Orders(id)
);

CREATE TABLE Products (
	id INTEGER PRIMARY KEY,
	name TEXT NOT NULL,
	price DOUBLE NOT NULL
);

CREATE TABLE Promotions (
	voucherId INTEGER NOT NULL REFERENCES Vouchers(id),
	productId INTEGER NOT NULL REFERENCES Products(id),
	discount DOUBLE NOT NULL,
	PRIMARY KEY (voucherId, productId)
);

CREATE TABLE ProductOrders (
	productId NOT NULL REFERENCES Products(id),
	orderId REFERENCES Orders(id),
	quantity INTEGER NOT NULL,
	PRIMARY KEY (productId, orderId)
);


/* --- TEST DATA --- */

INSERT INTO Users (name, username, password, nif) VALUES ("tiago", "tirafesi", "$2b$10$4hhfZMgRaZ9JerjwAuNSt.Y4EgsELabjubyEnSB0/rfK5ObSJAGG.", "987654321");
INSERT INTO Users (name, username, password, nif) VALUES ("claudia", "arwen7stars", "$2b$10$4hhfZMgRaZ9JerjwAuNSt.Y4EgsELabjubyEnSB0/rfK5ObSJAGG.", "876543210");
INSERT INTO CreditCards (type, number, validity, userId) VALUES ("mastercard", "123456789", "2020-03-21", 1);
INSERT INTO CreditCards (type, number, validity, userId) VALUES ("mastercard", "012345678", "2020-03-21", 2);