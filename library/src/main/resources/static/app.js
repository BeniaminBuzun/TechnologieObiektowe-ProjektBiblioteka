const API = "http://localhost:8081/api";

// --- AUTH ---

document.getElementById("registerForm").addEventListener("submit", async (e) => {
    e.preventDefault();

    const payload = {
        firstName: document.getElementById("regName").value,
        email: document.getElementById("regEmail").value,
        password: document.getElementById("regPassword").value,
        role: document.getElementById("regRole").value
    };
    
    const response = await fetch(`${API}/users`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(payload)
    });

    if (response.ok) {
        alert("User registered! Now try to login (if login logic exists) or just check DB.");
    } else {
        alert("Error registering user");
    }
});

document.getElementById("loginForm").addEventListener("submit", async (e) => {
    e.preventDefault();
    const email = document.getElementById("loginEmail").value;
    localStorage.setItem("user", email);
    showDashboard();
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

// --- LOANS & RESERVATIONS ---

document.getElementById("btnReserve").addEventListener("click", async () => {
    const userId = document.getElementById("loanUserId").value;
    const bookId = document.getElementById("loanBookId").value;
    
    if(!userId || !bookId) { alert("Podaj User ID i Book ID"); return; }

    const response = await fetch(`${API}/loans/reserve?userId=${userId}&bookId=${bookId}`, {
        method: "POST"
    });

    if(response.ok) {
        alert("Zarezerwowano pomyślnie!");
        loadLoans();
    } else {
        alert("Błąd rezerwacji (sprawdź czy ID istnieją)");
    }
});

document.getElementById("btnLoan").addEventListener("click", async () => {
    const userId = document.getElementById("loanUserId").value;
    const bookId = document.getElementById("loanBookId").value;

    if(!userId || !bookId) { alert("Podaj User ID i Book ID"); return; }

    const response = await fetch(`${API}/loans/loan?userId=${userId}&bookId=${bookId}`, {
        method: "POST"
    });

    if(response.ok) {
        alert("Wypożyczono pomyślnie!");
        loadLoans();
    } else {
        alert("Błąd wypożyczenia (sprawdź czy ID istnieją)");
    }
});

document.getElementById("btnReturn").addEventListener("click", async () => {
    const loanId = document.getElementById("returnLoanId").value;
    
    if(!loanId) { alert("Podaj Loan ID"); return; }

    const response = await fetch(`${API}/loans/return/${loanId}`, {
        method: "POST"
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
    const response = await fetch(`${API}/loans`);
    if(response.ok) {
        const data = await response.json();
        document.getElementById("loansOutput").innerText = JSON.stringify(data, null, 2);
    } else {
        document.getElementById("loansOutput").innerText = "Błąd pobierania listy.";
    }
}

showDashboard();
