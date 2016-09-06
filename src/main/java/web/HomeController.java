package web;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import repositories.CategoryRepository;
import data.entities.Category;
import data.entities.User;

@RequestMapping
@Controller
public class HomeController {
	
	@Autowired
	CategoryRepository categoryRepository;
	
	@RequestMapping(value="/", method=RequestMethod.GET)
	public String home(Model model){
		List<Category> categories = categoryRepository.findAll();
		model.addAttribute("categories",categories);
		model.addAttribute("user", new User());
		return "home";
	}
	
	@RequestMapping(value="/categories", method=RequestMethod.GET)
	@ResponseBody
	public List<Category> categories(Model model){
		List<Category> categories = categoryRepository.findAll();
		return categories;
	}
	
}
