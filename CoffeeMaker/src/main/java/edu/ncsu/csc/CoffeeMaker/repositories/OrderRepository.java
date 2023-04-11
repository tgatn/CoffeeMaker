package edu.ncsu.csc.CoffeeMaker.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import edu.ncsu.csc.CoffeeMaker.models.Order;

/**
 * OrderRepository is used to provide CRUD operations for the Order model.
 * Spring will generate appropriate code with JPA.
 *
 * @author Ben Abrams
 *
 */
public interface OrderRepository extends JpaRepository<Order, Long> {

    /**
     * Finds an Order object with the provided order number. Spring will
     * generate code to make this happen.
     *
     * @param orderNumber
     *            orderNumber of the order
     * @return Found order, null if none.
     */
    Order findByOrderNumber ( int orderNumber );

}
