package edu.ncsu.csc.CoffeeMaker.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import edu.ncsu.csc.CoffeeMaker.models.Order;
import edu.ncsu.csc.CoffeeMaker.services.OrderService;

/**
 * This is the controller that holds the REST endpoints that handle CRUD
 * operations for Orders.
 *
 * Spring will automatically convert all of the ResponseEntity and List results
 * to JSON
 *
 * @author Ben Abrams
 *
 */
@SuppressWarnings ( { "unchecked", "rawtypes" } )
@RestController
public class APIOrderController extends APIController {

    /**
     * OrderService object, to be autowired in by Spring to allow for
     * manipulating the Order model
     */
    @Autowired
    private OrderService service;

    /**
     * REST API method to provide POST access to the Order model. This is used
     * to create a new Order by automatically converting the JSON RequestBody
     * provided to an Order object. Invalid JSON will fail.
     *
     * @param order
     *            The valid order to be saved.
     * @return ResponseEntity indicating success if the Order could be saved to
     *         the database, or an error if it could not be
     */
    @PostMapping ( BASE_PATH + "/orders" )
    public ResponseEntity createOrder ( @RequestBody final Order order ) {
        if ( null != service.findByOrderNumber( order.getOrderNumber() ) ) {
            return new ResponseEntity( errorResponse( "Order number " + order.getOrderNumber() + " already exists" ),
                    HttpStatus.CONFLICT );
        }
        else {
            service.save( order );
            return new ResponseEntity( successResponse( order.getOrderNumber() + " successfully created" ),
                    HttpStatus.OK );
        }

    }

    /**
     * REST API method to provide GET access to a specific user, as indicated by
     * the path variable provided (the name of the desired user)
     *
     * @param username
     *            users username
     * @return response to the request
     */
    @GetMapping ( BASE_PATH + "/orders/{orderNumber}" )
    public ResponseEntity getUser ( @PathVariable ( "orderNumber" ) final int orderNumber ) {
        final Order order = service.findByOrderNumber( orderNumber );
        return null == order
                ? new ResponseEntity( errorResponse( "No Order found with order Number " + orderNumber ),
                        HttpStatus.NOT_FOUND )
                : new ResponseEntity( order, HttpStatus.OK );
    }

}
