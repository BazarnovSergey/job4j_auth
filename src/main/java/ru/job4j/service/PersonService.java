package ru.job4j.service;

import ru.job4j.domain.Person;
import ru.job4j.domain.PersonDTO;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Optional;

public interface PersonService {
    List<Person> findAll();

    Optional<Person> findById(int id);

    Person save(Person person);

    void delete(Person person);

    Optional<Person> put(PersonDTO personDTO) throws InvocationTargetException, IllegalAccessException;

}
