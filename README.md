Liber jest programem służącym do zarządzania biblioteką. Zbudowany przy użyciu javy i framewroka bootspring wraz z hibernate.

Aktualnie aplikacja implementuje podstawowe operacje CRUD na użytkowniku



Struktura ptojektu:
```
.
pl.agh.edu.library
│
├── controller
│   ├── BookController
│   ├── CategoryController
│   ├── LoanController
│   ├── LoginController
│   └── UserController
│
├── service
│   ├── UserService
│   ├── LoanService
│   ├── CategoryService
│   ├── LibraryService
│   └── BookService
│
├── dto
│   └── Category
|
├── security
│   ├── SecurityConfig
│   ├── JwtUtil
│   └── JwtFilter
|
├── model
│   ├── User
│   ├── Loan
│   ├── Book
│   └── Category
│
└── repository
    └── UserRepository
    └── LoanRepository
    └── CategoryRepository
    └── BookRepository

```
Kontrolery

[UserController](#UserController)

[BookController](#BookController)

[LoanController](#LoanController)

[LoginController](#LoginController)


## UserController
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





## BookController
```
Base URL: /api/books
Dependencies: BookService
```

Endpointy
1. Pobranie listy książek
```
GET /api/books
```


Zwraca wszystkie książki.

Odpowiedź:
```
200 OK
Body: List<Book>
```

2. Dodanie książki
```
POST /api/books
```


Przykład JSON:
```
{
  "name": "Dune",
  "author": "Frank Herbert",
  "quantity": 5
}
```


Odpowiedź:
```
200 OK
```

3. Pobranie książki po ID
```
GET /api/books/{id}
```


Odpowiedzi:
```
200 OK + Book
404 Not Found
```

4. Usunięcie książki
```
DELETE /api/books/{id}
```


Odpowiedź:
```
200 OK
```

5. Aktualizacja książki
```
PUT /api/books/{id}
```


Aktualizuje pola: name, author, quantity.

Odpowiedzi:
```
200 OK + updated Book
404 Not Found
```

6. Przypisanie kategorii do książki
```
POST /api/books/{bookId}/categories/{categoryId}
```


Odpowiedź:
```
200 OK
```

## LoanController
```
Base URL: /api/loans
Dependencies: LoanService
```

Endpointy
1. Rezerwacja książki
```
POST /api/loans/reserve?userId={userId}&bookId={bookId}
```


Odpowiedzi:
```
200 OK + Loan
400 Bad Request
```

2. Wypożyczenie książki
```
POST /api/loans/loan?userId={userId}&bookId={bookId}
```


Odpowiedzi:

```
200 OK + Loan
400 Bad Request
```

3. Zwrot książki
```
POST /api/loans/return/{loanId}
```


Odpowiedzi:

```
200 OK + Loan
400 Bad Request
```

4. Lista wypożyczeń
```
GET /api/loans
```


Odpowiedź:

```
200 OK
Body: List<Loan>
```

## LoginController
```
Base URL: /account
Dependencies: UserRepository, JwtUtil
```

Endpointy
1. Logowanie
```
POST /account/login
```


Przykład JSON:

```
{
  "userName": "admin",
  "password": "admin123"
}
```


Odpowiedzi:

```
200 OK + JWT token

```

lub

```
200 OK + "wrong username or password"
```

## Security

System Autoryzacji użytkowników przechowywany jest w katalogu security.
Jest on oparty na systemie SpringSecurity wykorzystując tokeny JWT


## JwtFilter
```
Package: pl.agh.edu.library.security
Class: JwtFilter
Extends: OncePerRequestFilter
Dependencies: JwtUtil
```
Opis

Filtr odpowiedzialny za:

odczyt tokenu JWT z nagłówka HTTP Authorization

walidację tokenu

odtworzenie użytkownika w kontekście Spring Security

ustawienie uprawnień użytkownika na podstawie ról w tokenie

Filtr uruchamiany jest raz na każde żądanie HTTP.

## JwtUtil
```
Package: pl.agh.edu.library.security
Class: JwtUtil
```
Opis

Klasa odpowiedzialna za:

generowanie tokenów JWT

walidację tokenów


## SecurityConfig
```
Package: pl.agh.edu.library.security
Class: SecurityConfig
```
Opis

Klasa przechowująca konfigurację zabezpieczeń wykożystywaną przez aplikację.

Elementy konfiguracji:

1. Wyłączenia
```
csrf disabled
cors enabled
frameOptions disabled (dla H2)
sessionCreationPolicy STATELESS
```

3. Dostęp publiczny
```
.requestMatchers("/h2-console/**").permitAll()
.requestMatchers("/", "/index.html", "/app.js", "/favicon.ico").permitAll() // Pliki statyczne
.requestMatchers("/account/**").permitAll()
.requestMatchers(HttpMethod.POST, "/api/users").permitAll() // Rejestracja
```

3. Endpointy tylko dla ADMIN
```
.requestMatchers(HttpMethod.POST, "/api/books").hasRole("ADMIN")
.requestMatchers(HttpMethod.PUT, "/api/books/**").hasRole("ADMIN")
.requestMatchers(HttpMethod.DELETE, "/api/books/**").hasRole("ADMIN")
```

4. Endpointy wymagające zalogowania (USER lub ADMIN)
```
anyRequest().authenticated()
```



## Model Bazy Danych.




Tabela: ```users```

| Pole      | Typ        | Opis                                    |
| --------- | ---------- | --------------------------------------- |
| id        | Integer    | Klucz główny (generowany automatycznie) |
| email     | String     | Adres e-mail użytkownika                |
| firstName | String     | Imię                                    |
| lastName  | String     | Nazwisko                                |
| password  | String     | Hasło użytkownika                       |
| role      | String     | Rola użytkownika (np. USER, ADMIN)      |
| loans     | List<Loan> | Relacja jeden-do-wielu                  |


Tabela ```reservations```
| Pole            | Typ     | Opis                          |
| --------------- | ------- | ----------------------------- |
| id              | Integer | Klucz główny                  |
| state           | String  | Status wypożyczenia           |
| reservationDate | Date    | Data rezerwacji               |
| loanDate        | Date    | Data rozpoczęcia wypożyczenia |
| returnDate      | Date    | Data zwrotu                   |
| user            | User    | Użytkownik wypożyczający      |
| book            | Book    | Wypożyczona książka           |


Tabela ```books```
| Pole       | Typ           | Opis                 |
| ---------- | ------------- | -------------------- |
| id         | Integer       | Klucz główny         |
| name       | String        | Tytuł książki        |
| author     | String        | Autor książki        |
| quantity   | Integer       | Liczba egzemplarzy   |
| loans      | List<Loan>    | Wypożyczenia książki |
| categories | Set<Category> | Przypisane kategorie |


Tabela ```categories```
| Pole  | Typ       | Opis                |
| ----- | --------- | ------------------- |
| id    | Integer   | Klucz główny        |
| name  | String    | Nazwa kategorii     |
| books | Set<Book> | Książki w kategorii |







