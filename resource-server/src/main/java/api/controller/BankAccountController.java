package api.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping (value = "/api/v1/bank")
public class BankAccountController {

    @PreAuthorize ("hasAuthority('SCOPE_api.read')")
    @GetMapping (value = "/extract")
    public String extract () {
        return "access granted. Your bank balance is R$1.000.000.000,00!";
    }
}
