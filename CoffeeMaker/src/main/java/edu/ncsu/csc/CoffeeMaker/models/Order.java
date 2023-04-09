package edu.ncsu.csc.CoffeeMaker.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;

/**
 * Order class represents an order placed by a customer in the Coffee Maker
 * system. An Order extends the Domain Object class and stores a customer,
 * password, and recipes.
 *
 * @author ben
 *
 */
public class Order extends DomainObject {

    /** Recipe id */
    @Id
    @GeneratedValue
    private Long         id;

    /** List of Recipes in the order */
    @OneToMany ( cascade = CascadeType.ALL, fetch = FetchType.EAGER )
    private List<Recipe> recipes;

    /** Cost of the order */
    private int          totalCost;

    /** Unique number that separates it from all other orders */
    private int          orderNumber;

    /** Customer who placed the order */
    private User         customer;

    /**
     * Creates a default order for the coffee maker.
     */
    public Order () {
        this.recipes = new ArrayList<Recipe>();
        this.totalCost = 0;
        this.orderNumber = 0;
        this.customer = null;
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
    public Order ( final List<Recipe> recipes, final int orderNumber, final User customer ) {
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
    public int getTotalCost () {
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
    public User getCustomer () {
        return customer;
    }

    /**
     * Sets the customer.
     *
     * @param customer
     *            the customer to set
     */
    private void setCustomer ( final User customer ) {
        this.customer = customer;
    }

    @Override
    public Serializable getId () {
        return id;
    }

}
