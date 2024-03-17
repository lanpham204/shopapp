package com.shopapp.controller;

import com.shopapp.models.Category;
import com.shopapp.response.CategoryMessageResponse;
import com.shopapp.services.CategoryService;
import com.shopapp.dtos.CategoryDTO;
import com.shopapp.component.LocalizationUtils;
import com.shopapp.utils.MessageKeys;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${api.prefix}/categories")
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryService categoryService;
    private final LocalizationUtils localizationUtils;

    @PostMapping("")
    public ResponseEntity<?> createCategory(
            @Valid @RequestBody CategoryDTO categoryDTO,
            BindingResult result
    ) {
        if(result.hasErrors()) {
            List<String> errosMessages = result.getFieldErrors().stream().map(FieldError::getDefaultMessage
            ).toList();
            return ResponseEntity.badRequest().body(errosMessages);
        }
        categoryService.create(categoryDTO);
        return ResponseEntity.ok("successfully ");
    }
    @GetMapping("/{id}")
    public ResponseEntity<?> getCategoryById(@PathVariable  long id) {
        Category category = categoryService.getById(id);
        return ResponseEntity.ok(category);
    }
    @GetMapping("")
    public ResponseEntity<?> getAllCategories() {
        List<Category> categories = categoryService.getAll();

        return ResponseEntity.ok(categories);
    }
    @PutMapping("/{id}")
    public ResponseEntity<?> updateCategoryBy(@PathVariable long id,
                                              @RequestBody @Valid CategoryDTO categoryDTO,
                                              BindingResult result
    ) {
        if(result.hasErrors()) {
            List<String> errosMessages = result.getFieldErrors().stream().map(FieldError::getDefaultMessage
            ).toList();
            return ResponseEntity.badRequest().body(errosMessages);
        }
        categoryService.update(id,categoryDTO);
        return ResponseEntity.ok(CategoryMessageResponse.builder()
                        .message(localizationUtils.getLocalizeMessage(MessageKeys.UPDATE_CATEGORY_SUCCESSFULLY))
                .build());
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCategory(@PathVariable long id) {
        categoryService.delete(id);
        return ResponseEntity.ok(
                CategoryMessageResponse.builder()
                        .message(localizationUtils.getLocalizeMessage("delete category with id:"+id+" successfully"))
                        .build());
    }


}
