const express = require('express');
const bodyParser = require('body-parser');
const sqlite3 = require('sqlite3').verbose();
const bcrypt = require('bcrypt');

const app = express();
const db = new sqlite3.Database('db/db.sqlite3');

app.use(bodyParser.json());

// TODO delete this
app.get('/', (req, res) => {

	db.all('SELECT * FROM Users', (err, rows) => {
		res.send(rows);
	});
});


//#region Users

/**
 * Get user by id.
 */
app.get('/users/:id', (req, res) => {
	const id = req.params.id;
	const sql = `
			SELECT name, username, nif, creditCardId
			FROM Users
			WHERE id = ?
	`;
	db.get(sql, id, (err, row) => {
		if (err) {
			console.error(err);
			return res.status(500).send('Internal Server Error.');
		}

		res.send(row);
	});
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