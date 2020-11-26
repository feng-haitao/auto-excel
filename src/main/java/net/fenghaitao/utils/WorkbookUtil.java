package net.fenghaitao.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class WorkbookUtil {
    /**
     * Get the Excel column name based on the given column name and step size
     */
    public static String getColName(String colName, int step) {
        return indexToColName(colNameToIndex(colName) + step);
    }

    /**
     * Converts the column name to the index location，eg.AB -> 27
     */
    public static int colNameToIndex(String chars) {
        if (chars.isEmpty())
            return -1;

        int index = 0;
        int place = chars.length();
        int tmp = 0;

        for (int i = 0; i < chars.length(); ++i) {
            tmp = charToNum(chars.charAt(i));
            if (place > 1)
                index += 26 * (place - 1) * (tmp + 1);
            else
                index += tmp;

            --place;
        }
        return index;
    }

    /**
     * Convert letters into numerical Numbers，starting from 0
     */
    public static int charToNum(char chr) {
        return chr - 'A';
    }

    /**
     * Converts the column index (starting at 0) to the Excel column name
     */
    public static String indexToColName(int colIndex) {
        List<Character> chars = new ArrayList<>();
        if (colIndex > 25) {
            int left = colIndex % 26;
            chars.add(numToChar(left));
            colIndex = colIndex / 26;
        } else {
            return String.valueOf(numToChar(colIndex));
        }
        while (colIndex > 26) {
            int left = colIndex % 26;
            chars.add(numToChar(left - 1));
            colIndex = colIndex / 26;
        }
        chars.add(numToChar(colIndex - 1));

        StringBuilder builder = new StringBuilder(chars.size());
        for (int i = chars.size() - 1; i >= 0; --i)
            builder.append(chars.get(i));

        return builder.toString();
    }

    /**
     * Convert Numbers between 0 and 25 into letters
     */
    public static char numToChar(int num) {
        if (num > 25 || num < 0)
            throw new IllegalArgumentException("The parameter num is out of range");

        return (char) ('A' + num);
    }

    /**
     * Gets the next column name for the specified column name
     */
    public static String nextColName(String colName) {
        boolean isFull = false;
        char[] chars = colName.toCharArray();
        for (int i = chars.length - 1; i >= 0; --i) {
            isFull = false;
            chars[i] = nextColName(chars[i]);
            if (chars[i] == 'A')
                isFull = true;
            else
                break;
        }
        if (isFull)
            return "A" + new String(chars);
        else
            return new String(chars);
    }

    /**
     * Gets the next column name for the specified column name
     */
    public static char nextColName(char chr) {
        return chr == 'Z' ? 'A' : (char) (chr + 1);
    }
}
