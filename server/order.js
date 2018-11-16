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
			/*if (err) {
				console.error(err);
				return res.sendStatus(500);
			} else if (valid === undefined) {
				return res.status(400).send('User ID doesn\'t exist.');
			} else if (valid === false) {
				return res.status(400).send('Invalid signature.');
			}*/

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

					// create new order in db
					db.run(
						`INSERT INTO Orders (price, userId)
						VALUES (?, ?)`,
						[price, userId],
						function (err) {
							if (err) {
								console.error(err);
								return res.sendStatus(500);
							}

							const orderId = this.lastID;
							const usedVoucherIds = usedVouchers.map(voucher => voucher.id);

							// update tickets with new order
							Voucher.updateMult(orderId, usedVoucherIds, (err) => {
								if (err) {
									console.error(err);
									return res.sendStatus(500);
								}

								// add product to orders
								Product.addOrder(orderId, products, (err) => {
									if (err) {
										console.error(err);
										return res.sendStatus(500);
									}

									res.send({ orderId, price, products, usedVoucherIds });
								});
							});
						}
					);
				});
			});
		});
	}
};