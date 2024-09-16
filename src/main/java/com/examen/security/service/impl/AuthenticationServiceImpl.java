package com.examen.security.service.impl;


import com.examen.security.aggregates.constants.Constants;
import com.examen.security.aggregates.request.PersonaLogInRequest;
import com.examen.security.aggregates.request.PersonaRequest;
import com.examen.security.aggregates.response.LogInResponse;
import com.examen.security.aggregates.response.PersonaResponse;
import com.examen.security.aggregates.response.ReniecResponse;
import com.examen.security.client.ReniecClient;
import com.examen.security.entity.PersonaEntity;
import com.examen.security.entity.Role;
import com.examen.security.entity.RoleEntity;
import com.examen.security.repository.PersonaRepository;
import com.examen.security.repository.RoleRepository;
import com.examen.security.service.AuthenticationService;
import com.examen.security.service.JsonWebTokenService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class AuthenticationServiceImpl implements AuthenticationService {

    @Value("${token.api}")
    private String token;

    @Autowired
    private PersonaRepository personaRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JsonWebTokenService jsonWebTokenService;

    @Autowired
    private ReniecClient reniecClient;

    @Override
    public PersonaResponse registerToUser(PersonaRequest personaRequest) {

        // Registra al usuario
        try {
            // Verificar que exista datos de entrada
            if(personaRequest.getEmail().equals("") || personaRequest.getPassword().equals("")){
                return new PersonaResponse(
                        Constants.ERROR_INFORMATION_USER,
                        Constants.USER_NOT_UPDATE,
                        Optional.empty()
                );
            }

            PersonaEntity persona = getPersonByEntity(personaRequest);

            // Guardar en la BD
            if(Objects.nonNull(persona)){
                persona.setRoles(Collections.singleton(getRoles(Role.USER)));
                persona.setPassword(new BCryptPasswordEncoder().encode(personaRequest.getPassword()));

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

    // Registra al admin

    @Override
    public PersonaResponse registerToAdmin(PersonaRequest personaRequest) {
        try {
            // Verificar que exista datos de entrada
            if(personaRequest.getEmail().equals("") || personaRequest.getPassword().equals("")){
                return new PersonaResponse(
                        Constants.ERROR_INFORMATION_USER,
                        Constants.USER_NOT_UPDATE,
                        Optional.empty()
                );
            }

            PersonaEntity persona = getPersonByEntity(personaRequest);

            // Guardar en la BD
            if(Objects.nonNull(persona)){
                persona.setRoles(Collections.singleton(getRoles(Role.ADMIN)));
                persona.setPassword(new BCryptPasswordEncoder().encode(personaRequest.getPassword()));

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


    // Genera el token

    @Override
    public LogInResponse login(PersonaLogInRequest personaLogInRequest) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                personaLogInRequest.getEmail(),personaLogInRequest.getPassword()));
        var user = personaRepository.findByEmail(personaLogInRequest.getEmail())
                .orElseThrow(()-> new IllegalArgumentException("ERROR USUARIO NO ENCONTRADO"));
        var token = jsonWebTokenService.generateToken(user);
        LogInResponse response = new LogInResponse();
        response.setToken(token);
        return response;
    }


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
            personaEntity.setEmail(personaRequest.getEmail());
            personaEntity.setPassword(personaRequest.getPassword());
            personaEntity.setDate_crea(new Timestamp(System.currentTimeMillis()));
            personaEntity.setUsua_crea(Constants.USU_CREA);
            personaEntity.setEstado(Constants.STATUS_ACTIVE);
            personaEntity.setIsAccountNonExpired(Constants.STATUS_ACTIVE);
            personaEntity.setIsAccountNonLocked(Constants.STATUS_ACTIVE);
            personaEntity.setIsCredentialsNonExpired(Constants.STATUS_ACTIVE);
            personaEntity.setIsEnabled(Constants.STATUS_ACTIVE);

            return personaEntity;
        }
        return null;
    }

    private RoleEntity getRoles(Role rolBuscado){
        return roleRepository.findByNombreRol(rolBuscado.name())
                .orElseThrow(() -> new RuntimeException("EL ROL BSUCADO NO EXISTE : "
                        + rolBuscado.name()));
    }
}
