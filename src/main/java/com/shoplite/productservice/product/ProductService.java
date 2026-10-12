package com.shoplite.productservice.product;

import com.shoplite.productservice.product.ProductDtos.ProductRequest;
import com.shoplite.productservice.product.ProductDtos.ProductView;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

/**
 * Cache-aside with Redis: reads go through {@code @Cacheable}, every write evicts the affected
 * entries so the next read reloads from PostgreSQL.
 */
@Service
public class ProductService {

	private static final Logger log = LoggerFactory.getLogger(ProductService.class);

	private final ProductRepository repository;

	public ProductService(ProductRepository repository) {
		this.repository = repository;
	}

	@Cacheable(cacheNames = "product-list", key = "'all'")
	public List<ProductView> findAll() {
		log.info("Cache miss: loading product list from database");
		return repository.findAllByOrderByIdAsc().stream().map(ProductView::of).toList();
	}

	@Cacheable(cacheNames = "product", key = "#id")
	public ProductView findById(long id) {
		log.info("Cache miss: loading product {} from database", id);
		return repository.findById(id).map(ProductView::of).orElseThrow(() -> notFound(id));
	}

	@Transactional
	@CacheEvict(cacheNames = "product-list", allEntries = true)
	public ProductView create(ProductRequest req) {
		Product saved = repository.save(new Product(req.name(), req.description(), req.price(), req.stock()));
		log.info("Created product {}", saved.getId());
		return ProductView.of(saved);
	}

	@Transactional
	@Caching(evict = { @CacheEvict(cacheNames = "product", key = "#id"),
			@CacheEvict(cacheNames = "product-list", allEntries = true) })
	public ProductView update(long id, ProductRequest req) {
		Product product = repository.findById(id).orElseThrow(() -> notFound(id));
		product.update(req.name(), req.description(), req.price(), req.stock());
		return ProductView.of(product);
	}

	@Transactional
	@Caching(evict = { @CacheEvict(cacheNames = "product", key = "#id"),
			@CacheEvict(cacheNames = "product-list", allEntries = true) })
	public void reserve(long id, int quantity) {
		if (!repository.existsById(id)) {
			throw notFound(id);
		}
		if (repository.reserve(id, quantity) == 0) {
			log.warn("Insufficient stock for product {} (requested {})", id, quantity);
			throw new ResponseStatusException(HttpStatus.CONFLICT, "Insufficient stock");
		}
		log.info("Reserved {} unit(s) of product {}", quantity, id);
	}

	private static ResponseStatusException notFound(long id) {
		return new ResponseStatusException(HttpStatus.NOT_FOUND, "Product " + id + " not found");
	}

}
