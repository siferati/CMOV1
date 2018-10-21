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

| Status | Response  |
| ------ | --------- |
| 200    | `json`                          |
|        | `   {`                          |
|        | ` "id": 10,`                    |
|        | ` "username": "alanpartridge",` |
|        | ` more code...`                 |
|        | `}`                             |
| 400    |                                 |

| Verb  | Endpoint | Req. Body | Res. Body | Description |
| :---: | :------: | :-------- | :-------- | :--------- |
| GET   | `/users/:id` | <code>{}</code> | <code>{<br>&nbsp;&nbsp;id,<br>&nbsp;&nbsp;name,<br>&nbsp;&nbsp;username,<br>&nbsp;&nbsp;password,<br>&nbsp;&nbsp;nif,<br>&nbsp;&nbsp;creditCardId,<br>}</code> |Get user by id. |
| POST | `/users` | <code>{<br>&nbsp;&nbsp;name,<br>&nbsp;&nbsp;username,<br>&nbsp;&nbsp;password,<br>&nbsp;&nbsp;nif<br>}</code> | <code>{<br>&nbsp;&nbsp;id<br>}</code> | Create a new user. |
| PUT | `/users/:id` | <code>{<br>&nbsp;&nbsp;name,<br>&nbsp;&nbsp;password,<br>&nbsp;&nbsp;nif<br>}</code> | <code>{}</code> | Update user by id.