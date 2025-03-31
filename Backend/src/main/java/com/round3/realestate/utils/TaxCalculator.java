package com.round3.realestate.utils;

public class TaxCalculator {

    public static double calculateNetMonthly(double annualSalary) {
        double retention;

        if (annualSalary <= 12450) {
            retention = 0.19;
        } else if (annualSalary <= 20199) {
            retention = 0.24;
        } else if (annualSalary <= 35199) {
            retention = 0.30;
        } else if (annualSalary <= 59999) {
            retention = 0.37;
        } else if (annualSalary <= 299999) {
            retention = 0.45;
        } else {
            retention = 0.50;
        }

        double netAnnual = annualSalary * (1 - retention);
        return Math.round(netAnnual / 12 * 100.0) / 100.0; // Redondear a 2 decimales
    }
}