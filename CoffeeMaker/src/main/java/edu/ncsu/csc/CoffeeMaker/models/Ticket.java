package edu.ncsu.csc.CoffeeMaker.models;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

/**
 * Order class represents an order placed by a customer in the Coffee Maker
 * system. An Order extends the Domain Object class and stores a customer,
 * password, and recipes.
 *
 * @author ben
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

    @OneToOne
    private RegisteredUser customer;

    /** List of Recipes in the order */
    @OneToMany ( cascade = CascadeType.ALL, fetch = FetchType.EAGER )
    private List<Recipe>   recipes;

    /**
     * Creates a default order for the coffee maker.
     */
    public Ticket () {
        this.recipes = new ArrayList<Recipe>();
        this.totalCost = 0;
        this.orderNumber = 0;
        this.customerID = null;
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
    public Ticket ( final List<Recipe> recipes, final int orderNumber, final String customer ) {
        setRecipes( recipes );
        updateTotalCost();
        setOrderNumber( orderNumber );
        setCustomer( customer );
    }

    /**
     * Check if any recipes in the order do not have enough of an ingredient.
     *
     * @return false if any ingredients are 0, otherwise return true
     */
    public boolean checkOrder () {
        for ( final Recipe r : recipes ) {
            if ( !r.checkRecipe() ) {
                return false;
            }
        }

        return true;
    }

    /**
     * Gets the recipes.
     *
     * @return the recipes
     */
    public List<Recipe> getRecipes () {
        return recipes;
    }

    /**
     * Sets the recipes.
     *
     * @param recipes
     *            the recipes to set
     */
    private void setRecipes ( final List<Recipe> recipes ) {
        this.recipes = recipes;
    }

    /**
     * Adds the given recipe to the order
     *
     * @param recipe
     *            given recipe to be added
     */
    public void addRecipe ( final Recipe recipe ) {
        recipes.add( recipe );
    }

    /**
     * Removes the given recipe from the order
     *
     * @param recipe
     *            given recipe to be removed
     * @return true if the recipe was removed false otherwise
     */
    public boolean removeRecipe ( final Recipe recipe ) {
        return recipes.remove( recipe );
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
        for ( final Recipe recipe : recipes ) {
            this.totalCost += recipe.getPrice();
        }
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
