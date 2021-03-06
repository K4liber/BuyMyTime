package web.controllers;

import java.security.Principal;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import repositories.CardRepository;
import repositories.CardTagRepository;
import repositories.CategoryRepository;
import repositories.TagToCardRepository;
import repositories.UserProfileRepository;
import repositories.UserRepository;
import data.entities.Card;
import data.entities.CardTag;
import data.entities.Category;
import data.entities.TagToCard;
import data.entities.User;
import data.entities.UserProfile;
import data.views.CardModel;

@Controller
public class CardController {
	
	@Autowired
	UserRepository userRepository;
	@Autowired
	UserProfileRepository userProfileRepository;
	@Autowired
	CardRepository cardRepository;
	@Autowired
	CardTagRepository cardTagRepository;
	@Autowired
	TagToCardRepository tagToCardRepository;
	@Autowired
	CategoryRepository categoryRepository;
	
	@RequestMapping(value="/cards", method=RequestMethod.GET)
	@ResponseBody
	public List<Card> showCards( Model model){
		List<Card> cards = cardRepository.findAll();
		return cards;
	}
	
	@RequestMapping(value="/cards/{categoryName}", method=RequestMethod.GET)
	@ResponseBody
	public List<Card> showCardsByCategory(@PathVariable("categoryName") String categoryName, Model model){
		List<Card> cards = cardRepository.findAllByCategoryName(categoryName);
		return cards;
	}
	
	@RequestMapping(value = { "/newcard" }, method = RequestMethod.POST)
	public String newCard(@RequestBody CardModel cardModel,
			RedirectAttributes model, Principal principal) throws Exception {
		Card cardFromModel = cardModel.getCard();
		User user = userRepository.findByUsername(principal.getName());
		UserProfile userProfile = userProfileRepository.findByUsername(principal.getName());
		cardFromModel.setUserId(user.getId());
		cardFromModel.setUserNick(user.getUsername());
		cardFromModel.setAuthorImageName(userProfile.getImageName());
		Card savedCard = cardRepository.save(cardModel.getCard());
		String tagsTable[] = cardModel.getTags().split(" ");
		for(int ii=0;ii<tagsTable.length;ii++){
			CardTag cardTag = new CardTag();
			cardTag.setTitle(tagsTable[ii]);
			CardTag savedCardTag = cardTagRepository.save(cardTag);
			TagToCard tagToCard = new TagToCard();
			tagToCard.setCardId(savedCard.getId());
			tagToCard.setTagId(savedCardTag.getId());
			tagToCardRepository.save(tagToCard);
		}
	    return "home";
	}
}
