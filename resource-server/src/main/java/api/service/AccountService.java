package api.service;

import api.dto.ApiRequestDTO;
import api.entity.Client;
import api.mapper.AccountRegisterMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final ClientService clientService;

    private final ClientAccountService clientAccountService;

    private final AccountRegisterMapper mapper;

    @Transactional
    public void saveClientAndAccount (ApiRequestDTO apiRequestDTO) {
        Client savedClient = clientService.save(mapper.toClient(apiRequestDTO));

        clientAccountService.save(savedClient, apiRequestDTO.cpf());
    }


}
