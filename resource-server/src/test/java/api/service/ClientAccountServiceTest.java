package api.service;

import api.EntityGetter;
import api.dto.ExtractResponseDTO;
import api.entity.ClientAccount;
import api.mapper.ClientAccountMapper;
import api.repository.ClientAccountRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith (MockitoExtension.class)
class ClientAccountServiceTest {

    @InjectMocks
    private ClientAccountService service;

    @Mock
    private ClientAccountRepository repository;

    @Mock
    private ClientAccountMapper mapper;

    private final EntityGetter entityGetter = new EntityGetter();

    @Test
    @DisplayName (value = "Should show the extract successfully")
    void extract() {
        ClientAccount clientAccount = entityGetter.getClientAccount(entityGetter.getClient());

        ExtractResponseDTO expectedResponse = entityGetter.getExtractResponseDTO(clientAccount);

        when(repository.findByClientCode(any(UUID.class))).thenReturn(Optional.of(clientAccount));
        when(mapper.toExtractResponseDTO(any(ClientAccount.class), any(BigDecimal.class))).thenReturn(expectedResponse);

        ExtractResponseDTO response = service.extract(UUID.randomUUID());

        Assertions.assertNotNull(response);
        Assertions.assertEquals(response.cpf(), expectedResponse.cpf());
        Assertions.assertEquals(response.balance(), expectedResponse.balance());
        Assertions.assertEquals(response.remainingTransferLimitValue(), expectedResponse.remainingTransferLimitValue());
    }
}