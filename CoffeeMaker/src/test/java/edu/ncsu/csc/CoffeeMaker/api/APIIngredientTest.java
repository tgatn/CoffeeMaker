package edu.ncsu.csc.CoffeeMaker.api;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
import edu.ncsu.csc.CoffeeMaker.services.IngredientService;

@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith ( SpringExtension.class )
public class APIIngredientTest {

    /**
     * MockMvc uses Spring's testing framework to handle requests to the REST
     * API
     */
    private MockMvc               mvc;

    @Autowired
    private IngredientService     service;

    @Autowired
    private WebApplicationContext context;

    @BeforeEach
    public void setup () {
        mvc = MockMvcBuilders.webAppContextSetup( context ).build();

        service.deleteAll();
    }

    @Test
    @Transactional
    public void testCreateIngredient () throws Exception {
        // adds ingredient
        final Ingredient i = new Ingredient( "coffee", 10 );
        mvc.perform( post( "/api/v1/ingredients" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( i ) ) ).andExpect( status().isOk() );
        assertEquals( 1, service.count() );

        // adds ingredient with same name
        mvc.perform( post( "/api/v1/ingredients" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( i ) ) ).andExpect( status().isConflict() );

    }

    @Test
    @Transactional
    public void testGetIngredient () throws Exception {
        // create ingredient
        final Ingredient i = new Ingredient( "coffee", 10 );
        mvc.perform( post( "/api/v1/ingredients" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( i ) ) ).andExpect( status().isOk() );

        final Ingredient i2 = new Ingredient( "milk", 10 );
        mvc.perform( post( "/api/v1/ingredients" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( i2 ) ) ).andExpect( status().isOk() );

        assertEquals( 2, service.count() );

        // get specific ingredient
        final String ingredient = mvc.perform( get( "/api/v1/ingredients/coffee" )
                .contentType( MediaType.APPLICATION_JSON ).content( TestUtils.asJsonString( i ) ) ).andReturn()
                .getResponse().getContentAsString();

        assertTrue( ingredient.contains( "coffee" ) );

        // get null ingredient
        mvc.perform( get( "/api/v1/ingredients/does_not_exist" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( i ) ) ).andExpect( status().isNotFound() );

        // get all ingredients
        final String ingredients = mvc.perform( get( "/api/v1/ingredients" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( i ) ) ).andReturn().getResponse().getContentAsString();

        assertTrue( ingredients.contains( "coffee" ) );
        assertTrue( ingredients.contains( "milk" ) );
    }

    @Test
    @Transactional
    public void testDeleteIngredient () throws Exception {
        // create ingredient
        final Ingredient i = new Ingredient( "coffee", 10 );
        mvc.perform( post( "/api/v1/ingredients" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( i ) ) );
        assertEquals( 1, service.count() );

        // get ingredient
        final String ingredient = mvc.perform( get( "/api/v1/ingredients/coffee" )
                .contentType( MediaType.APPLICATION_JSON ).content( TestUtils.asJsonString( i ) ) ).andReturn()
                .getResponse().getContentAsString();

        assertTrue( ingredient.contains( "coffee" ) );
        assertEquals( 1, service.count() );

        // delete ingredient
        mvc.perform( delete( "/api/v1/ingredients/coffee" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( i ) ) );
        assertEquals( 0, service.count() );

        mvc.perform( delete( "/api/v1/ingredients/coffee" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( i ) ) ).andExpect( status().isNotFound() );

    }
}
