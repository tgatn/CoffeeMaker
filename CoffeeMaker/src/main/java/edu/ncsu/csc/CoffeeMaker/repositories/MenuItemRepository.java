package edu.ncsu.csc.CoffeeMaker.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import edu.ncsu.csc.CoffeeMaker.models.MenuItem;

/**
 * MenuRepository is used to provide CRUD operations for the MenuItem model.
 * Spring will generate appropriate code with JPA.
 *
 * @author Brandon Jiang ( bjiang9 )
 *
 */
public interface MenuItemRepository extends JpaRepository<MenuItem, Long> {

}
