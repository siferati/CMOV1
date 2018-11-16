# Ticket and Payment System

### How to run

#### Create / Reset Database

 * `cd server/db/`
 * `sqlite3 db.sqlite3 < db.sql`

#### Run Local Server

 * `cd server/`
 * `npm install`
 * `npm start`

### RESTful Web Services

| Verb  | Endpoint | Req. Body | Res. Body | Description |
| ----- | -------- | --------- | --------- | ----------- |
| POST | /login | { username, password } | { } | Check if credentials match. |
| GET   | /users/:id | { } | { name, username, nif } | Get user by id. |
| POST | /users | { name, username, password, nif, keyN, keyE } | { id } | Create a new user. |
| PUT | /users/:id | { name, password, nif } | { } | Update user by id. |
| GET   | /users/:id/creditcard | { } | { id, type, number, validity } | Get user's credit card. |
| POST | /users/:id/creditcard | { type, number, validity } | { id } | Create new user's credit card. |
| PUT | /users/:id/creditcard | { type, number, validity } | { } | Update user's credit card. |
| GET | /shows?page=\<PAGE\>&limit=\<LIMIT\> | { } | [ { id, name, description, date, price } ] | Get the next airing shows. |
| POST | /shows/:id/tickets | { userId, quantity } | { tickets: [ { id, name, date, seatNumber, price }, ... ], vouchers: [ { id, name, discount }, ... ] } | Buy tickets for a show. **(signed)** |
| POST | /shows/:id/tickets/validation | { userId, [ ticketId, ... ] } | { valid: [ ticketId, ... ], invalid: [ ticketId, ... ] } | Validate tickets for a show. |
| GET | /users/:id/tickets | { } | [ { id, available, name, date, seatNumber, price }, ... ] | Get all tickets belonging to a user. |
| GET | /products | { } | [ { id, name, price }, ... ] | Get all products. |
| GET | /products/:id | { } | { id, name, price } | Get a product. |
| GET | /users/:id/vouchers | { } | [ { id, orderId, promotions: [ { productId, discount }, ... ] }, ... ] | Get all vouchers belonging to an user. |
| GET | /users/:userId/vouchers/:voucherId | { } | { id, orderId, promotions: [ { productId, discount }, ... ] } | Get a voucher belonging to an user. |
| POST | /users/:id/orders | { products: [ { id, quantity }, ... ], vouchers: [ id, ... ] } | { orderId, price, products: [ { id, name, quantity }, ... ], usedVouchers: [ id, ... ] } | Create a new order. **(signed)** |