package edu.ncsu.csc.CoffeeMaker.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
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
        System.out.println( "this is the t1: " + TestUtils.asJsonString( t1 ) );

        final String orders = mvc.perform( get( "/api/v1/orders" ).contentType( MediaType.APPLICATION_JSON ) )
                .andReturn().getResponse().getContentAsString();

        assertEquals( 1, service.count() );
        assertTrue( orders.contains( "Coffee" ) );

        ArrayList<Ticket> list = (ArrayList<Ticket>) service.findAll();

        long id = 0;
        for ( final Ticket t : list ) {
            id = t.getId();
        }

        mvc.perform( get( "/api/v1/orders/" + id ).contentType( MediaType.APPLICATION_JSON ) )
                .andExpect( status().isOk() );

        mvc.perform( get( "/api/v1/orders/" + 100 ).contentType( MediaType.APPLICATION_JSON ) )
                .andExpect( status().isNotFound() );

        // make a second customer
        final RegisteredUser customer2 = createUser( "customer2", "password2", "first2", "last2", Role.CUSTOMER );

        mvc.perform( post( "/api/v1/users" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( customer2 ) ) );

        assertEquals( 2, userService.count() );

        // make two tickets for second customer
        final Recipe r2 = createRecipe( "Tea", 2, 1, 1, 1, 1 );
        recipeService.save( r2 );
        final ArrayList<MenuItem> recipeList2 = new ArrayList<MenuItem>();
        final Ticket t2 = new Ticket( recipeList2, "customer2", false, 7833 );
        final ArrayList<MenuItem> recipeList3 = new ArrayList<MenuItem>();
        final Ticket t3 = new Ticket( recipeList3, "customer2", false, 7834 );
        t2.addRecipe( r1 );
        t2.addRecipe( r1 );
        t3.addRecipe( r2 );
        t3.addRecipe( r1 );
        mvc.perform( post( "/api/v1/orders" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( t2 ) ) ).andExpect( status().isOk() );
        mvc.perform( post( "/api/v1/orders" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( t3 ) ) ).andExpect( status().isOk() );

        list = (ArrayList<Ticket>) service.findAll();
        id = list.get( 1 ).getId();

        // fulfill t2
        mvc.perform( post( "/api/v1/makecoffee/orders/" + id ).contentType( MediaType.APPLICATION_JSON ) )
                .andExpect( status().isOk() );

        // get complete orders
        final String completeOrders = mvc
                .perform( get( "/api/v1/orders/complete" ).contentType( MediaType.APPLICATION_JSON ) ).andReturn()
                .getResponse().getContentAsString();

        // this is t2
        assertTrue( completeOrders.contains( "\"totalCost\":2,\"customer\":\"customer2\"" ) );

        // get pending orders
        final String pendingOrders = mvc
                .perform( get( "/api/v1/orders/pending" ).contentType( MediaType.APPLICATION_JSON ) ).andReturn()
                .getResponse().getContentAsString();

        // this is t1
        assertTrue( pendingOrders.contains( "\"totalCost\":1,\"customer\":\"customer1\"" ) );

        // this is t3
        assertTrue( pendingOrders.contains( "\"totalCost\":3,\"customer\":\"customer2\"" ) );

        // get pending orders from customer 2
        final String pendingOrdersCust2 = mvc
                .perform( get( "/api/v1/orders/customer2/pending" ).contentType( MediaType.APPLICATION_JSON ) )
                .andReturn().getResponse().getContentAsString();

        // this is t3
        assertTrue( pendingOrdersCust2.contains( "\"totalCost\":3,\"customer\":\"customer2\"" ) );

        // get pending orders from customer 2
        final String completeOrdersCust2 = mvc
                .perform( get( "/api/v1/orders/customer2/complete" ).contentType( MediaType.APPLICATION_JSON ) )
                .andReturn().getResponse().getContentAsString();

        // this is t2
        assertTrue( completeOrdersCust2.contains( "\"totalCost\":2,\"customer\":\"customer2\"" ) );

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
