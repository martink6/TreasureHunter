/**
 * The Town Class is where it all happens.
 * The Town is designed to manage all the things a Hunter can do in town.
 * This code has been adapted from Ivan Turner's original program -- thank you Mr. Turner!
 */

public class Town {
    // instance variables
    private Hunter hunter;
    private Shop shop;
    private Terrain terrain;
    private String printMessage;
    private boolean toughTown;
    private boolean searchedTown;

    /**
     * The Town Constructor takes in a shop and the surrounding terrain, but leaves the hunter as null until one arrives.
     *
     * @param shop The town's shoppe.
     * @param toughness The surrounding terrain.
     */
    public Town(Shop shop, double toughness) {
        this.shop = shop;
        this.terrain = getNewTerrain();

        // the hunter gets set using the hunterArrives method, which
        // gets called from a client class
        hunter = null;

        printMessage = "";

        // higher toughness = more likely to be a tough town
        toughTown = (Math.random() < toughness);

        searchedTown = false;
    }

    public String getLatestNews() {
        return printMessage;
    }

    public boolean isSearchedTown() {
        return searchedTown;
    }

    /**
     * Assigns an object to the Hunter in town.
     *
     * @param hunter The arriving Hunter.
     */
    public void hunterArrives(Hunter hunter) {
        this.hunter = hunter;
        printMessage = String.format("Welcome to town, %s%s%s.", Color.GREEN, hunter.getHunterName(), Color.RESET);

        if (toughTown) {
            printMessage += String.format("\nIt's pretty %srough%s around here, so %swatch yourself%s.", Color.RED, Color.RESET, Color.RED, Color.RESET);
        } else {
            printMessage += String.format("\nWe're just a %ssleepy%s little town with %smild mannered%s folk.", Color.BLUE, Color.RESET, Color.BLUE, Color.RESET);
        }
    }

    /**
     * Handles the action of the Hunter leaving the town.
     *
     * @return true if the Hunter was able to leave town.
     */
    public boolean leaveTown() {
        boolean canLeaveTown = terrain.canCrossTerrain(hunter);
        if (canLeaveTown) {
            String item = terrain.getNeededItem();
            printMessage = String.format("You used your %s%s%s to cross the %s%s%s.", Color.YELLOW, item, Color.RESET, Color.YELLOW, terrain.getTerrainName(), Color.RESET);
            if (checkItemBreak()) {
                hunter.removeItemFromKit(item);
                printMessage += String.format("\nUnfortunately, you lost your %s%s%s.", Color.YELLOW, item, Color.RESET);
            }
            searchedTown = false;

            return true;
        }

        printMessage = String.format("You can't leave town, %s%s%s. You don't have a %s%s%s.", Color.GREEN, hunter.getHunterName(), Color.RESET, Color.YELLOW, terrain.getNeededItem(), Color.RESET);
        return false;
    }

    /**
     * Handles calling the enter method on shop whenever the user wants to access the shop.
     *
     * @param choice If the user wants to buy or sell items at the shop.
     */
    public void enterShop(String choice) {
        shop.enter(hunter, choice);
        TreasureHunter.clearScreen("You left the shop");
    }

    /**
     * Gives the hunter a chance to fight for some gold.<p>
     * The chances of finding a fight and winning the gold are based on the toughness of the town.<p>
     * The tougher the town, the easier it is to find a fight, and the harder it is to win one.
     */
    public void lookForTrouble() {
        double noTroubleChance;
        if (toughTown) {
            noTroubleChance = 0.66;
        } else {
            noTroubleChance = 0.33;
        }

        if (Math.random() > noTroubleChance) {
            printMessage = "You couldn't find any trouble";
        } else {
            printMessage = String.format("You want trouble, %sstranger%s!  You got it!\nOof! Umph! Ow!\n", Color.RED, Color.RESET);
            int goldDiff = (int) (Math.random() * 10) + 1;
            if (Math.random() > noTroubleChance) {
                printMessage += "Okay, stranger! You proved yer mettle. Here, take my gold.";
                printMessage += String.format("\nYou %swon%s the brawl and receive %s%s%s gold.", Color.GREEN, Color.RESET, Color.YELLOW, goldDiff, Color.RESET);
                hunter.changeGold(goldDiff);
            } else {
                printMessage += "That'll teach you to go lookin' fer trouble in MY town! Now pay up!";
                printMessage += String.format("\nYou %slost%s the brawl and pay %s%s%s gold.", Color.RED, Color.RESET, Color.YELLOW, goldDiff, Color.RESET);
                hunter.changeGold(-goldDiff);
            }
        }
    }

    /**
     * Gives the hunter a chance to find some treasure!<p>
     * The chances of finding a treasure worth something is 75%.<p>
     * If the user hunts once, then the option to hunt is not available anymore and returns a message saying that they can no longer hunt for treasure in the town
     */
    public void huntForTreasure() {
        if(!searchedTown) {
            String[] treasures = new String[]{"a crown", "a trophy", "a gem", "dust"}; //in case more treasure is needed, then we can add it to this array
            String treasure = "dust";
            double rand = Math.random();
            if (rand < .25) {
                treasure = treasures[0];
            } else if (rand < .5) {
                treasure = treasures[1];
            } else if (rand < .75) {
                treasure = treasures[2];
            }  //else would have been to set treasure to dust
            if (!treasure.equals("dust")) {
                if (!hunter.hasTreasure(treasure)) {
                    System.out.printf("You found %s%s" + "!", Color.YELLOW, treasure);
                    hunter.addTreasure(treasure);
                } else {
                    System.out.printf("You already found %s%s, sorry!", Color.YELLOW, treasure);
                }
            } else {
                System.out.println("You found dust, sorry!");
            }
            searchedTown = true;
        } else {
            System.out.println("Ye' search treasure in empty soil!");
        }
    }

    public String toString() {
        return String.format("This nice little town is surrounded by %s%s%s.", Color.YELLOW, terrain.getTerrainName(), Color.RESET);
    }

    /**
     * Determines the surrounding terrain for a town, and the item needed in order to cross that terrain.
     *
     * @return A Terrain object.
     */
    private Terrain getNewTerrain() {
        double rnd = Math.random();
        if (rnd < .166) {
            return new Terrain("Mountains", "Rope");
        } else if (rnd < .333) {
            return new Terrain("Ocean", "Boat");
        } else if (rnd < .5) {
            return new Terrain("Plains", "Horse");
        } else if (rnd < .667) {
            return new Terrain("Desert", "Water");
        } else if (rnd < .833) {
            return new Terrain("Jungle", "Machete");
        } else {
            return new Terrain("Marsh", "Boots");
        }
    }

    /**
     * Determines whether a used item has broken.
     *
     * @return true if the item broke.
     */
    private boolean checkItemBreak() {
        double rand = Math.random();
        return (rand < 0.5);
    }
}