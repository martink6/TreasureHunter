import java.util.Scanner;

/**
 * This class is responsible for controlling the Treasure Hunter game.<p>
 * It handles the display of the menu and the processing of the player's choices.<p>
 * It handles all the display based on the messages it receives from the Town object. <p>
 *
 * This code has been adapted from Ivan Turner's original program -- thank you Mr. Turner!
 */

public class TreasureHunter {
    // static variables
    private static final Scanner SCANNER = new Scanner(System.in);

    // instance variables
    private Town currentTown;
    private Hunter hunter;
    private boolean hardMode;

    /**
     * Constructs the Treasure Hunter game.
     */
    public TreasureHunter() {
        // these will be initialized in the play method
        currentTown = null;
        hunter = null;
        hardMode = false;
    }

    /**
     * Starts the game; this is the only public method
     */
    public void play() {
        welcomePlayer();
        enterTown();
        showMenu();
    }

    /**
     * Creates a hunter object at the beginning of the game and populates the class member variable with it.
     */
    private void welcomePlayer() {
        System.out.printf("Welcome to %sTREASURE HUNTER%s!", Color.GREEN, Color.RESET);
        System.out.printf(" Going %shunting%s for the %sbig treasure%s, eh?", Color.RED, Color.RESET, Color.YELLOW, Color.RESET);
        System.out.print(" What's your name, Hunter? ");
        String name = SCANNER.nextLine().toLowerCase();

        // set hunter instance variable
        hunter = new Hunter(name, 10);

        System.out.printf("%sHard%s mode? (%sy%s/%sn%s): ", Color.RED, Color.RESET, Color.GREEN, Color.RESET, Color.RED, Color.RESET);
        String hard = SCANNER.nextLine().toLowerCase();
        if (hard.equals("y")) {
            hardMode = true;
        } else if (hard.equals("test")) {
            hunter.changeGold(96);
            hunter.buyItem("water", 1);
            hunter.buyItem("rope", 1);
            hunter.buyItem("machete", 1);
            hunter.buyItem("boots", 1);
            hunter.buyItem("horse", 1);
            hunter.buyItem("boat", 1);
        }
    }

    /**
     * Creates a new town and adds the Hunter to it.
     */
    private void enterTown() {
        double markdown = 0.25;
        double toughness = 0.4;
        if (hardMode) {
            // in hard mode, you get less money back when you sell items
            markdown = 0.5;

            // and the town is "tougher"
            toughness = 0.75;
        }

        // note that we don't need to access the Shop object
        // outside of this method, so it isn't necessary to store it as an instance
        // variable; we can leave it as a local variable
        Shop shop = new Shop(markdown);

        // creating the new Town -- which we need to store as an instance
        // variable in this class, since we need to access the Town
        // object in other methods of this class
        currentTown = new Town(shop, toughness);

        // calling the hunterArrives method, which takes the Hunter
        // as a parameter; note this also could have been done in the
        // constructor for Town, but this illustrates another way to associate
        // an object with an object of a different class
        currentTown.hunterArrives(hunter);
    }

    /**
     * Displays the menu and receives the choice from the user.<p>
     * The choice is sent to the processChoice() method for parsing.<p>
     * This method will loop until the user chooses to exit.
     */
    private void showMenu() {
        String choice = "";

        while (!choice.equals("x")) {
            System.out.println();
            System.out.println(currentTown.getLatestNews());
            System.out.println("***");
            System.out.println(hunter);
            System.out.println(currentTown);
            System.out.printf("(%sB%s)uy something at the shop.%n", Color.GREEN, Color.RESET);
            System.out.printf("(%sS%s)ell something at the shop.%n", Color.RED, Color.RESET);
            System.out.printf("(%sM%s)ove on to a different town.%n", Color.YELLOW, Color.RESET);
            System.out.printf("(%sL%s)ook for trouble!%n", Color.PURPLE, Color.RESET);
            System.out.printf("Give up the hunt and e(%sX%s)it.%n", Color.CYAN, Color.RESET);
            System.out.println();
            System.out.print("What's your next move? ");
            choice = SCANNER.nextLine().toLowerCase();
            processChoice(choice);
        }
    }

    /**
     * Takes the choice received from the menu and calls the appropriate method to carry out the instructions.
     * @param choice The action to process.
     */
    private void processChoice(String choice) {
        switch (choice) {
            case "b", "s":
                currentTown.enterShop(choice);
                break;
            case "m":
                if (currentTown.leaveTown()) {
                    // This town is going away so print its news ahead of time.
                    System.out.println(currentTown.getLatestNews());
                    enterTown();
                }
                break;
            case "l":
                currentTown.lookForTrouble();
                if (hunter.getGold() <= 0) {
                    System.out.println(currentTown.getLatestNews());
                    System.out.printf("You're out of gold, %s%s%s. You're gonna have to go home.%n", Color.GREEN, hunter.getHunterName(), Color.RESET);
                    processChoice("x");
                }
                break;
            case "x":
                System.out.printf("Fare thee well, %s%s%s !%n", Color.YELLOW, hunter.getHunterName(), Color.RESET);
                System.exit(0);
                break;
            default :
                System.out.printf("Yikes! That's an %sinvalid%s option! Try again.%n", Color.RED, Color.RESET);
                break;
        }
    }
}