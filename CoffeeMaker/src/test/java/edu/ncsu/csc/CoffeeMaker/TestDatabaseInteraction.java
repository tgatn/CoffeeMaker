package edu.ncsu.csc.CoffeeMaker;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.List;

import javax.transaction.Transactional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import edu.ncsu.csc.CoffeeMaker.models.Ingredient;
import edu.ncsu.csc.CoffeeMaker.models.Recipe;
import edu.ncsu.csc.CoffeeMaker.services.RecipeService;

@ExtendWith ( SpringExtension.class )
@EnableAutoConfiguration
@SpringBootTest ( classes = TestConfig.class )
/**
 * Test the RecipeServices class. Tests adding new recipes to the database.
 * Tests retrieving recipe from database, by index and by name. Tests editing
 * recipes and re-saving to database.
 *
 * @author Brandon Jiang
 * @author Ben Abrams
 * @author Tung Tran
 */
public class TestDatabaseInteraction {

    @Autowired
    private RecipeService recipeService;

    /**
     * Sets up the tests.
     */
    @BeforeEach
    public void setup () {
        recipeService.deleteAll();
    }

    /**
     * Tests the RecipeService class Add one recipe to the database and check
     * that we can retrieve with the correct values by index
     */
    @Test
    @Transactional
    public void testAddRecipe () {
        /* We'll fill this out in a bit */

        // Check that the database is empty
        List<Recipe> db = recipeService.findAll();
        assertEquals( 0, db.size() );

        // Create Test Recipe
        final Recipe r = createRecipe( "Coffee", 6, 5, 4, 3, 2 );
        recipeService.save( r );

        // Retrieve the recipe book from the database.
        // Should only have 1 recipe in it
        db = recipeService.findAll();
        assertEquals( 1, db.size() );

        // Retrieve the recipe
        final Recipe dbRecipe = db.get( 0 );

        // Test all the values are correct in the recipe
        assertEquals( 6, dbRecipe.getPrice() );
        testIngredientValues( dbRecipe, 5, 4, 3, 2 );

    }

    /**
     * Tests the RecipeService class Add one recipe to the database and check
     * that we can retrieve with the correct values by name
     */
    @Test
    @Transactional
    public void testGetRecipeByName () {
        /* We'll fill this out in a bit */

        // Create Test Recipe
        // Add recipe functionality should be working for this test to pass
        final Recipe r = createRecipe( "Coffee", 6, 5, 4, 3, 2 );
        recipeService.save( r );

        // Retrieve the recipe
        final Recipe dbRecipe = recipeService.findByName( "Coffee" );

        // Test all the values are correct in the recipe
        assertNotNull( dbRecipe );
        assertEquals( 6, dbRecipe.getPrice() );
        testIngredientValues( dbRecipe, 5, 4, 3, 2 );

    }

    /**
     * Tests the RecipeService class Add one recipe to the database and check
     * that we can retrieve with the correct values by name
     */
    @Test
    @Transactional
    public void testEditRecipe () {
        /* We'll fill this out in a bit */

        // Create Test Recipe
        // Add recipe functionality should be working for this test to pass
        final Recipe r = createRecipe( "Coffee", 6, 5, 4, 3, 2 );
        recipeService.save( r );

        // Retrieve the recipe
        Recipe dbRecipe = recipeService.findByName( "Coffee" );

        // Test all the values are correct in the recipe
        assertNotNull( dbRecipe );
        assertEquals( 6, dbRecipe.getPrice() );
        testIngredientValues( dbRecipe, 5, 4, 3, 2 );

        // Modify all non-name values in the recipe and save
        dbRecipe.setPrice( 20 );
        dbRecipe.editIngredient( "coffee", 19 );
        dbRecipe.editIngredient( "milk", 18 );
        dbRecipe.editIngredient( "sugar", 17 );
        dbRecipe.editIngredient( "chocolate", 16 );
        recipeService.save( dbRecipe );

        // Test that the database still only has one recipe
        assertEquals( 1, recipeService.count() );
        dbRecipe = recipeService.findByName( "Coffee" );
        assertNotNull( dbRecipe );
        assertEquals( 20, dbRecipe.getPrice() );
        testIngredientValues( dbRecipe, 19, 18, 17, 16 );
    }

    /**
     * Tests the RecipeService class by add two recipe to the database, checking
     * that the size of the database is 2, deleting a recipe, then check that
     * the size of the database is 1
     */
    @Test
    @Transactional
    public void testDeleteRecipe () {
        /* We'll fill this out in a bit */

        // Check that the database is empty
        List<Recipe> db = recipeService.findAll();
        assertEquals( 0, db.size() );

        // Create Test Recipe 1
        final Recipe r = createRecipe( "Coffee", 6, 5, 4, 3, 2 );
        recipeService.save( r );

        // Retrieve the recipe book from the database.
        // Should only have 1 recipe in it
        db = recipeService.findAll();
        assertEquals( 1, db.size() );

        // Retrieve the recipe
        final Recipe dbRecipe = db.get( 0 );

        // Test all the values are correct in the recipe
        assertEquals( 6, dbRecipe.getPrice() );
        testIngredientValues( dbRecipe, 5, 4, 3, 2 );

        // Create Test Recipe 2
        final Recipe r2 = createRecipe( "Mini Coffee", 3, 2, 2, 1, 1 );
        recipeService.save( r2 );

        // Retrieve the recipe book from the database.
        // Should only have 1 recipe in it
        db = recipeService.findAll();
        assertEquals( 2, db.size() );

        // Retrieve the recipe
        final Recipe dbRecipe2 = db.get( 1 );

        // Test all the values are correct in the recipe
        assertEquals( 3, dbRecipe2.getPrice() );
        testIngredientValues( dbRecipe2, 2, 2, 1, 1 );

        // Remove the first recipe then check the values of the remaining recipe
        db.remove( 0 );
        assertEquals( 1, db.size() );
        final Recipe dbRecipe3 = db.get( 0 );
        assertEquals( 3, dbRecipe3.getPrice() );
        testIngredientValues( dbRecipe3, 2, 2, 1, 1 );

        // Remove last recipe
        db.remove( 0 );
        assertEquals( 0, db.size() );

    }

    /**
     * Tests the values of 4 ingredients
     *
     * @param r
     *            the recipe to test
     * @param coffee
     *            amt coffee
     * @param milk
     *            amt milk
     * @param sugar
     *            amt sugar
     * @param chocolate
     *            amt chocolate
     */
    private void testIngredientValues ( final Recipe r, final int coffee, final int milk, final int sugar,
            final int chocolate ) {
        final List<Ingredient> list = r.getIngredients();
        for ( final Ingredient i : list ) {
            final String name = i.getIngredient();
            switch ( name.toLowerCase() ) {
                case "coffee":
                    Assertions.assertEquals( i.getAmount(), coffee );
                    break;
                case "milk":
                    Assertions.assertEquals( i.getAmount(), milk );
                    break;
                case "sugar":
                    Assertions.assertEquals( i.getAmount(), sugar );
                    break;
                case "chocolate":
                    Assertions.assertEquals( i.getAmount(), chocolate );
                    break;
                default:
                    Assertions.fail( String.format( "Unknown ingredient in recipe: %s\n", name ) );
            }
        }
    }

    private Recipe createRecipe ( final String name, final Integer price, final Integer coffee, final Integer milk,
            final Integer sugar, final Integer chocolate ) {
        final Recipe recipe = new Recipe();
        recipe.setName( name );
        recipe.setPrice( price );
        recipe.addIngredient( "coffee" );
        recipe.editIngredient( "coffee", coffee );
        recipe.addIngredient( "milk" );
        recipe.editIngredient( "milk", milk );
        recipe.addIngredient( "sugar" );
        recipe.editIngredient( "sugar", sugar );
        recipe.addIngredient( "chocolate" );
        recipe.editIngredient( "chocolate", chocolate );

        return recipe;
    }
}
