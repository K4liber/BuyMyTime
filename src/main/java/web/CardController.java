package web;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import data.Card;
import data.CardModel;
import data.CardRepository;
import data.CardTagRepository;
import data.CardTag;
import data.TagToCard;
import data.TagToCardRepository;
import data.User;

@Controller
public class CardController {
	
	@Autowired
	CardRepository cardRepository;
	@Autowired
	CardTagRepository cardTagRepository;
	@Autowired
	TagToCardRepository tagToCardRepository;
	
	@RequestMapping(value="/cards", method=RequestMethod.GET)
	public String showCards( Model model){
		List<Card> cards = cardRepository.findAll();
		model.addAttribute("cards", cards);
		return "cards";
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
			BindingResult bindingCardModelResult, RedirectAttributes model) throws Exception {

		if(bindingCardModelResult.hasErrors()){
			System.out.println(bindingCardModelResult.toString());
			return "cardForm";
		}
		
		Card cardFromModel = cardModel.getCard();
		HttpSession session = request.getSession();
		User user = (User) session.getAttribute("user");
		cardFromModel.setUserId(user.getId());
		cardFromModel.setUserNick(user.getNick());
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
