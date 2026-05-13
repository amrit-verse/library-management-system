// ===============================
// DEMO USERS
// ===============================

const users = [
    {
        email: "admin@library.com",
        password: "admin123",
        role: "admin",
        name: "Library Admin"
    },
    {
        email: "user@library.com",
        password: "user123",
        role: "user",
        name: "Library User"
    }
];

// ===============================
// LOGIN
// ===============================

const loginForm = document.getElementById("loginForm");

if (loginForm) {

    loginForm.addEventListener("submit", function (e) {

        e.preventDefault();

        const role = document.getElementById("role").value;
        const email = document.getElementById("email").value.trim();
        const password = document.getElementById("password").value.trim();

        // Validation
        if (!role || !email || !password) {
            alert("Please fill all fields");
            return;
        }

        // Find matching user
        const validUser = users.find(user =>
            user.email === email &&
            user.password === password &&
            user.role === role
        );

        // Invalid login
        if (!validUser) {
            alert("Invalid credentials");
            return;
        }

        // Store session
        sessionStorage.setItem("isLoggedIn", "true");
        sessionStorage.setItem("userRole", validUser.role);
        sessionStorage.setItem("userName", validUser.name);
        sessionStorage.setItem("userEmail", validUser.email);

        // Redirect
        if (validUser.role === "admin") {

            window.location.href = "./admin-dashboard.html";

        } else {

            window.location.href = "./user-dashboard.html";
        }

    });

}

// ===============================
// PASSWORD TOGGLE
// ===============================

const togglePassword = document.getElementById("togglePassword");

if (togglePassword) {

    togglePassword.addEventListener("click", () => {

        const passwordInput = document.getElementById("password");
        const icon = togglePassword.querySelector("i");

        if (passwordInput.type === "password") {

            passwordInput.type = "text";

            icon.classList.remove("fa-eye");
            icon.classList.add("fa-eye-slash");

        } else {

            passwordInput.type = "password";

            icon.classList.remove("fa-eye-slash");
            icon.classList.add("fa-eye");
        }

    });

}

// ===============================
// AUTH GUARD
// ===============================

function protectRoute(requiredRole = null) {

    const isLoggedIn =
        sessionStorage.getItem("isLoggedIn");

    const userRole =
        sessionStorage.getItem("userRole");

    // Not logged in
    if (!isLoggedIn) {

        window.location.href = "./login.html";

        return;
    }

    // Admin only protection
    if (
        requiredRole === "admin"
        &&
        userRole !== "admin"
    ) {

        alert("Access denied");

        window.location.href =
            "./user-dashboard.html";

        return;
    }

    // User only protection
    if (
        requiredRole === "user"
        &&
        userRole !== "user"
    ) {

        alert("Access denied");

        window.location.href =
            "./admin-dashboard.html";

        return;
    }
}

// ===============================
// LOGOUT
// ===============================

function logout() {

    sessionStorage.clear();

    window.location.href = "./login.html";

}

// ===============================
// USER INFO
// ===============================

function loadUserInfo() {

    const userName = sessionStorage.getItem("userName");

    const usernameElement = document.getElementById("loggedUser");

    if (usernameElement) {

        usernameElement.textContent = userName;
    }

}