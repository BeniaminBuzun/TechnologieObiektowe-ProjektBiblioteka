package pl.agh.edu.library.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.agh.edu.library.model.Book;
import pl.agh.edu.library.repository.BookRepository;

import java.util.List;
import java.util.Optional;

@Service
public class BookService {
	private final BookRepository bookRepository;

	@Autowired
	public BookService(BookRepository bookRepository) {
		this.bookRepository = bookRepository;
	}

	public List<Book> getBooks() {
		return bookRepository.findAll();
	}

	public void addBook(Book book) {
		bookRepository.save(book);
	}

	public Optional<Book> getBook(Long id) {
		return bookRepository.findById(id);
	}

	public void deleteBook(Long id) {
		bookRepository.deleteById(id);
	}
}
