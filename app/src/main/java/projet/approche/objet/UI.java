package projet.approche.objet;

import java.io.Console;
import java.util.Scanner;

import projet.approche.objet.domain.aggregates.Manager;
import projet.approche.objet.domain.entities.building.Building;
import projet.approche.objet.domain.valueObject.building.BuildingList;
import projet.approche.objet.domain.valueObject.building.BuildingType;
import projet.approche.objet.domain.valueObject.game.GameStarter;
import projet.approche.objet.domain.valueObject.game.exceptions.GameAlreadyStarted;
import projet.approche.objet.domain.valueObject.game.exceptions.GameEnded;
import projet.approche.objet.domain.valueObject.game.exceptions.GameNotStarted;
import projet.approche.objet.domain.valueObject.resource.Resource;

public class UI {
    
    public static void gameMode() {
		System.out.println("------------------------------------");
		System.out.println("-Choose the game diffculty-");
		System.out.println("-Depending on your choice you will have a varying quantity of resources-");
		System.out.println(
				"- E : EASY : 10 inhabitants + 6 workers -- Food (15)| Gold(15) | Wood (15) -- Wooden Cabin  (2) + House");
		System.out.println(
				"- N : NORMAL : 6 inhabitants + 2 workers -- Food (10)| Gold(10) | Wood (10) -- Wooden Cabin + House ");
		System.out.println("- H : HARD : 2 inhabitants + 2 workers -- Food (5)| Gold(5) | Wood (5) -- Wooden Cabin");
		System.out.println("------------------------------------");
	}

	public static void printGame(Manager game) {

		BuildingList buildings = game.getBuildings();

		// print the resources
		System.out.println("Workers : " + game.getWorkers());
		System.out.println("Inhabitants : " + game.getInhabitants());
		System.out.println("-----------");
		for (Resource r : game.getResources()) {
			System.out.println(r.toString());
		}

		// print the grid
		String[][] grid = game.getGrid();
		System.out.println("-----------");
		System.out.println("Grid : ");
		for (int i = 0; i < game.getGridSize(); i++) {
			for (int j = 0; j < game.getGridSize(); j++) {
				System.out.print(grid[i][j]);
			}
			System.out.println();
		}
	}

	public static void printBuildingTypes() {
		System.out.println("------------------------------------");
		System.out.println("- WC : Wooden Cabin");
		System.out.println("- H : House");
		System.out.println("- AB : Apartment Building");
		System.out.println("- F : Farm");
		System.out.println("- Q : Quarry");
		System.out.println("- LM : Lumber Mill");
		System.out.println("- CP : Cement Plant");
		System.out.println("- SM : Steel Mill");
		System.out.println("- TF : Tool Factory");
		System.out.println("------------------------------------");
	}

	public static Building buildingType(Console console, Scanner scanner, int id) {
		printBuildingTypes();
		String b_type = console.readLine("What type of building do you wish to build/destroy ?");

		Building building;

		switch (b_type.toUpperCase()) {
			case "WC":
				building = new Building(BuildingType.WOODENCABIN, id);
				break;
			case "H":
				building = new Building(BuildingType.HOUSE, id);
				break;
			case "AB":
				building = new Building(BuildingType.APARTMENTBUILDING, id);
				break;
			case "F":
				building = new Building(BuildingType.FARM, id);
				break;
			case "Q":
				building = new Building(BuildingType.QUARRY, id);
				break;
			case "LM":
				building = new Building(BuildingType.LUMBERMILL, id);
				break;
			case "CP":
				building = new Building(BuildingType.CEMENTPLANT, id);
				break;
			case "SM":
				building = new Building(BuildingType.STEELMILL, id);
				break;
			case "TF":
				building = new Building(BuildingType.TOOLFACTORY, id);
				break;
			default:
				System.out.println("Invalid building type. Defaulting to Wooden Cabin.");
				building = new Building(BuildingType.WOODENCABIN, id);
				break;
		}

		return building;
	}

    public static void main(String[] args) throws GameNotStarted, GameAlreadyStarted, GameEnded {

		// Game starter with the default number of inhabitants and workers and buildings
		// depending on the chosen difficulty :
		// EASY : 10 inhabitants + 6 workers -- Food (15)| Gold(15) | Wood (15) --
		// Wooden Cabin (2) + House
		// NORMAL : 6 inhabitants + 2 workers -- Food (10)| Gold(10) | Wood (10) --
		// Wooden Cabin + House
		// HARD : 2 inhabitants + 2 workers -- Food (5)| Gold(5) | Wood (5) -- Wooden
		// Cabin

		Scanner scanner = new Scanner(System.in);
		Console console = System.console();

		GameStarter game;
		String diff;
		int b_id = 3; // building id

		gameMode();
		diff = console.readLine("Which difficulty do you choose ?");

		if (diff.equals("E")) {
			game = GameStarter.EASY;
		} else if (diff.equals("N")) {
			game = GameStarter.NORMAL;
		} else {
			game = GameStarter.HARD;
		}

		Manager GM = new Manager(game, 20);
		GM.startGame();

		while (true) {
			// messages()
			String ui = scanner.nextLine(); // User input

			switch (ui) {
                case "I":
                    // add inhabitants to a building
                    // ask for type of building, and its coordinates on grid
                    break;
				
                case "W":
                    // add workers to a building
                    // ask for type of building, and its coordinates on grid
                    break;

                case "B":
					// ask for type of building, coordinates to put it on grid
					Building building = buildingType(console, scanner, b_id + 1);
					// update grid
					if (GM.updateGrid(1, 1, null)) {
						GM.buildBuilding(building);
					}
                    break;
                case "U":
                    // upgrade a building
                    // ask for type of building, and its coordinates on grid
                    // update building
                    break;
				case "RB":
					// ask for type of building to destroy, and its coordinates on grid
					Building buildingR = buildingType(console, scanner, b_id + 1);
					GM.destroyBuilding(buildingR);
                    break;
				
                case "T":
                    // goes to the next day
                
                

                case "Q":
					System.out.println("You are quitting the game. ");
					GM.endGame();
					break;
				default:
					System.out.println("Input another query. ");
			}
		}

	}
}
