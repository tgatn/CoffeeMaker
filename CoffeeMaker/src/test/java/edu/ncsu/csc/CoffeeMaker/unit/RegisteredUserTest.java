package edu.ncsu.csc.CoffeeMaker.unit;

import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
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
import edu.ncsu.csc.CoffeeMaker.models.RegisteredUser;
import edu.ncsu.csc.CoffeeMaker.models.User.Role;
import edu.ncsu.csc.CoffeeMaker.services.CustomerService;

@ExtendWith ( SpringExtension.class )
@EnableAutoConfiguration
@SpringBootTest ( classes = TestConfig.class )
public class RegisteredUserTest {

    @Autowired
    private CustomerService service;

    @BeforeEach
    public void setup () {
        service.deleteAll();
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
        List<RegisteredUser> list = service.findAll();
        assertEquals( 0, list.size() );

        // Save 1 customer to the database
        final RegisteredUser cust1 = new RegisteredUser( "John", "Doe", "jdoe", "pass1", Role.CUSTOMER );
        service.save( cust1 );

        // Update all lists
        list = service.findAll();

        // There should be 1 user in the system
        assertEquals( 1, list.size() );

        // Save an employee to the system
        final RegisteredUser cust2 = new RegisteredUser( "Mary", "Jane", "mjane", "pass2", Role.EMPLOYEE );
        service.save( cust2 );

        list = service.findAll();
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

        final RegisteredUser mod1 = service.findByName( "jdoe" );
        mod1.setFirstName( "Jane" );
        service.save( mod1 );
        list = service.findAll();

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
        final RegisteredUser del1 = service.findByName( "jdoe" );
        service.delete( del1 );
        list = service.findAll();

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
}
