package hykar.projects.rspr.entity;


import com.fasterxml.jackson.annotation.*;

import javax.persistence.*;
import java.util.Collection;

@Entity
@Table(name = "post")
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id;

    @Column(nullable = false)
    String message;

    @Column(nullable = false)
    String tags;

    @ManyToOne
    @JoinColumn(name="user")
    @JsonIgnore
    User user;

    @OneToMany(mappedBy = "post")
    @JsonIgnore
    private Collection<Comment> comments;

    public Collection<Comment> getComments() {
        return comments;
    }

    public void setComents(Collection<Comment> comments) {
        this.comments = comments;
    }

    public void addComment(Comment c)
    {
        c.setPost(this);
        this.comments.add(c);
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }






}
