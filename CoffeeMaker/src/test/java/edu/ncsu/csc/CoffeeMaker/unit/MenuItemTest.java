package edu.ncsu.csc.CoffeeMaker.unit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.List;

import javax.transaction.Transactional;

import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import edu.ncsu.csc.CoffeeMaker.TestConfig;
import edu.ncsu.csc.CoffeeMaker.models.MenuItem;
import edu.ncsu.csc.CoffeeMaker.models.Recipe;
import edu.ncsu.csc.CoffeeMaker.services.MenuItemService;

/**
 * Test that the MenuItem object behaves as expected
 *
 * @author Brandon Jiang (bjiang9)
 *
 */
@ExtendWith ( SpringExtension.class )
@EnableAutoConfiguration
@SpringBootTest ( classes = TestConfig.class )
public class MenuItemTest {

    @Autowired
    private MenuItemService service;

    @BeforeEach
    public void setup () {
        service.deleteAll();
    }

    @Test
    @Transactional
    public void testMenuItem () {
        // ==============================================================================================
        // Create MenuItems to populate MenuItems with
        // ==============================================================================================
        final Recipe r1 = new Recipe();
        r1.setName( "Coffee" );

        final Recipe r2 = new Recipe();
        r2.setName( "Tea" );

        final Recipe r3 = new Recipe();
        r3.setName( "Cookie" );

        // ==============================================================================================
        // Create Menu Item
        // ==============================================================================================
        final MenuItem m1 = new MenuItem( r1 ); // Should have amount 1

        assertEquals( "Coffee", m1.getRecipe().getName() );
        assertEquals( 1, m1.getAmount() );
        service.save( m1 );

        final MenuItem m2 = new MenuItem( r2, 4 ); // Should have amount 4
        assertEquals( "Tea", m2.getRecipe().getName() );
        assertEquals( 4, m2.getAmount() );
        service.save( m2 );

        // ==============================================================================================
        // Test MenuItems reconstruct correctly when retrieved from database
        // ==============================================================================================
        List<MenuItem> ticket = service.findAll();
        assertEquals( 2, ticket.size() );
        for ( final MenuItem m : ticket ) {
            final String recipe = m.getRecipe().getName();
            switch ( recipe ) {
                case "Coffee":
                    assertEquals( 1, m.getAmount() );
                    break;
                case "Tea":
                    assertEquals( 4, m.getAmount() );
                    break;
                default:
                    fail( "Unknow MenuItem found" );
            }
        }

        // ============================================================================================
        // Edit Menu Items
        // ============================================================================================
        m1.setAmount( 3 );
        service.save( m1 );

        m2.setAmount( 1 );
        service.save( m2 );

        ticket = service.findAll();
        assertEquals( 2, ticket.size() );
        for ( final MenuItem m : ticket ) {
            final String recipe = m.getRecipe().getName();
            switch ( recipe ) {
                case "Coffee":
                    assertEquals( 3, m.getAmount() );
                    break;
                case "Tea":
                    assertEquals( 1, m.getAmount() );
                    break;
                default:
                    fail( "Unknow MenuItem found" );
            }
        }

        // ============================================================================================
        // Test Invalid Values
        // ============================================================================================
        try {
            // Test null Recipe
            final MenuItem r = new MenuItem( null );
            fail( "Null Recipe should throw an exception" );
        }
        catch ( final IllegalArgumentException e ) {
            // Nothing should happen
        }

        try {
            // Try to construct a new MenuItem with negative amount
            final MenuItem m3 = new MenuItem( r3, -1 );
            fail( "MenuItems should have postive amounts" );
        }
        catch ( final IllegalArgumentException e ) {

        }

        try {
            // Try to construct a new MenuItem with 0 amount
            final MenuItem m3 = new MenuItem( r3, 0 );
            fail( "MenuItems should have postive amounts" );
        }
        catch ( final IllegalArgumentException e ) {

        }

        try {
            // Try to edit a new MenuItem with negative amount
            assertEquals( 3, m1.getAmount() );
            m1.setAmount( -1 );
            fail( "MenuItems should have postive amounts" );
        }
        catch ( final IllegalArgumentException e ) {
            // Amount should not change
            assertEquals( 3, m1.getAmount() );
        }

        try {
            // Try to edit a new MenuItem with 0 amount
            assertEquals( 3, m1.getAmount() );
            m1.setAmount( 0 );
            fail( "MenuItems should have postive amounts" );
        }
        catch ( final IllegalArgumentException e ) {
            // Amount should not change
            assertEquals( 3, m1.getAmount() );
        }
    }
}
