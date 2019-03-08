package com.example.demo.Repositories;

import com.example.demo.Models.Car;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Set;

public interface CarRepository extends CrudRepository<Car, Long> {

    Car findById(long id);
    List<Car> findByCategoryId(long id);
    List<Car> findAllByDeletedIsFalse();
}
