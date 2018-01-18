#sql("getUserRecord")
select * from user where userId = #para(userId);
#end
