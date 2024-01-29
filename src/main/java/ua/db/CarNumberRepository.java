package ua.db;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ua.model.CarNumber;

@Repository
public interface CarNumberRepository extends CrudRepository<CarNumber, Integer> {

    boolean existsByNumber(String number);

    @Query("select count(car.id) from CarNumber car")
    long size();
}
