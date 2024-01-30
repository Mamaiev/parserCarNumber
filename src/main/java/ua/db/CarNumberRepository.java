package ua.db;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ua.model.CarNumber;

import java.util.List;

@Repository
public interface CarNumberRepository extends CrudRepository<CarNumber, Integer> {

    boolean existsByNumber(String number);

    @Query("select count(car.id) from CarNumber car")
    long size();

    @Query("select car from CarNumber car where car.number like '%?1%'")
    List<CarNumber> checkNumberByLike(String text);

    CarNumber findByNumberContaining(String number);

    boolean existsByNumberContaining(String number);
}
