package edu.ncsu.csc.CoffeeMaker.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import edu.ncsu.csc.CoffeeMaker.models.Inventory;
import edu.ncsu.csc.CoffeeMaker.models.MenuItem;
import edu.ncsu.csc.CoffeeMaker.models.RegisteredUser;
import edu.ncsu.csc.CoffeeMaker.models.Ticket;
import edu.ncsu.csc.CoffeeMaker.models.User;
import edu.ncsu.csc.CoffeeMaker.services.InventoryService;
import edu.ncsu.csc.CoffeeMaker.services.TicketService;
import edu.ncsu.csc.CoffeeMaker.services.UserService;

/**
 * This is the controller that holds the REST endpoints that handle CRUD
 * operations for Tickets.
 *
 * Spring will automatically convert all of the ResponseEntity and List results
 * to JSON
 *
 * @author Ben Abrams
 *
 */
@SuppressWarnings ( { "unchecked", "rawtypes" } )
@RestController
public class APITicketController extends APIController {

    /**
     * TicketService object, to be autowired in by Spring to allow for
     * manipulating the Ticket model
     */
    @Autowired
    private TicketService    service;

    /**
     * InventoryService object, to be autowired in by Spring to allow for
     * manipulating the Inventory model
     */
    @Autowired
    private InventoryService inventoryService;

    /**
     * UserService object, to be autowired in by Spring to allow for
     * manipulating the User model
     */
    @Autowired
    private UserService      userService;

    // /**
    // * REST API method to provide POST access to the Ticket model. This is
    // used
    // * to create a new Ticket by automatically converting the JSON RequestBody
    // * provided to a Ticket object. Invalid JSON will fail.
    // *
    // * @param order
    // * The valid order to be saved.
    // * @return ResponseEntity indicating success if the Order could be saved
    // to
    // * the database, or an error if it could not be
    // */
    // @PostMapping ( BASE_PATH + "/orders" )
    // public ResponseEntity createOrder ( @RequestBody final Ticket order ) {
    // if ( null != service.findByOrderNumber( order.getOrderNumber() ) ) {
    // return new ResponseEntity( errorResponse( "Order number " +
    // order.getOrderNumber() + " already exists" ),
    // HttpStatus.CONFLICT );
    // }
    // else {
    // service.save( order );
    // return new ResponseEntity( successResponse( order.getOrderNumber() + "
    // successfully created" ),
    // HttpStatus.OK );
    // }
    //
    // }

    /**
     * Gets the completed orders in the data base
     *
     * @return ResponseEntity indicating success with the list of completed
     *         orders
     */
    @GetMapping ( BASE_PATH + "/orders/complete" )
    public ResponseEntity getCompleteOrders () {
        final List<Ticket> list = service.findAll();
        for ( final Ticket t : list ) {
            if ( !t.isComplete() ) {
                list.remove( t );
            }
        }

        return new ResponseEntity( list, HttpStatus.OK );
    }

    /**
     * REST API method to provide POST access to the Ticket model. This is used
     * to create a new Ticket by automatically converting the JSON RequestBody
     * provided to a Ticket object. Invalid JSON will fail.
     *
     * @param order
     *            The valid order to be saved.
     * @param amtPaid
     *            the amount of money paid.
     * @return ResponseEntity indicating success if the Order could be saved to
     *         the database, or an error if it could not be
     */
    @PostMapping ( BASE_PATH + "/orders" )
    public ResponseEntity createTicket ( @RequestBody final Ticket order, @RequestBody final int amtPaid ) {
        // make sure the order isn't null
        if ( order == null || order.isComplete() ) {
            return new ResponseEntity( errorResponse( "Invalid Order" ), HttpStatus.CONFLICT );
        }
        // check if the order number is already stored
        if ( order.getId() != 0 && null != service.findById( order.getId() ) ) {
            return new ResponseEntity( errorResponse( "Order number " + order.getId() + " already exists" ),
                    HttpStatus.CONFLICT );
        }
        // check if the username is valid
        final RegisteredUser u = userService.findByName( order.getCustomer() );
        if ( u == null || u.getRole() != User.Role.CUSTOMER || u.getRole() != User.Role.GUEST ) {
            return new ResponseEntity( errorResponse( "Invalid user" ), HttpStatus.NOT_ACCEPTABLE );
        }
        // make sure the list of recipes is greater than 0
        if ( order.getCart() == null || order.getCart().size() <= 0 ) {
            return new ResponseEntity( errorResponse( "Not enough recipes" ), HttpStatus.NOT_ACCEPTABLE );
        }

        // make sure all of the given recipes are valid
        // meaning no ingredient has a value of 0
        if ( !order.checkOrder() ) {
            return new ResponseEntity( errorResponse( "Invalid recipes" ), HttpStatus.NOT_ACCEPTABLE );
        }

        // make sure inventory has enough ingredients and
        // deduct ingredients from inventory
        final Inventory inventory = inventoryService.getInventory();
        for ( final MenuItem r : order.getCart() ) {
            for ( int i = 0; i < r.getAmount(); i++ ) {
                if ( !inventory.enoughIngredients( r.getRecipe() ) ) {
                    return new ResponseEntity( errorResponse( "Not enough ingredients in inventory" ),
                            HttpStatus.CONFLICT );
                }
                inventory.useIngredients( r.getRecipe() );
            }
        }

        // make sure the cost is up to date
        order.updateTotalCost();

        // make sure the customer paid enough money
        int change = 0;
        if ( amtPaid < order.getTotalCost() ) {
            return new ResponseEntity( errorResponse( amtPaid + " is not enough money for the order" ),
                    HttpStatus.NOT_ACCEPTABLE );
        }
        else {
            change = amtPaid - order.getTotalCost();
        }

        // Add the valid order to the users list
        u.addOrder( order );
        userService.save( u );

        return new ResponseEntity( change, HttpStatus.OK );
    }

    /**
     * REST API method to provide GET access to a specific user, as indicated by
     * the path variable provided (the name of the desired user)
     *
     * @param orderNumber
     *            Tickets unique order number
     * @return response to the request
     */
    @GetMapping ( BASE_PATH + "/orders/{orderNumber}" )
    public ResponseEntity getTicket ( @PathVariable ( "orderNumber" ) final long orderNumber ) {
        final Ticket order = service.findById( orderNumber );
        return null == order
                ? new ResponseEntity( errorResponse( "No Order found with order Number " + orderNumber ),
                        HttpStatus.NOT_FOUND )
                : new ResponseEntity( order, HttpStatus.OK );
    }
}
