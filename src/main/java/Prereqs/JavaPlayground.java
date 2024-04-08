package Prereqs;

import java.util.Arrays;

//THIS SCRIPT IS AN INDEPENDENT SCRIPT THAT CAN BE USED TO WRITE SMALL FUNCTIONS OR TEST
// PARTICULAR JAVA SYNTAX BEHAVIOUR.
public class JavaPlayground {

    public static void main(String[] args) {
        String firstAmountTraded = "100.56";
        String secondAmountTraded = "100.00";
        double totalAmount = Double.parseDouble(firstAmountTraded) + Double.parseDouble(secondAmountTraded);
        System.out.println(totalAmount);

        String exampleDateText = "For example, 1 3 2024";
        //Parse the string to get three values, 1, 3, 2024
        String[] dates = exampleDateText.split(",",2)[1].trim().split(" ", 3);
        String firstSaleDay = dates[0];
        String firstSaleMonth = dates[1];
        String firstSaleYear = dates[2];
        System.out.println(firstSaleDay);
        System.out.println(firstSaleMonth);
        System.out.println(firstSaleYear);


        Double amountToPay = Double.parseDouble("32")*(40/100d);
        System.out.println(amountToPay);

    }

}
