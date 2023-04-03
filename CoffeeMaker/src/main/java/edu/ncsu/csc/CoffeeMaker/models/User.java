package edu.ncsu.csc.CoffeeMaker.models;

/**
 * Superclass for a User of the CoffeeMaker system.
 *
 * @author jlcorrei
 *
 */
public abstract class User extends DomainObject {

    /** User role */
    private Role role;

    // /** User id */
    // @Id
    // @GeneratedValue
    // private Long id;

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
     * Enum representing the Role of a given User in the CoffeeMaker system. The
     * possible roles in the CoffeeMaker system are below.
     *
     * @author jlcorrei
     *
     */
    public enum Role {

        /**
         * Customer role
         */
        CUSTOMER,

        /**
         * Employee role
         */
        EMPLOYEE,

        /**
         * Manager role
         */
        MANAGER,

        /**
         * Guest role
         */
        GUEST;
    }

    // /**
    // * Get the ID of the User
    // *
    // * @return the ID
    // */
    // @Override
    // public Serializable getId () {
    // return this.id;
    // }
    //
    // /**
    // * User by Hibernate to set the ID of the User.
    // *
    // * @param id
    // * The id of the given user
    // */
    // @SuppressWarnings ( "unused" )
    // private void setId ( final Long id ) {
    // this.id = id;
    // }

}
