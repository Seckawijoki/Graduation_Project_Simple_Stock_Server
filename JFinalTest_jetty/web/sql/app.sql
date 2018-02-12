#sql("getLatestAppVersion")
select * from app_version order by versionCode desc;
#end

#sql("getApkDownloadUri")
select * from app_version where versionCode = #para(versionCode);
#end