package com.example.controllers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.core.io.Resource;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.entities.Correo;
import com.example.entities.Empleado;
import com.example.entities.Telefono;
import com.example.models.FileUploadResponse;
import com.example.services.EmpleadoService;
import com.example.utilities.FileDownloadUtil;
import com.example.utilities.FileUploadUtil;
import com.example.utilities.FileUtil;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/empleados")
@RequiredArgsConstructor
public class EmpleadoController {

    private final EmpleadoService empleadoService;
    private final FileUploadUtil fileUploadUtil;
    private final FileDownloadUtil fileDownloadUtil;
    private final FileUtil fileUtil;

   @GetMapping
    public ResponseEntity<Map<String, Object>> dameEmpleados(
            @RequestParam(name = "page", required = false) Integer page,
            @RequestParam(name = "size", required = false) Integer size) {

        List<Empleado> empleados = null;
        Map<String, Object> responseAsMap = new HashMap<>();
        ResponseEntity<Map<String, Object>> responseEntity = null;
        Sort sort = Sort.by("nombre");

        try {
            if (page != null && size != null) {
                Pageable pageable = PageRequest.of(page, size, sort);
                Page<Empleado> empleadoPage = empleadoService.findAll(pageable);
                empleados = empleadoPage.getContent();
                responseAsMap.put("empleados", empleados);
                responseAsMap.put("totalElementos", empleadoPage.getTotalElements());
                responseAsMap.put("totalPaginas", empleadoPage.getTotalPages());
                responseAsMap.put("paginaActual", empleadoPage.getNumber());
            } else {
                empleados = empleadoService.findAll(sort);
                responseAsMap.put("empleados", empleados);
                responseAsMap.put("totalElementos", empleados.size());
            }

            responseEntity = new ResponseEntity<>(responseAsMap, HttpStatus.OK);

        } catch (DataAccessException e) {
            responseAsMap.put("mensaje", "Error al recuperar los empleados. La causa más probable es: "
                    + e.getMostSpecificCause().getMessage());
            responseEntity = new ResponseEntity<>(responseAsMap, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return responseEntity;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> findEmpleadoById(
            @PathVariable(name = "id", required = true) Integer empleadoId) {

        Map<String, Object> responseAsMap = new HashMap<>();
        ResponseEntity<Map<String, Object>> responseEntity = null;

        try {
            Empleado empleado = empleadoService.findById(empleadoId);

            if (empleado != null) {
                responseAsMap.put("mensaje", "Empleado encontrado con id: " + empleadoId);
                responseAsMap.put("empleado", empleado);
                responseEntity = new ResponseEntity<>(responseAsMap, HttpStatus.OK);
            } else {
                responseAsMap.put("mensaje", "Empleado no encontrado con id: " + empleadoId);
                responseEntity = new ResponseEntity<>(responseAsMap, HttpStatus.NOT_FOUND);
            }

        } catch (DataAccessException e) {
            responseAsMap.put("mensaje", "Error grave al recuperar el empleado con id: "
                    + empleadoId + ". La causa más probable es: "
                    + e.getMostSpecificCause().getMessage());
            responseEntity = new ResponseEntity<>(responseAsMap, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return responseEntity;
    }

    @PostMapping(consumes = "multipart/form-data")
    @Transactional
    public ResponseEntity<Map<String, Object>> saveEmpleado(
            @Valid @RequestPart Empleado empleado,
            BindingResult result, @RequestPart(name = "file", required = false)
            MultipartFile imagenDelEmpleado) {

        List<String> mensajesDeError = new ArrayList<>();
        Map<String, Object> responseAsMap = new HashMap<>();
        ResponseEntity<Map<String, Object>> responseEntity = null;

        if (result.hasErrors()) {
            List<ObjectError> objectErrors = result.getAllErrors();
            objectErrors.forEach(objectError -> mensajesDeError.add(objectError.getDefaultMessage()));

            responseAsMap.put("errores", mensajesDeError);
            responseAsMap.put("empleado mal formado", empleado);

            responseEntity = new ResponseEntity<>(responseAsMap, HttpStatus.BAD_REQUEST);
            return responseEntity;
        }
        /**Antes vamos a comprobar si hemos recibido imagen del empleado para guardarla
         * en el system files 
         */
        if (imagenDelEmpleado != null && !imagenDelEmpleado.isEmpty()) {
            try {
                String fileCode = fileUploadUtil.saveFile(
                        imagenDelEmpleado.getOriginalFilename(),
                        imagenDelEmpleado);

                empleado.setImagen(fileCode + "-" + imagenDelEmpleado.getOriginalFilename());

                FileUploadResponse fileUploadResponse = new FileUploadResponse(
                        fileCode + "-" + imagenDelEmpleado.getOriginalFilename(),
                        "/empleados/fileDownload/" + fileCode,
                        imagenDelEmpleado.getSize()
                );

                responseAsMap.put("informacion de la imagen del empleado", fileUploadResponse);

            } catch (IOException e) {
                responseAsMap.put("mensaje", "Error al guardar la imagen del empleado y la causa más probable es: "
                        + e.getMessage());
                return new ResponseEntity<>(responseAsMap, HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }

        if (empleado.getTelefonos() != null) {
            empleado.getTelefonos().forEach(telefono -> telefono.setEmpleado(empleado));
        }

        if (empleado.getCorreos() != null) {
            empleado.getCorreos().forEach(correo -> correo.setEmpleado(empleado));
        }
            

        try {
            Empleado empleadoPersistido = empleadoService.save(empleado);
            responseAsMap.put("mensaje", "Empleado persistido exitosamente");
            responseAsMap.put("empleado", empleadoPersistido);

            responseEntity = new ResponseEntity<>(responseAsMap, HttpStatus.CREATED);

        } catch (DataAccessException e) {
            responseAsMap.put("mensaje", "Error al persistir el empleado y la causa más probable es: "
                    + e.getMostSpecificCause().getMessage());

            responseEntity = new ResponseEntity<>(responseAsMap, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return responseEntity;
    }


    @PutMapping(value = "/{id}", consumes = "multipart/form-data")
    @Transactional
    public ResponseEntity<Map<String, Object>> updateEmpleado(
            @Valid @RequestPart Empleado empleado,
            BindingResult result,
            @RequestPart(name = "file", required = false) MultipartFile imagenDelEmpleado,
            @PathVariable("id") int empleadoId) throws IOException {

        List<String> mensajesDeError = new ArrayList<>();
        Map<String, Object> responseAsMap = new HashMap<>();
        ResponseEntity<Map<String, Object>> responseEntity = null;

        if (result.hasErrors()) {
            List<ObjectError> objectErrors = result.getAllErrors();
            objectErrors.forEach(objectError -> mensajesDeError.add(objectError.getDefaultMessage()));

            responseAsMap.put("errores", mensajesDeError);
            responseAsMap.put("empleado mal formado", empleado);

            return new ResponseEntity<>(responseAsMap, HttpStatus.BAD_REQUEST);
        }

        Empleado empleadoGuardado = empleadoService.findById(empleadoId);

        if (empleadoGuardado == null) {
            responseAsMap.put("mensaje", "Empleado con id " + empleadoId + " no encontrado");
            return new ResponseEntity<>(responseAsMap, HttpStatus.NOT_FOUND);
        }

        try {
            empleadoGuardado.setNombre(empleado.getNombre());
            empleadoGuardado.setPrimerApellido(empleado.getPrimerApellido());
            empleadoGuardado.setSegundoApellido(empleado.getSegundoApellido());
            empleadoGuardado.setFechaAlta(empleado.getFechaAlta());
            empleadoGuardado.setSalario(empleado.getSalario());
            empleadoGuardado.setGenero(empleado.getGenero());
            empleadoGuardado.setDepartamento(empleado.getDepartamento());

            if (imagenDelEmpleado != null && !imagenDelEmpleado.isEmpty()) {

                if (empleadoGuardado.getImagen() != null) {
                    fileUtil.eliminarArchivo(empleadoGuardado.getImagen());
                }

                String fileCode = fileUploadUtil.saveFile(
                        imagenDelEmpleado.getOriginalFilename(),
                        imagenDelEmpleado);

                String nombreImagen = fileCode + "-" + imagenDelEmpleado.getOriginalFilename();
                empleadoGuardado.setImagen(nombreImagen);

                FileUploadResponse fileUploadResponse = new FileUploadResponse(
                        nombreImagen,
                        "/empleados/fileDownload/" + fileCode,
                        imagenDelEmpleado.getSize()
                );

                responseAsMap.put("informacion de la imagen del empleado", fileUploadResponse);

            } else {
                empleadoGuardado.setImagen(empleadoGuardado.getImagen());
            }

            if (empleado.getCorreos() != null) {
                empleadoGuardado.getCorreos().clear();
                for (Correo correo : empleado.getCorreos()) {
                    correo.setEmpleado(empleadoGuardado);
                    empleadoGuardado.getCorreos().add(correo);
                }
            }

            if (empleado.getTelefonos() != null) {
                empleadoGuardado.getTelefonos().clear();
                for (Telefono telefono : empleado.getTelefonos()) {
                    telefono.setEmpleado(empleadoGuardado);
                    empleadoGuardado.getTelefonos().add(telefono);
                }
            }

            Empleado empleadoActualizado = empleadoService.save(empleadoGuardado);

            responseAsMap.put("mensaje", "Empleado actualizado exitosamente");
            responseAsMap.put("empleado actualizado", empleadoActualizado);

            responseEntity = new ResponseEntity<>(responseAsMap, HttpStatus.CREATED);

        } catch (DataAccessException e) {
            responseAsMap.put("mensaje", "Error al actualizar el empleado y la causa más probable es: "
                    + e.getMostSpecificCause().getMessage());

            responseEntity = new ResponseEntity<>(responseAsMap, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return responseEntity;
    }


    @GetMapping("/fileDownload/{fileCode}")
    public ResponseEntity<?> downloadFile(@PathVariable String fileCode) {

        Resource resource = null;

        try {
            resource = fileDownloadUtil.getFileAsResource(fileCode);
        } catch (IOException e) {
            return ResponseEntity.internalServerError().build();
        }

        if (resource == null) {
            return new ResponseEntity<>("Imagen del empleado no encontrada", HttpStatus.NOT_FOUND);
        }

        String contentType = "application/octet-stream";
        String headerValue = "attachment; fileName=\"" + resource.getFilename() + "\"";

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, headerValue)
                .body(resource);
    }


}