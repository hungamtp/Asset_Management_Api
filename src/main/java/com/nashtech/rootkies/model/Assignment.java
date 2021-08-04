package com.nashtech.rootkies.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Collection;

@Entity
@Table(name = "assignments",
        indexes = {
                @Index(name = "assignment_assignedto_idx" , columnList = "assignedto"),
                @Index(name = "assignment_assetcode_idx", columnList = "assetcode"),
                @Index(name = "assignment_assigneddate_idx", columnList = "assigneddate"),
                @Index(name = "assignment_state_idx", columnList = "state")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Assignment {
    @Id
    @GeneratedValue
    @Column(name = "assignmentid")
    private Long assignmentId;

    @ManyToOne
    @JoinColumn(name = "assignedto", referencedColumnName = "username", nullable = false)
    private User assignedTo;

    @ManyToOne
    @JoinColumn(name = "assignedby", referencedColumnName = "username", nullable = false)
    private User assignedBy;

    @Column(name = "assigneddate")
    private LocalDateTime assignedDate;

    @Column(name = "state")
    private String state;

    @Column(name = "note")
    private String note;

    @ManyToOne
    @JoinColumn(name = "assetcode", nullable = false)
    private Asset asset;

    /*@ManyToOne
    @JoinColumn(name = "staffcode", referencedColumnName = "staffcode")
    private User staffCode;*/

    @Column(name = "isdeleted")
    private Boolean isDeleted;

    @OneToMany(mappedBy = "assignment")
    private Collection<Request> requests;
}
