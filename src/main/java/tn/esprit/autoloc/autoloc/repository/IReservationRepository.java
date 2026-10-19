package tn.esprit.autoloc.autoloc.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.esprit.autoloc.autoloc.domain.Reservation;

public interface IReservationRepository extends JpaRepository<Reservation, Long> {
}
