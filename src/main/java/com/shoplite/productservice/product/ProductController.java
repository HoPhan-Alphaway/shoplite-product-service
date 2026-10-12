package com.shoplite.productservice.product;

import com.shoplite.productservice.product.ProductDtos.ProductRequest;
import com.shoplite.productservice.product.ProductDtos.ProductView;
import com.shoplite.productservice.product.ProductDtos.ReserveRequest;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/products")
public class ProductController {

	private final ProductService service;

	public ProductController(ProductService service) {
		this.service = service;
	}

	@GetMapping
	public List<ProductView> list() {
		return service.findAll();
	}

	@GetMapping("/{id}")
	public ProductView get(@PathVariable long id) {
		return service.findById(id);
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	@PreAuthorize("hasRole('admin')")
	public ProductView create(@Valid @RequestBody ProductRequest req) {
		return service.create(req);
	}

	@PutMapping("/{id}")
	@PreAuthorize("hasRole('admin')")
	public ProductView update(@PathVariable long id, @Valid @RequestBody ProductRequest req) {
		return service.update(id, req);
	}

	/** Internal: called by order-service with a client-credentials token (service account). */
	@PostMapping("/{id}/reserve")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	@PreAuthorize("hasRole('stock-writer')")
	public void reserve(@PathVariable long id, @Valid @RequestBody ReserveRequest req) {
		service.reserve(id, req.quantity());
	}

}
