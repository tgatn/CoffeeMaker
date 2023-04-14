package edu.ncsu.csc.CoffeeMaker.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import edu.ncsu.csc.CoffeeMaker.models.Ingredient;
import edu.ncsu.csc.CoffeeMaker.models.Inventory;
import edu.ncsu.csc.CoffeeMaker.services.InventoryService;

/**
 * This is the controller that holds the REST endpoints that handle add and
 * update operations for the Inventory.
 *
 * Spring will automatically convert all of the ResponseEntity and List results
 * to JSON
 *
 * @author Kai Presler-Marshall
 * @author Michelle Lemons
 *
 */
@SuppressWarnings ( { "unchecked", "rawtypes" } )
@RestController
public class APIInventoryController extends APIController {

    /**
     * InventoryService object, to be autowired in by Spring to allow for
     * manipulating the Inventory model
     */
    @Autowired
    private InventoryService service;

    /**
     * REST API endpoint to provide GET access to the CoffeeMaker's singleton
     * Inventory. This will convert the Inventory to JSON.
     *
     * @return response to the request
     */
    @GetMapping ( BASE_PATH + "/inventory" )
    public ResponseEntity getInventory () {
        final Inventory inventory = service.getInventory();
        return new ResponseEntity( inventory, HttpStatus.OK );
    }

    /**
     * REST API endpoint to provide update access to CoffeeMaker's singleton
     * Inventory. This will update the Inventory of the CoffeeMaker by adding
     * amounts from the Inventory provided to the CoffeeMaker's stored inventory
     *
     * @param inventory
     *            amounts to add to inventory
     * @return response to the request
     */
    @PutMapping ( BASE_PATH + "/inventory" )
    public ResponseEntity updateInventory ( @RequestBody final Inventory inventory ) {
        final Inventory inventoryCurrent = service.getInventory();

        // For each ingredient in the inventory, add it to the current inventory
        for ( final Ingredient i : inventory.getIngredients() ) {
            inventoryCurrent.addIngredient( i.getIngredient(), i.getAmount() );
        }
        service.save( inventoryCurrent );
        return new ResponseEntity( inventoryCurrent, HttpStatus.OK );
    }

    /**
     * REST API endpoint to provide update access to CoffeeMaker's singleton
     * Inventory. This will update the Inventory of the CoffeeMaker by adding
     * the given ingredient
     *
     * @param ingredient
     *            ingredient to be added
     * @return response to the request
     */
    @PutMapping ( BASE_PATH + "/inventory/ingredient" )
    public ResponseEntity updateInventory ( @RequestBody final Ingredient ingredient ) {
        final Inventory inventoryCurrent = service.getInventory();

        if ( inventoryCurrent.addIngredient( ingredient.getIngredient(), ingredient.getAmount() ) ) {
            service.save( inventoryCurrent );
        }
        return new ResponseEntity( inventoryCurrent, HttpStatus.OK );
    }

    /**
     * REST API endpoint to provide update access to CoffeeMaker's singleton
     * Inventory. This will update the Inventory of the CoffeeMaker by adding
     * the amount of the ingredient
     *
     * @param name
     *            ingredient to be added
     * @param amount
     *            amount of ingredient to be added
     * @return response to the request
     */
    @PutMapping ( BASE_PATH + "/inventory/ingredient/{name}" )
    public ResponseEntity updateInventoryIngredient ( @PathVariable ( "name" ) final String name,
            @RequestBody final Integer amount ) {
        final Inventory inventoryCurrent = service.getInventory();

        if ( inventoryCurrent.updateIngredient( name, amount ) ) {
            service.save( inventoryCurrent );
            return new ResponseEntity( inventoryCurrent, HttpStatus.OK );
        }

        return new ResponseEntity( errorResponse( "No ingredient found for name " + name ), HttpStatus.NOT_FOUND );
    }
}
