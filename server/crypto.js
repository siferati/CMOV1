const rsa = require('node-rsa');
const sqlite3 = require('sqlite3').verbose();

const db = new sqlite3.cached.Database('db/db.sqlite3');

module.exports = {

	/**
	 * Verifies given signature.
	 * 
	 * @param id ID of user that sent the message.
	 * @param message The signed message.
	 * @param signature Signature.
	 * @param callback (err, boolean) => {}.
	 * 						err: In case an error happens.
	 * 						boolean: True if signature is valid, False otherwise.
	 */
	verify: (id, message, signature, callback) => {

		db.get(
			`SELECT keyN, keyE
			FROM Users
			WHERE id = ?`,
			id,
			(err, row) => {
				if (err || !row) {
					return callback(err, undefined);
				}

				// create public key from db values
				const key = new rsa();
				key.importKey({
					n: Buffer.from(row.keyN, 'hex'),
					e: parseInt(row.keyE, 16)
				}, 'components-public');

				// verify signature
				callback(err, key.verify(message, signature, 'utf8', 'base64'));
			}
		);		
	}
};