const express = require('express');
const bodyParser = require('body-parser');
const sqlite3 = require('sqlite3').verbose();
const bcrypt = require('bcrypt');

const app = express();
const db = new sqlite3.Database('db/db.sqlite3');

// turn on foreign keys support
db.run('PRAGMA foreign_keys = ON', (err) => {if (err) console.error(err);});

app.use(bodyParser.json());


//#region Users

/**
 * Login.
 */
app.post('/login', (req, res) => {
	const {
		username,
		password
	} = req.body;
	
	db.get(
		`SELECT password AS hash
		FROM Users
		WHERE username = ?`,
		[username],
		(err, row) => {
			if (err) {
				console.error(err);
				return res.sendStatus(500);
			} else if (!row) {
				return res.status(400).send('Username doesn\'t exist.');
			}

			bcrypt.compare(password, row.hash, (err, same) => {
				if (err) {
					console.error(err);
					return res.sendStatus(500);
				} else if (!same) {
					return res.status(400).send('Wrong password.');
				}

				res.sendStatus(200);
			});
		}
	);
});


/**
 * Get user by id.
 */
app.get('/users/:id', (req, res) => {
	const id = req.params.id;
	

	db.get(
		`SELECT name, username, nif
		FROM Users
		WHERE id = ?`,
		id,
		(err, row) => {
			if (err) {
				console.error(err);
				return res.sendStatus(500);
			}

			res.send(row);
		}
	);
});


/**
 * Create a new user.
 */
app.post('/users', (req, res) => {
	const {
		name,
		username,
		password,
		nif
	} = req.body;

	db.get(
		`SELECT id
		FROM Users
		WHERE username = ?
		OR nif = ?`,
		[username, nif],
		(err, row) => {
			if (err) {
				console.error(err);
				return res.sendStatus(500);
			} else if (row) {
				return res.status(400).send('Username and/or NIF already exist.');
			}

			bcrypt.hash(password, 10, (err, hash) => {
				if (err) {
					console.error(err);
					return res.sendStatus(500);
				}

				db.run(
					`INSERT INTO Users (name, username, password, nif)
					VALUES (?, ?, ?, ?)`,
					[name, username, hash, nif],
					function (err) {
						if (err) {
							console.error(err);
							return res.sendStatus(500);
						}

						res.send({id: this.lastID});
					}
				);
			});
		}
	);
});


/**
 * Update user by id.
 */
app.put('/users/:id', (req, res) => {
	const id = req.params.id;
	const {
		name,
		password,
		nif
	} = req.body;

	// in case password is null
	function update(hash) {
		db.run(
			`UPDATE Users
			SET name = coalesce(?, name), password = coalesce(?, password), nif = coalesce(?, nif)
			WHERE id = ?`,
			[name, hash, nif, id],
			(err) => {
				if (err) {
					console.error(err);
					return res.sendStatus(500);
				}

				res.sendStatus(200);
			}
		);
	}

	if (password) {
		bcrypt.hash(password, 10, (err, hash) => {
			if (err) {
				console.error(err);
				return res.sendStatus(500);
			}
			
			update(hash);
		});
	} else {
		update(null);
	}
});


/**
 * Get user's credit card.
 */
app.get('/users/:id/creditcard/', (req, res) => {
	const id = req.params.id;

	db.get(
		`SELECT id, type, number, validity
		FROM CreditCards
		WHERE userId = ?`,
		id,
		(err, row) => {
			if (err) {
				console.error(err);
				return res.sendStatus(500);
			}

			res.send(row);
		}
	);
});


/**
 * Create new user's credit card.
 */
app.post('/users/:id/creditcard', (req, res) => {
	const id = req.params.id;
	const {
		type,
		number,
		validity
	} = req.body;

	db.get(
		`SELECT id
		FROM CreditCards
		WHERE number = ?
		OR userId = ?`,
		[number, id],
		(err, row) => {
			if (err) {
				console.error(err);
				return res.sendStatus(500);
			} else if (row) {
				return res.status(400).send('Credit card already registered.');
			}

			db.run(
				`INSERT INTO CreditCards (type, number, validity, userId)
				VALUES (?, ?, ?, ?)`,
				[type, number, validity, id],
				function (err) {
					if (err) {
						console.error(err);
						return res.sendStatus(500);
					}

					res.send({id: this.lastID});
				}
			);
		}
	);
});


/**
 * Update user's credit card.
 */
app.put('/users/:id/creditcard', (req, res) => {
	const id = req.params.id;
	const {
		type,
		number,
		validity
	} = req.body;

	db.run(
		`UPDATE CreditCards
		SET type = coalesce(?, type), number = coalesce(?, number), validity = coalesce(?, validity)
		WHERE userId = ?`,
		[type, number, validity, id],
		(err) => {
			if (err) {
				console.error(err);
				return res.sendStatus(500);
			}

			res.sendStatus(200);
		}
	);
});

//#endregion

function cleanup () {
	console.log('Closing connection to database...');
	db.close((error) => {
		if (error !== null) {
			console.log('Failed to close the connection - forced exit.');
		} else {
			console.log('Connection closed.');
		}
		process.exit();		
	});
}

process.on('SIGINT', cleanup);
process.on('SIGTERM', cleanup);

app.listen(8080, () => {
	console.log('Server running on port 8080.');
});