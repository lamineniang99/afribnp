package sn.afribnpl.clientservice.mapper;


import org.mapstruct.Mapper;
import sn.afribnpl.clientservice.dto.ClientDto;
import sn.afribnpl.clientservice.dto.ClientRequest;
import sn.afribnpl.clientservice.enitity.Client;

@Mapper
public interface ClientMapper {
    Client toClient(ClientRequest clientRequest);
    ClientRequest toClientRequest(Client client);
    ClientDto toClientDto(Client client);
}
