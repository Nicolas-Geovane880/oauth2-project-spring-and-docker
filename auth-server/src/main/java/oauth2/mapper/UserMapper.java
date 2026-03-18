package oauth2.mapper;

import oauth2.dto.UserResponseDTO;
import oauth2.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper (componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface UserMapper {

    @Mapping (source = "user.id", target = "id")
    @Mapping (source = "user.userStatus.createdAt", target = "createdAt")
    UserResponseDTO parseToResponse (User user);

    @Named (value = "formatCpf")
    public static String formatCpf (String unformattedCpf) {
        String pieceOne = unformattedCpf.substring(0, 3);
        String pieceTwo = unformattedCpf.substring(3, 6);
        String pieceThree = unformattedCpf.substring(6, 9);
        String pieceFour = unformattedCpf.substring(9);
        return pieceOne + "." + pieceTwo + "." + pieceThree + "-" + pieceFour;
    }
}
