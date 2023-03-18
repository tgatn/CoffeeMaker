package edu.ncsu.csc.CoffeeMaker.models;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.validation.constraints.Min;

/**
 * Recipe for the coffee maker. Recipe is tied to the database using Hibernate
 * libraries. See RecipeRepository and RecipeService for the other two pieces
 * used for database support.
 *
 * @author Kai Presler-Marshall
 */
@Entity
public class Recipe extends DomainObject {

    /** Recipe id */
    @Id
    @GeneratedValue
    private Long                   id;

    /** Recipe name */
    private String                 name;

    /** Recipe price */
    @Min ( 0 )
    private Integer                price;

    /** List of Ingredients in the recipe */
    @OneToMany ( cascade = CascadeType.ALL, fetch = FetchType.EAGER )
    private final List<Ingredient> ingredients;

    /**
     * Creates a default recipe for the coffee maker.
     */
    public Recipe () {
        this.name = "";
        this.ingredients = new ArrayList<Ingredient>();
    }

    /**
     * Check if any ingredients in the list are 0
     *
     * @return false if any ingredients are 0, otherwise return true
     */
    public boolean checkRecipe () {
        for ( final Ingredient i : ingredients ) {
            if ( i.getAmount() <= 0 ) {
                return false;
            }
        }

        return true;
    }

    /**
     * Attempts to add an ingredient to the recipe. Upon success true is
     * returned false otherwise.
     *
     * @param ingredient
     *            ingredient name to be added
     * @return true if successfully added false otherwise
     */
    public boolean addIngredient ( final String ingredient ) {
        // make sure the list does not already contain the ingredient
        for ( final Ingredient i : ingredients ) {
            if ( i.getIngredient().equalsIgnoreCase( ingredient ) ) {
                return false;
            }
        }
        // add a new ingredient to the list
        ingredients.add( new Ingredient( ingredient, 1 ) );
        return true;
    }

    /**
     * Removes the given ingredient
     *
     * @param ingredient
     *            the ingredient to remove
     * @throws IllegalStateException
     *             if it is the last ingredient
     * @return true on success, false otherwise
     */
    public boolean removeIngredient ( final String ingredient ) {
        if ( ingredients.size() <= 1 ) {
            throw new IllegalStateException( "Recipe must have 1 Ingredient" );
        }
        for ( final Ingredient i : ingredients ) {
            if ( i.getIngredient().equalsIgnoreCase( ingredient ) ) {
                ingredients.remove( i );
                return true;
            }
        }
        // could not find the ingredient
        return false;
    }

    /**
     * Changes the amount of ingredient for the recipe
     *
     * @param ingredient
     *            the ingredient to update
     * @param amount
     *            new amount for recipe
     * @throws IllegalArgumentException
     *             if the amount is not positive integer
     * @return true if ingredient was successfully edited; false otherwise
     */
    public boolean editIngredient ( final String ingredient, final Integer amount ) {
        if ( amount <= 0 ) {
            throw new IllegalArgumentException( "Amount must be positive integer" );
        }
        for ( final Ingredient i : ingredients ) {
            if ( i.getIngredient().equalsIgnoreCase( ingredient ) ) {
                i.setAmount( amount );
                return true;
            }
        }

        // ingredient was not found
        return false;
    }

    /**
     * Returns the list of ingredients.
     *
     * @return the list of ingredients in the recipe
     */
    public List<Ingredient> getIngredients () {
        return ingredients;
    }

    /**
     * Get the ID of the Recipe
     *
     * @return the ID
     */
    @Override
    public Long getId () {
        return id;
    }

    /**
     * Set the ID of the Recipe (Used by Hibernate)
     *
     * @param id
     *            the ID
     */
    @SuppressWarnings ( "unused" )
    private void setId ( final Long id ) {
        this.id = id;
    }

    /**
     * Returns name of the recipe.
     *
     * @return Returns the name.
     */
    public String getName () {
        return name;
    }

    /**
     * Sets the recipe name.
     *
     * @param name
     *            The name to set.
     */
    public void setName ( final String name ) {
        this.name = name;
    }

    /**
     * Returns the price of the recipe.
     *
     * @return Returns the price.
     */
    public Integer getPrice () {
        return price;
    }

    /**
     * Sets the recipe price.
     *
     * @param price
     *            The price to set.
     */
    public void setPrice ( final Integer price ) {
        if ( price <= 0 ) {
            throw new IllegalArgumentException( "Price cannot be less than 1" );
        }
        this.price = price;
    }

    /**
     * Returns the name of the recipe followed by the ingredients.
     *
     * @return String representation of the recipe
     */
    @Override
    public String toString () {
        // create the string builder
        final StringBuilder s = new StringBuilder();
        s.append( getName() );
        s.append( "\n{ " );

        // append all of the ingredients
        for ( int i = 0; i < ingredients.size(); i++ ) {
            s.append( ingredients.get( i ).getIngredient() );
            if ( i + 1 < ingredients.size() ) {
                s.append( ", " );
            }
        }
        s.append( " }" );

        return s.toString();
    }

    @Override
    public int hashCode () {
        final int prime = 31;
        Integer result = 1;
        result = prime * result + ( ( name == null ) ? 0 : name.hashCode() );
        return result;
    }

    @Override
    public boolean equals ( final Object obj ) {
        if ( this == obj ) {
            return true;
        }
        if ( obj == null ) {
            return false;
        }
        if ( getClass() != obj.getClass() ) {
            return false;
        }
        final Recipe other = (Recipe) obj;
        if ( name == null ) {
            if ( other.name != null ) {
                return false;
            }
        }
        else if ( !name.equals( other.name ) ) {
            return false;
        }
        return true;
    }

}
