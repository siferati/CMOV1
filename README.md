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
| GET   | /users/:id | { } | { id, name, username,password, nif, creditCardId } | Get user by id. |
| POST | /users | { name, username, password, nif } | { id } | Create a new user. |
| PUT | /users/:id | { name, password, nif } | { } | Update user by id. |

