package com.polar.habbohotel.navigator;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
public class NavigatorManager {

    private final NavigatorCategoryRepository categoryRepository;
    private final Map<Integer, NavigatorCategory> categories = new ConcurrentHashMap<>();

    @Autowired
    public NavigatorManager(NavigatorCategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public void init() {
        log.info("Loading navigator categories...");
        categories.clear();
        List<NavigatorCategory> categoryList = categoryRepository.findByEnabledTrueOrderByOrderIdAsc();
        for (NavigatorCategory category : categoryList) {
            categories.put(category.getId(), category);
        }
        log.info("Loaded {} navigator categories.", categories.size());
    }

    public NavigatorCategory getCategory(int id) {
        return categories.get(id);
    }

    public List<NavigatorCategory> getAllCategories() {
        return List.copyOf(categories.values());
    }
}
