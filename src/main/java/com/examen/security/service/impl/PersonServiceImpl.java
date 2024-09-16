package com.examen.security.service.impl;

import com.examen.security.aggregates.constants.Constants;
import com.examen.security.aggregates.request.PersonaRequest;
import com.examen.security.aggregates.response.PersonaResponse;
import com.examen.security.aggregates.response.ReniecResponse;
import com.examen.security.client.ReniecClient;
import com.examen.security.controller.customer.ResourceNotFoundException;
import com.examen.security.entity.PersonaEntity;
import com.examen.security.redis.RedisService;
import com.examen.security.repository.PersonaRepository;
import com.examen.security.service.PersonaService;
import com.examen.security.util.Util;
import com.examen.security.util.Validations;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class PersonServiceImpl implements PersonaService {

    @Value("${token.api}")
    private String token;

    @Autowired
    private ReniecClient reniecClient;

    @Autowired
    private PersonaRepository personaRepository;

    @Autowired
    private RedisService redisService;

    @Autowired
    private Validations validations;


    @Override
    public PersonaResponse personByDni(String dni) throws Exception {

        if(validations.validateDni(dni)) throw new IllegalArgumentException("DNI no válido, solo debe ser de 8 dígitos y numérico");

        ReniecResponse reniecResponse = searchFromReniec(dni);

        // Guardar en redis
        String dataForRedis = Util.convertToString(reniecResponse);
        redisService.saveToRedis(Constants.REDIS_KEY_API_PERSON + dni, dataForRedis, Constants.REDIS_EXP);

        return new PersonaResponse(
                Constants.OK_DNI_CODE,
                Constants.USER_FOUND_MESSAGE,
                Optional.of(reniecResponse)
        );
    }

    @Override
    public PersonaResponse updatePerson(String dni, PersonaRequest personaRequest) {

        // Verificar que exista datos de entrada
        if(personaRequest.getEmail().equals(("")) || personaRequest.getPassword().equals((""))){
            return new PersonaResponse(
                    Constants.ERROR_INFORMATION_USER,
                    Constants.USER_NOT_UPDATE,
                    Optional.empty()
            );
        }

        if(validations.validateDni(dni)) throw new IllegalArgumentException("DNI no válido");

        Optional<PersonaEntity> personaEntity = personaRepository.findByNumeroDocumento(dni);
        if(!personaEntity.isPresent()){
            return new PersonaResponse(
                    Constants.ERROR_DNI_CODE_USER,
                    Constants.USER_FOUND_MESSAGE,
                    Optional.empty()
            );
        }

        PersonaEntity person = personaEntity.get();
        person.setEmail(personaRequest.getEmail());
        person.setPassword(new BCryptPasswordEncoder().encode(personaRequest.getPassword()));

        return new PersonaResponse(
                Constants.OK_DNI_CODE,
                Constants.USER_UPDATE_MESSAGE,
                Optional.of(personaRepository.save(person))
        );
    }

    @Override
    public PersonaResponse deletePerson(String dni) {

        if(validations.validateDni(dni)) throw new IllegalArgumentException("DNI no válido");


        Optional<PersonaEntity> personaEntity = personaRepository.findByNumeroDocumento(dni);
        if(Objects.nonNull(personaEntity)){
            personaEntity.get().setEstado(Constants.STATUS_INACTIVE);
            return new PersonaResponse(
                    Constants.OK_DNI_CODE,
                    Constants.USER_STATE_INACTIVE,
                    Optional.of(personaRepository.save(personaEntity.get()))
            );
        }

        PersonaResponse response = new PersonaResponse(
                Constants.ERROR_DNI_CODE,
                Constants.USER_NOT_FOUND_MESSAGE,
                Optional.empty()
        );
        return response;
    }

    @Override
    public PersonaResponse listPerson() {
        Optional<List<PersonaEntity>> listUsers = personaRepository.findByEstado(Constants.STATUS_ACTIVE);
        if(Objects.nonNull(listUsers)){
            int size = listUsers.map(List::size).orElse(0);

            if(size != 0) {
                return new PersonaResponse(
                        Constants.OK_DNI_CODE,
                        Constants.USERS_ACTIVOS_LIST,
                        listUsers
                );
            }

        }
        PersonaResponse response = new PersonaResponse(
                Constants.ERROR_CODE_LIST_EMPTY
                ,Constants.USERS_NOT_FOUND,
                Optional.empty()
        );
        return response;
    }


    @Override
    public UserDetailsService userDetailsService() {
        return new UserDetailsService() {
            @Override
            public UserDetails loadUserByUsername(String username)
                    throws UsernameNotFoundException {
                return personaRepository.findByEmail(username).orElseThrow(
                        ()-> new UsernameNotFoundException("USUARIO NO ENCONTRADO"));
            }
        };
    }


    // Métodos
    private ReniecResponse searchFromReniec(String dni){
        String data = redisService.getDataFromRedis(Constants.REDIS_KEY_API_PERSON + dni);
        if (Objects.nonNull(data)){
            return Util.convertToObject(data, ReniecResponse.class);
        } else {
            String auth = "Bearer " + token;
            ReniecResponse response = reniecClient.getPersonaReniec(dni, auth);
            return response;
        }
    }


}
