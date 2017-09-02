import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Stack;

public class SudokuSolver {
	private int[][] solution;
	private Stack<Node> path = new Stack<>();
	private ArrayList<Node> open;
	private ArrayList<Node> closed = new ArrayList<>();
	private int partitionSize;
	private ArrayList<Integer[]> bounds = new ArrayList<>();

	public SudokuSolver(int[][] vals) {
		this.solution = vals;
		this.partitionSize = (int) Math.sqrt(this.solution.length);
		this.populateBounds();
		this.populateOpenNodes();

		// this.debugPrint();
	}

	public boolean solve() {
		boolean guessingMode = false;
		while (!open.isEmpty()) {
			Node node = this.getSureAnswer();
			if (node != null) {
				int row = node.getRow();
				int col = node.getCol();
				if (!guessingMode) {
					solution[row][col] = node.nextOption();
				} else {
					node.nextOption();
					closed.add(node);
				}
				open.remove(open.indexOf(node));
				if (!this.updateOptions(row, col, node)) {
					if (!guessingMode) {
						return false;
					}
					while (true) {
						node = path.pop();
						this.restore(node);
						closed.add(node);
						node.nextOption();
						if (path.isEmpty() && !node.hasOptions()) {
							guessingMode = false;
						}
						if (!this.updateOptions(node.getRow(), node.getCol(), node)) {
							if (!guessingMode) {
								return false;
							}
							continue;
						}
						if (node.hasOptions()) {
							path.push(node);
							guessingMode = true;
						}
						break;

					}
				}
			} else {
				guessingMode = true;
				node = this.getOptimalAnswer(this.getCandidates());
				closed.add(open.remove(open.indexOf(node)));
				node.backUp(copy(open), copy(closed));
				this.updateOptions(node.getRow(), node.getCol(), node);
				path.push(node);

			}
		}

		this.populateSolution();
		return true;
	}

	private void restore(Node node) {
		this.open = node.getOpenHistory();
		this.closed = node.getClosedHistory();
	}

	private static ArrayList<Node> copy(ArrayList<Node> list) {
		ArrayList<Node> copy = new ArrayList<>();
		for (Node node : list) {
			copy.add(node.copy());
		}
		return copy;
	}

	private Node getOptimalAnswer(Node node) {
		int max = -1;
		int option = 0;
		
		ArrayList<Integer> options = node.getOptions();
		for (Integer op : options) {
			int degree = this.degreeOfEffect(node, op);
			if (degree > max) {
				option = op;
			}
		}
		
		node.nextOption(option);
		return node;
	}

	private int degreeOfEffect(Node candidate, int value) {
		int row = candidate.getRow();
		int col = candidate.getCol();
		Integer[] partition = this.bounds.get(candidate.getPartition());
		int rowL = partition[0];
		int rowH = partition[1];
		int colL = partition[2];
		int colH = partition[3];
		int degree = 0;
		for (Node node : this.open) {
			int nodeRow = node.getRow();
			int nodeCol = node.getCol();
			if (nodeRow == row || nodeCol == col
					|| ((nodeRow >= rowL && nodeRow < rowH) && (nodeCol >= colL && nodeCol < colH))) {
				if (node.containsOption(value)) {
					if (node.numOfOptions() == 2) {
						return Integer.MAX_VALUE;
					}
					degree++;
				}
			}
		}
		return degree;
	}

	private Node getCandidates() {
		int min = this.open.get(0).numOfOptions();
		Node result = this.open.get(0);
		for (Node node : this.open) {
			int num = node.numOfOptions();
			if (num == 2) {
				return node;
			}
			if (num < min) {
				min = num;
				result = node;
			}
		}
		return result;
	}

	private Node getSureAnswer() {
		for (Node node : this.open) {
			if (node.numOfOptions() == 1) {
				return node;
			}
		}

		for (int i = 0; i < bounds.size(); i++) {

			ArrayList<Node> partition = new ArrayList<>();
			for (Node node : this.open) {
				if (node.getPartition() == i) {
					partition.add(node);
				}
			}
			HashMap<Integer, ArrayList<Node>> repeats = new HashMap<>();
			for (Node node : partition) {
				ArrayList<Integer> options = node.getOptions();
				for (Integer num : options) {
					if (!repeats.containsKey(num)) {
						repeats.put(num, new ArrayList<>());
					}
					repeats.get(num).add(node);
				}
			}
			for (Integer key : repeats.keySet()) {
				ArrayList<Node> nodes = repeats.get(key);
				if (nodes.size() == 1) {
					Node result = nodes.get(0);
					ArrayList<Integer> options = result.getOptions();
					for (Integer num : options) {
						if (num != key) {
							result.deleteOption(num);
						}
					}
					return result;
				}
			}
		}

		return null;
	}

	private void populateSolution() {
		for (Node node : this.closed) {
			int row = node.getRow();
			int col = node.getCol();
			solution[row][col] = node.getVal();
			if (solution[row][col] == 0) {
				System.out.println("NO VALUE");
				return;
			}
		}
		HashSet<Integer> set = new HashSet<>();
		for (int i = 0; i < solution.length; i++) {
			set.clear();
			for (int j = 0; j < solution.length; j++) {
				if (set.contains(solution[i][j])) {
					System.out.println("WRONG HORIZONTAL");
					return;
				}
				set.add(solution[i][j]);
			}
		}
		set.clear();
		for (int i = 0; i < solution.length; i++) {
			set.clear();
			for (int j = 0; j < solution.length; j++) {
				if (set.contains(solution[j][i])) {
					System.out.println("WRONG VERTICAL");
					return;
				}
				set.add(solution[j][i]);
			}
		}
		set.clear();
		for (int i = 0; i < bounds.size(); i++) {
			set.clear();
			Integer[] bound = bounds.get(i);
			for (int row = bound[0]; row < bound[1]; row++) {
				for (int col = bound[2]; col < bound[3]; col++) {
					if (set.contains(solution[row][col])) {
						System.out.println("WRONG PARTITION");
						return;
					}
					set.add(solution[row][col]);
				}
			}

		}
	}

	private void populateOpenNodes() {
		open = new ArrayList<>();
		for (int i = 0; i < bounds.size(); i++) {
			Integer[] bound = bounds.get(i);
			for (int row = bound[0]; row < bound[1]; row++) {
				for (int col = bound[2]; col < bound[3]; col++) {
					if (solution[row][col] == 0) {
						ArrayList<Integer> options = new ArrayList<>();
						for (int k = 1; k <= solution.length; k++) {
							options.add(k);
						}
						open.add(new Node(row, col, options, i));
					}
				}
			}

		}

		this.initializeOptions();
	}

	private boolean updateOptions(int row, int col, Node target) {
		Integer[] partition = this.bounds.get(target.getPartition());
		int rowL = partition[0];
		int rowH = partition[1];
		int colL = partition[2];
		int colH = partition[3];
		int value = target.getVal();
		for (Node node : this.open) {
			int nodeRow = node.getRow();
			int nodeCol = node.getCol();
			if (nodeRow == row || nodeCol == col
					|| ((nodeRow >= rowL && nodeRow < rowH) && (nodeCol >= colL && nodeCol < colH))) {

				if (!node.deleteOption(value)) {
					return false;
				}
			}
		}
		return true;
	}

	private void initializeOptions() {
		for (int i = 0; i < bounds.size(); i++) {
			Integer[] bound = bounds.get(i);
			for (int row = bound[0]; row < bound[1]; row++) {
				for (int col = bound[2]; col < bound[3]; col++) {
					if (solution[row][col] != 0) {
						updateOptions(row, col, new Node(row, col, solution[row][col], i));
					}
				}
			}

		}
	}

	private void populateBounds() {
		for (int i = 0; i < partitionSize; i++) {
			for (int j = 0; j < partitionSize; j++) {
				Integer[] partition = new Integer[4];
				int rowL = i * partitionSize;
				int rowH = rowL + partitionSize;
				int colL = j * partitionSize;
				int colH = colL + partitionSize;
				partition[0] = rowL;
				partition[1] = rowH;
				partition[2] = colL;
				partition[3] = colH;
				bounds.add(partition);
			}
		}
	}

	private void debugPrint() {
		for (Node node : this.open) {
			System.out.println("Row: " + node.getRow() + " Col: " + node.getCol() + " Options: " + node.getOptions());
		}
	}
}
