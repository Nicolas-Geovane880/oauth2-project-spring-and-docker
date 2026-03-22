package api.service;

import api.EntityGetter;
import api.dto.ExtractResponseDTO;
import api.entity.ClientAccount;
import api.mapper.BankAccountMapper;
import api.repository.ClientAccountRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith (MockitoExtension.class)
class ClientAccountServiceTest {

    @InjectMocks
    private ClientAccountService service;

    @Mock
    private ClientAccountRepository repository;

    @Mock
    private BankAccountMapper mapper;

    private final EntityGetter entityGetter = new EntityGetter();

    @Test
    void extract() {
        ClientAccount clientAccount = entityGetter.getClientAccount(entityGetter.getClient());

        ExtractResponseDTO expectedResponse = entityGetter.getExtractResponseDTO(clientAccount);

        when(repository.findByClientAuthUserId(anyLong())).thenReturn(Optional.of(clientAccount));
        when(mapper.toExtractResponseDTO(any(ClientAccount.class), any(BigDecimal.class))).thenReturn(expectedResponse);

        ExtractResponseDTO response = service.extract(1L);

        Assertions.assertNotNull(response);
        Assertions.assertEquals(response.cpf(), expectedResponse.cpf());
        Assertions.assertEquals(response.balance(), expectedResponse.balance());
        Assertions.assertEquals(response.remainingTransferLimitValue(), expectedResponse.remainingTransferLimitValue());
    }
}