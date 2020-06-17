package com.mhp.ecommerce.app.controllers;

import java.util.List;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.mhp.ecommerce.app.models.entity.Cart;
import com.mhp.ecommerce.app.models.entity.CartItem;
import com.mhp.ecommerce.app.models.entity.Product;
import com.mhp.ecommerce.app.models.entity.UserSystem;
import com.mhp.ecommerce.app.models.service.IUserService;

@Secured("ROLE_ADMIN")
@Controller
@RequestMapping("/cart")
@SessionAttributes("cart")
public class CartController {

	@Autowired
	private IUserService userService;

	private final Logger log = LoggerFactory.getLogger(getClass());

	@Secured({"ROLE_ADMIN", "ROLE_USER"})
	@GetMapping("/details/{id}")
	public String details(@PathVariable(value = "id") Long id, Model model, RedirectAttributes flash) {

		Cart cart = userService.fetchCartByIdWithUserSystemWhithCartItemWithProduct(id);  //   userService.findCartById(id);

		if (cart == null) {
			flash.addFlashAttribute("error", "El carro de compras no existe en la base de datos!");
			return "redirect:/listed";
		}

		model.addAttribute("cart", cart);
		model.addAttribute("titulo", "Carro de Compras: ".concat(cart.getCartName()));
		return "cart/details";
	}

	@Secured({"ROLE_ADMIN", "ROLE_USER"})
	@GetMapping("/form/{userId}")
	public String create(@PathVariable(value = "userId") Long userId, Model model,
			RedirectAttributes flash) {

		UserSystem userSystem = userService.findOne(userId);
		List<Product> products = userService.findAllProducts();

		if (userSystem == null) {
			flash.addFlashAttribute("error", "El usuario no existe en la base de datos");
			return "redirect:/listed";
		}

		Cart cart = new Cart();
		cart.setUser(userSystem);

		model.addAttribute("cart", cart);
		model.addAttribute("products", products);
		model.addAttribute("titulo", "Crear Carro de compras");

		return "cart/form";
	}

	@Secured({"ROLE_ADMIN", "ROLE_USER"})
	@GetMapping(value = "/load-products/{term}", produces = { "application/json" })
	public @ResponseBody List<Product> loadProducts(@PathVariable String term) {
		return userService.findByName(term);
	}
	
	@Secured({"ROLE_ADMIN", "ROLE_USER"})
	@RequestMapping(value = "/edit/{id}")
	public String edit(@PathVariable(value = "id") Long id, Model model, RedirectAttributes flash) {

		Cart cart = userService.fetchCartByIdWithUserSystemWhithCartItemWithProduct(id);  //   userService.findCartById(id);
		List<Product> products = userService.findAllProducts();

		if (cart == null) {
			flash.addFlashAttribute("error", "El carro de compras no existe en la base de datos!");
			return "redirect:/listed";
		}

		model.addAttribute("cart", cart);
		model.addAttribute("products", products);
		model.addAttribute("titulo", "Editar Carro de compras");
		return "cart/form";
	}

	@Secured({"ROLE_ADMIN", "ROLE_USER"})
	@PostMapping("/form")
	public String save(@Valid Cart cart, BindingResult result, Model model,
			@RequestParam(name = "item_id[]", required = false) Long[] itemId,
			@RequestParam(name = "quantity[]", required = false) Integer[] quantity, RedirectAttributes flash,
			SessionStatus status) {

		if (result.hasErrors()) {
			log.info("Result empty");
			String titulo = (cart.getId() != null) ? "Editar Carro de Compras"
					: "Crear Carro de Compras";
			model.addAttribute("titulo", titulo);
			return "cart/form";
		}

		if (itemId == null || itemId.length == 0) {
			log.info("Carro vacio");
			String titulo = (cart.getId() != null) ? "Editar Carro de Compras"
					: "Crear Carro de Compras";
			model.addAttribute("titulo", titulo);
			model.addAttribute("cart",cart);
			model.addAttribute("error", "Error: La carro de compras no puede estar vacío!");
			return "cart/form";
		}					

		if(cart.getId() == null) {
			for (int i = 0; i < itemId.length; i++) {
				Product product = userService.findProductById(itemId[i]);
				CartItem item = new CartItem();					
				item.setQuantity(quantity[i]);
				item.setProduct(product);
				cart.addItemCart(item);						

				log.info("ID: " + itemId[i].toString() + ", quantity: " + quantity[i].toString());
			}
			flash.addFlashAttribute("success", "Carro de compras creado con éxito!");
		} else {
			//TODO Update items of Cart
			flash.addFlashAttribute("success", "Carro de compras editado con éxito!");
		}
		
		userService.saveCart(cart);
		status.setComplete();

		

		return "redirect:/details/" + cart.getUser().getId();
	}

	@Secured({"ROLE_ADMIN", "ROLE_USER"})
	@GetMapping("/delete/{id}")
	public String delete(@PathVariable(value = "id") Long id, RedirectAttributes flash) {

		Cart cart = userService.findCartById(id);

		if (cart != null) {
			userService.deleteCart(id);
			flash.addFlashAttribute("success", "Carro de compras eliminado con éxito!");
			return "redirect:/details/" + cart.getUser().getId();
		}
		flash.addFlashAttribute("error", "El carro de compras no existe en la base de datos, no se pudo eliminar!");

		return "redirect:/listed";
	}

}
