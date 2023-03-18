package edu.ncsu.csc.CoffeeMaker.models;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;

/**
 * Inventory for the coffee maker. Inventory is tied to the database using
 * Hibernate libraries. See InventoryRepository and InventoryService for the
 * other two pieces used for database support.
 *
 * @author Kai Presler-Marshall
 */
@Entity
public class Inventory extends DomainObject {

    /** id for inventory entry */
    @Id
    @GeneratedValue
    private Long                   id;
    /** List of Ingredients in the Inventory */
    @OneToMany ( cascade = CascadeType.ALL, fetch = FetchType.EAGER )
    private final List<Ingredient> ingredients;

    /**
     * Empty constructor for Hibernate
     */
    public Inventory () {
        // Intentionally empty so that Hibernate can instantiate
        // Inventory object.
        ingredients = new ArrayList<Ingredient>();
    }

    /**
     * Returns the ID of the entry in the DB
     *
     * @return long
     */
    @Override
    public Long getId () {
        return id;
    }

    /**
     * Set the ID of the Inventory (Used by Hibernate)
     *
     * @param id
     *            the ID
     */
    public void setId ( final Long id ) {
        this.id = id;
    }

    /**
     * Returns true if there are enough ingredients to make the beverage.
     *
     * @param r
     *            recipe to check if there are enough ingredients
     * @return true if enough ingredients to make the beverage
     */
    public boolean enoughIngredients ( final Recipe r ) {
        final boolean isEnough = true;
        for ( final Ingredient ri : r.getIngredients() ) {
            boolean found = false;
            for ( final Ingredient ing : this.ingredients ) {
                // If the current ingredient in the recipe is found in the
                // inventory
                if ( ri.getIngredient().equals( ing.getIngredient() ) ) {
                    // say the ingredient was found
                    found = true;
                    // If there is not enough of the ingredient, say there isn't
                    // enough inventory to make the recipe
                    if ( ing.getAmount() < ri.getAmount() ) {
                        return false;
                    }
                }
            }
            if ( !found ) {
                return false;
                // throw new IllegalArgumentException(
                // String.format( "%s is a recipe ingredient not found in the
                // inventory.", ri.getIngredient() ) );
            }
        }

        return isEnough;
    }

    /**
     * Removes the ingredients used to make the specified recipe. Assumes that
     * the user has checked that there are enough ingredients to make
     *
     * @param r
     *            recipe to make
     * @return true if recipe is made.
     */
    public boolean useIngredients ( final Recipe r ) {
        if ( enoughIngredients( r ) ) {
            for ( final Ingredient ri : r.getIngredients() ) {
                for ( final Ingredient ing : this.ingredients ) {
                    // If the current ingredient in the recipe is found in the
                    // inventory
                    if ( ri.getIngredient().equals( ing.getIngredient() ) ) {
                        // Subtract the recipe amount used from the inventory
                        // supply
                        ing.setAmount( ing.getAmount() - ri.getAmount() );
                    }
                }
            }
            return true;
        }
        else {
            return false;
        }
    }

    /**
     * Adds an amount of an ingredient in the inventory to the inventory
     *
     * @param ingredient
     *            the name of the ingredient to update
     * @param amount
     *            the amount of the ingredient to add
     * @return true if successful, false if not
     */
    public boolean updateIngredient ( final String ingredient, final Integer amount ) {
        if ( amount == null || amount <= 0 ) {
            return false;
        }
        Ingredient val = null;
        for ( final Ingredient i : ingredients ) {
            // If the ingredient is found,
            if ( i.getIngredient().equalsIgnoreCase( ingredient ) ) {
                // Hold onto it and stop searching
                val = i;
                break;
            }
        }

        // If the ingredient isn't found in the inventory
        if ( val == null ) {
            return false;
        }
        val.setAmount( val.getAmount() + amount );

        return true;
    }

    /**
     * Adds a new ingredient to the inventory with an initial amount of 0
     *
     * @param ingredient
     *            the name of the ingredient to add
     * @return true if successful, false if not
     */
    public boolean addIngredient ( final String ingredient ) {
        if ( ingredient == null || ingredient.isBlank() ) {
            throw new IllegalArgumentException( "Each ingredient must have a name" );
        }
        // make sure the list does not already contain the ingredient
        for ( final Ingredient i : ingredients ) {
            if ( i.getIngredient().equalsIgnoreCase( ingredient ) ) {
                throw new IllegalArgumentException( String.format( "%s is already in the inventory.", ingredient ) );
            }
        }
        // add a new ingredient to the list
        ingredients.add( new Ingredient( ingredient, 0 ) );
        return true;
    }

    /**
     * Add a new ingredient to the inventory with a predetermined amount
     *
     * @param ingredient
     *            the name of the ingredient to add
     * @param amt
     *            the amonut of the ingredient
     * @return the ingredient was successfully added to the inventory (T/F)
     */
    public boolean addIngredient ( final String ingredient, final Integer amt ) {
        // If the amount passed in is negative, throw an exception
        if ( amt == null || amt < 0 ) {
            throw new IllegalArgumentException( "Ingredient quantity must be more than 0." );
        }
        if ( ingredient == null || ingredient.isBlank() ) {
            throw new IllegalArgumentException( "Each ingredient must have a name" );
        }
        // make sure the list does not already contain the ingredient
        for ( final Ingredient i : ingredients ) {
            if ( i.getIngredient().equalsIgnoreCase( ingredient ) ) {
                throw new IllegalArgumentException( String.format( "%s is already in the inventory.", ingredient ) );
            }
        }
        // add a new ingredient to the list
        ingredients.add( new Ingredient( ingredient, amt ) );
        return true;
    }

    /**
     * Return the list of ingredient in the inventory
     *
     * @return all of the ingredients currently in the inventory.
     */
    public List<Ingredient> getIngredients () {
        return this.ingredients;
    }

    /**
     * Returns a string describing the current contents of the inventory.
     *
     * @return String
     */
    @Override
    public String toString () {
        final StringBuffer buf = new StringBuffer();
        // add the list of the ingredients to the buffer
        for ( final Ingredient i : ingredients ) {
            buf.append( i.getIngredient() );
            buf.append( ": " );
            buf.append( i.getAmount() );
            buf.append( "\n" );
        }
        return buf.toString();
    }

}
