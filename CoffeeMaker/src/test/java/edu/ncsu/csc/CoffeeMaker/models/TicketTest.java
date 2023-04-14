package edu.ncsu.csc.CoffeeMaker.models;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import edu.ncsu.csc.CoffeeMaker.TestConfig;
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
        assertEquals( 0, t1.getTotalCost() );
        assertEquals( 0, t1.getOrderNumber() );

    }

    // @Test
    // @Transactional
    // public void testTicket () {
    // // Make first recipe
    // final Recipe r1 = createRecipe( "Small Coffee", 50, 50, 50, 50, 50 );
    //
    // // Make second recipe
    // final Recipe r2 = createRecipe( "Medium Coffee", 50, 50, 50, 50, 50 );
    //
    // // create a recipe list and add recipes
    // final ArrayList<MenuItem> recipeList = new ArrayList<MenuItem>();
    // recipeList.add( new MenuItem( r1 ) );
    // recipeList.add( new MenuItem( r2 ) );
    //
    // // create a customer
    // final RegisteredUser u1 = new RegisteredUser( "John", "Doe", "jdoe",
    // "pass1", Role.CUSTOMER );
    //
    // // create a ticket
    // final Ticket t1 = new Ticket( recipeList, 1, u1.getUsername(), false );
    // assertFalse( t1.isComplete() );
    // assertEquals( recipeList, t1.getCart() );
    // assertTrue( t1.checkOrder() );
    // assertEquals( 2, t1.getCart().size() );
    //
    // // Make third recipe and adding
    // final Recipe r3 = createRecipe( "Large Coffee", 50, 50, 50, 50, 50 );
    // t1.addRecipe( r3 );
    // assertEquals( 3, t1.getCart().size() );
    // assertEquals( "jdoe", t1.getCustomer() );
    //
    // // removing recipe
    // t1.removeRecipe( r1 );
    // assertEquals( 2, t1.getCart().size() );
    //
    // // adding duplicate recipe
    // // final Recipe r4 = createRecipe( "Large Coffee", 50, 50, 50, 50, 50 );
    // t1.addRecipe( r3 );
    // assertEquals( 3, t1.getCart().size() );
    //
    // assertEquals( 0, service.count() );
    // service.save( t1 );
    // assertEquals( 1, service.count() );
    // };

    @Test
    @Transactional
    public void testTicket2 () {
        // ==============================================================================================
        // Create Recipes to populate the Ticket with
        // ==============================================================================================
        final Recipe r1 = createRecipe( "Coffee", 1, 1, 1, 1, 1 );
        final Recipe r2 = createRecipe( "Tea", 2, 2, 2, 2, 2 );
        final Recipe r3 = createRecipe( "Cookie", 3, 3, 3, 3, 3 );

        // ==============================================================================================
        // Create new Empty Ticket and add the first instance of items
        // ==============================================================================================
        final Ticket t1 = new Ticket();
        t1.addRecipe( r1 );
        t1.addRecipe( r2 );

        List<MenuItem> cart = t1.getCart();
        assertEquals( 2, cart.size() ); // Has 2 different menu items

        assertEquals( 3, t1.getTotalCost() );
        // All quantities should be 1
        for ( final MenuItem m : cart ) {
            final String recipe = m.getRecipe().getName();
            switch ( recipe ) {
                case "Coffee":
                    assertEquals( 1, m.getAmount() );
                    break;
                case "Tea":
                    assertEquals( 1, m.getAmount() );
                    break;
                default:
                    fail( "Unknow MenuItem found" );
            }
        }

        // ==============================================================================================
        // Test Increment Items
        // ==============================================================================================
        t1.addRecipe( r1 );
        t1.addRecipe( r1 );

        t1.addRecipe( r2 );
        cart = t1.getCart();

        // No new Items should've been added to the Ticket
        assertFalse( t1.isComplete() );
        assertEquals( 2, cart.size() );
        assertEquals( 7, t1.getTotalCost() );
        for ( final MenuItem m : cart ) {
            final String recipe = m.getRecipe().getName();
            switch ( recipe ) {
                case "Coffee":
                    assertEquals( 3, m.getAmount() );
                    break;
                case "Tea":
                    assertEquals( 2, m.getAmount() );
                    break;
                default:
                    fail( "Unknow MenuItem found" );
            }
        }

        // ==============================================================================================
        // Test Decrement Items
        // ==============================================================================================
        t1.removeRecipe( r1 );

        t1.removeRecipe( r2 );
        t1.removeRecipe( r2 );

        cart = t1.getCart();

        // No new Items should've been added to the Ticket
        assertFalse( t1.isComplete() );
        assertEquals( 2, cart.size() );
        assertEquals( 7, t1.getTotalCost() );
        for ( final MenuItem m : cart ) {
            final String recipe = m.getRecipe().getName();
            switch ( recipe ) {
                case "Coffee":
                    assertEquals( 3, m.getAmount() );
                    break;
                case "Tea":
                    assertEquals( 2, m.getAmount() );
                    break;
                default:
                    fail( "Unknow MenuItem found" );
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
