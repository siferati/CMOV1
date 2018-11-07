const sqlite3 = require('sqlite3').verbose();
const crypto = require('./crypto.js');
const uuidv4 = require('uuid/v4');

const db = new sqlite3.cached.Database('db/db.sqlite3');

// taken from MDN
function getRandomInt(min, max) {
	min = Math.ceil(min);
	max = Math.floor(max);
	return Math.floor(Math.random() * (max - min)) + min; //The maximum is exclusive and the minimum is inclusive
}

module.exports = {

	create: (req, res) => {
		
		const showId = req.params.id;
		const userId = req.body.userId;
		const quantity = parseInt(req.body.quantity);
		const signature = req.headers.signature;

		if (isNaN(quantity) || quantity <= 0) {
			return res.status(400).send('Invalid quantity.');
		} else if (!signature) {
			return res.status(400).send('Signature is missing.');
		}

		db.get(
			`SELECT name, date, price
			FROM Shows
			WHERE id = ?`,
			showId,
			(err, row) => {
				if (err) {
					console.error(err);
					return res.sendStatus(500);
				} else if (!row) {
					return res.status(400).send('Show doesn\'t exist.');
				}

				const name = row.name;
				const date = row.date;
				const price = row.price;

				crypto.verify(userId, req.body, signature, (err, valid) => {
					if (err) {
						console.error(err);
						return res.sendStatus(500);
					} else if (valid === undefined) {
						return res.status(400).send('User ID doesn\'t exist.');
					} else if (valid === false) {
						return res.status(400).send('Invalid signature.');
					}
		
					let sql = 'INSERT INTO Tickets (id, seatNumber, showId, userId) VALUES';
					let params = [];
					let tickets = [];

					// create sql statement for N inserts
					for (let i = 0; i < quantity; i++) {
						const ticketId = uuidv4();
						const seatNumber = getRandomInt(1, 100);
						sql += ' (?, ?, ?, ?),';
						params.push(ticketId, seatNumber, showId, userId);
						tickets.push({
							id: ticketId,
							name: name,
							date: date,
							seatNumber: seatNumber,
							price: price
						});
					}
		
					// remove last comma
					sql = sql.substr(0, sql.length - 1);
		
					db.run(
						sql,
						params,
						function (err) {
							if (err) {
								console.error(err);
								return res.sendStatus(500);
							}
		
							res.send({tickets: tickets});
						}
					);
				});
			}
		);
	}
};