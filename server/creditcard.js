const sqlite3 = require('sqlite3').verbose();

const db = new sqlite3.cached.Database('db/db.sqlite3');

module.exports = {

	get: (req, res) => {
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
	},

	create: (req, res) => {
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
	},

	update: (req, res) => {
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
	}

};