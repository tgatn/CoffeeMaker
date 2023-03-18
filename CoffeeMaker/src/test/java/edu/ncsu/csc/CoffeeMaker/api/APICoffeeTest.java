package edu.ncsu.csc.CoffeeMaker.api;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import javax.transaction.Transactional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import edu.ncsu.csc.CoffeeMaker.common.TestUtils;
import edu.ncsu.csc.CoffeeMaker.models.Inventory;
import edu.ncsu.csc.CoffeeMaker.models.Recipe;
import edu.ncsu.csc.CoffeeMaker.services.InventoryService;
import edu.ncsu.csc.CoffeeMaker.services.RecipeService;

@ExtendWith ( SpringExtension.class )
@SpringBootTest
@AutoConfigureMockMvc
public class APICoffeeTest {

    @Autowired
    private MockMvc          mvc;

    @Autowired
    private RecipeService    service;

    @Autowired
    private InventoryService iService;

    /**
     * Sets up the tests.
     */
    @BeforeEach
    public void setup () {
        service.deleteAll();
        iService.deleteAll();

        final Inventory ivt = iService.getInventory();

        ivt.addIngredient( "Chocolate", 15 );
        ivt.addIngredient( "Coffee", 15 );
        ivt.addIngredient( "Milk", 15 );
        ivt.addIngredient( "Sugar", 15 );
        ivt.addIngredient( "Ice", 1 );

        iService.save( ivt );

        final Recipe recipe = new Recipe();
        recipe.setName( "Coffee" );
        recipe.setPrice( 50 );
        recipe.addIngredient( "Coffee" );
        recipe.editIngredient( "Coffee", 3 );
        recipe.addIngredient( "Milk" );
        recipe.editIngredient( "Milk", 1 );
        recipe.addIngredient( "Sugar" );
        recipe.editIngredient( "Sugar", 1 );

        recipe.addIngredient( "Chocolate" );
        recipe.editIngredient( "Chocolate", 1 );
        recipe.addIngredient( "Ice" );
        recipe.editIngredient( "Ice", 10 );
        service.save( recipe );
    }

    @Test
    @Transactional
    public void testPurchaseBeverage1 () throws Exception {

        final Inventory ivt = iService.getInventory();
        ivt.updateIngredient( "ice", 50 );
        iService.save( ivt );

        final String name = "Coffee";

        mvc.perform( post( String.format( "/api/v1/makecoffee/%s", name ) ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( 60 ) ) ).andExpect( status().isOk() )
                .andExpect( jsonPath( "$.message" ).value( 10 ) );

    }

    @Test
    @Transactional
    public void testPurchaseBeverage2 () throws Exception {
        /* Insufficient amount paid */

        final Inventory ivt = iService.getInventory();
        ivt.updateIngredient( "ice", 20 );
        iService.save( ivt );

        final String name = "Coffee";

        mvc.perform( post( String.format( "/api/v1/makecoffee/%s", name ) ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( 40 ) ) ).andExpect( status().is4xxClientError() )
                .andExpect( jsonPath( "$.message" ).value( "Not enough money paid" ) );

    }

    @Test
    @Transactional
    public void testPurchaseBeverage3 () throws Exception {
        /* Insufficient inventory */

        final Inventory ivt = iService.getInventory();

        // In setup, all ingredients are set to 15
        final Recipe r = new Recipe();
        r.addIngredient( "Coffee" );
        r.editIngredient( "Coffee", 15 );

        ivt.useIngredients( r );
        // Previously set Coffee to 0

        iService.save( ivt );

        final String name = "Coffee";

        try {
            mvc.perform( post( String.format( "/api/v1/makecoffee/%s", name ) )
                    .contentType( MediaType.APPLICATION_JSON ).content( TestUtils.asJsonString( 50 ) ) )
                    .andExpect( status().is4xxClientError() )
                    .andExpect( jsonPath( "$.message" ).value( "Not enough inventory" ) );
        }
        catch ( final Exception e ) {
            // expected
        }

    }

}
