package co.edu.javeriana.eas.pica.toures.balon.models.repositories;

import co.edu.javeriana.eas.pica.toures.balon.models.documents.Provider;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface IProviderRepository extends MongoRepository<Provider, String> {

    Optional<List<Provider>> findProviderByType(String type);

    Optional<Provider> findProviderByNameAndAndType(String name, String type);

}
