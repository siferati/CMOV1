const express = require('express')
const sqlite3 = require('sqlite3').verbose()

const app = express()
const port = 8080
const db = new sqlite3.Database('db/db.db')

app.get('/', (req, res) => {

	db.serialize(function () {

		db.each('SELECT * FROM Users', function (err, row) {
			console.log(err)
			console.log(row)
		})
	})
	res.send('Hello World!')
})

function cleanup () {
	console.log('closing db connection...')
	db.close((error) => {
		if (error == null) {
			console.log("db closed.")
		} else {
			console.log("error closing db.")
			console.log(error)
		}
		process.exit()		
	});
}

process.on('SIGINT', cleanup);
process.on('SIGTERM', cleanup);

app.listen(port, () => console.log(`Example app listening on port ${port}!`))