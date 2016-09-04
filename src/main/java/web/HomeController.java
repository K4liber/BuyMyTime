package web;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import repositories.CategoryRepository;
import data.entities.Category;

@RequestMapping
@Controller
public class HomeController {
	
	@Autowired
	CategoryRepository categoryRepository;
	
	@RequestMapping(value="/", method=RequestMethod.GET)
	public String home(Model model){
		List<Category> categories = categoryRepository.findAll();
		model.addAttribute("categories",categories);
		return "home";
	}
	
}
