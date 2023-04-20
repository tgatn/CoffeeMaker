package edu.ncsu.csc.CoffeeMaker.controllers;

import java.util.Iterator;
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
import edu.ncsu.csc.CoffeeMaker.models.Recipe;
import edu.ncsu.csc.CoffeeMaker.models.RegisteredUser;
import edu.ncsu.csc.CoffeeMaker.models.Ticket;
import edu.ncsu.csc.CoffeeMaker.models.User;
import edu.ncsu.csc.CoffeeMaker.services.InventoryService;
import edu.ncsu.csc.CoffeeMaker.services.RecipeService;
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
     * RecipeService object, to be autowired in by Spring to allow for
     * manipulating the Recipe model
     */
    @Autowired
    private RecipeService    recipeService;

    /**
     * UserService object, to be autowired in by Spring to allow for
     * manipulating the User model
     */
    @Autowired
    private UserService      userService;

    /**
     * Gets the completed orders in the data base
     *
     * @return ResponseEntity indicating success with the list of completed
     *         orders
     */
    @GetMapping ( BASE_PATH + "/orders/complete" )
    public ResponseEntity getCompleteOrders () {
        final List<Ticket> list = service.findAll();
        final Iterator<Ticket> iterator = list.iterator();
        while ( iterator.hasNext() ) {
            final Ticket t = iterator.next();
            if ( !t.isComplete() ) {
                iterator.remove();
            }
        }
        return new ResponseEntity( list, HttpStatus.OK );
    }

    /**
     * Gets the pending orders in the data base
     *
     * @return ResponseEntity indicating success with the list of completed
     *         orders
     */
    @GetMapping ( BASE_PATH + "/orders/pending" )
    public ResponseEntity getPendingOrders () {
        final List<Ticket> list = service.findAll();
        final Iterator<Ticket> iterator = list.iterator();
        while ( iterator.hasNext() ) {
            final Ticket t = iterator.next();
            if ( t.isComplete() ) {
                iterator.remove();
            }
        }
        return new ResponseEntity( list, HttpStatus.OK );
    }

    /**
     * Gets the completed orders for a specific user in the data base
     *
     * @param username
     *            the username for which to retrieve completed orders
     * @return ResponseEntity indicating success with the list of completed
     *         orders for the user
     */
    @GetMapping ( BASE_PATH + "/orders/{username}/pending" )
    public ResponseEntity getPendingOrdersByUsername ( @PathVariable final String username ) {
        final List<Ticket> list = service.findAllByUsername( username );
        final Iterator<Ticket> iterator = list.iterator();
        while ( iterator.hasNext() ) {
            final Ticket t = iterator.next();
            if ( t.isComplete() ) {
                iterator.remove();
            }
        }
        return new ResponseEntity( list, HttpStatus.OK );
    }

    /**
     * Gets the completed orders for a specific user in the data base
     *
     * @param username
     *            the username for which to retrieve completed orders
     * @return ResponseEntity indicating success with the list of completed
     *         orders for the user
     */
    @GetMapping ( BASE_PATH + "/orders/{username}/complete" )
    public ResponseEntity getCompleteOrdersByUsername ( @PathVariable final String username ) {
        final List<Ticket> list = service.findAllByUsername( username );
        final Iterator<Ticket> iterator = list.iterator();
        while ( iterator.hasNext() ) {
            final Ticket t = iterator.next();
            if ( !t.isComplete() ) {
                iterator.remove();
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
     * @return ResponseEntity indicating success if the Order could be saved to
     *         the database, or an error if it could not be
     */
    @PostMapping ( BASE_PATH + "/orders" )
    public ResponseEntity createTicket ( @RequestBody final Ticket order ) {
        // make sure the order isn't null
        if ( order == null || order.isComplete() ) {
            return new ResponseEntity( errorResponse( "Invalid Order" ), HttpStatus.CONFLICT );
        }

        // check if the username is valid
        final RegisteredUser u = userService.findByName( order.getCustomer() );
        if ( u == null || u.getRole() == User.Role.MANAGER || u.getRole() == User.Role.EMPLOYEE ) {
            return new ResponseEntity( errorResponse( "Invalid user" ), HttpStatus.NOT_ACCEPTABLE );
        }
        // make sure the list of recipes is greater than 0
        if ( order.getCart() == null || order.getCart().size() <= 0 ) {
            return new ResponseEntity( errorResponse( "Not enough recipes" ), HttpStatus.NOT_ACCEPTABLE );
        }

        // ==========================================================================
        // Reconstruct the order with the Recipe Objects
        // ==========================================================================
        // Create a new Ticket with the same customer
        final Ticket t = new Ticket( order.getCustomer() );
        // Add each recipe to the new Ticket
        for ( final MenuItem r : order.getCart() ) {
            final Recipe add = recipeService.findByName( r.getRecipe().getName() );
            for ( int i = 0; i < r.getAmount(); i++ ) {
                t.addRecipe( add );
            }
        }

        // make sure all of the given recipes are valid
        // meaning no ingredient has a value of 0
        if ( !t.checkOrder() ) {
            return new ResponseEntity( errorResponse( "Invalid recipes" ), HttpStatus.NOT_ACCEPTABLE );
        }

        // make sure inventory has enough ingredients and
        // deduct ingredients from inventory
        final Inventory inventory = inventoryService.getInventory();
        for ( final MenuItem r : t.getCart() ) {
            for ( int i = 0; i < r.getAmount(); i++ ) {
                if ( !inventory.enoughIngredients( r.getRecipe() ) ) {
                    return new ResponseEntity( errorResponse( "Not enough ingredients in inventory" ),
                            HttpStatus.CONFLICT );
                }
                inventory.useIngredients( r.getRecipe() );
            }
        }

        // make sure the cost is up to date
        t.updateTotalCost();

        // Add the valid order to the users list
        u.addOrder( t );
        userService.save( u );
        return new ResponseEntity( HttpStatus.OK );
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

    /**
     * Gets the all orders in the data base that are not complete
     *
     * @return ResponseEntity indicating success with the list of orders
     */
    @GetMapping ( BASE_PATH + "/orders" )
    public ResponseEntity getAllTicket () {
        final List<Ticket> list = service.findAll();
        return new ResponseEntity( list, HttpStatus.OK );
    }

    /**
     * REST API method to mark a ticket as complete.
     *
     * @param id
     *            The id of the ticket to be marked as complete.
     * @return ResponseEntity indicating success if the ticket could be marked
     *         as complete, or an error if it could not be
     */
    @PostMapping ( BASE_PATH + "/orders/{id}/complete" )
    public ResponseEntity fulfillOrder ( @PathVariable final int id ) {
        // Find the ticket with the given id
        final Ticket ticket = service.findById( Long.valueOf( id ) );

        // If the ticket was not found, return an error response
        if ( ticket == null ) {
            return new ResponseEntity( errorResponse( "Ticket with id " + id + " not found." ), HttpStatus.NOT_FOUND );
        }

        // Mark the ticket as complete
        ticket.fulfill();
        service.save( ticket );

        return new ResponseEntity( successResponse( "Ticket with id " + id + " has been marked as complete." ),
                HttpStatus.OK );
    }
}
