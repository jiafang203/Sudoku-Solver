import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class Sudoku {
	private static int boardSize = 0;
	private static int partitionSize = 0;

	public static void main(String[] args) {
		System.out.println("Pick a sudoku puzzel (i.e. sudoku_easy): ");
		Scanner sc = new Scanner(System.in);
		String filename = sc.nextLine();
		File inputFile = new File("src/SudokuProblems/" + filename + ".txt");
		Scanner input = null;
		int[][] vals = null;
		int temp = 0;
		int count = 0;

		try {
			input = new Scanner(inputFile);
			temp = input.nextInt();
			boardSize = temp;
			partitionSize = (int) Math.sqrt(boardSize);
			System.out.println("Boardsize: " + temp + "x" + temp);
			vals = new int[boardSize][boardSize];

			System.out.println("Input:");
			int i = 0;
			int j = 0;
			while (input.hasNext()) {
				temp = input.nextInt();
				count++;
				System.out.print(temp);
				vals[i][j] = temp;
				j++;
				if (j == boardSize) {
					j = 0;
					i++;
					System.out.println();
				}
				if (j == boardSize) {
					break;
				}
			}
			input.close();
		} catch (FileNotFoundException exception) {
			System.out.println("Input file not found: " + filename);
		} catch (IOException e) {
			System.out.println(e);
		}
		if (count != boardSize * boardSize)
			throw new RuntimeException("Incorrect number of inputs.");

		SudokuSolver ss = new SudokuSolver(vals);
		long startTime = System.currentTimeMillis();
		boolean solved = ss.solve();
		long endTime = System.currentTimeMillis();
		StringBuffer sb = new StringBuffer();
		if (!solved) {
			sb.append("No solutions exist.\n");
			System.out.println();
			System.out.println("Output");
			System.out.println("No solutions exist.");
		} else {
			System.out.println();
			System.out.println("Output");
			System.out.println();
			for (int i = 0; i < boardSize; i++) {
				for (int j = 0; j < boardSize; j++) {
					sb.append(" " + vals[i][j] + " ");
					System.out.printf("%3d", vals[i][j]);
				}
				sb.append("\n");
				System.out.println();
			}
		}

		double total = Math.round((endTime - startTime) * 10000.0) / 10000.0;
		sb.append("Time taken: " + total + " ms");
		System.out.println("Time taken: " + total + " ms");
//		BufferedWriter bw = null;
//		FileWriter fw = null;
//
//		try {
//			fw = new FileWriter("src/SudokuProblems/" + filename + "Solution" + ".txt");
//			bw = new BufferedWriter(fw);
//			bw.write(sb.toString());
//		} catch (IOException e) {
//			e.printStackTrace();
//		} finally {
//			try {
//				if (bw != null) {
//					bw.close();
//				}
//				if (fw != null) {
//					fw.close();
//				}
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//		}

	}

}