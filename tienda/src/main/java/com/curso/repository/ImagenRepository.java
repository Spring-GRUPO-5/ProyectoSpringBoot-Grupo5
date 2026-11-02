package com.curso.repository;

import com.curso.model.Imagen;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ImagenRepository extends JpaRepository<Imagen, Long> {

    List<Imagen> findByProductoId(Long productoId);
    @Modifying
    @Query(value = "DELETE FROM imagen WHERE id = :id", nativeQuery = true)
    int eliminarDirectamente(@Param("id") Long id);
}