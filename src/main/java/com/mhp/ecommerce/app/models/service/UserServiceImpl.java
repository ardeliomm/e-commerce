package com.mhp.ecommerce.app.models.service;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mhp.ecommerce.app.models.dao.ICartDao;
import com.mhp.ecommerce.app.models.dao.IProductDao;
import com.mhp.ecommerce.app.models.dao.IUserDao;
import com.mhp.ecommerce.app.models.entity.Cart;
import com.mhp.ecommerce.app.models.entity.Product;
import com.mhp.ecommerce.app.models.entity.Role;
import com.mhp.ecommerce.app.models.entity.UserSystem;


@Service
public class UserServiceImpl implements IUserService{
	
	@Autowired
	private BCryptPasswordEncoder passwordEncoder;

	@Autowired
	private IUserDao userDao;
	
	@Autowired
	private IProductDao productDao;
	
	@Autowired
	private ICartDao cartDao;
	
	private Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);
	
	@Override
	@Transactional(readOnly = true)
	public List<UserSystem> findAll() {
		// TODO Auto-generated method stub
		return (List<UserSystem>) userDao.findAll();
	}

	@Override
	@Transactional
	public void save(UserSystem userSystem) {
		userSystem.setPassword(passwordEncoder.encode(userSystem.getPassword()));
		userSystem.setEnabled(true);
		//The users created are buyers by default 
		List<Role> roles = new ArrayList<Role>();
		Role buyerRole = new Role();
		buyerRole.setAuthority("ROLE_USER");
		roles.add(buyerRole);
		userSystem.setRoles(roles );
		userDao.save(userSystem);
	}

	@Override
	@Transactional(readOnly = true)
	public UserSystem findOne(Long id) {
		return userDao.findById(id).orElse(null);
	}
	
	@Override
	@Transactional(readOnly = true)
	public UserSystem fetchByIdWithCarts(Long id) {
		return userDao.fetchByIdWithCarts(id);
	}

	@Override
	@Transactional
	public void delete(Long id) {
		userDao.deleteById(id);
	}

	@Override
	@Transactional(readOnly = true)
	public Page<UserSystem> findAll(Pageable pageable) {
		return userDao.findAll(pageable);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	@Transactional(readOnly = true)
	public Page<UserSystem> findUserAll(Pageable pageable, String username) {
		return (Page<UserSystem>) userDao.findByUsername(username);
	}

	@Override
	@Transactional(readOnly = true)
	public List<Product> findByName(String term) {
		return productDao.findByName(term);
	}
	
	@Override
	@Transactional(readOnly = true)
	public List<Product> findAllProducts() {
		return (List<Product>) productDao.findAll();
	}

	@Override
	public UserSystem findUserByName(String username) {
		return userDao.findByUsername(username);
	}

	@Override
	@Transactional
	public void saveCart(Cart cart) {
		cartDao.save(cart);
	}

	@Override
	@Transactional(readOnly=true)
	public Product findProductById(Long id) {
		return productDao.findById(id).orElse(null);
	}

	@Override
	@Transactional(readOnly=true)
	public Cart findCartById(Long id) {
		return cartDao.findById(id).orElse(null);
	}

	@Override
	@Transactional
	public void deleteCart(Long id) {
		cartDao.deleteById(id); // facturaDao.deleteById(id);
	}

	@Override
	@Transactional(readOnly=true)
	public Cart fetchCartByIdWithUserSystemWhithCartItemWithProduct(Long id) {
		return cartDao.fetchByIdWithUserSystemWhithCartItemWithProduct(id);
	}
	
	@Override
	@Transactional(readOnly=true)
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		
        UserSystem userSystem = userDao.findByUsername(username);
        
        if(userSystem == null) {
        	logger.error("Error en el Login: no existe el usuario '" + username + "' en el sistema!");
        	throw new UsernameNotFoundException("Username: " + username + " no existe en el sistema!");
        }
        
        List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
        
        for(Role role: userSystem.getRoles()) {
        	logger.info("Role: ".concat(role.getAuthority()));
        	authorities.add(new SimpleGrantedAuthority(role.getAuthority()));
        }
        
        if(authorities.isEmpty()) {
        	logger.error("Error en el Login: Usuario '" + username + "' no tiene roles asignados!");
        	throw new UsernameNotFoundException("Error en el Login: usuario '" + username + "' no tiene roles asignados!");
        }
        
		return new User(userSystem.getUsername(), userSystem.getPassword(), userSystem.getEnabled(), true, true, true, authorities);
	}
	
	
}
