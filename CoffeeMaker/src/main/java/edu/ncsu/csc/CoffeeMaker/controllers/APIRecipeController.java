package edu.ncsu.csc.CoffeeMaker.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import edu.ncsu.csc.CoffeeMaker.models.Recipe;
import edu.ncsu.csc.CoffeeMaker.services.RecipeService;

/**
 * This is the controller that holds the REST endpoints that handle CRUD
 * operations for Recipes.
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
public class APIRecipeController extends APIController {

    /**
     * RecipeService object, to be autowired in by Spring to allow for
     * manipulating the Recipe model
     */
    @Autowired
    private RecipeService service;

    /**
     * REST API method to provide GET access to all recipes in the system
     *
     * @return JSON representation of all recipies
     */
    @GetMapping ( BASE_PATH + "/recipes" )
    public List<Recipe> getRecipes () {
        return service.findAll();
    }

    /**
     * REST API method to provide GET access to a specific recipe, as indicated
     * by the path variable provided (the name of the recipe desired)
     *
     * @param name
     *            recipe name
     * @return response to the request
     */
    @GetMapping ( BASE_PATH + "/recipes/{name}" )
    public ResponseEntity getRecipe ( @PathVariable ( "name" ) final String name ) {
        final Recipe recipe = service.findByName( name );
        return null == recipe
                ? new ResponseEntity( errorResponse( "No recipe found with name " + name ), HttpStatus.NOT_FOUND )
                : new ResponseEntity( recipe, HttpStatus.OK );
    }

    /**
     * REST API method to provide PUT access to the Recipe model. This is used
     * to edit a Recipes price.
     *
     * @param name
     *            the name of the recipe being edited
     * @param price
     *            the new price to save
     * @return ResponseEntity indicating success if the Recipe could be saved,
     *         or an error if it could not be
     */
    @PutMapping ( BASE_PATH + "/recipes/{name}" )
    public ResponseEntity editPrice ( @PathVariable final String name, @RequestBody final Integer price ) {
        final Recipe currentRecipe = service.findByName( name );
        if ( null == currentRecipe ) {
            return new ResponseEntity( errorResponse( "No recipe found for name " + name ), HttpStatus.NOT_FOUND );
        }

        try {
            currentRecipe.setPrice( price );
            service.save( currentRecipe );
            return new ResponseEntity( successResponse( "Successfully updated price of " + currentRecipe.getName() ),
                    HttpStatus.OK );
        }
        catch ( final Exception e ) {
            return new ResponseEntity( errorResponse( "Could not edit price of recipe " + name ), HttpStatus.CONFLICT );
        }
    }

    /**
     * REST API method to provide POST access to the Recipe model. This is used
     * to create a new Recipe by automatically converting the JSON RequestBody
     * provided to a Recipe object. Invalid JSON will fail.
     *
     * @param recipe
     *            The valid Recipe to be saved.
     * @return ResponseEntity indicating success if the Recipe could be saved to
     *         the inventory, or an error if it could not be
     */
    @PostMapping ( BASE_PATH + "/recipes" )
    public ResponseEntity createRecipe ( @RequestBody final Recipe recipe ) {
        if ( null != service.findByName( recipe.getName() ) ) {
            return new ResponseEntity( errorResponse( "Recipe with the name " + recipe.getName() + " already exists" ),
                    HttpStatus.CONFLICT );
        }
        if ( service.findAll().size() < 3 ) {
            service.save( recipe );
            return new ResponseEntity( successResponse( recipe.getName() + " successfully created" ), HttpStatus.OK );
        }
        else {
            return new ResponseEntity(
                    errorResponse( "Insufficient space in recipe book for recipe " + recipe.getName() ),
                    HttpStatus.INSUFFICIENT_STORAGE );
        }

    }

    /**
     * REST API method to provide PUT access to the Recipe model. This is used
     * to edit a Recipes ingredient.
     *
     * @param name
     *            The valid Recipe name to be edited.
     * @param ingredient
     *            the stored ingredient to edit
     * @param units
     *            the new units to store
     * @return ResponseEntity indicating success if the Recipe could be edited,
     *         or an error if it could not be
     */
    @PutMapping ( BASE_PATH + "/recipes/{name}/ingredients/{ingredient}" )
    public ResponseEntity editUnits ( @PathVariable final String name, @PathVariable final String ingredient,
            @RequestBody final Integer units ) {
        final Recipe currentRecipe = service.findByName( name );
        if ( null == currentRecipe ) {
            return new ResponseEntity( errorResponse( "No recipe found for name " + name ), HttpStatus.NOT_FOUND );
        }

        try {
            if ( currentRecipe.editIngredient( ingredient, units ) ) {
                service.save( currentRecipe );
                return new ResponseEntity( successResponse( name + " was edited successfully" ), HttpStatus.OK );
            }
            else {
                return new ResponseEntity( errorResponse( "Could not edit units of ingredient " + ingredient ),
                        HttpStatus.CONFLICT );
            }
        }
        catch ( final Exception e ) {
            return new ResponseEntity( errorResponse( "Could not edit units of ingredient " + ingredient ),
                    HttpStatus.CONFLICT );
        }
    }

    /**
     * REST API method to provide POST access to the Recipe model. This is used
     * to add a Recipe ingredient.
     *
     * @param name
     *            The valid Recipe name to be added.
     * @param ingredient
     *            the new ingredient to add
     * @return ResponseEntity indicating success if the Recipe could be edited,
     *         or an error if it could not be
     */
    @PostMapping ( BASE_PATH + "/recipes/{name}/ingredients" )
    public ResponseEntity addIngredient ( @PathVariable final String name, @RequestBody final String ingredient ) {
        final Recipe currentRecipe = service.findByName( name );
        if ( null == currentRecipe ) {
            return new ResponseEntity( errorResponse( "No recipe found for name " + name ), HttpStatus.NOT_FOUND );
        }

        try {
            if ( currentRecipe.addIngredient( ingredient ) ) {
                service.save( currentRecipe );
                return new ResponseEntity( successResponse( name + " was edited successfully" ), HttpStatus.OK );
            }
            else {
                return new ResponseEntity( errorResponse( "Could not add ingredient " + ingredient ),
                        HttpStatus.CONFLICT );
            }
        }
        catch ( final Exception e ) {
            return new ResponseEntity( errorResponse( "Could not add ingredient " + ingredient ), HttpStatus.CONFLICT );
        }
    }

    /**
     * REST API method to provide DELETE access to the Recipe model. This is
     * used to delete a Recipe ingredient.
     *
     * @param name
     *            The valid Recipe name to be deleted.
     * @param ingredient
     *            the new ingredient to delete
     * @return ResponseEntity indicating success if the Recipe could be edited,
     *         or an error if it could not be
     */
    @DeleteMapping ( BASE_PATH + "/recipes/{name}/ingredients/{ingredient}" )
    public ResponseEntity deleteIngredient ( @PathVariable final String name, @PathVariable final String ingredient ) {
        final Recipe currentRecipe = service.findByName( name );
        if ( null == currentRecipe ) {
            return new ResponseEntity( errorResponse( "No recipe found for name " + name ), HttpStatus.NOT_FOUND );
        }

        try {
            if ( currentRecipe.removeIngredient( ingredient ) ) {
                service.save( currentRecipe );
                return new ResponseEntity( successResponse( name + " was edited successfully" ), HttpStatus.OK );
            }
            else {
                return new ResponseEntity( errorResponse( "Could not remove ingredient " + ingredient ),
                        HttpStatus.CONFLICT );
            }
        }
        catch ( final Exception e ) {
            return new ResponseEntity( errorResponse( "Could not remove ingredient " + ingredient ),
                    HttpStatus.CONFLICT );
        }
    }

    /**
     * REST API method to allow deleting a Recipe from the CoffeeMaker's
     * Inventory, by making a DELETE request to the API endpoint and indicating
     * the recipe to delete (as a path variable)
     *
     * @param name
     *            The name of the Recipe to delete
     * @return Success if the recipe could be deleted; an error if the recipe
     *         does not exist
     */
    @DeleteMapping ( BASE_PATH + "/recipes/{name}" )
    public ResponseEntity deleteRecipe ( @PathVariable final String name ) {
        final Recipe recipe = service.findByName( name );
        if ( null == recipe ) {
            return new ResponseEntity( errorResponse( "No recipe found for name " + name ), HttpStatus.NOT_FOUND );
        }
        service.delete( recipe );

        return new ResponseEntity( successResponse( name + " was deleted successfully" ), HttpStatus.OK );
    }
}
