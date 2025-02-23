package org.pancakelab.model;

public class Ingredient implements Comparable<Ingredient> {
    private final String name;

    public Ingredient(String name) {
        if (!ApprovedIngredients.isApproved(name)) {
            throw new IllegalArgumentException("Invalid ingredient: " + name);
        }
        this.name = name.toLowerCase();
    }

    public String getName() {
        return name;
    }
    
    @Override
    public boolean equals(Object obj) {
    	if (obj == null || obj.getClass() != this.getClass()) {
    		return false;
    	}
    	Ingredient other = (Ingredient)obj;
    	return name.equals(other.getName());
    }
    
    @Override
    public int hashCode() {
    	return name.hashCode();
    }

	@Override
	public int compareTo(Ingredient o) {
		return name.compareTo(o.getName());
	}
}
