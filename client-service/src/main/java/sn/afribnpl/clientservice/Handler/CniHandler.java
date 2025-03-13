package sn.afribnpl.clientservice.Handler;

import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import sn.afribnpl.clientservice.dao.ClientRepository;
import sn.afribnpl.clientservice.enitity.Client;
import sn.afribnpl.clientservice.service.ClientService;

import java.util.function.Consumer;

@AllArgsConstructor
@Component
public class CniHandler {

    private static final Logger log = LoggerFactory.getLogger(CniHandler.class);
    private ClientService clientService;
    private ClientRepository clientRepository;

    @Bean
    public Consumer<Client> cniResponseConsumer(){
        return (client)->{
            log.info("***************le client recue depuis le ocr-service : {} ", client.toString());
            if (client.isCniVerified()){
                Client saved = clientRepository.save(client);
                log.info("Le client avec cni verifié {}", saved.toString());
                log.info("Envoie de message pour notifier que ton compte est activé");
            }
        } ;
    }
}
