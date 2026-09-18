package com.project.sistema_ventas_api.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Data
@MappedSuperclass //Con esto ya se crea una tabla en JPA, pero sí inyectara las columnas en las entidades hijas
@EntityListeners(AuditingEntityListener.class) //Esto escucha cada accion del repositorio y controla el tiempo exacto de las operaciones
public abstract class AuditoriaBase  { //Esta clase sera una plantilla, las demas entidades heredan de aqui para que contengan estos atributos

    @Column(name = "activo")
    private Boolean activo = true;

    @CreatedDate //llena el campo automaticamente con la fecha en la que nace una fila en la bd
    @Column(name = "fecha_creacion", updatable = false) //No se podra actualizar, la fecha de creacion siempe será la misma
    private LocalDateTime fechaCreacion;

    @LastModifiedDate //actualiza automaticamente el campo cada vez que el registro tiene una modificacion
    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;
}
