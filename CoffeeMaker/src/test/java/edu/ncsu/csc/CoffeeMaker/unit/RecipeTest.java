package edu.ncsu.csc.CoffeeMaker.unit;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import edu.ncsu.csc.CoffeeMaker.TestConfig;
import edu.ncsu.csc.CoffeeMaker.models.Ingredient;
import edu.ncsu.csc.CoffeeMaker.models.Inventory;
import edu.ncsu.csc.CoffeeMaker.models.Recipe;
import edu.ncsu.csc.CoffeeMaker.services.RecipeService;

@ExtendWith ( SpringExtension.class )
@EnableAutoConfiguration
@SpringBootTest ( classes = TestConfig.class )
public class RecipeTest {

    @Autowired
    private RecipeService service;

    @BeforeEach
    public void setup () {
        service.deleteAll();
    }

    @Test
    @Transactional
    public void testAddRecipe () {

        final Recipe r1 = new Recipe();
        r1.setName( "Black Coffee" );
        r1.setPrice( 1 );
        r1.addIngredient( "Coffee" );
        r1.editIngredient( "Coffee", 1 );
        Assertions.assertFalse( r1.editIngredient( "random", 1 ) );
        service.save( r1 );

        final Recipe r2 = new Recipe();
        r2.setName( "Mocha" );
        r2.setPrice( 1 );
        r2.addIngredient( "Coffee" );
        r2.editIngredient( "Coffee", 1 );
        service.save( r2 );

        final List<Recipe> recipes = service.findAll();
        Assertions.assertEquals( 2, recipes.size(),
                "Creating two recipes should result in two recipes in the database" );

        Assertions.assertEquals( r1, recipes.get( 0 ), "The retrieved recipe should match the created one" );
    }

    @Test
    @Transactional
    public void testAddRecipe1 () {

        Assertions.assertEquals( 0, service.findAll().size(), "There should be no Recipes in the CoffeeMaker" );
        final String name = "Coffee";
        final Recipe r1 = new Recipe();
        r1.setName( name );
        r1.setPrice( 50 );
        r1.addIngredient( "Coffee" );
        Assertions.assertFalse( r1.addIngredient( "Coffee" ) );

        service.save( r1 );

        Assertions.assertEquals( 1, service.findAll().size(), "There should only one recipe in the CoffeeMaker" );
        Assertions.assertNotNull( service.findByName( name ) );

    }

    /* Test2 is done via the API for different validation */

    @Test
    @Transactional
    public void testAddRecipe3 () {
        Assertions.assertEquals( 0, service.findAll().size(), "There should be no Recipes in the CoffeeMaker" );
        final String name = "Coffee";
        final Recipe r1 = new Recipe();
        try {
            r1.setPrice( -50 );
            fail( "Price cannot be negative" );
        }
        catch ( final IllegalArgumentException e ) {
            // expected
        }

    }

    @Test
    @Transactional
    public void testAddRecipe4 () {
        Assertions.assertEquals( 0, service.findAll().size(), "There should be no Recipes in the CoffeeMaker" );
        final String name = "Coffee";

        try {
            final Recipe r1 = new Recipe();
            r1.setPrice( 50 );
            r1.addIngredient( "sugar" );
            r1.editIngredient( "sugar", -3 );

            fail( "A recipe was able to be created with a negative amount of ingredient" );
        }
        catch ( final IllegalArgumentException e ) {
            // expected
        }

    }

    @Test
    @Transactional
    public void testAddRecipe5 () {
        Assertions.assertEquals( 0, service.findAll().size(), "There should be no Recipes in the CoffeeMaker" );
        final String name = "Coffee";
        try {
            final Recipe r1 = createRecipe( name, 50, 3, -1, 1, 2 );
            fail( "ingredient cannot be negative" );
        }
        catch ( final IllegalArgumentException e ) {
            // expected
        }

    }

    @Test
    @Transactional
    public void testAddRecipe13 () {
        Assertions.assertEquals( 0, service.findAll().size(), "There should be no Recipes in the CoffeeMaker" );

        final Recipe r1 = createRecipe( "Coffee", 50, 3, 1, 1, 1 );
        service.save( r1 );
        final Recipe r2 = createRecipe( "Mocha", 50, 3, 1, 1, 2 );
        service.save( r2 );

        Assertions.assertEquals( 2, service.count(),
                "Creating two recipes should result in two recipes in the database" );

    }

    @Test
    @Transactional
    public void testAddRecipe14 () {
        Assertions.assertEquals( 0, service.findAll().size(), "There should be no Recipes in the CoffeeMaker" );

        final Recipe r1 = createRecipe( "Coffee", 50, 3, 1, 1, 1 );
        service.save( r1 );
        final Recipe r2 = createRecipe( "Mocha", 50, 3, 1, 1, 2 );
        service.save( r2 );
        final Recipe r3 = createRecipe( "Latte", 60, 3, 2, 2, 1 );
        service.save( r3 );

        Assertions.assertEquals( 3, service.count(),
                "Creating three recipes should result in three recipes in the database" );

    }

    @Test
    @Transactional
    public void testDeleteRecipe1 () {
        Assertions.assertEquals( 0, service.findAll().size(), "There should be no Recipes in the CoffeeMaker" );

        final Recipe r1 = createRecipe( "Coffee", 50, 3, 1, 1, 1 );
        service.save( r1 );

        Assertions.assertEquals( 1, service.count(), "There should be one recipe in the database" );

        service.delete( r1 );
        Assertions.assertEquals( 0, service.findAll().size(), "There should be no Recipes in the CoffeeMaker" );
    }

    @Test
    @Transactional
    public void testDeleteRecipe2 () {
        Assertions.assertEquals( 0, service.findAll().size(), "There should be no Recipes in the CoffeeMaker" );

        final Recipe r1 = createRecipe( "Coffee", 50, 3, 1, 1, 1 );
        service.save( r1 );
        final Recipe r2 = createRecipe( "Mocha", 50, 3, 1, 1, 2 );
        service.save( r2 );
        final Recipe r3 = createRecipe( "Latte", 60, 3, 2, 2, 1 );
        service.save( r3 );

        Assertions.assertEquals( 3, service.count(), "There should be three recipes in the database" );

        service.deleteAll();

        Assertions.assertEquals( 0, service.count(), "`service.deleteAll()` should remove everything" );

    }

    @Test
    @Transactional
    public void testEditRecipe1 () {
        Assertions.assertEquals( 0, service.findAll().size(), "There should be no Recipes in the CoffeeMaker" );

        final Recipe r1 = createRecipe( "Coffee", 50, 3, 1, 1, 1 );
        service.save( r1 );

        r1.setPrice( 70 );

        service.save( r1 );

        final Recipe retrieved = service.findByName( "Coffee" );

        Assertions.assertEquals( 70, (int) retrieved.getPrice() );
        for ( final Ingredient i : retrieved.getIngredients() ) {
            if ( i.getIngredient().equalsIgnoreCase( "coffee" ) ) {
                Assertions.assertEquals( 3, i.getAmount() );
            }
            else if ( i.getIngredient().equalsIgnoreCase( "milk" ) ) {
                Assertions.assertEquals( 1, i.getAmount() );
            }
            else if ( i.getIngredient().equalsIgnoreCase( "sugar" ) ) {
                Assertions.assertEquals( 1, i.getAmount() );
            }
            else if ( i.getIngredient().equalsIgnoreCase( "chocolate" ) ) {
                Assertions.assertEquals( 1, i.getAmount() );
            }
            else {
                fail( "Unknown ingredient" );
            }
        }

        Assertions.assertEquals( 1, service.count(), "Editing a recipe shouldn't duplicate it" );

    }

    @SuppressWarnings ( "unlikely-arg-type" )
    @Test
    @Transactional
    public void testRecipeEquals () {
        Assertions.assertEquals( 0, service.findAll().size(), "There should be no Recipes in the CoffeeMaker" );

        final Recipe r1 = createRecipe( "Coffee", 50, 50, 50, 50, 50 );
        final Recipe r2 = createRecipe( "Different", 5, 5, 5, 5, 5 );
        final Recipe r3 = createRecipe( null, 2, 2, 2, 2, 2 );
        final Recipe r4 = createRecipe( null, 1, 1, 1, 1, 1 );
        final Recipe r5 = createRecipe( "Coffee", 1, 1, 1, 1, 1 );

        Assertions.assertFalse( r1.equals( null ) );
        Assertions.assertFalse( r1.equals( new Inventory() ) );
        Assertions.assertFalse( r3.equals( r1 ) );
        Assertions.assertFalse( r1.equals( r2 ) );
        Assertions.assertTrue( r3.equals( r4 ) );
        Assertions.assertTrue( r1.equals( r5 ) );
    }

    @Test
    @Transactional
    public void testRecipeToStringAndHash () {
        final Recipe r1 = createRecipe( "Coffee", 50, 50, 50, 50, 50 );
        final Recipe r2 = createRecipe( "Different", 5, 5, 5, 5, 5 );
        final Recipe r3 = createRecipe( null, 1, 1, 1, 1, 1 );

        // test toString
        Assertions.assertEquals( "Coffee\n{ coffee, milk, sugar, chocolate }", r1.toString() );
        Assertions.assertEquals( "Different\n{ coffee, milk, sugar, chocolate }", r2.toString() );

        // test Hash
        Assertions.assertEquals( 31 + 1 * "Coffee".hashCode(), r1.hashCode() );
        Assertions.assertEquals( 31 + 1 * "Different".hashCode(), r2.hashCode() );
        Assertions.assertEquals( 31, r3.hashCode() );
    }

    @Test
    @Transactional
    public void testRemoveIngredient () {
        final Recipe r1 = createRecipe( "Coffee", 50, 50, 50, 50, 50 );
        Assertions.assertEquals( "Coffee\n{ coffee, milk, sugar, chocolate }", r1.toString() );

        // milk is successfully removed
        Assertions.assertTrue( r1.removeIngredient( "MILK" ) );
        Assertions.assertEquals( "Coffee\n{ coffee, sugar, chocolate }", r1.toString() );

        // milk cannot be removed twice
        Assertions.assertFalse( r1.removeIngredient( "MILK" ) );
        Assertions.assertEquals( "Coffee\n{ coffee, sugar, chocolate }", r1.toString() );

        // try removing all ingredients
        Assertions.assertTrue( r1.removeIngredient( "Sugar" ) );
        Assertions.assertTrue( r1.removeIngredient( "cHoCoLate" ) );
        try {
            r1.removeIngredient( "coffee" );
            fail( "cannot remove last ingredients" );
        }
        catch ( final IllegalStateException e ) {
            // expected
        }
        Assertions.assertEquals( "Coffee\n{ coffee }", r1.toString() );
    }

    @Test
    @Transactional
    public void testCheckRecipe () {
        final Recipe r1 = createRecipe( "Coffee", 50, 50, 50, 50, 50 );

        // cannot be false because there is always at least one of every
        // ingredient in the list
        assertTrue( r1.checkRecipe() );
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
