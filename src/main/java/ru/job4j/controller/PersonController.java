package ru.job4j.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import ru.job4j.domain.Person;
import ru.job4j.repository.PersonRepository;

import java.util.List;
import java.util.Optional;

/**
 * контроллер для регистрации пользователя и
 * получения списка всех пользователей системы
 */
@RestController
@RequestMapping("/person")
public class PersonController {
    private final PersonRepository persons;
    private final BCryptPasswordEncoder encoder;

    public PersonController(final PersonRepository persons, BCryptPasswordEncoder encoder) {
        this.persons = persons;
        this.encoder = encoder;
    }

    @GetMapping("/")
    public List<Person> findAll() {
        return (List<Person>) this.persons.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Person> findById(@PathVariable int id) {
        var person = this.persons.findById(id);
        return new ResponseEntity<Person>(
                person.orElse(new Person()),
                person.isPresent() ? HttpStatus.OK : HttpStatus.NOT_FOUND
        );
    }

    @PostMapping("/")
    public ResponseEntity<Person> create(@RequestBody Person person) {
        return new ResponseEntity<Person>(
                this.persons.save(person),
                HttpStatus.CREATED
        );
    }

    @PutMapping("/")
    public ResponseEntity<Void> update(@RequestBody Person person) {
        var updatedPersonOptional = Optional.of(this.persons.save(person));
        return updatedPersonOptional.isPresent() ? ResponseEntity.ok().build() : ResponseEntity.badRequest().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable int id) {
        Person person = new Person();
        var deletedPerson = this.persons.findById(id);
        if (deletedPerson.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        person.setId(id);
        this.persons.delete(person);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/sign-up")
    public void signUp(@RequestBody Person person) {
        person.setPassword(encoder.encode(person.getPassword()));
        persons.save(person);
    }
}