package org.example.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;

import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AuthGrantedAuthority implements GrantedAuthority {

    @Id
    private UUID id;

    @Column(name = "authority")
    private String authority;

    @ManyToOne
    @JoinColumn(name = "manager_id")
    private Manager manager;
}
