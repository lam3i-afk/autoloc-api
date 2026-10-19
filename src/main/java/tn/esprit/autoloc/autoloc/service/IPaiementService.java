package tn.esprit.autoloc.autoloc.service;

import tn.esprit.autoloc.autoloc.domain.Paiement;

import java.util.List;

public interface IPaiementService {
    Paiement findById(Long id);
    List<Paiement> findAll();
}
