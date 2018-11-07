const sqlite3 = require('sqlite3').verbose();

const db = new sqlite3.cached.Database('db/db.sqlite3');

module.exports = {

	all: (req, res) => {

		const limit = parseInt(req.query.limit);
		const offset = parseInt(limit * (req.query.page - 1));

		if (isNaN(limit) || limit < 0 || isNaN(offset) || offset < 0) {
			return res.status(400).send('Limit and/or page number missing.');
		}

		db.all(
			`SELECT name, description, date
			FROM Shows
			ORDER BY date ASC
			LIMIT ?, ?`,
			[offset, limit],
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