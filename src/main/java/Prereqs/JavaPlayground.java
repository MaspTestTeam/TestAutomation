package Prereqs;

//THIS SCRIPT IS AN INDEPENDENT SCRIPT THAT CAN BE USED TO WRITE SMALL FUNCTIONS OR TEST
// PARTICULAR JAVA SYNTAX BEHAVIOUR.
public class JavaPlayground {

    public static void main(String[] args) {
        String firstAmountTraded = "100.56";
        String secondAmountTraded = "100.00";
        double totalAmount = Double.parseDouble(firstAmountTraded) + Double.parseDouble(secondAmountTraded);
        System.out.println(totalAmount);
    }

}
