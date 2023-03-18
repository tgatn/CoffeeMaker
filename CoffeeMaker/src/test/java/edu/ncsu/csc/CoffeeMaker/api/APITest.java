package edu.ncsu.csc.CoffeeMaker.api;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
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
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import edu.ncsu.csc.CoffeeMaker.common.TestUtils;
import edu.ncsu.csc.CoffeeMaker.models.Ingredient;
import edu.ncsu.csc.CoffeeMaker.models.Inventory;
import edu.ncsu.csc.CoffeeMaker.models.Recipe;

/**
 * API Test class Tests getting recipes, adding inventory, and making coffee.
 * Used the CSC326 class pages for reference.
 *
 * @author Ben Abrams
 * @author Tung Tran
 *
 */
@ExtendWith ( SpringExtension.class )
@SpringBootTest
@AutoConfigureMockMvc
public class APITest {
    /**
     * MockMvc uses Spring's testing framework to handle requests to the REST
     * API
     */
    private MockMvc               mvc;

    /** Web application interface to be used in tests */
    @Autowired
    private WebApplicationContext context;

    /**
     * Sets up the tests.
     */
    @BeforeEach
    public void setup () {
        mvc = MockMvcBuilders.webAppContextSetup( context ).build();
    }

    /**
     * Tests getting recipes, adding inventory, and making coffee using the web
     * application.
     *
     * @throws Exception
     *             if our calls to web application fail
     */
    @Test
    @Transactional
    public void testEnsureRecipe () throws Exception {
        String recipe = mvc.perform( get( "/api/v1/recipes" ) ).andDo( print() ).andExpect( status().isOk() )
                .andReturn().getResponse().getContentAsString();

        /* Figure out if the recipe we want is present */
        if ( !recipe.contains( "Mocha" ) ) {
            // create a new Mocha recipe
            final Recipe r = createRecipe( "Mocha", 10, 3, 4, 8, 5 );

            mvc.perform( post( "/api/v1/recipes" ).contentType( MediaType.APPLICATION_JSON )
                    .content( TestUtils.asJsonString( r ) ) ).andExpect( status().isOk() );
        }

        recipe = mvc.perform( get( "/api/v1/recipes" ) ).andDo( print() ).andExpect( status().isOk() ).andReturn()
                .getResponse().getContentAsString();

        assertTrue( recipe.contains( "Mocha" ) );

        final Inventory i = new Inventory();
        i.addIngredient( "Coffee" );
        i.updateIngredient( "Coffee", 50 );
        i.addIngredient( "Milk" );
        i.updateIngredient( "Milk", 50 );
        i.addIngredient( "Chocolate" );
        i.updateIngredient( "Chocolate", 50 );
        i.addIngredient( "Sugar" );
        i.updateIngredient( "sugar", 50 );

        mvc.perform( put( "/api/v1/inventory" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( i ) ) ).andExpect( status().isOk() );

        mvc.perform( post( "/api/v1/makecoffee/Mocha" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( 100 ) ) ).andExpect( status().isOk() );

        // update inventory with one ingredient
        String inventory = mvc.perform( get( "/api/v1/inventory" ) ).andDo( print() ).andExpect( status().isOk() )
                .andReturn().getResponse().getContentAsString();
        assertFalse( inventory.contains( "Berry" ) );
        final Ingredient berry = new Ingredient( "Berry", 10 );
        mvc.perform( put( "/api/v1/inventory/ingredient" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( berry ) ) ).andExpect( status().isOk() );

        // get inventory
        inventory = mvc.perform( get( "/api/v1/inventory" ) ).andDo( print() ).andExpect( status().isOk() ).andReturn()
                .getResponse().getContentAsString();
        assertTrue( inventory.contains( "Berry" ) );

        // test adding inventory valid ingredient
        mvc.perform( put( "/api/v1/inventory/ingredient/Coffee" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( 10 ) ) ).andExpect( status().isOk() );
        inventory = mvc.perform( get( "/api/v1/inventory" ) ).andDo( print() ).andExpect( status().isOk() ).andReturn()
                .getResponse().getContentAsString();
        assertTrue( inventory.contains( "\"amount\":57,\"ingredient\":\"Coffee\"" ) );

        // test adding inventory invalid ingredient
        mvc.perform( put( "/api/v1/inventory/ingredient/random" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( 10 ) ) ).andExpect( status().isNotFound() );

        // Adding a 2nd recipe
        final Recipe r2 = createRecipe( "IceCoffee", 5, 5, 5, 2, 1 );

        // puts recipe in database
        mvc.perform( post( "/api/v1/recipes" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( r2 ) ) ).andExpect( status().isOk() );

        // gets a list of recipe
        recipe = mvc.perform( get( "/api/v1/recipes" ) ).andDo( print() ).andExpect( status().isOk() ).andReturn()
                .getResponse().getContentAsString();

        // ensure coffee is in the recipe list
        assertTrue( recipe.contains( "IceCoffee" ) );

        // deletes the recipe
        mvc.perform( delete( String.format( "/api/v1/recipes/IceCoffee" ) ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( r2 ) ) ).andExpect( status().isOk() );

        // update recipe
        recipe = mvc.perform( get( "/api/v1/recipes" ) ).andDo( print() ).andExpect( status().isOk() ).andReturn()
                .getResponse().getContentAsString();

        // ensure the recipe is no longer in the database
        assertFalse( recipe.contains( "IceCoffee" ) );

    }

    private Recipe createRecipe ( final String name, final Integer price, final Integer coffee, final Integer milk,
            final Integer sugar, final Integer chocolate ) {
        final Recipe recipe = new Recipe();
        recipe.setName( name );
        recipe.setPrice( price );
        recipe.addIngredient( "Coffee" );
        recipe.editIngredient( "Coffee", coffee );
        recipe.addIngredient( "Milk" );
        recipe.editIngredient( "Milk", milk );
        recipe.addIngredient( "Sugar" );
        recipe.editIngredient( "Sugar", sugar );
        recipe.addIngredient( "Chocolate" );
        recipe.editIngredient( "Chocolate", chocolate );

        return recipe;
    }

}
