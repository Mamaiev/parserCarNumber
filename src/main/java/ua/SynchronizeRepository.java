package ua;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SynchronizeRepository extends CrudRepository<Synchronize, Integer> {
}
