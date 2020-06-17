package com.mhp.ecommerce.app.models.dao;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.mhp.ecommerce.app.models.entity.Product;


public interface IProductDao extends CrudRepository<Product, Long>{

	@Query("select p from Product p where p.name like %?1% or p.id like %?1% or p.price like %?1%")
	public List<Product> findByName(String term);
	
	public List<Product> findByNameLikeIgnoreCase(String term);
}
