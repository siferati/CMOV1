const nodeRSA = require('node-rsa');
const sqlite3 = require('sqlite3').verbose();

const db = new sqlite3.cached.Database('db/db.sqlite3');

module.exports = {

	/**
	 * Verify signature.
	 * 
	 * @param id ID of user that sent the message.
	 * @param message The signed message.
	 * @param signature Signature.
	 * @param callback (boolean) => {}. True if signature is valid, False otherwise.
	 */
	verify: (id, message, signature, callback) => {

		db.get(
			`SELECT keyN, keyE
			FROM Users
			WHERE id = ?`,
			id,
			(err, row) => {
				if (err) {
					console.error(err);
					return callback(false);
				} else if (!row) {
					return callback(false);
				}

				const key = new nodeRSA();
				key.importKey({
					n: Buffer.from(row.keyN, 'hex'),
					e: Buffer.from(row.keyE, 'hex')
				}, 'components-public');

				return callback(key.verify(message, signature, 'utf8', 'base64'));
			}
		);		
	}
};