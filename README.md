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
| :---: | :------: | :-------- | :-------- | :--------- |
| GET   | `/users/:id` | <pre><code>{}</code></pre> | <pre><code>{<br>&nbsp;&nbsp;id,<br>&nbsp;&nbsp;name,<br>&nbsp;&nbsp;username,<br>&nbsp;&nbsp;password,<br>&nbsp;&nbsp;nif,<br>&nbsp;&nbsp;creditCardId,<br>}</code></pre> |Get user by id. |
| POST | `/users` | <pre><code>{<br>&nbsp;&nbsp;name,<br>&nbsp;&nbsp;username,<br>&nbsp;&nbsp;password,<br>&nbsp;&nbsp;nif<br>}</code></pre> | <pre><code>{<br>&nbsp;&nbsp;id<br>}</code></pre> | Create a new user. |
| PUT | `/users/:id` | <pre><code>{<br>&nbsp;&nbsp;name,<br>&nbsp;&nbsp;password,<br>&nbsp;&nbsp;nif<br>}</code></pre> | <pre><code>{}</code></pre> | Update user by id.