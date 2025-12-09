Liber jest programem służącym do zarządzania biblioteką. Zbudowany przy użyciu javy i framewroka bootspring wraz z hibernate.

Aktualnie aplikacja implementuje podstawowe operacje CRUD na użytkowniku


Struktura ptojektu:
```
.
pl.agh.edu.library
│
├── controller
│   └── UserController
│
├── service
│   └── UserService
│
├── model
│   ├── User
│   ├── Loan
│   ├── Book
│   └── Category
│
└── repository
    └── UserRepository
```



UserController
```
Package: pl.agh.edu.library.controller
Base URL: /api/users
Dependencies: UserService
```

Endpointy:

1. Pobieranie Listy użytkowników.
```GET /api/users```


Opis:

Zwraca listę użytkwoników zapisanych w bazie danych.

Odpowiedź:

```
200 OK
Body: List<User>
```
2. Stworzenie nowego użytkownika.
```
POST /api/users
```

Opis:

Dodaje nowego użytkownika do bazy danych.

Przykład:
```
Request Body (JSON example):

{
  "firstName": "John",
  "lastName": "Doe",
  "email": "john.doe@email.com",
  "password": "password123",
  "role": "USER"
}
```

Odpowiedź:
```
200 OK 
```
3. Znajdź użytkownika po id
```
GET /api/users/{id}
```

Opis:

Zwraca użytkownika z podanym id.

Odpowiedzi:
```
200 OK + User object if found
```
```
404 Not Found if user does not exist
```
4. Aktualizacja użytkownika
```
PUT /api/users/{id}
```

Opis:

Aktualizuje dane użytkownika (first name, last name, email, role).

Request Body:
Obiekt użytkownika z nowymi danymi.

Odpowiedzi:
```
200 OK + updated User
```
```
404 Not Found if user does not exist
```
5. Usuwanie użytkownika
```
DELETE /api/users/{id}
```

Opis:

Usuwa użytkownika z podanym id.

Response:
```
200 OK
```



Model Bazy Danych.

Pole	Typ	Opis
id	Integer	Klucz główny (generowany automatycznie)
email	String	Adres e-mail użytkownika
firstName	String	Imię
lastName	String	Nazwisko
password	String	Hasło użytkownika
role	String	Rola użytkownika (np. USER, ADMIN)
loans	List<Loan>	Relacja jeden-do-wielu
