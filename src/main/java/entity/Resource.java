package entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

//Zasobem będzie zestaw sal konferencyjnych

@NoArgsConstructor
@Entity
@Table(name = "resource")
@Data
public class Resource {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "name")
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "isActive")
    private ResourceStatus resourceStatus;

    @Enumerated
    @Column(name = "location")
    private Location location;

    @Column(name = "room_number")
    private String roomNumber;

    @Column(name = "capacity")
    private  int capacity;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "resource", fetch = FetchType.LAZY)
    private List<Reservation> reservations;


    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }


}
