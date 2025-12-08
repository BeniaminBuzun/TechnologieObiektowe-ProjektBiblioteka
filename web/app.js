const API = "http://localhost:8080/api";

document.getElementById("registerForm").addEventListener("submit", async (e) => {
    e.preventDefault();

    const payload = {
        name: document.getElementById("regName").value,
        email: document.getElementById("regEmail").value,
        password: document.getElementById("regPassword").value,
        role: document.getElementById("regRole").value
    };

    const response = await fetch(`${API}/register`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(payload)
    });

    const data = await response.text();
    alert(data);
});

document.getElementById("loginForm").addEventListener("submit", async (e) => {
    e.preventDefault();

    const payload = {
        email: document.getElementById("loginEmail").value,
        password: document.getElementById("loginPassword").value
    };

    const response = await fetch(`${API}/login`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(payload)
    });

    if (!response.ok) {
        alert("Invalid login");
        return;
    }

    const data = await response.json();

    localStorage.setItem("token", data.token);
    localStorage.setItem("user", data.name);

    showDashboard();
});

function showDashboard() {
    const user = localStorage.getItem("user");

    if (!user) return;

    document.getElementById("welcome").innerText = "Logged in as: " + user;
    document.getElementById("dashboard").style.display = "block";
}

document.getElementById("logoutBtn").addEventListener("click", () => {
    localStorage.clear();
    location.reload();
});

showDashboard();

