package api.service;

import api.EntityGetter;
import api.dto.TransferCreateDTO;
import api.dto.TransferResponseDTO;
import api.entity.ClientAccount;
import api.entity.Transfer;
import api.exception.InvalidTransferException;
import api.mapper.TransferMapper;
import api.repository.ClientAccountRepository;
import api.repository.TransferRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.math.BigDecimal;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith (MockitoExtension.class)
class TransferServiceTest {

    @InjectMocks
    private TransferService service;

    @Mock
    private TransferRepository repository;

    @Mock
    private ClientAccountRepository clientAccountRepository;

    @Mock
    private ClientAccountService clientAccountService;

    @Mock
    private TransferMapper mapper;

    private final EntityGetter entityGetter = new EntityGetter();

    @Test
    void makeTransfer() {
        ClientAccount source = entityGetter.getClientAccount(null);
        source.setId(1L);

        ClientAccount target = entityGetter.getClientAccount(null);
        target.setId(2L);

        BigDecimal value = BigDecimal.TEN;

        String anonymizedSourceCPF = TransferMapper.anonymizeCPF(source.getCpf());
        String anonymizedTargetCPF = TransferMapper.anonymizeCPF(target.getCpf());

        TransferResponseDTO expectedResponse = entityGetter.getTransferResponseDTO
                (source, target, value, anonymizedSourceCPF, anonymizedTargetCPF);

        when(clientAccountService.findByClientCode(any(UUID.class))).thenReturn(source);
        when(clientAccountService.findByCpf(anyString())).thenReturn(target);

        when(clientAccountService.findLockedById(1L)).thenReturn(source);
        when(clientAccountService.findLockedById(2L)).thenReturn(target);

        when(repository.save(any(Transfer.class))).thenAnswer(i -> i.getArgument(0));
        when(mapper.toTransferResponseDTO(any(Transfer.class))).thenReturn(expectedResponse);

        TransferResponseDTO response = service.makeTransfer(UUID.randomUUID(), new TransferCreateDTO("cpf", value));

        Assertions.assertNotNull(response);
        Assertions.assertEquals(response.sourceCPF(), anonymizedSourceCPF);
        Assertions.assertEquals(response.targetCPF(), anonymizedTargetCPF);

        Assertions.assertEquals(new BigDecimal("990.00"), source.getBalance());
        Assertions.assertEquals(new BigDecimal("1010.00"), target.getBalance());
        Assertions.assertEquals(value, source.getAccountTransferLock().getTotalValueTransferredToday());
    }

    @Test
    void shouldRevokeTransferWhenTheLimitHasReached () {
        ClientAccount source = entityGetter.getClientAccount(null);
        source.getAccountTransferLock().setTotalValueTransferredToday(new BigDecimal("4000.00"));
        source.addBalance(new BigDecimal("5000.00"));
        source.setId(1L);

        ClientAccount target = entityGetter.getClientAccount(null);
        target.setId(2L);

        when(clientAccountService.findByClientCode(any(UUID.class))).thenReturn(source);
        when(clientAccountService.findByCpf(anyString())).thenReturn(target);

        when(clientAccountService.findLockedById(1L)).thenReturn(source);
        when(clientAccountService.findLockedById(2L)).thenReturn(target);

        Assertions.assertThrows(InvalidTransferException.class, () -> service.makeTransfer(UUID.randomUUID(), new TransferCreateDTO("cpf", new BigDecimal("1500.00"))));
    }
}