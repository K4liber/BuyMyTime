package web.controllers;

import java.io.IOException;
import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import data.entities.Category;
import data.entities.User;
import repositories.CategoryRepository;

@Controller
public class AdminController {
	
	@Autowired
	CategoryRepository categoryRepository;
	
	@RequestMapping(value="/admin/categories", method=RequestMethod.GET)
	public String showRegistrationForm( Model model){
		model.addAttribute("category", new Category());
		List<Category> categories = categoryRepository.findAll();
		model.addAttribute("categories",categories);
		return "categories";
	}
	
	@RequestMapping(value="/admin/categories", method=RequestMethod.POST)
	public String processRegistration(@ModelAttribute(value="category") Category category, Model model){
		categoryRepository.save(category);
		List<Category> categories = categoryRepository.findAll();
		model.addAttribute("categories",categories);
		return "categories";
	}
}
