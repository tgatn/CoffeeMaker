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

import edu.ncsu.csc.CoffeeMaker.models.Ingredient;
import edu.ncsu.csc.CoffeeMaker.services.IngredientService;

/**
 * This is the controller that holds the REST endpoints that handle CRUD
 * operations for Ingredients.
 *
 * Spring will automatically convert all of the ResponseEntity and List results
 * to JSON
 *
 * @author Ben
 *
 */
@SuppressWarnings ( { "unchecked", "rawtypes" } )
@RestController
public class APIIngredientController extends APIController {
    // get, cReate, and delete ingredient

    /**
     * IngredientService object, to be autowired in by Spring to allow for
     * manipulating the Recipe model
     */
    @Autowired
    private IngredientService ingredientService;

    /**
     * REST API method to provide GET access to all ingredients in the system
     *
     * @return JSON representation of all ingredients
     */
    @GetMapping ( BASE_PATH + "/ingredients" )
    public List<Ingredient> getIngredients () {
        return ingredientService.findAll();
    }

    /**
     * REST API method to provide GET access to specific ingredients in the
     * system
     *
     * @param name
     *            is name of ingredient to get
     *
     * @return JSON representation of specific ingredients
     */
    @GetMapping ( BASE_PATH + "ingredients/{name}" )
    public ResponseEntity getIngredient ( @PathVariable final String name ) {

        final Ingredient ingr = ingredientService.findByName( name );

        if ( null == ingr ) {
            return new ResponseEntity( HttpStatus.NOT_FOUND );
        }

        return new ResponseEntity( ingr, HttpStatus.OK );
    }

    /**
     * REST API method to provide POST access to the Ingredient model. This is
     * used to create a new Ingredient by automatically converting the JSON
     * RequestBody provided to an Ingredient object. Invalid JSON will fail.
     *
     * @param ingredient
     *            The valid Ingredient to be saved
     * @return ResponseEntity indicating success if the Recipe could be saved,
     *         or an error if it could not be
     */
    @PostMapping ( BASE_PATH + "/ingredients" )
    public ResponseEntity createIngredient ( @RequestBody final Ingredient ingredient ) {

        final Ingredient db = ingredientService.findByName( ingredient.getIngredient() );

        // If the ingredient is already stored return a conflict and don't save
        if ( null != db ) {
            return new ResponseEntity( HttpStatus.CONFLICT );
        }

        // try to save the new ingredient
        try {
            ingredientService.save( ingredient );
            return new ResponseEntity( HttpStatus.OK );
        }
        catch ( final Exception e ) {
            return new ResponseEntity( HttpStatus.BAD_REQUEST );
        }
    }

    /**
     * REST API method to allow deleting an Ingredient from the CoffeeMaker's
     * Inventory, by making a DELETE request to the API endpoint and indicating
     * the ingredient to delete (as a path variable)
     *
     * @param name
     *            The name of the Ingredient to delete
     * @return Success if the ingredient could be deleted; an error if the
     *         ingredient does not exist
     */
    @DeleteMapping ( BASE_PATH + "/ingredients/{name}" )
    public ResponseEntity deleteIngredient ( @PathVariable final String name ) {
        final Ingredient ingredient = ingredientService.findByName( name );
        if ( null == ingredient ) {
            return new ResponseEntity( errorResponse( "No ingredient found for name " + name ), HttpStatus.NOT_FOUND );
        }
        ingredientService.delete( ingredient );

        return new ResponseEntity( successResponse( name + " was deleted successfully" ), HttpStatus.OK );
    }

}
