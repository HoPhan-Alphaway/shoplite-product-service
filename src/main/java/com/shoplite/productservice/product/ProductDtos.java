package com.shoplite.productservice.product;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import java.io.Serializable;
import java.math.BigDecimal;

public final class ProductDtos {

	private ProductDtos() {
	}

	/** Cached in Redis, hence Serializable. */
	public record ProductView(Long id, String name, String description, BigDecimal price, int stock)
			implements Serializable {

		static ProductView of(Product p) {
			return new ProductView(p.getId(), p.getName(), p.getDescription(), p.getPrice(), p.getStock());
		}
	}

	public record ProductRequest(@NotBlank String name, String description,
			@NotNull @PositiveOrZero BigDecimal price, @PositiveOrZero int stock) {
	}

	public record ReserveRequest(@Min(1) int quantity) {
	}

}
