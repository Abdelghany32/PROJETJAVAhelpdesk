package com.brub.ticketer.repository;

import com.brub.ticketer.model.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import java.util.List;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {
    List<Ticket> findByAgentNullAndSector(Sector sector);
    List<Ticket> findByStudentId(long studentId);
    List<Ticket> findByStatusAndStudentId(Status status, Long id);
    List<Ticket> findByAgentIdAndSector(long id, Sector sector);
    // Trouver tous les tickets d'un secteur avec un statut spécifique
    List<Ticket> findByStatusAndSector(Status status, Sector sector);
    List<Ticket> findByPriorityAndStudentId(Priority priority, Long id);
    List<Ticket> findBySector(Sector sector);
    List<Ticket> findByPriorityAndSector(Priority priority, Sector sector);
    List<Ticket> findBySectorAndAgentIdNot(Sector sector, Long agentId);




    @Query("SELECT t FROM Ticket t WHERE t.student = :student AND t.sector = :sector AND t.status = :status ORDER BY t.creationDate DESC")
    List<Ticket> findFirstTicketByStudentAndSectorAndStatus(
            @Param("student") Student student,
            @Param("sector") Sector sector,
            @Param("status") Status status,
            Pageable pageable
    );
    @Query("SELECT DISTINCT t.student FROM Ticket t WHERE t.sector = :sector")
    List<Student> findDistinctStudentsBySector(@Param("sector") Sector sector);

    @Query("SELECT DISTINCT t.student FROM Ticket t WHERE t.sector = :sector AND t.status = :status")
    List<Student> findDistinctStudentsBySectorAndStatus(
            @Param("sector") Sector sector,
            @Param("status") Status status
    );

    @Query("SELECT t FROM Ticket t WHERE t.student = :student AND t.sector = :sector ORDER BY t.creationDate DESC")
    List<Ticket> findFirstTicketByStudentAndSector(@Param("student") Student student,
                                                   @Param("sector") Sector sector,
                                                   Pageable pageable);

    // Ajout d'une méthode pour trouver des tickets avec filtrage multiple
    @Query("SELECT t FROM Ticket t WHERE (:status is null OR t.status = :status) AND (:priority is null OR t.priority = :priority) AND t.student.id = :studentId ORDER BY t.priority DESC, t.creationDate DESC")
    List<Ticket> findTicketsWithFilters(Status status, Priority priority, Long studentId);

    @Query("SELECT t FROM Ticket t WHERE (:status is null OR t.status = :status) AND (:priority is null OR t.priority = :priority) AND ((:agentId is null AND t.agent is null) OR t.agent.id = :agentId) AND (:sector is null OR t.sector = :sector) ORDER BY t.priority DESC, t.creationDate DESC")
    List<Ticket> findAgentTicketsWithFilters(Status status, Priority priority, Long agentId, Sector sector);
}