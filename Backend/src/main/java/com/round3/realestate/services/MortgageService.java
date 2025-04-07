package com.round3.realestate.services;

import com.round3.realestate.dtos.MortgageRequestDto;
import com.round3.realestate.dtos.MortgageResponseDto;
import com.round3.realestate.entity.*;
import com.round3.realestate.enums.EmploymentStatus;
import com.round3.realestate.repository.EmploymentRepository;
import com.round3.realestate.repository.MortgageRepository;
import com.round3.realestate.repository.PropertyRepository;
import com.round3.realestate.repository.UserRepository;
import com.round3.realestate.utils.MonthlyPayment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class MortgageService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PropertyRepository propertyRepository;

    @Autowired
    private EmploymentRepository employmentRepository;

    @Autowired
    private MortgageRepository mortgageRepository;

    public MortgageResponseDto processMortgageRequest(MortgageRequestDto requestDto) {
        // Obtener el usuario autenticado
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // Verificar que el usuario tiene empleo
        Employment employment = employmentRepository.findByUser(user)
                .orElseThrow(() -> new IllegalArgumentException("Employment data not found. Cannot process mortgage."));

        if (employment.getEmploymentStatus() != EmploymentStatus.EMPLOYED) {
            MortgageResponseDto responseDto = new MortgageResponseDto();
            responseDto.setMessage("Mortgage rejected: Applicant is not employed.");
            responseDto.setApproved(false);
            return responseDto;
        }

        // Obtener la propiedad
        Property property = propertyRepository.findById(requestDto.getPropertyId())
                .orElseThrow(() -> new IllegalArgumentException("Property not found"));

        // Calcular el costo total (precio + 15%)
        BigDecimal propertyPrice = property.getPrice();
        BigDecimal totalCost = propertyPrice.multiply(new BigDecimal("1.15"))
                .setScale(2, RoundingMode.HALF_UP);

        // Determinar el porcentaje permitido según tipo de contrato
        double allowedPercentage = "indefinite".equalsIgnoreCase(employment.getContract().toString()) ? 0.30 : 0.15;

        // Calcular el pago mensual permitido
        double netMonthly = employment.getNetMonthly();
        double maxAllowedPayment = netMonthly * allowedPercentage;

        // Calcular el pago mensual real
        int numberOfMonths = requestDto.getTermYears() * 12;
        double monthlyInterestRate = 0.02 / 12; // 2% anual convertido a mensual

        // Fórmula de amortización
        double monthlyPayment = MonthlyPayment.calculateMonthlyPayment(
                totalCost.doubleValue(),
                monthlyInterestRate,
                numberOfMonths
        );

        // Verificar si el pago mensual está dentro del umbral permitido
        if (monthlyPayment > maxAllowedPayment) {
             MortgageResponseDto responseDto = new MortgageResponseDto();
                    responseDto.setApproved(false);
                    responseDto.setNetMonthly(netMonthly);
                    responseDto.setMonthlyPayment(Math.round(monthlyPayment * 100.0) / 100.0);
                    responseDto.setAllowedPercentage(String.format("%.1f%%", allowedPercentage * 100));
                    responseDto.setMessage("Mortgage rejected");
                    responseDto.setNumberOfMonths(numberOfMonths);
                    return responseDto;
        }

        // IMPORTANTE: Solo guardamos si la hipoteca es aprobada
        Mortgage mortgage = new Mortgage();
        mortgage.setUser(user);
        mortgage.setProperty(property);
        mortgage.setTotalCost(totalCost);
        mortgage.setMonthlyPayment(new BigDecimal(monthlyPayment).setScale(2, RoundingMode.HALF_UP));
        mortgage.setNumberOfMonths(numberOfMonths);
        mortgage.setAllowedPercentage(allowedPercentage);

         mortgageRepository.save(mortgage);

        // Actualizar disponibilidad de la propiedad
        property.setAvailability("Unavailable");
        propertyRepository.save(property);

         MortgageResponseDto responseDto = new MortgageResponseDto();
                responseDto.setApproved(true);
                responseDto.setMortgageId(mortgage.getId());
                responseDto.setNetMonthly(netMonthly);
                responseDto.setMonthlyPayment(Math.round(monthlyPayment * 100.0) / 100.0);
                responseDto.setAllowedPercentage(String.format("%.1f%%", allowedPercentage * 100));
                responseDto.setMessage("Mortgage approved.");
                responseDto.setNumberOfMonths(numberOfMonths);

                return  responseDto;
    }
}