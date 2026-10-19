package tn.esprit.autoloc.autoloc.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.esprit.autoloc.autoloc.domain.Agence;

public interface IAgenceRepository extends JpaRepository<Agence, Long> {
}
