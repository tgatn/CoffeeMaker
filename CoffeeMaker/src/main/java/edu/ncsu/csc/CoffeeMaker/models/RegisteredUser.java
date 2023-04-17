package edu.ncsu.csc.CoffeeMaker.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;

/**
 * RegisteredUser class represents a User who has a registered account in the
 * Coffee Maker system. A RegisteredUser extends the User class and stores a
 * username, password, first name, and last name, as well as an enumerated Role
 * defined in the User class.
 *
 * @author jlcorrei
 *
 */
@Entity
public class RegisteredUser extends User {

    /** User id */
    @Id
    @GeneratedValue
    private Long         id;

    /** User name of the Registered User */
    private String       username;

    /** Password of the Registered User */
    private String       password;

    /** The user's first name */
    private String       firstName;

    /** The user's last name */
    private String       lastName;

    /** User role */
    private Role         role;

    /** list of current orders */
    @OneToMany ( cascade = CascadeType.ALL, fetch = FetchType.EAGER )
    private List<Ticket> orders;

    /**
     * Constructs a RegisteredUser using the given user name and password.
     *
     * @param username
     *            The user name of the registered user
     * @param password
     *            The password of the registered user
     */
    public RegisteredUser ( final String username, final String password ) {
        this( null, null, username, password, null );
    }

    /**
     * Creates a user with the given parameters.
     *
     *
     * @param first
     *            users first name
     * @param last
     *            users last name
     * @param user
     *            users username
     * @param pass
     *            users password
     * @param role
     *            users role
     */
    public RegisteredUser ( final String first, final String last, final String user, final String pass,
            final Role role ) {
        this.setFirstName( first );
        this.setLastName( last );
        this.setUsername( user );
        this.setPassword( pass );
        setRole( role );

        // Only customer will have their order history tracked
        if ( role == Role.CUSTOMER ) {
            this.orders = new ArrayList<Ticket>();
        }
        else {
            this.orders = null;
        }
    }

    /**
     * Creates a default user for the coffee maker.
     */
    public RegisteredUser () {
        this( "", "", "", "", null );
        this.orders = new ArrayList<Ticket>();
    }

    /**
     * Returns the user's user name.
     *
     * @return user name
     */
    public String getUsername () {
        return this.username;
    }

    /**
     * Returns the user's first name.
     *
     * @return the first name of the user
     */
    public String getFirstName () {
        return this.firstName;
    }

    /**
     * Returns the user's last name.
     *
     * @return the last name of the user
     */
    public String getLastName () {
        return this.lastName;
    }

    /**
     * Returns the role of the given User.
     *
     * @return User's role.
     */
    public Role getRole () {
        return this.role;
    }

    /**
     * Set the role of the user to the role given
     *
     * @param role
     *            the role of this user
     */
    public void setRole ( final Role role ) {
        this.role = role;
    }

    /**
     * Confirms the saved password of the given user with the input password.
     *
     * @param pass
     *            password to confirm with the user's saved password
     *
     * @return if the given password matches the saved password
     */
    public boolean checkPassword ( final String pass ) {
        return pass.equals( password );
    }

    /**
     * Sets the user's password to the given password.
     *
     * @param pass
     *            The password to save for the user
     */
    public void setPassword ( final String pass ) {
        this.password = pass;
    }

    /**
     * Sets the user's user name to the given user name.
     *
     * @param name
     *            The user name to save for the user
     */
    public void setUsername ( final String name ) {
        this.username = name;
    }

    /**
     * Sets the user's first name
     *
     * @param first
     *            The first name of the user
     */
    public void setFirstName ( final String first ) {
        this.firstName = first;
    }

    /**
     * Sets the user's last name
     *
     * @param last
     *            the last name of the user
     */
    public void setLastName ( final String last ) {
        this.lastName = last;
    }

    /**
     * Returns orders list
     *
     * @return the completeOrders
     */
    public List<Ticket> getOrders () {
        return orders;
    }

    /**
     * Adds the given order to current orders
     *
     * @param order
     *            order to add
     */
    public void addOrder ( final Ticket order ) {
        orders.add( order );
    }

    /**
     * Completes the given order
     *
     * @param order
     *            order to complete
     * @return true if found and completed false otherwise
     */
    public boolean completeOrder ( final Ticket order ) {
        if ( order == null ) {
            return false;
        }
        for ( int i = 0; i < orders.size(); i++ ) {
            if ( orders.get( i ).getId() == order.getId() ) {
                orders.get( i ).fulfill();
                return true;
            }
        }

        return false;
    }

    /**
     * Get a list of all orders waiting to be fulfilled.
     *
     * @return the list of orders waiting to be fulfilled.
     */
    public List<Ticket> getPendingOrders () {
        final List<Ticket> pending = new ArrayList<Ticket>();
        for ( final Ticket t : orders ) {
            if ( !t.isComplete() ) {
                pending.add( t );
            }
        }
        return pending;
    }

    /**
     * Returns a String representation of the user
     *
     * @return A String representation of the user
     */
    @Override
    public String toString () {
        return "User [id=" + this.getId() + ", Role=" + this.getRole().toString() + ", User Name=" + this.getUsername()
                + ", First Name=" + this.getFirstName() + ", Last name=" + this.getLastName() + "]";
    }

    @Override
    public Serializable getId () {
        return id;
    }

    /**
     * Set the ID of the User (Used by Hibernate)
     *
     * @param id
     *            the ID
     */
    @SuppressWarnings ( "unused" )
    private void setId ( final Long id ) {
        this.id = id;
    }

}
