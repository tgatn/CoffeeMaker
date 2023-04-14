package edu.ncsu.csc.CoffeeMaker.models;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;

@Entity
public class MenuItem extends DomainObject {

    @Id
    @GeneratedValue
    private Long    id;

    @OneToOne
    private Recipe  recipe;

    private Integer amount;

    public MenuItem ( final Recipe r, final Integer amt ) {
        setRecipe( r );
        setAmount( amt );
    }

    public MenuItem ( final Recipe r ) {
        this( r, 1 );
    }

    @Override
    public Long getId () {
        return this.id;
    }

    @SuppressWarnings ( "unused" )
    private void setId ( final Long id ) {
        this.id = id;
    }

    public Recipe getRecipe () {
        return recipe;
    }

    /**
     * Store the recipe this menu item represents.
     *
     * The recipe cannot be null.
     *
     * @param recipe
     *            the recipe on the Ticket
     */
    private void setRecipe ( final Recipe recipe ) {
        if ( recipe == null ) {
            return;
        }
        this.recipe = recipe;
    }

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
            return;
        }
        this.amount = amount;
    }

}
