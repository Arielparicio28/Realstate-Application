package com.round3.realestate.utils;


public class MonthlyPayment {
    public static double calculateMonthlyPayment(double principal, double monthlyRate, int numberOfMonths) {
        double payment;
        if (monthlyRate == 0) {
            payment = principal / numberOfMonths;
        } else {
            payment = principal *
                    (monthlyRate * Math.pow(1 + monthlyRate, numberOfMonths)) /
                    (Math.pow(1 + monthlyRate, numberOfMonths) - 1);
        }
        return payment;
    }
}

