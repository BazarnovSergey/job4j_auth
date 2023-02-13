package ru.job4j.repository;

import lombok.NonNull;
import org.springframework.data.repository.CrudRepository;
import ru.job4j.domain.Person;

import java.util.List;
import java.util.Optional;

public interface PersonRepository extends CrudRepository<Person, Integer> {

    @Override
    @NonNull
    List<Person> findAll();

    Optional<Person> findPersonByLogin(String login);

    Optional<Person> findByLogin(String login);

}