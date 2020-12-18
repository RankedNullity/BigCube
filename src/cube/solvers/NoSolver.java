package cube.solvers;

import cube.Action;
import cube.GameCube;

/**
 * Simple bogus solver for testing animation displays
 * 
 * @author Jaron Wang
 *
 */
public class NoSolver implements Solver {
	public NoSolver() {
	}

	
	@Override
	public String getType() {
		return "No Solver";
	}

	@Override
	public Action getBestAction() {
		return null;
	}

}
