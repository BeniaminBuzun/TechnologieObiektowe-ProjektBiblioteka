package pl.agh.edu.library.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.agh.edu.library.model.Book;
import pl.agh.edu.library.model.Category;
import pl.agh.edu.library.repository.BookRepository;

import java.util.List;
import java.util.Optional;

@Service
public class BookService {
	private final BookRepository bookRepository;
	private final CategoryService categoryService;

	@Autowired
	public BookService(BookRepository bookRepository, CategoryService categoryService) {
		this.bookRepository = bookRepository;
		this.categoryService = categoryService;
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

	public void addCategoryToBook(Long bookId, Long categoryId) {
		Optional<Book> optionalBook = getBook(bookId);
		if(optionalBook.isEmpty())
			System.out.println("Book not found!");
		Optional<Category> optionalCategory = categoryService.getCategory(categoryId);
		if(optionalCategory.isEmpty())
			System.out.println("Category not found!");
		optionalBook.get().getCategories().add(optionalCategory.get());
		bookRepository.save(optionalBook.get());
	}
}
