package pl.agh.edu.library.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.agh.edu.library.model.Book;
import pl.agh.edu.library.service.BookService;

import java.util.List;

@RestController
@RequestMapping("api/books")
public class BookController {
	private final BookService bookService;

	@Autowired
	public BookController(BookService bookService) {
		this.bookService = bookService;
	}

	@GetMapping
	public List<Book> getBooks() {
		return bookService.getBooks();
	}

	@PostMapping
	public void addUser(@RequestBody Book book) {
		bookService.addBook(book);
	}

	@GetMapping("/{id}")
	public ResponseEntity<Book> getBook(@PathVariable Long id) {
		return bookService.getBook(id)
				.map(ResponseEntity::ok)
				.orElse(ResponseEntity.notFound().build());
	}

	@DeleteMapping("/{id}")
	public void deleteBook(@PathVariable Long id) {
		bookService.deleteBook(id);
	}

	@PutMapping("/{id}")
	public ResponseEntity<Book> updateBook(@PathVariable Long id, @RequestBody Book bookDetails) {
		return bookService.getBook(id)
				.map(book -> {
					book.setName(bookDetails.getName());
					book.setAuthor(bookDetails.getAuthor());
					book.setQuantity(bookDetails.getQuantity());
					bookService.addBook(book);
					return ResponseEntity.ok(book);
				})
				.orElse(ResponseEntity.notFound().build());
	}

}
