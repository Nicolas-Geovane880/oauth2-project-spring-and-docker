package api.mapper;

import api.dto.BankAccountRegisterDTO;
import api.dto.UserAccountResponseDTO;
import api.dto.UserAccountUpdateDTO;
import api.entity.BankAccountUpdateHolder;
import api.entity.Client;
import api.entity.ClientAccount;
import org.mapstruct.*;

import java.time.LocalDate;

@Mapper (componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface BankAccountMapper {

    @Mapping (source = "birthDate", target = "age", qualifiedByName = "calculateAge")
    Client toClient (BankAccountRegisterDTO apiRequestDTO);

    @Mapping (source = "client.code", target = "clientCode")
    @Mapping (source = "client", target = "name", qualifiedByName = "formFullName")
    UserAccountResponseDTO toUserAccountResponseDTO (Client client, String cpf);

    BankAccountUpdateHolder toBankAccountUpdateHolder (UserAccountUpdateDTO updateDTO);

    @Mapping (target = "id", ignore = true)
    void update (BankAccountUpdateHolder updateHolder, @MappingTarget ClientAccount clientAccount);

    @Mapping (target = "id", ignore = true)
    void updateClient (BankAccountUpdateHolder updateHolder, @MappingTarget Client client);

    @AfterMapping
    default void updateClientAccount (BankAccountUpdateHolder updateHolder, @MappingTarget ClientAccount clientAccount) {
        Client linkedClient = clientAccount.getClient();

        updateClient(updateHolder, linkedClient);

        int updatedAge = calculateAge(linkedClient.getBirthDate());

        linkedClient.setAge(updatedAge);
    }

    @Named(value = "formFullName")
    static String formFullName (Client client) {
        return client.getLastName() == null ? client.getName(): client.getName() + " " + client.getLastName();
    }

    @Named (value = "calculateAge")
    static int calculateAge (LocalDate birthDate) {
        return LocalDate.now().getYear() - birthDate.getYear();
    }
}
