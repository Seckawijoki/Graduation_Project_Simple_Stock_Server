#sql("getUserRecord")
select * from user where userId = #para(userId);
#end

#sql("updateUserPortraitUri")
update user
set portraitUri = #para(portraitUri)
where userId = #para(userId)
#end
