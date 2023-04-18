package edu.ncsu.csc.CoffeeMaker.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Controller class for the URL mappings for CoffeeMaker. The controller returns
 * the approprate HTML page in the /src/main/resources/templates folder. For a
 * larger application, this should be split across multiple controllers.
 *
 * @author Kai Presler-Marshall
 */
@Controller
public class MappingController {

    /**
     * On a GET request to /index, the IndexController will return
     * /src/main/resources/templates/index.html.
     *
     * @param model
     *            underlying UI model
     * @return contents of the page
     */
    @GetMapping ( { "/index", "/" } )
    public String index ( final Model model ) {
        return "redirect:/login";
    }

    // /**
    // * On a GET request to /recipe, the RecipeController will return
    // * /src/main/resources/templates/recipe.html.
    // *
    // * @param model
    // * underlying UI model
    // * @return contents of the page
    // */
    // @GetMapping ( { "/recipe", "/recipe.html" } )
    // public String addRecipePage ( final Model model ) {
    // return "recipe";
    // }
    //
    // /**
    // * On a GET request to /addingredient, the RecipeController will return
    // * /src/main/resources/templates/addingredient.html.
    // *
    // * @param model
    // * underlying UI model
    // * @return contents of the page
    // */
    // @GetMapping ( { "/addingredient", "/addingredient.html" } )
    // public String addIngredientPage ( final Model model ) {
    // return "addingredient";
    // }
    //
    // /**
    // * On a GET request to /deleterecipe, the DeleteRecipeController will
    // return
    // * /src/main/resources/templates/deleterecipe.html.
    // *
    // * @param model
    // * underlying UI model
    // * @return contents of the page
    // */
    // @GetMapping ( { "/deleterecipe", "/deleterecipe.html" } )
    // public String deleteRecipeForm ( final Model model ) {
    // return "deleterecipe";
    // }
    //
    // /**
    // * On a GET request to /editrecipe, the EditRecipeController will return
    // * /src/main/resources/templates/editrecipe.html.
    // *
    // * @param model
    // * underlying UI model
    // * @return contents of the page
    // */
    // @GetMapping ( { "/editrecipe", "/editrecipe.html" } )
    // public String editRecipeForm ( final Model model ) {
    // return "editrecipe";
    // }
    //
    // /**
    // * Handles a GET request for inventory. The GET request provides a view to
    // * the client that includes the list of the current ingredients in the
    // * inventory and a form where the client can enter more ingredients to add
    // * to the inventory.
    // *
    // * @param model
    // * underlying UI model
    // * @return contents of the page
    // */
    // @GetMapping ( { "/inventory", "/inventory.html" } )
    // public String inventoryForm ( final Model model ) {
    // return "inventory";
    // }
    //
    /**
     * On a GET request to /makecoffee, the MakeCoffeeController will return
     * /src/main/resources/templates/makecoffee.html.
     *
     * @param model
     *            underlying UI model
     * @return contents of the page
     */
    @GetMapping ( { "/makecoffee", "/makecoffee.html" } )
    public String makeCoffeeForm ( final Model model ) {
        return "makecoffee";
    }

    /**
     * On a GET request to /stafflogin, the MakeCoffeeController will return
     * /src/main/resources/templates/staffLogin.html.
     *
     * @param model
     *            underlying UI model
     * @return contents of the page
     */
    @GetMapping ( { "/stafflogin", "/staffLogin.html" } )
    public String staffLogin ( final Model model ) {
        return "stafflogin";
    }

    /**
     * On a GET request to /customerlogin, the MakeCoffeeController will return
     * /src/main/resources/templates/customerLogin.html.
     *
     * @param model
     *            underlying UI model
     * @return contents of the page
     */
    @GetMapping ( { "/customerlogin", "/customerLogin.html" } )
    public String customerLogin ( final Model model ) {
        return "customerlogin";
    }

    /**
     * On a GET request to /login, the MakeCoffeeController will return
     * /src/main/resources/templates/login.html.
     *
     * @param model
     *            underlying UI model
     * @return contents of the page
     */
    @GetMapping ( { "/login", "/login.html" } )
    public String login ( final Model model ) {
        return "login";
    }

    /**
     * On a GET request to /signup, the MakeCoffeeController will return
     * /src/main/resources/templates/signUp.html.
     *
     * @param model
     *            underlying UI model
     * @return contents of the page
     */
    @GetMapping ( { "/signup", "/signUp.html" } )
    public String signUp ( final Model model ) {
        return "signup";
    }

    /**
     * On a GET request to /customerIndex, the MakeCoffeeController will return
     * /src/main/resources/templates/customerIndex.html.
     *
     * @param model
     *            underlying UI model
     * @return contents of the page
     */
    @GetMapping ( { "/customerIndex", "/customerIndex.html" } )
    public String customerIndex ( final Model model ) {
        return "customerIndex";
    }

    /**
     * On a GET request to /pastOrders, the MakeCoffeeController will return
     * /src/main/resources/templates/pastOrders.html.
     *
     * @param model
     *            underlying UI model
     * @return contents of the page
     */
    @GetMapping ( { "/pastOrders", "/pastOrders.html" } )
    public String pastOrders ( final Model model ) {
        return "pastOrders";
    }

    /**
     * On a GET request to /currentOrders, the MakeCoffeeController will return
     * /src/main/resources/templates/currentOrders.html.
     *
     * @param model
     *            underlying UI model
     * @return contents of the page
     */
    @GetMapping ( { "/currentOrders", "/currentOrders.html" } )
    public String currentOrders ( final Model model ) {
        return "currentOrders";
    }

    /**
     * On a GET request to /currentOrders, the MakeCoffeeController will return
     * /src/main/resources/templates/currentOrders.html.
     *
     * @param model
     *            underlying UI model
     * @return contents of the page
     */
    @GetMapping ( { "/placeOrders", "/placeOrders.html" } )
    public String placeOrders ( final Model model ) {
        return "placeOrders";
    }

    /**
     * On a GET request to /staffIndex, the MakeCoffeeController will return
     * /src/main/resources/templates/staffIndex.html.
     *
     * @param model
     *            underlying UI model
     * @return contents of the page
     */
    @GetMapping ( { "/staffIndex", "/staffIndex.html" } )
    public String staffIndex ( final Model model ) {
        return "staffIndex";
    }

    /**
     * On a GET request to /viewOrders, the MakeCoffeeController will return
     * /src/main/resources/templates/viewOrders.html.
     *
     * @param model
     *            underlying UI model
     * @return contents of the page
     */
    @GetMapping ( { "/viewOrders", "/viewOrders.html" } )
    public String viewOrders ( final Model model ) {
        return "viewOrders";
    }

}
