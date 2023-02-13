package ru.job4j.service;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ru.job4j.domain.Person;
import ru.job4j.domain.PersonDTO;
import ru.job4j.repository.PersonRepository;

import java.util.List;
import java.util.Optional;

@Service
public class SimplePersonService implements PersonService {

    private final PersonRepository persons;

    private final BCryptPasswordEncoder encoder;

    public SimplePersonService(PersonRepository persons, BCryptPasswordEncoder encoder) {
        this.persons = persons;
        this.encoder = encoder;
    }

    @Override
    public List<Person> findAll() {
        return persons.findAll();
    }

    @Override
    public Optional<Person> findById(int id) {
        return persons.findById(id);
    }

    @Override
    public Person save(Person person) {
        person.setPassword(encoder.encode(person.getPassword()));
        return persons.save(person);
    }

    @Override
    public void delete(Person person) {
        persons.delete(person);
    }

    public Optional<Person> put(PersonDTO personDTO) {
        Optional<Person> current = persons.findByLogin(personDTO.getLogin());
        if (current.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        current.get().setPassword(encoder.encode(personDTO.getPassword()));
        persons.save(current.get());
        return Optional.of(persons.save(current.get()));
    }

}
