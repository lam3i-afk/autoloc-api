package tn.esprit.autoloc.autoloc.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.esprit.autoloc.autoloc.domain.Client;

public interface IClientRepository extends JpaRepository<Client, Long> {
}
