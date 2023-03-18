package edu.ncsu.csc.CoffeeMaker.unit;

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
import edu.ncsu.csc.CoffeeMaker.services.InventoryService;

@ExtendWith ( SpringExtension.class )
@EnableAutoConfiguration
@SpringBootTest ( classes = TestConfig.class )
public class InventoryTest {

    @Autowired
    private InventoryService inventoryService;

    @BeforeEach
    public void setup () {
        final Inventory ivt = inventoryService.getInventory();
        inventoryService.save( ivt );
    }

    /**
     * Test adding ingredient(s) to the inventory one at a time. The default
     * quantity is zero.
     */
    @Test
    @Transactional
    public void testAddInventory1 () {
        Inventory ivt = inventoryService.getInventory();

        // This add method defaults the amount to zero
        ivt.addIngredient( "Coffee" );

        /* Save and retrieve again to update with DB */
        inventoryService.save( ivt );

        // Get the inventory from the what is saved to database
        ivt = inventoryService.getInventory();

        // The inventory should only have 1 ingredient
        List<Ingredient> list = ivt.getIngredients();
        Assertions.assertEquals( 1, list.size() );

        // The default value is zero
        Ingredient ing = list.get( 0 );
        Assertions.assertEquals( 0, ing.getAmount() );

        /* ============================================== */
        /* Add more than 1 ingredient to the inventory */
        /* They should each be stored in separate ingredient objects */
        /*
         * The first test will have passed and the inventory will only have the
         * 1 Ingredient
         */

        ivt.addIngredient( "Tea" );

        /* Save and retrieve again to update with DB */
        inventoryService.save( ivt );

        // Get the inventory from the what is saved to database
        ivt = inventoryService.getInventory();

        // The inventory should have 2 ingredients (Coffee, Tea)
        list = ivt.getIngredients();
        Assertions.assertEquals( 2, list.size() );

        // Put all of the names in an array for testing
        final String[] names = { "Coffee", "Tea" };
        final boolean[] tests = { false, false };
        for ( int i = 0; i < names.length; i++ ) {
            for ( int j = 0; j < list.size(); j++ ) {
                ing = list.get( j );
                if ( names[i].equals( ing.getIngredient() ) ) {
                    tests[i] = true;
                    Assertions.assertEquals( 0, ing.getAmount(),
                            String.format( "The quantity of %s was %d. Expected 0.", names[i], ing.getAmount() ) );
                }
            }
        }

        for ( int i = 0; i < tests.length; i++ ) {
            Assertions.assertTrue( tests[i], String.format( "%s was not found in the inventory.", names[i] ) );
        }

        /* ============================================== */
        /* Add more than 1 ingredient to the inventory */
        /* They should each be stored in separate ingredient objects */
        /*
         * The first tests will have passed and the inventory will only have the
         * 2 Ingredient
         */

        ivt.addIngredient( "Milk" );

        /* Save and retrieve again to update with DB */
        inventoryService.save( ivt );

        // Get the inventory from the what is saved to database
        ivt = inventoryService.getInventory();

        // The inventory should have 2 ingredients (Coffee, Tea)
        list = ivt.getIngredients();
        Assertions.assertEquals( 3, list.size() );

        // Put all of the names in an array for testing
        final String[] name = { "Coffee", "Tea", "Milk" };
        final boolean[] test = { false, false, false };
        for ( int i = 0; i < names.length; i++ ) {
            for ( int j = 0; j < list.size(); j++ ) {
                ing = list.get( j );
                if ( names[i].equals( ing.getIngredient() ) ) {
                    tests[i] = true;
                    Assertions.assertEquals( 0, ing.getAmount(),
                            String.format( "The quantity of %s was %d. Expected 0.", names[i], ing.getAmount() ) );
                }
            }
        }

        for ( int i = 0; i < tests.length; i++ ) {
            Assertions.assertTrue( tests[i], String.format( "%s was not found in the inventory.", names[i] ) );
        }
    }

    /**
     * Test adding an ingredient with no name (blank, null, whitespace)
     */
    @Test
    @Transactional
    public void testAddInventory2 () {
        // Add empty string
        Inventory ivt = inventoryService.getInventory();
        try {
            ivt.addIngredient( "" );
        }
        catch ( final IllegalArgumentException iae ) {
            final List<Ingredient> list = ivt.getIngredients();
            Assertions.assertEquals( 0, list.size() );
        }

        // Add null
        ivt = inventoryService.getInventory();
        try {
            ivt.addIngredient( null );
        }
        catch ( final IllegalArgumentException iae ) {
            final List<Ingredient> list = ivt.getIngredients();
            Assertions.assertEquals( 0, list.size() );
        }

        // Add whitespace
        ivt = inventoryService.getInventory();
        try {
            ivt.addIngredient( "\t\n" );
        }
        catch ( final IllegalArgumentException iae ) {
            final List<Ingredient> list = ivt.getIngredients();
            Assertions.assertEquals( 0, list.size() );
        }
    }

    /**
     * Test that ingredients with duplicate names are not added.
     */
    @Test
    @Transactional
    public void testAddInventory3 () {
        Inventory ivt = inventoryService.getInventory();
        List<Ingredient> list = ivt.getIngredients();
        Assertions.assertEquals( 0, list.size() );

        try {
            // Add first ingredient
            ivt.addIngredient( "Coffee" );
            inventoryService.save( ivt );
            ivt = inventoryService.getInventory();
            list = ivt.getIngredients();
            Assertions.assertEquals( 1, list.size() );

            // Add duplicate
            ivt.addIngredient( "Coffee" );
            Assertions.fail( "Added a duplicate ingredient to the inventory.\n2 instances of Coffee." );
        }
        catch ( final IllegalArgumentException iae ) {
            inventoryService.save( ivt );
            ivt = inventoryService.getInventory();
            list = ivt.getIngredients();
            Assertions.assertEquals( 1, list.size() );
        }

    }

    /**
     * Test adding ingredient(s) to the inventory one at a time. The default
     * quantity is zero.
     */
    @Test
    @Transactional
    public void testAddInventory4 () {

        Inventory ivt = inventoryService.getInventory();

        // This add method defaults the amount to zero
        ivt.addIngredient( "Coffee", 1 );

        /* Save and retrieve again to update with DB */
        inventoryService.save( ivt );

        // Get the inventory from the what is saved to database
        ivt = inventoryService.getInventory();

        // The inventory should only have 1 ingredient
        List<Ingredient> list = ivt.getIngredients();
        Assertions.assertEquals( 1, list.size() );

        // The default value is zero
        Ingredient ing = list.get( 0 );
        Assertions.assertEquals( 1, ing.getAmount() );

        /* ============================================== */
        /* Add more than 1 ingredient to the inventory */
        /* They should each be stored in separate ingredient objects */
        /*
         * The first test will have passed and the inventory will only have the
         * 1 Ingredient
         */

        ivt.addIngredient( "Tea", 2 );

        /* Save and retrieve again to update with DB */
        inventoryService.save( ivt );

        // Get the inventory from the what is saved to database
        ivt = inventoryService.getInventory();

        // The inventory should have 2 ingredients (Coffee, Tea)
        list = ivt.getIngredients();
        Assertions.assertEquals( 2, list.size() );

        // Put all of the names in an array for testing
        final String[] names = { "Coffee", "Tea" };
        final boolean[] tests = { false, false };
        for ( int i = 0; i < names.length; i++ ) {
            for ( int j = 0; j < list.size(); j++ ) {
                ing = list.get( j );
                if ( names[i].equals( ing.getIngredient() ) ) {
                    tests[i] = true;
                    Assertions.assertEquals( i + 1, ing.getAmount(), String
                            .format( "The quantity of %s was %d. Expected %d.", names[i], ing.getAmount(), i + 1 ) );
                }
            }
        }

        for ( int i = 0; i < tests.length; i++ ) {
            Assertions.assertTrue( tests[i], String.format( "%s was not found in the inventory.", names[i] ) );
        }

        /* ============================================== */
        /* Add more than 1 ingredient to the inventory */
        /* They should each be stored in separate ingredient objects */
        /*
         * The first tests will have passed and the inventory will only have the
         * 2 Ingredient
         */

        ivt.addIngredient( "Milk", 3 );

        /* Save and retrieve again to update with DB */
        inventoryService.save( ivt );

        // Get the inventory from the what is saved to database
        ivt = inventoryService.getInventory();

        // The inventory should have 2 ingredients (Coffee, Tea)
        list = ivt.getIngredients();
        Assertions.assertEquals( 3, list.size() );

        // Put all of the names in an array for testing
        final String[] name = { "Coffee", "Tea", "Milk" };
        final boolean[] test = { false, false, false };
        for ( int i = 0; i < names.length; i++ ) {
            for ( int j = 0; j < list.size(); j++ ) {
                ing = list.get( j );
                if ( names[i].equals( ing.getIngredient() ) ) {
                    tests[i] = true;
                    Assertions.assertEquals( i + 1, ing.getAmount(), String
                            .format( "The quantity of %s was %d. Expected %d.", names[i], ing.getAmount(), i + 1 ) );
                }
            }
        }

        for ( int i = 0; i < tests.length; i++ ) {
            Assertions.assertTrue( tests[i], String.format( "%s was not found in the inventory.", names[i] ) );
        }
    }

    /**
     * Test adding an ingredient with no name (blank, null, whitespace). Test
     * adding negative amount of ingredient. Test Null Amount of ingredient.
     */
    @Test
    @Transactional
    public void testAddInventory5 () {
        // Add empty string
        Inventory ivt = inventoryService.getInventory();
        try {
            ivt.addIngredient( "", 0 );
        }
        catch ( final IllegalArgumentException iae ) {
            final List<Ingredient> list = ivt.getIngredients();
            Assertions.assertEquals( 0, list.size() );
        }

        // Add null
        ivt = inventoryService.getInventory();
        try {
            ivt.addIngredient( null, 0 );
        }
        catch ( final IllegalArgumentException iae ) {
            final List<Ingredient> list = ivt.getIngredients();
            Assertions.assertEquals( 0, list.size() );
        }

        // Add whitespace
        ivt = inventoryService.getInventory();
        try {
            ivt.addIngredient( "\t\n", 0 );
        }
        catch ( final IllegalArgumentException iae ) {
            final List<Ingredient> list = ivt.getIngredients();
            Assertions.assertEquals( 0, list.size() );
        }

        // Add negative ingredient
        ivt = inventoryService.getInventory();
        try {
            ivt.addIngredient( "Coffee", -1 );
        }
        catch ( final IllegalArgumentException iae ) {
            final List<Ingredient> list = ivt.getIngredients();
            Assertions.assertEquals( 0, list.size() );
        }

        // Add null amount
        ivt = inventoryService.getInventory();
        try {
            ivt.addIngredient( "Coffee", null );
        }
        catch ( final IllegalArgumentException iae ) {
            final List<Ingredient> list = ivt.getIngredients();
            Assertions.assertEquals( 0, list.size() );
        }

    }

    /**
     * Test that ingredients with duplicate names are not added. Tests with
     * nonzero quantities
     */
    @Test
    @Transactional
    public void testAddInventory6 () {
        Inventory ivt = inventoryService.getInventory();
        List<Ingredient> list = ivt.getIngredients();
        try {
            // Add first ingredient
            ivt.addIngredient( "Coffee", 5 );
            inventoryService.save( ivt );
            ivt = inventoryService.getInventory();
            list = ivt.getIngredients();
            Assertions.assertEquals( 1, list.size() );
            Assertions.assertEquals( 5, list.get( 0 ).getAmount() );

            // Add duplicate
            ivt.addIngredient( "Coffee", 6 );
            Assertions.fail( "Added a duplicate ingredient to the inventory.\n2 instances of Coffee." );
        }
        catch ( final IllegalArgumentException iae ) {
            inventoryService.save( ivt );
            ivt = inventoryService.getInventory();
            list = ivt.getIngredients();
            Assertions.assertEquals( 1, list.size() );
            Assertions.assertEquals( 5, list.get( 0 ).getAmount() );
        }

    }

    /**
     * Test adding more of an ingredient to the inventory. Quantities of other
     * ingredients should not change
     */
    @Test
    @Transactional
    public void testUpdateInventory1 () {
        Inventory ivt = inventoryService.getInventory();
        List<Ingredient> list = ivt.getIngredients();
        ivt.addIngredient( "Coffee" );
        inventoryService.save( ivt );
        ivt = inventoryService.getInventory();

        ivt.addIngredient( "Tea" );
        inventoryService.save( ivt );
        ivt = inventoryService.getInventory();

        // Both ingredients should have value 0 to start
        list = ivt.getIngredients();
        Assertions.assertEquals( 2, list.size() );
        for ( final Ingredient i : list ) {
            Assertions.assertEquals( 0, i.getAmount() );
        }

        // Add coffee to the inventory
        // Tea is unchanged
        ivt.updateIngredient( "Coffee", 5 );
        inventoryService.save( ivt );
        ivt = inventoryService.getInventory();
        list = ivt.getIngredients();
        Assertions.assertEquals( 2, list.size() );

        final String[] names = { "Coffee", "Tea" };
        final boolean[] tests = { false, false };
        // Check each ingredient
        for ( final Ingredient i : list ) {
            final String n = i.getIngredient();
            switch ( n ) {
                case "Coffee":
                    // 5 units of coffee expected
                    Assertions.assertEquals( 5, i.getAmount() );
                    tests[0] = true;
                    break;
                case "Tea":
                    // Tea should be unmodified
                    Assertions.assertEquals( 0, i.getAmount() );
                    tests[1] = true;
                    break;
                default:
                    Assertions.fail( String.format( "Unknown ingredient found in inventory: %s", n ) );
            }
        }
        for ( int i = 0; i < tests.length; i++ ) {
            Assertions.assertTrue( tests[i], String.format( "%s not found in the inventory", names[i] ) );
        }
        /*
         * ====================================================================
         * Add Tea to the inventory. Coffee unchanged
         * ====================================================================
         */

        // Add Tea to the inventory
        // Coffee unchanged
        ivt.updateIngredient( "Tea", 7 );
        inventoryService.save( ivt );
        ivt = inventoryService.getInventory();
        list = ivt.getIngredients();
        Assertions.assertEquals( 2, list.size() );

        tests[0] = false;
        tests[1] = false;
        // Check each ingredient
        for ( final Ingredient i : list ) {
            final String n = i.getIngredient();
            switch ( n ) {
                case "Coffee":
                    // 5 units of coffee expected, unmodified
                    Assertions.assertEquals( 5, i.getAmount() );
                    tests[0] = true;
                    break;
                case "Tea":
                    // Tea should be 7
                    Assertions.assertEquals( 7, i.getAmount() );
                    tests[1] = true;
                    break;
                default:
                    Assertions.fail( String.format( "Unknown ingredient found in inventory: %s", n ) );
            }
        }
        for ( int i = 0; i < tests.length; i++ ) {
            Assertions.assertTrue( tests[i], String.format( "%s not found in the inventory", names[i] ) );
        }
        /*
         * ====================================================================
         * Check that adding a second time will increase the amount, not set the
         * amount
         * ====================================================================
         */

        // Add coffee to the inventory
        // Tea is unchanged
        ivt.updateIngredient( "Coffee", 7 );
        inventoryService.save( ivt );
        ivt = inventoryService.getInventory();
        list = ivt.getIngredients();
        Assertions.assertEquals( 2, list.size() );

        tests[0] = false;
        tests[1] = false;
        // Check each ingredient
        for ( final Ingredient i : list ) {
            final String n = i.getIngredient();
            switch ( n ) {
                case "Coffee":
                    // 5 + 7 = 12 units of coffee expected
                    Assertions.assertEquals( 12, i.getAmount() );
                    tests[0] = true;
                    break;
                case "Tea":
                    // Tea should be unmodified (7)
                    Assertions.assertEquals( 7, i.getAmount() );
                    tests[1] = true;
                    break;
                default:
                    Assertions.fail( String.format( "Unknown ingredient found in inventory: %s", n ) );
            }
        }
        for ( int i = 0; i < tests.length; i++ ) {
            Assertions.assertTrue( tests[i], String.format( "%s not found in the inventory", names[i] ) );
        }
    }

    /**
     * Test adding invalid inputs. (0, null, and negative amounts)
     */
    @Test
    @Transactional
    public void testUpdateInventory2 () {
        // Add the initial 2 ingredients
        Inventory ivt = inventoryService.getInventory();
        List<Ingredient> list = ivt.getIngredients();
        ivt.addIngredient( "Coffee" );
        inventoryService.save( ivt );
        ivt = inventoryService.getInventory();

        ivt.addIngredient( "Tea" );
        inventoryService.save( ivt );
        ivt = inventoryService.getInventory();

        // Both ingredients should have value 0 to start
        list = ivt.getIngredients();
        Assertions.assertEquals( 2, list.size() );
        for ( final Ingredient i : list ) {
            Assertions.assertEquals( 0, i.getAmount() );
        }

        /*
         * ====================================================================
         * Added inventory 0
         * ====================================================================
         */
        inventoryService.save( ivt );
        ivt = inventoryService.getInventory();
        try {
            Assertions.assertFalse( ivt.updateIngredient( "Coffee", 0 ) );
            // Should still have 2 ingredients with 0 inventory
            list = ivt.getIngredients();
            Assertions.assertEquals( 2, list.size() );
            for ( final Ingredient i : list ) {
                Assertions.assertEquals( 0, i.getAmount() );
            }
        }
        catch ( final Exception e ) {
            Assertions.fail( String.format( "Unknown EXception Thrown:\n%s", e.getMessage() ) );
        }

        /*
         * ====================================================================
         * Added inventory Negative
         * ====================================================================
         */
        inventoryService.save( ivt );
        ivt = inventoryService.getInventory();
        try {
            Assertions.assertFalse( ivt.updateIngredient( "Coffee", -1 ) );
            // Should still have 2 ingredients with 0 inventory
            list = ivt.getIngredients();
            Assertions.assertEquals( 2, list.size() );
            for ( final Ingredient i : list ) {
                Assertions.assertEquals( 0, i.getAmount() );
            }
        }
        catch ( final Exception e ) {
            Assertions.fail( String.format( "Unknown EXception Thrown:\n%s", e.getMessage() ) );
        }

        /*
         * ====================================================================
         * Added inventory Null
         * ====================================================================
         */
        inventoryService.save( ivt );
        ivt = inventoryService.getInventory();
        try {
            Assertions.assertFalse( ivt.updateIngredient( "Coffee", null ) );

            // Should still have 2 ingredients with 0 inventory
            list = ivt.getIngredients();
            Assertions.assertEquals( 2, list.size() );
            for ( final Ingredient i : list ) {
                Assertions.assertEquals( 0, i.getAmount() );
            }
        }
        catch ( final Exception e ) {
            Assertions.fail( String.format( "Unknown EXception Thrown:\n%s", e.getMessage() ) );
        }
    }

    /**
     * Test adding unknown ingredient to inventory
     */
    @Test
    @Transactional
    public void testUpdateInventory3 () {
        // Add the initial 2 ingredients
        Inventory ivt = inventoryService.getInventory();
        List<Ingredient> list = ivt.getIngredients();
        ivt.addIngredient( "Coffee" );
        inventoryService.save( ivt );
        ivt = inventoryService.getInventory();

        ivt.addIngredient( "Tea" );
        inventoryService.save( ivt );
        ivt = inventoryService.getInventory();

        // Both ingredients should have value 0 to start
        list = ivt.getIngredients();
        Assertions.assertEquals( 2, list.size() );
        for ( final Ingredient i : list ) {
            Assertions.assertEquals( 0, i.getAmount() );
        }

        /*
         * ====================================================================
         * Added unknown ingredient
         * ====================================================================
         */
        inventoryService.save( ivt );
        ivt = inventoryService.getInventory();
        try {
            Assertions.assertFalse( ivt.updateIngredient( "Milk", 5 ) );
            // Should still have 2 ingredients with 0 inventory
            list = ivt.getIngredients();
            Assertions.assertEquals( 2, list.size() );
            for ( final Ingredient i : list ) {
                Assertions.assertEquals( 0, i.getAmount() );
            }
        }
        catch ( final Exception e ) {
            Assertions.fail( String.format( "Unknown EXception Thrown:\n%s", e.getMessage() ) );
        }
    }

    /**
     * Test enough ingredients
     */
    @Test
    @Transactional
    public void testEnoughIngredients1 () {
        // Add the initial 2 ingredients
        Inventory ivt = inventoryService.getInventory();
        List<Ingredient> list = ivt.getIngredients();
        ivt.addIngredient( "Coffee", 10 );
        inventoryService.save( ivt );
        ivt = inventoryService.getInventory();

        ivt.addIngredient( "Milk", 10 );
        inventoryService.save( ivt );
        ivt = inventoryService.getInventory();

        // Both ingredients should have value 10 to start
        list = ivt.getIngredients();
        Assertions.assertEquals( 2, list.size() );
        for ( final Ingredient i : list ) {
            Assertions.assertEquals( 10, i.getAmount() );
        }

        /*
         * ====================================================================
         * Test enough of all ingredients
         * ====================================================================
         */
        final Recipe r = new Recipe();
        r.setName( "Latte" );
        r.addIngredient( "Coffee" );
        r.editIngredient( "Coffee", 4 );

        r.addIngredient( "Milk" );
        r.editIngredient( "Milk", 2 );

        Assertions.assertTrue( ivt.enoughIngredients( r ) );

        /*
         * ====================================================================
         * Test not enough coffee
         * ====================================================================
         */
        r.setName( "Latte" );
        r.addIngredient( "Coffee" );
        r.editIngredient( "Coffee", 11 );

        r.addIngredient( "Milk" );
        r.editIngredient( "Milk", 2 );

        Assertions.assertFalse( ivt.enoughIngredients( r ) );

        /*
         * ====================================================================
         * Test not enough Milk
         * ====================================================================
         */
        r.setName( "Latte" );
        r.addIngredient( "Coffee" );
        r.editIngredient( "Coffee", 1 );

        r.addIngredient( "Milk" );
        r.editIngredient( "Milk", 12 );

        Assertions.assertFalse( ivt.enoughIngredients( r ) );

        /*
         * ====================================================================
         * Test not enough of all ingredient
         * ====================================================================
         */
        r.setName( "Latte" );
        r.addIngredient( "Coffee" );
        r.editIngredient( "Coffee", 11 );

        r.addIngredient( "Milk" );
        r.editIngredient( "Milk", 12 );

        Assertions.assertFalse( ivt.enoughIngredients( r ) );

        /*
         * ====================================================================
         * Test not enough of all ingredient
         * ====================================================================
         */
        r.setName( "Mocha" );
        r.addIngredient( "Coffee" );
        r.editIngredient( "Coffee", 4 );

        r.addIngredient( "Milk" );
        r.editIngredient( "Milk", 2 );

        r.addIngredient( "Chocolate" );
        r.editIngredient( "Chocolate", 3 );

        Assertions.assertFalse( ivt.enoughIngredients( r ) );

    }

    @Test
    @Transactional
    public void testUseIngredients1 () {
        /*
         * ====================================================================
         * Test enough of all ingredients
         * ====================================================================
         */
        // Add the initial 2 ingredients
        Inventory ivt = inventoryService.getInventory();
        List<Ingredient> list = ivt.getIngredients();
        ivt.addIngredient( "Coffee", 10 );
        inventoryService.save( ivt );
        ivt = inventoryService.getInventory();

        ivt.addIngredient( "Milk", 10 );
        inventoryService.save( ivt );
        ivt = inventoryService.getInventory();

        // Both ingredients should have value 10 to start
        list = ivt.getIngredients();
        Assertions.assertEquals( 2, list.size() );
        for ( final Ingredient i : list ) {
            Assertions.assertEquals( 10, i.getAmount() );
        }

        /*
         * ====================================================================
         * Test enough of all ingredients
         * ====================================================================
         */
        final Recipe r = new Recipe();
        r.setName( "Latte" );
        r.addIngredient( "Coffee" );
        r.editIngredient( "Coffee", 4 );

        r.addIngredient( "Milk" );
        r.editIngredient( "Milk", 2 );

        Assertions.assertTrue( ivt.useIngredients( r ) );
        inventoryService.save( ivt );
        ivt = inventoryService.getInventory();
        list = ivt.getIngredients();

        for ( final Ingredient i : list ) {
            final String n = i.getIngredient();
            switch ( n ) {
                case "Coffee":
                    // 10 - 4 = 6 units of coffee
                    Assertions.assertEquals( 6, i.getAmount() );
                    break;
                case "Milk":
                    // 10 - 2 = 8 units of Milk
                    Assertions.assertEquals( 8, i.getAmount() );
                    break;
                default:
                    Assertions.fail( String.format( "Unknown ingredient found in inventory: %s", n ) );
            }
        }

        /*
         * ====================================================================
         * Test not enough Coffee. Ingredient amounts should not change
         * ====================================================================
         */
        r.setName( "Latte" );
        r.addIngredient( "Coffee" );
        r.editIngredient( "Coffee", 10 );

        r.addIngredient( "Milk" );
        r.editIngredient( "Milk", 2 );
        try {
            Assertions.assertFalse( ivt.useIngredients( r ) );
        }
        catch ( final IllegalArgumentException e ) {
            inventoryService.save( ivt );
            ivt = inventoryService.getInventory();
            list = ivt.getIngredients();

            for ( final Ingredient i : list ) {
                final String n = i.getIngredient();
                switch ( n ) {
                    case "Coffee":
                        // 10 - 4 = 6 units of coffee
                        Assertions.assertEquals( 6, i.getAmount() );
                        break;
                    case "Milk":
                        // 10 - 2 = 8 units of Milk
                        Assertions.assertEquals( 8, i.getAmount() );
                        break;
                    default:
                        Assertions.fail( String.format( "Unknown ingredient found in inventory: %s", n ) );
                }
            }
        }

        /*
         * ====================================================================
         * All other cases are covered by enoughIngredients() tests
         * ====================================================================
         */
    }

    @Test
    @Transactional
    public void testToString () {
        Inventory ivt = inventoryService.getInventory();
        ivt.addIngredient( "Coffee", 50 );
        ivt.addIngredient( "Milk", 50 );
        ivt.addIngredient( "Chocolate", 50 );
        ivt.addIngredient( "Sugar", 50 );
        String exp = "Coffee: 50\nMilk: 50\nChocolate: 50\nSugar: 50\n";
        Assertions.assertEquals( exp, ivt.toString() );

        /*
         * ====================================================================
         * Test enough of all ingredients
         * ====================================================================
         */
        final Recipe r = new Recipe();
        r.setName( "Latte" );
        r.addIngredient( "Coffee" );
        r.editIngredient( "Coffee", 4 );

        r.addIngredient( "Milk" );
        r.editIngredient( "Milk", 2 );

        Assertions.assertTrue( ivt.useIngredients( r ) );
        inventoryService.save( ivt );
        ivt = inventoryService.getInventory();

        exp = "Coffee: 46\nMilk: 48\nChocolate: 50\nSugar: 50\n";
        Assertions.assertEquals( exp, ivt.toString() );
    }
}
