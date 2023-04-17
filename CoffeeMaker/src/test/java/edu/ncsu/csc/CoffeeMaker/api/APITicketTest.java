package edu.ncsu.csc.CoffeeMaker.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;

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
import edu.ncsu.csc.CoffeeMaker.models.Inventory;
import edu.ncsu.csc.CoffeeMaker.models.MenuItem;
import edu.ncsu.csc.CoffeeMaker.models.Recipe;
import edu.ncsu.csc.CoffeeMaker.models.RegisteredUser;
import edu.ncsu.csc.CoffeeMaker.models.Ticket;
import edu.ncsu.csc.CoffeeMaker.models.User.Role;
import edu.ncsu.csc.CoffeeMaker.services.RecipeService;
import edu.ncsu.csc.CoffeeMaker.services.TicketService;
import edu.ncsu.csc.CoffeeMaker.services.UserService;

@ExtendWith ( SpringExtension.class )
@SpringBootTest
@AutoConfigureMockMvc
public class APITicketTest {

    /**
     * MockMvc uses Spring's testing framework to handle requests to the REST
     * API
     */
    private MockMvc               mvc;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private TicketService         service;
    @Autowired
    private UserService           userService;
    @Autowired
    private RecipeService         recipeService;

    /**
     * Sets up the tests.
     */
    @BeforeEach
    public void setup () {
        mvc = MockMvcBuilders.webAppContextSetup( context ).build();

        service.deleteAll();
        userService.deleteAll();
    }

    @Test
    @Transactional
    public void testAPITicket () throws Exception {
        // ensure there are no tickets to begin with
        Assertions.assertEquals( 0, service.findAll().size(), "There should be no Tickets in the CoffeeMaker" );

        // populate the inventory
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

        // make a customer
        final RegisteredUser customer1 = createUser( "customer1", "password1", "first1", "last1", Role.CUSTOMER );

        mvc.perform( post( "/api/v1/users" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( customer1 ) ) );

        assertEquals( 1, userService.count() );

        // make an order and add to data base
        final Recipe r1 = createRecipe( "Coffee", 1, 1, 1, 1, 1 );
        recipeService.save( r1 );

        final ArrayList<MenuItem> recipeList = new ArrayList<MenuItem>();
        final Ticket t1 = new Ticket( recipeList, "customer1", false, 7832 );
        t1.addRecipe( r1 );
        assertEquals( 7832, t1.getId() );
        assertEquals( "customer1", t1.getCustomer() );

        mvc.perform( post( "/api/v1/orders" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( t1 ) ) ).andExpect( status().isOk() );

        final String orders = mvc.perform( get( "/api/v1/orders" ).contentType( MediaType.APPLICATION_JSON ) )
                .andReturn().getResponse().getContentAsString();

        assertEquals( 1, service.count() );
        assertTrue( orders.contains( "Coffee" ) );

        // make first Customer
        final RegisteredUser njdksa = createUser( "customer1", "password1", "first1", "last1", Role.CUSTOMER );

        mvc.perform( post( "/api/v1/users" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( customer1 ) ) );

        assertEquals( 1, userService.count() );

        // make second customer
        final RegisteredUser customer2 = createUser( "customer2", "password2", "first2", "last2", Role.CUSTOMER );

        mvc.perform( post( "/api/v1/users" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( customer2 ) ) ).andExpect( status().isOk() );

        assertEquals( 2, userService.count() );

        // get specific customer
        final String customers = mvc
                .perform( get( "/api/v1/users/customer2" ).contentType( MediaType.APPLICATION_JSON ) ).andReturn()
                .getResponse().getContentAsString();

        assertTrue( customers.contains( "customer2" ) );

        // get null customer
        mvc.perform( get( "/api/v1/users/does_not_exist" ).contentType( MediaType.APPLICATION_JSON ) )
                .andExpect( status().isNotFound() );

        // get all customers
        final String customers2 = mvc.perform( get( "/api/v1/users" ).contentType( MediaType.APPLICATION_JSON ) )
                .andReturn().getResponse().getContentAsString();

        assertTrue( customers2.contains( "customer1" ) );
        assertTrue( customers2.contains( "customer2" ) );

        // check passwords
        // check valid password
        mvc.perform( post( "/api/v1/customer/login/customer2" ).contentType( MediaType.APPLICATION_JSON )
                .content( "password2" ) ).andExpect( status().isOk() );
        // check invalid password
        mvc.perform( post( "/api/v1/customer/login/customer2" ).contentType( MediaType.APPLICATION_JSON )
                .content( "password1" ) ).andExpect( status().isUnauthorized() );
        // check invalid username
        mvc.perform( post( "/api/v1/customer/login/random" ).contentType( MediaType.APPLICATION_JSON )
                .content( "password1" ) ).andExpect( status().isNotFound() );
        // check staff login
        mvc.perform( post( "/api/v1/staff/login/customer2" ).contentType( MediaType.APPLICATION_JSON )
                .content( "password2" ) ).andExpect( status().isUnauthorized() );

        // check invalid password

        // delete first customer
        mvc.perform( delete( "/api/v1/users/customer1" ).contentType( MediaType.APPLICATION_JSON ) )
                .andExpect( status().isOk() );
        assertEquals( 1, userService.count() );
        final String customers3 = mvc.perform( get( "/api/v1/users" ).contentType( MediaType.APPLICATION_JSON ) )
                .andReturn().getResponse().getContentAsString();
        assertFalse( customers3.contains( "customer1" ) );
        assertTrue( customers3.contains( "customer2" ) );

        // delete null customer
        mvc.perform( delete( "/api/v1/users/does_not_exist" ).contentType( MediaType.APPLICATION_JSON ) )
                .andExpect( status().isNotFound() );

        service.deleteAll();

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

    private RegisteredUser createUser ( final String username, final String password, final String first,
            final String last, final Role role ) {
        final RegisteredUser user = new RegisteredUser( username, password );
        user.setFirstName( first );
        user.setLastName( last );
        user.setRole( role );

        return user;
    }

}
