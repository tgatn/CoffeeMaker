package edu.ncsu.csc.CoffeeMaker.models;

/**
 * Superclass for a User of the CoffeeMaker system.
 *
 * @author jlcorrei
 *
 */
public abstract class User extends DomainObject {

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

}
