package pl.agh.edu.library.model;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "books")
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;
	String name;
    String author;
    Integer quantity;
    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Loan> loans = new ArrayList<>();
    @ManyToMany
    @JoinTable(
            name = "book_categories",
            joinColumns = @JoinColumn(name = "book_id"),
            inverseJoinColumns = @JoinColumn(name = "category_id")
    )
    private Set<Category> categories = new HashSet<>();

	public Integer getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getAuthor() {
		return author;
	}

	public Integer getQuantity() {
		return quantity;
	}

	public Set<Category> getCategories() {
		return categories;
	}

	public List<Loan> getLoans() {
		return loans;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}

	public void setLoans(List<Loan> loans) {
		this.loans = loans;
	}

	public void setCategories(Set<Category> categories) {
		this.categories = categories;
	}

	public void addLoan(Loan loan) {
		loans.add(loan);
	}

	public void addCategory(Category category) {
		categories.add(category);
	}

	public void removeCategory(Category category) {
		categories.remove(category);
	}
}
