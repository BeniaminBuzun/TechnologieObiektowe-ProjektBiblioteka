console.log("App loaded v7");

const API = "http://localhost:8081/api";
const AUTH_API = "http://localhost:8081/account";

// --- AUTH ---

document.getElementById("registerForm").addEventListener("submit", async (e) => {
    e.preventDefault();
    const email = document.getElementById("regEmail").value;
    const payload = {
        firstName: document.getElementById("regName").value,
        email: email,
        userName: email,
        password: document.getElementById("regPassword").value,
        role: document.getElementById("regRole").value
    };
    
    const response = await fetch(`${API}/users`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(payload)
    });

    if (response.ok) {
        alert("User registered! Now try to login.");
    } else {
        alert("Error registering user");
    }
});

document.getElementById("loginForm").addEventListener("submit", async (e) => {
    e.preventDefault();
    const email = document.getElementById("loginEmail").value;
    const password = document.getElementById("loginPassword").value;

    const payload = { userName: email, password: password };

    const response = await fetch(`${AUTH_API}/login`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(payload)
    });

    if (response.ok) {
        const token = await response.text();
        if (token === "wrong username or password") {
            alert("Błędne dane logowania");
        } else {
            localStorage.setItem("token", token);
            localStorage.setItem("user", email);
            showDashboard();
        }
    } else {
        alert("Login failed");
    }
});

function showDashboard() {
    const user = localStorage.getItem("user");
    if (!user) {
        document.getElementById("authSection").style.display = "block";
        document.getElementById("dashboard").style.display = "none";
        return;
    }
    document.getElementById("authSection").style.display = "none";
    document.getElementById("welcome").innerText = "Logged in as: " + user;
    document.getElementById("dashboard").style.display = "block";
    
    loadAvailableBooks();
    loadAllBooksForReviews();
    loadMyLoans();
}

document.getElementById("logoutBtn").addEventListener("click", () => {
    localStorage.clear();
    showDashboard();
});

function getAuthHeaders() {
    const token = localStorage.getItem("token");
    return {
        "Content-Type": "application/json",
        "Authorization": `Bearer ${token}`
    };
}

// --- BOOKS ---

async function loadAvailableBooks() {
    const response = await fetch(`${API}/books/available`, { headers: getAuthHeaders() });
    if(response.ok) {
        const books = await response.json();
        const select = document.getElementById("loanBookSelect");
        select.innerHTML = '<option value="">-- Wybierz książkę --</option>';
        books.forEach(book => {
            const option = document.createElement("option");
            option.value = book.id;
            option.text = `${book.name} (${book.author}) - Dostępne: ${book.quantity}`;
            select.appendChild(option);
        });
    }
}

async function loadAllBooksForReviews() {
    const response = await fetch(`${API}/books`, { headers: getAuthHeaders() });
    if(response.ok) {
        const books = await response.json();
        const select = document.getElementById("viewReviewsBookSelect");
        select.innerHTML = '<option value="">-- Wybierz książkę --</option>';
        books.forEach(book => {
            const option = document.createElement("option");
            option.value = book.id;
            option.text = `${book.name} (${book.author})`;
            select.appendChild(option);
        });
    }
}

// --- MY LOANS & HISTORY ---

async function loadMyLoans() {
    const response = await fetch(`${API}/loans/my`, { headers: getAuthHeaders() });
    if(response.ok) {
        const loans = await response.json();
        renderMyLoans(loans);
    } else {
        document.getElementById("myActiveLoansList").innerText = "Błąd ładowania.";
    }
}

function renderMyLoans(loans) {
    const activeList = document.getElementById("myActiveLoansList");
    const historyList = document.getElementById("myHistoryList");
    
    activeList.innerHTML = "";
    historyList.innerHTML = "";
    
    if(loans.length === 0) {
        activeList.innerHTML = "<p>Brak wypożyczeń.</p>";
        historyList.innerHTML = "<p>Brak historii.</p>";
        return;
    }

    loans.forEach(loan => {
        const div = document.createElement("div");
        div.className = "loan-item";
        
        const bookTitle = loan.book ? loan.book.name : "Nieznana książka";
        const bookId = loan.book ? loan.book.id : null;

        if (loan.state === "LOANED") {
            let penaltyInfo = "";
            if (loan.penalty > 0) {
                penaltyInfo = `<br><span style="color:red; font-weight:bold;">KARA: ${loan.penalty} PLN</span>`;
            }
            
            let extendBtn = "";
            if (!loan.extended) {
                extendBtn = `<button class="btn-small" onclick="extendLoan(${loan.id})">Przedłuż (+14 dni)</button>`;
            } else {
                extendBtn = `<span style="font-size:0.8em; color:gray;">(Przedłużono)</span>`;
            }

            div.innerHTML = `
                <div class="loan-info">
                    <strong>${bookTitle}</strong> <br>
                    Termin: ${loan.dueDate} ${penaltyInfo}
                </div>
                <div>
                    ${extendBtn}
                    <button class="btn-small" onclick="returnBook(${loan.id})">Zwróć</button>
                </div>
            `;
            activeList.appendChild(div);
        } else if (loan.state === "RESERVED") {
             div.innerHTML = `
                <div class="loan-info">
                    <strong>${bookTitle}</strong> <br>
                    Status: ZAREZERWOWANA (Data: ${loan.reservationDate})
                </div>
                <button class="btn-small" onclick="returnBook(${loan.id})">Anuluj</button>
            `;
            activeList.appendChild(div);
        } else if (loan.state === "RETURNED") {
            div.innerHTML = `
                <div class="loan-info">
                    <strong>${bookTitle}</strong> <br>
                    Zwrócono: ${loan.returnDate}
                </div>
                <button class="btn-small" onclick="openReviewForm(${bookId}, '${bookTitle}')">Oceń</button>
            `;
            historyList.appendChild(div);
        }
    });
}

// --- ACTIONS ---

window.extendLoan = async function(loanId) {
    if(!confirm("Czy chcesz przedłużyć wypożyczenie o 14 dni?")) return;
    
    const response = await fetch(`${API}/loans/extend/${loanId}`, {
        method: "POST",
        headers: getAuthHeaders()
    });

    if(response.ok) {
        alert("Przedłużono!");
        loadMyLoans();
    } else {
        const text = await response.text();
        alert("Błąd: " + text);
    }
};

window.returnBook = async function(loanId) {
    if(!confirm("Czy na pewno chcesz zwrócić tę książkę?")) return;
    
    const response = await fetch(`${API}/loans/return/${loanId}`, {
        method: "POST",
        headers: getAuthHeaders()
    });

    if(response.ok) {
        alert("Zwrócono pomyślnie!");
        loadMyLoans();
        loadAvailableBooks();
    } else {
        alert("Błąd zwrotu");
    }
};

window.openReviewForm = function(bookId, bookTitle) {
    document.getElementById("reviewForm").style.display = "block";
    document.getElementById("reviewBookId").value = bookId;
    document.getElementById("reviewBookTitle").innerText = bookTitle;
    document.getElementById("reviewRating").value = "";
    document.getElementById("reviewComment").value = "";
};

// --- REVIEWS ---

document.getElementById("btnViewReviews").addEventListener("click", async () => {
    const bookId = document.getElementById("viewReviewsBookSelect").value;
    if(!bookId) { alert("Wybierz książkę"); return; }
    
    const response = await fetch(`${API}/reviews/book/${bookId}`, { headers: getAuthHeaders() });
    const container = document.getElementById("bookReviewsList");
    container.innerHTML = "Ładowanie...";
    
    if(response.ok) {
        const reviews = await response.json();
        container.innerHTML = "";
        
        if(reviews.length === 0) {
            container.innerHTML = "<p>Brak recenzji dla tej książki.</p>";
            return;
        }
        
        reviews.forEach(review => {
            const div = document.createElement("div");
            div.className = "review-item";
            const stars = "★".repeat(review.rating) + "☆".repeat(5 - review.rating);
            
            div.innerHTML = `
                <div class="review-content">
                    <div class="review-header">
                        <span class="review-rating">${stars}</span> 
                        przez ${review.user ? review.user.userName : "Anonim"}
                    </div>
                    <p>${review.comment}</p>
                </div>
            `;
            container.appendChild(div);
        });
    } else {
        container.innerText = "Błąd pobierania recenzji.";
    }
});

document.getElementById("btnAddReview").addEventListener("click", async () => {
    const bookId = document.getElementById("reviewBookId").value;
    const rating = document.getElementById("reviewRating").value;
    const comment = document.getElementById("reviewComment").value;

    if(!bookId || !rating) { alert("Wypełnij ocenę"); return; }

    const payload = {
        bookId: bookId,
        rating: rating,
        comment: comment
    };

    const response = await fetch(`${API}/reviews`, {
        method: "POST",
        headers: getAuthHeaders(),
        body: JSON.stringify(payload)
    });

    if(response.ok) {
        alert("Recenzja dodana!");
        document.getElementById("reviewForm").style.display = "none";
    } else {
        const text = await response.text();
        alert("Błąd: " + text);
    }
});

document.getElementById("btnReserve").addEventListener("click", async () => {
    const bookId = document.getElementById("loanBookSelect").value;
    if(!bookId) { alert("Wybierz książkę"); return; }

    const response = await fetch(`${API}/loans/reserve?bookId=${bookId}`, {
        method: "POST",
        headers: getAuthHeaders()
    });

    if(response.ok) {
        alert("Zarezerwowano pomyślnie!");
        loadMyLoans();
        loadAvailableBooks();
    } else {
        alert("Błąd rezerwacji");
    }
});

document.getElementById("btnLoan").addEventListener("click", async () => {
    const bookId = document.getElementById("loanBookSelect").value;
    if(!bookId) { alert("Wybierz książkę"); return; }

    const response = await fetch(`${API}/loans/loan?bookId=${bookId}`, {
        method: "POST",
        headers: getAuthHeaders()
    });

    if(response.ok) {
        alert("Wypożyczono pomyślnie!");
        loadMyLoans();
        loadAvailableBooks();
    } else {
        const text = await response.text();
        alert("Błąd: " + (text || response.status));
    }
});

document.getElementById("btnListLoans").addEventListener("click", async () => {
    const response = await fetch(`${API}/loans`, { headers: getAuthHeaders() });
    if(response.ok) {
        const data = await response.json();
        document.getElementById("loansOutput").innerText = JSON.stringify(data, null, 2);
    } else {
        document.getElementById("loansOutput").innerText = "Błąd (Wymagana rola ADMIN).";
    }
});

showDashboard();
