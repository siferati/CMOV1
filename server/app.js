const express = require('express')
const bodyParser = require('body-parser');
const sqlite3 = require('sqlite3').verbose()

const app = express()
const db = new sqlite3.Database('db/db.sqlite3')

app.use(bodyParser.json())

app.get('/', (req, res) => {

	db.serialize(function () {

		db.each('SELECT * FROM Users', function (err, row) {
			console.log(row)
		})
	})
	res.send('Hello World!')
})

app.get('/users/:id', (req, res) => {
	const id = req.params.id
	sql = `
		SELECT *
		FROM Users
		WHERE id = ?;`
	db.get(sql, id, (err, row) => {
		if (err) {
			console.log(err)
			return res.status(500).send("Internal Server Error.");
		} else {
			return res.send(row);
		}
	})
});

app.post('/users', (req, res) => {
	const { firstName, lastName, phone } = req.body
	// TODO: create user
});

app.put('/users/:id', (req, res) => {
	const id = parseInt(req.params.id)
	const { firstName, lastName, phone } = req.body
	// TODO: find and update user by id
});



function cleanup () {
	console.log('Closing connection to database...')
	db.close((error) => {
		if (error !== null) {
			console.log("Failed to close the connection - forced exit.")
		} else {
			console.log("Connection closed.")
		}
		process.exit()		
	});
}

process.on('SIGINT', cleanup);
process.on('SIGTERM', cleanup);

app.listen(8080, () => {
	console.log(`Server running on port 8080.`)
})