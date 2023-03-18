package edu.ncsu.csc.CoffeeMaker.models;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * Ingredient class represents an ingredient in the Coffee Maker project. An
 * ingredient extends the DomainObject class and stores an enumerated type id
 * and an amount.
 *
 * @author Ben
 * @author Brandon
 * @author Tung
 *
 */
@Entity
public class Ingredient extends DomainObject {

    /** id for the ingredient */
    @Id
    @GeneratedValue
    private Long    id;

    /** String that represents the type of ingredient */
    private String  name;

    /** amount of the ingredient */
    private Integer amount;

    /**
     * Creates a default ingredient for the coffee maker.
     */
    public Ingredient () {
        this.name = "";
        this.amount = 0;
    }

    /**
     * Constructor for Ingredient with given parameters.
     *
     * @param ingredient
     *            type of ingredient
     * @param amount
     *            the units of ingredient
     */
    public Ingredient ( final String ingredient, final Integer amount ) {
        setName( ingredient );
        setAmount( amount );
    }

    /**
     * gets the ingredient type
     *
     * @return the ingredient type
     */
    public String getIngredient () {
        return name;
    }

    /**
     * get the amount
     *
     * @return the amount
     */
    public Integer getAmount () {
        return amount;
    }

    /**
     * Sets the type to be the given
     *
     * @param ingredient
     *            type to be set
     */
    private void setName ( final String ingredient ) {
        this.name = ingredient;
    }

    /**
     * Sets the amount to be the given
     *
     * @param amount
     *            units to be assigned
     */
    public void setAmount ( final Integer amount ) {
        if ( amount < 0 ) {
            throw new IllegalArgumentException( "Amount cannot be negative" );
        }
        this.amount = amount;
    }

    /**
     * returns the id of the ingredient
     *
     * @return the id of the ingredient
     */
    @Override
    public Serializable getId () {
        return id;
    }

    /**
     * Set the ID of the Ingredient (Used by Hibernate)
     *
     * @param id
     *            the ID
     */
    @SuppressWarnings ( "unused" )
    private void setId ( final Long id ) {
        this.id = id;
    }

    /**
     * Returns a string representation of the object
     *
     * @return the string representation of the ingredient
     */
    @Override
    public String toString () {
        return "Ingredient [id=" + id + ", ingredient=" + name + ", amount=" + amount + "]";
    }

}
