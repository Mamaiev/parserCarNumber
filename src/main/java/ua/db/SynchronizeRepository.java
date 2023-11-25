package ua.db;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ua.model.Synchronize;

@Repository
public interface SynchronizeRepository extends CrudRepository<Synchronize, Integer> {
}
