package projet.approche.objet.domain.aggregates;

import java.util.Collection;

import projet.approche.objet.domain.entities.building.Building;
import projet.approche.objet.domain.valueObject.building.BuildingType;
import projet.approche.objet.domain.valueObject.building.exceptions.NotBuiltException;
import projet.approche.objet.domain.valueObject.game.GameStarter;
import projet.approche.objet.domain.valueObject.game.GameState;
import projet.approche.objet.domain.valueObject.game.exceptions.GameAlreadyStarted;
import projet.approche.objet.domain.valueObject.game.exceptions.GameEnded;
import projet.approche.objet.domain.valueObject.game.exceptions.GameNotStarted;
import projet.approche.objet.domain.valueObject.game.exceptions.NotEnoughInhabitants;
import projet.approche.objet.domain.valueObject.game.exceptions.NotEnoughWorkers;
import projet.approche.objet.domain.valueObject.grid.Coordinate;
import projet.approche.objet.domain.valueObject.grid.Grid;
import projet.approche.objet.domain.valueObject.grid.exceptions.NoBuildingHereException;
import projet.approche.objet.domain.valueObject.grid.exceptions.NotFreeException;
import projet.approche.objet.domain.valueObject.grid.exceptions.NotInGridException;
import projet.approche.objet.domain.valueObject.resource.Resource;
import projet.approche.objet.domain.valueObject.resource.ResourceAmount;
import projet.approche.objet.domain.valueObject.resource.ResourceList;
import projet.approche.objet.domain.valueObject.resource.ResourceType;
import projet.approche.objet.domain.valueObject.resource.exceptions.NotEnoughResourceException;
import projet.approche.objet.domain.valueObject.resource.exceptions.ResourceNotFoundException;

public class Manager {
	private static Long count = Long.valueOf(0);
	private static Long idBuildings = Long.valueOf(0);

	private final Long id = ++count;

	private int inhabitants; // number of inhabitants
	private int workers; // number of workers

	private GameState state = GameState.NOTSTARTED;

	private Grid grid; // buildings are in the grid
	private ResourceList resources = new ResourceList();

	/**
	 * Manager of the game, it will manage the buildings, the resources, the
	 * inhabitants and the workers
	 * 
	 * @param gameStarter the game starter
	 */
	public Manager(GameStarter gameStarter, int gridSize) {
		this.inhabitants = gameStarter.inhabitants;
		this.workers = gameStarter.workers;
		this.resources = gameStarter.startingResources;
		this.grid = new Grid(gridSize);
		for (var coordinate : gameStarter.startingBuildings.keySet()) {
			try {
				this.grid = this.grid.setBuilding(
						new Building(gameStarter.startingBuildings.get(coordinate), ++idBuildings), coordinate);
			} catch (NotInGridException | NotFreeException e) {
				// Shouldn't happen since it's a gameStarter otherwise throw an exception
				throw new RuntimeException(e);
			}
		}
		this.grid = new Grid(gridSize);
	}

	public Long getId() {
		return id;
	}

	public int getInhabitants() {
		return inhabitants;
	}

	public int getWorkers() {
		return workers;
	}

	public GameState getState() {
		return state;
	}

	public ResourceList getResources() {
		return resources;
	}

	public void buildBuilding(Building building, Coordinate c) throws NotInGridException, NotFreeException {
		this.grid = this.grid.setBuilding(building, c);
	}

	public void buildBuilding(BuildingType buildingType, Coordinate c) throws NotInGridException, NotFreeException {
		this.grid = this.grid.setBuilding(new Building(buildingType, ++idBuildings), c);
	}

	public void destroyBuilding(Coordinate c) throws NotInGridException, NoBuildingHereException {
		this.grid = this.grid.removeBuilding(c);
	}

	public Grid getGrid() {
		return grid;
	}

	public void addInhabitantToBuilding(Building building, int inhabitantsToAdd)
			throws NotEnoughInhabitants, NotBuiltException {
		if (!building.isBuilt())
			throw new NotBuiltException();
		if (this.inhabitants >= inhabitantsToAdd) {
			building.addInhabitantToBuilding(inhabitantsToAdd);
			this.inhabitants -= inhabitantsToAdd;
		} else {
			throw new NotEnoughInhabitants();
		}
	}

	public void removeInhabitantFromBuilding(Building building, int inhabitantsToRemove)
			throws NotEnoughInhabitants, NotBuiltException {
		if (!building.isBuilt())
			throw new NotBuiltException();
		if (building.getInhabitants() >= inhabitantsToRemove) {
			building.addInhabitantToBuilding(-inhabitantsToRemove);
			this.inhabitants += inhabitantsToRemove;
		} else {
			throw new NotEnoughInhabitants();
		}
	}

	public void addWorkerToBuilding(Building building, int workersToAdd) throws NotEnoughWorkers, NotBuiltException {
		if (!building.isBuilt())
			throw new NotBuiltException();
		if (this.workers >= workersToAdd) {
			building.addWorkerToBuilding(workersToAdd);
			this.workers -= workersToAdd;
		} else {
			throw new NotEnoughWorkers();
		}
	}

	public void removeWorkerFromBuilding(Building building, int workersToRemove)
			throws NotEnoughWorkers, NotBuiltException {
		if (!building.isBuilt())
			throw new NotBuiltException();
		if (building.getWorkers() >= workersToRemove) {
			building.addWorkerToBuilding(-workersToRemove);
			this.workers += workersToRemove;
		} else {
			throw new NotEnoughWorkers();
		}
	}

	public void startGame() throws GameAlreadyStarted, GameEnded {
		if (state == GameState.NOTSTARTED)
			state = GameState.RUNNING;
		else if (state == GameState.ENDED)
			throw new GameEnded();
		else if (state == GameState.RUNNING || state == GameState.PAUSED)
			throw new GameAlreadyStarted();
	}

	public void pauseGame() throws GameNotStarted, GameEnded {
		if (state == GameState.RUNNING) {
			state = GameState.PAUSED;
		} else if (state == GameState.PAUSED) {
			state = GameState.RUNNING;
		} else if (state == GameState.NOTSTARTED) {
			throw new GameNotStarted();
		} else if (state == GameState.ENDED) {
			throw new GameEnded();
		}
	}

	public void endGame() throws GameNotStarted {
		if (state == GameState.RUNNING || state == GameState.PAUSED) {
			state = GameState.ENDED;
		} else if (state == GameState.NOTSTARTED) {
			throw new GameNotStarted();
		}
	}

	/**
	 * @breif Update the game and all its components
	 */
	public void update() {
		if (state == GameState.RUNNING) {
			Collection<Building> buildings = this.grid.getBuildings();
			for (Building building : buildings) {
				resources = building.update(resources);
			}
			// get food needed for the next day
			Resource foodNeeded = this.foodConsumption();
			if (resources.get(ResourceType.FOOD).isGreaterOrEqual(foodNeeded)) {
				// remove food needed for the next day
				try {
					resources = resources.remove(foodNeeded);
				} catch (NotEnoughResourceException | ResourceNotFoundException e) {
					// Shouldn't happen since we checked if there was enough food
					throw new RuntimeException(e);
				}
			} else {
				// TODO : end game or kill inhabitants / kill workers
				System.out.println("GAME OVER");
			}
		}
	}

	public Resource foodConsumption() {
		// minimum amount of food needed for the next day
		int foodNeeded = this.inhabitants + this.workers;
		// add food needed for each building by adding its inhabitants and workers
		for (Building building : this.grid.getBuildings()) {
			foodNeeded += building.getInhabitants() + building.getWorkers();
		}
		return new Resource(ResourceType.FOOD, new ResourceAmount(foodNeeded));
	}

	/**
	 * @param state the state to set
	 */
	public void setState(GameState state) {
		this.state = state;
	}

	/**
	 * @param buildings the grid to set
	 */
	public void setGrid(Grid grid) {
		this.grid = grid;
	}

	/**
	 * @param resources the resources to set
	 */
	public void setResources(ResourceList resources) {
		this.resources = resources;
	}
}
