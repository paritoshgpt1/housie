package io.github.paritoshgpt1.Housie.util;

import lombok.Builder;
import lombok.Data;

import java.util.Arrays;

@Data
public class TambolaTicket {

    static final int TICKETS_IN_A_SHEET = 6;
    static final int NUMBER_OF_COLUMNS = 9;
    static final int NUMBER_OF_ROWS = 3;

    public int[][] numbers;
    public int id;

    public TambolaTicket() {
        this.numbers = new int[NUMBER_OF_ROWS][NUMBER_OF_COLUMNS];
    }

    int getRowCount(int r) {
        int count = 0;
        for (int i = 0; i < NUMBER_OF_COLUMNS; i++) {
            if (numbers[r][i] != 0) count++;
        }
        return count;
    }

    public String getDBValue() {
        return Arrays.deepToString(this.numbers)
                .replace("],", ";")
                .replace("[","")
                .replace("]","")
                .replace(" ","");
    }
}