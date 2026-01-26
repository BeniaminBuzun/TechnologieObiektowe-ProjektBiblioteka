package pl.agh.edu.library.controller;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
class WebController {

    @GetMapping(value = "/", produces = MediaType.TEXT_HTML_VALUE)
    public ResponseEntity<String> loginPage() {
        String html = """
                <!doctype html>
                <html lang="en">
                  <head>
                    <meta charset="utf-8" />
                    <meta name="viewport" content="width=device-width, initial-scale=1" />
                    <title>Login</title>
                    <style>
                      body { font-family: system-ui, -apple-system, Segoe UI, Roboto, Arial, sans-serif; margin: 0; }
                      a { color: #93c5fd; text-decoration: none; }
                      a:hover { text-decoration: underline; }
                      main { min-height: 100vh; display: grid; place-items: center; padding: 2rem; background: #0b1220; color: #e5e7eb; }
                      .card { max-width: 520px; width: 100%; background: #111827; border: 1px solid #243042; border-radius: 14px; padding: 1.5rem; }
                      h1 { margin: 0 0 .5rem; font-size: 2rem; }
                      p { margin: 0; opacity: .9; line-height: 1.5; }
                      .muted { opacity: .85; }
                      code { background: #0b1220; padding: .15rem .35rem; border-radius: .35rem; }
                      label { display: block; margin-top: 1rem; font-size: .9rem; opacity: .9; }
                      input { width: 100%; margin-top: .35rem; padding: .7rem .8rem; border-radius: .6rem; border: 1px solid #243042; background: #0b1220; color: #e5e7eb; }
                      button { margin-top: 1rem; width: 100%; padding: .75rem .9rem; border-radius: .6rem; border: 1px solid #3b82f6; background: #2563eb; color: white; font-weight: 600; cursor: pointer; }
                      button:disabled { opacity: .6; cursor: not-allowed; }
                      .msg { margin-top: 1rem; font-size: .95rem; }
                      .ok { color: #34d399; }
                      .err { color: #fb7185; }
                    </style>
                  </head>
                  <body>
                    <main>
                      <div class="card">
                        <h1>Login</h1>
                        <p>Uses <code>POST /account/login</code> and saves the received token as a cookie.</p>
                        <p class="muted" style="margin-top:.75rem">
                          <a href="/library">Library</a> · <a href="/myAccount">My account</a> · <a href="/logout">Logout</a>
                        </p>

                        <form id="loginForm" autocomplete="on">
                          <label>
                            Username
                            <input id="userName" name="userName" placeholder="user@example.com" required />
                          </label>
                          <label>
                            Password
                            <input id="password" name="password" type="password" placeholder="••••••••" required />
                          </label>
                          <button id="submitBtn" type="submit">Sign in</button>
                          <div id="message" class="msg" aria-live="polite"></div>
                        </form>
                      </div>
                    </main>

                    <script>
                      const form = document.getElementById('loginForm');
                      const submitBtn = document.getElementById('submitBtn');
                      const message = document.getElementById('message');

                      function setMessage(text, kind) {
                        message.textContent = text;
                        message.className = 'msg ' + (kind || '');
                      }

                      function setTokenCookie(token) {
                        // Note: cookies set from JS cannot be HttpOnly.
                        const encoded = encodeURIComponent(token);
                        const base = `token=${encoded}; Path=/; SameSite=Lax; Max-Age=86400`;
                        const secure = location.protocol === 'https:' ? '; Secure' : '';
                        document.cookie = base + secure;
                      }

                      function looksLikeJwt(text) {
                        // Very small sanity check: three base64url-ish segments separated by dots.
                        return /^[A-Za-z0-9_-]+\\.[A-Za-z0-9_-]+\\.[A-Za-z0-9_-]+$/.test(text);
                      }

                      form.addEventListener('submit', async (e) => {
                        e.preventDefault();
                        setMessage('', '');
                        submitBtn.disabled = true;

                        const payload = {
                          userName: document.getElementById('userName').value,
                          password: document.getElementById('password').value
                        };

                        try {
                          const res = await fetch('/account/login', {
                            method: 'POST',
                            headers: { 'Content-Type': 'application/json' },
                            body: JSON.stringify(payload)
                          });

                          const text = await res.text();

                          if (!res.ok) {
                            setMessage(`Login failed (${res.status}).`, 'err');
                            return;
                          }

                          if (text === 'wrong username or password') {
                            setMessage('Wrong username or password.', 'err');
                            return;
                          }

                          if (!looksLikeJwt(text)) {
                            setMessage('Unexpected response from server.', 'err');
                            return;
                          }

                          setTokenCookie(text);
                          setMessage('Logged in. Token saved to cookie: token', 'ok');
                        } catch (err) {
                          setMessage('Network error while logging in.', 'err');
                        } finally {
                          submitBtn.disabled = false;
                        }
                      });
                    </script>
                  </body>
                </html>
                """;

        return ResponseEntity.ok()
                .contentType(MediaType.TEXT_HTML)
                .body(html);
    }

    @GetMapping(value = "/library", produces = MediaType.TEXT_HTML_VALUE)
    public ResponseEntity<String> libraryPage() {
        // Requirement: send GET request to /api/books (BookController) and display all received books.
        String html = """
                <!doctype html>
                <html lang="en">
                  <head>
                    <meta charset="utf-8" />
                    <meta name="viewport" content="width=device-width, initial-scale=1" />
                    <title>Library</title>
                    <style>
                      body { font-family: system-ui, -apple-system, Segoe UI, Roboto, Arial, sans-serif; margin: 0; background: #0b1220; color: #e5e7eb; }
                      header { padding: 1.25rem 1.5rem; border-bottom: 1px solid #243042; background: #111827; }
                      main { max-width: 820px; margin: 0 auto; padding: 1.5rem; }
                      a { color: #93c5fd; text-decoration: none; }
                      a:hover { text-decoration: underline; }
                      .card { background: #111827; border: 1px solid #243042; border-radius: 14px; padding: 1.25rem; }
                      h1 { margin: 0; font-size: 1.75rem; }
                      p { margin: .5rem 0 0; opacity: .9; }
                      ul { margin: 1rem 0 0; padding-left: 1.25rem; }
                      li { margin: .6rem 0; }
                      .muted { opacity: .85; }
                      .row { display: flex; align-items: center; justify-content: space-between; gap: .75rem; }
                      .meta { flex: 1; min-width: 0; }
                      .meta strong { display: block; }
                      .meta span { opacity: .85; }
                      .actions { display: flex; gap: .5rem; }
                      .btn { padding: .4rem .6rem; border-radius: .55rem; border: 1px solid #243042; background: #0b1220; color: #e5e7eb; cursor: pointer; }
                      .btn-primary { border-color: #3b82f6; background: #1d4ed8; }
                      .btn:disabled { opacity: .6; cursor: not-allowed; }
                    </style>
                  </head>
                  <body>
                    <header>
                      <h1>Library</h1>
                      <p>
                        <a href="/">Login</a> · <a href="/myAccount">My account</a> · <a href="/logout">Logout</a>
                      </p>
                    </header>
                    <main>
                      <div class="card">
                        <div id="status" class="muted">Loading…</div>
                        <ul id="books"></ul>
                      </div>
                    </main>

                    <script>
                      const statusEl = document.getElementById('status');
                      const listEl = document.getElementById('books');

                      function setStatus(text) {
                        statusEl.textContent = text;
                      }

                      function getCookie(name) {
                        const parts = document.cookie.split(';').map(v => v.trim());
                        for (const p of parts) {
                          if (p.startsWith(name + '=')) return decodeURIComponent(p.substring(name.length + 1));
                        }
                        return null;
                      }

                      function getAuthHeaders() {
                        const token = getCookie('token');
                        return token ? { 'Authorization': `Bearer ${token}` } : {};
                      }

                      async function callLoanEndpoint(kind, bookId, btnReserve, btnLoan) {
                        const token = getCookie('token');
                        if (!token) {
                          setStatus('You are not logged in (missing token cookie). Go to / and login first.');
                          return;
                        }

                        const url = kind === 'reserve'
                          ? `/api/loans/reserve?bookId=${encodeURIComponent(bookId)}`
                          : `/api/loans/loan?bookId=${encodeURIComponent(bookId)}`;

                        try {
                          btnReserve.disabled = true;
                          btnLoan.disabled = true;
                          setStatus('');

                          const res = await fetch(url, {
                            method: 'POST',
                            headers: {
                              ...getAuthHeaders()
                            }
                          });

                          const text = await res.text();

                          if (!res.ok) {
                            setStatus(text || `Request failed (HTTP ${res.status}).`);
                            return;
                          }

                          setStatus(kind === 'reserve' ? 'Reserved successfully.' : 'Loan created successfully.');
                        } catch (e) {
                          setStatus('Network error while calling loan endpoint.');
                        } finally {
                          btnReserve.disabled = false;
                          btnLoan.disabled = false;
                        }
                      }

                      function addBookRow(book) {
                        const li = document.createElement('li');

                        const row = document.createElement('div');
                        row.className = 'row';

                        const meta = document.createElement('div');
                        meta.className = 'meta';

                        const title = document.createElement('strong');
                        title.textContent = book?.name ?? '(no name)';

                        const details = document.createElement('span');
                        const author = book?.author ?? '(no author)';
                        const qty = (book?.quantity ?? '?');
                        details.textContent = `${author} (qty: ${qty})`;

                        meta.appendChild(title);
                        meta.appendChild(details);

                        const actions = document.createElement('div');
                        actions.className = 'actions';

                        const btnReserve = document.createElement('button');
                        btnReserve.className = 'btn';
                        btnReserve.type = 'button';
                        btnReserve.textContent = 'Reserve';

                        const btnLoan = document.createElement('button');
                        btnLoan.className = 'btn btn-primary';
                        btnLoan.type = 'button';
                        btnLoan.textContent = 'Loan';

                        const btnDetails = document.createElement('button');
                        btnDetails.className = 'btn';
                        btnDetails.type = 'button';
                        btnDetails.textContent = 'View details';

                        const bookId = book?.id;
                        btnReserve.addEventListener('click', () => callLoanEndpoint('reserve', bookId, btnReserve, btnLoan));
                        btnLoan.addEventListener('click', () => callLoanEndpoint('loan', bookId, btnReserve, btnLoan));
                        btnDetails.addEventListener('click', () => {
                          if (bookId === undefined || bookId === null) return;
                          window.location.href = `/viewDetails/${encodeURIComponent(bookId)}`;
                        });

                        // If id is missing, disable actions.
                        if (bookId === undefined || bookId === null) {
                          btnReserve.disabled = true;
                          btnLoan.disabled = true;
                          btnDetails.disabled = true;
                        }

                        actions.appendChild(btnReserve);
                        actions.appendChild(btnLoan);
                        actions.appendChild(btnDetails);

                        row.appendChild(meta);
                        row.appendChild(actions);
                        li.appendChild(row);
                        listEl.appendChild(li);
                      }

                      async function loadBooks() {
                        try {
                          const res = await fetch('/api/books', { headers: { 'Accept': 'application/json' } });
                          if (!res.ok) {
                            setStatus(`Failed to load books (HTTP ${res.status}).`);
                            return;
                          }

                          const books = await res.json();
                          listEl.innerHTML = '';

                          if (!Array.isArray(books) || books.length === 0) {
                            setStatus('No books found.');
                            return;
                          }

                          setStatus('');
                          for (const b of books) {
                            addBookRow(b);
                          }
                        } catch (e) {
                          setStatus('Network / parsing error while loading books.');
                        }
                      }

                      loadBooks();
                    </script>
                  </body>
                </html>
                """;

        return ResponseEntity.ok()
                .contentType(MediaType.TEXT_HTML)
                .body(html);
    }

    @GetMapping(value = "/myAccount", produces = MediaType.TEXT_HTML_VALUE)
    public ResponseEntity<String> myAccountPage() {
        String html = """
                <!doctype html>
                <html lang="en">
                  <head>
                    <meta charset="utf-8" />
                    <meta name="viewport" content="width=device-width, initial-scale=1" />
                    <title>My account</title>
                    <style>
                      body { font-family: system-ui, -apple-system, Segoe UI, Roboto, Arial, sans-serif; margin: 0; background: #0b1220; color: #e5e7eb; }
                      header { padding: 1.25rem 1.5rem; border-bottom: 1px solid #243042; background: #111827; }
                      main { max-width: 920px; margin: 0 auto; padding: 1.5rem; }
                      a { color: #93c5fd; text-decoration: none; }
                      a:hover { text-decoration: underline; }
                      .card { background: #111827; border: 1px solid #243042; border-radius: 14px; padding: 1.25rem; }
                      h1 { margin: 0; font-size: 1.75rem; }
                      p { margin: .5rem 0 0; opacity: .9; }
                      ul { margin: 1rem 0 0; padding: 0; list-style: none; }
                      li { padding: .85rem .75rem; border: 1px solid #243042; border-radius: 12px; margin: .75rem 0; background: #0b1220; }
                      .muted { opacity: .85; }
                      .row { display: flex; align-items: flex-start; justify-content: space-between; gap: .9rem; }
                      .meta { flex: 1; min-width: 0; }
                      .meta strong { display: block; font-size: 1.05rem; }
                      .meta .sub { margin-top: .25rem; }
                      .pill { display: inline-block; padding: .15rem .5rem; border-radius: 999px; border: 1px solid #243042; font-size: .8rem; opacity: .95; }
                      .actions { display: flex; flex-wrap: wrap; gap: .5rem; justify-content: flex-end; }
                      .btn { padding: .4rem .6rem; border-radius: .55rem; border: 1px solid #243042; background: #111827; color: #e5e7eb; cursor: pointer; }
                      .btn-primary { border-color: #3b82f6; background: #1d4ed8; }
                      .btn-danger { border-color: #fb7185; background: #9f1239; }
                      .btn:disabled { opacity: .6; cursor: not-allowed; }
                      code { background: #0b1220; padding: .15rem .35rem; border-radius: .35rem; }
                    </style>
                  </head>
                  <body>
                    <header>
                      <h1>My account</h1>
                      <p>
                        <a href="/">Login</a> · <a href="/library">Library</a> · <a href="/logout">Logout</a>
                      </p>
                    </header>
                    <main>
                      <div class="card">
                        <div id="status" class="muted">Loading…</div>
                        <ul id="loans"></ul>
                      </div>
                    </main>

                    <script>
                      const statusEl = document.getElementById('status');
                      const listEl = document.getElementById('loans');

                      function setStatus(text) {
                        statusEl.textContent = text;
                      }

                      function getCookie(name) {
                        const parts = document.cookie.split(';').map(v => v.trim());
                        for (const p of parts) {
                          if (p.startsWith(name + '=')) return decodeURIComponent(p.substring(name.length + 1));
                        }
                        return null;
                      }

                      function getAuthHeaders() {
                        const token = getCookie('token');
                        return token ? { 'Authorization': `Bearer ${token}` } : {};
                      }

                      async function post(url, { disableButtons = [] } = {}) {
                        const token = getCookie('token');
                        if (!token) {
                          setStatus('You are not logged in (missing token cookie). Go to / and login first.');
                          return { ok: false, text: 'Missing token cookie' };
                        }

                        for (const b of disableButtons) b.disabled = true;
                        setStatus('');
                        try {
                          const res = await fetch(url, { method: 'POST', headers: { ...getAuthHeaders() } });
                          const text = await res.text();
                          return { ok: res.ok, status: res.status, text };
                        } catch (e) {
                          return { ok: false, status: 0, text: 'Network error' };
                        } finally {
                          for (const b of disableButtons) b.disabled = false;
                        }
                      }

                      function formatDate(v) {
                        // Backend sends java.sql.Date -> usually "YYYY-MM-DD"
                        return v ? String(v) : '-';
                      }

                      function renderLoan(loan) {
                        const li = document.createElement('li');
                        const row = document.createElement('div');
                        row.className = 'row';

                        const meta = document.createElement('div');
                        meta.className = 'meta';

                        const bookName = loan?.book?.name ?? '(unknown book)';
                        const bookAuthor = loan?.book?.author ?? '(unknown author)';
                        const loanId = loan?.id;
                        const state = (loan?.state ?? 'UNKNOWN').toUpperCase();

                        const title = document.createElement('strong');
                        title.textContent = bookName;

                        const sub = document.createElement('div');
                        sub.className = 'sub muted';

                        const pill = document.createElement('span');
                        pill.className = 'pill';
                        pill.textContent = state;

                        const info = document.createElement('span');
                        const penalty = loan?.penalty ?? 0;
                        info.textContent =
                          ` by ${bookAuthor} · loanId: ${loanId ?? '?'} · due: ${formatDate(loan?.dueDate)} · penalty: ${penalty}`;

                        sub.appendChild(pill);
                        sub.appendChild(document.createTextNode(info.textContent ? ' ' + info.textContent : ''));

                        meta.appendChild(title);
                        meta.appendChild(sub);

                        const actions = document.createElement('div');
                        actions.className = 'actions';

                        const btnExtend = document.createElement('button');
                        btnExtend.className = 'btn';
                        btnExtend.type = 'button';
                        btnExtend.textContent = 'Extend';

                        const btnReturn = document.createElement('button');
                        btnReturn.className = 'btn btn-danger';
                        btnReturn.type = 'button';
                        btnReturn.textContent = 'Return';

                        const btnLoan = document.createElement('button');
                        btnLoan.className = 'btn btn-primary';
                        btnLoan.type = 'button';
                        btnLoan.textContent = 'Loan';

                        // Disable if missing loanId
                        if (loanId === undefined || loanId === null) {
                          btnExtend.disabled = true;
                          btnReturn.disabled = true;
                        }

                        btnExtend.addEventListener('click', async () => {
                          const r = await post(`/api/loans/extend/${encodeURIComponent(loanId)}`, { disableButtons: [btnExtend, btnReturn, btnLoan] });
                          if (!r.ok) {
                            setStatus(r.text || `Extend failed (HTTP ${r.status}).`);
                            return;
                          }
                          setStatus('Extended successfully.');
                          loadLoans();
                        });

                        btnReturn.addEventListener('click', async () => {
                          const r = await post(`/api/loans/return/${encodeURIComponent(loanId)}`, { disableButtons: [btnExtend, btnReturn, btnLoan] });
                          if (!r.ok) {
                            setStatus(r.text || `Return failed (HTTP ${r.status}).`);
                            return;
                          }
                          setStatus('Returned successfully.');
                          loadLoans();
                        });

                        // "Loan" option if state is RESERVED -> use LoanController: POST /api/loans/loan-reservation?loanId=<loanId>
                        if (state !== 'RESERVED' || loanId === undefined || loanId === null) {
                          btnLoan.style.display = 'none';
                        } else {
                          btnLoan.addEventListener('click', async () => {
                            const r = await post(`/api/loans/loan-reservation?loanId=${encodeURIComponent(loanId)}`, { disableButtons: [btnExtend, btnReturn, btnLoan] });
                            if (!r.ok) {
                              setStatus(r.text || `Loan failed (HTTP ${r.status}).`);
                              return;
                            }
                            setStatus('Loan created successfully.');
                            loadLoans();
                          });
                        }

                        actions.appendChild(btnExtend);
                        actions.appendChild(btnReturn);
                        actions.appendChild(btnLoan);

                        row.appendChild(meta);
                        row.appendChild(actions);
                        li.appendChild(row);
                        return li;
                      }

                      async function loadLoans() {
                        const token = getCookie('token');
                        if (!token) {
                          setStatus('You are not logged in (missing token cookie). Go to / and login first.');
                          listEl.innerHTML = '';
                          return;
                        }

                        setStatus('Loading…');
                        try {
                          const res = await fetch('/api/loans/my', {
                            headers: { 'Accept': 'application/json', ...getAuthHeaders() }
                          });

                          if (!res.ok) {
                            const text = await res.text();
                            setStatus(text || `Failed to load loans (HTTP ${res.status}).`);
                            listEl.innerHTML = '';
                            return;
                          }

                          const loans = await res.json();
                          listEl.innerHTML = '';

                          if (!Array.isArray(loans) || loans.length === 0) {
                            setStatus('No loans found.');
                            return;
                          }

                          setStatus('');
                          for (const loan of loans) {
                            // Safety: don't render returned loans even if backend changes
                            if (String(loan?.state ?? '').toUpperCase() === 'RETURNED') continue;
                            listEl.appendChild(renderLoan(loan));
                          }
                        } catch (e) {
                          setStatus('Network / parsing error while loading loans.');
                          listEl.innerHTML = '';
                        }
                      }

                      loadLoans();
                    </script>
                  </body>
                </html>
                """;

        return ResponseEntity.ok()
                .contentType(MediaType.TEXT_HTML)
                .body(html);
    }

    @GetMapping("/logout")
    public ResponseEntity<Void> logout() {
        // Expire the JWT cookie set by the login page (token).
        // Path must match cookie Path used when setting it (we use Path=/).
        String expireTokenCookie = "token=; Path=/; Max-Age=0; SameSite=Lax";

        return ResponseEntity.status(302)
                .header("Set-Cookie", expireTokenCookie)
                .header("Location", "/")
                .build();
    }

	@GetMapping(value="/admin", produces=MediaType.TEXT_HTML_VALUE)
	public ResponseEntity<String> adminMenu() {
		String html = """
				<!doctype html>
				<html lang="en">
				  <head>
				    <meta charset="utf-8" />
				    <meta name="viewport" content="width=device-width, initial-scale=1" />
				    <title>Admin menu</title>
				    <style>
				      body {
				        font-family: system-ui, -apple-system, Segoe UI, Roboto, Arial, sans-serif;
				        margin: 0;
				        background: #0b1220;
				        color: #e5e7eb;
				      }
				      header {
				        padding: 1.25rem 1.5rem;
				        border-bottom: 1px solid #243042;
				        background: #111827;
				      }
				      main {
				        max-width: 1100px;
				        margin: 0 auto;
				        padding: 1.5rem;
				      }
				      a {
				        color: #93c5fd;
				        text-decoration: none;
				      }
				      a:hover {
				        text-decoration: underline;
				      }
				      h1 {
				        margin: 0;
				        font-size: 1.75rem;
				      }
				      p {
				        margin: .5rem 0 0;
				        opacity: .9;
				      }
				      .card {
				        background: #111827;
				        border: 1px solid #243042;
				        border-radius: 14px;
				        padding: 1.25rem;
				      }
				
				      /* new styles */
				      .grid {
				        display: grid;
				        grid-template-columns: repeat(auto-fill, minmax(240px, 1fr));
				        gap: 1rem;
				        margin-top: 1rem;
				      }
				      .dir {
				        display: block;
				        padding: 1rem 1.1rem;
				        border-radius: 12px;
				        border: 1px solid #243042;
				        background: #0b1220;
				        transition: border-color .15s, background .15s, transform .05s;
				      }
				      .dir:hover {
				        background: #0f172a;
				        border-color: #3b82f6;
				        transform: translateY(-1px);
				      }
				      .dir-title {
				        font-weight: 600;
				        font-size: 1.05rem;
				        margin-bottom: .25rem;
				      }
				      .dir-desc {
				        font-size: .9rem;
				        opacity: .85;
				      }
				      .muted {
				        opacity: .75;
				      }
				    </style>
				  </head>
				
				  <body>
				    <header>
				      <h1>Admin panel</h1>
				      <p>
				        <a href="/">Login</a> ·
				        <a href="/library">Library</a> ·
				        <a href="/myAccount">My account</a> ·
				        <a href="/logout">Logout</a>
				      </p>
				    </header>
				
				    <main>
				      <div class="card">
				        <p class="muted">Administrative sections and tools</p>
				
				        <div class="grid">
				          <a class="dir" href="/admin/stats">
				            <div class="dir-title">Statystyki</div>
				            <div class="dir-desc">Strona zawierająca statystyki</div>
				          </a>
				
				          <a class="dir" href="/admin/user-loans">
				            <div class="dir-title">Wypożyczenia</div>
				            <div class="dir-desc">Widok na wypożyczenia użytkownikow</div>
				          </a>
				
				          <a class="dir" href="/admin/user-penalties">
				            <div class="dir-title">Kary</div>
				            <div class="dir-desc">Kary nałożone na użytkowników</div>
				          </a>
				
				          <a class="dir" href="/admin/books-ratings">
				            <div class="dir-title">Recenzje</div>
				            <div class="dir-desc">Widok na recenzje książek</div>
				          </a>
				
				          <a class="dir" href="/admin/category-loans">
				            <div class="dir-title">Wypożyczenia kategorii</div>
				            <div class="dir-desc">Widok na wypożyczenia kategorii</div>
				          </a>
				        </div>
				      </div>
				    </main>
				  </body>
				</html>
				
                """;
			return ResponseEntity.ok().contentType(MediaType.TEXT_HTML).body(html);
	}

    @GetMapping(value = "/admin/stats", produces = MediaType.TEXT_HTML_VALUE)
    public ResponseEntity<String> adminStatsPage() {
        String html = """
                <!doctype html>
                <html lang="en">
                  <head>
                    <meta charset="utf-8" />
                    <meta name="viewport" content="width=device-width, initial-scale=1" />
                    <title>Admin stats</title>
                    <style>
                      body { font-family: system-ui, -apple-system, Segoe UI, Roboto, Arial, sans-serif; margin: 0; background: #0b1220; color: #e5e7eb; }
                      header { padding: 1.25rem 1.5rem; border-bottom: 1px solid #243042; background: #111827; }
                      main { max-width: 1100px; margin: 0 auto; padding: 1.5rem; }
                      a { color: #93c5fd; text-decoration: none; }
                      a:hover { text-decoration: underline; }
                      .card { background: #111827; border: 1px solid #243042; border-radius: 14px; padding: 1.25rem; }
                      h1 { margin: 0; font-size: 1.75rem; }
                      p { margin: .5rem 0 0; opacity: .9; }
                      .muted { opacity: .85; }
                      canvas { width: 100%; height: 420px; background: #0b1220; border: 1px solid #243042; border-radius: 12px; margin-top: 1rem; }
                      .legend { margin-top: .75rem; font-size: .95rem; }
                      code { background: #0b1220; padding: .15rem .35rem; border-radius: .35rem; }
                    </style>
                  </head>
                  <body>
                    <header>
                      <h1>Admin stats</h1>
                      <p>
                        <a href="/">Login</a> · <a href="/library">Library</a> · <a href="/myAccount">My account</a> · <a href="/logout">Logout</a>
                      </p>
                    </header>
                    <main>
                      <div class="card">
                        <div id="status" class="muted">Loading…</div>
                        <canvas id="chart" width="1100" height="420"></canvas>
                        <div id="legend" class="legend muted"></div>
                      </div>
                    </main>

                    <script>
                      const statusEl = document.getElementById('status');
                      const legendEl = document.getElementById('legend');
                      const canvas = document.getElementById('chart');
                      const ctx = canvas.getContext('2d');

                      function setStatus(text) { statusEl.textContent = text; }

                      function getCookie(name) {
                        const parts = document.cookie.split(';').map(v => v.trim());
                        for (const p of parts) {
                          if (p.startsWith(name + '=')) return decodeURIComponent(p.substring(name.length + 1));
                        }
                        return null;
                      }

                      function getAuthHeaders() {
                        const token = getCookie('token');
                        return token ? { 'Authorization': `Bearer ${token}` } : {};
                      }

                      function clear() {
                        ctx.clearRect(0, 0, canvas.width, canvas.height);
                        ctx.fillStyle = '#0b1220';
                        ctx.fillRect(0, 0, canvas.width, canvas.height);
                      }

                      function drawBarChart(items) {
                        clear();

                        const padding = 50;
                        const w = canvas.width - padding * 2;
                        const h = canvas.height - padding * 2;

                        const max = Math.max(...items.map(x => x.count || 0), 1);
                        const barGap = 10;
                        const barW = Math.max(12, Math.floor((w - barGap * (items.length - 1)) / items.length));

                        // axes
                        ctx.strokeStyle = '#243042';
                        ctx.lineWidth = 1;
                        ctx.beginPath();
                        ctx.moveTo(padding, padding);
                        ctx.lineTo(padding, padding + h);
                        ctx.lineTo(padding + w, padding + h);
                        ctx.stroke();

                        // y labels
                        ctx.fillStyle = '#9ca3af';
                        ctx.font = '12px system-ui';
                        for (let i = 0; i <= 4; i++) {
                          const v = Math.round((max * i) / 4);
                          const y = padding + h - (h * i) / 4;
                          ctx.fillText(String(v), 10, y + 4);
                          ctx.strokeStyle = 'rgba(36,48,66,0.5)';
                          ctx.beginPath();
                          ctx.moveTo(padding, y);
                          ctx.lineTo(padding + w, y);
                          ctx.stroke();
                        }

                        // bars + x labels (truncated)
                        ctx.fillStyle = '#60a5fa';
                        let x = padding;
                        for (const it of items) {
                          const val = it.count || 0;
                          const bh = Math.round((val / max) * h);
                          const y = padding + h - bh;
                          ctx.fillRect(x, y, barW, bh);

                          const label = (it.name || '(no name)');
                          const short = label.length > 14 ? label.slice(0, 14) + '…' : label;
                          ctx.save();
                          ctx.translate(x + barW / 2, padding + h + 14);
                          ctx.rotate(-Math.PI / 6);
                          ctx.fillStyle = '#e5e7eb';
                          ctx.font = '12px system-ui';
                          ctx.textAlign = 'center';
                          ctx.fillText(short, 0, 0);
                          ctx.restore();

                          x += barW + barGap;
                        }
                      }

                      async function load() {
                        try {
                          const res = await fetch('/api/stats/book-loans', {
                            headers: { 'Accept': 'application/json', ...getAuthHeaders() }
                          });
                          if (!res.ok) {
                            const text = await res.text();
                            setStatus(text || `Failed to load stats (HTTP ${res.status}).`);
                            clear();
                            return;
                          }

                          const data = await res.json();
                          if (!Array.isArray(data)) {
                            setStatus('Unexpected stats response format.');
                            clear();
                            return;
                          }

                          const items = data
                            .map(x => ({
                              bookId: x?.bookId,
                              name: x?.name,
                              author: x?.author,
                              count: Number(x?.count ?? 0)
                            }))
                            .sort((a, b) => b.count - a.count)
                            .slice(0, 25);

                          if (items.length === 0) {
                            setStatus('No stats available.');
                            clear();
                            return;
                          }

                          setStatus('');
                          legendEl.textContent = `Showing top ${items.length} books by number of loans.`;
                          drawBarChart(items);
                        } catch (e) {
                          setStatus('Network / parsing error while loading stats.');
                          clear();
                        }
                      }

                      load();
                    </script>
                  </body>
                </html>
                """;

        return ResponseEntity.ok().contentType(MediaType.TEXT_HTML).body(html);
    }

    @GetMapping(value = "/admin/stats/user-loans", produces = MediaType.TEXT_HTML_VALUE)
    public ResponseEntity<String> adminUserLoansPage() {
        String html = """
                <!doctype html>
                <html lang="en">
                  <head>
                    <meta charset="utf-8" />
                    <meta name="viewport" content="width=device-width, initial-scale=1" />
                    <title>Admin stats - user loans</title>
                    <style>
                      body { font-family: system-ui, -apple-system, Segoe UI, Roboto, Arial, sans-serif; margin: 0; background: #0b1220; color: #e5e7eb; }
                      header { padding: 1.25rem 1.5rem; border-bottom: 1px solid #243042; background: #111827; }
                      main { max-width: 1100px; margin: 0 auto; padding: 1.5rem; }
                      a { color: #93c5fd; text-decoration: none; }
                      a:hover { text-decoration: underline; }
                      .card { background: #111827; border: 1px solid #243042; border-radius: 14px; padding: 1.25rem; }
                      h1 { margin: 0; font-size: 1.75rem; }
                      p { margin: .5rem 0 0; opacity: .9; }
                      .muted { opacity: .85; }
                      canvas { width: 100%; height: 420px; background: #0b1220; border: 1px solid #243042; border-radius: 12px; margin-top: 1rem; }
                      code { background: #0b1220; padding: .15rem .35rem; border-radius: .35rem; }
                    </style>
                  </head>
                  <body>
                    <header>
                      <h1>Admin stats - user loans</h1>
                      <p>
                        <a href="/">Login</a> · <a href="/library">Library</a> · <a href="/myAccount">My account</a> · <a href="/logout">Logout</a>
                      </p>
                    </header>
                    <main>
                      <div class="card">
                        <div id="status" class="muted">Loading…</div>
                        <canvas id="chart" width="1100" height="420"></canvas>
                        <div id="legend" class="muted" style="margin-top:.75rem"></div>
                      </div>
                    </main>
                    <script>
                      const statusEl = document.getElementById('status');
                      const legendEl = document.getElementById('legend');
                      const canvas = document.getElementById('chart');
                      const ctx = canvas.getContext('2d');

                      function setStatus(text) { statusEl.textContent = text; }
                      function getCookie(name) {
                        const parts = document.cookie.split(';').map(v => v.trim());
                        for (const p of parts) if (p.startsWith(name + '=')) return decodeURIComponent(p.substring(name.length + 1));
                        return null;
                      }
                      function getAuthHeaders() {
                        const token = getCookie('token');
                        return token ? { 'Authorization': `Bearer ${token}` } : {};
                      }
                      function clear() {
                        ctx.clearRect(0, 0, canvas.width, canvas.height);
                        ctx.fillStyle = '#0b1220';
                        ctx.fillRect(0, 0, canvas.width, canvas.height);
                      }
                      function drawBar(items) {
                        clear();
                        const padding = 50;
                        const w = canvas.width - padding * 2;
                        const h = canvas.height - padding * 2;
                        const max = Math.max(...items.map(x => x.count || 0), 1);
                        const gap = 10;
                        const barW = Math.max(12, Math.floor((w - gap * (items.length - 1)) / items.length));

                        ctx.strokeStyle = '#243042';
                        ctx.beginPath();
                        ctx.moveTo(padding, padding);
                        ctx.lineTo(padding, padding + h);
                        ctx.lineTo(padding + w, padding + h);
                        ctx.stroke();

                        ctx.fillStyle = '#9ca3af';
                        ctx.font = '12px system-ui';
                        for (let i = 0; i <= 4; i++) {
                          const v = Math.round((max * i) / 4);
                          const y = padding + h - (h * i) / 4;
                          ctx.fillText(String(v), 10, y + 4);
                          ctx.strokeStyle = 'rgba(36,48,66,0.5)';
                          ctx.beginPath();
                          ctx.moveTo(padding, y);
                          ctx.lineTo(padding + w, y);
                          ctx.stroke();
                        }

                        ctx.fillStyle = '#34d399';
                        let x = padding;
                        for (const it of items) {
                          const val = it.count || 0;
                          const bh = Math.round((val / max) * h);
                          const y = padding + h - bh;
                          ctx.fillRect(x, y, barW, bh);

                          const label = it.userName || it.email || String(it.userId ?? '');
                          const short = label.length > 14 ? label.slice(0, 14) + '…' : label;
                          ctx.save();
                          ctx.translate(x + barW / 2, padding + h + 14);
                          ctx.rotate(-Math.PI / 6);
                          ctx.fillStyle = '#e5e7eb';
                          ctx.textAlign = 'center';
                          ctx.fillText(short, 0, 0);
                          ctx.restore();

                          x += barW + gap;
                        }
                      }

                      async function load() {
                        try {
                          const res = await fetch('/api/stats/user-loans', { headers: { 'Accept': 'application/json', ...getAuthHeaders() } });
                          if (!res.ok) {
                            const text = await res.text();
                            setStatus(text || `Failed to load stats (HTTP ${res.status}).`);
                            clear();
                            return;
                          }
                          const data = await res.json();
                          if (!Array.isArray(data)) { setStatus('Unexpected response format.'); clear(); return; }

                          const items = data.map(x => ({
                            userId: x?.userId,
                            userName: x?.userName,
                            email: x?.email,
                            count: Number(x?.count ?? 0)
                          })).sort((a,b)=>b.count-a.count).slice(0, 25);

                          if (items.length === 0) { setStatus('No stats available.'); clear(); return; }
                          setStatus('');
                          legendEl.textContent = `Top ${items.length} users by number of loans.`;
                          drawBar(items);
                        } catch (e) {
                          setStatus('Network / parsing error while loading stats.');
                          clear();
                        }
                      }
                      load();
                    </script>
                  </body>
                </html>
                """;
        return ResponseEntity.ok().contentType(MediaType.TEXT_HTML).body(html);
    }

    @GetMapping(value = "/admin/stats/user-penalties", produces = MediaType.TEXT_HTML_VALUE)
    public ResponseEntity<String> adminUserPenaltiesPage() {
        String html = """
                <!doctype html>
                <html lang="en">
                  <head>
                    <meta charset="utf-8" />
                    <meta name="viewport" content="width=device-width, initial-scale=1" />
                    <title>Admin stats - user penalties</title>
                    <style>
                      body { font-family: system-ui, -apple-system, Segoe UI, Roboto, Arial, sans-serif; margin: 0; background: #0b1220; color: #e5e7eb; }
                      header { padding: 1.25rem 1.5rem; border-bottom: 1px solid #243042; background: #111827; }
                      main { max-width: 1100px; margin: 0 auto; padding: 1.5rem; }
                      a { color: #93c5fd; text-decoration: none; }
                      a:hover { text-decoration: underline; }
                      .card { background: #111827; border: 1px solid #243042; border-radius: 14px; padding: 1.25rem; }
                      h1 { margin: 0; font-size: 1.75rem; }
                      p { margin: .5rem 0 0; opacity: .9; }
                      .muted { opacity: .85; }
                      canvas { width: 100%; height: 420px; background: #0b1220; border: 1px solid #243042; border-radius: 12px; margin-top: 1rem; }
                      code { background: #0b1220; padding: .15rem .35rem; border-radius: .35rem; }
                    </style>
                  </head>
                  <body>
                    <header>
                      <h1>Admin stats - user penalties</h1>
                      <p>
                        <a href="/">Login</a> · <a href="/library">Library</a> · <a href="/myAccount">My account</a> · <a href="/logout">Logout</a>
                      </p>
                    </header>
                    <main>
                      <div class="card">
                        <div id="status" class="muted">Loading…</div>
                        <canvas id="chart" width="1100" height="420"></canvas>
                        <div id="legend" class="muted" style="margin-top:.75rem"></div>
                      </div>
                    </main>
                    <script>
                      const statusEl = document.getElementById('status');
                      const legendEl = document.getElementById('legend');
                      const canvas = document.getElementById('chart');
                      const ctx = canvas.getContext('2d');

                      function setStatus(text) { statusEl.textContent = text; }
                      function getCookie(name) {
                        const parts = document.cookie.split(';').map(v => v.trim());
                        for (const p of parts) if (p.startsWith(name + '=')) return decodeURIComponent(p.substring(name.length + 1));
                        return null;
                      }
                      function getAuthHeaders() {
                        const token = getCookie('token');
                        return token ? { 'Authorization': `Bearer ${token}` } : {};
                      }
                      function clear() {
                        ctx.clearRect(0, 0, canvas.width, canvas.height);
                        ctx.fillStyle = '#0b1220';
                        ctx.fillRect(0, 0, canvas.width, canvas.height);
                      }
                      function drawBar(items) {
                        clear();
                        const padding = 50;
                        const w = canvas.width - padding * 2;
                        const h = canvas.height - padding * 2;
                        const max = Math.max(...items.map(x => x.penalty || 0), 1);
                        const gap = 10;
                        const barW = Math.max(12, Math.floor((w - gap * (items.length - 1)) / items.length));

                        ctx.strokeStyle = '#243042';
                        ctx.beginPath();
                        ctx.moveTo(padding, padding);
                        ctx.lineTo(padding, padding + h);
                        ctx.lineTo(padding + w, padding + h);
                        ctx.stroke();

                        ctx.fillStyle = '#9ca3af';
                        ctx.font = '12px system-ui';
                        for (let i = 0; i <= 4; i++) {
                          const v = Math.round((max * i) / 4);
                          const y = padding + h - (h * i) / 4;
                          ctx.fillText(String(v), 10, y + 4);
                          ctx.strokeStyle = 'rgba(36,48,66,0.5)';
                          ctx.beginPath();
                          ctx.moveTo(padding, y);
                          ctx.lineTo(padding + w, y);
                          ctx.stroke();
                        }

                        ctx.fillStyle = '#fb7185';
                        let x = padding;
                        for (const it of items) {
                          const val = it.penalty || 0;
                          const bh = Math.round((val / max) * h);
                          const y = padding + h - bh;
                          ctx.fillRect(x, y, barW, bh);

                          const label = it.userName || it.email || String(it.userId ?? '');
                          const short = label.length > 14 ? label.slice(0, 14) + '…' : label;
                          ctx.save();
                          ctx.translate(x + barW / 2, padding + h + 14);
                          ctx.rotate(-Math.PI / 6);
                          ctx.fillStyle = '#e5e7eb';
                          ctx.textAlign = 'center';
                          ctx.fillText(short, 0, 0);
                          ctx.restore();

                          x += barW + gap;
                        }
                      }

                      async function load() {
                        try {
                          const res = await fetch('/api/stats/user-penalties', { headers: { 'Accept': 'application/json', ...getAuthHeaders() } });
                          if (!res.ok) {
                            const text = await res.text();
                            setStatus(text || `Failed to load stats (HTTP ${res.status}).`);
                            clear();
                            return;
                          }
                          const data = await res.json();
                          if (!Array.isArray(data)) { setStatus('Unexpected response format.'); clear(); return; }

                          const items = data.map(x => ({
                            userId: x?.userId,
                            userName: x?.userName,
                            email: x?.email,
                            penalty: Number(x?.penalty ?? 0)
                          })).sort((a,b)=>b.penalty-a.penalty).slice(0, 25);

                          if (items.length === 0) { setStatus('No stats available.'); clear(); return; }
                          setStatus('');
                          legendEl.textContent = `Top ${items.length} users by penalties (sum).`;
                          drawBar(items);
                        } catch (e) {
                          setStatus('Network / parsing error while loading stats.');
                          clear();
                        }
                      }
                      load();
                    </script>
                  </body>
                </html>
                """;
        return ResponseEntity.ok().contentType(MediaType.TEXT_HTML).body(html);
    }

    @GetMapping(value = "/admin/stats/books-ratings", produces = MediaType.TEXT_HTML_VALUE)
    public ResponseEntity<String> adminBookRatingsPage() {
        String html = """
                <!doctype html>
                <html lang="en">
                  <head>
                    <meta charset="utf-8" />
                    <meta name="viewport" content="width=device-width, initial-scale=1" />
                    <title>Admin stats - book ratings</title>
                    <style>
                      body { font-family: system-ui, -apple-system, Segoe UI, Roboto, Arial, sans-serif; margin: 0; background: #0b1220; color: #e5e7eb; }
                      header { padding: 1.25rem 1.5rem; border-bottom: 1px solid #243042; background: #111827; }
                      main { max-width: 1100px; margin: 0 auto; padding: 1.5rem; }
                      a { color: #93c5fd; text-decoration: none; }
                      a:hover { text-decoration: underline; }
                      .card { background: #111827; border: 1px solid #243042; border-radius: 14px; padding: 1.25rem; }
                      h1 { margin: 0; font-size: 1.75rem; }
                      p { margin: .5rem 0 0; opacity: .9; }
                      .muted { opacity: .85; }
                      canvas { width: 100%; height: 420px; background: #0b1220; border: 1px solid #243042; border-radius: 12px; margin-top: 1rem; }
                      code { background: #0b1220; padding: .15rem .35rem; border-radius: .35rem; }
                    </style>
                  </head>
                  <body>
                    <header>
                      <h1>Admin stats - book ratings</h1>
                      <p>
                        <a href="/">Login</a> · <a href="/library">Library</a> · <a href="/myAccount">My account</a> · <a href="/logout">Logout</a>
                      </p>
                    </header>
                    <main>
                      <div class="card">
                        <div id="status" class="muted">Loading…</div>
                        <canvas id="chart" width="1100" height="420"></canvas>
                        <div id="legend" class="muted" style="margin-top:.75rem"></div>
                      </div>
                    </main>
                    <script>
                      const statusEl = document.getElementById('status');
                      const legendEl = document.getElementById('legend');
                      const canvas = document.getElementById('chart');
                      const ctx = canvas.getContext('2d');

                      function setStatus(text) { statusEl.textContent = text; }
                      function getCookie(name) {
                        const parts = document.cookie.split(';').map(v => v.trim());
                        for (const p of parts) if (p.startsWith(name + '=')) return decodeURIComponent(p.substring(name.length + 1));
                        return null;
                      }
                      function getAuthHeaders() {
                        const token = getCookie('token');
                        return token ? { 'Authorization': `Bearer ${token}` } : {};
                      }
                      function clear() {
                        ctx.clearRect(0, 0, canvas.width, canvas.height);
                        ctx.fillStyle = '#0b1220';
                        ctx.fillRect(0, 0, canvas.width, canvas.height);
                      }
                      function drawBar(items) {
                        clear();
                        const padding = 50;
                        const w = canvas.width - padding * 2;
                        const h = canvas.height - padding * 2;
                        const max = Math.max(...items.map(x => x.rating || 0), 5);
                        const gap = 10;
                        const barW = Math.max(12, Math.floor((w - gap * (items.length - 1)) / items.length));

                        ctx.strokeStyle = '#243042';
                        ctx.beginPath();
                        ctx.moveTo(padding, padding);
                        ctx.lineTo(padding, padding + h);
                        ctx.lineTo(padding + w, padding + h);
                        ctx.stroke();

                        ctx.fillStyle = '#9ca3af';
                        ctx.font = '12px system-ui';
                        for (let i = 0; i <= 5; i++) {
                          const v = i;
                          const y = padding + h - (h * i) / 5;
                          ctx.fillText(String(v), 10, y + 4);
                          ctx.strokeStyle = 'rgba(36,48,66,0.5)';
                          ctx.beginPath();
                          ctx.moveTo(padding, y);
                          ctx.lineTo(padding + w, y);
                          ctx.stroke();
                        }

                        ctx.fillStyle = '#fbbf24';
                        let x = padding;
                        for (const it of items) {
                          const val = it.rating || 0;
                          const bh = Math.round((val / max) * h);
                          const y = padding + h - bh;
                          ctx.fillRect(x, y, barW, bh);

                          const label = it.name || '(no name)';
                          const short = label.length > 14 ? label.slice(0, 14) + '…' : label;
                          ctx.save();
                          ctx.translate(x + barW / 2, padding + h + 14);
                          ctx.rotate(-Math.PI / 6);
                          ctx.fillStyle = '#e5e7eb';
                          ctx.textAlign = 'center';
                          ctx.fillText(short, 0, 0);
                          ctx.restore();

                          x += barW + gap;
                        }
                      }

                      async function load() {
                        try {
                          const res = await fetch('/api/stats/books-ratings', { headers: { 'Accept': 'application/json', ...getAuthHeaders() } });
                          if (!res.ok) {
                            const text = await res.text();
                            setStatus(text || `Failed to load stats (HTTP ${res.status}).`);
                            clear();
                            return;
                          }
                          const data = await res.json();
                          if (!Array.isArray(data)) { setStatus('Unexpected response format.'); clear(); return; }

                          const items = data.map(x => ({
                            bookId: x?.bookId,
                            name: x?.name,
                            author: x?.author,
                            rating: Number(x?.rating ?? 0)
                          })).sort((a,b)=>b.rating-a.rating).slice(0, 25);

                          if (items.length === 0) { setStatus('No stats available.'); clear(); return; }
                          setStatus('');
                          legendEl.textContent = `Top ${items.length} books by average rating.`;
                          drawBar(items);
                        } catch (e) {
                          setStatus('Network / parsing error while loading stats.');
                          clear();
                        }
                      }
                      load();
                    </script>
                  </body>
                </html>
                """;
        return ResponseEntity.ok().contentType(MediaType.TEXT_HTML).body(html);
    }

    @GetMapping(value = "/admin/stats/category-loans", produces = MediaType.TEXT_HTML_VALUE)
    public ResponseEntity<String> adminCategoryLoansPage() {
        String html = """
                <!doctype html>
                <html lang="en">
                  <head>
                    <meta charset="utf-8" />
                    <meta name="viewport" content="width=device-width, initial-scale=1" />
                    <title>Admin stats - category loans</title>
                    <style>
                      body { font-family: system-ui, -apple-system, Segoe UI, Roboto, Arial, sans-serif; margin: 0; background: #0b1220; color: #e5e7eb; }
                      header { padding: 1.25rem 1.5rem; border-bottom: 1px solid #243042; background: #111827; }
                      main { max-width: 1100px; margin: 0 auto; padding: 1.5rem; }
                      a { color: #93c5fd; text-decoration: none; }
                      a:hover { text-decoration: underline; }
                      .card { background: #111827; border: 1px solid #243042; border-radius: 14px; padding: 1.25rem; }
                      h1 { margin: 0; font-size: 1.75rem; }
                      p { margin: .5rem 0 0; opacity: .9; }
                      .muted { opacity: .85; }
                      canvas { width: 100%; height: 420px; background: #0b1220; border: 1px solid #243042; border-radius: 12px; margin-top: 1rem; }
                      code { background: #0b1220; padding: .15rem .35rem; border-radius: .35rem; }
                    </style>
                  </head>
                  <body>
                    <header>
                      <h1>Admin stats - category loans</h1>
                      <p>
                        <a href="/">Login</a> · <a href="/library">Library</a> · <a href="/myAccount">My account</a> · <a href="/logout">Logout</a>
                      </p>
                    </header>
                    <main>
                      <div class="card">
                        <div id="status" class="muted">Loading…</div>
                        <canvas id="chart" width="1100" height="420"></canvas>
                        <div id="legend" class="muted" style="margin-top:.75rem"></div>
                      </div>
                    </main>
                    <script>
                      const statusEl = document.getElementById('status');
                      const legendEl = document.getElementById('legend');
                      const canvas = document.getElementById('chart');
                      const ctx = canvas.getContext('2d');

                      function setStatus(text) { statusEl.textContent = text; }
                      function getCookie(name) {
                        const parts = document.cookie.split(';').map(v => v.trim());
                        for (const p of parts) if (p.startsWith(name + '=')) return decodeURIComponent(p.substring(name.length + 1));
                        return null;
                      }
                      function getAuthHeaders() {
                        const token = getCookie('token');
                        return token ? { 'Authorization': `Bearer ${token}` } : {};
                      }
                      function clear() {
                        ctx.clearRect(0, 0, canvas.width, canvas.height);
                        ctx.fillStyle = '#0b1220';
                        ctx.fillRect(0, 0, canvas.width, canvas.height);
                      }
                      function drawBar(items) {
                        clear();
                        const padding = 50;
                        const w = canvas.width - padding * 2;
                        const h = canvas.height - padding * 2;
                        const max = Math.max(...items.map(x => x.count || 0), 1);
                        const gap = 10;
                        const barW = Math.max(12, Math.floor((w - gap * (items.length - 1)) / items.length));

                        ctx.strokeStyle = '#243042';
                        ctx.beginPath();
                        ctx.moveTo(padding, padding);
                        ctx.lineTo(padding, padding + h);
                        ctx.lineTo(padding + w, padding + h);
                        ctx.stroke();

                        ctx.fillStyle = '#9ca3af';
                        ctx.font = '12px system-ui';
                        for (let i = 0; i <= 4; i++) {
                          const v = Math.round((max * i) / 4);
                          const y = padding + h - (h * i) / 4;
                          ctx.fillText(String(v), 10, y + 4);
                          ctx.strokeStyle = 'rgba(36,48,66,0.5)';
                          ctx.beginPath();
                          ctx.moveTo(padding, y);
                          ctx.lineTo(padding + w, y);
                          ctx.stroke();
                        }

                        ctx.fillStyle = '#60a5fa';
                        let x = padding;
                        for (const it of items) {
                          const val = it.count || 0;
                          const bh = Math.round((val / max) * h);
                          const y = padding + h - bh;
                          ctx.fillRect(x, y, barW, bh);

                          const label = it.name || '(no name)';
                          const short = label.length > 14 ? label.slice(0, 14) + '…' : label;
                          ctx.save();
                          ctx.translate(x + barW / 2, padding + h + 14);
                          ctx.rotate(-Math.PI / 6);
                          ctx.fillStyle = '#e5e7eb';
                          ctx.textAlign = 'center';
                          ctx.fillText(short, 0, 0);
                          ctx.restore();

                          x += barW + gap;
                        }
                      }

                      async function load() {
                        try {
                          const res = await fetch('/api/stats/category-loans', { headers: { 'Accept': 'application/json', ...getAuthHeaders() } });
                          if (!res.ok) {
                            const text = await res.text();
                            setStatus(text || `Failed to load stats (HTTP ${res.status}).`);
                            clear();
                            return;
                          }
                          const data = await res.json();
                          if (!Array.isArray(data)) { setStatus('Unexpected response format.'); clear(); return; }

                          const items = data.map(x => ({
                            categoryId: x?.categoryId,
                            name: x?.name,
                            count: Number(x?.count ?? 0)
                          })).sort((a,b)=>b.count-a.count).slice(0, 25);

                          if (items.length === 0) { setStatus('No stats available.'); clear(); return; }
                          setStatus('');
                          legendEl.textContent = `Top ${items.length} categories by number of books (with categories).`;
                          drawBar(items);
                        } catch (e) {
                          setStatus('Network / parsing error while loading stats.');
                          clear();
                        }
                      }
                      load();
                    </script>
                  </body>
                </html>
                """;
        return ResponseEntity.ok().contentType(MediaType.TEXT_HTML).body(html);
    }

    @GetMapping(value = "/viewDetails/{id}", produces = MediaType.TEXT_HTML_VALUE)
    public ResponseEntity<String> viewDetails(@PathVariable Integer id) {
        String html = """
                <!doctype html>
                <html lang="en">
                  <head>
                    <meta charset="utf-8" />
                    <meta name="viewport" content="width=device-width, initial-scale=1" />
                    <title>Book details</title>
                    <style>
                      body { font-family: system-ui, -apple-system, Segoe UI, Roboto, Arial, sans-serif; margin: 0; background: #0b1220; color: #e5e7eb; }
                      header { padding: 1.25rem 1.5rem; border-bottom: 1px solid #243042; background: #111827; }
                      main { max-width: 920px; margin: 0 auto; padding: 1.5rem; }
                      a { color: #93c5fd; text-decoration: none; }
                      a:hover { text-decoration: underline; }
                      .card { background: #111827; border: 1px solid #243042; border-radius: 14px; padding: 1.25rem; }
                      h1 { margin: 0; font-size: 1.75rem; }
                      h2 { margin: 1.25rem 0 .5rem; font-size: 1.2rem; }
                      p { margin: .5rem 0 0; opacity: .9; }
                      .muted { opacity: .85; }
                      .kv { margin-top: .75rem; display: grid; grid-template-columns: 140px 1fr; gap: .35rem .75rem; }
                      code { background: #0b1220; padding: .15rem .35rem; border-radius: .35rem; }
                      ul { margin: .75rem 0 0; padding-left: 1.25rem; }
                      li { margin: .5rem 0; }
                      .review { border: 1px solid #243042; border-radius: 12px; padding: .75rem; background: #0b1220; list-style: none; }
                      .review + .review { margin-top: .6rem; }
                      .stars { color: #fbbf24; font-weight: 700; }
                      .form { margin-top: .85rem; padding: .85rem; border: 1px solid #243042; border-radius: 12px; background: #0b1220; }
                      label { display: block; margin-top: .6rem; font-size: .9rem; opacity: .9; }
                      textarea, select { width: 100%; margin-top: .35rem; padding: .6rem .7rem; border-radius: .6rem; border: 1px solid #243042; background: #111827; color: #e5e7eb; box-sizing: border-box; }
                      textarea { min-height: 90px; resize: vertical; }
                      .btn { margin-top: .75rem; padding: .55rem .75rem; border-radius: .6rem; border: 1px solid #3b82f6; background: #1d4ed8; color: #fff; font-weight: 600; cursor: pointer; }
                      .btn:disabled { opacity: .6; cursor: not-allowed; }
                    </style>
                  </head>
                  <body>
                    <header>
                      <h1>Book details</h1>
                      <p>
                        <a href="/">Login</a> · <a href="/library">Library</a> · <a href="/myAccount">My account</a> · <a href="/logout">Logout</a>
                      </p>
                    </header>
                    <main>
                      <div class="card">
                        <div id="status" class="muted">Loading…</div>

                        <div id="book" style="display:none">
                          <h2 id="bookTitle"></h2>
                          <div class="kv">
                            <div class="muted">Author</div><div id="bookAuthor"></div>
                            <div class="muted">Quantity</div><div id="bookQty"></div>
                            <div class="muted">Book ID</div><div id="bookId"></div>
                          </div>
                        </div>

                        <div id="reviewsSection" style="margin-top:1.25rem">
                          <h2>Reviews</h2>

                          <div class="form">
                            <div class="muted" style="margin-bottom:.25rem">Add review</div>
                            <label>
                              Comment
                              <textarea id="reviewComment" placeholder="Write your review..."></textarea>
                            </label>
                            <label>
                              Rating (1-5)
                              <select id="reviewRating">
                                <option value="">-- Select --</option>
                                <option value="1">1</option>
                                <option value="2">2</option>
                                <option value="3">3</option>
                                <option value="4">4</option>
                                <option value="5">5</option>
                              </select>
                            </label>
                            <button id="addReviewBtn" class="btn" type="button">Add review</button>
                            <div id="addReviewStatus" class="muted" style="margin-top:.4rem"></div>
                          </div>

                          <div id="reviewsStatus" class="muted">Loading…</div>
                          <ul id="reviews" style="padding:0; margin: .75rem 0 0; list-style:none"></ul>
                        </div>
                      </div>
                    </main>

                    <script>
                      const BOOK_ID = {{ID}};
                      const statusEl = document.getElementById('status');
                      const bookEl = document.getElementById('book');
                      const titleEl = document.getElementById('bookTitle');
                      const authorEl = document.getElementById('bookAuthor');
                      const qtyEl = document.getElementById('bookQty');
                      const idEl = document.getElementById('bookId');

                      const reviewsStatusEl = document.getElementById('reviewsStatus');
                      const reviewsListEl = document.getElementById('reviews');
                      const addReviewBtn = document.getElementById('addReviewBtn');
                      const addReviewStatusEl = document.getElementById('addReviewStatus');
                      const reviewCommentEl = document.getElementById('reviewComment');
                      const reviewRatingEl = document.getElementById('reviewRating');

                      function getCookie(name) {
                        const parts = document.cookie.split(';').map(v => v.trim());
                        for (const p of parts) {
                          if (p.startsWith(name + '=')) return decodeURIComponent(p.substring(name.length + 1));
                        }
                        return null;
                      }

                      function getAuthHeaders() {
                        const token = getCookie('token');
                        return token ? { 'Authorization': `Bearer ${token}` } : {};
                      }

                      function stars(n) {
                        const r = Math.max(0, Math.min(5, Number(n || 0)));
                        return '★'.repeat(r) + '☆'.repeat(5 - r);
                      }

                      function renderReview(r) {
                        const li = document.createElement('li');
                        li.className = 'review';

                        const rating = document.createElement('div');
                        rating.className = 'stars';
                        rating.textContent = stars(r?.rating);

                        const meta = document.createElement('div');
                        meta.className = 'muted';
                        meta.style.marginTop = '.2rem';
                        meta.textContent = (r?.user?.userName ? `by ${r.user.userName}` : 'by (unknown user)');

                        const comment = document.createElement('div');
                        comment.style.marginTop = '.5rem';
                        comment.textContent = r?.comment ?? '';

                        li.appendChild(rating);
                        li.appendChild(meta);
                        if ((r?.comment ?? '').length > 0) li.appendChild(comment);
                        return li;
                      }

                      async function loadBook() {
                        try {
                          const res = await fetch(`/api/books/${encodeURIComponent(BOOK_ID)}`, { headers: { 'Accept': 'application/json' } });
                          if (!res.ok) {
                            const text = await res.text();
                            statusEl.textContent = text || `Failed to load book (HTTP ${res.status}).`;
                            return;
                          }

                          const b = await res.json();
                          titleEl.textContent = b?.name ?? '(no name)';
                          authorEl.textContent = b?.author ?? '(no author)';
                          qtyEl.textContent = String(b?.quantity ?? '?');
                          idEl.textContent = String(b?.id ?? BOOK_ID);
                          bookEl.style.display = 'block';
                          statusEl.textContent = '';
                        } catch (e) {
                          statusEl.textContent = 'Network / parsing error while loading book.';
                        }
                      }

                      async function loadReviews() {
                        try {
                          const res = await fetch(`/api/reviews/book/${encodeURIComponent(BOOK_ID)}`, {
                            headers: { 'Accept': 'application/json', ...getAuthHeaders() }
                          });
                          if (!res.ok) {
                            const text = await res.text();
                            reviewsStatusEl.textContent = text || `Failed to load reviews (HTTP ${res.status}).`;
                            return;
                          }

                          const reviews = await res.json();
                          reviewsListEl.innerHTML = '';

                          if (!Array.isArray(reviews) || reviews.length === 0) {
                            reviewsStatusEl.textContent = 'No reviews for this book.';
                            return;
                          }

                          reviewsStatusEl.textContent = '';
                          for (const r of reviews) {
                            reviewsListEl.appendChild(renderReview(r));
                          }
                        } catch (e) {
                          reviewsStatusEl.textContent = 'Network / parsing error while loading reviews.';
                        }
                      }

                      async function addReview() {
                        const token = getCookie('token');
                        if (!token) {
                          addReviewStatusEl.textContent = 'Login required to add a review (missing token cookie).';
                          return;
                        }

                        const rating = Number(reviewRatingEl.value);
                        const comment = (reviewCommentEl.value || '').trim();

                        if (!Number.isInteger(rating) || rating < 1 || rating > 5) {
                          addReviewStatusEl.textContent = 'Please select rating 1-5.';
                          return;
                        }

                        addReviewBtn.disabled = true;
                        addReviewStatusEl.textContent = '';

                        try {
                          const res = await fetch('/api/reviews', {
                            method: 'POST',
                            headers: { 'Content-Type': 'application/json', ...getAuthHeaders() },
                            body: JSON.stringify({ bookId: BOOK_ID, rating, comment })
                          });

                          const text = await res.text();
                          if (!res.ok) {
                            addReviewStatusEl.textContent = text || `Failed to add review (HTTP ${res.status}).`;
                            return;
                          }

                          addReviewStatusEl.textContent = 'Review added.';
                          reviewCommentEl.value = '';
                          reviewRatingEl.value = '';
                          await loadReviews();
                        } catch (e) {
                          addReviewStatusEl.textContent = 'Network error while adding review.';
                        } finally {
                          addReviewBtn.disabled = false;
                        }
                      }

                      loadBook();
                      loadReviews();
                      addReviewBtn.addEventListener('click', addReview);
                    </script>
                  </body>
                </html>
                """.replace("{{ID}}", String.valueOf(id));

        return ResponseEntity.ok().contentType(MediaType.TEXT_HTML).body(html);
    }
}
