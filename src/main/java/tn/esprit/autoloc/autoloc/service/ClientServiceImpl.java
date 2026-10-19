package tn.esprit.autoloc.autoloc.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tn.esprit.autoloc.autoloc.domain.Client;
import tn.esprit.autoloc.autoloc.exception.ResourceNotFoundException;
import tn.esprit.autoloc.autoloc.repository.IClientRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ClientServiceImpl implements IClientService {

    private final IClientRepository clientRepository;

    @Override
    public Client create(Client client) {
        if (client.getIdClient() != null) {
            throw new IllegalArgumentException(
                    "Un nouveau client ne doit pas avoir d'identifiant");
        }
        if (client.getNom() == null || client.getNom().trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "Le nom du client est obligatoire");
        }
        if (client.getEmail() == null || client.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "L'email du client est obligatoire");
        }
        return clientRepository.save(client);
    }

    @Override
    public Client findById(Long id) {
        return clientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Client", id));
    }

    @Override
    public List<Client> findAll() {
        return clientRepository.findAll();
    }

    @Override
    public Client update(Long id, Client client) {
        Client existant = findById(id);
        
        if (client.getNom() != null) {
            if (client.getNom().trim().isEmpty()) {
                throw new IllegalArgumentException(
                        "Le nom du client est obligatoire");
            }
            existant.setNom(client.getNom());
        }
        if (client.getPrenom() != null) {
            existant.setPrenom(client.getPrenom());
        }
        if (client.getEmail() != null) {
            if (client.getEmail().trim().isEmpty()) {
                throw new IllegalArgumentException(
                        "L'email du client est obligatoire");
            }
            existant.setEmail(client.getEmail());
        }
        if (client.getTelephone() != null) {
            existant.setTelephone(client.getTelephone());
        }
        if (client.getNumPermis() != null) {
            existant.setNumPermis(client.getNumPermis());
        }
        if (client.getDateInscription() != null) {
            existant.setDateInscription(client.getDateInscription());
        }
        
        return clientRepository.save(existant);
    }

    @Override
    public void deleteById(Long id) {
        if (!clientRepository.existsById(id)) {
            throw new ResourceNotFoundException("Client", id);
        }
        clientRepository.deleteById(id);
    }
}
