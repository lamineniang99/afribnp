package sn.afribnpl.clientservice.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import sn.afribnpl.clientservice.dao.ClientRepository;
import sn.afribnpl.clientservice.dto.ClientRequest;

import java.util.Optional;

@Service
@AllArgsConstructor
public class ClientService implements IClientService {
    private ClientRepository clientRepository;


    @Override
    public Optional<ClientRequest> createClient(ClientRequest request) {

        return null;
    }
}
