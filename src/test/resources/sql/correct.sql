-- 这是一个正确的sql脚本

////////////////////////////
//       注释              //
////////////////////////////

delete from student where id > 906;

insert into student values (907, '王七', '男', '1991', '文学系', '浙江省杭州市');

select * from student;

select
	*
	from student;
	
select
	*
	from
	student
		;