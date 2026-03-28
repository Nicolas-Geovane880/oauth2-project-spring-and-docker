package api.service;

import api.constant.ConstantValue;
import api.constant.ErrorsMessage;
import api.dto.TransferCreateDTO;
import api.dto.TransferResponseDTO;
import api.entity.ClientAccount;
import api.entity.Transfer;
import api.enums.Status;
import api.exception.InvalidProcessException;
import api.exception.InvalidTransferException;
import api.mapper.TransferMapper;
import api.repository.TransferRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TransferService {

    private final TransferRepository repository;

    private final ClientAccountService clientAccountService;

    private final TransferMapper mapper;

    @Transactional
    public TransferResponseDTO makeTransfer (UUID clientCode, TransferCreateDTO requestDTO) {
        ClientAccount sourceTemp = clientAccountService.findByClientCode(clientCode);

        ClientAccount targetTemp = clientAccountService.findByCpf(requestDTO.targetCPF());

        if (sourceTemp.getClient().getStatus() == Status.DELETED || targetTemp.getClient().getStatus() == Status.DELETED) {
            throw new InvalidProcessException("This account is deleted! Can not transfer");
        }

        if (sourceTemp.getId().equals(targetTemp.getId())) {
            throw new InvalidTransferException(ErrorsMessage.TRANSFER_TO_YOURSELF);
        }

        long firstToLock = Math.min(sourceTemp.getId(), targetTemp.getId());
        long lastToLock = Math.max(sourceTemp.getId(), targetTemp.getId());

        ClientAccount firstLocked = clientAccountService.findLockedById(firstToLock);
        ClientAccount lastLocked = clientAccountService.findLockedById(lastToLock);

        ClientAccount sourceAccount = sourceTemp.getId().equals(firstToLock) ? firstLocked : lastLocked;
        ClientAccount targetAccount = targetTemp.getId().equals(firstToLock) ? firstLocked : lastLocked;

        handleTransferLimit(requestDTO.value(), sourceAccount);

        sourceAccount.discountBalance(requestDTO.value());
        targetAccount.addBalance(requestDTO.value());

        Transfer transfer = Transfer.builder()
                .sourceAccount(sourceAccount)
                .targetAccount(targetAccount)
                .value(requestDTO.value())
                .build();

        return mapper.toTransferResponseDTO(repository.save(transfer));
    }

    @Transactional (readOnly = true)
    public Page<TransferResponseDTO> transferHistoric (UUID clientCode, int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by("transferredAt").descending());

        Page<Transfer> allByAccount = repository.findAllByClientCode(clientCode, pageRequest);

        return allByAccount.map(mapper::toTransferResponseDTO);
    }

    private void handleTransferLimit (BigDecimal value, ClientAccount sourceAccount) {
        BigDecimal limit = new BigDecimal(ConstantValue.LIMIT_TRANSFER_VALUE);

        sourceAccount.getAccountTransferLock().resetIfIsNewDay();

        BigDecimal totalValueTransferredToday = sourceAccount.getAccountTransferLock().getTotalValueTransferredToday();

        if (totalValueTransferredToday.add(value).compareTo(limit) > 0) {
            BigDecimal remainingValue = limit.subtract(totalValueTransferredToday);
            String additionalMessage = remainingValue.compareTo(BigDecimal.ZERO) == 0 ? "" : " You can only transfer R$" + remainingValue.toPlainString() + " today";

            throw new InvalidTransferException(ErrorsMessage.TRANSFER_LIMIT_OVERPASSED, additionalMessage);
        }

        sourceAccount.getAccountTransferLock().increaseValueTransferredToday(value);
    }
}
