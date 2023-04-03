package edu.ncsu.csc.CoffeeMaker.models;

/**
 * RegisteredUser class represents a User who has a registered account in the
 * Coffee Maker system. A RegisteredUser extends the User class and stores a
 * username, password, first name, and last name, as well as an enumerated Role
 * defined in the User class.
 *
 * @author jlcorrei
 *
 */
public class RegisteredUser extends User {

    /** User name of the Registered User */
    private String username;

    /** Password of the Registered User */
    private String password;

    /** The user's first name */
    private String firstName;

    /** The user's last name */
    private String lastName;

    /**
     * Constructs a RegisteredUser using the given user name and password.
     *
     * @param username
     *            The user name of the registered user
     * @param password
     *            The password of the registered user
     */
    public RegisteredUser ( final String username, final String password ) {
        setUsername( username );
        setPassword( password );
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
     * Returns a String representation of the user
     *
     * @return A String representation of the user
     */
    @Override
    public String toString () {
        return "User [id=" + this.getId() + ", Role=" + this.getRole().toString() + ", User Name=" + this.getUsername()
                + ", First Name=" + this.getFirstName() + ", Last name=" + this.getLastName() + "]";
    }

}