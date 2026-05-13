const API_URL =
    "https://library-management-system-wgmk.onrender.com/books";

let allBooks = [];

/* =========================
   LOAD BOOKS
========================= */

async function loadBooks() {

    const loadingState =
        document.getElementById("loadingState");

    const emptyState =
        document.getElementById("emptyState");

    const tableBody =
        document.getElementById("bookTableBody");

    try {

        if (loadingState) {
            loadingState.style.display = "block";
        }

        const response = await fetch(API_URL);

        const books = await response.json();

        allBooks = books;

        renderBooks(books);

        updateBookCount(books);

    } catch (error) {

        console.error("Error loading books:", error);

    } finally {

        if (loadingState) {
            loadingState.style.display = "none";
        }
    }
}

/* =========================
   RENDER BOOKS
========================= */

function renderBooks(books) {

    const tableBody =
        document.getElementById("bookTableBody");

    const emptyState =
        document.getElementById("emptyState");

    if (!tableBody) return;

    tableBody.innerHTML = "";

    if (books.length === 0) {

        emptyState.style.display = "flex";

        return;

    } else {

        emptyState.style.display = "none";
    }

    books.forEach(book => {

        const row = `

            <tr>

                <td>${book.id}</td>

                <td>${book.title}</td>

                <td>${book.author}</td>

                <td>${book.category}</td>

                <td>

                    <span class="
                        status
                        ${book.available
                            ? "available"
                            : "issued"}
                    ">

                        ${book.available
                            ? "Available"
                            : "Borrowed"}

                    </span>

                </td>

                <td class="action-buttons">

                    <button
                        class="btn btn-primary"
                        onclick="editBook(
                            ${book.id},
                            `${book.title.replace(/'/g, "\\'")}`,
                            `${book.author.replace(/'/g, "\\'")}`,
                            `${book.category.replace(/'/g, "\\'")}`
                        )"
                    >
                        Edit
                    </button>

                    <button
                        class="btn btn-danger"
                        onclick="deleteBook(${book.id})"
                    >
                        Delete
                    </button>

                </td>

            </tr>
        `;

        tableBody.innerHTML += row;
    });
}

/* =========================
   ADD OR UPDATE BOOK
========================= */

async function addBook(event) {

    event.preventDefault();

    const bookId =
        document.getElementById("bookId").value;

    const title =
        document.getElementById("title").value.trim();

    const author =
        document.getElementById("author").value.trim();

    const category =
        document.getElementById("category").value.trim();

    const bookData = {

        title,
        author,
        category,
        available: true
    };

    try {

        // UPDATE BOOK
        if (bookId) {

            await fetch(`${API_URL}/${bookId}`, {

                method: "PUT",

                headers: {
                    "Content-Type": "application/json"
                },

                body: JSON.stringify(bookData)
            });

            showAlert("Book updated successfully");

        }

        // ADD BOOK
        else {

            await fetch(API_URL, {

                method: "POST",

                headers: {
                    "Content-Type": "application/json"
                },

                body: JSON.stringify(bookData)
            });

            showAlert("Book added successfully");
        }

        resetForm();

        loadBooks();

    } catch (error) {

        console.error("Error saving book:", error);
    }
}

/* =========================
   EDIT BOOK
========================= */

function editBook(id, title, author, category) {

    document.getElementById("bookId").value = id;

    document.getElementById("title").value = title;

    document.getElementById("author").value = author;

    document.getElementById("category").value = category;

    document.getElementById("formTitle").textContent =
        "Edit Book";

    document.getElementById("submitBtn").innerHTML =
        "Update Book";

    document.getElementById("cancelEditBtn").style.display =
        "inline-block";

    window.scrollTo({

        top: 0,
        behavior: "smooth"
    });
}

/* =========================
   CANCEL EDIT
========================= */

function cancelEdit() {

    resetForm();
}

/* =========================
   RESET FORM
========================= */

function resetForm() {

    document.getElementById("bookForm").reset();

    document.getElementById("bookId").value = "";

    document.getElementById("formTitle").textContent =
        "Add New Book";

    document.getElementById("submitBtn").innerHTML =
        `
        <i class="fa-solid fa-plus"></i>
        Add Book
        `;

    document.getElementById("cancelEditBtn").style.display =
        "none";
}

/* =========================
   DELETE BOOK
========================= */

async function deleteBook(id) {

    const confirmDelete = confirm(
        "Are you sure you want to delete this book?"
    );

    if (!confirmDelete) return;

    try {

        await fetch(`${API_URL}/${id}`, {

            method: "DELETE"
        });

        showAlert("Book deleted successfully");

        loadBooks();

    } catch (error) {

        console.error("Error deleting book:", error);
    }
}

/* =========================
   SEARCH
========================= */

function setupSearch() {

    const searchInput =
        document.getElementById("searchInput");

    if (!searchInput) return;

    searchInput.addEventListener("input", () => {

        const searchTerm =
            searchInput.value.toLowerCase();

        const filteredBooks = allBooks.filter(book => {

            return (

                book.title.toLowerCase()
                    .includes(searchTerm)

                ||

                book.author.toLowerCase()
                    .includes(searchTerm)

                ||

                book.category.toLowerCase()
                    .includes(searchTerm)
            );
        });

        renderBooks(filteredBooks);

        updateBookCount(filteredBooks);
    });
}

/* =========================
   BOOK COUNT
========================= */

function updateBookCount(books) {

    const bookCount =
        document.getElementById("bookCount");

    if (!bookCount) return;

    bookCount.textContent =
        `${books.length} Books`;
}

/* =========================
   SIMPLE ALERT
========================= */

function showAlert(message) {

    alert(message);
}

/* =========================
   INITIAL LOAD
========================= */

loadBooks();