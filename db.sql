-- noinspection SqlNoDataSourceInspectionForFile

drop table if exists man;
drop table if exists company;
drop table if exists woman;
drop table if exists child;
drop table if exists tel;

create table man
(
    man_id        bigint(20) not null,
    name      varchar(30),
    lao_po_id bigint(20) not null,
    company_id bigint(20) not null
);


create table company
(
    company_id        bigint(20) not null,
    name      varchar(30)
);



create table woman
(
    woman_id          bigint(20) not null,
    name        varchar(30),
    lao_gong_id bigint(20) not null
);

create table child
(
    child_id         bigint(20) not null,
    name       varchar(30),
    lao_han_id bigint(20) not null,
    lao_ma_id bigint(20) not null
);


create table tel
(
    id         bigint(20) not null,
    tel       varchar(30),
    man_id bigint(20) not null
);

create table course
(
    course_id         bigint(20) not null,
    name       varchar(30)
);


create table student_course
(
    id         bigint(20) not null,
    student_id      bigint(20) not null,
    course_id      bigint(20) not null
);




-- noinspection SqlNoDataSourceInspectionForFile

insert into man(man_id, name, lao_po_id, company_id)
values (1, '程序猿小明', 1, 1),
       (2, '隔壁老王', 2, 1);
       
insert into company(company_id,name)
values (1,'百度'),
	   (2,'小米');

insert into woman(woman_id, name, lao_gong_id)
values (1, '程序猿小明老婆', 1),
       (2, '隔壁老王老婆', 2);

INSERT INTO child (child_id, name, lao_han_id, lao_ma_id)
VALUES (1, '小小明', 1, 1),
       (2, '小小王', 2, 2),
       (3, '旺仔', 2, 1),
       (4, '小馒头', 2, 1),
       (5, '大礼包', 1, 2);
       
       
INSERT INTO tel (id, tel, man_id)
VALUES (1, '139xxxxxx', 1),
       (2, '137xxxxxx', 1),
       (3, '158xxxxxx', 2),
       (4, '159xxxxxx', 1);
       
insert into course(course_id,name)
values  (1,'语文'),
		(2,'英语'),
		(3,'化学'),
		(4,'物理'),
	    (5,'数学');
	   
	   
	   
insert into student_course(id,student_id,course_id)
values  (1,1,1),
		(2,1,2),
		(3,1,3),
		(4,2,2),
 		(5,2,3),
	    (6,3,1);