const sqlite3 = require('sqlite3').verbose();
const crypto = require('./crypto.js');
const Product = require('./product.js');
const Voucher = require('./voucher.js');

const db = new sqlite3.cached.Database('db/db.sqlite3');

// taken from SO
function hasDuplicates(array) {
	return (new Set(array)).size !== array.length;
}

module.exports = {

	create: (req, res) => {

		const productIds = req.body.products;
		const voucherIds = req.body.vouchers;

		Voucher.getMult(voucherIds, (err, vouchers) => {
			if (err) {
				console.error(err);
				return res.sendStatus(500);
			}

			Product.getMult(productIds, (err, products) => {
				if (err) {
					console.error(err);
					return res.sendStatus(500);
				}
	
				res.send({vouchers: vouchers, products: products});
			});
		});

			
	}

	
	/*


	// TODO add vouchers, add transaction
	create: (req, res) => {

		const userId = req.params.userId;
		const signature = req.headers.signature;
		const {
			voucherIds,
			productIds
		} = req.body;


		if (!Array.isArray(voucherIds) || hasDuplicates(voucherIds) ||
			!Array.isArray(productIds) || productIds.length < 1 || hasDuplicates(productIds)) {
			return res.status(400).send('Invalid list of vouchers and/or products.');
		}

		crypto.verify(userId, req.body, signature, (err, valid) => {
			if (err) {
				console.error(err);
				return res.sendStatus(500);
			} else if (valid === undefined) {
				return res.status(400).send('User ID doesn\'t exist.');
			} else if (valid === false) {
				return res.status(400).send('Invalid signature.');
			}

			let sql =
				`SELECT voucherId, productId, discount
				FROM Promotions, Vouchers
				WHERE voucherId = Vouchers.id
				AND available = TRUE
				AND (`;

			let params = [];

			// create sql statement to get N vouchers
			voucherIds.forEach((voucherId) => {
				sql += ' voucherId = ? OR';
				params.push(voucherId);
			});

			// remove last OR and close )
			sql = sql.substr(0, sql.length - 3);
			sql += ')';
			
			// get valid vouchers to use in this order
			db.all(
				sql,
				params,
				(err, rows) => {
					if (err) {
						console.error(err);
						return res.sendStatus(500);
					}

					// vouchers to use in this order
					let vouchers = [];

					rows.forEach((row) => {
						vouchers.push({
							id: row.voucherId,
							product: row.productId,
							discount: row.discount
						});
					});

					let sql = 'SELECT id, price FROM Products WHERE';
					let params = [];

					productIds.forEach((productId) => {
						sql += ' id = ? OR';
						params.push(productId.id);
					});

					// remove last OR
					sql = sql.substr(0, sql.length - 3);

					// get products to order
					db.all(
						sql,
						params,
						(err, rows) => {
							if (err) {
								console.error(err);
								return res.sendStatus(500);
							}

							// products to add to order
							let products = [];

							rows.forEach((row) => {
								products.push({
									id: row.id,
									price: row.price
								});
							});

							// create order
							db.run(
								`INSERT INTO Orders (userId)
								VALUES (?)`,
								userId,
								function (err) {
									if (err) {
										console.error(err);
										return res.sendStatus(500);
									}

									const orderId = this.lastID;

									let sql = 'INSERT INTO ProductOrders (productId, orderId, quantity) VALUES';
									let params = [];

									products.forEach((product) => {
										sql += ' (?, ?, ?),';
										params.push(product.id, orderId, product.quantity);
									});

									// remove last comma
									sql = sql.substr(0, sql.length - 1);

									// add products to orders
									db.run(
										sql,
										params,
										(err) => {
											if (err) {
												console.error(err);
												return res.sendStatus(500);
											}
										}
									);
								}
							);
						}
					);

					

				}
			);					
		});
	}

	*/
};