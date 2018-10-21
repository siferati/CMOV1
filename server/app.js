const express = require('express');
const bodyParser = require('body-parser');
const sqlite3 = require('sqlite3').verbose();

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

app.get('/users/:id', (req, res) => {
	const id = req.params.id;
	const sql = `
			SELECT *
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

			db.run(
				`INSERT INTO Users (name, username, password, nif)
				VALUES (?, ?, ?, ?)`,
				[name, username, password, nif],
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

app.put('/users/:id', (req, res) => {
	const id = req.params.id;
	const {
		name,
		password,
		nif
	} = req.body;

	db.run(
		`UPDATE Users
		SET name = coalesce(?, name), password = coalesce(?, password), nif = coalesce(?, nif)
		WHERE id = ?`,
		[name, password, nif, id],
		(err) => {
			if (err) {
				console.error(err);
				return res.sendStatus(500);
			}
			res.send();
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