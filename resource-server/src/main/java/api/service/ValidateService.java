package api.service;

import api.dto.ProfileValidationDTO;
import api.repository.ClientAccountRepository;
import api.repository.ClientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ValidateService {

    private final ClientRepository clientRepository;

    @Transactional (readOnly = true)
    public List<String> handleValidationConflict(ProfileValidationDTO validationDTO) {
        List<String> validationConflicts = new ArrayList<>();

        if (clientRepository.existsByEmail(validationDTO.email())) {
            validationConflicts.add("email");
        }

        if (clientRepository.existsByPhone(validationDTO.phone())) {
            validationConflicts.add("phone");
        }

        return validationConflicts;
    }
}
