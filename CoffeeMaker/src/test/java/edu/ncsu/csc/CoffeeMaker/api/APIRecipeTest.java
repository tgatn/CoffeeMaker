package edu.ncsu.csc.CoffeeMaker.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import javax.transaction.Transactional;

import org.junit.jupiter.api.Assertions;
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
import edu.ncsu.csc.CoffeeMaker.models.Recipe;
import edu.ncsu.csc.CoffeeMaker.services.RecipeService;

@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith ( SpringExtension.class )
public class APIRecipeTest {

    /**
     * MockMvc uses Spring's testing framework to handle requests to the REST
     * API
     */
    private MockMvc               mvc;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private RecipeService         service;

    /**
     * Sets up the tests.
     */
    @BeforeEach
    public void setup () {
        mvc = MockMvcBuilders.webAppContextSetup( context ).build();

        service.deleteAll();
    }

    @Test
    @Transactional
    public void ensureRecipe () throws Exception {
        service.deleteAll();

        final Recipe r = createRecipe( "Mocha", 10, 3, 4, 8, 5 );

        mvc.perform( post( "/api/v1/recipes" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( r ) ) ).andExpect( status().isOk() );

    }

    @Test
    @Transactional
    public void testRecipeAPI () throws Exception {

        service.deleteAll();

        final Recipe recipe = createRecipe( "Delicious Not-Coffee", 5, 1, 20, 5, 20 );
        recipe.setName( "Delicious Not-Coffee" );

        mvc.perform( post( "/api/v1/recipes" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( recipe ) ) );

        Assertions.assertEquals( 1, (int) service.count() );

    }

    @Test
    @Transactional
    public void testAddRecipe2 () throws Exception {

        /* Tests a recipe with a duplicate name to make sure it's rejected */

        Assertions.assertEquals( 0, service.findAll().size(), "There should be no Recipes in the CoffeeMaker" );
        final String name = "Coffee";
        final Recipe r1 = createRecipe( name, 50, 3, 1, 1, 1 );

        service.save( r1 );

        final Recipe r2 = createRecipe( name, 50, 3, 1, 1, 1 );
        mvc.perform( post( "/api/v1/recipes" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( r2 ) ) ).andExpect( status().is4xxClientError() );

        Assertions.assertEquals( 1, service.findAll().size(), "There should only one recipe in the CoffeeMaker" );
    }

    @Test
    @Transactional
    public void testAddRecipe15 () throws Exception {

        /* Tests to make sure that our cap of 3 recipes is enforced */

        Assertions.assertEquals( 0, service.findAll().size(), "There should be no Recipes in the CoffeeMaker" );

        final Recipe r1 = createRecipe( "Coffee", 50, 3, 1, 1, 1 );
        service.save( r1 );
        final Recipe r2 = createRecipe( "Mocha", 50, 3, 1, 1, 2 );
        service.save( r2 );
        final Recipe r3 = createRecipe( "Latte", 60, 3, 2, 2, 1 );
        service.save( r3 );

        Assertions.assertEquals( 3, service.count(),
                "Creating three recipes should result in three recipes in the database" );

        final Recipe r4 = createRecipe( "Hot Chocolate", 75, 1, 2, 1, 2 );

        mvc.perform( post( "/api/v1/recipes" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( r4 ) ) ).andExpect( status().isInsufficientStorage() );

        Assertions.assertEquals( 3, service.count(), "Creating a fourth recipe should not get saved" );
    }

    @Test
    @Transactional
    public void testGetRecipe () throws Exception {
        // make first recipe
        Assertions.assertEquals( 0, service.findAll().size(), "There should be no Recipes in the CoffeeMaker" );
        final String name = "Coffee";
        final Recipe r1 = createRecipe( name, 50, 3, 1, 1, 1 );

        service.save( r1 );
        assertEquals( 1, service.count() );

        mvc.perform( post( "/api/v1/recipes" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( r1 ) ) );

        // make second recipe
        final String name2 = "Coffee2";
        final Recipe r2 = createRecipe( name2, 25, 1, 1, 1, 1 );

        service.save( r2 );
        assertEquals( 2, service.count() );

        mvc.perform( post( "/api/v1/recipes" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( r2 ) ) );

        // get specific recipe
        final String recipes = mvc.perform( get( "/api/v1/recipes/Coffee" ).contentType( MediaType.APPLICATION_JSON ) )
                .andReturn().getResponse().getContentAsString();

        assertTrue( recipes.contains( "Coffee" ) );

        // get null recipe
        mvc.perform( get( "/api/v1/recipes/does_not_exist" ).contentType( MediaType.APPLICATION_JSON ) )
                .andExpect( status().isNotFound() );

        // get all recipes
        final String recipes2 = mvc.perform( get( "/api/v1/recipes" ).contentType( MediaType.APPLICATION_JSON ) )
                .andReturn().getResponse().getContentAsString();

        assertTrue( recipes2.contains( "Coffee" ) );
        assertTrue( recipes2.contains( "Milk" ) );

    }

    @Test
    @Transactional
    public void testDeleteRecipe () throws Exception {
        // make first recipe
        Assertions.assertEquals( 0, service.findAll().size(), "There should be no Recipes in the CoffeeMaker" );
        final String name = "Coffee";
        final Recipe r1 = createRecipe( name, 50, 3, 1, 1, 1 );

        service.save( r1 );
        assertEquals( 1, service.count() );

        mvc.perform( post( "/api/v1/recipes" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( r1 ) ) );

        // make second recipe
        final String name2 = "Coffee2";
        final Recipe r2 = createRecipe( name2, 25, 1, 1, 1, 1 );

        service.save( r2 );
        assertEquals( 2, service.count() );

        mvc.perform( post( "/api/v1/recipes" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( r2 ) ) );

        // delete first recipe
        mvc.perform( delete( "/api/v1/recipes/Coffee" ).contentType( MediaType.APPLICATION_JSON ) )
                .andExpect( status().isOk() );

        // delete null recipe
        mvc.perform( delete( "/api/v1/recipes/does_not_exist" ).contentType( MediaType.APPLICATION_JSON ) )
                .andExpect( status().isNotFound() );

    }

    @Test
    @Transactional
    public void testDeleteRecipeIngredient () throws Exception {
        // make first recipe
        Assertions.assertEquals( 0, service.findAll().size(), "There should be no Recipes in the CoffeeMaker" );
        final String name = "IceCoffee";
        final Recipe r1 = createRecipe( name, 50, 3, 1, 1, 1 );

        service.save( r1 );
        assertEquals( 1, service.count() );

        // post recipe
        mvc.perform( post( "/api/v1/recipes" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( r1 ) ) );

        // delete milk
        mvc.perform( delete( "/api/v1/recipes/IceCoffee/ingredients/Milk" ).contentType( MediaType.APPLICATION_JSON ) )
                .andExpect( status().isOk() );

        Recipe recipe = service.findByName( "IceCoffee" );
        assertFalse( recipe.toString().contains( "Milk" ) );
        assertTrue( recipe.toString().contains( "Coffee" ) );
        assertTrue( recipe.toString().contains( "Sugar" ) );
        assertTrue( recipe.toString().contains( "Chocolate" ) );

        // delete Coffee from an invalid recipe name
        mvc.perform( delete( "/api/v1/recipes/random/ingredients/Coffee" ).contentType( MediaType.APPLICATION_JSON ) )
                .andExpect( status().isNotFound() );

        // delete an invalid ingredient name
        mvc.perform(
                delete( "/api/v1/recipes/IceCoffee/ingredients/random" ).contentType( MediaType.APPLICATION_JSON ) )
                .andExpect( status().isConflict() );

        // remove all ingredients except chocolate
        mvc.perform(
                delete( "/api/v1/recipes/IceCoffee/ingredients/Coffee" ).contentType( MediaType.APPLICATION_JSON ) )
                .andExpect( status().isOk() );
        mvc.perform( delete( "/api/v1/recipes/IceCoffee/ingredients/Sugar" ).contentType( MediaType.APPLICATION_JSON ) )
                .andExpect( status().isOk() );

        recipe = service.findByName( "IceCoffee" );
        assertFalse( recipe.toString().contains( "Milk" ) );
        assertFalse( recipe.toString().contains( " Coffee" ) );
        assertFalse( recipe.toString().contains( "Sugar" ) );
        assertTrue( recipe.toString().contains( "Chocolate" ) );

        // try to remove the last ingredient
        mvc.perform(
                delete( "/api/v1/recipes/IceCoffee/ingredients/Chocolate" ).contentType( MediaType.APPLICATION_JSON ) )
                .andExpect( status().isConflict() );

        recipe = service.findByName( "IceCoffee" );
        assertFalse( recipe.toString().contains( "Milk" ) );
        assertFalse( recipe.toString().contains( " Coffee" ) );
        assertFalse( recipe.toString().contains( "Sugar" ) );
        assertTrue( recipe.toString().contains( "Chocolate" ) );
    }

    @Test
    @Transactional
    public void testAddRecipeIngredient () throws Exception {
        // make first recipe
        Assertions.assertEquals( 0, service.findAll().size(), "There should be no Recipes in the CoffeeMaker" );
        final String name = "IceCoffee";
        final Recipe r1 = createRecipe( name, 50, 3, 1, 1, 1 );

        service.save( r1 );
        assertEquals( 1, service.count() );
        assertEquals( 4, service.findByName( name ).getIngredients().size() );

        // post recipe
        mvc.perform( post( "/api/v1/recipes" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( r1 ) ) );

        // add cheese
        final String i = "Cheese";
        mvc.perform( post( "/api/v1/recipes/IceCoffee/ingredients" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( i ) ) );

        Recipe recipe = service.findByName( "IceCoffee" );
        assertEquals( 5, recipe.getIngredients().size() );

        // try adding the same thing twice
        mvc.perform( post( "/api/v1/recipes/IceCoffee/ingredients" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( i ) ) ).andExpect( status().isConflict() );

        recipe = service.findByName( "IceCoffee" );
        assertEquals( 5, recipe.getIngredients().size() );

        // try adding to a random recipe
        mvc.perform( post( "/api/v1/recipes/random/ingredients" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( i ) ) ).andExpect( status().isNotFound() );
    }

    @Test
    @Transactional
    public void testEditRecipeIngredient () throws Exception {
        // make first recipe
        Assertions.assertEquals( 0, service.findAll().size(), "There should be no Recipes in the CoffeeMaker" );
        final String name = "IceCoffee";
        final Recipe r1 = createRecipe( name, 50, 3, 1, 1, 1 );

        service.save( r1 );
        assertEquals( 1, service.count() );

        Recipe recipe = service.findByName( name );
        assertEquals( "Coffee", recipe.getIngredients().get( 0 ).getIngredient() );
        assertEquals( 3, recipe.getIngredients().get( 0 ).getAmount() );

        // post recipe
        mvc.perform( post( "/api/v1/recipes" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( r1 ) ) );

        // change units of coffee to 20
        Integer units = 20;
        mvc.perform( put( "/api/v1/recipes/IceCoffee/ingredients/Coffee" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( units ) ) ).andExpect( status().isOk() );

        // check if it was updated
        recipe = service.findByName( name );
        assertEquals( "Coffee", recipe.getIngredients().get( 0 ).getIngredient() );
        assertEquals( 20, recipe.getIngredients().get( 0 ).getAmount() );

        // edit a random recipe
        units = 5;
        mvc.perform( put( "/api/v1/recipes/random/ingredients/Coffee" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( units ) ) ).andExpect( status().isNotFound() );

        // make sure nothing changed
        recipe = service.findByName( name );
        assertEquals( "Coffee", recipe.getIngredients().get( 0 ).getIngredient() );
        assertEquals( 20, recipe.getIngredients().get( 0 ).getAmount() );

        // edit a random ingredient
        mvc.perform( put( "/api/v1/recipes/IceCoffee/ingredients/cheese" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( units ) ) ).andExpect( status().isConflict() );

        // make sure nothing changed
        recipe = service.findByName( name );
        assertEquals( 4, recipe.getIngredients().size() );

        // enter invalid units
        units = -1;
        mvc.perform( put( "/api/v1/recipes/IceCoffee/ingredients/Coffee" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( units ) ) ).andExpect( status().isConflict() );

        // make sure nothing changed
        recipe = service.findByName( name );
        assertEquals( "Coffee", recipe.getIngredients().get( 0 ).getIngredient() );
        assertEquals( 20, recipe.getIngredients().get( 0 ).getAmount() );
    }

    @Test
    @Transactional
    public void testEditPriceIngredient () throws Exception {
        // make first recipe
        Assertions.assertEquals( 0, service.findAll().size(), "There should be no Recipes in the CoffeeMaker" );
        final String name = "IceCoffee";
        final Recipe r1 = createRecipe( name, 50, 3, 1, 1, 1 );

        service.save( r1 );
        assertEquals( 1, service.count() );

        // post recipe
        mvc.perform( post( "/api/v1/recipes" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( r1 ) ) );

        Recipe recipe = service.findByName( name );
        assertEquals( 50, recipe.getPrice() );

        // edit price to be 20
        final Integer price = 20;
        mvc.perform( put( "/api/v1/recipes/IceCoffee" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( price ) ) ).andExpect( status().isOk() );

        // check that it updated
        recipe = service.findByName( name );
        assertEquals( 20, recipe.getPrice() );

        // edit random recipe
        mvc.perform( put( "/api/v1/recipes/random" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( price ) ) ).andExpect( status().isNotFound() );
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
