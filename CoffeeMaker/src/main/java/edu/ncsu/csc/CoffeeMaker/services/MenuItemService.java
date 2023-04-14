package edu.ncsu.csc.CoffeeMaker.services;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

import edu.ncsu.csc.CoffeeMaker.models.MenuItem;
import edu.ncsu.csc.CoffeeMaker.repositories.MenuItemRepository;

@Component
@Transactional
public class MenuItemService extends Service<MenuItem, Long> {

    @Autowired
    private MenuItemRepository menuItemRepository;

    @Override
    protected JpaRepository<MenuItem, Long> getRepository () {
        return menuItemRepository;
    }

}
