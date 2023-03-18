package edu.ncsu.csc.CoffeeMaker.unit;

import static org.junit.Assert.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;

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
import edu.ncsu.csc.CoffeeMaker.services.IngredientService;

@ExtendWith ( SpringExtension.class )
@EnableAutoConfiguration
@SpringBootTest ( classes = TestConfig.class )
public class IngredientTest {
    @Autowired
    private IngredientService service;

    @BeforeEach
    public void setup () {
        service.deleteAll();
    }

    /**
     * Test adding valid ingredients to the database
     */
    @Test
    @Transactional
    public void testAddIngredient () {
        /*
         * ====================================================================
         * Test add and retrieve one ingredient from database
         * ====================================================================
         */
        final Ingredient i = new Ingredient( "Milk", 1 );
        service.save( i );

        List<Ingredient> list = service.findAll();

        Assertions.assertEquals( 1, list.size() );
        final Ingredient dbIng = list.get( 0 );
        Assertions.assertEquals( "Milk", dbIng.getIngredient() );
        Assertions.assertEquals( 1, dbIng.getAmount() );

        /*
         * ====================================================================
         * Add a second ingredient
         * ====================================================================
         */

        final Ingredient i2 = new Ingredient( "Tea", 2 );
        service.save( i2 );
        list = service.findAll();

        Assertions.assertEquals( 2, list.size() );
        for ( final Ingredient ing : list ) {
            final String name = ing.getIngredient();
            switch ( name ) {
                case "Milk":
                    Assertions.assertEquals( 1, ing.getAmount() );
                    break;
                case "Tea":
                    Assertions.assertEquals( 2, ing.getAmount() );
                    break;
                default:
                    fail( "Unknown ingredeient: " + name );

            }
        }

        /*
         * ====================================================================
         * Add a multiple ingredients ingredient
         * ====================================================================
         */

        final Ingredient i3 = new Ingredient( "Coffee", 3 );
        service.save( i3 );

        final Ingredient i4 = new Ingredient( "Chocolate", 4 );
        service.save( i4 );

        list = service.findAll();

        assertEquals( 4, list.size() );

        for ( final Ingredient ing : list ) {
            final String name = ing.getIngredient();
            switch ( name ) {
                case "Milk":
                    Assertions.assertEquals( 1, ing.getAmount() );
                    break;
                case "Tea":
                    Assertions.assertEquals( 2, ing.getAmount() );
                    break;
                case "Coffee":
                    Assertions.assertEquals( 3, ing.getAmount() );
                    break;
                case "Chocolate":
                    Assertions.assertEquals( 4, ing.getAmount() );
                    break;
                default:
                    fail( "Unknown ingredeient: " + name );

            }
        }

        /*
         * ====================================================================
         * Test findByName
         * ====================================================================
         */
        final Ingredient found = service.findByName( "Tea" );
        Assertions.assertNotNull( found );
        assertEquals( "Tea", found.getIngredient() );
        assertEquals( 2, found.getAmount() );
    }

    /**
     * Test error cases
     */
    @Test
    @Transactional
    public void testAddIngredientErros () {
        /*
         * ====================================================================
         * Test negative amounts
         * ====================================================================
         */
        try {
            final Ingredient i = new Ingredient( "Milk", -1 );
            fail( "Should throw an IAE for negative amounts of ingredient" );
        }
        catch ( final IllegalArgumentException e ) {
            // Do Nothing
        }

        final List<Ingredient> list = service.findAll();
        Assertions.assertEquals( 0, list.size() );
    }
}
