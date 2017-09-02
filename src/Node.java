import java.util.ArrayList;

public class Node implements Comparable<Node>{
	private int row;
	private int col;
	private int value;
	private int partition;
	private ArrayList<Integer> options;
	private ArrayList<Node> openHistory;
	private ArrayList<Node> closedHistory;

	public Node(int row, int col, int val, int partition) {
		this.row = row;
		this.col = col;
		this.value = val;
		this.partition = partition;
	}

	public Node(int row, int col, ArrayList<Integer> options, int partition) {
		this.row = row;
		this.col = col;
		this.options = options;
		this.partition = partition;
	}

	public boolean hasOptions() {
		return !this.options.isEmpty();
	}

	public int getPartition() {
		return this.partition;
	}

	public int getRow() {
		return this.row;
	}

	public int getCol() {
		return this.col;
	}

	public int getVal() {
		return this.value;
	}

	public int numOfOptions() {
		return this.options.size();
	}

	public int nextOption() {
		this.value = this.options.remove(this.options.size() - 1);
		return this.value;
	}

	public int nextOption(int value) {
		this.options.remove(this.options.indexOf(value));
		this.value = value;
		return value;
	}

	public void addOption(int num) {
		if (!options.contains(num)) {
			options.add(num);
		}
	}

	public boolean containsOption(int num) {
		return options.contains(num);
	}

	public ArrayList<Integer> getOptions() {
		ArrayList<Integer> result = new ArrayList<>();
		for (Integer i : this.options) {
			result.add(i);
		}
		return new ArrayList<>(result);
	}

	public boolean deleteOption(int num) {
		for (int i = 0; i < this.options.size(); i++) {
			if (options.get(i).equals(num)) {
				this.options.remove(i);
			}
		}
		return this.hasOptions();
	}
	
	public Node copy() {
		Node node = new Node(this.row, this.col, this.getOptions(), this.partition);
		node.value = this.value;
		return node;
	}
	
	public ArrayList<Node> getOpenHistory() {
		return this.openHistory;
	}
	
	public ArrayList<Node> getClosedHistory() {
		return this.closedHistory;
	}
	
	public void backUp(ArrayList<Node> open, ArrayList<Node> closed) {
		
		this.openHistory = open;
		this.closedHistory = closed;
	}

	public void resetOptions() {
		this.options.clear();
	}

	@Override
	public int compareTo(Node other) {
		return this.numOfOptions() - other.numOfOptions();
	}
}
