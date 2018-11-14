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

					// tickets
					let sqlTickets = 'INSERT INTO Tickets (id, seatNumber, showId, userId) VALUES';
					let paramsTickets = [];
					let tickets = [];

					// vouchers
					let sqlVouchers = 'INSERT INTO Vouchers (id, userId) VALUES';
					let paramsVouchers = [];
					let vouchers = [];

					// promotions
					let sqlPromotions = 'INSERT INTO Promotions (voucherId, productId, discount) VALUES';
					let paramsPromotions = [];

					// create sql statement for N inserts
					for (let i = 0; i < quantity; i++) {

						// tickets
						const ticketId = uuidv4();
						const seatNumber = getRandomInt(1, 100);
						sqlTickets += ' (?, ?, ?, ?),';
						paramsTickets.push(ticketId, seatNumber, showId, userId);

						// vouchers
						const voucherId = uuidv4();
						sqlVouchers += ' (?, ?),';
						paramsVouchers.push(voucherId, userId);

						// promotions
						const productId = getRandomInt(2, 4); // 2: coffee, 3: popcorn
						let productName = productId === 2 ? 'Coffee' : 'Popcorn';
						const discount = 1.0; // free
						sqlPromotions += ' (?, ?, ?),';
						paramsPromotions.push(voucherId, productId, discount);

						// store for response
						tickets.push({
							id: ticketId,
							name: name,
							date: date,
							seatNumber: seatNumber,
							price: price
						});
						vouchers.push({
							id: voucherId,
							name: productName,
							discount: discount
						});
					}
		
					// remove last comma
					sqlTickets = sqlTickets.substr(0, sqlTickets.length - 1);
					sqlVouchers = sqlVouchers.substr(0, sqlVouchers.length - 1);
					sqlPromotions = sqlPromotions.substr(0, sqlPromotions.length - 1);
		
					// create tickets
					db.run(
						sqlTickets,
						paramsTickets,
						(err) => {
							if (err) {
								console.error(err);
								return res.sendStatus(500);
							}

							// create vouchers
							db.run(
								sqlVouchers,
								paramsVouchers,
								(err) => {
									if (err) {
										console.error(err);
										return res.sendStatus(500);
									}

									// create promotions
									db.run(
										sqlPromotions,
										paramsPromotions,
										(err) => {
											if (err) {
												console.error(err);
												return res.sendStatus(500);
											}

											return res.send({
												tickets: tickets,
												vouchers: vouchers
											});
										}										
									);
								}
							);
						}
					);					
				});
			}
		);
	}
};