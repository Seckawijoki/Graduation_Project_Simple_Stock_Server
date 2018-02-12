#sql("getUserRecord")
select * from user where userId = #para(userId);
#end

#sql("updateUserPortrait")
update user
set portraitFileName = #para(portraitFileName)
where userId = #para(userId)
#end
