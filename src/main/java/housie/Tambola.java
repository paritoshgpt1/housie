package housie;

import java.util.*;

/**
 * @author Paritosh
 */
public class Tambola {

    private static final int TICKETS_IN_A_SHEET = 6;
    private static final int NUMBER_OF_COLUMNS = 9;
    private static final int NUMBER_OF_ROWS = 3;

    public static class Ticket {
        public int[][] numbers;

        Ticket() {
            this.numbers = new int[NUMBER_OF_ROWS][NUMBER_OF_COLUMNS];
        }

        int getRowCount(int r) {
            int count = 0;
            for (int i = 0; i < NUMBER_OF_COLUMNS; i++) {
                if (numbers[r][i] != 0) count++;
            }
            return count;
        }

        @Override
        public String toString() {
            return Arrays.deepToString(this.numbers).replace("],", "],\n") + "\n\n";
        }
    }

    public static void main(String[] args) {
        System.out.println(Arrays.toString(getTickets(6)));
    }

    static int getRand(int min, int max) {
        Random rand = new Random();
        return rand.nextInt(max - min + 1) + min;
    }

    static int getNumberOfElementsInSet(List<List<Integer>> set) {
        int count = 0;
        for (List<Integer> li : set) count += li.size();
        return count;
    }

    public static Ticket[] getTickets(int n) {
        Ticket[] res = new Ticket[n];
        Ticket[] generatedTickets = generateTickets();
        System.arraycopy(generatedTickets, 0, res, 0, n);
        return res;
    }

    private static Ticket[] generateTickets() {

        List<List<Integer>> columns = getTicketColumns();
        List<List<List<Integer>>> sets = initializeColumnForEachTicket();

        Ticket[] tickets = new Ticket[TICKETS_IN_A_SHEET];
        for (int i = 0; i < TICKETS_IN_A_SHEET; i++) {
            tickets[i] = new Ticket();
        }

        // assign 1 element in each column in each ticket
        // this fills out a total of 54 numbers from the board.
        // 36 numbers left to be filled
        for (int i = 0; i < NUMBER_OF_COLUMNS; i++) {
            List<Integer> li = columns.get(i);
            for (int j = 0; j < TICKETS_IN_A_SHEET; j++) {
                int randNumIndex = getRand(0, li.size() - 1);
                int randNum = li.get(randNumIndex);

                List<Integer> set = sets.get(j).get(i);
                set.add(randNum);

                li.remove(randNumIndex);
            }
            printSets(sets);
        }

        // add 1 extra number to last column of any ticket
        // as last number set contains one extra number from other set
        // all sets have 9 numbers (10-19, 20-29) but last has (80-90) 10 numbers
        // this fills 1 more numbers
        // 35 numbers left
        List<Integer> lastCol = columns.get(8);
        int randNumIndex = getRand(0, lastCol.size() - 1);
        int randNum = lastCol.get(randNumIndex);

        int randSetIndex = getRand(0, sets.size() - 1);
        List<Integer> randSet = sets.get(randSetIndex).get(8);
        randSet.add(randNum);

        lastCol.remove(randNumIndex);

        printSets(sets);

        System.out.println("Step 3");
        //3 passes over the remaining columns
        // fill out 2nd number (only) in random columns in random ticket
        // this fills out 27 more numbers. 8 numbers left
        for (int pass = 0; pass < 3; pass++) {
            for (int i = 0; i < NUMBER_OF_COLUMNS; i++) {
                List<Integer> col = columns.get(i);
                if (col.size() == 0) continue;

                int randNumIndex_p = getRand(0, col.size() - 1);
                int randNum_p = col.get(randNumIndex_p);

                boolean vacantSetFound = false;
                while (!vacantSetFound) {
                    int randSetIndex_p = getRand(0, sets.size() - 1);
                    List<List<Integer>> randSet_p = sets.get(randSetIndex_p);

                    if (getNumberOfElementsInSet(randSet_p) == 15 || randSet_p.get(i).size() == 2) continue;

                    vacantSetFound = true;
                    randSet_p.get(i).add(randNum_p);

                    col.remove(randNumIndex_p);
                }
            }
            printSets(sets);
        }

        System.out.println(columns);
        System.out.println("Step 4");
        //one more pass over the remaining columns
        for (int i = 0; i < 9; i++) {
            List<Integer> col = columns.get(i);
            if (col.size() == 0) continue;

            int randNumIndex_p = getRand(0, col.size() - 1);
            int randNum_p = col.get(randNumIndex_p);

            boolean vacantSetFound = false;
            while (!vacantSetFound) {
                int randSetIndex_p = getRand(0, sets.size() - 1);
                List<List<Integer>> randSet_p = sets.get(randSetIndex_p);

                if (getNumberOfElementsInSet(randSet_p) == 15 || randSet_p.get(i).size() == 3) continue;

                vacantSetFound = true;
                randSet_p.get(i).add(randNum_p);

                col.remove(randNumIndex_p);
            }
        }

        printSets(sets);

        System.out.println("Step 5");
        //sort the internal sets
        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 9; j++) {
                Collections.sort(sets.get(i).get(j));
            }
        }
        printSets(sets);

        System.out.println("Step 6");
        //got the sets - need to arrange in tickets now
        for (int setIndex = 0; setIndex < TICKETS_IN_A_SHEET; setIndex++) {
            List<List<Integer>> currSet = sets.get(setIndex);
            Ticket currTicket = tickets[setIndex];
            fillTicket(currTicket, currSet);
        }

        return tickets;
    }


    private static void fillTicket(Ticket currTicket, List<List<Integer>> currSet) {

        System.out.println("Ticket numbers going to be filled");
        System.out.println(currSet);

        // Fill columns with 3 numbers
        for (int colIndex = 0; colIndex < NUMBER_OF_COLUMNS; colIndex++) {
            List<Integer> currSetCol = currSet.get(colIndex);
            if (currSetCol.size() == 3) {
                currTicket.numbers[0][colIndex] = currSetCol.remove(0);
                currTicket.numbers[1][colIndex] = currSetCol.remove(0);
                currTicket.numbers[2][colIndex] = currSetCol.remove(0);
            }
        }
        System.out.println("After fiiling columns with 3 numbers");
        System.out.println(currTicket);

        // Fill columns with 2 numbers
        for (int colIndex = 0; colIndex < NUMBER_OF_COLUMNS; colIndex++) {
            List<Integer> currSetCol = currSet.get(colIndex);
            if (currSetCol.size() != 2) continue;
            while (!currSetCol.isEmpty()) {
                // If first row already has 5 numbers, put the 2 numbers in 2nd and 3rd row
                if (currTicket.getRowCount(0) == 5) {
                    currTicket.numbers[1][colIndex] = currSetCol.remove(0);
                    currTicket.numbers[2][colIndex] = currSetCol.remove(0);
                } else {
                    // if first row still has space, select the row randomly for 1st number
                    // 2nd row will have space for sure, because if 1st has space
                    int randIndex = getRand(0, 1);
                    currTicket.numbers[randIndex][colIndex] = currSetCol.remove(0);

                    // if 1st number is in second row, put 2nd number in 3rd row
                    if (randIndex == 1) {
                        currTicket.numbers[2][colIndex] = currSetCol.remove(0);
                    } else {
                        // if 2nd and 3rd row both have less than 5 numbers, select a row randomly for 2nd number
                        if (currTicket.getRowCount(1) < 5 && currTicket.getRowCount(2) < 5) {
                            int randIndex2 = getRand(1, 2);
                            currTicket.numbers[randIndex2][colIndex] = currSetCol.remove(0);
                        } else if (currTicket.getRowCount(1) == 5) {
                            // if 2nd row already has 5 numbers, put 2nd number in 3rd row
                            currTicket.numbers[2][colIndex] = currSetCol.remove(0);
                        } else if (currTicket.getRowCount(2) == 5) {
                            // if 3rd row already has 5 numbers, put 2nd number in 2nd row
                            currTicket.numbers[1][colIndex] = currSetCol.remove(0);
                        } else {
                            // This will never happen
                            System.out.println("ERROR !!!!!!!!!!!!: " + currSetCol.get(0));
                        }
                    }
                }
            }
        }
        System.out.println("After fiiling columns with 2 numbers");
        System.out.println(currTicket);

        // Fill columns with 1 numbers in random position in any row (wherever possible)
        for (int colIndex = 0; colIndex < NUMBER_OF_COLUMNS; colIndex++) {
            List<Integer> currSetCol = currSet.get(colIndex);
            Set<Integer> unique = new HashSet<>();
            if (currSetCol.size() == 1) {
                while (true) {
                    int randIndex = getRand(0, 2);
                    unique.add(randIndex);
                    if (currTicket.getRowCount(randIndex) == 5) continue;

                    currTicket.numbers[randIndex][colIndex] = currSetCol.remove(0);
                    break;
                }
            }
        }
        System.out.println("After fiiling columns with 1 number");
        System.out.println(currTicket);
    }

    private static List<List<Integer>> getTicketColumns() {
        // Generating all the 9 columns with 10 numbers each [[1-10],[11-20],.....,[81,90]]
        List<Integer> l1 = new ArrayList<>();
        for (int i = 1; i <= 9; i++) {
            l1.add(i);
        }

        List<Integer> l2 = new ArrayList<>();
        for (int i = 10; i <= 19; i++) {
            l2.add(i);
        }

        List<Integer> l3 = new ArrayList<>();
        for (int i = 20; i <= 29; i++) {
            l3.add(i);
        }

        List<Integer> l4 = new ArrayList<>();
        for (int i = 30; i <= 39; i++) {
            l4.add(i);
        }

        List<Integer> l5 = new ArrayList<>();
        for (int i = 40; i <= 49; i++) {
            l5.add(i);
        }

        List<Integer> l6 = new ArrayList<>();
        for (int i = 50; i <= 59; i++) {
            l6.add(i);
        }

        List<Integer> l7 = new ArrayList<>();
        for (int i = 60; i <= 69; i++) {
            l7.add(i);
        }

        List<Integer> l8 = new ArrayList<>();
        for (int i = 70; i <= 79; i++) {
            l8.add(i);
        }

        List<Integer> l9 = new ArrayList<>();
        for (int i = 80; i <= 90; i++) {
            l9.add(i);
        }

        List<List<Integer>> columns = new ArrayList<>();
        columns.add(l1);
        columns.add(l2);
        columns.add(l3);
        columns.add(l4);
        columns.add(l5);
        columns.add(l6);
        columns.add(l7);
        columns.add(l8);
        columns.add(l9);

        return columns;
    }

    private static List<List<List<Integer>>> initializeColumnForEachTicket() {
        List<List<List<Integer>>> sets = new ArrayList<>();
        for (int i = 0; i < TICKETS_IN_A_SHEET; i++) {
            List<List<Integer>> set = new ArrayList<>();
            for (int j = 0; j < NUMBER_OF_COLUMNS; j++) {
                set.add(new ArrayList<>());
            }
            sets.add(set);
        }
        return sets;
    }

    static void printSets(List<List<List<Integer>>> sets) {
        for (List<List<Integer>> set : sets) {
            System.out.println(set);
        }
        System.out.println();
    }

}
