
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
	id TEXT PRIMARY KEY,
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
	name TEXT NOT NULL,
	date DATETIME NOT NULL
);

CREATE TABLE Tickets (
	id INTEGER PRIMARY KEY,
	seatNumber INTEGER NOT NULL,
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
/* --- USERS --- */

INSERT INTO Users (id, name, username, password, nif) VALUES ("one", "tiago", "tirafesi", "$2b$10$4hhfZMgRaZ9JerjwAuNSt.Y4EgsELabjubyEnSB0/rfK5ObSJAGG.", "987654321");
INSERT INTO Users (id, name, username, password, nif) VALUES ("two", "claudia", "arwen7stars", "$2b$10$4hhfZMgRaZ9JerjwAuNSt.Y4EgsELabjubyEnSB0/rfK5ObSJAGG.", "876543210");
INSERT INTO CreditCards (type, number, validity, userId) VALUES ("Master Card", "123456789", "2020-03-21", "one");
INSERT INTO CreditCards (type, number, validity, userId) VALUES ("Master Card", "012345678", "2020-03-21", "two");

/* --- SHOWS --- */

INSERT INTO Shows (name, date) VALUES ("Dead Combo", "2019-10-25");
INSERT INTO Shows (name, date) VALUES ("Jojo Mayer & Nerve", "2019-10-26");
INSERT INTO Shows (name, date) VALUES ("Anna von Hausswolff", "2019-11-04");
INSERT INTO Shows (name, date) VALUES ("Júlio Resende", "2019-11-13");
INSERT INTO Shows (name, date) VALUES ("Festival Termómetro", "2019-11-16");

/* --- TICKETS --- */

INSERT INTO Tickets (seatNumber, price, showId) VALUES (1, 10.00, 1);
INSERT INTO Tickets (seatNumber, price, showId) VALUES (2, 15.00, 1);
INSERT INTO Tickets (seatNumber, price, showId) VALUES (3, 20.00, 1);

INSERT INTO Tickets (seatNumber, price, showId) VALUES (1, 17.00, 2);
INSERT INTO Tickets (seatNumber, price, showId) VALUES (2, 20.00, 2);

INSERT INTO Tickets (seatNumber, price, showId) VALUES (1, 5.00, 3);
INSERT INTO Tickets (seatNumber, price, showId) VALUES (2, 7.50, 3);
INSERT INTO Tickets (seatNumber, price, showId) VALUES (3, 10.00, 3);

INSERT INTO Tickets (seatNumber, price, showId) VALUES (1, 20.00, 4);

INSERT INTO Tickets (seatNumber, price, showId) VALUES (1, 10.00, 5);