package edu.ncsu.csc.CoffeeMaker.services;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

import edu.ncsu.csc.CoffeeMaker.models.RegisteredUser;
import edu.ncsu.csc.CoffeeMaker.repositories.StaffRepository;

/**
 * The UserService is used to handle CRUD operations on the staff User model. In
 * addition to all functionality from `Service`, we also have functionality for
 * retrieving a single customer or staff by username.
 *
 * @author Ben Abrams
 *
 */
@Component
@Transactional
public class StaffService extends Service<RegisteredUser, Long> {

    /**
     * CustomerRepository, to be autowired in by Spring and provide CRUD
     * operations on User model.
     */
    @Autowired
    private StaffRepository staffRepository;

    @Override
    protected JpaRepository<RegisteredUser, Long> getRepository () {
        return staffRepository;
    }

    /**
     * Find a customer with the provided name
     *
     * @param username
     *            Username of the user to find
     * @return found user, null if none
     */
    public RegisteredUser findByName ( final String username ) {
        return staffRepository.findByUsername( username );
    }

}
