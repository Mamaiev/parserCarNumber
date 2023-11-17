package ua;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CarNumberRepository extends CrudRepository<CarNumber, Integer> {
}
