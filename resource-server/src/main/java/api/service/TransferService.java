package api.service;

import api.dto.TransferRequestDTO;
import api.dto.TransferResponseDTO;
import api.entity.ClientAccount;
import api.entity.Transfer;
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

@Service
@RequiredArgsConstructor
public class TransferService {

    private final TransferRepository repository;

    private final ClientAccountService clientAccountService;

    private final TransferMapper mapper;

    @Transactional
    public TransferResponseDTO makeTransfer (Long sourceAuthUserId, TransferRequestDTO requestDTO) {
        ClientAccount sourceTemp = clientAccountService.findByClientAuthUserId(sourceAuthUserId);

        ClientAccount targetTemp = clientAccountService.findByCpf(requestDTO.targetCPF());

        if (sourceTemp.getId().equals(targetTemp.getId())) {
            throw new InvalidTransferException("You can not transfer to yourself");
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
    public Page<TransferResponseDTO> transferHistoric (Long authUserId, int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by("transferredAt").descending());

        Page<Transfer> allByAccount = repository.findAllByAuthUserId(authUserId, pageRequest);

        return allByAccount.map(mapper::toTransferResponseDTO);
    }

    private void handleTransferLimit (BigDecimal value, ClientAccount sourceAccount) {
        BigDecimal limit = new BigDecimal("5000.0");

        sourceAccount.getAccountTransferLock().resetIfIsNewDay();

        BigDecimal totalValueTransferredToday = sourceAccount.getAccountTransferLock().getTotalValueTransferredToday();

        if (totalValueTransferredToday.add(value).compareTo(limit) > 0) {
            BigDecimal remainingValue = limit.subtract(totalValueTransferredToday);
            String additionalMessage = remainingValue.compareTo(BigDecimal.ZERO) == 0 ? "" : " You can only transfer R$" + remainingValue.toPlainString();
            throw new InvalidTransferException("This value would overpass your daily transfer limit." + additionalMessage);
        }

        sourceAccount.getAccountTransferLock().increaseValueTransferredToday(value);
    }
}
