# Atelier 4 - Notes sur la couche Service

## A. Modes d'injection comparés

| Critère | Constructeur | Attribut (@Autowired) |
|---------|-------------|----------------------|
| Champ `final` possible | ✅ Oui | ❌ Non |
| Dépendance visible dans la signature | ✅ Oui (dans le constructeur) | ❌ Non (cachée dans la classe) |
| Classe utilisable avec `new` hors conteneur sans dépendance `null` | ✅ Oui (obligation de fournir les dépendances) | ❌ Non (dépendances `null`) |
| Signalé par SonarQube | ❌ Non | ✅ Oui (règle S6813) |

**Conclusion** : L'injection par constructeur est la bonne pratique recommandée car elle garantit l'immutabilité (champs `final`), rend les dépendances explicites et permet la création d'objets toujours valides.

---

## B. Services créés

| Service | Dépendances injectées | Mode d'injection | Justification |
|---------|----------------------|------------------|---------------|
| ContratServiceImpl | IContratRepository | Constructeur | Injection par constructeur avec champ `final` pour garantir l'immutabilité et l'état valide du service. Lombok @RequiredArgsConstructor génère le constructeur automatiquement. |
| AgenceServiceImpl | IAgenceRepository | Constructeur | Même justification : champ `final`, dépendance explicite, immutabilité garantie. |
| EmployeServiceImpl | IEmployeRepository | Constructeur | Injection par constructeur pour assurer que le service ne peut jamais exister sans son repository. |
| VehiculeServiceImpl | IVehiculeRepository | Constructeur | Champ `final` + @RequiredArgsConstructor pour une injection propre et immutable. |
| EquipementServiceImpl | IEquipementRepository | Constructeur | Dépendance obligatoire injectée par constructeur, garantissant un objet toujours valide. |
| ClientServiceImpl | IClientRepository | Constructeur | Injection par constructeur avec champ `final` pour l'immutabilité et la clarté des dépendances. |
| ReservationServiceImpl | IReservationRepository | Constructeur | Même justification : injection par constructeur, champ `final`, dépendances explicites. |
| PaiementServiceImpl | IPaiementRepository | Constructeur | Service en lecture seule mais avec la même approche d'injection par constructeur. |
| MaintenanceServiceImpl | IMaintenanceRepository | Constructeur | Injection par constructeur pour garantir l'immutabilité et la validité du service. |

---

## C. Messages d'erreur du conteneur

### Message 1 : NoSuchBeanDefinitionException

**Description** :
```
Parameter 0 of constructor in tn.esprit.autoloc.web.controller.ContratController
required a bean of type 'tn.esprit.autoloc.service.IContratService'
that could not be found.
```

**Causes probables** :
1. L'annotation `@Service` est absente sur `ContratServiceImpl`
2. La classe `ContratServiceImpl` est placée en dehors du package scanné (hors de `tn.esprit.autoloc`)

**Correction** :
- Ajouter `@Service` sur la classe d'implémentation
- Vérifier que la classe est bien dans le package `tn.esprit.autoloc` ou ses sous-packages

---

### Message 2 : NoUniqueBeanDefinitionException

**Description** :
```
Parameter 0 of constructor required a single bean, but 2 were found:
 - emailNotificateur
 - smsNotificateur
```

**Correction 1** : Utiliser `@Primary` (sur la classe du bean préféré)
```java
@Service
@Primary
public class EmailNotificateur implements INotificateur {
    // ...
}
```

**Correction 2** : Utiliser `@Qualifier` (sur le paramètre du constructeur)
```java
public NotificationController(@Qualifier("emailNotificateur") INotificateur notificateur) {
    this.notificateur = notificateur;
}
```

---

### Message 3 : Dépendance circulaire

**Description** :
```
The dependencies of some of the beans form a cycle:
 clientServiceImpl
 | ^
 v |
 reservationServiceImpl
```

**Explication** :
L'injection par constructeur rend ce cycle impossible à résoudre car :
- Pour créer `ClientServiceImpl`, Spring doit d'abord créer `ReservationServiceImpl`
- Mais pour créer `ReservationServiceImpl`, Spring doit d'abord créer `ClientServiceImpl`
- → Cycle infini impossible à résoudre

**Piste de conception** :
- Extraire la responsabilité commune dans un troisième service
- Revoir la conception : un service ne devrait pas avoir de dépendance bidirectionnelle
- Utiliser des événements ou un médiateur pour découpler les services

---

## D. Anomalies SonarQube for IDE

| Règle | Explication | Correction apportée |
|-------|-------------|---------------------|
| S6813 | Injection par attribut (`@Autowired` sur un champ) déconseillée car rend les dépendances cachées et empêche l'utilisation de champs `final` | Remplacement par injection par constructeur avec `@RequiredArgsConstructor` |
| S112 | Lever une exception générique (`RuntimeException`) au lieu d'une exception métier spécifique | Création et utilisation de `ResourceNotFoundException` |
| S1068 | Import inutilisé détecté dans les classes | Suppression des imports inutiles |
| S1172 | Paramètre de méthode non utilisé | Suppression du paramètre ou utilisation effective |
| S2589 | Condition toujours vraie ou fausse | Simplification ou suppression de la condition |

---

## E. Questions de compréhension

### 1. Où est le `new` de `ContratServiceImpl` et qui l'exécute ?

Le `new ContratServiceImpl(...)` n'apparaît nulle part dans notre code. C'est le **conteneur Spring IoC** (représenté par `ApplicationContext`) qui :
- Détecte la classe annotée `@Service` lors du scan des composants
- Crée une instance de `ContratServiceImpl` en appelant son constructeur
- Injecte automatiquement `IContratRepository` en résolvant la dépendance
- Gère le cycle de vie du bean (singleton par défaut)

### 2. Pourquoi un contrôleur dépendra-t-il de `IContratService` et non de `ContratServiceImpl` ?

**Principe de dépendance aux abstractions (Dependency Inversion Principle)** :
- Permet de changer l'implémentation sans modifier le contrôleur
- Facilite les tests unitaires (on peut fournir un mock de `IContratService`)
- Réduit le couplage entre les couches
- Respecte les principes SOLID

Exemple : si on crée `ContratServiceImplV2`, on peut changer l'implémentation en modifiant juste l'annotation, sans toucher au contrôleur.

### 3. Pourquoi un service singleton doit-il rester sans état ? Donner un exemple de bug.

Un service singleton est **partagé par tous les threads** (toutes les requêtes HTTP). Si un service contient un état modifiable, cela crée des **conditions de course** (race conditions).

**Exemple de bug** :
```java
@Service
public class ContratServiceImpl {
    private Contrat contratCourant; // ❌ ERREUR : état modifiable
    
    public void traiterContrat(Long id) {
        this.contratCourant = contratRepository.findById(id).get();
        // Traitement long...
        // Pendant ce temps, un autre thread peut modifier contratCourant !
        calculerMontant(this.contratCourant); // Bug : mauvais contrat !
    }
}
```

**Solution** : Toutes les données doivent être passées en paramètres de méthodes, jamais stockées comme attributs de classe.

### 4. Pourquoi `update` charge-t-il l'entité existante avant d'appeler `save` au lieu de lui passer directement l'objet reçu ?

**Raisons** :
1. **Éviter l'écrasement de données** : Si on passe directement l'objet reçu, toutes les colonnes non renseignées seraient mises à `null`
2. **Préserver les collections** : Avec `orphanRemoval=true`, passer un objet détaché viderait les collections enfants (ex: les paiements d'un contrat)
3. **Contrôle précis** : On copie uniquement les attributs modifiables, protégeant l'ID et les relations

**Exemple** :
```java
// ❌ Mauvaise pratique
public Contrat update(Long id, Contrat contrat) {
    contrat.setId(id);
    return contratRepository.save(contrat); // Écrase tout, y compris les paiements !
}

// ✅ Bonne pratique
public Contrat update(Long id, Contrat contrat) {
    Contrat existant = findById(id);
    existant.setMontantTotal(contrat.getMontantTotal()); // Copie sélective
    return contratRepository.save(existant);
}
```

### 5. Questions complémentaires

**Quelle différence entre `@Component` et `@Bean` ?**
- `@Component` : Annotation de classe, détection automatique par scan de composants
- `@Bean` : Annotation de méthode dans une classe `@Configuration`, déclaration explicite (utile pour des classes qu'on ne peut pas annoter, comme celles de bibliothèques tierces)

**Entre `@Primary` et `@Qualifier` ?**
- `@Primary` : Marque un bean comme candidat par défaut quand plusieurs beans du même type existent
- `@Qualifier` : Choix explicite d'un bean spécifique par son nom lors de l'injection

**Pourquoi `IPaiementService` n'expose-t-il ni création, ni modification, ni suppression ?**

Les paiements sont des **entités dépendantes** du Contrat :
- Ils sont créés via `Contrat.addPaiement()`
- Ils sont supprimés automatiquement par cascade lors de la suppression du contrat
- Leur cycle de vie est entièrement contrôlé par le Contrat (agrégat)
- Le service Paiement ne propose donc que la lecture (`findById`, `findAll`)

---

## Conclusion

L'Atelier 4 a permis de :
- ✅ Créer 9 services avec injection par constructeur
- ✅ Implémenter la logique métier (validations, exceptions)
- ✅ Comprendre le conteneur Spring IoC et l'injection de dépendances
- ✅ Appliquer les bonnes pratiques (champs `final`, interfaces, singleton sans état)
- ✅ Analyser le code avec SonarQube for IDE

L'application démarre sans erreur et affiche :
```
Found 9 JPA repository interfaces
Started AutolocApiApplication
```

Les services seront testés à l'Atelier 5 via les contrôleurs REST et Postman.
