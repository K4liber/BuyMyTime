package web;

import java.security.Principal;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import repositories.CardRepository;
import repositories.CardTagRepository;
import repositories.TagToCardRepository;
import repositories.UserRepository;
import data.entities.Card;
import data.entities.CardModel;
import data.entities.CardTag;
import data.entities.TagToCard;
import data.entities.User;

@Controller
public class CardController {
	
	@Autowired
	UserRepository userRepository;
	@Autowired
	CardRepository cardRepository;
	@Autowired
	CardTagRepository cardTagRepository;
	@Autowired
	TagToCardRepository tagToCardRepository;
	
	@RequestMapping(value="/cards", method=RequestMethod.GET)
	@ResponseBody
	public List<Card> showCards( Model model){
		List<Card> cards = cardRepository.findAll();
		return cards;
	}
	
	@RequestMapping(value="/newcard", method=RequestMethod.GET)
	public String showCardForm( Model model){
		CardModel cardModel = new CardModel();
		cardModel.setCard(new Card());
		model.addAttribute("cardModel", cardModel);
		return "cardForm";
	}
	
	@RequestMapping(value = { "/newcard" }, method = RequestMethod.POST)
	public String login(HttpServletRequest request, @ModelAttribute(value="cardModel") @Valid CardModel cardModel,
			BindingResult bindingCardModelResult, RedirectAttributes model, Principal principal) throws Exception {

		if(bindingCardModelResult.hasErrors()){
			System.out.println(bindingCardModelResult.toString());
			return "cardForm";
		}
		
		Card cardFromModel = cardModel.getCard();
		User user = userRepository.findByUsername(principal.getName());
		cardFromModel.setUserId(user.getId());
		cardFromModel.setUserNick(user.getUsername());
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
