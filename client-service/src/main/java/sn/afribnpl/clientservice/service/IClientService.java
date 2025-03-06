package sn.afribnpl.clientservice.service;

import sn.afribnpl.clientservice.dto.ClientDto;
import sn.afribnpl.clientservice.dto.ClientRequest;

import java.util.Optional;

public interface IClientService {

    Optional<ClientRequest> createClient(ClientRequest request);
    Optional<ClientDto> getClientById(String clientId);
}
