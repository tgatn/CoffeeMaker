package edu.ncsu.csc.CoffeeMaker.unit;

import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
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
import edu.ncsu.csc.CoffeeMaker.models.MenuItem;
import edu.ncsu.csc.CoffeeMaker.models.Recipe;
import edu.ncsu.csc.CoffeeMaker.models.RegisteredUser;
import edu.ncsu.csc.CoffeeMaker.models.Ticket;
import edu.ncsu.csc.CoffeeMaker.models.User.Role;
import edu.ncsu.csc.CoffeeMaker.services.RecipeService;
import edu.ncsu.csc.CoffeeMaker.services.TicketService;
import edu.ncsu.csc.CoffeeMaker.services.UserService;

/**
 * Test the Registered User and User Service Classes
 *
 * @author Brandon Jiang (bjiang9)
 *
 */
@ExtendWith ( SpringExtension.class )
@EnableAutoConfiguration
@SpringBootTest ( classes = TestConfig.class )
public class RegisteredUserTest {

    @Autowired
    private UserService   uService;

    @Autowired
    private TicketService tService;

    @Autowired
    private RecipeService rService;

    @BeforeEach
    public void setup () {
        uService.deleteAll();
        rService.deleteAll();
        tService.deleteAll();
    }

    /**
     * Test that service class saves things correctly to the database.
     *
     * Test the RegisteredUser class
     */
    @Test
    @Transactional
    public void test () {
        // Check that no users are in the system
        List<RegisteredUser> list = uService.findAll();
        assertEquals( 0, list.size() );

        // Save 1 customer to the database
        final RegisteredUser cust1 = new RegisteredUser( "John", "Doe", "jdoe", "pass1", Role.CUSTOMER );
        uService.save( cust1 );

        // Update all lists
        list = uService.findAll();

        // There should be 1 user in the system
        assertEquals( 1, list.size() );

        // Save an employee to the system
        final RegisteredUser cust2 = new RegisteredUser( "Mary", "Jane", "mjane", "pass2", Role.EMPLOYEE );
        uService.save( cust2 );

        list = uService.findAll();
        assertEquals( 2, list.size() );

        // Test each user in the database
        // Switch case used in case of randomized order
        for ( final RegisteredUser u : list ) {
            final String user = u.getUsername();
            switch ( user ) {
                case "jdoe":
                    assertEquals( "John", u.getFirstName() );
                    assertEquals( "Doe", u.getLastName() );
                    assertEquals( Role.CUSTOMER, u.getRole() );
                    assertTrue( u.checkPassword( "pass1" ) );
                    break;
                case "mjane":
                    assertEquals( "Mary", u.getFirstName() );
                    assertEquals( "Jane", u.getLastName() );
                    assertEquals( Role.EMPLOYEE, u.getRole() );
                    assertTrue( u.checkPassword( "pass2" ) );
                    break;
            }
        }

        // ==============================================================
        // Check getbyname()
        // Modify some users and check for persistence
        // ==============================================================

        final RegisteredUser mod1 = uService.findByName( "jdoe" );
        mod1.setFirstName( "Jane" );
        uService.save( mod1 );
        list = uService.findAll();

        // Test each user in the database
        // Switch case used in case of randomized order
        for ( final RegisteredUser u : list ) {
            final String user = u.getUsername();
            switch ( user ) {
                case "jdoe":
                    assertEquals( "Jane", u.getFirstName() );
                    assertEquals( "Doe", u.getLastName() );
                    assertEquals( Role.CUSTOMER, u.getRole() );
                    assertTrue( u.checkPassword( "pass1" ) );
                    break;
                case "mjane":
                    assertEquals( "Mary", u.getFirstName() );
                    assertEquals( "Jane", u.getLastName() );
                    assertEquals( Role.EMPLOYEE, u.getRole() );
                    assertTrue( u.checkPassword( "pass2" ) );
                    break;
                default:
                    fail( "User not recognized" );
            }
        }

        // ==============================================================
        // Check delete()
        // ==============================================================
        final RegisteredUser del1 = uService.findByName( "jdoe" );
        uService.delete( del1 );
        list = uService.findAll();

        assertEquals( 1, list.size() );

        for ( final RegisteredUser u : list ) {
            final String user = u.getUsername();
            switch ( user ) {
                case "mjane":
                    assertEquals( "Mary", u.getFirstName() );
                    assertEquals( "Jane", u.getLastName() );
                    assertEquals( Role.EMPLOYEE, u.getRole() );
                    assertTrue( u.checkPassword( "pass2" ) );
                    break;
                default:
                    fail( "User not recognized" );
            }
        }

        // ==============================================================
        // Check toString()
        // ==============================================================
        final RegisteredUser rand1 = new RegisteredUser( "nameless", "pass" );
        assertNull( rand1.getFirstName() );
        assertNull( rand1.getLastName() );
        assertNull( rand1.getRole() );
        assertEquals( "nameless", rand1.getUsername() );
        assertTrue( rand1.checkPassword( "pass" ) );

        assertEquals( list.get( 0 ).toString(), cust2.toString() );

    }

    @Test
    @Transactional
    public void test2 () {
        // ========================================================================================
        // Populate Recipes
        // ========================================================================================
        final Recipe coffee = makeRecipe( "Coffee", 1, 1, 1 );
        rService.save( coffee );

        final Recipe tea = makeRecipe( "Tea", 2, 2, 2 );
        rService.save( tea );

        final Recipe cookie = makeRecipe( "Cookie", 3, 3, 3 );
        rService.save( cookie );

        // ========================================================================================
        // Create Registered Users
        // ========================================================================================

        final RegisteredUser cust = new RegisteredUser( "John", "Doe", "jdoe", "pass", Role.CUSTOMER );
        uService.save( cust );

        final RegisteredUser staff = new RegisteredUser( "Ming", "Hua", "mhua", "pass", Role.EMPLOYEE );
        uService.save( staff );

        final RegisteredUser manager = new RegisteredUser( "Mary", "Jane", "mjane", "pass", Role.MANAGER );
        uService.save( manager );

        final RegisteredUser guest = new RegisteredUser( "Mary", "Sue", "msue", "pass", Role.GUEST );
        uService.save( guest );
        // ========================================================================================
        // Only Customers have their order history tracked
        // ========================================================================================
        final List<RegisteredUser> roster = uService.findAll();
        List<Ticket> list = null;
        //
        // for ( final RegisteredUser u : roster ) {
        // final String name = u.getUsername();
        // list = u.getOrders();
        // switch ( name ) {
        // case "jdoe":
        // assertNotNull( list );
        // break;
        // case "mhua":
        // assertNull( list );
        // break;
        // case "mjane":
        // assertNull( list );
        // break;
        // case "msue":
        // assertNull( list );
        // break;
        // default:
        // fail( "Unknown User Found" );
        // }
        // }

        // ========================================================================================
        // Create Orders for Customer
        // ========================================================================================
        final Ticket t1 = new Ticket( cust.getUsername() );
        t1.addRecipe( coffee );
        t1.addRecipe( coffee );
        t1.addRecipe( cookie );
        t1.addRecipe( cookie );
        tService.save( t1 );

        cust.addOrder( t1 );
        uService.save( cust );

        // ========================================================================================
        // Retrieve Customer from the database to check for correct save
        // ========================================================================================
        RegisteredUser dbCust = uService.findByName( cust.getUsername() );
        list = dbCust.getOrders();
        assertEquals( 1, list.size() );

        Ticket dbT = list.get( 0 );
        assertFalse( dbT.isComplete() );
        assertEquals( 8, dbT.getTotalCost() );
        List<MenuItem> order = dbT.getCart();
        for ( final MenuItem m : order ) {
            final String recipe = m.getRecipe().getName();
            switch ( recipe ) {
                case "Coffee":
                    assertEquals( 2, m.getAmount() );
                    break;
                case "Cookie":
                    assertEquals( 2, m.getAmount() );
                    break;
                default:
                    fail( "Unknow MenuItem found" );
            }
        }

        // ========================================================================================
        // Order has not been completed
        // ========================================================================================
        list = dbCust.getPendingOrders();
        assertFalse( dbT.isComplete() );
        assertEquals( 8, dbT.getTotalCost() );
        order = dbT.getCart();
        for ( final MenuItem m : order ) {
            final String recipe = m.getRecipe().getName();
            switch ( recipe ) {
                case "Coffee":
                    assertEquals( 2, m.getAmount() );
                    break;
                case "Cookie":
                    assertEquals( 2, m.getAmount() );
                    break;
                default:
                    fail( "Unknown MenuItem found" );
            }
        }

        // ========================================================================================
        // Fulfill the first order
        // ========================================================================================
        dbT.fulfill();
        tService.save( dbT );

        // ========================================================================================
        // Add a second ticket to check for sorting
        // ========================================================================================
        final Ticket t2 = new Ticket( cust.getUsername() );
        t2.addRecipe( coffee );
        t2.addRecipe( tea );
        t2.addRecipe( cookie );

        tService.save( t2 );
        cust.addOrder( t2 );
        uService.save( cust );

        // ========================================================================================
        // Retrieve Customer from the database to check for correct save
        // ========================================================================================
        assertEquals( 4, uService.findAll().size() );

        dbCust = uService.findByName( cust.getUsername() );
        list = dbCust.getOrders();
        assertEquals( 2, list.size() );

        list = dbCust.getPendingOrders();
        assertEquals( 1, list.size() );

        dbT = list.get( 0 );
        order = dbT.getCart();
        assertEquals( 3, order.size() );
        for ( final MenuItem m : order ) {
            final String recipe = m.getRecipe().getName();
            switch ( recipe ) {
                case "Coffee":
                    assertEquals( 1, m.getAmount() );
                    break;
                case "Cookie":
                    assertEquals( 1, m.getAmount() );
                    break;
                case "Tea":
                    assertEquals( 1, m.getAmount() );
                    break;
                default:
                    fail( "Unknown MenuItem found" );
            }
        }
    }

    @Test
    @Transactional
    public void testManager () {
        final RegisteredUser manager = new RegisteredUser( "manager", "manager", "manager", "pass", Role.MANAGER );
        uService.save( manager );
        assertEquals( 1, uService.count() );
    }

    @Test
    @Transactional
    public void testFindTicketByUserName () {
        // ========================================================================================
        // Make Customers
        // ========================================================================================
        final RegisteredUser u1 = new RegisteredUser( "John", "Doe", "jdoe", "pass", Role.CUSTOMER );
        final RegisteredUser u2 = new RegisteredUser( "Mary", "Jane", "mjane", "pass", Role.CUSTOMER );
        final RegisteredUser u3 = new RegisteredUser( "Mary", "Sue", "msue", "pass", Role.CUSTOMER );
        uService.save( u1 );
        uService.save( u2 );
        uService.save( u3 );

        // ========================================================================================
        // Make Recipes
        // ========================================================================================
        final Recipe coffee = makeRecipe( "Coffee", 1, 1, 1 );
        rService.save( coffee );

        final Recipe tea = makeRecipe( "Tea", 2, 2, 2 );
        rService.save( tea );

        final Recipe cookie = makeRecipe( "Cookie", 3, 3, 3 );
        rService.save( cookie );

        // ========================================================================================
        // Make Tickets for (Customer 1)
        // ========================================================================================
        final Ticket t1A = new Ticket( u1.getUsername() );
        t1A.addRecipe( coffee );
        t1A.fulfill();
        tService.save( t1A );

        final Ticket t1B = new Ticket( u1.getUsername() );
        t1B.addRecipe( tea );
        tService.save( t1B );

        final Ticket t1C = new Ticket( u1.getUsername() );
        t1C.addRecipe( cookie );
        tService.save( t1C );

        // ========================================================================================
        // Make Tickets for (Customer 2)
        // ========================================================================================
        final Ticket t2A = new Ticket( u2.getUsername() );
        t2A.addRecipe( coffee );
        t2A.addRecipe( coffee );
        tService.save( t2A );

        final Ticket t2B = new Ticket( u2.getUsername() );
        t2B.addRecipe( tea );
        t2B.addRecipe( tea );
        t2B.fulfill();
        tService.save( t2B );

        final Ticket t2C = new Ticket( u2.getUsername() );
        t2C.addRecipe( cookie );
        t2C.addRecipe( cookie );
        tService.save( t2C );

        // ========================================================================================
        // Make Tickets for (Customer 3)
        // ========================================================================================
        final Ticket t3A = new Ticket( u3.getUsername() );
        t3A.addRecipe( coffee );
        t3A.addRecipe( coffee );
        t3A.addRecipe( coffee );
        tService.save( t3A );

        final Ticket t3B = new Ticket( u3.getUsername() );
        t3B.addRecipe( tea );
        t3B.addRecipe( tea );
        t3B.addRecipe( tea );
        tService.save( t3B );

        final Ticket t3C = new Ticket( u3.getUsername() );
        t3C.addRecipe( cookie );
        t3C.addRecipe( cookie );
        t3C.addRecipe( cookie );
        t3C.fulfill();
        tService.save( t3C );

        // ========================================================================================
        // Test findAllByUsername (Customer 1)
        // ========================================================================================
        List<Ticket> list = tService.findAllByUsername( "jdoe" );
        assertEquals( 3, list.size() );
        for ( final Ticket t : list ) {
            final List<MenuItem> cart = t.getCart();
            assertEquals( 1, cart.size() );
            final MenuItem item = cart.get( 0 );
            final String rName = item.getRecipe().getName();

            switch ( rName ) {
                case "Coffee":
                    assertEquals( 1, item.getAmount() );
                    assertTrue( t.isComplete() );
                    break;
                case "Tea":
                    assertEquals( 1, item.getAmount() );
                    break;
                case "Cookie":
                    assertEquals( 1, item.getAmount() );
                    break;
                default:
                    fail( "Unknown MenuItem found." );
            }
        }

        // ========================================================================================
        // Test findAllByUsername (Customer 2)
        // ========================================================================================
        list = tService.findAllByUsername( "mjane" );
        assertEquals( 3, list.size() );
        for ( final Ticket t : list ) {
            final List<MenuItem> cart = t.getCart();
            assertEquals( 1, cart.size() );
            final MenuItem item = cart.get( 0 );
            final String rName = item.getRecipe().getName();

            switch ( rName ) {
                case "Coffee":
                    assertEquals( 2, item.getAmount() );
                    break;
                case "Tea":
                    assertEquals( 2, item.getAmount() );
                    assertTrue( t.isComplete() );
                    break;
                case "Cookie":
                    assertEquals( 2, item.getAmount() );
                    break;
                default:
                    fail( "Unknown MenuItem found." );
            }
        }

        // ========================================================================================
        // Test findAllByUsername (Customer 3)
        // ========================================================================================
        list = tService.findAllByUsername( "msue" );
        assertEquals( 3, list.size() );
        for ( final Ticket t : list ) {
            final List<MenuItem> cart = t.getCart();
            assertEquals( 1, cart.size() );
            final MenuItem item = cart.get( 0 );
            final String rName = item.getRecipe().getName();

            switch ( rName ) {
                case "Coffee":
                    assertEquals( 3, item.getAmount() );
                    break;
                case "Tea":
                    assertEquals( 3, item.getAmount() );

                    break;
                case "Cookie":
                    assertEquals( 3, item.getAmount() );
                    assertTrue( t.isComplete() );
                    break;
                default:
                    fail( "Unknown MenuItem found." );
            }
        }

    }

    /**
     * Make a recipe with the given name, price, and quantities of 2 ingredients
     *
     * @param name
     *            name of recipe
     * @param price
     *            price of recipe
     * @param coffee
     *            quantity of ingredient 1
     * @param milk
     *            quantity of ingredient 2
     * @return The fully made recipe with the given values
     */
    private Recipe makeRecipe ( final String name, final int price, final int coffee, final int milk ) {
        final Recipe r = new Recipe();
        r.setName( name );
        r.setPrice( price );
        r.addIngredient( "Coffee" );
        r.editIngredient( "Coffee", coffee );
        r.addIngredient( "Milk" );
        r.editIngredient( "Milk", milk );
        return r;

    }
}
