# spring-test
[unmaintained] Trying out Spring Boot. Not a clean code.

Purpose
=======

Maintains list of MySQL databases and allows to get some data and metadata for them.

REST API
========

- `GET /databases` - list all databases
- `POST /databases` - create new database info
- `GET /databases/{name}` - get database info by its name
- `PUT /databases/{name}` - update database info
- `DELETE /databases/{name}` - delete database info
- `GET /databases/{name}/tables` - get list of database tables
- `GET /databases/{name}/tables/{table}` - get table metadata
- `GET /databases/{name}/tables/{table}/data` - get table data preview (first 10 rows)

Database:
```json
  {
    "name": "testDatabase",
    "hostname": "localhost",
    "port": 3306,
    "databaseName": "test",
    "username": "",
    "password": ""
  }
```

Table (only `name` is filled when all tables are listed):
```json
{
  "name": "Persons",
  "columns": [
    {
      "field": "PersonID",
      "type": "INT",
      "size": 10,
      "nullable": false,
      "defaultValue": "0",
      "primaryKey": true
    },
    {
      "field": "Address",
      "type": "VARCHAR",
      "size": 255,
      "nullable": true,
      "defaultValue": null,
      "primaryKey": false
    },
    {
      "field": "FirstName",
      "type": "VARCHAR",
      "size": 255,
      "nullable": true,
      "defaultValue": null,
      "primaryKey": false
    },
    {
      "field": "LastName",
      "type": "VARCHAR",
      "size": 255,
      "nullable": true,
      "defaultValue": null,
      "primaryKey": false
    },
    {
      "field": "City",
      "type": "VARCHAR",
      "size": 255,
      "nullable": true,
      "defaultValue": null,
      "primaryKey": false
    }
  ]
}
```

Data preview:
```json
[
  [
    [
      "1",
      "Doe",
      "Jane",
      "Who Knows",
      "Somewhere"
    ],
    [
      "2",
      "Doe",
      "John",
      "Who Knows",
      "Somewhere"
    ]
  ]
]
```

Missing
=======

- nullability annotations (had them, but Eclipse started to invent some in Spring and it got messy)
- more testing
- integration tests
- authentication and authorization
- table name validation (names are used directly in SQL statements)
- true RESTful API (hyperlinks etc.)
- more column metadata
- better REST API docs
- UI
- better architecture (too tied to Spring, naked objects)
- and for sure many more...
