package edu.ncsu.csc.CoffeeMaker.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import edu.ncsu.csc.CoffeeMaker.models.Ticket;

/**
 * RecipeRepository is used to provide CRUD operations for the Ticket model.
 * Spring will generate appropriate code with JPA.
 *
 * @author Brandon Jiang (bjiang9)
 *
 */
public interface TicketRepository extends JpaRepository<Ticket, Long> {

    /**
     * Finds a Ticket object with the provided order number. Spring will
     * generate code to make this happen.
     *
     * @param orderNumber
     *            order number of the ticket
     * @return Found ticket, null if none.
     */
    Ticket findByOrderNumber ( int orderNumber );

}
