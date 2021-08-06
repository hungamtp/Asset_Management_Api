package com.nashtech.rootkies.model;

import com.nashtech.rootkies.enums.Gender;
import com.nashtech.rootkies.generator.StaffCodeGenerator;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.Collection;

@Entity
@Table(	name = "users",
		uniqueConstraints = { 
			@UniqueConstraint(columnNames = "staffcode"),
			@UniqueConstraint(columnNames = "username")
		},
		indexes ={
			@Index(name = "user_role_idx" , columnList = "roleid"),
				@Index(name = "user_username_idx" , columnList = "username"),
				@Index(name = "user_location_idx" , columnList = "locationid")
		})
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class User {
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "staffcode_generator")
	@GenericGenerator(
		name = "staffcode_generator",
		strategy = "com.nashtech.rootkies.generator.StaffCodeGenerator",
		parameters = {
				@Parameter(name = StaffCodeGenerator.INCREMENT_PARAM, value = "0"),
				@Parameter(name = StaffCodeGenerator.VALUE_PREFIX_PARAMETER, value = "SD"),
				@Parameter(name = StaffCodeGenerator.NUMBER_FORMAT_PARAMETER, value = "%04d") })
	@Column(name = "staffcode")
	private String staffCode;

	@NotBlank
	@Column(name = "username")
	private String username;

	@NotBlank
	@Column(name = "password")
	private String password;

	@Column(name = "firstname")
	private String firstName;

	@Column(name = "lastname")
	private String lastName;

	@Column(name = "dateofbirth")
	private LocalDateTime dateOfBirth;

	@Column(name = "joineddate")
	private LocalDateTime joinedDate;

	@Enumerated(EnumType.STRING)
	@Column(name = "gender")
	private Gender gender;

	@ManyToOne
	@JoinColumn(name = "locationid")
	private Location location;

	@ManyToOne
	@JoinColumn(name = "roleid", nullable = false)
	private Role role;

	@Column(name = "firstlogin")
	private Boolean firstLogin;

	@Column(name = "isdeleted")
	private Boolean isDeleted;

	@OneToMany(mappedBy = "assignedTo")
	private Collection<Assignment> assignmentAssignedTo;

	@OneToMany(mappedBy = "assignedBy")
	private Collection<Assignment> assignmentAssignedBy;

	@OneToMany(mappedBy = "requestedBy")
	private Collection<Request> requestRequestedBy;

	@OneToMany(mappedBy = "acceptedBy")
	private Collection<Request> requestAcceptedBy;
}
