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
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
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
    public PersonaResponse createPerson(PersonaRequest personaRequest) {
        try {
            PersonaEntity persona = getPersonByEntity(personaRequest);

            // Guardar en la BD
            if(Objects.nonNull(persona)){
                return new PersonaResponse(
                        Constants.OK_DNI_CODE,
                        Constants.USER_CREATE_MESSAGE,
                        Optional.of(personaRepository.save(persona))
                );
            }
            return null;
        }catch (DataIntegrityViolationException e){
            PersonaResponse response = new PersonaResponse(
                    Constants.ERROR_DNI_CODE,
                    Constants.USER_EXISTS_DNI,
                    Optional.empty()
            );
            return response;

        } catch (Exception e){
            PersonaResponse response = new PersonaResponse(
                    Constants.ERROR_DNI_CODE,
                    Constants.USER_NOT_FOUND_MESSAGE,
                    Optional.empty()
            );
            return response;
        }
    }

    @Override
    public PersonaResponse personByDni(String dni) throws Exception {

        Boolean isValidDni = validations.validateDni(dni);
        if(isValidDni) {
            throw new IllegalArgumentException("DNI no válido");
        }

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
        if(personaRequest.getApellidoPaterno().equals(("")) || personaRequest.getNombres().equals((""))){
            return new PersonaResponse(
                    Constants.ERROR_INFORMATION_USER,
                    Constants.USER_NOT_UPDATE,
                    Optional.empty()
            );
        }

        Boolean isValidDni = validations.validateDni(dni);
        if(isValidDni) {
            throw new IllegalArgumentException("DNI no válido");
        }

        Optional<PersonaEntity> personaEntity = personaRepository.findByNumeroDocumento(dni);
        if(!personaEntity.isPresent()){
            return new PersonaResponse(
                    Constants.ERROR_DNI_CODE_USER,
                    Constants.USER_FOUND_MESSAGE,
                    Optional.empty()
            );
        }

        PersonaEntity person = personaEntity.get();
        person.setNombres(personaRequest.getNombres());
        person.setApellidoPaterno(personaRequest.getApellidoPaterno());

        return new PersonaResponse(
                Constants.OK_DNI_CODE,
                Constants.USER_UPDATE_MESSAGE,
                Optional.of(personaRepository.save(person))
        );
    }

    @Override
    public PersonaResponse deletePerson(String dni) {

        Boolean isValidDni = validations.validateDni(dni);
        if(isValidDni) {
            throw new IllegalArgumentException("DNI no válido");
        }

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


    // Métodos

    private PersonaEntity getPersonByEntity(PersonaRequest personaRequest){
        String auth = "Bearer " + token;
        PersonaEntity personaEntity = new PersonaEntity();
        // Busca la persona en la API externa
        ReniecResponse response = reniecClient.getPersonaReniec(personaRequest.getNumDoc(), auth);
        if(Objects.nonNull(response)){
            // Almacenar la respuesta en el objeto
            personaEntity.setNombres(response.getNombres());
            personaEntity.setApellidoPaterno(response.getApellidoPaterno());
            personaEntity.setApellidoMaterno(response.getApellidoMaterno());
            personaEntity.setTipoDocumento(response.getTipoDocumento());
            personaEntity.setNumeroDocumento(response.getNumeroDocumento());
            personaEntity.setDigitoVerificador(response.getDigitoVerificador());
            personaEntity.setUsua_crea(Constants.USU_CREA);
            personaEntity.setEstado(Constants.STATUS_ACTIVE);
            personaEntity.setDate_crea(new Timestamp(System.currentTimeMillis()));
            return personaEntity;
        }
        return null;
    }

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
