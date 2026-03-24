package api.mapper;

import api.dto.UserAccountResponseDTO;
import api.dto.event_dto.AuthUserRegisterEventDTO;
import api.dto.UserAccountRegisterDTO;
import api.entity.Client;
import org.mapstruct.*;

import java.time.LocalDate;
import java.util.UUID;

@Mapper (componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface AccountRegisterMapper {

    @Mapping (source = "birthDate", target = "age", qualifiedByName = "calculateAge")
    Client toClient (UserAccountRegisterDTO apiRequestDTO);

    @Mapping (source = "client.code", target = "clientCode")
    UserAccountResponseDTO toUserAccountResponseDTO (Client client, String cpf);

    @Named (value = "calculateAge")
    static int calculateAge (LocalDate birthDate) {
        return LocalDate.now().getYear() - birthDate.getYear();
    }
}
