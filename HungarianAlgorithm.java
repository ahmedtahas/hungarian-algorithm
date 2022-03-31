import java.util.Arrays;

public class HungarianAlgorithm
{
	public static void main(String[] args)
	{
		HungarianAlgorithm object = new HungarianAlgorithm();

		int [][] costArray = new int[][]{{82,83,69,92}, {77,37,49,92}, {11,69,5,86}, {8,9,98,23}};

		int [] assignedValues = object.run(costArray);

		System.out.println(Arrays.toString(assignedValues));
	}

	/**
	 * @param costArray - Cost Array
	 * @return assignedValues
	 */
	public int[] run(int [][] costArray)
	{

//		uncomment to maximize gain
		/*
		int max = 0;
		for (int[] ints : costArray)
			for (int anInt : ints)
				if (anInt > max)
					max = anInt;

		for(int i = 0; i < costArray.length; i++)
			for(int j = 0; j < costArray[i].length; j++)
				costArray[i][j] = max - costArray[i][j];
		*/

		printArray(costArray);

		costArray = subtractMinFromRows(costArray);

		printArray(costArray);

		costArray = subtractMinFromColumns(costArray);

		printArray(costArray);

		Pair covers = drawLines(costArray);

		while (countLines(covers.rows, covers.columns) < costArray.length)
		{
			costArray = refactorArray(costArray, covers.rows, covers.columns);
			covers = drawLines(costArray);

			printArray(costArray);
		}
		costArray = refactorArray(costArray, covers.rows, covers.columns);

		printArray(costArray);

		return assignValues(costArray);
	}

	/**
	 * @param costArray - Cost Array
	 */
	void printArray(int[][] costArray)
	{
		for(int[] row : costArray)
		{
			for (int element : row)
				System.out.print(element + ", ");
			System.out.println();
		}
	}

	/**
	 * Counts the covered lines
	 *
	 * @param rows - marked rows
	 * @param columns - marked columns
	 * @return count
	 */
	int countLines(int[] rows, int[] columns)
	{
		int count = 0;
		for(int i = 0; i < rows.length; i++)
		{
			if (rows[i] == -1)
				count++;
			if(columns[i] != -1)
				count++;
		}
		return count;
	}

	/**
	 * Assigns values
	 *
	 * @param costArray - Cost Array
	 * @return assignedValues
	 */
	public int[] assignValues(int[][] costArray)
	{
		boolean[][] deprecatedLines = new boolean[costArray.length][costArray[0].length];

		for (boolean[] booleans : deprecatedLines) Arrays.fill(booleans, true);

		int[] zerosInRows = countZeros(deprecatedLines, costArray);
		int[] assignedValues = new int[costArray.length];

		int min, indexOfMin = 0;

		do {
			min = Integer.MAX_VALUE;
			for (int i : zerosInRows)
				if (i < min && i != 0)
					min = i;
			for (int i = zerosInRows.length - 1; i >= 0; i--)
				if (zerosInRows[i] == min)
					indexOfMin = i;
			for (int j = 0; j < costArray[indexOfMin].length; j++) {
				if (costArray[indexOfMin][j] == 0 && deprecatedLines[indexOfMin][j]) {
					deprecatedLines = deprecateRowAndColumn(deprecatedLines, costArray, indexOfMin, j);
					assignedValues[indexOfMin] = j;
					break;
				}
			}
			zerosInRows = countZeros(deprecatedLines, costArray);
		} while (countTrues(deprecatedLines) != 1);

		for(int i = 0; i < deprecatedLines.length; i++)
			for(int j = 0; j < deprecatedLines[i].length; j++)
				if(deprecatedLines[i][j])
					assignedValues[i] = j;

		return assignedValues;
	}

	/**
	 * Counts the number of true cells in the boolean array
	 *
	 * @param deprecatedLines - Boolean Array that stores covered lines
	 * @return count
	 */
	int countTrues(boolean [][] deprecatedLines)
	{
		int count = 0;
		for (boolean[] booleans : deprecatedLines)
			for (boolean cell : booleans)
				if (cell)
					count++;

		return count;
	}

	/**
	 * Refactors row and column of the selected cell to false
	 * in the boolean array
	 *
	 * @param deprecatedLines - Boolean Array that stores covered lines
	 * @param costArray - Cost Array
	 * @param row - row index
	 * @param column - column index
	 * @return deprecatedLines
	 */
	public boolean[][] deprecateRowAndColumn(boolean[][] deprecatedLines, int[][] costArray, int row, int column)
	{

		for(int i = 0; i < costArray.length; i++)
			for (int j = 0; j < costArray[i].length; j++)
				if (j == column || i == row)
					deprecatedLines[i][j] = false;
		return deprecatedLines;
	}

	/**
	 * Returns an integer array that stores
	 * the number of zeros in every row
	 *
	 * @param deprecatedLines - Boolean Array that stores covered lines
	 * @param costArray - Cost Array
	 * @return zerosInRows
	 */
	public int[] countZeros(boolean[][] deprecatedLines, int[][] costArray)
	{
		int[] zerosInRows = new int[costArray.length];
		Arrays.fill(zerosInRows, 0);
		for(int i = 0; i < costArray.length; i++)
			for (int j = 0; j < costArray[i].length; j++)
				if (costArray[i][j] == 0 && deprecatedLines[i][j])
					zerosInRows[i]++;
		return zerosInRows;
	}

	/**
	 * Subtracts the minimum uncovered element from
	 * uncovered rows, then adds it to covered columns
	 * @param costArray - Cost Array
	 * @param rows - marked rows
	 * @param columns - marked columns
	 * @return costArray
	 */
	int[][] refactorArray(int[][] costArray, int[] rows, int[] columns)
	{
		int min = Integer.MAX_VALUE;
		for(int i = 0; i < costArray.length; i++)
			for(int j = 0; j < costArray[i].length; j++)
				if(costArray[i][j] < min && rows[i] != -1 && columns[j] == -1)
					min = costArray[i][j];

		for(int i = 0; i < costArray.length; i++)
			for (int j = 0; j < costArray[i].length; j++)
				if(rows[i] != -1)
					costArray[i][j] -= min;

		for(int i = 0; i < costArray.length; i++)
			for (int j = 0; j < costArray[i].length; j++)
				if(columns[j] != -1)
					costArray[i][j] += min;

		return costArray;
	}


	/**
	 * Returns a Pair object for covered rows and columns
	 *
	 * @param costArray - Cost Array
	 * @return Pair
	 */
	Pair drawLines(int[][] costArray) {

		int[] assignRows = new int[costArray.length];
		int[] coverRows = new int[costArray.length];
		int[] assignColumns = new int[costArray.length];
		int[] coverColumns = new int[costArray.length];

		Arrays.fill(assignRows, -1);
		Arrays.fill(coverRows, -1);
		Arrays.fill(assignColumns, -1);
		Arrays.fill(coverColumns, -1);

		for (int i = 0; i < costArray.length; i++)
			for (int j = 0; j < costArray[i].length; j++)
				if (costArray[i][j] == 0 && assignColumns[i] == -1 && assignRows[j] == -1) {
					assignColumns[j] = i;
					assignRows[i] = j;
				}

		boolean drawn = true;

		for (int i = 0; i < costArray.length; i++)
			for (int j = 0; j < costArray[i].length; j++)
				if (assignRows[i] == -1)
					coverRows[i] = j;

		while (drawn)
		{
			drawn = false;

			for (int i = 0; i < costArray.length; i++)
				for (int j = 0; j < costArray[i].length; j++)
					if (costArray[i][j] == 0 && coverRows[i] != -1 && coverColumns[j] == -1)
					{
						coverColumns[j] = i;
						drawn = true;
					}

			for (int i = 0; i < costArray.length; i++)
				for (int j = 0; j < costArray[i].length; j++)
					if (costArray[i][j] == 0 && coverColumns[j] != -1
							&& coverRows[i] == -1 && assignRows[i] == j)
					{
						coverRows[i] = j;
						drawn = true;
					}
		}

		return new Pair(coverRows, coverColumns);
	}

	/**
	 * Subtracts the minimum element from its row
	 *
	 * @param costArray - Cost Array
	 * @return costArray
	 */
	public int[][] subtractMinFromRows(int[][] costArray)
	{
		int[] min = new int[costArray.length];

		Arrays.fill(min, Integer.MAX_VALUE);

		for(int i = 0; i < costArray.length; i++)
			for(int j = 0; j < costArray[i].length; j++)
				if(costArray[i][j] < min[i])
					min[i] = costArray[i][j];

		for(int i = 0; i < costArray.length; i++)
			for(int j = 0; j < costArray[i].length; j++)
				costArray[i][j] -= min[i];

		return costArray;
	}

	/**
	 * Subtracts the minimum element from its columns
	 * 
	 * @param costArray - Cost Array
	 * @return costArray
	 */
	public int[][] subtractMinFromColumns(int[][] costArray)
	{
		int[] min = new int[costArray.length];
		Arrays.fill(min, Integer.MAX_VALUE);

		for (int[] ints : costArray)
			for (int j = 0; j < ints.length; j++)
				if (ints[j] < min[j])
					min[j] = ints[j];

		for(int i = 0; i < costArray.length; i++)
			for(int j = 0; j < costArray[i].length; j++)
				costArray[i][j] -= min[j];

		return costArray;
	}

	private class Pair
	{
		int[] rows;
		int[] columns;

		/**
		 * Constructor for Pair object
		 *
		 * @param rows - marked rows
		 * @param columns - marked columns
		 */
		public Pair(int[] rows, int[] columns)
		{
			this.rows = rows;
			this.columns = columns;
		}
	}
}
