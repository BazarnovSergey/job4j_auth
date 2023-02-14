package ru.job4j.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.job4j.domain.Person;
import ru.job4j.domain.PersonDTO;
import ru.job4j.service.PersonService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * контроллер для регистрации пользователя и
 * получения списка всех пользователей системы
 */
@RestController
@RequestMapping("/person")
public class PersonController {
    private final PersonService persons;

    private final ObjectMapper objectMapper;

    private static final Logger LOGGER = LoggerFactory.getLogger(PersonController.class.getSimpleName());

    public PersonController(PersonService persons, ObjectMapper objectMapper) {
        this.persons = persons;
        this.objectMapper = objectMapper;
    }

    @GetMapping("/")
    public ResponseEntity<List<Person>> findAll() {
        return new ResponseEntity<>(this.persons.findAll(), HttpStatus.OK);
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
    public ResponseEntity<Person> create(@Valid @RequestBody Person person) {
        if (Objects.equals(person.getLogin(), "") || Objects.equals(person.getPassword(), "")) {
            throw new NullPointerException("login and password mustn't be empty");
        }
        return new ResponseEntity<Person>(
                this.persons.save(person),
                HttpStatus.CREATED
        );
    }

    @PatchMapping("/")
    public ResponseEntity<Void> update(@RequestBody PersonDTO personDTO) throws InvocationTargetException, IllegalAccessException {
        if (Objects.equals(personDTO.getLogin(), "") || Objects.equals(personDTO.getPassword(), "")) {
            throw new NullPointerException("login and password mustn't be empty");
        }
        Optional<Person> updatedPersonOptional = this.persons.put(personDTO);
        return updatedPersonOptional.isPresent() ? ResponseEntity.ok().build() : ResponseEntity.badRequest().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable int id) {
        Person person = new Person();
        var deletedPerson = this.persons.findById(id);
        if (deletedPerson.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Account is not found. Please, check requisites.");
        }
        person.setId(id);
        this.persons.delete(person);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/sign-up")
    public void signUp(@Valid @RequestBody Person person) {
        if (Objects.equals(person.getLogin(), "") || Objects.equals(person.getPassword(), "")) {
            throw new NullPointerException("login and password mustn't be empty");
        }
        if (person.getPassword().length() < 6) {
            throw new IllegalArgumentException("Invalid password. Password length must be more than 5 characters.");
        }
        persons.save(person);
    }

    @ExceptionHandler(value = {IllegalArgumentException.class})
    public void exceptionHandler(Exception e, HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setStatus(HttpStatus.BAD_REQUEST.value());
        response.setContentType("application/json");
        response.getWriter().write(objectMapper.writeValueAsString(new HashMap<>() {
            {
                put("message", e.getMessage());
                put("type", e.getClass());
            }
        }));
        LOGGER.error(e.getLocalizedMessage());
    }

}