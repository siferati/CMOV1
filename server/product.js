const sqlite3 = require('sqlite3').verbose();

const db = new sqlite3.cached.Database('db/db.sqlite3');

module.exports = {

	get: (req, res) => {
		
		const id = req.params.id;
	
		db.get(
			`SELECT id, name, price, image
			FROM Products
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

	all: (req, res) => {

		db.all(
			`SELECT id, name, price, image
			FROM Products
			WHERE name != 'total'`,
			(err, rows) => {
				if (err) {
					console.error(err);
					return res.sendStatus(500);
				}
				
				res.send(rows);
			}
		);
	}
};