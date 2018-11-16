const express = require('express');
const bodyParser = require('body-parser');
const sqlite3 = require('sqlite3').verbose();

const user = require('./user.js');
const creditcard = require('./creditcard.js');
const show = require('./show.js');
const ticket = require('./ticket.js');
const voucher = require('./voucher.js');
const product = require('./product.js');
const order = require('./order.js');

const app = express();

app.use(bodyParser.json());

// turn on foreign keys support
const db = new sqlite3.cached.Database('db/db.sqlite3');
db.run('PRAGMA foreign_keys = ON', (err) => {if (err) console.error(err);});

/* --- User --- */
app.post('/login', user.login);
app.get('/users/:id', user.get);
app.post('/users', user.create);
app.put('/users/:id', user.update);

/* --- Credit Card --- */
app.get('/users/:id/creditcard', creditcard.get);
app.post('/users/:id/creditcard', creditcard.create);
app.put('/users/:id/creditcard', creditcard.update);

/* --- Show --- */
app.get('/shows', show.all);

/* --- Ticket --- */
app.post('/shows/:id/tickets', ticket.create);
app.post('/shows/:id/tickets/validation', ticket.validate);
app.get('/users/:id/tickets', ticket.all);

/* --- Voucher --- */
app.get('/users/:id/vouchers', voucher.all);
app.get('/users/:userId/vouchers/:voucherId', voucher.get);

/* --- Product --- */
app.get('/products', product.all);
app.get('/products/:id', product.get);

/* --- Order --- */
app.post('/users/:id/orders', order.create);
app.get('/users/:id/orders', order.all);

/**
 * Close the db connection on exiting.
 */
function cleanup () {
	console.log('Closing connection to database...');
	db.close((error) => {
		if (error !== null) {
			console.log('Failed to close the connection - forced exit.');
		} else {
			console.log('Connection closed.');
		}
		process.exit();		
	});
}

process.on('SIGINT', cleanup);
process.on('SIGTERM', cleanup);

app.listen(8080, () => {
	console.log('Server running on port 8080.');
});