const sqlite3 = require('sqlite3').verbose();
const bcrypt = require('bcrypt');
const uuidv4 = require('uuid/v4');

const db = new sqlite3.cached.Database('db/db.sqlite3');

module.exports = {

	login: (req, res) => {

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
	},

	get: (req, res) => {

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
	},

	create: (req, res) => {
		const {
			name,
			username,
			password,
			nif,
			keyN,
			keyE
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
	
					const id = uuidv4();
					db.run(
						`INSERT INTO Users (id, name, username, password, nif, keyN, keyE)
						VALUES (?, ?, ?, ?, ?, ?, ?)`,
						[id, name, username, hash, nif, keyN, keyE],
						function (err) {
							if (err) {
								console.error(err);
								return res.sendStatus(500);
							}
	
							res.send({id: id});
						}
					);
				});
			}
		);
	},

	update: (req, res) => {
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
	}
};