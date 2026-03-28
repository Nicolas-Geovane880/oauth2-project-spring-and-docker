package api.mapper;

import api.dto.ExtractResponseDTO;
import api.entity.Client;
import api.entity.ClientAccount;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.NullValuePropertyMappingStrategy;
import java.math.BigDecimal;

@Mapper (componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ClientAccountMapper {

    @Mapping (source = "clientAccount.cpf", target = "cpf")
    @Mapping (source = "clientAccount.client", target = "name", qualifiedByName = "formFullName")
    @Mapping (source = "remainingTransferLimitValue", target = "remainingTransferLimitValue")
    ExtractResponseDTO toExtractResponseDTO (ClientAccount clientAccount, BigDecimal remainingTransferLimitValue);

    @Named(value = "formFullName")
    static String formFullName (Client client) {
        return client.getLastName() == null ? client.getName(): client.getName() + " " + client.getLastName();
    }
}
