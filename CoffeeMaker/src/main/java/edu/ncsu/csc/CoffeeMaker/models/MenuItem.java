package edu.ncsu.csc.CoffeeMaker.models;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;

/**
 * MenuItem is a wrapper class for Recipes. MenuItems allow a Ticket to keep
 * track of how many of a recipe an order has.
 *
 * Tickets / Orders are saved to the database, therefore MenuItems will also be
 * saved to the database.
 *
 * @author Brandon Jiang (bjiang9)
 *
 */
@Entity
public class MenuItem extends DomainObject {

    /** Menu Item ID */
    @Id
    @GeneratedValue
    private Long    id;

    /** The recipe to count */
    @OneToOne
    private Recipe  recipe;

    /** The count of the recipe */
    private Integer amount;

    /**
     * Create a new MenuItem with the given recipe and count
     *
     * @param r
     *            the recipe in the order
     * @param amt
     *            the count of the recipe
     */
    public MenuItem ( final Recipe r, final Integer amt ) {
        setRecipe( r );
        setAmount( amt );
    }

    /**
     * Create a new MenuItem with the given recipe and count 1.
     *
     * @param r
     *            the recipe in the order
     */
    public MenuItem ( final Recipe r ) {
        this( r, 1 );
    }

    /**
     * Get the ID of the MenuItem
     *
     * @return the ID
     */
    @Override
    public Long getId () {
        return this.id;
    }

    /**
     * Set the ID of the MenuItem (Used by Hibernate)
     *
     * @param id
     *            the ID
     */
    @SuppressWarnings ( "unused" )
    private void setId ( final Long id ) {
        this.id = id;
    }

    /**
     * Get the recipe
     *
     * @return the recipe
     */
    public Recipe getRecipe () {
        return recipe;
    }

    /**
     * Store the recipe this menu item represents.
     *
     * The recipe cannot be null. The recipe cannot be changed after creation.
     *
     * @param recipe
     *            the recipe on the Ticket
     */
    private void setRecipe ( final Recipe recipe ) {
        if ( recipe == null ) {
            throw new IllegalArgumentException( "Recipe cannot be null." );
            // TODO
        }
        this.recipe = recipe;
    }

    /**
     * Get the amount of the recipe
     *
     * @return the amount
     */
    public Integer getAmount () {
        return amount;
    }

    /**
     * Set the number of the recipe in the order.
     *
     * Each item in the order must have positive amounts
     *
     * @param amount
     */
    public void setAmount ( final Integer amount ) {
        if ( amount <= 0 ) {
            throw new IllegalArgumentException( "Amount must be positive." );
            // TODO
        }
        this.amount = amount;
    }

}
