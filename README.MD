-----------------
# MPRelation #
-----------------

mybatis-plus relations one2one one2many many2many

mprelation(  AutoMapper : one2one/one2many/many2many)

not XML and not SQL (like hibernate)

对于一对一，一对多，多对一，多对多的关联查询，Mybatis-Plus 在处理时，需要编写关联查询方法及配置resultMap，并且书写SQL。

为了简化这种操作，可以自定义注解来简化。

 

 

注解工具源码及jar包地址：

https://github.com/dreamyoung/mprelation.git   

 https://gitee.com/dreamyoung/mprelation.git

测试项目地址(GITHUB)  ：

https://github.com/dreamyoung/mprelation_demo.git 

https://gitee.com/dreamyoung/mprelation_demo.git

 

POM引用 ：

<dependency>
     <groupId>com.github.dreamyoung</groupId>
     <artifactId>mprelation</artifactId>
     <version>0.0.3-RELEASE</version> 
</dependency>

 

 

注解工具使用公优缺点：

优点：

       使用简单，通过在实体类上添加@OneToOne / @OneToMany /  @ManyToOne /  @ManyToMany  等注解即可。

       1对1、1对多、多对1、多对多映射时，可以不再写SQL及XML配置文件，免去配置冗长的<resultMap>的麻烦。

       Service层及Mapper层不需要再添加 getLinkById 、 selectLinkById   之类的方法来关联映射

       重写过的ServiceImpl各种内置的查询方法都自动关联查询，非内置方法可以调用autoMapper相关方法进行自动或手动关联

       解决关联处理的1+n问题

缺点：

       目前只针对SqlSession/Mappe形式有效（ActiveRecord形式暂未涉及修改，也没有测试）


              非事务下， 1个连接（1个SqlSession）只执行一条SQL，而自动获取每个关联属性的sql都会创建1~2个SqlSession（并执行1~2条SQL)。 可通过配置事务，让所有关联属性都使用同一个SqlSession（此时非延迟加载的关联属性无论有多少都可以同在一个事务一个SqlSession中执行，而每个延迟加载的关联属性，在自动触发时还会创建一次SqlSession，但可以配置为非自动触发---即实体类上标注@AutoLazy(false)或没有标注该注解（默认），之后通过initialize方法在事务范围内的一个SqlSession中同时加载多个延迟加载的属性。）
 

使用注意点：

       非ServiceImpl内置的业务查询，配置事务管理，减少SqlSession的创建。

       实体上可用注解@AutoLazy(true)来标注是否自动触发延迟加载（true的话则

       如果可以，不使用延迟加载（延迟加载的使用是在SqlSession关闭后执行的，需要重新创建SqlSession）。

       如果确实需要延迟加载，可使用ServiceImpl 或  AutoMapper 相关的initialize方法一次性加载所有需要的被延迟的属性(只需要创建额外的一个SqlSession）


 

 

 

 

注解使用：

 一对多(多对一) ：

Company实体类中配置：



@Data
public class Company {
    @TableId(value = "company_id")
    private Long id;
    private String name;
    
    //一对多
    @TableField(exist = false)
    @OneToMany
    @JoinColumn(name="company_id",referencedColumnName = "company_id")
    private Set<Man> employees;
}



 Man实体类中配置：



@Data
public class Man {

    @TableId(value = "man_id")
    private Long id;
    private String name;

    //多对一
    @TableField("company_id")
    private Long companyId;
    
    @TableField(exist = false)
    @ManyToOne
    @JoinColumn(name = "company_id", referencedColumnName = "company_id")
    private Company company;
}



 一对多（多对一）表结构：  company: (compnay_id,   name)           man: (man_id,    name,   company_id)

 

 

一对一：

Woman实体类配置：



@Data
public class Woman {
    @TableId(value = "woman_id")
    private Long id;
    private String name;
    
    //一对一
    @TableField("lao_gong_id")
    private Long laoGongId;
    
    @TableField(exist = false)
    @OneToOne
    @JoinColumn(name = "lao_gong_id", referencedColumnName = "man_id")
    private Man laoGong;
}



 

Man实体类配置：



@Data
public class Man {
    @TableId(value = "man_id")
    private Long id;
    private String name;

    //一对一
    @TableField("lao_po_id")
    private Long laoPoId;
    
    @TableField(exist = false)
    @OneToOne
    @JoinColumn(name = "lao_po_id", referencedColumnName = "woman_id")
    private Woman laoPo;
}



 一对一表结构：（实际可以减少一方）  woman: (woman_id,  name,   lao_gong_id)           man: (man_id,   name,   lao_po_id)

  

 

多对多：

Course实体类配置：



@Data
public class Course {
    @TableId(value = "course_id")
    private Long id;
    private String name;

    //多对多
    @TableField(exist = false)
    @ManyToMany
    @JoinTable(targetMapper = StudentCourseMapper.class)
    @JoinColumn(name = "course_id", referencedColumnName = "course_id")
    @InverseJoinColumn(name = "child_id", referencedColumnName = "student_id")
    private List<Child> students;
}



 

 

Child实体类配置：



@Data
public class Child {
    @TableId("child_id")
    private Long id;
    private String name;//多对多
    @TableField(exist = false)
    @ManyToMany
    @JoinTable(targetMapper=StudentCourseMapper.class)
    @JoinColumn(name = "child_id", referencedColumnName = "student_id")
    @InverseJoinColumn(name = "course_id", referencedColumnName = "course_id")private List<Course> courses;
}



 

StudenCourse中间类（多对多必须要有）：



@Data
public class StudentCourse {
    //可以有也可以无此ID
    private Long id;
    
    @TableField("student_id")
    private Long studentId;
    
    @TableField("course_id")
    private Long courseId;
}



 多对多表结构：course: (course_id,  name)          child: (child_id,   name)       student_course:(id,   student_id,    course_id)

 

 

 

使用过程：   

1.   POM中引入mprelation， 并配置 AutoMapper （只要是扫描被注解的实体类）



@Configuration
public class AutoMapperConfig {
    @Bean
    public AutoMapper autoMapper() {
        return new AutoMapper(new String[] { "demo.entity","demo.bean" }); //配置实体类所在目录（可多个,暂时不支持通过符*号配置）
    }
}



 

2.   在实体类中配置注解（更多的注解配置见上边注解部分，这里只只列出其中一个）

 



@Data
@AutoLazy  //对标注了@Lazy(true)的延迟关联属性启动自动触发加载，否则须以手动触发加载（如initialize等方法）
public class Man { 
    @TableId(value = "man_id")
    private Long id;

    private String name;

    private Long laoPoId;

    @TableField(exist = false)
    @OneToOne
    @Lazy(true)
    @JoinColumn(name = "lao_po_id", referencedColumnName = "woman_id")
    private Woman laoPo;

    @TableField("company_id")
    private Long companyId;

    @TableField(exist = false)
    @ManyToOne
    @JoinColumn(name = "company_id", referencedColumnName = "company_id")
    @Lazy(true)
    private Company company;

    @TableField(exist = false)
    @OneToMany
    @JoinColumn(name = "man_id", referencedColumnName = "lao_han_id")private List<Child> waWa;

    @TableField(exist = false)
    @OneToMany
    @JoinColumn(name = "man_id", referencedColumnName = "man_id")
    @Lazy(false)
    private Set<Tel> tels;

}



 

 

 

3.   在Service层、Mapper层的使用，见下面：
以下是基于Mybatis-Plus官方示例修改而来的测试程序： 

 通过继承工具类重写过的IService /  ServiceImpl   会自动执行关联映射, 无须再写gettLinkById之类的方法（可以使得各实现类没有任何方法）：

 mapper接口：

public interface ManMapper extends BaseMapper<Man> {}

 service接口：

public interface IManService extends IService<Man> {}  // IService为重写过的同名接口

 Service实现：

@Service
public class ManServiceImpl extends ServiceImpl<ManMapper, Man> implements IManService {}  // ServiceImpl为重写过的同名接口

 

测试调用：



public class ServiceTest {
    @Autowired
    ManService manService;

    @Test
    public void t_man_serviceImpl() {
        Man man = manService.getById(1); // 原Mybatis-Plus的ServiceImpl的各种查询，被重写过后，都可以自动关联,
        System.out.println(man);        
    }
}
 

结果输出:

Man(
    id=1, 
    name=程序猿小明, 
    laoPoId=1, 
    laoPo=Woman(id=1, name=程序猿小明老婆, laoGongId=1, laoGong=null, waWa=null), 
    companyId=1, 
    company=Company(id=1, name=百度, employees=null), 
    waWa=[ 
        Child(id=1,name=xxx1,lao_han_id=null, laoHan=null, lao_ma_id=null, laoMa=null, courses=null), 
        Child(id=2,name=xxxx2, lao_han_id=null, laoHan=null, lao_ma_id=null, laoMa=null, courses=null)  
    ], 
    tels=[
        Tel(id=1, tel=139xxxxxx, manId=1, laoHan=null), 
        Tel(id=4, tel=159xxxxxx, manId=1, laoHan=null), 
        Tel(id=2, tel=137xxxxxx, manId=1, laoHan=null)
    ]
)

 

如需需要对其关联属性对象的关联属性进行自动加载，可以继续使用AutoMapper对象的mapperEntity、mapperEntityList、mapperEntitySet、mapperEntityPage来操作：

比如想获取（填充）waWas 的关联，则：

List waWas=man.getWaWas();
autoMapper.mapperEntityList(waWas); 


 

AutoMapper类中的几个常用方法说明：

        mapperEntity(entity)                                            可以对一个实体类，实现自动关联。

       mapperEntityList(entity_list)                                可以对一个实体类List，实现自动关联。

       mapperEntitySet(entity_set)                                可以对一个实体类Set，实现自动关联。

       mapperEntityCollection(entity_list_or_set)          可以对一个实体类Set或List，实现自动关联。

       mapperEntityPage(entity_page)                          可以对一个实体类Page，实现自动关联。


	  initialize(entity/entityList/entitySet/entityPage,    OneOrMoreLazyPropertyName ...)                   

                    可以对一个实体类/实体类List/实体类Set/实体类Page，在事务范围内，手动立即触发其各个被@Lazy(true)标注的关联属性。

                  该方法在重写过的ServiceImpl内也存在(供Controller层调用来加载延迟关联的属性）。

 

 

 

AutoMapper在重写过的ServiceImpl类中已经自动注入可用(名为autoMapper)，其它情况也可以手动注入：

public class MPRTest2 {
    @Autowired
    AutoMapper autoMapper;

    @Resource
    private ManMapper manMapper;

    @Test
    public void t_man() {
        Man man = manMapper.selectById(1L);
        autoMapper.mapperEntity(man);
        System.out.println(man);
    }

}



  
