package sn.afribnpl.clientservice.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.stereotype.Service;
import sn.afribnpl.clientservice.dao.ClientRepository;
import sn.afribnpl.clientservice.dto.ClientDto;
import sn.afribnpl.clientservice.dto.ClientRequest;
import sn.afribnpl.clientservice.enitity.Client;
import sn.afribnpl.clientservice.exceptions.DuplicateEmailException;
import sn.afribnpl.clientservice.exceptions.EmailNotValidException;
import sn.afribnpl.clientservice.mapper.ClientMapper;

import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
public class ClientService implements IClientService {
    private static final Logger log = LoggerFactory.getLogger(ClientService.class);
    private ClientRepository clientRepository;
    private ClientMapper clientMapper;
    private S3Service s3Service;
    @Autowired
    private StreamBridge streamBridge ;


    @Override
    public Optional<ClientRequest> createClient(ClientRequest request) {
        Optional<Client> byEmail = clientRepository.findByEmail(request.getEmail());
        if (byEmail.isPresent()) {
            throw new DuplicateEmailException("L'email "+request.getEmail()+" existe deja veuillez vous connecter directement");
        }

        if (! isEmailValid(request.getEmail())) {
            throw new EmailNotValidException("L'email "+request.getEmail()+" est invalide");
        }

        Client client = clientMapper.toClient(request);
        client.setId(UUID.randomUUID().toString());

        String urlCni = s3Service.uploadFile(client.getId(), request.getCni());

        client.setUrlCni(urlCni);

        log.info("-------------creation du client");

        Client saved = clientRepository.save(client);
        log.info("le client {}  a été creé avec success", saved.toString());
        log.info("envoie des données vers kafka...............");
        streamBridge.send("cni", saved) ;
        log.info("Données envoyer a kafka");

        return Optional.of(clientMapper.toClientRequest(client));
    }

    @Override
    public Optional<ClientDto> getClientById(String clientId) {
        log.info("recuperation d'un client grace a son id ");
        Optional<Client> client = clientRepository.findById(clientId);
        if (client.isPresent()) {
            return Optional.of(clientMapper.toClientDto(client.get())) ;
        }else {
            log.info("L'id {} n'existe pas", clientId);
            throw new EntityNotFoundException("ce client demandé n'existe pas ");
        }

    }

    /**
     * Verification de la validité de l'amail
     * @param email
     * @return
     */
    private boolean isEmailValid(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        return email.matches(emailRegex);
    }
}
