#sql("searchForMatchedStocks")
select * from all_stocks
where stockId like concat('%',#para(search),'%')
or stockName like concat('%',#para(search),'%')
limit #para(limit);
#end


#sql("getStockSearchTime")
select * from search_history
where stockTableId = #para(stockTableId)
and userId = #para(userId)
#end

#sql("getStockInFavorite")
select * from favorite_stock
where stockTableId = #para(stockTableId)
and userId = #para(userId)
#end

#sql("getStockSearchHistory")
select a.*, b.searchTime
from all_stocks as a, search_history as b
where b.stockTableId = a.stockTableId
and a.stockTableId in (
 select stockTableId from search_history
 where userId = #para(userId)
)
order by b.searchTime desc
limit #para(limit);
#end

#sql("updateStockSearchHistory")
update search_history
set searchTime = now()
where userId = #para(userId)
and stockTableId = #para(stockTableId)
#end

#sql("clearStockSearchHistory")
delete from search_history
where userId = #para(userId);
#end

#sql("deleteFavoriteStockFromSearch")
delete from favorite_stock
where userId = #para(userId)
and stockTableId = #para(stockTableId)
#end