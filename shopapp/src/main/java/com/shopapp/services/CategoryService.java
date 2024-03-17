package com.shopapp.services;

import com.shopapp.models.Category;
import com.shopapp.repositories.CategoryRepository;
import com.shopapp.dtos.CategoryDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
@Service
@RequiredArgsConstructor
public class CategoryService implements IService<Category,CategoryDTO,Long>{
    private final CategoryRepository categoryRepository;

    @Override
    public Category create(CategoryDTO categoryDTO) {
        Category newCategory = Category.builder()
                .name(categoryDTO.getName()).build();
        return categoryRepository.save(newCategory);
    }

    @Override
    public Category getById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found"));
    }

    @Override
    public List getAll() {
        return categoryRepository.findAll();
    }

    @Override
    @Transactional
    public Category update(Long id, CategoryDTO categoryDTO) {
        Category existingCategory = getById(id);
        existingCategory.setName(categoryDTO.getName());
        categoryRepository.save(existingCategory);
        return existingCategory;
    }

    @Override
    @Transactional

    public void delete(Long id) {
        categoryRepository.deleteById(id);

    }

}
