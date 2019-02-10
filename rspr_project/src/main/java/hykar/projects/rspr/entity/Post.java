package hykar.projects.rspr.entity;


import com.fasterxml.jackson.annotation.JsonIgnore;
import hykar.projects.rspr.compiler.MessageCompiler;

import javax.persistence.*;
import java.util.Collection;
import java.util.Date;

@Entity
@Table(name = "post")
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id;

    @Column(nullable = false,length = 5000)
    private String message;

    @Column(nullable = false)
    private String tags;

    @Column
    private String title;

    @Column
    @Temporal(TemporalType.TIMESTAMP)
    private Date created;

    @ManyToOne
    @JoinColumn(name = "user")
    @JsonIgnore
    User user;

    @OneToMany(mappedBy = "post", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
    @JsonIgnore
    private Collection<Comment> comments;

    public Collection<Comment> getComments() {
        return comments;
    }

    public void setComents(Collection<Comment> comments) {
        this.comments = comments;
    }

    public void addComment(Comment c) {
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

    public String getCompiledMessage(){
        return MessageCompiler.createDefault().compile(this.message);
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

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
