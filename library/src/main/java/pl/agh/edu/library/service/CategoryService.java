package pl.agh.edu.library.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.agh.edu.library.model.Category;
import pl.agh.edu.library.repository.CategoryRepository;

import java.util.List;
import java.util.Optional;

@Service
public class CategoryService {
	private final CategoryRepository categoryRepository;

	@Autowired
	public CategoryService(CategoryRepository categoryRepository) {
		this.categoryRepository=categoryRepository;
	}

	public List<Category> getCategories() {
		return categoryRepository.findAll();
	}

	public void addCategory(Category category) {
		categoryRepository.save(category);
	}

	public void deleteCategory(Long id) {
		categoryRepository.deleteById(id);
	}

	Optional<Category> getCategory(Long id) {
		return categoryRepository.findById(id);
	}
}
