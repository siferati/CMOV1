const sqlite3 = require('sqlite3').verbose();

const db = new sqlite3.cached.Database('db/db.sqlite3');

module.exports = {

	getMult: (ids, callback) => {

		let sql = 'SELECT id, name, price FROM Products WHERE';

		ids.forEach(() => {
			sql += ' id = ? OR';
		});
		sql = sql.substr(0, sql.length - 3);

		db.all(sql, ids, (err, rows) => {
			return callback(err, rows);
		});
	},

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