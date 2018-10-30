const sqlite3 = require('sqlite3').verbose();

const db = new sqlite3.cached.Database('db/db.sqlite3');

module.exports = {

	/* TODO pagination */
	all: (req, res) => {

		db.all(
			`SELECT name, date
			FROM Shows
			ORDER BY date DESC`,
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