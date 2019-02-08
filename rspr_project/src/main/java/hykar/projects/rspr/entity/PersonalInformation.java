package hykar.projects.rspr.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;

@Entity
@Table(name = "information")
public class PersonalInformation {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String name;
    private String city;
    private String favouriteSpecies;
    private boolean shown;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user")
    @JsonIgnore
    private User user;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getFavouriteSpecies() {
        return favouriteSpecies;
    }

    public void setFavouriteSpecies(String favouriteSpecies) {
        this.favouriteSpecies = favouriteSpecies;
    }

    public boolean isShown() {
        return shown;
    }

    public void setShown(boolean shown) {
        this.shown = shown;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
