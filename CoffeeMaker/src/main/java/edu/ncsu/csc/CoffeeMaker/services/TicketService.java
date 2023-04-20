package edu.ncsu.csc.CoffeeMaker.services;

import java.util.ArrayList;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

import edu.ncsu.csc.CoffeeMaker.models.Ticket;
import edu.ncsu.csc.CoffeeMaker.repositories.TicketRepository;

/**
 * The TicketService is used to handle CRUD operations on the Ticket model. In
 * addition to all functionality from `Service`, we also have functionality for
 * retrieving a single Ticket by number.
 *
 * @author Ben Abrams
 * @author Brandon
 *
 */
@Component
@Transactional
public class TicketService extends Service<Ticket, Long> {

    /**
     * TicketRepository, to be autowired in by Spring and provide CRUD
     * operations on Recipe model.
     */
    @Autowired
    private TicketRepository ticketRepository;

    @Override
    protected JpaRepository<Ticket, Long> getRepository () {
        return ticketRepository;
    }

    /**
     * Returns a list of tickets that are associated with the given username.
     *
     * @param username
     *            given users username
     * @return The list of associated tickets
     */
    public List<Ticket> findAllByUsername ( final String username ) {
        final List<Ticket> allTickets = findAll();
        final List<Ticket> userTickets = new ArrayList<>();

        for ( final Ticket ticket : allTickets ) {
            if ( ticket.getCustomer().equals( username ) ) {
                userTickets.add( ticket );
            }
        }

        return userTickets;
    }

}
