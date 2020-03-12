-----------------
# MPRelation #
-----------------

mybatis-plus relations one2one one2many many2many

mprelation(  AutoMapper : one2one/one2many/many2many)

not XML and not SQL (like hibernate)

==========
One to Many /  Many To One
==========


@Data
public class Company {
    @TableId(value = "company_id")
    private Long id;
    private String name;
    
    //one to many
    @TableField(exist = false)
    @OneToMany
    @JoinColumn(name="company_id",referencedColumnName = "company_id")
    private Set<Man> employees;
}



  
@Data
public class Man {
    @TableId(value = "man_id")
    private Long id;
    private String name;

    //many to one
    @TableField("company_id")
    private Long companyId;
    
    @TableField(exist = false)
    @ManyToOne
    @JoinColumn(name = "company_id", referencedColumnName = "company_id")
    private Company company;
}

one to many / many to one table:
company:     compnay_id      name
man:         man_id          name      company_id

==========
One To One
==========
@Data
public class Woman {
    @TableId(value = "woman_id")
    private Long id;
    private String name;
    
    //one to one
    @TableField("lao_gong_id")
    private Long laoGongId;
    
    @TableField(exist = false)
    @OneToOne
    @JoinColumn(name = "lao_gong_id", referencedColumnName = "man_id")
    private Man laoGong;
}


@Data
public class Man {
    @TableId(value = "man_id")
    private Long id;
    private String name;

    //one to one
    @TableField("lao_po_id")
    private Long laoPoId;
    
    @TableField(exist = false)
    @OneToOne
    @JoinColumn(name = "lao_po_id", referencedColumnName = "woman_id")
    private Woman laoPo;
}

one to one table:
woman:       woman_id       name         lao_gong_id
man:         man_id         name         lao_po_id

==========
Many To Many
==========
@Data
public class Course {
    @TableId(value = "course_id")
    private Long id;
    private String name;

    //many to many
    @TableField(exist = false)
    @ManyToMany
    @JoinTable(targetMapper = StudentCourseMapper.class)
    @JoinColumn(name = "course_id", referencedColumnName = "course_id")
    @InverseJoinColumn(name = "child_id", referencedColumnName = "student_id")
    private List<Child> students;
}

@Data
public class Child {
    @TableId("child_id")
    private Long id;
    private String name;
    
    //many to many
    @TableField(exist = false)
    @ManyToMany
    @JoinTable(targetMapper=StudentCourseMapper.class)
    @JoinColumn(name = "child_id", referencedColumnName = "student_id")
    @InverseJoinColumn(name = "course_id", referencedColumnName = "course_id")
    private List<Course> courses;
}

@Data
public class StudentCourse {
    private Long existOrNotId;
    
    @TableField("student_id")
    private Long studentId;
    
    @TableField("course_id")
    private Long courseId;
}

many to many table:
course:             course_id      name
child:              child_id       name
student_course:     existOrNotId             student_id          course_id
