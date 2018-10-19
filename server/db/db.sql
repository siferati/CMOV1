
.mode columns
.headers on
.nullvalue NULL
PRAGMA FOREIGN_KEYS = ON;

DROP TABLE IF EXISTS Users;
CREATE TABLE Users (
	id INTEGER PRIMARY KEY,
	name TEXT NOT NULL,
	username TEXT NOT NULL UNIQUE,
	password TEXT NOT NULL,
	nif TEXT NOT NULL,
	creditCardId INTEGER NOT NULL UNIQUE REFERENCES CreditCards(id)
);

DROP TABLE IF EXISTS  CreditCards;
CREATE TABLE CreditCards (
	id INTEGER PRIMARY KEY,
	type TEXT NOT NULL,
	number TEXT NOT NULL UNIQUE,
	validity DATE NOT NULL
);

DROP TABLE IF EXISTS  Shows;
CREATE TABLE Shows (
	id INTEGER PRIMARY KEY,
	date DATETIME NOT NULL
);

DROP TABLE IF EXISTS  Tickets;
CREATE TABLE Tickets (
	id INTEGER PRIMARY KEY,
	seatNumber INTEGER NOT NULL UNIQUE,
	price DOUBLE NOT NULL,
	showId INTEGER NOT NULL REFERENCES Shows(id),
	userId INTEGER REFERENCES Users(id)
);

DROP TABLE IF EXISTS  Orders;
CREATE TABLE Orders (
	id INTEGER PRIMARY KEY,
	date DATETIME NOT NULL,
	userId INTEGER NOT NULL REFERENCES Users(id)
);

DROP TABLE IF EXISTS  Vouchers;
CREATE TABLE Vouchers (
	id INTEGER PRIMARY KEY,
	available BOOLEAN NOT NULL DEFAULT TRUE,
	userId INTEGER NOT NULL REFERENCES Users(id),
	orderId INTEGER REFERENCES Orders(id)
);

DROP TABLE IF EXISTS  Products;
CREATE TABLE Products (
	id INTEGER PRIMARY KEY,
	name TEXT NOT NULL,
	price DOUBLE NOT NULL
);

DROP TABLE IF EXISTS  Promotions;
CREATE TABLE Promotions (
	voucherId INTEGER NOT NULL REFERENCES Vouchers(id),
	productId INTEGER NOT NULL REFERENCES Products(id),
	discount DOUBLE NOT NULL,
	PRIMARY KEY (voucherId, productId)
);

DROP TABLE IF EXISTS  ProductOrders;
CREATE TABLE ProductOrders (
	productId NOT NULL REFERENCES Products(id),
	orderId REFERENCES Orders(id),
	quantity INTEGER NOT NULL,
	PRIMARY KEY (productId, orderId)
);


/* --- TEST DATA --- */

INSERT INTO CreditCards (type, number, validity) VALUES ("mastercard", "123456789", "2020-03-21");
INSERT INTO CreditCards (type, number, validity) VALUES ("mastercard", "012345678", "2020-03-21");
INSERT INTO Users (name, username, password, nif, creditCardId) VALUES ("tiago", "tirafesi", "123456", "987654321", 1);
INSERT INTO Users (name, username, password, nif, creditCardId) VALUES ("claudia", "arwen7stars", "123456", "987654321", 2);