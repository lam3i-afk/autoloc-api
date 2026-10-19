package tn.esprit.autoloc.autoloc.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.esprit.autoloc.autoloc.domain.Equipement;

public interface IEquipementRepository extends JpaRepository<Equipement, Long> {
}
