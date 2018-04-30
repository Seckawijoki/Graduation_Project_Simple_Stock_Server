#sql("getFavoriteGroupTypeByName")
select * from favorite_group_type where favoriteGroupName = #para(favoriteGroupName)
#end

#sql("getFavoriteGroupTypeById")
select * from favorite_group_type where favoriteGroupId = #para(favoriteGroupId)
#end

#sql("getFavoriteGroup")
select * from favorite_group where userId = #para(userId)
and favoriteGroupId = #para(favoriteGroupId)
#end

#sql("getFavoriteGroupByGroupName")
select * from favorite_group where userId = #para(userId)
and favoriteGroupId = (
 select favoriteGroupId from favorite_group_type
 where favoriteGroupName = #para(favoriteGroupName)
)
#end

#sql("getMinRankWeightFromFavoriteGroup")
select min(rankWeight) from favorite_group where userId = ?
#end

#sql("getFavoriteGroupName")
select distinct
favorite_group_type.favoriteGroupName,
favorite_group_type.favoriteGroupId,
favorite_group.rankWeight
from favorite_group_type, favorite_group
where favorite_group_type.favoriteGroupId = favorite_group.favoriteGroupId
and favorite_group.userId = #para(userId)
order by favorite_group.rankWeight desc ;
#end

#sql("countAllFavoriteStock")
select count(*) from favorite_stock where userId = ? ;
#end

#sql("countSpecialFavoriteStock")
select count(*) from favorite_stock where userId = ? and specialAttention = true;
#end

#sql("countFavoriteStock")
select count(*) from favorite_stock where userId = ? and favoriteGroupId = ?;
#end

#sql("deleteFavoriteGroupByGroupId")
delete from favorite_group where userId = ? and favoriteGroupId = ?
#end

#sql("deleteFavoriteGroupByGroupName")
delete from favorite_group where userId = ? and favoriteGroupId = (
select favoriteGroupId from favorite_group_type where favoriteGroupName = ?;
#end

#sql("getFavoriteStocks")
select stockTableId, rankWeight, specialAttention
from favorite_stock
where userId = #para(userId)
and favoriteGroupId = #para(favoriteGroupId)
order by rankWeight desc;
#end

#sql("getAllFavoriteStocks")
select stockTableId, rankWeight, specialAttention
from favorite_stock
where userId = #para(userId)
order by rankWeight desc;
#end

#sql("getSpecialFavoriteStocks")
select stockTableId, rankWeight, specialAttention
from favorite_stock
where userId = #para(userId)
and specialAttention <> 0
order by rankWeight desc;
#end

#sql("getExistentFavoriteStock")
select * from favorite_stock
where userId = #para(userId)
and stockTableId = #para(stockTableId);
#end

#sql("getExistentFavoriteStockFromFavoriteGroup")
select * from favorite_stock
where userId = #para(userId)
and favoriteGroupId = #para(favoriteGroupId)
and stockTableId = #para(stockTableId);
#end

#sql("getMinRankWeightFromFavoriteStock")
select min(rankWeight) from favorite_stock where userId = ?
#end

#sql("getMaxRankWeightFromFavoriteStock")
select max(rankWeight) from favorite_stock where userId = ?
#end

#sql("deleteFavoriteStock")
delete from favorite_stock where userId = ?
and favoriteGroupId = ?
and stockTableId = ?;
#end

#sql("deleteFavoriteStockFromAllGroup")
delete from favorite_stock where userId = ?
and stockTableId = ?;
#end

#sql("setSpecialFavoriteStock")
update favorite_stock
set specialAttention = #para(specialAttention)
where userId = #para(userId)
and stockTableId = #para(stockTableId)
#end

#sql("setFavoriteStockTop")
update favorite_stock
set rankWeight = #para(rankWeight)
where userId = #para(userId)
and stockTableId = #para(stockTableId)
#end


#sql("getFavoriteStockMaxRankWeight")
select max(rankWeight) from favorite_stock
where userId =
(select userId from user
where phone = ?
or email = ?
or userId = ? )
#end