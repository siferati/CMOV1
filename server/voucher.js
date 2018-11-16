const sqlite3 = require('sqlite3').verbose();

const db = new sqlite3.cached.Database('db/db.sqlite3');

module.exports = {

	updateMult: (orderId, ids, callback) => {

		if (!Array.isArray(ids) || ids.length < 1) {
			return callback(null);
		}

		let sql = 'UPDATE Vouchers SET orderId = ? WHERE';

		ids.forEach(() => {
			sql += ' id = ? OR';
		});
		sql = sql.substr(0, sql.length - 3);
		ids.unshift(orderId);

		db.run(sql, ids, (err) => {
			ids.shift();
			callback(err);
		});
	},

	getMult: (ids, callback) => {

		if (!Array.isArray(ids) || ids.length < 1) {
			return callback(null, []);
		}

		let sql = 'SELECT id, orderId FROM Vouchers WHERE';

		ids.forEach(() => {
			sql += ' id = ? OR';
		});
		sql = sql.substr(0, sql.length - 3);

		db.all(sql, ids, (err, vouchers) => {
			if (err) {
				return callback(err, vouchers);
			}

			let vouchersToUpdate = vouchers.length;

			vouchers.forEach((voucher) => {

				db.all(
					`SELECT productId, discount
					FROM Promotions
					WHERE voucherId = ?`,
					voucher.id,
					(err, promotions) => {
						if (err) {
							return callback(err, vouchers);
						}
	
						voucher = Object.assign(voucher, {promotions: promotions});
						vouchersToUpdate--;

						if (vouchersToUpdate === 0) {
							return callback(err, vouchers);
						}
					}
				);
			});
		});
	},

	get: (req, res) => {
		
		const userId = req.params.userId;
		const voucherId = req.params.voucherId;
	
		db.get(
			`SELECT id, orderId
			FROM Vouchers
			WHERE userId = ?
			AND id = ?`,
			[userId, voucherId],
			(err, voucher) => {
				if (err) {
					console.error(err);
					return res.sendStatus(500);
				} else if (!voucher) {
					return res.send({});
				}
	
				db.all(
					`SELECT productId, discount
					FROM Promotions
					WHERE voucherId = ?`,
					voucher.id,
					(err, promotions) => {
						if (err) {
							console.error(err);
							return res.sendStatus(500);
						}
	
						voucher = Object.assign(voucher, {promotions: promotions});
						res.send(voucher);
					}
				);
			}
		);
	},

	all: (req, res) => {

		const id = req.params.id;

		db.all(
			`SELECT id, orderId
			FROM Vouchers
			WHERE userId = ?`,
			id,
			(err, vouchers) => {
				if (err) {
					console.error(err);
					return res.sendStatus(500);
				}

				let vouchersToUpdate = vouchers.length;

				vouchers.forEach((voucher) => {

					db.all(
						`SELECT productId, discount
						FROM Promotions
						WHERE voucherId = ?`,
						voucher.id,
						(err, promotions) => {
							if (err) {
								console.error(err);
								return res.sendStatus(500);
							}
		
							voucher = Object.assign(voucher, {promotions: promotions});
							vouchersToUpdate--;

							if (vouchersToUpdate === 0) {
								res.send(vouchers);
							}
						}
					);
				});
			}
		);		
	}

};