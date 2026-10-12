package com.shoplite.productservice.product;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProductRepository extends JpaRepository<Product, Long> {

	List<Product> findAllByOrderByIdAsc();

	/**
	 * Atomic "check and decrement": returns 0 when stock is insufficient, so two concurrent
	 * orders can never oversell the last item.
	 */
	@Modifying
	@Query("update Product p set p.stock = p.stock - :qty, p.updatedAt = CURRENT_TIMESTAMP "
			+ "where p.id = :id and p.stock >= :qty")
	int reserve(@Param("id") long id, @Param("qty") int qty);

}
