package edu.ncsu.csc.CoffeeMaker.controllers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import edu.ncsu.csc.CoffeeMaker.models.RegisteredUser;
import edu.ncsu.csc.CoffeeMaker.models.User;
import edu.ncsu.csc.CoffeeMaker.services.UserService;

/**
 * This is the controller that holds the REST endpoints that handle CRUD
 * operations for Users.
 *
 * Spring will automatically convert all of the ResponseEntity and List results
 * to JSON
 *
 * @author Ben Abrams
 *
 */
@SuppressWarnings ( { "unchecked", "rawtypes" } )
@RestController
public class APIUserController extends APIController {

    /**
     * UserService object, to be autowired in by Spring to allow for
     * manipulating the User model
     */
    @Autowired
    private UserService service;

    /**
     * REST API method to provide login access for customers
     *
     * @param username
     *            the user trying to login
     * @param password
     *            the entered password
     * @param response
     *            the response
     * @param session
     *            the login session
     *
     * @return response to the request
     */
    @PostMapping ( BASE_PATH + "/customer/login/{username}" )
    public ResponseEntity customerLogin ( @PathVariable final String username, @RequestBody final String password,
            final HttpServletResponse response, final HttpSession session ) {
        final RegisteredUser dbUser = service.findByName( username );
        if ( dbUser == null ) {
            return new ResponseEntity( errorResponse( "No User found with username " + username ),
                    HttpStatus.NOT_FOUND );
        }
        // If the found user is not a customer then deny access
        if ( dbUser.getRole() != User.Role.CUSTOMER ) {
            return new ResponseEntity( errorResponse( "No Customer found with username " + username ),
                    HttpStatus.UNAUTHORIZED );
        }
        // If the user enters an invalid password deny access
        if ( !dbUser.checkPassword( password ) ) {
            return new ResponseEntity( errorResponse( "Invalid password" ), HttpStatus.UNAUTHORIZED );
        }
        session.setAttribute( "username", dbUser.getUsername() );
        session.setAttribute( "role", dbUser.getRole() );

        return new ResponseEntity( successResponse( username + " successfully logged in" ), HttpStatus.OK );

    }

    /**
     * REST API method to provide login access for staff
     *
     * @param username
     *            the user trying to login
     * @param password
     *            the entered password
     * @param response
     *            the response
     * @param session
     *            the login session
     *
     * @return response to the request
     */
    @PostMapping ( BASE_PATH + "/staff/login/{username}" )
    public ResponseEntity staffLogin ( @PathVariable final String username, @RequestBody final String password,
            final HttpServletResponse response, final HttpSession session ) {
        final RegisteredUser dbUser = service.findByName( username );
        if ( dbUser == null ) {
            return new ResponseEntity( errorResponse( "No User found with username " + username ),
                    HttpStatus.NOT_FOUND );
        }
        // If the found user is not a staff or manager then deny access
        if ( dbUser.getRole() != User.Role.MANAGER && dbUser.getRole() != User.Role.EMPLOYEE ) {
            return new ResponseEntity( errorResponse( "No Staff found with username " + username ),
                    HttpStatus.UNAUTHORIZED );
        }
        if ( !dbUser.checkPassword( password ) ) {
            return new ResponseEntity( errorResponse( "Invalid password" ), HttpStatus.UNAUTHORIZED );
        }
        session.setAttribute( "username", dbUser.getUsername() );
        session.setAttribute( "role", dbUser.getRole() );

        return new ResponseEntity( dbUser.getRole(), HttpStatus.OK );
    }

    /**
     * REST API method to provide GET access to all users in the system
     *
     * @return JSON representation of all users
     */
    @GetMapping ( BASE_PATH + "/users" )
    public List<RegisteredUser> getUsers () {
        return service.findAll();
    }

    /**
     * REST API method to provide GET access to a specific user, as indicated by
     * the path variable provided (the name of the desired user)
     *
     * @param username
     *            users username
     * @return response to the request
     */
    @GetMapping ( BASE_PATH + "/users/{username}" )
    public ResponseEntity getUser ( @PathVariable ( "username" ) final String username ) {
        final RegisteredUser user = service.findByName( username );
        return null == user
                ? new ResponseEntity( errorResponse( "No User found with username " + username ), HttpStatus.NOT_FOUND )
                : new ResponseEntity( user, HttpStatus.OK );

    }

    /**
     * REST API method to provide POST access to the User model. This is used to
     * create a new User by automatically converting the JSON RequestBody
     * provided to a User object. Invalid JSON will fail.
     *
     * @param user
     *            The valid user to be saved.
     * @return ResponseEntity indicating success if the User could be saved to
     *         the database, or an error if it could not be
     */
    @PostMapping ( BASE_PATH + "/users" )
    public ResponseEntity createUser ( @RequestBody final RegisteredUser user ) {
        if ( null != service.findByName( user.getUsername() ) ) {
            return new ResponseEntity( errorResponse( "User with the name " + user.getUsername() + " already exists" ),
                    HttpStatus.CONFLICT );
        }
        else {
            service.save( user );
            return new ResponseEntity( successResponse( user.getUsername() + " successfully created" ), HttpStatus.OK );
        }

    }

    /**
     * REST API method to provide POST access to the User guest model. This is
     * used to create a new guest User
     *
     * @param session
     *            the session
     *
     * @return ResponseEntity indicating success if the User could be saved to
     *         the database, or an error if it could not be
     */
    @PostMapping ( BASE_PATH + "/users/guest" )
    public ResponseEntity createGuestUser ( final HttpSession session ) {
        if ( null != service.findByName( "TEMP GUEST" ) ) {
            return new ResponseEntity( errorResponse( "Could not create guest" ), HttpStatus.CONFLICT );
        }
        RegisteredUser user = new RegisteredUser();
        user.setRole( User.Role.GUEST );
        user.setUsername( "TEMP GUEST" );
        service.save( user );
        user = service.findByName( "TEMP GUEST" );
        user.setUsername( "Guest" + user.getId() );
        service.save( user );
        session.setAttribute( "username", user.getUsername() );
        session.setAttribute( "role", user.getRole() );
        return new ResponseEntity( successResponse( user.getUsername() + "successfully created" ), HttpStatus.OK );

    }

    /**
     * REST API method to provide GET access to the a session.
     *
     * @param request
     *            the servlet request
     * @return ResponseEntity indicating success if the session exists or an
     *         error if it does not
     */
    @GetMapping ( BASE_PATH + "/session" )
    public ResponseEntity< ? > getSessionInfo ( final HttpServletRequest request ) {
        final HttpSession session = request.getSession( false );
        if ( session == null ) {
            return new ResponseEntity<>( errorResponse( "Session not found" ), HttpStatus.NOT_FOUND );
        }
        final Map<String, Object> sessionData = new HashMap<>();
        sessionData.put( "username", session.getAttribute( "username" ) );
        sessionData.put( "role", session.getAttribute( "role" ) );
        return new ResponseEntity<>( sessionData, HttpStatus.OK );
    }

    /**
     * REST API method to provide GET access to the a staff session.
     *
     * @param request
     *            the servlet request
     * @return ResponseEntity indicating success if the session exists or an
     *         error if it does not, or if the user is not a staff member
     */
    @GetMapping ( BASE_PATH + "/session/staff" )
    public ResponseEntity< ? > getSessionInfoManager ( final HttpServletRequest request ) {
        final HttpSession session = request.getSession( false );
        if ( session == null ) {
            return new ResponseEntity<>( errorResponse( "Session not found" ), HttpStatus.NOT_FOUND );
        }
        if ( session.getAttribute( "role" ) != User.Role.MANAGER
                && session.getAttribute( "role" ) != User.Role.EMPLOYEE ) {
            return new ResponseEntity<>( session.getAttribute( "role" ), HttpStatus.UNAUTHORIZED );
        }
        final Map<String, Object> sessionData = new HashMap<>();
        sessionData.put( "username", session.getAttribute( "username" ) );
        sessionData.put( "role", session.getAttribute( "role" ) );

        return new ResponseEntity<>( sessionData, HttpStatus.OK );
    }

    /**
     * REST API method to allow deleting a User from the CoffeeMaker's system,
     * by making a DELETE request to the API endpoint and indicating the User to
     * delete (as a path variable)
     *
     * @param username
     *            The username of the User to delete
     * @return Success if the User could be deleted; an error if the User does
     *         not exist
     */
    @DeleteMapping ( BASE_PATH + "/users/{username}" )
    public ResponseEntity deleteUser ( @PathVariable final String username ) {
        final RegisteredUser user = service.findByName( username );
        if ( null == user ) {
            return new ResponseEntity( errorResponse( "No Customer found for name " + username ),
                    HttpStatus.NOT_FOUND );
        }
        service.delete( user );

        return new ResponseEntity( successResponse( username + " was deleted successfully" ), HttpStatus.OK );
    }

    /**
     * REST API method to provide logout functionality for users
     *
     * @param session
     *            the current user's HttpSession
     * @param response
     *            the current HttpServletResponse
     * @return response to the request
     */
    @PostMapping ( BASE_PATH + "/logout" )
    public ResponseEntity logout ( final HttpSession session, final HttpServletResponse response ) {
        // Invalidate the current session and remove the user's session
        // attribute
        session.removeAttribute( "username" );
        session.invalidate();
        // Clear the authentication cookie
        final Cookie cookie = new Cookie( "username", "" );
        cookie.setMaxAge( 0 );
        cookie.setDomain( "localhost" );
        cookie.setPath( "/" );
        response.addCookie( cookie );

        return new ResponseEntity( successResponse( "User successfully logged out" ), HttpStatus.OK );
    }

}
