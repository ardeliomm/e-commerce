package com.mhp.ecommerce.app.models.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetailsService;

import com.mhp.ecommerce.app.models.entity.Cart;
import com.mhp.ecommerce.app.models.entity.Product;
import com.mhp.ecommerce.app.models.entity.UserSystem;

public interface IUserService extends UserDetailsService{

	public List<UserSystem> findAll();
	
	public Page<UserSystem> findAll(Pageable pageable);
	
	public Page<UserSystem> findUserAll(Pageable pageable, String username);

	public void save(UserSystem userSystem);
	
	public UserSystem findOne(Long id);
	
	public UserSystem findUserByName(String term);
	
	public UserSystem fetchByIdWithCarts(Long id);
	
	public void delete(Long id);
	
	public List<Product> findByName(String term);
	
	public List<Product> findAllProducts();
	
	public Product findProductById(Long id);
	
	public void saveCart(Cart cart);	
	
	public Cart findCartById(Long id);
	
	public void deleteCart(Long id);
	
	public Cart fetchCartByIdWithUserSystemWhithCartItemWithProduct(Long id);

}
