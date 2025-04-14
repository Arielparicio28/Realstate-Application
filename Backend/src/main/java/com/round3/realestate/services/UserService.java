package com.round3.realestate.services;

import com.round3.realestate.dtos.DashboardResponseDto;
import com.round3.realestate.dtos.EmploymentDataDto;
import com.round3.realestate.dtos.MortgageDto;
import com.round3.realestate.entity.Employment;
import com.round3.realestate.entity.Mortgage;
import com.round3.realestate.entity.User;
import com.round3.realestate.repository.EmploymentRepository;
import com.round3.realestate.repository.MortgageRepository;
import com.round3.realestate.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmploymentRepository employmentRepository;

    @Autowired
    private MortgageRepository mortgageRepository;

    public DashboardResponseDto getUserDashboard() {
        // Obtener el usuario autenticado
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        DashboardResponseDto dashboardDto = new DashboardResponseDto();

        // Obtener datos de empleo
        Optional<Employment> employmentOpt = employmentRepository.findByUser(user);
        if (employmentOpt.isPresent()) {
            Employment employment = employmentOpt.get();
            EmploymentDataDto employmentDataDto = new EmploymentDataDto();
            employmentDataDto.setId(employment.getId());
            employmentDataDto.setContract(String.valueOf(employment.getContract()));
            employmentDataDto.setSalary(employment.getSalary());
            employmentDataDto.setNetMonthly(employment.getNetMonthly());
            employmentDataDto.setEmploymentStatus(employment.getEmploymentStatus());
            dashboardDto.setEmploymentData(employmentDataDto);
        }

        // Obtener hipotecas
        List<Mortgage> mortgages = mortgageRepository.findByUser(user);
        List<MortgageDto> mortgageDtos = new ArrayList<>();

        for (Mortgage mortgage : mortgages) {
            MortgageDto mortgageDto = getMortgageDto(mortgage);

            mortgageDtos.add(mortgageDto);
        }

        dashboardDto.setMortgages(mortgageDtos);

        return dashboardDto;
    }

    private static MortgageDto getMortgageDto(Mortgage mortgage) {
        MortgageDto mortgageDto = new MortgageDto();
        mortgageDto.setId(mortgage.getId());
        mortgageDto.setPropertyId(mortgage.getProperty().getId());
        mortgageDto.setPropertyName(mortgage.getProperty().getName());
        mortgageDto.setPropertyLocation(mortgage.getProperty().getLocation());
        mortgageDto.setTotalCost(mortgage.getTotalCost());
        mortgageDto.setMonthlyPayment(mortgage.getMonthlyPayment());
        mortgageDto.setNumberOfMonths(mortgage.getNumberOfMonths());
        mortgageDto.setInterestRate(mortgage.getInterestRate());
        return mortgageDto;
    }
}
