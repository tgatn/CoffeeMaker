package edu.ncsu.csc.CoffeeMaker.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import edu.ncsu.csc.CoffeeMaker.models.RegisteredUser;

/**
 * StaffRepository is used to provide CRUD operations for the User model. Spring
 * will generate appropriate code with JPA.
 *
 * @author Ben Abrams
 *
 */
public interface StaffRepository extends JpaRepository<RegisteredUser, Long> {

    /**
     * Finds a staff User object with the provided username. Spring will
     * generate code to make this happen.
     *
     * @param username
     *            Username of the user
     * @return Found user, null if none.
     */
    RegisteredUser findByUsername ( String username );

}
