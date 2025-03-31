package com.round3.realestate.services;

import com.round3.realestate.dto.EmploymentDataDto;
import com.round3.realestate.dto.EmploymentRequestDto;
import com.round3.realestate.dto.EmploymentResponseDto;
import com.round3.realestate.entity.Employment;
import com.round3.realestate.entity.User;
import com.round3.realestate.enums.ContractType;
import com.round3.realestate.enums.EmploymentStatus;
import com.round3.realestate.repository.EmploymentRepository;
import com.round3.realestate.repository.UserRepository;
import com.round3.realestate.utils.TaxCalculator;
import org.springframework.stereotype.Service;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@Service
public class EmploymentService {


    private final EmploymentRepository employmentRepository;
    private final UserRepository userRepository;

    public EmploymentService(EmploymentRepository employmentRepository, UserRepository userRepository) {
        this.employmentRepository = employmentRepository;
        this.userRepository = userRepository;
    }

    public EmploymentResponseDto createEmployment(EmploymentRequestDto requestDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Employment employment = employmentRepository.findByUser(user)
                .orElse(new Employment());

        employment.setUser(user);
        if (requestDto.getContract() != null) {
            employment.setContract(ContractType.fromValue(requestDto.getContract()));
        } else {
            employment.setContract(null);
        }

        employment.setSalary(requestDto.getSalary());
        employment.setNetMonthly(TaxCalculator.calculateNetMonthly(requestDto.getSalary()));
        employment.setEmploymentStatus(EmploymentStatus.EMPLOYED);

        employment = employmentRepository.save(employment);

        // Actualizar también el status del User
        user.setStatus(EmploymentStatus.EMPLOYED);
        userRepository.save(user);

        // Crear la respuesta
        EmploymentResponseDto responseDto = new EmploymentResponseDto();
        responseDto.setMessage("Employment data updated successfully");
        responseDto.setSuccess(true);

        EmploymentDataDto dataDto = new EmploymentDataDto();
        dataDto.setId(employment.getId());
        dataDto.setContract(String.valueOf(employment.getContract()).toLowerCase());
        dataDto.setSalary(employment.getSalary());
        dataDto.setNetMonthly(Math.round(employment.getNetMonthly() * 100.0) / 100.0);
        dataDto.setEmploymentStatus(EmploymentStatus.EMPLOYED);


        responseDto.setEmploymentData(dataDto);

        return responseDto;
    }

    public EmploymentResponseDto updateEmployment(EmploymentRequestDto requestDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // Verificar que los datos de empleo ya existen
        Employment employment = employmentRepository.findByUser(user)
                .orElseThrow(() -> new IllegalArgumentException("Employment data not found. Use POST to create new employment data."));

        // Actualizar solo los campos proporcionados
        if (requestDto.getContract() != null) {
            employment.setContract(ContractType.fromValue(requestDto.getContract()));
        }

        employment.setSalary(requestDto.getSalary());
        employment.setNetMonthly(TaxCalculator.calculateNetMonthly(requestDto.getSalary()));

        employment = employmentRepository.save(employment);

        // Crear respuesta correcta con punto final y campo success
        EmploymentResponseDto responseDto = new EmploymentResponseDto();
        responseDto.setMessage("Employment data updated successfully.");
        responseDto.setSuccess(true);

        EmploymentDataDto dataDto = new EmploymentDataDto();
        dataDto.setId(employment.getId());
        dataDto.setContract(String.valueOf(employment.getContract()).toLowerCase());
        dataDto.setSalary(employment.getSalary());
        dataDto.setNetMonthly(Math.round(employment.getNetMonthly() * 10.0) / 10.0);
        dataDto.setEmploymentStatus(EmploymentStatus.EMPLOYED);

        responseDto.setEmploymentData(dataDto);

        return responseDto;
    }


}