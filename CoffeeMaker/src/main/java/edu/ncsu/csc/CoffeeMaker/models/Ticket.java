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
 * Order class represents an order placed by a customer in the Coffee Maker
 * system. An Order extends the Domain Object class and stores a customer,
 * password, and recipes.
 *
 * @author Ben Abrams (bsabram2)
 * @author Brandon Jiang (bjiang9)
 *
 */
@Entity
public class Ticket extends DomainObject {

    /** Recipe id */
    @Id
    @GeneratedValue
    private Long           id;

    /** Cost of the order */
    private float          totalCost;

    /** Unique number that separates it from all other orders */
    private int            orderNumber;

    /** Customer who placed the order */
    private String         customerID;

    /** List of Recipes in the order */
    @OneToMany ( cascade = CascadeType.ALL, fetch = FetchType.EAGER )
    private List<MenuItem> cart;

    /** Boolean value to indicate if an order is complete */
    private boolean        isComplete;

    /**
     * Creates a default order for the coffee maker.
     */
    public Ticket () {
        this( new ArrayList<MenuItem>(), 0, null, false );
    }

    /**
     * Creates an order from the given parameters.
     *
     * @param recipes
     *            list of recipes
     * @param orderNumber
     *            unique order number
     * @param customer
     *            order's customer
     */
    public Ticket ( final List<MenuItem> recipes, final int orderNumber, final String customer,
            final boolean isComplete ) {
        setRecipes( recipes );
        updateTotalCost();
        setOrderNumber( orderNumber );
        setCustomer( customer );
        setTicketStatus( isComplete );
    }

    /**
     * Check if any recipes in the order do not have enough of an ingredient.
     *
     * @return false if any ingredients are 0, otherwise return true
     */
    public boolean checkOrder () {
        for ( final MenuItem m : cart ) {
            if ( !m.getRecipe().checkRecipe() ) {
                return false;
            }
        }

        return true;
    }

    /**
     * Returns the field isComplete
     *
     * @return true if ticket is complete; false otherwise
     */
    public boolean isComplete () {
        return this.isComplete;
    }

    /**
     * When all the items on the Ticket have been made, change the status of the
     * ticket to fulfilled
     */
    public void fulfill () {
        this.isComplete = true;
    }

    /**
     * Sets the ticket status
     *
     * @param status
     *            is true if ticket is complete and false if not
     */
    private void setTicketStatus ( final boolean status ) {
        this.isComplete = status;
    }

    /**
     * Gets the recipes.
     *
     * @return the recipes
     */
    public List<MenuItem> getRecipes () {
        return cart;
    }

    /**
     * Sets the recipes.
     *
     * @param recipes
     *            the recipes to set
     */
    private void setRecipes ( final List<MenuItem> recipes ) {
        this.cart = recipes;
    }

    /**
     * Adds the given recipe to the order
     *
     * @param recipe
     *            given recipe to be added
     */
    public void addRecipe ( final Recipe recipe ) {
        // Iterate over all items in the cart
        for ( final MenuItem m : cart ) {
            // If the recipe is already in the cart
            if ( recipe.getName().equals( m.getRecipe().getName() ) ) {
                // Increment the count and break
                m.setAmount( m.getAmount() + 1 );
                return;
            }
        }
        // If the recipe is not in the cart, add to cart with count 1
        cart.add( new MenuItem( recipe ) );
    }

    /**
     * Decrements the recipe in the cart if the recipe is in the cart
     *
     * If decrementing causes the count to be nonpositive, the entry for the
     * recipe is removed from the ticket
     *
     * @param recipe
     *            given recipe to be removed
     * @return true if the recipe was removed false otherwise
     */
    public boolean removeRecipe ( final Recipe recipe ) {
        // Iterate over the cart to find the recipe
        for ( int i = 0; i < cart.size(); i++ ) {
            // Name of recipe in cart
            final String current = cart.get( i ).getRecipe().getName();

            // If the recipe is found, decrement
            if ( current.equals( recipe.getName() ) ) {
                final MenuItem m = cart.get( i );
                try {
                    m.setAmount( m.getAmount() );
                }
                catch ( final IllegalArgumentException e ) {
                    cart.remove( i );
                }
                updateTotalCost();
                return true;
            }

        }

        return false;
    }

    /**
     * Gets total cost.
     *
     * @return the totalCost
     */
    public float getTotalCost () {
        return totalCost;
    }

    /**
     * Updates total cost.
     */
    private void updateTotalCost () {
        int total = 0;
        for ( final MenuItem m : cart ) {
            total += ( m.getAmount() * m.getRecipe().getPrice() );
        }
        totalCost = total;
    }

    /**
     * Orders unique number.
     *
     * @return the orderNumber
     */
    public int getOrderNumber () {
        return orderNumber;
    }

    /**
     * Sets order number.
     *
     * @param orderNumber
     *            the orderNumber to set
     */
    private void setOrderNumber ( final int orderNumber ) {
        this.orderNumber = orderNumber;
    }

    /**
     * Gets the customer.
     *
     * @return the customer
     */
    public String getCustomer () {
        return customerID;
    }

    /**
     * Sets the customer.
     *
     * @param customer
     *            the customer to set
     */
    private void setCustomer ( final String customer ) {
        this.customerID = customer;
    }

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

}
