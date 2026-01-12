console.log("App loaded v2"); // Wersja 2 - wymuszenie odświeżenia

const API = "http://localhost:8081/api";
const AUTH_API = "http://localhost:8081/account";

// --- AUTH ---

document.getElementById("registerForm").addEventListener("submit", async (e) => {
    e.preventDefault();

    const email = document.getElementById("regEmail").value;
    const payload = {
        firstName: document.getElementById("regName").value,
        email: email,
        userName: email, // Używamy emaila jako nazwy użytkownika dla uproszczenia
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

    const payload = {
        userName: email, // Backend oczekuje userName
        password: password
    };

    const response = await fetch(`${AUTH_API}/login`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(payload)
    });

    if (response.ok) {
        const token = await response.text(); // Backend zwraca token jako String
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
}

document.getElementById("logoutBtn").addEventListener("click", () => {
    localStorage.clear();
    showDashboard();
});

// --- HELPER: Get Headers with Token ---
function getAuthHeaders() {
    const token = localStorage.getItem("token");
    return {
        "Content-Type": "application/json",
        "Authorization": `Bearer ${token}`
    };
}

// --- LOANS & RESERVATIONS ---

document.getElementById("btnReserve").addEventListener("click", async () => {
    const userId = document.getElementById("loanUserId").value;
    const bookId = document.getElementById("loanBookId").value;
    
    if(!userId || !bookId) { alert("Podaj User ID i Book ID"); return; }

    const response = await fetch(`${API}/loans/reserve?userId=${userId}&bookId=${bookId}`, {
        method: "POST",
        headers: getAuthHeaders() // Dodajemy token
    });

    if(response.ok) {
        alert("Zarezerwowano pomyślnie!");
        loadLoans();
    } else {
        alert("Błąd rezerwacji (403 = brak uprawnień/tokena, 400 = złe ID)");
    }
});

document.getElementById("btnLoan").addEventListener("click", async () => {
    const userId = document.getElementById("loanUserId").value;
    const bookId = document.getElementById("loanBookId").value;

    if(!userId || !bookId) { alert("Podaj User ID i Book ID"); return; }

    const response = await fetch(`${API}/loans/loan?userId=${userId}&bookId=${bookId}`, {
        method: "POST",
        headers: getAuthHeaders()
    });

    if(response.ok) {
        alert("Wypożyczono pomyślnie!");
        loadLoans();
    } else {
        const text = await response.text();
        alert("Błąd: " + (text || response.status));
    }
});

document.getElementById("btnReturn").addEventListener("click", async () => {
    const loanId = document.getElementById("returnLoanId").value;
    
    if(!loanId) { alert("Podaj Loan ID"); return; }

    const response = await fetch(`${API}/loans/return/${loanId}`, {
        method: "POST",
        headers: getAuthHeaders()
    });

    if(response.ok) {
        alert("Zwrócono pomyślnie!");
        loadLoans();
    } else {
        alert("Błąd zwrotu");
    }
});

document.getElementById("btnListLoans").addEventListener("click", loadLoans);

async function loadLoans() {
    // Ten endpoint wymaga roli ADMIN. Jeśli jesteś USER, dostaniesz 403.
    const response = await fetch(`${API}/loans`, {
        headers: getAuthHeaders()
    });
    
    if(response.ok) {
        const data = await response.json();
        document.getElementById("loansOutput").innerText = JSON.stringify(data, null, 2);
    } else {
        document.getElementById("loansOutput").innerText = "Błąd pobierania listy (Wymagana rola ADMIN).";
    }
}

showDashboard();
