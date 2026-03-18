package gateway.mapper;

import gateway.dto.AccountRegisterDTO;
import gateway.dto.ApiRequestDTO;
import gateway.dto.AuthRequestDTO;
import gateway.dto.ProfileValidationDTO;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper (componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface AccountRegisterMapper {

    ApiRequestDTO toApiRequestDTO (AccountRegisterDTO registerDTO, Long authUserId);

    AuthRequestDTO toAuthRequestDTO (AccountRegisterDTO registerDTO);

    ProfileValidationDTO toProfileValidationDTO (AccountRegisterDTO registerDTO);
}
