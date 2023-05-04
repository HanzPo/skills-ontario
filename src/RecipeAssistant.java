import java.awt.Dimension;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

import javax.swing.*;

public class RecipeAssistant {

	// Specifies location of recipies file
	private static String recipesFile = "src/recipes_modified.csv";

	// Declarations
	private String name;
	private ArrayList<Recipe> recipes = new ArrayList<>();

	private StringBuilder builder = new StringBuilder();
	private StringBuilder shoppingList = new StringBuilder();

	public static void main(String[] args) {
		RecipeAssistant bakedBytes = new RecipeAssistant("Baked Bytes");

		// NOTE: CSV file has been slightly edited from what was provided in order to fix formatting errors
		bakedBytes.loadRecipes(recipesFile);
		bakedBytes.mainMenu();
	}

	public RecipeAssistant(String name) {
		this.name = name;
		JOptionPane.showMessageDialog(null, "Welcome to " + name);
	}

	// Reads csv and loads into an ArrayList
	private void loadRecipes(String fileName) {
		String line = "";
		String values[];

		try {
			Scanner input = new Scanner(new File(fileName));

			while (input.hasNext()) {
				line = input.nextLine();

				values = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);

				recipes.add(new Recipe(values[0], Integer.parseInt(values[1]), values[2]));

				Recipe currentRecipe = recipes.get(recipes.size() - 1);

				for (int i = 3; i < values.length; i+=3) {
					currentRecipe.addIngredient(values[i], values[i+1], values[i+2]);
				}
			}
		}
		catch(IOException e) {
			JOptionPane.showMessageDialog(null, e, "Error", JOptionPane.ERROR_MESSAGE);
		}

	}

	// Converts units
	private void convertUnits() {
		final int cupsToTbsp = 16;
		final int cupsToTsp = 48;
		final int tbspToTsp = 3;

		String[] options = new String[recipes.size()];
		int recipeSelection;

		for (int i = 0; i < options.length; i++) {
			options[i] = recipes.get(i).getTitle();
		}

		String input = (String) JOptionPane.showInputDialog(null, "In which recipe would you like to convert units?", name, JOptionPane.PLAIN_MESSAGE, null, options, options[0]);

		recipeSelection = getIndexFromString(input);

		Recipe currentRecipe = recipes.get(recipeSelection);

		Object[] ingredientsToModify = currentRecipe.getIngredients();

		String ingredientToModify = (String) JOptionPane.showInputDialog(null, "Which ingredient would you like to convert units for?", name, JOptionPane.PLAIN_MESSAGE, null, ingredientsToModify, ingredientsToModify[0]);

		int ingredientIndex = this.getIngredientIndexFromString(ingredientToModify, currentRecipe);

		String[] units = {"cups", "tbsp", "tsp"};

		String newUnits = (String) JOptionPane.showInputDialog(null, "This ingredient is currently in " + currentRecipe.getUnit(ingredientIndex) + "\nWhich unit would you like to convert to?", "Modifying units for " + ingredientToModify, JOptionPane.PLAIN_MESSAGE, null, units, units[0]);

		if (currentRecipe.getUnit(ingredientIndex).equals("cup") || currentRecipe.getUnit(ingredientIndex).equals("cups")) {
			if (newUnits.equals("tbsp")) {
				currentRecipe.setQuantity(ingredientIndex, String.valueOf(currentRecipe.getQuantityDouble(ingredientIndex) * cupsToTbsp));
			}
			else if (newUnits.equals("tsp")) {
				currentRecipe.setQuantity(ingredientIndex, String.valueOf(currentRecipe.getQuantityDouble(ingredientIndex) * cupsToTsp));
			}
		}
		else if (currentRecipe.getUnit(ingredientIndex).equals("tbsp")) {
			if (newUnits.equals("cups")) {
				currentRecipe.setQuantity(ingredientIndex, String.valueOf(currentRecipe.getQuantityDouble(ingredientIndex) / cupsToTbsp));
			}
			else if (newUnits.equals("tsp")) {
				currentRecipe.setQuantity(ingredientIndex, String.valueOf(currentRecipe.getQuantityDouble(ingredientIndex) * tbspToTsp));
			}
		}
		else if (currentRecipe.getUnit(ingredientIndex).equals("tsp")) {
			if (newUnits.equals("cups")) {
				currentRecipe.setQuantity(ingredientIndex, String.valueOf(currentRecipe.getQuantityDouble(ingredientIndex) / cupsToTsp));
			}
			else if (newUnits.equals("tbsp")) {
				currentRecipe.setQuantity(ingredientIndex, String.valueOf(currentRecipe.getQuantityDouble(ingredientIndex) / tbspToTsp));
			}
		}

		currentRecipe.setUnit(ingredientIndex, newUnits);

		int result = JOptionPane.showConfirmDialog(null,"Would you like this changes to persist after exiting?", "Save?", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
		if(result == JOptionPane.YES_OPTION){
			try {
				this.saveCSV(recipesFile);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	// Gets index of a recipe based on a provided string
	private int getIngredientIndexFromString(String string, Recipe rec) {
		for (int i = 0; i < rec.getIngredientLength(); i++) {
			if (rec.getIngredient(i).equals(string)) {
				return i;
			}
		}

		return -1;
	}

	// Shows all recipes 
	private void showAllRecipes() {
		builder.setLength(0);

		builder.append("Here are all of the recipes currently available at " + name + ":");
		builder.append("\n\n");

		for (int i = 0; i < recipes.size(); i++) {
			builder.append((i + 1) + ") " + recipes.get(i).getTitle());
			builder.append("\n");
		}

		JOptionPane.showMessageDialog(null, builder, name, JOptionPane.PLAIN_MESSAGE);
	}

	private void showRecipe() {
		String[] options = new String[recipes.size()];
		int recipeSelection;

		for (int i = 0; i < options.length; i++) {
			options[i] = recipes.get(i).getTitle();
		}

		String input = (String) JOptionPane.showInputDialog(null, "Which recipe would you like to print?", name, JOptionPane.PLAIN_MESSAGE, null, options, options[0]);

		recipeSelection = getIndexFromString(input);

		Recipe currentRecipe = recipes.get(recipeSelection);

		builder.setLength(0);
		builder.append("This recipe serves " + currentRecipe.getServings() + " people");
		builder.append("\n\n");
		builder.append("Ingredients:");
		builder.append("\n");
		builder.append(currentRecipe.getIngredientsString());
		builder.append("\n");
		builder.append("Instructions: \n" + currentRecipe.getInstructions().toString());

		JTextArea ta = new JTextArea(builder.toString());
		JScrollPane sp = new JScrollPane(ta);
		ta.setEditable(false);
		ta.setLineWrap(true);
		ta.setWrapStyleWord(true);
		sp.setPreferredSize(new Dimension(500, 500));

		JOptionPane.showMessageDialog(null, sp, currentRecipe.getTitle(), JOptionPane.PLAIN_MESSAGE);

	}

	private void showRecipe(String recipeName) {

		int recipeSelection;

		recipeSelection = getIndexFromString(recipeName);

		Recipe currentRecipe = recipes.get(recipeSelection);

		builder.setLength(0);
		builder.append("This recipe serves " + currentRecipe.getServings() + " people");
		builder.append("\n\n");
		builder.append("Ingredients:");
		builder.append("\n");
		builder.append(currentRecipe.getIngredientsString());
		builder.append("\n");
		builder.append("Instructions: \n" + currentRecipe.getInstructions().toString());

		JTextArea ta = new JTextArea(builder.toString());
		JScrollPane sp = new JScrollPane(ta);
		ta.setEditable(false);
		ta.setLineWrap(true);
		ta.setWrapStyleWord(true);
		sp.setPreferredSize(new Dimension(500, 500));

		JOptionPane.showMessageDialog(null, sp, currentRecipe.getTitle(), JOptionPane.PLAIN_MESSAGE);

	}

	private void showRecipe(int servings) {
		String[] options = new String[recipes.size()];
		int recipeSelection;

		for (int i = 0; i < options.length; i++) {
			options[i] = recipes.get(i).getTitle();
		}

		String input = (String) JOptionPane.showInputDialog(null, "Which recipe would you like to print?", name, JOptionPane.PLAIN_MESSAGE, null, options, options[0]);

		recipeSelection = getIndexFromString(input);

		Recipe currentRecipe = recipes.get(recipeSelection);

		builder.setLength(0);
		builder.append("This recipe has been modified to serve " + servings + " people");
		builder.append("\n\n");
		builder.append("Ingredients:");
		builder.append("\n");
		builder.append(currentRecipe.getIngredientsString(servings));
		builder.append("\n");
		builder.append("Instructions: \n" + currentRecipe.getInstructions().toString());

		JTextArea ta = new JTextArea(builder.toString());
		JScrollPane sp = new JScrollPane(ta);
		ta.setEditable(false);
		ta.setLineWrap(true);
		ta.setWrapStyleWord(true);
		sp.setPreferredSize(new Dimension(500, 500));

		JOptionPane.showMessageDialog(null, sp, currentRecipe.getTitle(), JOptionPane.PLAIN_MESSAGE);

	}

	private int getIndexFromString(String string) {
		for (int i = 0; i < recipes.size(); i++) {
			if (recipes.get(i).getTitle().equals(string)) {
				return i;
			}
		}

		return -1;
	}

	private void createRecipe() {
		String title = JOptionPane.showInputDialog(null, "What would you like to call your recipe?");
		int servings = 0;
		String instructions;

		int ingredientCount = 0;

		try {
			servings = Integer.parseInt(JOptionPane.showInputDialog(null, "How many people does your recipe serve?"));
		}
		catch (Exception e) {
			JOptionPane.showMessageDialog(null, e);
		}

		JTextArea ta = new JTextArea("Please replace this text with the instructions for your recipe");
		JScrollPane sp = new JScrollPane(ta);
		ta.setLineWrap(true);
		ta.setWrapStyleWord(true);
		sp.setPreferredSize(new Dimension(500, 500));

		JOptionPane.showConfirmDialog(null, sp, title + " Instructions", JOptionPane.PLAIN_MESSAGE);

		instructions = ta.getText();

		recipes.add(new Recipe(title, servings, instructions));

		Recipe currentRecipe = recipes.get(recipes.size() - 1);

		try {
			ingredientCount = Integer.parseInt(JOptionPane.showInputDialog(null, "How many ingredients does your recipe have?"));
		}
		catch (Exception e) {
			JOptionPane.showMessageDialog(null, e);
		}

		String[] ingredients = new String[ingredientCount];
		String[] quantities = new String[ingredientCount];
		String[] units = new String[ingredientCount];

		for (int i = 0; i < ingredientCount; i++) {
			ingredients[i] = JOptionPane.showInputDialog(null, "Please enter ingredient number " + (i + 1), "Entering ingredient " + (i + 1) + " of " + ingredientCount, JOptionPane.PLAIN_MESSAGE);
			units[i] = JOptionPane.showInputDialog(null, "What unit does " + ingredients[i].toLowerCase() + " use?\n(cups, tsp, tbsp, etc.)", "Entering ingredient " + (i + 1) + " of " + ingredientCount, JOptionPane.PLAIN_MESSAGE);
			quantities[i] = JOptionPane.showInputDialog(null, "How many " + units[i].toLowerCase() + " of " + ingredients[i].toLowerCase() + " are needed?", "Entering ingredient " + (i + 1) + " of " + ingredientCount, JOptionPane.PLAIN_MESSAGE);
			currentRecipe.addIngredient(ingredients[i], quantities[i], units[i]);
		}

		try {
			this.saveCSV(recipesFile);
		} catch (IOException e) {
			e.printStackTrace();
		}

		showRecipe(title);
	}

	private void deleteRecipe() {
		String[] options = new String[recipes.size()];
		int recipeSelection;

		for (int i = 0; i < options.length; i++) {
			options[i] = recipes.get(i).getTitle();
		}

		String input = (String) JOptionPane.showInputDialog(null, "Which recipe would you like to delete?", name, JOptionPane.PLAIN_MESSAGE, null, options, options[0]);

		recipeSelection = getIndexFromString(input);

		recipes.remove(recipeSelection);

		try {
			this.saveCSV(recipesFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void addToShoppingList() {
		String[] options = new String[recipes.size()];
		int recipeSelection;

		for (int i = 0; i < options.length; i++) {
			options[i] = recipes.get(i).getTitle();
		}

		String input = (String) JOptionPane.showInputDialog(null, "Which recipe's ingredients would you\nlike to add to your shopping list?", name, JOptionPane.PLAIN_MESSAGE, null, options, options[0]);

		recipeSelection = getIndexFromString(input);

		Recipe currentRecipe = recipes.get(recipeSelection);

		shoppingList.append(currentRecipe.getIngredientsString());

		JOptionPane.showMessageDialog(null, "The ingredients for " + input + " have been added to the shopping list");
	}

	private void viewShoppingList() {
		JTextArea ta = new JTextArea(shoppingList.toString());
		JScrollPane sp = new JScrollPane(ta);
		ta.setEditable(false);
		ta.setLineWrap(true);
		ta.setWrapStyleWord(true);
		sp.setPreferredSize(new Dimension(500, 500));

		JOptionPane.showMessageDialog(null, sp, "My shopping list", JOptionPane.PLAIN_MESSAGE);
	}

	private void clearShoppingList() {
		shoppingList.setLength(0);

		JOptionPane.showMessageDialog(null, "The shopping list has been cleared");
	}

	private void dietaryRestrictions() {
		String input = JOptionPane.showInputDialog(null, "What dietary restrictions do you have?\nPlease separate with commas.");
		String[] restrictions = input.split(",");

		for (int i = 0; i < restrictions.length; i++) {
			restrictions[i] = restrictions[i].strip();

			for (int j = 0; j < recipes.size(); j++) {
				if (recipes.get(j).getIngredientsString().toLowerCase().contains(restrictions[i])) {
					recipes.remove(j);
				}
			}
		}

	}


	// Main menu
	private void mainMenu() {
		String[] options = {"Show available recipes", "Print a recipe", "Print a recipe with serving size", "Convert units in a recipe", "Create a recipe", "Delete a recipe", "Add recipe to shopping list", "View shopping list", "Clear shopping list", "I have dietary restrictions", "Exit the program"};
		String input = (String) JOptionPane.showInputDialog(null, "What would you like to do next?", name, JOptionPane.PLAIN_MESSAGE, null, options, options[0]);

		if (input == null) {
			System.exit(0);
		}
		else if (input.equals("Exit the program")) {
			System.exit(0);
		}
		else if (input.equals("Show available recipes")) {
			this.showAllRecipes();
		}
		else if (input.equals("Print a recipe")) {
			this.showRecipe();
		}
		else if (input.equals("Print a recipe with serving size")) {
			try {
				int servingSize = Integer.parseInt(JOptionPane.showInputDialog(null, "How many servings would you like?", name, JOptionPane.PLAIN_MESSAGE));
				this.showRecipe(servingSize);
			}
			catch (Exception e) {
				JOptionPane.showMessageDialog(null, "Please enter a valid number of people", name, JOptionPane.ERROR_MESSAGE);
			}
		}
		else if (input.equals("Add recipe to shopping list")) {
			this.addToShoppingList();
		}
		else if (input.equals("View shopping list")) {
			this.viewShoppingList();
		}
		else if (input.equals("Clear shopping list")) {
			this.clearShoppingList();
		}
		else if (input.equals("Convert units in a recipe")) {
			this.convertUnits();
		}
		else if (input.equals("I have dietary restrictions")) {
			this.dietaryRestrictions();
		}
		else if (input.equals("Create a recipe")) {
			this.createRecipe();
		}
		else if (input.equals("Delete a recipe")) {
			this.deleteRecipe();
		}

		this.mainMenu();
	}

	private void saveCSV(String fileName) throws IOException {
		File csv = new File(fileName);
		FileWriter fileWriter = new FileWriter(csv);

		for (Recipe recipe : recipes) {
			builder.setLength(0);
			builder.append(recipe.getTitle() + ",");
			builder.append(recipe.getServings() + ",");
			builder.append("\"");
			builder.append(recipe.getInstructions().replace("\n", "\\n"));	
			builder.append("\"");
			builder.append(",");

			for (int i = 0; i < recipe.getIngredientLength(); i++) {
				builder.append(recipe.getIngredient(i) + ",");
				builder.append(recipe.getQuantity(i) + ",");
				builder.append(recipe.getUnit(i));
				if (i != recipe.getIngredientLength() - 1) {
					builder.append(",");
				}
			}

			builder.append("\n");
			fileWriter.write(builder.toString());
		}

		fileWriter.close();
	}

}
