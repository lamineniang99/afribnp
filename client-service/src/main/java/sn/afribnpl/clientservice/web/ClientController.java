package sn.afribnpl.clientservice.web;

import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sn.afribnpl.clientservice.dto.ClientRequest;
import sn.afribnpl.clientservice.service.ClientService;

import java.util.Optional;

@RestController
@RequestMapping("/api/v1/clients")
@AllArgsConstructor
public class ClientController {

    private static final Logger log = LoggerFactory.getLogger(ClientController.class);
    private ClientService clientService;

    @PostMapping
    public ResponseEntity<ClientRequest> createClient(@ModelAttribute ClientRequest request) {
        try{
            Optional<ClientRequest> client = clientService.createClient(request);
            if (client.isPresent()) {
                return new ResponseEntity<>(client.get(), HttpStatus.CREATED);
            }else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }

        }catch (Exception e) {
            log.info("Une erreur s'est produite lors de l'inscriptionn");
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
