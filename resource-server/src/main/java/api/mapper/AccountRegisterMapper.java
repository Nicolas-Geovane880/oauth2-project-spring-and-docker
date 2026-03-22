package api.mapper;

import api.dto.ApiRequestDTO;
import api.entity.Client;
import api.entity.ClientAccount;
import org.mapstruct.*;

import java.time.LocalDate;

@Mapper (componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface AccountRegisterMapper {

    @Mapping (source = "birthDate", target = "age", qualifiedByName = "calculateAge")
    Client toClient (ApiRequestDTO apiRequestDTO);

    @Named (value = "calculateAge")
    static int calculateAge (LocalDate birthDate) {
        return LocalDate.now().getYear() - birthDate.getYear();
    }
}
