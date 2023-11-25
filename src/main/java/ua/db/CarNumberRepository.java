package ua.db;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ua.model.CarNumber;

@Repository
public interface CarNumberRepository extends CrudRepository<CarNumber, Integer> {
}
