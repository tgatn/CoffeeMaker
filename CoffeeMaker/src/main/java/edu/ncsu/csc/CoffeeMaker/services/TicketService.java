package edu.ncsu.csc.CoffeeMaker.services;

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
     * Find a Ticket with the provided number
     *
     * @param orderNumber
     *            number of the ticket to find
     * @return found ticket, null if none
     */
    public Ticket findByOrderNumber ( final int orderNumber ) {
        return ticketRepository.findByOrderNumber( orderNumber );
    }

}
