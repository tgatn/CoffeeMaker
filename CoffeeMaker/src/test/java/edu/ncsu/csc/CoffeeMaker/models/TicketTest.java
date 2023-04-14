package edu.ncsu.csc.CoffeeMaker.models;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import edu.ncsu.csc.CoffeeMaker.TestConfig;
import edu.ncsu.csc.CoffeeMaker.models.User.Role;
import edu.ncsu.csc.CoffeeMaker.services.TicketService;

@ExtendWith ( SpringExtension.class )
@EnableAutoConfiguration
@SpringBootTest ( classes = TestConfig.class )
public class TicketTest {

    @Autowired
    private TicketService service;

    @BeforeEach
    public void setup () {
        service.deleteAll();
    }

    @Test
    @Transactional
    public void defaultConstructor () {
        final Ticket t1 = new Ticket();
        assertEquals( 0, t1.getTotalCost(), 2 );
        assertEquals( 0, t1.getOrderNumber() );

    }

    @Test
    @Transactional
    public void testTicket () {
        // Make first recipe
        final Recipe r1 = createRecipe( "Small Coffee", 50, 50, 50, 50, 50 );

        // Make second recipe
        final Recipe r2 = createRecipe( "Medium Coffee", 50, 50, 50, 50, 50 );

        // create a recipe list and add recipes
        final ArrayList<Recipe> recipeList = new ArrayList<Recipe>();
        recipeList.add( r1 );
        recipeList.add( r2 );

        // create a customer
        final RegisteredUser u1 = new RegisteredUser( "John", "Doe", "jdoe", "pass1", Role.CUSTOMER );

        // create a ticket
        final Ticket t1 = new Ticket( recipeList, 1, u1.getUsername(), false );
        assertFalse( t1.isComplete() );
        assertEquals( recipeList, t1.getRecipes() );
        assertTrue( t1.checkOrder() );
        assertEquals( 2, t1.getRecipes().size() );

        // Make third recipe and adding
        final Recipe r3 = createRecipe( "Large Coffee", 50, 50, 50, 50, 50 );
        t1.addRecipe( r3 );
        assertEquals( 3, t1.getRecipes().size() );
        assertEquals( "jdoe", t1.getCustomer() );

        // removing recipe
        t1.removeRecipe( r1 );
        assertEquals( 2, t1.getRecipes().size() );

        // adding duplicate recipe
        // final Recipe r4 = createRecipe( "Large Coffee", 50, 50, 50, 50, 50 );
        t1.addRecipe( r3 );
        assertEquals( 3, t1.getRecipes().size() );

        assertEquals( 0, service.count() );
        service.save( t1 );
        assertEquals( 1, service.count() );
    };

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
