package edu.ncsu.csc.CoffeeMaker.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import edu.ncsu.csc.CoffeeMaker.models.Recipe;

/**
 * This is the controller that holds the REST endpoints that handle CRUD
 * operations for Users.
 *
 * Spring will automatically convert all of the ResponseEntity and List results
 * to JSON
 *
 * @author Ben Abrams
 *
 */
@SuppressWarnings ( { "unchecked", "rawtypes" } )
@RestController
public class APIUserController extends APIController {

    /**
     * UserService object, to be autowired in by Spring to allow for
     * manipulating the User model
     */
    @Autowired
    private UserService service;

    /**
     * REST API method to provide GET access to all customers in the system
     *
     * @return JSON representation of all Customers
     */
    @GetMapping ( BASE_PATH + "/users/customers" )
    public List<Recipe> getCustomers () {
        return service.findAllCustomers();
    }

    /**
     * REST API method to provide GET access to all staff in the system
     *
     * @return JSON representation of all staff
     */
    @GetMapping ( BASE_PATH + "/users/staff" )
    public List<Recipe> getStaff () {
        return service.findAllStaff();
    }

    /**
     * REST API method to provide GET access to a specific Customer user, as
     * indicated by the path variable provided (the name of the desired user)
     *
     * @param username
     *            customers username
     * @return response to the request
     */
    @GetMapping ( BASE_PATH + "/users/customers/{username}" )
    public ResponseEntity getCustomer ( @PathVariable ( "username" ) final String username ) {
        final User user = service.findCustomerByName( username );
        return null == user
                ? new ResponseEntity( errorResponse( "No Customer found with username " + username ),
                        HttpStatus.NOT_FOUND )
                : new ResponseEntity( user, HttpStatus.OK );
    }

    /**
     * REST API method to provide GET access to a specific Staff user, as
     * indicated by the path variable provided (the name of the desired user)
     *
     * @param username
     *            staff username
     * @return response to the request
     */
    @GetMapping ( BASE_PATH + "/users/staff/{username}" )
    public ResponseEntity getStaff ( @PathVariable ( "username" ) final String username ) {
        final User user = service.findStaffByName( username );
        return null == user
                ? new ResponseEntity( errorResponse( "No Staff found with username " + username ),
                        HttpStatus.NOT_FOUND )
                : new ResponseEntity( user, HttpStatus.OK );
    }

    /**
     * REST API method to provide POST access to the User model. This is used to
     * create a new User by automatically converting the JSON RequestBody
     * provided to a User object. Invalid JSON will fail.
     *
     * @param user
     *            The valid user to be saved.
     * @return ResponseEntity indicating success if the User could be saved to
     *         the database, or an error if it could not be
     */
    @PostMapping ( BASE_PATH + "/users/customers" )
    public ResponseEntity createCustomer ( @RequestBody final User user ) {
        if ( null != service.findCustomerByName( user.getUsername() ) ) {
            return new ResponseEntity(
                    errorResponse( "Customer with the name " + user.getUsername() + " already exists" ),
                    HttpStatus.CONFLICT );
        }
        else {
            service.save( user );
            return new ResponseEntity( successResponse( user.getUsername() + " successfully created" ), HttpStatus.OK );
        }

    }

    /**
     * REST API method to provide POST access to the User model. This is used to
     * create a new User by automatically converting the JSON RequestBody
     * provided to a User object. Invalid JSON will fail.
     *
     * @param user
     *            The valid user to be saved.
     * @return ResponseEntity indicating success if the User could be saved to
     *         the database, or an error if it could not be
     */
    @PostMapping ( BASE_PATH + "/users/staff" )
    public ResponseEntity createStaff ( @RequestBody final User user ) {
        if ( null != service.findStaffByName( user.getUsername() ) ) {
            return new ResponseEntity( errorResponse( "Staff with the name " + user.getUsername() + " already exists" ),
                    HttpStatus.CONFLICT );
        }
        else {
            service.save( user );
            return new ResponseEntity( successResponse( user.getUsername() + " successfully created" ), HttpStatus.OK );
        }

    }

    /**
     * REST API method to allow deleting a User from the CoffeeMaker's system,
     * by making a DELETE request to the API endpoint and indicating the User to
     * delete (as a path variable)
     *
     * @param username
     *            The username of the User to delete
     * @return Success if the User could be deleted; an error if the User does
     *         not exist
     */
    @DeleteMapping ( BASE_PATH + "/users/customers/{username}" )
    public ResponseEntity deleteRecipe ( @PathVariable final String username ) {
        final User user = service.findCustomerByName( username );
        if ( null == user ) {
            return new ResponseEntity( errorResponse( "No Customer found for name " + username ),
                    HttpStatus.NOT_FOUND );
        }
        service.deleteCustomer( user );

        return new ResponseEntity( successResponse( username + " was deleted successfully" ), HttpStatus.OK );
    }

    /**
     * REST API method to allow deleting a User from the CoffeeMaker's system,
     * by making a DELETE request to the API endpoint and indicating the User to
     * delete (as a path variable)
     *
     * @param username
     *            The username of the User to delete
     * @return Success if the User could be deleted; an error if the User does
     *         not exist
     */
    @DeleteMapping ( BASE_PATH + "/users/staff/{username}" )
    public ResponseEntity deleteRecipe ( @PathVariable final String username ) {
        final User user = service.findStaffByName( username );
        if ( null == user ) {
            return new ResponseEntity( errorResponse( "No Staff found for name " + username ), HttpStatus.NOT_FOUND );
        }
        service.deleteStaff( user );

        return new ResponseEntity( successResponse( username + " was deleted successfully" ), HttpStatus.OK );
    }

}
