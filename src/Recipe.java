import java.util.*;

public class Recipe {
	
	// Declarations
	private String title;
	private int servings;
	private String instructions;
	private ArrayList<String> ingredients = new ArrayList<>();
	private ArrayList<String> quantities = new ArrayList<>();
	private ArrayList<String> units = new ArrayList<>();
	
	private StringBuilder builder = new StringBuilder();
	
	// Constructor, ensures proper importing of csv
	public Recipe(String title, int servings, String instructions) {
		this.title = title;
		this.servings = servings;
		
		if (instructions.startsWith("\"") && instructions.endsWith("\"")) {
			this.instructions = instructions.substring(1, instructions.length() - 1);
			this.instructions = this.instructions.replace("\\n", "\n");
			
		}
		else {
			this.instructions = instructions;
		}
	}
	
	// Adds a new ingredient
	public void addIngredient(String ingredient, String quantity, String unit) {
		ingredients.add(ingredient);
		quantities.add(quantity);
		units.add(unit);
	}
	
	
	// Getters and setters 
	public String getTitle() {
		return title;
	}
	
	public int getServings() {
		return servings;
	}
	
	public String getInstructions() {
		return instructions;
	}
	
	public Object[] getIngredients() {
		return (Object[]) ingredients.toArray();
	}
	
	public String getIngredientsString() {
		if (ingredients.size() > 0) {
			builder.setLength(0);
			
			for (int i = 0; i < ingredients.size(); i++) {
				builder.append(ingredients.get(i));
				
				if (ingredients.get(i) != "") {
					builder.append(" - ");
					builder.append(quantities.get(i) + " " + units.get(i));
					builder.append("\n");
				}
			}
			
			
			return builder.toString();
		}
		
		return "";
	}
	
	public String getIngredientsString(int customServings) {
		
		if (ingredients.size() > 0) {
			builder.setLength(0);
			
			for (int i = 0; i < ingredients.size(); i++) {
				builder.append(ingredients.get(i));
				
				if (ingredients.get(i) != "") {
					builder.append(" - ");
					builder.append(roundTwo(convertFraction(quantities.get(i)) * ((double) customServings / this.servings)) + " " + units.get(i));
					builder.append("\n");
				}
			}
			
			
			return builder.toString();
		}
		
		return "";
		
	}
	
	public int getIngredientLength() {
		return ingredients.size();
	}
	
	public String getIngredient(int num) {
		return ingredients.get(num);
	}
	
	public double getQuantityDouble(int num) {
		return convertFraction(quantities.get(num));
	}
	
	public String getQuantity(int num) {
		return quantities.get(num);
	}
	
	public String getUnit(int num) {
		return units.get(num);
	}
	
	public void setQuantity(int location, String value) {
		quantities.set(location, value);
	}
	
	public void setUnit(int location, String value) {
		units.set(location, value);
	}
	
	// Utilities
	private double convertFraction(String fraction) {
		String[] parts;
		double sum = 0.0;
		
		parts = fraction.split(" ");
		
		for (int i = 0; i < parts.length; i++) {
			if (!parts[i].contains("/")) {
				sum += Double.parseDouble(parts[i]);
			}
			else {
				String[] fractionParts = parts[i].split("/");
				
				sum += (double) Integer.parseInt(fractionParts[0]) / Integer.parseInt(fractionParts[1]);
			}
		}
		
		return sum;
		
	}
	
	private double roundTwo(double num) {
		return Math.round(num * 100.0) / 100.0;
	}
}
