package api.mapper;

import api.dto.TransferResponseDTO;
import api.entity.Client;
import api.entity.Transfer;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper (componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface TransferMapper {

    @Mapping (source = "sourceAccount.client", target = "sourceName", qualifiedByName = "formFullName")
    @Mapping (source = "targetAccount.client", target = "targetName", qualifiedByName = "formFullName")
    @Mapping (source = "sourceAccount.cpf", target = "sourceCPF", qualifiedByName = "anonymizeCPF")
    @Mapping (source = "targetAccount.cpf", target = "targetCPF", qualifiedByName = "anonymizeCPF")
    @Mapping (source = "transferredAt", target = "transferredAt")
    @Mapping (source = "code", target = "code")
    TransferResponseDTO toTransferResponseDTO (Transfer transfer);

    @Named (value = "anonymizeCPF")
    static String anonymizeCPF (String cpf) {
        if (cpf == null) return null;

        cpf = cpf.replaceAll("\\D", "");

        if (cpf.length() != 11) return "Error";

        String firstPart = "." + cpf.substring(3, 6);
        String lastPart = "." + cpf.substring(6, 9) + "-";

        return "xxx" + firstPart + lastPart + "xx";
    }

    @Named (value = "formFullName")
    static String formFullName (Client client) {
        return client.getLastName() == null ? client.getName(): client.getName() + " " + client.getLastName();
    }
}
