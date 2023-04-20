package edu.ncsu.csc.CoffeeMaker.services;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

import edu.ncsu.csc.CoffeeMaker.models.MenuItem;
import edu.ncsu.csc.CoffeeMaker.repositories.MenuItemRepository;

/**
 * The MenuItemService is used to handle CRUD operations on the Menu Item model.
 *
 * @author bjiang
 *
 */
@Component
@Transactional
public class MenuItemService extends Service<MenuItem, Long> {

    /**
     * MenuItemRepository, to be autowired in by Spring and provide CRUD
     * operations on Menu Item model.
     */
    @Autowired
    private MenuItemRepository menuItemRepository;

    @Override
    protected JpaRepository<MenuItem, Long> getRepository () {
        return menuItemRepository;
    }

}
