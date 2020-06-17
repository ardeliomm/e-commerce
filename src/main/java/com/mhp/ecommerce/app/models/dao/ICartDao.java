package com.mhp.ecommerce.app.models.dao;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.mhp.ecommerce.app.models.entity.Cart;

public interface ICartDao extends CrudRepository<Cart, Long>{

	@Query("select c from Cart c join fetch c.user u join fetch c.items l join fetch l.product where c.id=?1")
	public Cart fetchByIdWithUserSystemWhithCartItemWithProduct(Long id);
}
