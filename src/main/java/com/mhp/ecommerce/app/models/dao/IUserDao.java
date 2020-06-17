package com.mhp.ecommerce.app.models.dao;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.mhp.ecommerce.app.models.entity.UserSystem;


public interface IUserDao extends PagingAndSortingRepository<UserSystem, Long>{

	public UserSystem findByUsername(String username);
	
	@Query("select u from UserSystem u left join fetch u.carts c where u.id=?1")
	public UserSystem fetchByIdWithCarts(Long id);
}
