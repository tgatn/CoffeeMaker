package edu.ncsu.csc.CoffeeMaker.services;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

import edu.ncsu.csc.CoffeeMaker.models.RegisteredUser;
import edu.ncsu.csc.CoffeeMaker.repositories.CustomerRepository;

/**
 * The UserService is used to handle CRUD operations on the customer User model.
 * In addition to all functionality from `Service`, we also have functionality
 * for retrieving a single customer or staff by username.
 *
 * @author Ben Abrams
 *
 */
@Component
@Transactional
public class CustomerService extends Service<RegisteredUser, Long> {

    /**
     * CustomerRepository, to be autowired in by Spring and provide CRUD
     * operations on User model.
     */
    @Autowired
    private CustomerRepository customerRepository;

    @Override
    protected JpaRepository<RegisteredUser, Long> getRepository () {
        return customerRepository;
    }

    /**
     * Find a customer with the provided name
     *
     * @param username
     *            Username of the user to find
     * @return found user, null if none
     */
    public RegisteredUser findByName ( final String username ) {
        return customerRepository.findByUsername( username );
    }

}
