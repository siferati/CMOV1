const sqlite3 = require('sqlite3').verbose();
const crypto = require('./crypto.js');
const Product = require('./product.js');
const Voucher = require('./voucher.js');
const uuidv4 = require('uuid/v4');
const User = require('./user.js');

const db = new sqlite3.cached.Database('db/db.sqlite3');

// taken from SO
function hasDuplicates(array) {
	return (new Set(array)).size !== array.length;
}

module.exports = {

	all: (req, res) => {

		const userId = req.params.id;

		// get basic info from orders belonging to user
		db.all(
			`SELECT id, date, price
			FROM Orders
			WHERE userId = ?`,
			userId,
			(err, orders) => {
				if (err) {
					console.error(err);
					return res.sendStatus(500);
				}

				if (orders.length < 1) {
					return res.send(orders);
				}

				// get info about products of each order
				let ordersToUpdate = orders.length;
				orders.forEach(order => {					
					db.all(
						`SELECT id, name, image, quantity
						FROM ProductOrders, Products
						WHERE orderId = ?
						AND productId = id`,
						order.id,
						(err, products) => {
							if (err) {
								console.error(err);
								return res.sendStatus(500);
							}

							// update each order with their products
							order = Object.assign(order, { products: products });
							ordersToUpdate--;

							// all orders are updated, continue
							if (ordersToUpdate === 0) {

								// get info about vouchers of each order
								let ordersToUpdate = orders.length;
								orders.forEach (order => {
									db.all(
										`SELECT id
										FROM Vouchers
										WHERE orderId = ?
										AND userId = ?`,
										[order.id, userId],
										(err, vouchers) => {
											if (err) {
												console.error(err);
												return res.sendStatus(500);
											}

											// update each order with their vouchers
											order = Object.assign(order, { vouchers: vouchers });
											ordersToUpdate--;

											// all orders are updated, respond to request
											if (ordersToUpdate === 0) {
												return res.send(orders);
											}
										}
									);
								});
							}
						}
					);
				});
			}
		);
	},

	create: (req, res) => {
		
		// read provided info
		const userId = req.params.id;
		const productIds = req.body.products.map(product => product.id);
		const productQts = req.body.products.map(product => product.quantity);
		const voucherIds = req.body.vouchers;
		const signature = req.headers.signature || '';

		// check for bad request
		if (!Array.isArray(voucherIds) || hasDuplicates(voucherIds) ||
			!Array.isArray(productIds) || productIds.length < 1 || hasDuplicates(productIds) ||
			productIds.some(isNaN) || productIds.includes(1) || productQts.some(isNaN)) {
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

			// get full info on vouchers
			Voucher.getMult(voucherIds, (err, vouchers) => {
				if (err) {
					console.error(err);
					return res.sendStatus(500);
				}
				
				// get full info on products
				Product.getMult(productIds, (err, products) => {
					if (err) {
						console.error(err);
						return res.sendStatus(500);
					}
					
					// append order quantity to each product info
					for (let i = 0; i < products.length; i++) {
						products[i] = Object.assign(products[i], {quantity: productQts[i]});
					}

					// vouchers used in this order
					let usedVouchers = [];

					// price to pay for the order
					let price = 0;

					// raw price (no discounts)
					products.forEach(product => price += product.quantity * product.price);

					// check for available discounts
					vouchers.forEach((voucher) => {
						if (voucher.orderId !== null) {
							return;
						}

						products.forEach((product) => {
							for (let i = 0; i < voucher.promotions.length; i++) {
								let promotion = voucher.promotions[i];
								if (promotion.productId === product.id) {
									// update price with discount (only affects one unit!)
									price -= promotion.discount * product.price;
									usedVouchers.push(voucher);
								}
							}
						});
					});

					// remove duplicates
					usedVouchers = Array.from(new Set(usedVouchers).values());

					// check of special voucher
					vouchers.forEach(voucher => {
						if (voucher.orderId !== null) {
							return;
						}
						for (let i = 0; i < voucher.promotions.length; i++) {
							let promotion = voucher.promotions[i];
							if (promotion.productId == 1) {
								price = price * 0.95;
								usedVouchers.push(voucher);
								return;
							}
						}
					});

					// 2 decimal places
					price = price.toFixed(2);

					// get money user spent so far
					User.getMoneySpent(userId, (err, moneySpent) => {
						if (err) {
							console.error(err);
							return res.sendStatus(500);
						}

						// only interested in the hundreds place
						const hdsMoneySpent = Math.floor(parseFloat(moneySpent) / 100);
						const hdsNewMoneySpent = Math.floor((parseFloat(moneySpent) + parseFloat(price)) / 100);
						const newSpecialVoucher = hdsNewMoneySpent > hdsMoneySpent;

						// begin transaction in new serialized db connection
						const transdb = new sqlite3.Database('db/db.sqlite3');
						transdb.serialize();
						transdb.run('BEGIN TRANSACTION');

						// create new order in db
						transdb.run(
							`INSERT INTO Orders (price, userId)
							VALUES (?, ?)`,
							[price, userId],
							function (err) {
								if (err) {
									console.error(err);
									transdb.run('ROLLBACK');
									transdb.close();
									return res.sendStatus(500);
								}

								const orderId = this.lastID;
								const usedVoucherIds = usedVouchers.map(voucher => voucher.id);

								// update tickets with new order
								Voucher.updateMult(transdb, orderId, usedVoucherIds, (err) => {
									if (err) {
										console.error(err);
										transdb.run('ROLLBACK');
										transdb.close();
										return res.sendStatus(500);
									}

									// add product to orders
									Product.addOrder(transdb, orderId, products, (err) => {
										if (err) {
											console.error(err);
											transdb.run('ROLLBACK');
											transdb.close();
											return res.sendStatus(500);
										}

										// if there's a need for a new special voucher
										if (newSpecialVoucher) {

											// create special voucher
											const voucherId = uuidv4();
											transdb.run(
												`INSERT INTO Vouchers (id, userId)
												VALUES (?, ?)`,
												[voucherId, userId],
												(err) => {
													if (err) {
														console.error(err);
														transdb.run('ROLLBACK');
														transdb.close();
														return res.sendStatus(500);
													}

													// create promotion
													transdb.run(
														`INSERT INTO Promotions (voucherId, productId, discount)
														VALUES (?, ?, ?)`,
														[voucherId, 1, 0.05],
														(err) => {
															if (err) {
																console.error(err);
																transdb.run('ROLLBACK');
																transdb.close();
																return res.sendStatus(500);
															}

															// commit transaction and close db connection
															transdb.run('COMMIT');
															transdb.close();

															res.send({ id: orderId, price, products, usedVoucherIds });
														}	
													);
												}
											);
										}
										else {
											// commit transaction and close db connection
											transdb.run('COMMIT');
											transdb.close();

											res.send({ id: orderId, price, products, usedVoucherIds });
										}
									});
								});
							}
						);
					});					
				});
			});
		});
	}
};