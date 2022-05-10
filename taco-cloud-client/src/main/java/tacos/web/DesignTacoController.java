package tacos.web;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;

import tacos.Ingredient;
import tacos.Ingredient.Type;
import tacos.Taco;

@Controller
@RequestMapping("/design")
public class DesignTacoController {
	private RestTemplate rest = new RestTemplate();
	// rest.postForObject("http://localhost:8080/ingredients", ingredients,
	// Ingredient.class);

	@ModelAttribute
	public void addIngredientsToModel(Model model) {

		List<Ingredient> ingredients = Arrays
				.asList(rest.getForObject("http://localhost:8080/ingredients", Ingredient[].class));

		Type[] types = Ingredient.Type.values();
		for (Type type : types) {
			model.addAttribute(type.toString().toLowerCase(), filterByType(ingredients, type));

		}
	}

	private Object filterByType(List<Ingredient> ingredients, Type type) {
		// TODO Auto-generated method stub
		return rest.postForObject("http://localhost:8080/ingredients/CARN", ingredients, Ingredient.class);
	}

	@PostMapping
	public String processDesign(@RequestParam("ingredients") String ingredientIds, @RequestParam("name") String name) {
		List<Ingredient> ingredients = new ArrayList<Ingredient>();
		for (String ingredientId : ingredientIds.split(",")) {
			Ingredient ingredient = rest.getForObject("http://localhost:8080/ ingredients/{id}", Ingredient.class,
					ingredientId);
			ingredients.add(ingredient);
		}
		Taco taco = new Taco();
		taco.setName(name);
		taco.setIngredients(ingredients);
		System.out.println(taco);
		rest.postForObject("http://localhost:8080/design", taco, Taco.class);
		return "redirect:/orders/current";
	}
}