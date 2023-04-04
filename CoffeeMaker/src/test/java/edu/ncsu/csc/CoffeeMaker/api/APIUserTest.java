package edu.ncsu.csc.CoffeeMaker.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
import edu.ncsu.csc.CoffeeMaker.models.RegisteredUser;
import edu.ncsu.csc.CoffeeMaker.services.UserService;

@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith ( SpringExtension.class )
public class APIUserTest {

    /**
     * MockMvc uses Spring's testing framework to handle requests to the REST
     * API
     */
    private MockMvc               mvc;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private UserService           service;

    /**
     * Sets up the tests.
     */
    @BeforeEach
    public void setup () {
        mvc = MockMvcBuilders.webAppContextSetup( context ).build();

        service.deleteAll();
        service.deleteAll();
    }

    @Test
    @Transactional
    public void testCustomer () throws Exception {
        // ensure there are no customers to begin with
        Assertions.assertEquals( 0, service.findAll().size(), "There should be no Customers in the CoffeeMaker" );

        // make first Customer
        final RegisteredUser customer1 = createUser( "customer1", "password1", "first1", "last1" );

        service.save( customer1 );
        assertEquals( 1, service.count() );

        mvc.perform( post( "/api/v1/users/customers" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( customer1 ) ) );

        // make second staff
        final RegisteredUser customer2 = createUser( "customer2", "password2", "first2", "last2" );

        service.save( customer2 );
        assertEquals( 2, service.count() );

        mvc.perform( post( "/api/v1/users/customers" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( customer2 ) ) );

        // get specific customer
        final String customers = mvc
                .perform( get( "/api/v1/users/customers/customer2" ).contentType( MediaType.APPLICATION_JSON ) )
                .andReturn().getResponse().getContentAsString();

        assertTrue( customers.contains( "customer2" ) );

        // get null customer
        mvc.perform( get( "/api/v1/users/does_not_exist" ).contentType( MediaType.APPLICATION_JSON ) )
                .andExpect( status().isNotFound() );

        // get all customers
        final String customers2 = mvc
                .perform( get( "/api/v1/users/customers" ).contentType( MediaType.APPLICATION_JSON ) ).andReturn()
                .getResponse().getContentAsString();

        assertTrue( customers2.contains( "customer1" ) );
        assertTrue( customers2.contains( "customer2" ) );

        // delete first customer
        mvc.perform( delete( "/api/v1/users/customers/customer1" ).contentType( MediaType.APPLICATION_JSON ) )
                .andExpect( status().isOk() );
        assertEquals( 1, service.count() );
        final String customers3 = mvc
                .perform( get( "/api/v1/users/customers" ).contentType( MediaType.APPLICATION_JSON ) ).andReturn()
                .getResponse().getContentAsString();
        assertFalse( customers3.contains( "customer1" ) );
        assertTrue( customers3.contains( "customer2" ) );

        // delete null customer
        mvc.perform( delete( "/api/v1/users/customers/does_not_exist" ).contentType( MediaType.APPLICATION_JSON ) )
                .andExpect( status().isNotFound() );

    }

    @Test
    @Transactional
    public void testStaff () throws Exception {
        // ensure there are no staffs to begin with
        Assertions.assertEquals( 0, service.findAll().size(), "There should be no Staff in the CoffeeMaker" );

        // make first Staff
        final RegisteredUser staff1 = createUser( "staff1", "password1", "first1", "last1" );

        service.save( staff1 );
        assertEquals( 1, service.count() );

        mvc.perform( post( "/api/v1/users/staff" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( staff1 ) ) );

        // make second Staff
        final RegisteredUser staff2 = createUser( "staff2", "password2", "first2", "last2" );

        service.save( staff2 );
        assertEquals( 2, service.count() );

        mvc.perform( post( "/api/v1/users/staff" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( staff2 ) ) );

        // get specific Staff
        final String staffs = mvc
                .perform( get( "/api/v1/users/staff/staff2" ).contentType( MediaType.APPLICATION_JSON ) ).andReturn()
                .getResponse().getContentAsString();

        assertTrue( staffs.contains( "staff2" ) );

        // get null staff
        mvc.perform( get( "/api/v1/users/staff/does_not_exist" ).contentType( MediaType.APPLICATION_JSON ) )
                .andExpect( status().isNotFound() );

        // get all staff
        final String staffs2 = mvc.perform( get( "/api/v1/users/staff" ).contentType( MediaType.APPLICATION_JSON ) )
                .andReturn().getResponse().getContentAsString();

        assertTrue( staffs2.contains( "staff1" ) );
        assertTrue( staffs2.contains( "staff2" ) );

        // delete first staff
        mvc.perform( delete( "/api/v1/users/staff/staff1" ).contentType( MediaType.APPLICATION_JSON ) )
                .andExpect( status().isOk() );
        assertEquals( 1, service.count() );
        final String staffs3 = mvc.perform( get( "/api/v1/users/staff" ).contentType( MediaType.APPLICATION_JSON ) )
                .andReturn().getResponse().getContentAsString();
        assertFalse( staffs3.contains( "staff1" ) );
        assertTrue( staffs3.contains( "staff2" ) );

        // delete null staff
        mvc.perform( delete( "/api/v1/users/staff/does_not_exist" ).contentType( MediaType.APPLICATION_JSON ) )
                .andExpect( status().isNotFound() );

    }

    private RegisteredUser createUser ( final String username, final String password, final String first,
            final String last ) {
        final RegisteredUser user = new RegisteredUser( username, password );
        user.setFirstName( first );
        user.setLastName( last );

        return user;
    }

}
