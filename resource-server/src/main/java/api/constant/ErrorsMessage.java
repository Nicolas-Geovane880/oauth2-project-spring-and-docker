package api.constant;

public class ErrorsMessage {

    private ErrorsMessage () {}

    public static final String FIELD_NOT_BLANK = "{validation.field.not.blank}";

    public static final String NAME_VALID_SIZE = "{validation.name.valid.size}";

    public static final String EMAIL_VALID_SIZE = "{validation.email.valid.size}";

    public static final String PASSWORD_VALID_SIZE = "{validation.password.valid.size}";

    public static final String CPF_VALID_SIZE = "{validation.cpf.valid.size}";

    public static final String CPF_MUST_BE_NUMERAL = "{validation.cpf.valid.type}";

    public static final String EMAIL_VALID = "{validation.cpf.valid}";

    public static final String PHONE_VALID = "{validation.phone.valid}";

    //Exception messages

    public static final String INVALID_FIELD = "validation.invalid.field";

    public static final String CONFLICTED_FIELD = "validation.conflicted.field";

    public static final String CLIENT_NOT_FOUND = "error.client.not.found";

    public static final String CLIENT_ACCOUNT_NOT_FOUND = "error.client.account.not.found";

    public static final String TRANSFER_TO_YOURSELF = "error.transfer.to.yourself";

    public static final String TRANSFER_LIMIT_OVERPASSED = "error.transfer.limit.overpassed";

    public static final String UNEXPECTED_ERROR = "error.unexpected";

    public static final String DESERIALIZATION_ERROR = "error.deserialization";

    public static final String DELETED_ACCOUNT = "error.deleted.account";

    public static final String REMAINING_BALANCE = "error.remaining.balance";
}
