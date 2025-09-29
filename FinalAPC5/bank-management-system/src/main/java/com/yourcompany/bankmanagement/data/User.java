package com.yourcompany.bankmanagement.data;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true, nullable = false)
    private String username;
    private String password;
    private String role; // e.g., "USER", "BANK_PERSON", "ADMIN"
    @Column(unique = true, nullable = false)
    private String userId; // Unique user identifier for display
    @Column(unique = true)
    private String bankPersonId; // Unique bank person identifier for bank staff
}