package com.mhp.ecommerce.app.controllers;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Collection;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.mhp.ecommerce.app.models.entity.UserSystem;
import com.mhp.ecommerce.app.models.service.IUploadFileService;
import com.mhp.ecommerce.app.models.service.IUserService;
import com.mhp.ecommerce.app.util.paginator.PageRender;

@Controller
@SessionAttributes("userSystem")
public class UserController {

	protected final Log logger = LogFactory.getLog(this.getClass());	

	@Autowired
	private IUserService userService;

	@Autowired
	private IUploadFileService uploadFileService;

	@Secured({ "ROLE_USER" })
	@GetMapping(value = "/uploads/{filename:.+}")
	public ResponseEntity<Resource> verFoto(@PathVariable String filename) {

		Resource recurso = null;

		try {
			recurso = uploadFileService.load(filename);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return ResponseEntity.ok()
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + recurso.getFilename() + "\"")
				.body(recurso);
	}

	@PreAuthorize("hasRole('ROLE_USER')")
	@GetMapping(value = "/details/{id}")
	public String details(@PathVariable(value = "id") Long id, Model model, RedirectAttributes flash) {

		UserSystem userSystem = userService.fetchByIdWithCarts(id);
		if (userSystem == null) {
			flash.addFlashAttribute("error", "El usuario no existe en la base de datos");
			return "redirect:/listed";
		}

		model.addAttribute("userSystem", userSystem);
		model.addAttribute("titulo", "Detalle de usuario: " + userSystem.getName());
		return "details";
	}

	@Secured({ "ROLE_USER", "ROLE_ADMIN" })
	@RequestMapping(value = { "/listed", "/" }, method = RequestMethod.GET)
	public String listed(@RequestParam(name = "page", defaultValue = "0") int page, Model model,
			Authentication authentication, HttpServletRequest request) {

		if (authentication != null) {
			logger.info("Hola usuario autenticado, tu username es: ".concat(authentication.getName()));
		}

		Authentication auth = SecurityContextHolder.getContext().getAuthentication();

		if (auth != null) {
			logger.info(
					"Utilizando forma estática SecurityContextHolder.getContext().getAuthentication(): Usuario autenticado: "
							.concat(auth.getName()));
		}

		Pageable pageRequest = PageRequest.of(page, 4);
		Page<UserSystem> usersSystem;
		
		if (hasRole("ROLE_ADMIN")) {
			usersSystem = userService.findAll(pageRequest);
		} else {
			ArrayList<UserSystem> userSystemList = new ArrayList<UserSystem>();
			userSystemList.add(userService.findUserByName(auth.getName()));
			int start = (int) pageRequest.getOffset();
			int end = (start + pageRequest.getPageSize()) > userSystemList.size() ? userSystemList.size() : (start + pageRequest.getPageSize());
			Page<UserSystem> pages = new PageImpl<UserSystem>(userSystemList.subList(start, end), pageRequest, userSystemList.size());
			usersSystem = pages;
		}		

		PageRender<UserSystem> pageRender = new PageRender<UserSystem>("/listed", usersSystem);
		model.addAttribute("titulo", "Listado de usuarios");
		model.addAttribute("usersSystem", usersSystem);
		model.addAttribute("page", pageRender);
		return "listed";
	}

	@Secured("ROLE_ADMIN")
	@RequestMapping(value = "/form")
	public String create(Model model) {

		UserSystem userSystem = new UserSystem();		
		model.addAttribute("userSystem", userSystem);
		model.addAttribute("titulo", "Crear Usuario");
		return "form";
	}

	@Secured({"ROLE_ADMIN", "ROLE_USER"})
	@RequestMapping(value = "/form/{id}")
	public String edit(@PathVariable(value = "id") Long id, Model model, RedirectAttributes flash) {

		UserSystem userSystem = null;

		if (id > 0) {
			userSystem = userService.findOne(id);
			if (userSystem == null) {
				flash.addFlashAttribute("error", "El ID del usuario no existe en la BBDD!");
				return "redirect:/listed";
			}
		} else {
			flash.addFlashAttribute("error", "El ID del usuario no puede ser cero!");
			return "redirect:/listed";
		}
		model.addAttribute("userSystem", userSystem);
		model.addAttribute("titulo", "Editar Usuario");
		return "form";
	}

	@Secured({"ROLE_ADMIN", "ROLE_USER"})
	@RequestMapping(value = "/form", method = RequestMethod.POST)
	public String save(@Valid UserSystem userSystem, BindingResult result, Model model,
			@RequestParam("file") MultipartFile foto, RedirectAttributes flash, SessionStatus status) {

		if (result.hasErrors()) {
			if(userSystem.getId() != null) {
				model.addAttribute("titulo", "Editar Usuario");
			} else {
				model.addAttribute("titulo", "Crear Usuario");
			}
			
			return "form";
		}

		if (!foto.isEmpty()) {

			if (userSystem.getId() != null && userSystem.getId() > 0 && userSystem.getPhoto() != null
					&& userSystem.getPhoto().length() > 0) {

				uploadFileService.delete(userSystem.getPhoto());
			}

			String uniqueFilename = null;
			try {
				uniqueFilename = uploadFileService.copy(foto);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			flash.addFlashAttribute("info", "Has subido correctamente '" + uniqueFilename + "'");

			userSystem.setPhoto(uniqueFilename);

		}

		String menssageFlash = (userSystem.getId() != null) ? "Usuario editado con éxito!"
				: "Usuario creado con éxito!";

		userService.save(userSystem);
		status.setComplete();
		flash.addFlashAttribute("success", menssageFlash);
		return "redirect:listed";
	}

	@Secured("ROLE_ADMIN")
	@RequestMapping(value = "/delete/{id}")
	public String delete(@PathVariable(value = "id") Long id, RedirectAttributes flash) {

		if (id > 0) {
			UserSystem userSystem = userService.findOne(id);

			userService.delete(id);
			flash.addFlashAttribute("success", "User eliminado con éxito!");

			if (uploadFileService.delete(userSystem.getPhoto())) {
				flash.addFlashAttribute("info", "Foto " + userSystem.getPhoto() + " eliminada con exito!");
			}

		}
		return "redirect:/listed";
	}

	private boolean hasRole(String role) {

		SecurityContext context = SecurityContextHolder.getContext();

		if (context == null) {
			return false;
		}

		Authentication auth = context.getAuthentication();

		if (auth == null) {
			return false;
		}

		Collection<? extends GrantedAuthority> authorities = auth.getAuthorities();

		return authorities.contains(new SimpleGrantedAuthority(role));

	}
}
